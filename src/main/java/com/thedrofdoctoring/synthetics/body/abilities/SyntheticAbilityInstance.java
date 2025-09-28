package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class SyntheticAbilityInstance<T extends SyntheticAbilityType> {

    protected final T ability;
    protected final SyntheticsPlayer player;
    protected final ResourceLocation instanceID;
    protected double abilityFactor = 1f;

    public SyntheticAbilityInstance(T ability, SyntheticsPlayer player, double abilityFactor, ResourceLocation instanceID) {
        this.ability = ability;
        this.player = player;
        this.instanceID  = instanceID;
        this.abilityFactor = abilityFactor;
    }

    public T getAbility() {
        return ability;
    }

    public SyntheticsPlayer getPlayer() {
        return player;
    }

    public ResourceLocation getInstanceID() {
        return instanceID;
    }

    public double getAbilityFactor() {
        return abilityFactor;
    }
}
