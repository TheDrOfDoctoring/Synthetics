package com.thedrofdoctoring.synthetics.body.abilities.passive.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.AttributeAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public class AttributeAbilityInstance extends AbilityPassiveInstance<AttributeAbilityType> {

    private final AttributeModifier.Operation operation;
    private final Holder<Attribute> modified;

    public Optional<Pair<Holder<Attribute>, AttributeModifier>> getModifiedAttribute(LivingEntity entity) {
        if(operation == null) return Optional.empty();
        if(modified != null) {
            return Optional.of(Pair.of(modified, new AttributeModifier(instanceID, getAbilityData().factor(), operation)));
        }
        return Optional.empty();
    }

    public AttributeAbilityInstance(AttributeAbilityType ability, SyntheticsPlayer player, AttributeAbilityData data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
        this.operation = data.operation;
        this.modified = data.attribute;
    }

    public static class AttributeAbilityData extends AbilityData {

        private final AttributeModifier.Operation operation;
        private final Holder<Attribute> attribute;

        public AttributeAbilityData(double factor, AttributeModifier.Operation operation, Holder<Attribute> attribute) {
            super(factor);
            this.operation = operation;
            this.attribute = attribute;
        }

        public AttributeModifier.Operation operation() {
            return operation;
        }

        public Holder<Attribute> attribute() {
            return attribute;
        }

        public static final MapCodec<AttributeAbilityData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(AttributeAbilityData::factor),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeAbilityData::operation),
                Attribute.CODEC.fieldOf("attribute").forGetter(AttributeAbilityData::attribute)
        ).apply(instance, AttributeAbilityData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AttributeAbilityData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, AttributeAbilityData::factor,
                AttributeModifier.Operation.STREAM_CODEC, AttributeAbilityData::operation,
                Attribute.STREAM_CODEC, AttributeAbilityData::attribute,
                AttributeAbilityData::new);

        @Override
        public MapCodec<AttributeAbilityData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, AttributeAbilityData> streamCodec() {
            return STREAM_CODEC;
        }
    }


}
