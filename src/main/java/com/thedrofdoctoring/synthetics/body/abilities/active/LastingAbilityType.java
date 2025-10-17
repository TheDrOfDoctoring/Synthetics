package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class LastingAbilityType extends ActiveAbilityType {
    public LastingAbilityType(ResourceLocation id) {
        super(id);
    }

    public abstract boolean onTick(SyntheticsPlayer syntheticsPlayer, double abilityFactor);

    public abstract void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, double abilityFactor);
}
