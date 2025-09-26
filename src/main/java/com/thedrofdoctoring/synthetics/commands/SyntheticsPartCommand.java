package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPart;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SyntheticsPartCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("body_part")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("replace")
                        .then(Commands.argument("part", new PartArgument(context))
                                .executes(con -> modifyAugment(con, PartArgument.getPart(con, "part"),  Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyAugment(con, PartArgument.getPart(con, "part"),  EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                );

    }
    private static int modifyAugment(@NotNull CommandContext<CommandSourceStack> context, BodyPart part, @NotNull Collection<ServerPlayer> players) {
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
}
