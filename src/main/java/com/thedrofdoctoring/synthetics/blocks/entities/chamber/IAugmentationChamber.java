package com.thedrofdoctoring.synthetics.blocks.entities.chamber;

import net.minecraft.world.entity.player.Player;

public interface IAugmentationChamber {

    Player getActivePlayer();
    void setActivePlayer(Player player);
}
