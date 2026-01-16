package com.thedrofdoctoring.synthetics.core.data.types.body.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record BodySegmentType(ResourceKey<BodySegment> defaultSegment, ResourceLocation id) {

    public static final MapCodec<BodySegmentType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(SyntheticsData.BODY_SEGMENTS).fieldOf("default_segment").forGetter(BodySegmentType::defaultSegment),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodySegmentType::id)
    ).apply(instance, BodySegmentType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodySegmentType> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(SyntheticsData.BODY_SEGMENTS), BodySegmentType::defaultSegment,
            ResourceLocation.STREAM_CODEC, BodySegmentType::id,
            BodySegmentType::new);

    public static final Codec<Holder<BodySegmentType>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.BODY_SEGMENT_TYPES, CODEC.codec());

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(obj instanceof BodySegmentType part) {
            return part.id.equals(this.id);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
