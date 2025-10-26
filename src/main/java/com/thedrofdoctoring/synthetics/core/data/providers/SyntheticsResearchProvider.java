package com.thedrofdoctoring.synthetics.core.data.providers;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings("unused")
public class SyntheticsResearchProvider {

    
    public static void createNodes(BootstrapContext<ResearchNode> context) {
        createAugmentNodes(context);
        createBodyPartNodes(context);
    }

    private static void createBodyPartNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_CAPILLARIES, context
                        )
                        .unlocksItem(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 8),
                                        Pair.of(Ingredient.of(Tags.Items.FOODS_RAW_MEAT), 24)
                                ))
                        .position(0, 120)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_NEURONS, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_CAPILLARIES)
                        .unlocksItem(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 20),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 8)
                                ))
                        .position(-30, 90)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_TISSUE, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_CAPILLARIES)
                        .unlocksItem(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                        Pair.of(Ingredient.of(Items.STRING), 12),
                                        Pair.of(Ingredient.of(Items.CLAY), 32)
                                ))
                        .position(30, 90)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_FEET, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_FEET))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BONE), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(60, 60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_FEET, context
                        )
                        .parent(ResearchNodes.ORGANIC_FEET)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_FEET))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(60, 30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_SKIN, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_TISSUE, BodyParts.ORGANIC_TISSUE))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 20),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(-30, 60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_HANDS, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_HANDS))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BONE), 4),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 12)
                                ))
                        .position(0, 60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_HANDS, context
                        )
                        .parent(ResearchNodes.ORGANIC_HANDS)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_HANDS))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(0, 30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );


        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_BONES, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_TIBIA, BodyParts.ORGANIC_RIBCAGE, BodyParts.ORGANIC_SKULL))
                        .experience(125)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BONE), 32),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 6),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 2)
                                ))
                        .position(90, 60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
    }
    private static void createAugmentNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INERTIAL_DAMPENERS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.CYBERNETIC_INERTIAL_DAMPENERS))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                        Pair.of(Ingredient.of(Items.COAL), 64),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 4)
                                ))
                        .position(0, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.LAUNCH_BOOTS, context
                        )
                        .parent(ResearchNodes.INERTIAL_DAMPENERS)
                        .unlocksAugments(
                                List.of(Augments.LAUNCH_BOOT))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                                ))
                        .position(0, -50)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.HEART_BATTERY, context
                        )
                        .unlocksAugments(
                                List.of(Augments.HEART_BATTERY))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15)
                                ))
                        .position(-125, -50)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );


        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SOLAR_TISSUE, context
                        )
                        .parent(ResearchNodes.HEART_BATTERY)
                        .unlocksAugments(
                                List.of(Augments.SOLAR_TISSUE))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 24),
                                        Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 40)
                                ))
                        .position(-150, -80)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ADVANCED_SOLAR_TISSUE, context
                        )
                        .parent(ResearchNodes.SOLAR_TISSUE)
                        .unlocksAugments(
                                List.of(Augments.ADVANCED_SOLAR_TISSUE))
                        .experience(125)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 64),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 48),
                                        Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 64)
                                ))
                        .position(-180, -80)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.FLUID_AUGMENTS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTEGRATED_RESPIRATOR, Augments.VISION_CLARIFIER))
                        .experience(25)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.BUCKET), 10),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 2)
                                ))
                        .position(30, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.STOMACH_AUGMENTS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.METABOLIC_CONVERTER))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 16)
                                ))
                        .position(-180, -50)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.HAND_WALL_CLIMB, context
                        )
                        .unlocksAugments(
                                List.of(Augments.EMITTABLE_ADHESIVE))
                        .experience(40)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.STRING), 25),
                                        Pair.of(Ingredient.of(Items.SPIDER_EYE), 10),
                                        Pair.of(Ingredient.of(Items.SLIME_BALL), 16)
                                ))
                        .position(60, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTERNAL_PLATING, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTERNAL_PLATING))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_BLOCK), 2),
                                        Pair.of(Ingredient.of(ItemTags.WOOL), 20),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 8)
                                ))
                        .position(90, 30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTEGRATED_EXOSKELETON, context
                        )
                        .parent(ResearchNodes.INTERNAL_PLATING)
                        .unlocksAugments(
                                List.of(Augments.INTEGRATED_EXOSKELETON))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.BONE), 40),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 5)
                                ))
                        .position(90, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );


        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.EXTENDED_GRIP, context
                        )
                        .parent(ResearchNodes.HAND_WALL_CLIMB)
                        .unlocksAugments(
                                List.of(Augments.EXTENDED_GRIP))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(Items.STRING), 30)
                                ))
                        .position(60, 30)
                        .tab(ResearchNodes.TAB_AUGMENTS)

        );


        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.AUTOPILOT, context
                        )
                        .unlocksAugments(
                                List.of(Augments.MOTION_AUTOPILOT))
                        .experience(30)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 32),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 48)
                                ))
                        .position(120, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
    }

    public static void createTabs(BootstrapContext<ResearchTab> context) {

        context.register(ResearchNodes.TAB_BODY_PARTS,
                new ResearchTab(
                        createPart(context, BodyParts.ORGANIC_HEART)
                )
        );
        context.register(ResearchNodes.TAB_AUGMENTS,
                new ResearchTab(
                        createAugment(context, Augments.CYBERNETIC_INERTIAL_DAMPENERS)
                )
        );

    }

    private static void register(BootstrapContext<ResearchNode> context, ResearchNode.Builder builder) {
        context.register(builder.key(), builder.build());
    }
    public static Holder<ResearchNode> getNode(HolderGetter<ResearchNode> lookup, ResourceKey<ResearchNode> part) {
        return (lookup.getOrThrow(part));
    }

    private static ItemStack createAugment(BootstrapContext<ResearchTab> provider, ResourceKey<Augment> augmentKey) {
        ItemStack stack = new ItemStack(SyntheticsItems.AUGMENT_INSTALLABLE);
        stack.set(SyntheticsDataComponents.AUGMENT, provider.lookup(SyntheticsData.AUGMENTS).getOrThrow(augmentKey));
        return stack;
    }
    private static ItemStack createPart(BootstrapContext<ResearchTab> provider, ResourceKey<BodyPart> partKey) {
        ItemStack stack = new ItemStack(SyntheticsItems.BODY_PART_INSTALLABLE);
        stack.set(SyntheticsDataComponents.BODY_PART, provider.lookup(SyntheticsData.BODY_PARTS).getOrThrow(partKey));
        return stack;
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
