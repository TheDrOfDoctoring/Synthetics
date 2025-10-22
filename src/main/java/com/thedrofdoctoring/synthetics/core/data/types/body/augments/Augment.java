package com.thedrofdoctoring.synthetics.core.data.types.body.augments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
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


public record Augment(int complexity, int powerCost, int maxTotal, int maxPerPart, HolderSet<BodyPart> validParts, Optional<HolderSet<Ability>> abilities, ResourceLocation augmentID) implements IBodyInstallable<Augment> {

    public static final MapCodec<Augment> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("complexity").forGetter(Augment::complexity),
            Codec.INT.optionalFieldOf("power_cost", 0).forGetter(Augment::powerCost),
            Codec.INT.optionalFieldOf("max_copies_total", 1).forGetter(Augment::maxTotal),
            Codec.INT.optionalFieldOf("max_copies_per_part", 1).forGetter(Augment::maxPerPart),
            BodyPart.SET_CODEC.fieldOf("valid_parts").forGetter(Augment::validParts),
            Ability.SET_CODEC.optionalFieldOf("abilities").forGetter(Augment::abilities),
            ResourceLocation.CODEC.fieldOf("augment_id").forGetter(Augment::augmentID)
    ).apply(instance, Augment::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, Augment> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.VAR_INT, Augment::complexity,
            ByteBufCodecs.VAR_INT, Augment::powerCost,
            ByteBufCodecs.VAR_INT, Augment::maxTotal,
            ByteBufCodecs.VAR_INT, Augment::maxPerPart,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS), Augment::validParts,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), Augment::abilities,
            ResourceLocation.STREAM_CODEC, Augment::augmentID,
            Augment::new);


    @Override
    public @NotNull String toString() {
        return augmentID().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Augment augment) {
            return augment.augmentID.equals(this.augmentID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return augmentID.hashCode();
    }


    public static Augment create(int complexity, int powerCost, HolderSet<BodyPart> validParts, HolderSet<Ability> abilities, ResourceLocation id) {
        return new Augment(complexity, powerCost, 1, 1, validParts, Optional.of(abilities), id);
    }
    public static Augment create(int complexity, int powerCost, int maxTotal, int maxPerPart, HolderSet<BodyPart> validParts, HolderSet<Ability> abilities, ResourceLocation id) {
        return new Augment(complexity, powerCost, maxTotal, maxPerPart, validParts, Optional.of(abilities), id);
    }

    public static final Codec<Holder<Augment>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.AUGMENTS, CODEC.codec());

    public static final Codec<HolderSet<Augment>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.AUGMENTS, CODEC.codec());

    @Override
    public ResourceLocation id() {
        return augmentID;
    }

    @Override
    public ResourceKey<Registry<Augment>> getType() {
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
