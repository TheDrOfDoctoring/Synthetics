package com.thedrofdoctoring.synthetics.body.abilities.passive.types;

import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

@SuppressWarnings("unused")
public class PassiveAbilityType extends AbilityType {

    private final ResourceLocation ID;

    public PassiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }


    public Optional<AbilityPassiveInstance<? extends PassiveAbilityType>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof GenericPassiveAbilityInstance.Data passive) {
            return Optional.of(new GenericPassiveAbilityInstance<>(this, player, passive, instanceID, powerDraw));
        }
        return Optional.empty();
    }

    public static GenericPassiveAbilityInstance.Data create(double factor) {
        return new GenericPassiveAbilityInstance.Data(factor);
    }

    public void onAbilityAdded(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {}

    public void onAbilityRemoved(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {}



}
