package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import net.minecraft.resources.ResourceLocation;

public abstract class StandardLastingAbility extends LastingAbilityType<AbilityActiveInstance.Data> {
    public StandardLastingAbility(ResourceLocation id) {
        super(id);
    }
}
