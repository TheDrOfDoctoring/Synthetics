package com.thedrofdoctoring.synthetics.core.data.types.body.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BodySegment(int maxComplexity, Holder<BodySegmentType> type,  ResourceLocation id) implements IBodyInstallable<BodySegment> {

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

    @Override
    public Optional<HolderSet<Ability>> abilities() {
        return Optional.empty();
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BodySegment segment) {
            return segment.id.equals(this.id);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    @Override
    public ResourceKey<Registry<BodySegment>> getType() {
        return SyntheticsData.BODY_SEGMENTS;
    }

    @Override
    public @NotNull ItemStack createDefaultItemStack() {
        return ItemStack.EMPTY;
    }
    @Override
    public @NotNull ItemStack createDefaultItemStack(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }
}
