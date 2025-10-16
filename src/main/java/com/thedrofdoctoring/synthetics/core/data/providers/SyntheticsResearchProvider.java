package com.thedrofdoctoring.synthetics.core.data.providers;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

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
                        -125,
                        -50,
                        ResearchNodes.HEART_BATTERY.location()
                )
        );
        context.register(
                ResearchNodes.ARTIFICIAL_CAPILLARIES,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.createUnlocks(
                                Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                        ),
                        ResearchRequirements.create(50, List.of(
                                Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 8),
                                Pair.of(Ingredient.of(Tags.Items.FOODS_RAW_MEAT), 24)
                        )),
                        60,
                        275,
                        ResearchNodes.ARTIFICIAL_CAPILLARIES.location()
                )
        );
        context.register(
                ResearchNodes.ARTIFICIAL_NEURONS,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ARTIFICIAL_CAPILLARIES),
                        ResearchNode.createUnlocks(
                                Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get())
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 20),
                                Pair.of(Ingredient.of(Items.REDSTONE), 32),
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 8)
                        )),
                        40,
                        235,
                        ResearchNodes.ARTIFICIAL_NEURONS.location()
                )
        );
        context.register(
                ResearchNodes.ARTIFICIAL_TISSUE,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ARTIFICIAL_CAPILLARIES),
                        ResearchNode.createUnlocks(
                                Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get())
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                Pair.of(Ingredient.of(Items.STRING), 12),
                                Pair.of(Ingredient.of(Items.CLAY), 32)
                        )),
                        80,
                        235,
                        ResearchNodes.ARTIFICIAL_TISSUE.location()
                )
        );
        context.register(
                ResearchNodes.ORGANIC_FEET,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE),
                        ResearchNode.partsUnlock(
                                getInstallableParts(partLookup, List.of(BodyParts.ORGANIC_FEET))
                        ),
                        ResearchRequirements.create(175, List.of(
                                Pair.of(Ingredient.of(Items.BONE), 8),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )),
                        80,
                        190,
                        ResearchNodes.ORGANIC_FEET.location()
                )
        );
        context.register(
                ResearchNodes.CYBERNETIC_FEET,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ORGANIC_FEET),
                        ResearchNode.partsUnlock(
                                getInstallableParts(partLookup, List.of(BodyParts.CYBERNETIC_FEET))
                        ),
                        ResearchRequirements.create(175, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )),
                        80,
                        160 ,
                        ResearchNodes.CYBERNETIC_FEET.location()
                )
        );
        context.register(
                ResearchNodes.ARTIFICIAL_SKIN,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE),
                        ResearchNode.partsUnlock(
                                getInstallableParts(partLookup, List.of(BodyParts.CYBERNETIC_TISSUE))
                        ),
                        ResearchRequirements.create(50, List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 20),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )),
                        50,
                        190,
                        ResearchNodes.ARTIFICIAL_SKIN.location()
                )
        );
        context.register(
                ResearchNodes.SOLAR_TISSUE,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.HEART_BATTERY),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.SOLAR_TISSUE))
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 32),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 24),
                                Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 40)
                        )),
                        -150,
                        -80,
                        ResearchNodes.SOLAR_TISSUE.location()
                )
        );
        context.register(
                ResearchNodes.ADVANCED_SOLAR_TISSUE,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.SOLAR_TISSUE),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.ADVANCED_SOLAR_TISSUE))
                        ),
                        ResearchRequirements.create(125, List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 64),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 48),
                                Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 64)
                        )),
                        -180,
                        -80,
                        ResearchNodes.ADVANCED_SOLAR_TISSUE.location()
                )
        );
        context.register(
                ResearchNodes.FLUID_AUGMENTS,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.INTEGRATED_RESPIRATOR, Augments.VISION_CLARIFIER))
                        ),
                        ResearchRequirements.create(25, List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 25),
                                Pair.of(Ingredient.of(Items.BUCKET), 10),
                                Pair.of(Ingredient.of(Items.DIAMOND), 2)

                        )),
                        30,
                        0,
                        ResearchNodes.FLUID_AUGMENTS.location()
                )
        );
        context.register(
                ResearchNodes.STOMACH_AUGMENTS,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.HEART_BATTERY),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.METABOLIC_CONVERTER))
                        ),
                        ResearchRequirements.create(50, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.REDSTONE), 16)
                        )),
                        -180,
                        -50,
                        ResearchNodes.STOMACH_AUGMENTS.location()
                )
        );
        context.register(
                ResearchNodes.HAND_WALL_CLIMB,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.EMITTABLE_ADHESIVE))
                        ),
                        ResearchRequirements.create(40, List.of(
                                Pair.of(Ingredient.of(Items.STRING), 25),
                                Pair.of(Ingredient.of(Items.SPIDER_EYE), 10),
                                Pair.of(Ingredient.of(Items.SLIME_BALL), 16)

                        )),
                        60,
                        0,
                        ResearchNodes.HAND_WALL_CLIMB.location()
                )
        );
        context.register(
                ResearchNodes.INTERNAL_PLATING,
                new ResearchNode(
                        Optional.empty(),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.INTERNAL_PLATING))
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.IRON_BLOCK), 2),
                                Pair.of(Ingredient.of(ItemTags.WOOL), 20),
                                Pair.of(Ingredient.of(Items.DIAMOND), 8)
                        )),
                        90,
                        30,
                        ResearchNodes.INTERNAL_PLATING.location()
                )
        );
        context.register(
                ResearchNodes.INTEGRATED_EXOSKELETON,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.INTERNAL_PLATING),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.INTEGRATED_EXOSKELETON))
                        ),
                        ResearchRequirements.create(75, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                Pair.of(Ingredient.of(Items.BONE), 40),
                                Pair.of(Ingredient.of(Items.DIAMOND), 5)
                        )),
                        90,
                        0,
                        ResearchNodes.INTEGRATED_EXOSKELETON.location()
                )
        );
        context.register(
                ResearchNodes.ORGANIC_HANDS,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE),
                        ResearchNode.partsUnlock(
                                getInstallableParts(partLookup, List.of(BodyParts.ORGANIC_HANDS))
                        ),
                        ResearchRequirements.create(175, List.of(
                                Pair.of(Ingredient.of(Items.BONE), 4),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 12)
                        )),
                        110,
                        190,
                        ResearchNodes.ORGANIC_HANDS.location()
                )
        );
        context.register(
                ResearchNodes.CYBERNETIC_HANDS,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.ORGANIC_HANDS),
                        ResearchNode.partsUnlock(
                                getInstallableParts(partLookup, List.of(BodyParts.CYBERNETIC_HANDS))
                        ),
                        ResearchRequirements.create(175, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )),
                        110,
                        160 ,
                        ResearchNodes.CYBERNETIC_HANDS.location()
                )
        );
        context.register(
                ResearchNodes.EXTENDED_GRIP,
                new ResearchNode(
                        getNode(nodeLookup, ResearchNodes.HAND_WALL_CLIMB),
                        ResearchNode.augmentsUnlock(
                                getInstallableAugments(augmentLookup, List.of(Augments.EXTENDED_GRIP))
                        ),
                        ResearchRequirements.create(50, List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(Items.STRING), 30)
                        )),
                        60,
                        30,
                        ResearchNodes.EXTENDED_GRIP.location()
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
