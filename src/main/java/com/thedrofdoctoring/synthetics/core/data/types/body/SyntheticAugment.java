package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record SyntheticAugment(int complexity, int powerCost, HolderSet<BodyPart> validParts, Optional<HolderSet<SyntheticAbility>> abilities, ResourceLocation augmentID) implements IBodyInstallable {

    public static final MapCodec<SyntheticAugment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("complexity").forGetter(SyntheticAugment::complexity),
            Codec.INT.optionalFieldOf("powerCost", 0).forGetter(SyntheticAugment::powerCost),
            BodyPart.SET_CODEC.fieldOf("validParts").forGetter(SyntheticAugment::validParts),
            SyntheticAbility.SET_CODEC.optionalFieldOf("abilities").forGetter(SyntheticAugment::abilities),
            ResourceLocation.CODEC.fieldOf("augment_id").forGetter(SyntheticAugment::augmentID)
    ).apply(instance, SyntheticAugment::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAugment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyntheticAugment::complexity,
            ByteBufCodecs.VAR_INT, SyntheticAugment::powerCost,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS), SyntheticAugment::validParts,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), SyntheticAugment::abilities,
            ResourceLocation.STREAM_CODEC, SyntheticAugment::augmentID,
            SyntheticAugment::new);

    @Override
    public @NotNull String toString() {
        return augmentID().toString();
    }
    public static SyntheticAugment create(int complexity, int powerCost, HolderSet<BodyPart> validParts, HolderSet<SyntheticAbility> abilities, ResourceLocation id) {
        return new SyntheticAugment(complexity, powerCost, validParts, Optional.of(abilities), id);
    }
    public static final Codec<HolderSet<SyntheticAugment>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.AUGMENTS, CODEC.codec());

    @Override
    public ResourceLocation id() {
        return augmentID;
    }


}
