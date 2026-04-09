package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
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

public class SyntheticsPartCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("body_part")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("replace")
                        .then(Commands.argument("part", new PartArgument(context))
                                .executes(con -> modifyPart(con, PartArgument.getPart(con, "part"),  Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyPart(con, PartArgument.getPart(con, "part"),  EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("give")
                        .then(Commands.argument("part", new PartArgument(context))
                                .executes(con -> giveBodyPart(con, PartArgument.getPart(con, "part"),  Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> giveBodyPart(con, PartArgument.getPart(con, "part"),  EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                );

    }
    @SuppressWarnings("SameReturnValue")
    private static int modifyPart(@NotNull CommandContext<CommandSourceStack> context, BodyPart part, @NotNull Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
            if(synthetics.getPartManager().isPartInstalled(part)) {
                context.getSource().sendFailure(Component.translatable("command.synthetics.already_installed", part.id().toString(), player.getDisplayName()));
                return 0;
            }
            synthetics.getPartManager().replacePart(part, true);
            context.getSource().sendSuccess(() -> Component.translatable("command.synthetics.modify_success"), true);


        }
        return 0;
    }

    private static int giveBodyPart(@NotNull CommandContext<CommandSourceStack> context, BodyPart bodyPart, @NotNull Collection<ServerPlayer> players) {
        ItemStack stack = bodyPart.createDefaultItemStack(context.getSource().registryAccess());
        for(ServerPlayer player : players) {
            if(player.getInventory().add(stack)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        }
        return 0;
    }
}
