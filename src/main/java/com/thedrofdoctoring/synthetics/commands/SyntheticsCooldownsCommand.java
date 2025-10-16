package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SyntheticsCooldownsCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("reset_timers")
                .requires(p -> p.hasPermission(2))
                .executes(con -> resetTimers(con, Lists.newArrayList(con.getSource().getPlayerOrException())))
                .then(Commands.argument("player", EntityArgument.entities())
                        .executes(con -> resetTimers(con, EntityArgument.getPlayers(con, "player"))
                        )
                );

    }

    private static int resetTimers(@NotNull CommandContext<CommandSourceStack> context, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
            synthetics.getAbilityManager().resetAll();
        }
        return 0;

    }
}
