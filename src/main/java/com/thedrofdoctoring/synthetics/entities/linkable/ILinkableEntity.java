package com.thedrofdoctoring.synthetics.entities.linkable;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ILinkableEntity {

    @Nullable Player getLinkedTo();
}
