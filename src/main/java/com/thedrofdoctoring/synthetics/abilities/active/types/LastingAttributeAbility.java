package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.LastingAttributeAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Optional;

public class LastingAttributeAbility extends LastingAbilityType<LastingAttributeAbilityInstance> {
    public LastingAttributeAbility(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, LastingAttributeAbilityInstance instance) {
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, LastingAttributeAbilityInstance instance) {
        activate(syntheticsPlayer, instance.modifiers());
    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, LastingAttributeAbilityInstance instance) {
        for(Modifier modifier : instance.modifiers()) {
            AttributeInstance inst = syntheticsPlayer.getEntity().getAttribute(modifier.attribute);
            if(inst != null) {
                inst.removeModifier(instance.getInstanceID());
            }
        }
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, LastingAttributeAbilityInstance instance) {

    }

    private void activate(SyntheticsPlayer player, Iterable<Modifier> modifiers) {
        for(Modifier modifier : modifiers) {
            AttributeInstance inst = player.getEntity().getAttribute(modifier.attribute);
            if(inst != null && !inst.hasModifier(modifier.modifier().id())) {
                inst.addTransientModifier(modifier.modifier());
            }
        }
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, LastingAttributeAbilityInstance abilityData) {
        activate(syntheticsPlayer, abilityData.modifiers());
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof LastingAttributeAbilityInstance.Data activeData) {
            return Optional.of(new LastingAttributeAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static LastingAttributeAbilityInstance.Data create(ActiveAbilityOptions options, List<Modifier> modifiers, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new LastingAttributeAbilityInstance.Data(options, modifiers, Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }
    public static LastingAttributeAbilityInstance.Data create(ActiveAbilityOptions options,  String titlePathOverride, ResourceLocation texturePathOverride, Modifier... modifiers) {
        return new LastingAttributeAbilityInstance.Data(options, List.of(modifiers), Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }

    public static Modifier modifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        return new Modifier(attribute, modifier);
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        ActiveAbilityType.activeAbilityDescription(ability, description);
        if(ability.abilityData() instanceof LastingAttributeAbilityInstance.Data data) {
            ChatFormatting colour = ability.abilityNature().defaultColour();
            for(Modifier modifier : data.modifiers()) {
                if(modifier.modifier().operation() == AttributeModifier.Operation.ADD_VALUE) {
                    description.add(Component.translatable("abilities.synthetics.description.operation_add").withStyle(colour));
                    description.add(Component.translatable("abilities.synthetics.description.ability_factor", modifier.modifier().amount()).withStyle(colour));
                } else {
                    description.add(Component.translatable("abilities.synthetics.description.operation_mult").withStyle(colour));
                    if(ability.abilityData().factor() >= 0) {
                        description.add(Component.translatable("abilities.synthetics.description.ability_factor_mult_plus", modifier.modifier.amount() * 100d).withStyle(colour));
                    } else {
                        description.add(Component.translatable("abilities.synthetics.description.ability_factor_mult", modifier.modifier.amount() * 100d).withStyle(colour));
                    }
                }
            }
        }
    }

    public record Modifier(Holder<Attribute> attribute, AttributeModifier modifier) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
                Attribute.STREAM_CODEC, Modifier::attribute,
                AttributeModifier.STREAM_CODEC, Modifier::modifier,
                Modifier::new);

        public static final MapCodec<Modifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
                AttributeModifier.CODEC.fieldOf("modifier").forGetter(Modifier::modifier)
        ).apply(instance, Modifier::new));
    }


}
