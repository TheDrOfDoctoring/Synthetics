package com.thedrofdoctoring.synthetics.body.abilities.passive.types;

import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
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
    public void addDescriptionInfo(AbilityData data, List<Component> description) {
        if(data instanceof AttributeAbilityInstance.AttributeAbilityData attributeData) {
            if(attributeData.operation() == AttributeModifier.Operation.ADD_VALUE) {
                description.add(Component.translatable("abilities.synthetics.description.operation_add").withStyle(ChatFormatting.BLUE));
            } else {
                description.add(Component.translatable("abilities.synthetics.description.operation_mult").withStyle(ChatFormatting.BLUE));

            }
        }
        super.addDescriptionInfo(data, description);
    }
}
