package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public abstract class ActiveAbilityType extends AbilityType {

    private final ResourceLocation ID;

    public ActiveAbilityType(ResourceLocation id) {
        this.ID = id;
    }

    @Override
    public ResourceLocation getAbilityID() {
        return ID;
    }

    public abstract boolean activate(SyntheticsPlayer syntheticsPlayer, double abilityFactor);

    public abstract void activateClient(SyntheticsPlayer syntheticsPlayer, double abilityFactor);

    public abstract boolean canBeUsed(SyntheticsPlayer syntheticsPlayer);

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof AbilityActiveInstance.ActiveAbilityData activeData) {
            return Optional.of(new AbilityActiveInstance<>(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static AbilityActiveInstance.ActiveAbilityData create(double factor, ActiveAbilityOptions options) {
        return new AbilityActiveInstance.ActiveAbilityData(factor, options);
    }

    @Override
    public void addDescriptionInfo(AbilityData data, List<Component> description) {
        if (data instanceof AbilityActiveInstance.ActiveAbilityData activeData) {
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
