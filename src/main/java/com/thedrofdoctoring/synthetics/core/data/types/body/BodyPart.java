package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @param maxComplexity - The max complexity that this body part can have on it
 * @param segment - The segments that this body part can belong to
 * @param type - The type of body part this body part is. Note that the first element of this tag is the default.
 * @param id - Self ID
 */
public record BodyPart(int maxComplexity, HolderSet<BodySegment> segment, Holder<BodyPartType> type, Optional<HolderSet<SyntheticAbility>> abilities, ResourceLocation id) implements IBodyInstallable<BodyPart> {

    public static final MapCodec<BodyPart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("max_complexity").forGetter(BodyPart::maxComplexity),
            BodySegment.SET_CODEC.fieldOf("body_segment").forGetter(BodyPart::segment),
            BodyPartType.HOLDER_CODEC.fieldOf("type").forGetter(BodyPart::type),
            SyntheticAbility.SET_CODEC.optionalFieldOf("abilities").forGetter(BodyPart::abilities),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodyPart::id)
    ).apply(instance, BodyPart::new));


    public static final Codec<HolderSet<BodyPart>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.BODY_PARTS, CODEC.codec());
    public static final Codec<Holder<BodyPart>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.BODY_PARTS, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BodyPart::maxComplexity,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_SEGMENTS), BodyPart::segment,
            ByteBufCodecs.holder(SyntheticsData.BODY_PART_TYPES, BodyPartType.STREAM_CODEC), BodyPart::type,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), BodyPart::abilities,
            ResourceLocation.STREAM_CODEC, BodyPart::id,
            BodyPart::new);

    public static BodyPart create(int complexity, HolderSet<BodySegment> segment, Holder<BodyPartType> type, ResourceLocation id) {
        return new BodyPart(complexity, segment, type, Optional.empty(), id);
    }
    public static BodyPart create(int complexity, HolderSet<BodySegment> segment, Holder<BodyPartType> type, HolderSet<SyntheticAbility> abilities, ResourceLocation id) {
        return new BodyPart(complexity, segment, type, Optional.of(abilities), id);
    }

    @Override
    public ResourceKey<Registry<BodyPart>> getType() {
        return SyntheticsData.BODY_PARTS;
    }

    @Override
    public @NotNull ItemStack createDefaultItemStack() {
        ItemStack stack = new ItemStack(SyntheticsItems.BODY_PART_INSTALLABLE);
        stack.set(SyntheticsDataComponents.BODY_PART, Holder.direct(this));
        return stack;
    }
    @Override
    public @NotNull ItemStack createDefaultItemStack(HolderLookup.Provider provider) {
        ItemStack stack = new ItemStack(SyntheticsItems.BODY_PART_INSTALLABLE);
        stack.set(SyntheticsDataComponents.BODY_PART, provider.lookupOrThrow(SyntheticsData.BODY_PARTS).getOrThrow(ResourceKey.create(getType(), id())));
        return stack;
    }
}
