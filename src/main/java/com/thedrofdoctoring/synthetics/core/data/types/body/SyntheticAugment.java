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
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record SyntheticAugment(int complexity, int powerCost, int maxTotal, int maxPerPart, HolderSet<BodyPart> validParts, Optional<HolderSet<SyntheticAbility>> abilities, ResourceLocation augmentID) implements IBodyInstallable<SyntheticAugment> {

    public static final MapCodec<SyntheticAugment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("complexity").forGetter(SyntheticAugment::complexity),
            Codec.INT.optionalFieldOf("power_cost", 0).forGetter(SyntheticAugment::powerCost),
            Codec.INT.optionalFieldOf("max_copies_total", 1).forGetter(SyntheticAugment::maxTotal),
            Codec.INT.optionalFieldOf("max_copies_per_part", 1).forGetter(SyntheticAugment::maxPerPart),
            BodyPart.SET_CODEC.fieldOf("valid_parts").forGetter(SyntheticAugment::validParts),
            SyntheticAbility.SET_CODEC.optionalFieldOf("abilities").forGetter(SyntheticAugment::abilities),
            ResourceLocation.CODEC.fieldOf("augment_id").forGetter(SyntheticAugment::augmentID)
    ).apply(instance, SyntheticAugment::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAugment> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.VAR_INT, SyntheticAugment::complexity,
            ByteBufCodecs.VAR_INT, SyntheticAugment::powerCost,
            ByteBufCodecs.VAR_INT, SyntheticAugment::maxTotal,
            ByteBufCodecs.VAR_INT, SyntheticAugment::maxPerPart,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS), SyntheticAugment::validParts,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), SyntheticAugment::abilities,
            ResourceLocation.STREAM_CODEC, SyntheticAugment::augmentID,
            SyntheticAugment::new);


    @Override
    public @NotNull String toString() {
        return augmentID().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SyntheticAugment augment) {
            return augment.augmentID.equals(this.augmentID);
        }
        return false;
    }

    public static SyntheticAugment create(int complexity, int powerCost, HolderSet<BodyPart> validParts, HolderSet<SyntheticAbility> abilities, ResourceLocation id) {
        return new SyntheticAugment(complexity, powerCost, 1, 1, validParts, Optional.of(abilities), id);
    }
    public static SyntheticAugment create(int complexity, int powerCost, int maxTotal, int maxPerPart, HolderSet<BodyPart> validParts, HolderSet<SyntheticAbility> abilities, ResourceLocation id) {
        return new SyntheticAugment(complexity, powerCost, maxTotal, maxPerPart, validParts, Optional.of(abilities), id);
    }

    public static final Codec<Holder<SyntheticAugment>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.AUGMENTS, CODEC.codec());

    public static final Codec<HolderSet<SyntheticAugment>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.AUGMENTS, CODEC.codec());

    @Override
    public ResourceLocation id() {
        return augmentID;
    }

    @Override
    public ResourceKey<Registry<SyntheticAugment>> getType() {
        return SyntheticsData.AUGMENTS;
    }

    @Override
    public @NotNull ItemStack createDefaultItemStack() {
        ItemStack stack = new ItemStack(SyntheticsItems.AUGMENT_INSTALLABLE);
        stack.set(SyntheticsDataComponents.AUGMENT, Holder.direct(this));
        return stack;
    }
    @Override
    public @NotNull ItemStack createDefaultItemStack(HolderLookup.Provider provider) {
        ItemStack stack = new ItemStack(SyntheticsItems.AUGMENT_INSTALLABLE);
        stack.set(SyntheticsDataComponents.AUGMENT, provider.lookupOrThrow(SyntheticsData.AUGMENTS).getOrThrow(ResourceKey.create(getType(), id())));
        return stack;
    }
}
