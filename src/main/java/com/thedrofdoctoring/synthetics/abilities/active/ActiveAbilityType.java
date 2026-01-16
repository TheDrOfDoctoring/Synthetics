package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;


public abstract class ActiveAbilityType<T extends AbilityActiveInstance.Data> extends AbilityType {

    private final ResourceLocation ID;

    public ActiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public abstract boolean activate(SyntheticsPlayer syntheticsPlayer, T abilityData);

    public abstract void activateClient(SyntheticsPlayer syntheticsPlayer, T abilityData);

    @SuppressWarnings("unchecked")
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityData abilityData) {
        try {
            T t = (T) abilityData;
            return activate(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability data {} is wrong for type", abilityData.toString());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityData abilityData) {
        try {
            T t = (T) abilityData;
            activateClient(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability data {} is wrong for type", abilityData.toString());
        }
    }

    public abstract boolean canBeUsed(SyntheticsPlayer syntheticsPlayer);

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof AbilityActiveInstance.Data activeData) {
            return Optional.of(new AbilityActiveInstance<>(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options) {
        return new AbilityActiveInstance.Data(factor, options);
    }

    @Override
    public void addDescriptionInfo(AbilityData data, List<Component> description) {
        if (data instanceof AbilityActiveInstance.Data activeData) {
            ActiveAbilityOptions options = activeData.options();
            description.add(Component.translatable("abilities.synthetics.description.cooldown", options.cooldown()).withStyle(ChatFormatting.BLUE));
            if (options.duration() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.duration", options.duration()).withStyle(ChatFormatting.BLUE));
            }
            if (options.powerCost() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_cost", options.powerCost()).withStyle(ChatFormatting.BLUE));

            }
            if (options.powerDrain() > 0) {
                description.add(Component.translatable("abilities.synthetics.description.power_drain", options.powerDrain()).withStyle(ChatFormatting.BLUE));
            }


            description.add(Component.translatable("abilities.synthetics.description.ability_factor", activeData.factor()).withStyle(ChatFormatting.BLUE));
        }
    }
}
