package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;


@SuppressWarnings("unused")
public record ResearchNode(Optional<Holder<ResearchNode>> parent, ResearchNodeUnlocks unlocked, ResearchRequirements requirements, int x, int y, ResourceLocation id) {


    public static final MapCodec<ResearchNode> CODEC = MapCodec.recursive("ResearchNode", (a) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchNode.HOLDER_CODEC.optionalFieldOf("parent").forGetter(ResearchNode::parent),
            ResearchNodeUnlocks.CODEC.fieldOf("unlocked").forGetter(ResearchNode::unlocked),
            ResearchRequirements.CODEC.fieldOf("requirements").forGetter(ResearchNode::requirements),
            Codec.INT.fieldOf("x").forGetter(ResearchNode::x),
            Codec.INT.fieldOf("y").forGetter(ResearchNode::y),
            ResourceLocation.CODEC.fieldOf("id").forGetter(ResearchNode::id)

    ).apply(instance, ResearchNode::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNode> STREAM_CODEC = StreamCodec.recursive(
            recursive -> StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.holder(SyntheticsData.RESEARCH_NODES, ResearchNode.STREAM_CODEC)), ResearchNode::parent,
                    ResearchNodeUnlocks.STREAM_CODEC, ResearchNode::unlocked,
                    ResearchRequirements.STREAM_CODEC, ResearchNode::requirements,
                    ByteBufCodecs.VAR_INT, ResearchNode::x,
                    ByteBufCodecs.VAR_INT, ResearchNode::y,
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
        private final int x;
        private final int y;
        private final ResourceKey<ResearchNode> resourceKey;

        public Builder(ResourceKey<ResearchNode> resourceKey, int x, int y) {
            this.resourceKey = resourceKey;
            this.x = x;
            this.y = y;
        }

        public static Builder of(ResourceKey<ResearchNode> resourceKey, int x, int y) {
            return new Builder(resourceKey, x, y);
        }

        public Builder parent(Holder<ResearchNode> parent) {
            this.parent = parent;
            return this;
        }

        public Builder unlocks(ResearchNodeUnlocks unlocked) {
            this.unlocked = unlocked;
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

        public Builder requirements(ResearchRequirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder experience(int xp) {
            this.requirements = new ResearchRequirements(xp, this.requirements.requiredItems());
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
            return new ResearchNode(Optional.ofNullable(parent), unlocked, requirements, x, y, resourceKey.location());
        }


    }


}
