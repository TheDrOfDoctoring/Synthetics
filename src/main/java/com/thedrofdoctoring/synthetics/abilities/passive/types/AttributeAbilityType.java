package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Optional;

public class AttributeAbilityType extends PassiveAbilityType {
    public AttributeAbilityType(ResourceLocation id) {
        super(id);
    }

    @Override
    public Optional<AbilityPassiveInstance<? extends PassiveAbilityType>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        if(data instanceof AttributeAbilityInstance.AttributeAbilityData attributeAbilityData) {
            return Optional.of(new AttributeAbilityInstance(this, player, attributeAbilityData, instanceID, powerDraw));
        }
        return Optional.empty();
    }

    public static AttributeAbilityInstance.AttributeAbilityData create(double factor, AttributeModifier.Operation operation, Holder<Attribute> attribute) {
        return new AttributeAbilityInstance.AttributeAbilityData(factor, operation, attribute);
    }

    @Override
    public Component title(AbilityData data) {
        if(data instanceof AttributeAbilityInstance.AttributeAbilityData attributeData) {
            return Component.translatable("abilities.synthetics.attribute." + attributeData.attribute().value().getDescriptionId());
        }
        return super.title(data);
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        if(ability.abilityData() instanceof AttributeAbilityInstance.AttributeAbilityData attributeData) {
            ChatFormatting colour = ability.abilityNature().defaultColour();
            if(attributeData.operation() == AttributeModifier.Operation.ADD_VALUE) {
                description.add(Component.translatable("abilities.synthetics.description.operation_add").withStyle(colour));
                super.addDescriptionInfo(ability, description);
            } else {
                description.add(Component.translatable("abilities.synthetics.description.operation_mult").withStyle(colour));
                if(ability.abilityData().factor() >= 0) {
                    description.add(Component.translatable("abilities.synthetics.description.ability_factor_mult_plus", ability.abilityData().factor() * 100f).withStyle(colour));
                } else {
                    description.add(Component.translatable("abilities.synthetics.description.ability_factor_mult", ability.abilityData().factor() * 100f).withStyle(colour));
                }
            }
        }

    }
}
