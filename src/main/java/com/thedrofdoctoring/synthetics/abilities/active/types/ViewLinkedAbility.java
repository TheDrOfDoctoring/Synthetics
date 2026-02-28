package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.resources.ResourceLocation;

public class ViewLinkedAbility extends StandardLastingAbility {

    public ViewLinkedAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
        return true;
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        return false;
    }
    private void activate(SyntheticsPlayer player) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player.getEntity());
        cache.canViewLinked = true;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
    }


    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(syntheticsPlayer.getEntity());
        cache.canViewLinked = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> data) {
        activate(syntheticsPlayer);
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer player) {
        return true;
    }
}
