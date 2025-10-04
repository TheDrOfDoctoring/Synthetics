package com.thedrofdoctoring.synthetics.body.abilities.passive;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Optional;

public class SyntheticPassiveAbilityType extends SyntheticAbilityType {

    private final ResourceLocation ID;
    private Holder<Attribute> attribute;

    public SyntheticPassiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }
    public SyntheticPassiveAbilityType(Holder<Attribute> modifiedAttribute, ResourceLocation id) {
        this.ID = id;
        this.attribute = modifiedAttribute;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public Optional<Holder<Attribute>> getModifiedAttribute() {
        return Optional.ofNullable(attribute);
    }
}
