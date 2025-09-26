package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class SyntheticAbilityInstance<T extends SyntheticAbilityType> {

    protected final T ability;
    protected final SyntheticsPlayer player;
    protected final ResourceLocation instanceID;

    public SyntheticAbilityInstance(T ability, SyntheticsPlayer player, ResourceLocation instanceID) {
        this.ability = ability;
        this.player = player;
        this.instanceID  = instanceID;
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
}
