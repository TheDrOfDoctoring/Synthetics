package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SyntheticsAugmentCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("augment")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("install")
                        .then(Commands.argument("part", new AugmentArgument(context))
                                .executes(con -> modifyAugment(con, AugmentArgument.getAugment(con, "part"), true, Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyAugment(con, AugmentArgument.getAugment(con, "part"),  true, EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        ))
                .then(Commands.literal("remove")
                        .then(Commands.argument("part", new AugmentArgument(context))
                        .executes(con -> modifyAugment(con, AugmentArgument.getAugment(con, "part"), false, Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyAugment(con, AugmentArgument.getAugment(con, "part"),  false, EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("give")
                        .then(Commands.argument("part", new AugmentArgument(context))
                                .executes(con -> giveAugment(con, AugmentArgument.getAugment(con, "part"),  Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> giveAugment(con, AugmentArgument.getAugment(con, "part"),  EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                );


    }
    @SuppressWarnings("SameReturnValue")
    private static int modifyAugment(@NotNull CommandContext<CommandSourceStack> context, Augment augment, boolean install, @NotNull Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
            if(install) {
                if(synthetics.isInstalled(augment)) {
                    context.getSource().sendFailure(Component.translatable("command.synthetics.already_installed", augment.augmentID().toString(), player.getDisplayName()));
                    return 0;
                }
                synthetics.addAugment(new AppliedAugmentInstance(augment, synthetics.getPartManager().getDefaultPartForAugment(augment)), true);
                context.getSource().sendSuccess(() -> Component.translatable("command.synthetics.modify_success"), true);

            } else {
                if(!synthetics.isInstalled(augment)) {
                    context.getSource().sendFailure(Component.translatable("command.synthetics.not_installed", augment.id().toString(), player.getDisplayName()));
                    return 0;
                }
                synthetics.removeAugment(new AppliedAugmentInstance(augment, synthetics.getPartManager().getDefaultPartForAugment(augment)));
                context.getSource().sendSuccess(() -> Component.translatable("command.synthetics.modify_success"), true);
            }

        }
        return 0;
    }

    private static int giveAugment(@NotNull CommandContext<CommandSourceStack> context, Augment augment, @NotNull Collection<ServerPlayer> players) {
        ItemStack stack = augment.createDefaultItemStack(context.getSource().registryAccess());
        for(ServerPlayer player : players) {
            if(player.getInventory().add(stack)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        }
        return 0;
    }


}
