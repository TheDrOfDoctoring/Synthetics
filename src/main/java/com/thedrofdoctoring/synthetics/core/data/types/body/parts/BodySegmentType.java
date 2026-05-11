package com.thedrofdoctoring.synthetics.core.data.types.body.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.client.renderers.installables.*;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record BodySegmentType(Holder<BodySegment> defaultSegment, ResourceLocation id, BodyPosition bodyPosition) implements IInstallableModelSupplier, IInstallableModelPositioner {

    public static final MapCodec<BodySegmentType> CODEC = MapCodec.recursive("Body Segment Type", (a) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            BodySegment.HOLDER_CODEC.fieldOf("default_segment").forGetter(BodySegmentType::defaultSegment),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodySegmentType::id),
            BodyPosition.CODEC.fieldOf("position").forGetter(BodySegmentType::bodyPosition)
    ).apply(instance, BodySegmentType::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodySegmentType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holder(SyntheticsData.BODY_SEGMENTS, BodySegment.STREAM_CODEC), BodySegmentType::defaultSegment,
            ResourceLocation.STREAM_CODEC, BodySegmentType::id,
            BodyPosition.STREAM_CODEC, BodySegmentType::bodyPosition,
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

    @Override
    public IBodyPosition getModelPosition() {
        return bodyPosition;
    }

    @Override
    public @NotNull Optional<IInstallableModel> getInstallableModel() {
        return IInstallableModelSupplier.getSegmentModel(id());
    }
}
