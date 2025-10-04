package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record BodyPartType(ResourceKey<BodyPart> defaultPart, ResourceLocation id) {

    public static final MapCodec<BodyPartType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(SyntheticsData.BODY_PARTS).fieldOf("default_part").forGetter(BodyPartType::defaultPart),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodyPartType::id)
    ).apply(instance, BodyPartType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPartType> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(SyntheticsData.BODY_PARTS), BodyPartType::defaultPart,
            ResourceLocation.STREAM_CODEC, BodyPartType::id,
            BodyPartType::new);

    public static final Codec<Holder<BodyPartType>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.BODY_PART_TYPES, CODEC.codec());

}
