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

/**
 * @param maxComplexity - The max complexity that this body part can have on it
 * @param segment - The segments that this body part can belong to
 * @param type - The type of body part this body part is. Note that the first element of this tag is the default.
 * @param id - Self ID
 */
public record BodyPart(int maxComplexity, HolderSet<BodySegment> segment, Holder<BodyPartType> type, ResourceLocation id) {

    public static final MapCodec<BodyPart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("max_complexity").forGetter(BodyPart::maxComplexity),
            BodySegment.SET_CODEC.fieldOf("body_segment").forGetter(BodyPart::segment),
            BodyPartType.HOLDER_CODEC.fieldOf("type").forGetter(BodyPart::type),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodyPart::id)
    ).apply(instance, BodyPart::new));
    public static final Codec<HolderSet<BodyPart>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.BODY_PARTS, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BodyPart::maxComplexity,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_SEGMENTS), BodyPart::segment,
            ByteBufCodecs.holder(SyntheticsData.BODY_PART_TYPES, BodyPartType.STREAM_CODEC), BodyPart::type,
            ResourceLocation.STREAM_CODEC, BodyPart::id,
            BodyPart::new);
}
