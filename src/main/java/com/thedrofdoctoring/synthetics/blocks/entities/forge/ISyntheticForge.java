package com.thedrofdoctoring.synthetics.blocks.entities.forge;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public interface ISyntheticForge {

    Player getActivePlayer();
    void setActivePlayer(Player player);

    int getRecipeTime();

    SyntheticForgeBlockEntity getMaster();

    AbstractContainerMenu createMenu(int id, @NotNull Inventory player);
}
