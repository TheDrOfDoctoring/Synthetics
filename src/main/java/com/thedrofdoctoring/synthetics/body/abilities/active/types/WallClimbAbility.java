package com.thedrofdoctoring.synthetics.body.abilities.active.types;

import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticLastingAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.resources.ResourceLocation;

public class WallClimbAbility extends SyntheticLastingAbilityType {

    public WallClimbAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, double factor) {
        activate(syntheticsPlayer);
        return true;
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, double factor) {
        syntheticsPlayer.getEntity().resetFallDistance();
        return false;
    }
    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player.getEntity());
        cache.hasWallClimb = true;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, double factor) {
        activate(syntheticsPlayer);
    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(syntheticsPlayer.getEntity());
        cache.hasWallClimb = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, double factor) {
        activate(syntheticsPlayer);
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }
}
