package com.thedrofdoctoring.synthetics.core.data.types.body.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
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
 * @param validSegments - The valid body segments that this body part can belong to
 * @param type - The type of body part this body part is.
 * @param abilities - The optional set of abilities that this body part has
 * @param id - Self ID
 */
public record BodyPart(int maxComplexity, HolderSet<BodySegment> validSegments, Holder<BodyPartType> type, Optional<HolderSet<Ability>> abilities, ResourceLocation id) implements IBodyInstallable<BodyPart> {

    public static final MapCodec<BodyPart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("max_complexity").forGetter(BodyPart::maxComplexity),
            BodySegment.SET_CODEC.fieldOf("valid_body_segments").forGetter(BodyPart::validSegments),
            BodyPartType.HOLDER_CODEC.fieldOf("type").forGetter(BodyPart::type),
            Ability.SET_CODEC.optionalFieldOf("abilities").forGetter(BodyPart::abilities),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodyPart::id)
    ).apply(instance, BodyPart::new));


    public static final Codec<HolderSet<BodyPart>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.BODY_PARTS, CODEC.codec());
    public static final Codec<Holder<BodyPart>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.BODY_PARTS, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BodyPart::maxComplexity,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_SEGMENTS), BodyPart::validSegments,
            ByteBufCodecs.holder(SyntheticsData.BODY_PART_TYPES, BodyPartType.STREAM_CODEC), BodyPart::type,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), BodyPart::abilities,
            ResourceLocation.STREAM_CODEC, BodyPart::id,
            BodyPart::new);

    public static BodyPart create(int maxComplexity, HolderSet<BodySegment> validSegments, Holder<BodyPartType> type, ResourceLocation id) {
        return new BodyPart(maxComplexity, validSegments, type, Optional.empty(), id);
    }
    public static BodyPart create(int maxComplexity, HolderSet<BodySegment> validSegments, Holder<BodyPartType> type, HolderSet<Ability> abilities, ResourceLocation id) {
        return new BodyPart(maxComplexity, validSegments, type, Optional.of(abilities), id);
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BodyPart part) {
            return part.id.equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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
