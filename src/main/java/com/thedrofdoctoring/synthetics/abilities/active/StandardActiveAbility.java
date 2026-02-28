package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import net.minecraft.resources.ResourceLocation;

public abstract class StandardActiveAbility extends ActiveAbilityType<AbilityActiveInstance<?>> {
    public StandardActiveAbility(ResourceLocation id) {
        super(id);
    }
}
