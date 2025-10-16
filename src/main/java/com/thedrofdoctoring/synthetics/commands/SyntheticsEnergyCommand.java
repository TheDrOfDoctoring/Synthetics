package com.thedrofdoctoring.synthetics.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class SyntheticsEnergyCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
        return Commands.literal("energy")
                .requires(p -> p.hasPermission(2))
                .then(Commands.literal("set_energy")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                                .executes(con -> setEnergy(con, IntegerArgumentType.getInteger(con, "amount"), Lists.newArrayList(con.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(con -> setEnergy(con, IntegerArgumentType.getInteger(con, "amount"),  EntityArgument.getPlayers(con, "player"))
                                        )
                                )
                        )
                );

    }
    private static int setEnergy(@NotNull CommandContext<CommandSourceStack> context, int amount,  @NotNull Collection<ServerPlayer> players) {
        for(ServerPlayer player : players) {
            SyntheticsPlayer synthetics = SyntheticsPlayer.get(player);
            synthetics.getPowerManager().setPower(amount);
            synthetics.getPowerManager().markDirty();

        }
        return 0;
    }
}
