package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.AbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.IAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;


public abstract class ActiveAbilityType<T extends IAbilityInstance> extends AbilityType {

    private final ResourceLocation ID;

    public ActiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public abstract boolean activate(SyntheticsPlayer syntheticsPlayer, T abilityData);

    @SuppressWarnings("unchecked")
    public boolean activate(SyntheticsPlayer syntheticsPlayer, AbilityInstance<?> instance) {
        try {
            T t = (T) instance;
            return activate(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability data {} is wrong for type", instance.getInstanceID().toString());
        }
        return false;
    }

    public void onAbilityAdded(T instance, SyntheticsPlayer player) {}

    public void onAbilityRemoved(T instance, SyntheticsPlayer player) {}

    @SuppressWarnings("unchecked")
    public void onAbilityAddedInst(IAbilityInstance instance, SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onAbilityAdded(t, player);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.getInstanceID().toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void onAbilityRemovedInst(IAbilityInstance instance,SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onAbilityRemoved(t,player);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.getInstanceID().toString());
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
        return new AbilityActiveInstance.Data(factor, options, Optional.empty(), Optional.empty());
    }

    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options, String titlePathOverride) {
        return new AbilityActiveInstance.Data(factor, options, Optional.of(titlePathOverride), Optional.empty());
    }
    public static AbilityActiveInstance.Data create(double factor, ActiveAbilityOptions options, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new AbilityActiveInstance.Data(factor, options, Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if (ability.abilityData() instanceof AbilityActiveInstance.Data activeData) {
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
