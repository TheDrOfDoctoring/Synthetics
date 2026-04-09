package com.thedrofdoctoring.synthetics.capabilities.cache;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SyntheticsPlayerCache {
    public static SyntheticsPlayerCache get(@NotNull Player player) {
        return ((ISyntheticsPlayerCache) player).synthetics$getCache();
    }

    public boolean canViewLinked;
    public boolean isNotViewingSelf;

    public int     onRoofTimer;
    public boolean onWall;
    public boolean onRoof;
    public boolean hasWallClimb;

    public boolean invisible;

    public final EffectAmplifierCache effectCache    = new EffectAmplifierCache();
    public final BlockHighlightCache  highlightCache = new BlockHighlightCache();
}
