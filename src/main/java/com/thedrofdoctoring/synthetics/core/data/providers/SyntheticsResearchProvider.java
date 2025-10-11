package com.thedrofdoctoring.synthetics.core.data.providers;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchRequirements;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
public class SyntheticsResearchProvider {

    
    public static void createNodes(BootstrapContext<ResearchNode> context) {
        HolderGetter<SyntheticAugment> augmentLookup = context.lookup(SyntheticsData.AUGMENTS);
        HolderGetter<BodyPart> partLookup = context.lookup(SyntheticsData.BODY_PARTS);
        HolderGetter<ResearchNode> nodeLookup = context.lookup(SyntheticsData.RESEARCH_NODES);

        context.register(
                ResearchNodes.INERTIAL_DAMPENERS,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.CYBERNETIC_INERTIAL_DAMPENERS))
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                Pair.of(Ingredient.of(Items.COAL), 64),
                                Pair.of(Ingredient.of(Items.DIAMOND), 4)

                                )),
                        0,
                        0,
                        ResearchNodes.INERTIAL_DAMPENERS.location()
                )
        );
        context.register(
                ResearchNodes.LAUNCH_BOOTS,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.INERTIAL_DAMPENERS),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.LAUNCH_BOOT))
                        ),
                        ResearchRequirements.create(100, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                        )),
                        0,
                        -50,
                        ResearchNodes.LAUNCH_BOOTS.location()
                )
        );
        context.register(
                ResearchNodes.HEART_BATTERY,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.HEART_BATTERY))
                        ),
                        ResearchRequirements.create(100, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                        )),
                        0,
                        -50,
                        ResearchNodes.HEART_BATTERY.location()
                )
        );
    }
    public static Optional<Holder<ResearchNode>> getNode(HolderGetter<ResearchNode> lookup, ResourceKey<ResearchNode> part) {
        return Optional.of((lookup.getOrThrow(part)));
    }

    public static Optional<HolderSet<ResearchNode>> getNodes(HolderGetter<ResearchNode> lookup, List<ResourceKey<ResearchNode>> abilities) {

        List<Holder<ResearchNode>> abilityHolders = abilities.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return Optional.of(HolderSet.direct(abilityHolders));
    }

    public static HolderSet<SyntheticAugment> getInstallableAugments(HolderGetter<SyntheticAugment> lookup, List<ResourceKey<SyntheticAugment>> augments) {

        List<Holder<SyntheticAugment>> augmentHolders = augments.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(augmentHolders);
    }
    public static HolderSet<BodyPart> getInstallableParts(HolderGetter<BodyPart> lookup, List<ResourceKey<BodyPart>> augments) {

        List<Holder<BodyPart>> partHolders = augments.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(partHolders);
    }

}
