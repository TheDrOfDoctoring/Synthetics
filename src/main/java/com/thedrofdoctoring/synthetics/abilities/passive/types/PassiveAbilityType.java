package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.IAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public abstract class PassiveAbilityType<T extends IAbilityInstance> extends AbilityType {

    private final ResourceLocation ID;

    public PassiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public abstract Optional<T> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw);

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        ChatFormatting colour = ability.abilityNature().defaultColour();
        description.add(Component.translatable("abilities.synthetics.description.ability_factor", ability.abilityData().factor()).withStyle(colour));
    }



    public void onAbilityAdded(T instance, int instanceCount, SyntheticsPlayer player) {}

    public void onAbilityRemoved(T instance, int instanceCount, SyntheticsPlayer player) {}

    @SuppressWarnings("unchecked")
    public void onAbilityAddedInst(IAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onAbilityAdded(t, instanceCount, player);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.getInstanceID().toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void onAbilityRemovedInst(IAbilityInstance instance, int instanceCount, SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onAbilityRemoved(t, instanceCount, player);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.getInstanceID().toString());
        }
    }



}
