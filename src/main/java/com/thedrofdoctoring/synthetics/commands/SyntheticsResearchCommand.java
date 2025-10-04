package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.ResearchManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SyntheticsResearchCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("research")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("learn")
                        .then(Commands.argument("node", new ResearchArgument(context))
                                .executes(con -> modifyResearch(con, ResearchArgument.getResearch(con, "node"), true, Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyResearch(con, ResearchArgument.getResearch(con, "node"),  true, EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        ))
                .then(Commands.literal("unlearn")
                        .then(Commands.argument("node", new ResearchArgument(context))
                                .executes(con -> modifyResearch(con, ResearchArgument.getResearch(con, "node"), false, Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> modifyResearch(con, ResearchArgument.getResearch(con, "node"),  false, EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                );

    }
    @SuppressWarnings("SameReturnValue")
    private static int modifyResearch(@NotNull CommandContext<CommandSourceStack> context, ResearchNode node, boolean learn, @NotNull Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            ResearchManager manager = SyntheticsPlayer.get(player).getResearchManager();
            if(learn) {
                if(manager.hasResearched(node)) {
                    context.getSource().sendFailure(Component.translatable("command.synthetics.already_researched", node.id().toString(), player.getDisplayName()));
                    return 0;
                }
                manager.addResearched(node);
                context.getSource().sendSuccess(() -> Component.translatable("command.synthetics.modify_research"), true);

            } else {
                if(!manager.hasResearched(node)) {
                    context.getSource().sendFailure(Component.translatable("command.synthetics.not_researched", node.id().toString(), player.getDisplayName()));
                    return 0;
                }
                manager.removedResearched(node);
                context.getSource().sendSuccess(() -> Component.translatable("command.synthetics.modify_research"), true);
            }

        }
        return 0;
    }
}
