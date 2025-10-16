package com.thedrofdoctoring.synthetics.capabilities.cache;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SyntheticsPlayerCache {
    public static SyntheticsPlayerCache get(@NotNull Player player) {
        return ((ISyntheticsPlayerCache) player).synthetics$getCache();
    }

    public boolean hasWallClimb;
}
