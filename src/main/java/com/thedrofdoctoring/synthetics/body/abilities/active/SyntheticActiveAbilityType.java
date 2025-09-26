package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import net.minecraft.resources.ResourceLocation;

public class SyntheticActiveAbilityType extends SyntheticAbilityType {
    private final ResourceLocation ID;

    public SyntheticActiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }
}
