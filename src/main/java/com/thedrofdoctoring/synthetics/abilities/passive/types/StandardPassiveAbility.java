package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class StandardPassiveAbility extends PassiveAbilityType<GenericPassiveAbilityInstance<?>> {

    public StandardPassiveAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public Optional<GenericPassiveAbilityInstance<?>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof GenericPassiveAbilityInstance.Data genericData) {
            return Optional.of(new GenericPassiveAbilityInstance<>(this, player, genericData, instanceID, powerDraw));
        }
        return Optional.empty();
    }
}
