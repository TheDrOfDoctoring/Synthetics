package com.thedrofdoctoring.synthetics.core.data.providers;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
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
import java.util.stream.Collectors;
@SuppressWarnings("unused")
public class SyntheticsResearchProvider {

    
    public static void createNodes(BootstrapContext<ResearchNode> context) {
        HolderGetter<Augment> augmentLookup = context.lookup(SyntheticsData.AUGMENTS);
        HolderGetter<BodyPart> partLookup = context.lookup(SyntheticsData.BODY_PARTS);
        HolderGetter<ResearchNode> nodeLookup = context.lookup(SyntheticsData.RESEARCH_NODES);

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INERTIAL_DAMPENERS,
                                0,
                                0
                        ).unlocksAugments(
                                getInstallableAugments(augmentLookup,
                                        List.of(Augments.CYBERNETIC_INERTIAL_DAMPENERS)
                                )
                        )
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                        Pair.of(Ingredient.of(Items.COAL), 64),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 4)
                                )
                        )
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.LAUNCH_BOOTS,
                                0,
                                -50

                        ).parent(getNode(nodeLookup, ResearchNodes.INERTIAL_DAMPENERS)
                        ).unlocksAugments(
                                getInstallableAugments(augmentLookup,
                                        List.of(Augments.LAUNCH_BOOT)
                                )
                        )
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                                )
                        )
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.HEART_BATTERY,
                                -125,
                                -50
                        ).unlocksAugments(
                                getInstallableAugments(augmentLookup,
                                        List.of(Augments.HEART_BATTERY)
                                )
                        )
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                                )
                        )
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_CAPILLARIES,
                                60,
                                275
                        ).unlocksItem(
                                Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                        )
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 8),
                                        Pair.of(Ingredient.of(Tags.Items.FOODS_RAW_MEAT), 24)
                                )
                        )
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_NEURONS,
                                40,
                                235
                        ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_CAPILLARIES)
                        ).unlocksItem(
                                Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get())
                        )
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 20),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 8)
                                )
                        )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.ARTIFICIAL_TISSUE,
                    80,
                    235
                ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_CAPILLARIES)
                ).unlocksItem(
                        Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get())
                )
                .experience(75)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                Pair.of(Ingredient.of(Items.STRING), 12),
                                Pair.of(Ingredient.of(Items.CLAY), 32)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.ORGANIC_FEET,
                        80,
                        190
                ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE)
                ).unlocksParts(
                        getInstallableParts(partLookup,
                                List.of(BodyParts.ORGANIC_BRAIN)
                        )
                )
                .experience(100)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.BONE), 8),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.CYBERNETIC_FEET,
                    80,
                    160
                ).parent(getNode(nodeLookup, ResearchNodes.ORGANIC_FEET)
                ).unlocksParts(
                        getInstallableParts(partLookup,
                                List.of(BodyParts.CYBERNETIC_FEET)
                        )
                )
                .experience(100)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.ARTIFICIAL_SKIN,
                        50,
                        190
                ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE)
                ).unlocksParts(
                        getInstallableParts(partLookup,
                                List.of(BodyParts.CYBERNETIC_TISSUE, BodyParts.ORGANIC_TISSUE)
                        )
                )
                .experience(50)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 20),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.SOLAR_TISSUE,
                        -150,
                        -80
                ).parent(getNode(nodeLookup, ResearchNodes.HEART_BATTERY)
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.SOLAR_TISSUE)
                        )
                )
                .experience(75)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 32),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 24),
                                Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 40)
                        )
                )
        );


        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.ADVANCED_SOLAR_TISSUE,
                        -180,
                        -80
                ).parent(getNode(nodeLookup, ResearchNodes.SOLAR_TISSUE)
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.ADVANCED_SOLAR_TISSUE)
                        )
                )
                .experience(125)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 64),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 48),
                                Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 64)
                        )
                )
        );


        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.FLUID_AUGMENTS,
                        30,
                        0
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.INTEGRATED_RESPIRATOR, Augments.VISION_CLARIFIER)
                        )
                )
                .experience(25)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.COPPER_INGOT), 25),
                                Pair.of(Ingredient.of(Items.BUCKET), 10),
                                Pair.of(Ingredient.of(Items.DIAMOND), 2)
                        )
                )
        );
        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.STOMACH_AUGMENTS,
                    -180,
                    -50
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.METABOLIC_CONVERTER)
                        )
                )
                .experience(50)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.REDSTONE), 16)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.HAND_WALL_CLIMB,
                    60,
                    0
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.EMITTABLE_ADHESIVE)
                        )
                )
                .experience(40)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.STRING), 25),
                                Pair.of(Ingredient.of(Items.SPIDER_EYE), 10),
                                Pair.of(Ingredient.of(Items.SLIME_BALL), 16)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.INTERNAL_PLATING,
                    90,
                    30
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.INTERNAL_PLATING)
                        )
                )
                .experience(75)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_BLOCK), 2),
                                Pair.of(Ingredient.of(ItemTags.WOOL), 20),
                                Pair.of(Ingredient.of(Items.DIAMOND), 8)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.INTEGRATED_EXOSKELETON,
                    90,
                    0
                ).parent(getNode(nodeLookup, ResearchNodes.INTERNAL_PLATING)
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.INTEGRATED_EXOSKELETON)
                        )
                )
                .experience(75)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                Pair.of(Ingredient.of(Items.BONE), 40),
                                Pair.of(Ingredient.of(Items.DIAMOND), 5)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.ORGANIC_HANDS,
                    110,
                    190
                ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE)
                ).unlocksParts(
                        getInstallableParts(partLookup,
                                List.of(BodyParts.ORGANIC_HANDS)
                        )
                )
                .experience(100)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.BONE), 4),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 12)
                        )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.CYBERNETIC_HANDS,
                    110,
                    160
                ).parent(getNode(nodeLookup, ResearchNodes.ORGANIC_HANDS)
                ).unlocksParts(
                        getInstallableParts(partLookup,
                                List.of(BodyParts.CYBERNETIC_HANDS)
                        )
                )
                .experience(100)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                        )
                )
        );
        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.EXTENDED_GRIP,
                    60,
                    30
                ).parent(getNode(nodeLookup, ResearchNodes.HAND_WALL_CLIMB)
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.EXTENDED_GRIP)
                        )
                )
                .experience(50)
                .requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                Pair.of(Ingredient.of(Items.STRING), 30)
                        )
                )
        );
        register(context,
                ResearchNode.Builder.of(
                    ResearchNodes.ORGANIC_BONES,
                    140,
                    190
                ).parent(getNode(nodeLookup, ResearchNodes.ARTIFICIAL_TISSUE)
                ).unlocksParts(
                    getInstallableParts(partLookup,
                            List.of(BodyParts.ORGANIC_TIBIA, BodyParts.ORGANIC_RIBCAGE, BodyParts.ORGANIC_SKULL)
                    )
                )
                .experience(125)
                .requiredItems(
                    List.of(
                            Pair.of(Ingredient.of(Items.BONE), 32),
                            Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 6),
                            Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 2)
                    )
                )
        );

        register(context,
                ResearchNode.Builder.of(
                        ResearchNodes.AUTOPILOT,
                        120,
                        0
                ).unlocksAugments(
                        getInstallableAugments(augmentLookup,
                                List.of(Augments.MOTION_AUTOPILOT)
                        )
                ).experience(30).requiredItems(
                        List.of(
                                Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 32),
                                Pair.of(Ingredient.of(Items.REDSTONE), 48)
                        )
                )
        );
    }

    private static void register(BootstrapContext<ResearchNode> context, ResearchNode.Builder builder) {
        context.register(builder.key(), builder.build());
    }
    public static Holder<ResearchNode> getNode(HolderGetter<ResearchNode> lookup, ResourceKey<ResearchNode> part) {
        return (lookup.getOrThrow(part));
    }


    public static HolderSet<Augment> getInstallableAugments(HolderGetter<Augment> lookup, List<ResourceKey<Augment>> augments) {

        List<Holder<Augment>> augmentHolders = augments.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(augmentHolders);
    }
    public static HolderSet<BodyPart> getInstallableParts(HolderGetter<BodyPart> lookup, List<ResourceKey<BodyPart>> augments) {

        List<Holder<BodyPart>> partHolders = augments.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(partHolders);
    }

}
