package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.Optional;


@SuppressWarnings("unused")
public record ResearchNode(Optional<Holder<ResearchNode>> parent, ResearchNodeUnlocks unlocked, ResearchRequirements requirements, int x, int y, Holder<ResearchTab> tab, ResourceLocation id) {


    public static final MapCodec<ResearchNode> CODEC = MapCodec.recursive("ResearchNode", (a) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchNode.HOLDER_CODEC.optionalFieldOf("parent").forGetter(ResearchNode::parent),
            ResearchNodeUnlocks.CODEC.fieldOf("unlocked").forGetter(ResearchNode::unlocked),
            ResearchRequirements.CODEC.fieldOf("requirements").forGetter(ResearchNode::requirements),
            Codec.INT.fieldOf("x").forGetter(ResearchNode::x),
            Codec.INT.fieldOf("y").forGetter(ResearchNode::y),
            ResearchTab.HOLDER_CODEC.fieldOf("tab").forGetter(ResearchNode::tab),
            ResourceLocation.CODEC.fieldOf("id").forGetter(ResearchNode::id)
    ).apply(instance, ResearchNode::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNode> STREAM_CODEC = StreamCodec.recursive(
            recursive -> NeoForgeStreamCodecs.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.holder(SyntheticsData.RESEARCH_NODES, ResearchNode.STREAM_CODEC)), ResearchNode::parent,
                    ResearchNodeUnlocks.STREAM_CODEC, ResearchNode::unlocked,
                    ResearchRequirements.STREAM_CODEC, ResearchNode::requirements,
                    ByteBufCodecs.VAR_INT, ResearchNode::x,
                    ByteBufCodecs.VAR_INT, ResearchNode::y,
                    ByteBufCodecs.holder(SyntheticsData.RESEARCH_TABS, ResearchTab.STREAM_CODEC), ResearchNode::tab,
                    ResourceLocation.STREAM_CODEC, ResearchNode::id,
                    ResearchNode::new
                    )
            );

    public Component description() {
        return Component.translatable("research." + id().getNamespace() + "." + id().getPath() + ".desc");
    }
    public Component title() {
        return Component.translatable("research." + id().getNamespace() + "." + id().getPath());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ResearchNode node) {
            return node.id.equals(this.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final Codec<HolderSet<ResearchNode>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.RESEARCH_NODES, CODEC.codec());

    public static final Codec<Holder<ResearchNode>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.RESEARCH_NODES, CODEC.codec());


    public static class Builder {

        private Holder<ResearchNode> parent;
        private ResearchNodeUnlocks unlocked = new ResearchNodeUnlocks(Optional.empty(), Optional.empty(), Optional.empty());
        private ResearchRequirements requirements = new ResearchRequirements(0, Optional.empty());
        private int x;
        private int y;
        private final ResourceKey<ResearchNode> resourceKey;
        private Holder<ResearchTab> tab;
        private final BootstrapContext<ResearchNode> context;
        private boolean setPosition;

        public Builder(BootstrapContext<ResearchNode> context, ResourceKey<ResearchNode> resourceKey) {
            this.resourceKey = resourceKey;
            this.context = context;
        }

        public static Builder of(ResourceKey<ResearchNode> resourceKey, BootstrapContext<ResearchNode> context) {
            return new Builder(context, resourceKey);
        }
        public static Builder of(ResourceKey<ResearchNode> resourceKey) {
            return new Builder(null, resourceKey);
        }

        public Builder parent(Holder<ResearchNode> parent) {
            this.parent = parent;
            return this;
        }
        public Builder parent(ResourceKey<ResearchNode> parent) {
            this.parent = context.lookup(SyntheticsData.RESEARCH_NODES).getOrThrow(parent);
            return this;
        }

        public Builder unlocks(ResearchNodeUnlocks unlocked) {
            this.unlocked = unlocked;
            return this;
        }
        public Builder position(int x, int y) {
            this.setPosition = true;
            this.x = x; this.y = y;
            return this;
        }

        public Builder unlocksItem(Ingredient item) {
            this.unlocked = new ResearchNodeUnlocks(Optional.of(item), this.unlocked.augments(), this.unlocked.parts());
            return this;
        }

        public Builder unlocksAugments(HolderSet<Augment> augments) {
            this.unlocked = new ResearchNodeUnlocks(this.unlocked.item(), Optional.of(augments), this.unlocked.parts());
            return this;
        }

        public Builder unlocksParts(HolderSet<BodyPart> parts) {
            this.unlocked = new ResearchNodeUnlocks(this.unlocked.item(), this.unlocked.augments(), Optional.of(parts));
            return this;
        }

        public Builder unlocksParts(List<ResourceKey<BodyPart>> validParts) {
            HolderGetter<BodyPart> getter = context.lookup(SyntheticsData.BODY_PARTS);
            HolderSet<BodyPart> parts = HolderSet.direct(validParts.stream().map(getter::getOrThrow).toList());
            return unlocksParts(parts);
        }
        public Builder unlocksAugments(List<ResourceKey<Augment>> augments) {
            HolderGetter<Augment> getter = context.lookup(SyntheticsData.AUGMENTS);
            HolderSet<Augment> augmentsSet = HolderSet.direct(augments.stream().map(getter::getOrThrow).toList());
            return unlocksAugments(augmentsSet);
        }

        public Builder requirements(ResearchRequirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder experience(int xp) {
            this.requirements = new ResearchRequirements(xp, this.requirements.requiredItems());
            return this;
        }

        public Builder tab(Holder<ResearchTab> tab) {
            this.tab = tab;
            return this;
        }
        public Builder tab(ResourceKey<ResearchTab> tab) {
            this.tab = context.lookup(SyntheticsData.RESEARCH_TABS).getOrThrow(tab);
            return this;
        }

        public ResourceKey<ResearchNode> key() {
            return resourceKey;
        }
        public ResourceLocation id() {
            return key().location();
        }

        public Builder requiredItems(List<Pair<Ingredient, Integer>> items) {
            this.requirements = new ResearchRequirements(this.requirements.experienceCost(), Optional.of(items));
            return this;
        }


        public ResearchNode build() {

            if(tab == null) {
                throw new IllegalStateException("Research node built but belongs to no valid research tab");
            }
            if(!setPosition) {
                throw new IllegalStateException("Unset position for Research Node");
            }

            return new ResearchNode(Optional.ofNullable(parent), unlocked, requirements, x, y, tab, resourceKey.location());
        }


    }


}
