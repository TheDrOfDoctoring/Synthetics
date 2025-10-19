package com.thedrofdoctoring.synthetics.body.abilities.passive.types;

import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.GenericPassiveAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
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

    @Override
    public void addDescriptionInfo(AbilityData data, List<Component> description) {
        description.add(Component.translatable("abilities.synthetics.description.ability_factor", data.factor()).withStyle(ChatFormatting.BLUE));
    }

    public static GenericPassiveAbilityInstance.Data create(double factor) {
        return new GenericPassiveAbilityInstance.Data(factor);
    }

    public void onAbilityAdded(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {}

    public void onAbilityRemoved(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {}



}
