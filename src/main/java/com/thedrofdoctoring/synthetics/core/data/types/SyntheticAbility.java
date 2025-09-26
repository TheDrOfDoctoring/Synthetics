package com.thedrofdoctoring.synthetics.core.data.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record SyntheticAbility(SyntheticAbilityType abilityType, double factor,
                               AttributeModifier.Operation operation, ResourceLocation id) {

    public static final MapCodec<SyntheticAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SyntheticAbilityType.CODEC.fieldOf("ability_type").forGetter(SyntheticAbility::abilityType),
            Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(SyntheticAbility::factor),
            AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(SyntheticAbility::operation),

            ResourceLocation.CODEC.fieldOf("id").forGetter(SyntheticAbility::id)
    ).apply(instance, SyntheticAbility::new));

    public static SyntheticAbility create(SyntheticAbilityType abilityType, ResourceLocation id) {
        return new SyntheticAbility(abilityType, 1d, AttributeModifier.Operation.ADD_VALUE, id);

    }
    public static SyntheticAbility create(SyntheticAbilityType abilityType, ResourceLocation id, double factor, AttributeModifier.Operation operation) {
        return new SyntheticAbility(abilityType, factor, operation, id);
    }

    public static final Codec<HolderSet<SyntheticAbility>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAbility> STREAM_CODEC = StreamCodec.composite(
            SyntheticAbilityType.STREAM_CODEC, SyntheticAbility::abilityType,
            ByteBufCodecs.DOUBLE, SyntheticAbility::factor,
            AttributeModifier.Operation.STREAM_CODEC, SyntheticAbility::operation,
            ResourceLocation.STREAM_CODEC, SyntheticAbility::id,
            SyntheticAbility::new);
}
