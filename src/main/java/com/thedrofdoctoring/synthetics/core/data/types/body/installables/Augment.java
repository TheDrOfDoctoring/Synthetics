package com.thedrofdoctoring.synthetics.core.data.types.body.installables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.client.renderers.installables.IInstallableModel;
import com.thedrofdoctoring.synthetics.client.renderers.installables.IInstallableModelSupplier;
import com.thedrofdoctoring.synthetics.codec.ExtendedStreamCodecs;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public record Augment(int complexity, int powerCost, int maxTotal, int maxPerPart, HolderSet<BodyPart> validParts, Optional<HolderSet<Ability>> abilities, Optional<HolderSet<Augment>> groupedWith, ResourceLocation augmentID) implements IBodyInstallable<Augment>, IInstallableModelSupplier {

    public static final MapCodec<Augment> CODEC = MapCodec.recursive("Augment", (a) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("complexity").forGetter(Augment::complexity),
            Codec.INT.optionalFieldOf("power_cost", 0).forGetter(Augment::powerCost),
            Codec.INT.optionalFieldOf("max_copies_total", 1).forGetter(Augment::maxTotal),
            Codec.INT.optionalFieldOf("max_copies_per_part", 1).forGetter(Augment::maxPerPart),
            BodyPart.SET_CODEC.fieldOf("valid_parts").forGetter(Augment::validParts),
            Ability.SET_CODEC.optionalFieldOf("abilities").forGetter(Augment::abilities),
            Augment.SET_CODEC.optionalFieldOf("grouped_with").forGetter(Augment::groupedWith),
            ResourceLocation.CODEC.fieldOf("augment_id").forGetter(Augment::augmentID)
    ).apply(instance, Augment::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, Augment> STREAM_CODEC = ExtendedStreamCodecs.composite(
            ByteBufCodecs.VAR_INT, Augment::complexity,
            ByteBufCodecs.VAR_INT, Augment::powerCost,
            ByteBufCodecs.VAR_INT, Augment::maxTotal,
            ByteBufCodecs.VAR_INT, Augment::maxPerPart,
            ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS), Augment::validParts,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.ABILITIES)), Augment::abilities,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.AUGMENTS)), Augment::groupedWith,
            ResourceLocation.STREAM_CODEC, Augment::augmentID,
            Augment::new);


    @Override
    public @NotNull String toString() {
        return augmentID().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof Augment augment) {
            return augment.augmentID.equals(this.augmentID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return augmentID.hashCode();
    }


    public static final Codec<Holder<Augment>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.AUGMENTS, CODEC.codec());

    public static final Codec<HolderSet<Augment>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.AUGMENTS, CODEC.codec());

    public List<Augment> groupedWithList() {
        return groupedWith.map(set -> set
                        .stream()
                        .map(Holder::value)
                        .toList())
                .orElse(Collections.emptyList());
    }


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

    @Override
    public @NotNull Optional<IInstallableModel> getInstallableModel() {
        return IInstallableModelSupplier.getAugmentModel(id());
    }

    public static class Builder {

        private final ResourceKey<Augment> resourceKey;
        private final HolderSet<BodyPart> validParts;
        private int complexity = 1;
        private int powerCost = 0;
        private int maxCopies = 1;
        private int maxCopiesPerPart = 1;
        private HolderSet<Ability> abilities;
        private HolderSet<Augment> groupedWith;

        private final BootstrapContext<Augment> context;


        public Builder(BootstrapContext<Augment> context, ResourceKey<Augment> resourceKey, HolderSet<BodyPart> validParts) {
            this.resourceKey = resourceKey;
            this.validParts = validParts;
            this.context = context;
        }

        public static Builder of(BootstrapContext<Augment> context, ResourceKey<Augment> resourceKey, HolderSet<BodyPart> validParts) {
            return new Builder(context, resourceKey, validParts);
        }

        public Builder complexity(int complexity) {
            this.complexity = complexity;
            return this;
        }

        public Builder powerCost(int powerCost) {
            this.powerCost = powerCost;
            return this;
        }

        public Builder maxCopies(int maxCopies, int maxPerPart) {
            this.maxCopies = maxCopies;
            this.maxCopiesPerPart = maxPerPart;
            return this;
        }

        public Builder abilities(HolderSet<Ability> abilities) {
            this.abilities = abilities;
            return this;
        }
        public Builder abilities(List<ResourceKey<Ability>> abilities) {
            HolderGetter<Ability> getter = context.lookup(SyntheticsData.ABILITIES);
            this.abilities = HolderSet.direct(abilities.stream().map(getter::getOrThrow).toList());
            return this;
        }

        @SafeVarargs
        public final Builder abilities(ResourceKey<Ability>... abilities) {
            HolderGetter<Ability> getter = context.lookup(SyntheticsData.ABILITIES);
            this.abilities = HolderSet.direct(Arrays.stream(abilities).map(getter::getOrThrow).toList());
            return this;
        }

        public Builder groupedWith(TagKey<Augment> augmentGroup) {
            HolderGetter<Augment> getter = context.lookup(SyntheticsData.AUGMENTS);
            this.groupedWith = getter.getOrThrow(augmentGroup);
            return this;
        }

        public ResourceLocation id() {
            return resourceKey.location();
        }
        public ResourceKey<Augment> key() {
            return resourceKey;
        }


        public Augment build() {
            return new Augment(complexity, powerCost, maxCopies, maxCopiesPerPart, validParts, Optional.ofNullable(abilities), Optional.ofNullable(groupedWith), id());
        }
    }
}
