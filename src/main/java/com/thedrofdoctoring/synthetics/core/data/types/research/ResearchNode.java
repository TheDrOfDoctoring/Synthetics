package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record ResearchNode(Optional<Holder<ResearchNode>> parent, ResearchNodeUnlocks unlocked, ResearchRequirements requirements, int x, int y, ResourceLocation id) {


    public static final MapCodec<ResearchNode> CODEC = MapCodec.recursive("ResearchNode", (a) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResearchNode.HOLDER_CODEC.optionalFieldOf("parents").forGetter(ResearchNode::parent),
            ResearchNodeUnlocks.CODEC.fieldOf("unlocked").forGetter(ResearchNode::unlocked),
            ResearchRequirements.CODEC.fieldOf("type").forGetter(ResearchNode::requirements),
            Codec.INT.fieldOf("x").forGetter(ResearchNode::x),
            Codec.INT.fieldOf("y").forGetter(ResearchNode::y),
            ResourceLocation.CODEC.fieldOf("id").forGetter(ResearchNode::id)

    ).apply(instance, ResearchNode::new)));

    public Component description() {
        return Component.translatable("research." + id().getNamespace() + "." + id().getPath() + ".desc");
    }
    public Component title() {
        return Component.translatable("research." + id().getNamespace() + "." + id().getPath());
    }

    public static final Codec<HolderSet<ResearchNode>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.RESEARCH_NODES, CODEC.codec());

    public static final Codec<Holder<ResearchNode>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.RESEARCH_NODES, CODEC.codec());

    public static ResearchNodeUnlocks createUnlocks(HolderSet<SyntheticAugment> augments, HolderSet<BodyPart> parts) {
        return new ResearchNodeUnlocks(Optional.of(augments), Optional.of(parts));
    }
    public static ResearchNodeUnlocks partsUnlock(HolderSet<BodyPart> parts) {
        return new ResearchNodeUnlocks(Optional.empty(), Optional.of(parts));
    }
    public static ResearchNodeUnlocks augmentsUnlock(HolderSet<SyntheticAugment> augments) {
        return new ResearchNodeUnlocks(Optional.of(augments), Optional.empty());
    }


}
