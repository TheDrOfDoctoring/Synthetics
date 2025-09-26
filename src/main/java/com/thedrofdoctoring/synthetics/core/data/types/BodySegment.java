package com.thedrofdoctoring.synthetics.core.data.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record BodySegment(int maxComplexity, Holder<BodySegmentType> type,  ResourceLocation id) {

    public static final MapCodec<BodySegment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("max_complexity").forGetter(BodySegment::maxComplexity),
            BodySegmentType.HOLDER_CODEC.fieldOf("type").forGetter(BodySegment::type),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodySegment::id)

    ).apply(instance, BodySegment::new));

    public static final Codec<HolderSet<BodySegment>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.BODY_SEGMENTS, CODEC.codec());


    public static final StreamCodec<RegistryFriendlyByteBuf, BodySegment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BodySegment::maxComplexity,
            ByteBufCodecs.holder(SyntheticsData.BODY_SEGMENT_TYPES, BodySegmentType.STREAM_CODEC), BodySegment::type,
            ResourceLocation.STREAM_CODEC, BodySegment::id,
            BodySegment::new);
}
