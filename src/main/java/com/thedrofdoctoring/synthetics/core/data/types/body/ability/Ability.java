package com.thedrofdoctoring.synthetics.core.data.types.body.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record Ability(AbilityType abilityType, AbilityData abilityData, ResourceLocation id) {

    public static final MapCodec<Ability> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AbilityType.CODEC.fieldOf("ability_type").forGetter(Ability::abilityType),
            AbilityData.DISPATCH_CODEC.fieldOf("ability_data").forGetter(Ability::abilityData),
            ResourceLocation.CODEC.fieldOf("id").forGetter(Ability::id)
    ).apply(instance, Ability::new));

    public static Ability create(AbilityType abilityType, AbilityData data, ResourceLocation id) {
        return new Ability(abilityType, data, id);
    }

    public static final Codec<HolderSet<Ability>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, Ability::abilityType,
            AbilityData.DISPATCH_STREAM, Ability::abilityData,
            ResourceLocation.STREAM_CODEC, Ability::id,
            Ability::new);
}
