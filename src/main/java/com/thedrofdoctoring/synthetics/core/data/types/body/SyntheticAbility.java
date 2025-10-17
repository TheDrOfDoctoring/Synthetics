package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record SyntheticAbility(AbilityType abilityType, AbilityData abilityData, ResourceLocation id) {

    public static final MapCodec<SyntheticAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityType.CODEC.fieldOf("ability_type").forGetter(SyntheticAbility::abilityType),
            AbilityData.DISPATCH_CODEC.fieldOf("ability_data").forGetter(SyntheticAbility::abilityData),
            ResourceLocation.CODEC.fieldOf("id").forGetter(SyntheticAbility::id)
    ).apply(instance, SyntheticAbility::new));

    public static SyntheticAbility create(AbilityType abilityType, AbilityData data, ResourceLocation id) {
        return new SyntheticAbility(abilityType, data, id);
    }

    public static final Codec<HolderSet<SyntheticAbility>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAbility> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, SyntheticAbility::abilityType,
            AbilityData.DISPATCH_STREAM, SyntheticAbility::abilityData,
            ResourceLocation.STREAM_CODEC, SyntheticAbility::id,
            SyntheticAbility::new);
}
