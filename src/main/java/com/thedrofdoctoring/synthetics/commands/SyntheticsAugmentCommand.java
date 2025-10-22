package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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


}
