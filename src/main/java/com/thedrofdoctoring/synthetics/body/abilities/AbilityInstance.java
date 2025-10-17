package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class AbilityInstance<T extends AbilityType> {

    protected final T ability;
    protected final SyntheticsPlayer player;
    protected final ResourceLocation instanceID;
    protected final AbilityData data;

    public AbilityInstance(T ability, SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        this.ability = ability;
        this.player = player;
        this.instanceID  = instanceID;
        this.data = data;
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

    public AbilityData getAbilityData() {
        return data;
    }
    public double factor() {
        return data.factor();
    }

}
