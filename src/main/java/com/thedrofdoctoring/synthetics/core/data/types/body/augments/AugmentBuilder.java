package com.thedrofdoctoring.synthetics.core.data.types.body.augments;

import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class AugmentBuilder {

    private final ResourceKey<Augment> resourceKey;
    private final HolderSet<BodyPart> validParts;
    private int complexity = 1;
    private int powerCost = 0;
    private int maxCopies = 1;
    private int maxCopiesPerPart = 1;
    private HolderSet<Ability> abilities;

    public AugmentBuilder(ResourceKey<Augment> resourceKey, HolderSet<BodyPart> validParts) {
        this.resourceKey = resourceKey;
        this.validParts = validParts;
    }

    public static AugmentBuilder of(ResourceKey<Augment> resourceKey, HolderSet<BodyPart> validParts) {
        return new AugmentBuilder(resourceKey, validParts);
    }

    public AugmentBuilder complexity(int complexity) {
        this.complexity = complexity;
        return this;
    }

    public AugmentBuilder powerCost(int powerCost) {
        this.powerCost = powerCost;
        return this;
    }

    public AugmentBuilder maxCopies(int maxCopies, int maxPerPart) {
        this.maxCopies = maxCopies;
        this.maxCopiesPerPart = maxPerPart;
        return this;
    }

    public AugmentBuilder abilities(HolderSet<Ability> abilities) {
        this.abilities = abilities;
        return this;
    }

    public ResourceLocation id() {
        return resourceKey.location();
    }
    public ResourceKey<Augment> key() {
        return resourceKey;
    }


    public Augment build() {
        return new Augment(complexity, powerCost, maxCopies, maxCopiesPerPart, validParts, Optional.ofNullable(abilities), id());
    }
}
