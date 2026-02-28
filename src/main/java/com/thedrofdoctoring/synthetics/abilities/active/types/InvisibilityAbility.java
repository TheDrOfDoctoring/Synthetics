package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.thedrofdoctoring.synthetics.abilities.active.StandardLastingAbility;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.resources.ResourceLocation;

public class InvisibilityAbility extends StandardLastingAbility {
    public InvisibilityAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        activate(syntheticsPlayer);
    }

    private void activate(SyntheticsPlayer player) {
        player.getEntity().setInvisible(true);
        SyntheticsPlayerCache.get(player.getEntity()).invisible = true;
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer) {
        syntheticsPlayer.getEntity().setInvisible(false);
        SyntheticsPlayerCache.get(syntheticsPlayer.getEntity()).invisible = false;
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        activate(syntheticsPlayer);
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> abilityData) {
        activate(syntheticsPlayer);
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }
}
