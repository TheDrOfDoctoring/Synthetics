package com.thedrofdoctoring.synthetics.core.data.providers;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@SuppressWarnings({"unused", "DataFlowIssue"})
public class SyntheticsResearchProvider {

    
    public static void createNodes(BootstrapContext<ResearchNode> context) {
        createAugmentNodes(context);
        createBodyPartNodes(context);
    }

    private static final Map<ResourceKey<ResearchNode>, ResearchNode> nodes = new HashMap<>();

    private static void createBodyPartNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.MECHANICAL_PARTS, context
                        )
                        .unlocksParts(List.of(BodyParts.MECHANICAL_LEFT_HAND, BodyParts.MECHANICAL_RIGHT_HAND))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 24)
                                ))
                        .position(0, -150)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ARTIFICIAL_CAPILLARIES, context
                        )
                        .unlocksItem(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                        .experience(50)
                        .parent(ResearchNodes.MECHANICAL_PARTS)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ROTTEN_FLESH), 32),
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 8),
                                        Pair.of(Ingredient.of(Tags.Items.FOODS_RAW_MEAT), 24)
                                ))
                        .position(0, -120)
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
                        .position(-30, -90)
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
                        .position(30, -90)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_FEET, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_LEFT_FOOT, BodyParts.ORGANIC_RIGHT_FOOT))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BONE), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(60, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_FEET, context
                        )
                        .parent(ResearchNodes.ORGANIC_FEET)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_LEFT_FOOT, BodyParts.CYBERNETIC_RIGHT_FOOT))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(60, -30)
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
                        .position(-30, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_HANDS, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_LEFT_HAND, BodyParts.ORGANIC_RIGHT_HAND))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BONE), 4),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 12)
                                ))
                        .position(0, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_HANDS, context
                        )
                        .parent(ResearchNodes.ORGANIC_HANDS)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_LEFT_HAND, BodyParts.CYBERNETIC_RIGHT_HAND))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 8)
                                ))
                        .position(0, -30)
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
                        .position(90, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_EYES, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_LEFT_EYE, BodyParts.ORGANIC_RIGHT_EYE))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.GLASS), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 8)
                                ))
                        .position(150, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_EYES, context
                        )
                        .parent(ResearchNodes.ORGANIC_EYES)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_LEFT_EYE, BodyParts.CYBERNETIC_RIGHT_EYE))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 8)
                                ))
                        .position(150, -30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORGANIC_BRAIN, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.ORGANIC_BRAIN))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_NEURON.get()), 16)
                                ))
                        .position(120, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_BRAIN, context
                        )
                        .parent(ResearchNodes.ORGANIC_BRAIN)
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_BRAIN))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 32)
                                ))
                        .position(120, -30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.MECHANICAL_HEART, context
                        )
                        .parent(ResearchNodes.ARTIFICIAL_TISSUE)
                        .unlocksParts(
                                List.of(BodyParts.MECHANICAL_HEART))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_COPPER), 16),
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_GOLD), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.IRON_GEAR.get()), 8)
                                ))
                        .position(150, -60)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.RIBCAGES, context
                        )
                        .unlocksParts(
                                List.of(BodyParts.ARMOURED_RIBCAGE, BodyParts.SYNTHETIC_RIBCAGE))
                        .experience(250)
                        .parent(ResearchNodes.ORGANIC_BONES)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Tags.Items.BONES), 48)
                                ))
                        .position(90, -30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CYBERNETIC_HEART, context
                        )
                        .unlocksParts(
                                List.of(BodyParts.CYBERNETIC_HEART, BodyParts.ORGANIC_HEART))
                        .experience(250)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 12)
                                ))
                        .position(150, -30)
                        .tab(ResearchNodes.TAB_BODY_PARTS)
        );


    }

    private static void createEnergyAugmentNodes(BootstrapContext<ResearchNode> context) {

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
                        .position(-400, -150)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.METABOLIC_CONVERTER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.METABOLIC_CONVERTER))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 16)
                                ))
                        .position(-370, -120)
                        .parent(ResearchNodes.HEART_BATTERY)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.FAT_CONVERTER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.FAT_CONVERTER))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BLAZE_POWDER), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8)
                                ))
                        .position(-370, -90)
                        .parent(ResearchNodes.METABOLIC_CONVERTER)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SHOCK_ABSORBER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.SHOCK_ABSORBER))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(ItemTags.WOOL), 24),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 6),
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 16)
                                ))
                        .position(-430, -120)
                        .parent(ResearchNodes.HEART_BATTERY)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.POSITION_LOCK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.POSITION_LOCK))
                        .experience(50)
                        .parent(ResearchNodes.SHOCK_ABSORBER)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), 6),
                                        Pair.of(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), 6)
                                ))
                        .position(-430, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SOLAR_TISSUE, context
                        )
                        .parent(ResearchNodes.HEART_BATTERY)
                        .unlocksAugments(
                                List.of(Augments.SOLAR_TISSUE))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 24),
                                        Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 40)
                                ))
                        .position(-400, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ADVANCED_SOLAR_TISSUE, context
                        )
                        .parent(ResearchNodes.SOLAR_TISSUE)
                        .unlocksAugments(
                                List.of(Augments.ADVANCED_SOLAR_TISSUE))
                        .experience(250)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 64),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 48),
                                        Pair.of(Ingredient.of(Items.LAPIS_LAZULI), 64)
                                ))
                        .position(-400, -30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
    }
    private static void createMobilityAugmentNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.MECHANICAL_DAMPENERS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.MECHANICAL_INERTIAL_DAMPENERS))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                        Pair.of(Ingredient.of(Items.COAL), 16)
                                ))
                        .position(-200, -150)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.LAUNCH_BOOTS, context
                        )
                        .parent(ResearchNodes.MECHANICAL_DAMPENERS)
                        .unlocksAugments(
                                List.of(Augments.LAUNCH_BOOT))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 16),
                                        Pair.of(Ingredient.of(Items.GUNPOWDER), 16)
                                ))
                        .position(-170, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.BASIC_DAMPENERS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.BASIC_INERTIAL_DAMPENERS))
                        .experience(125)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 24),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 4)
                                ))
                        .position(-200, -90)
                        .parent(ResearchNodes.MECHANICAL_DAMPENERS)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.FEET_WALL_CLIMB, context
                        )
                        .unlocksAugments(
                                List.of(Augments.MAGNETIC_FEET_IMPLANTS))
                        .experience(40)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.STRING), 25),
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 16),
                                        Pair.of(Ingredient.of(Items.SLIME_BALL), 16)
                                ))
                        .position(-170, -90)
                        .parent(ResearchNodes.LAUNCH_BOOTS)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTEGRATED_EXOSKELETON, context
                        )
                        .parent(ResearchNodes.BASIC_DAMPENERS)
                        .unlocksAugments(
                                List.of(Augments.INTEGRATED_EXOSKELETON))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(Items.BONE), 40),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 5)
                                ))
                        .position(-170, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.WATERWALKING_BOOT, context
                        )
                        .parent(ResearchNodes.BASIC_DAMPENERS)
                        .unlocksAugments(
                                List.of(Augments.WATERWALKING_BOOT))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ICE), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 4),
                                        Pair.of(Ingredient.of(Items.GLOW_INK_SAC), 8)
                                ))
                        .position(-230, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INERTIAL_DAMPENERS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.CYBERNETIC_INERTIAL_DAMPENERS))
                        .experience(250)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 32),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.ANCIENT_ALLOY.get()), 1)
                                ))
                        .position(-200, -30)
                        .parent(ResearchNodes.BASIC_DAMPENERS)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.TELEPORT, context
                        )
                        .unlocksAugments(
                                List.of(Augments.TELEPORT))
                        .experience(150)
                        .parent(ResearchNodes.INERTIAL_DAMPENERS)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 20),
                                        Pair.of(Ingredient.of(Tags.Items.GEMS_AMETHYST), 20)
                                ))
                        .position(-170, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ROCKET_BOOTS, context
                        )
                        .parent(ResearchNodes.WATERWALKING_BOOT)
                        .unlocksAugments(
                                List.of(Augments.ROCKET_BOOTS))
                        .experience(250)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 24),
                                        Pair.of(Ingredient.of(Items.OBSIDIAN), 16),
                                        Pair.of(Ingredient.of(Items.TNT), 24)
                                ))
                        .position(-230, -30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
    }
    private static void createCombatAugmentNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SHOCK_GAUNTLET, context
                        )
                        .unlocksAugments(
                                List.of(Augments.SHOCK_GAUNTLET))
                        .experience(100)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_COPPER), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 16)
                                ))
                        .position(0, -150)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.HAND_FLAMETHROWER, context
                        )
                        .parent(ResearchNodes.SHOCK_GAUNTLET)
                        .unlocksAugments(
                                List.of(Augments.HAND_FLAMETHROWER))
                        .experience(50)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 15),
                                        Pair.of(Ingredient.of(Items.GUNPOWDER), 20),
                                        Pair.of(Ingredient.of(Items.BLAZE_POWDER), 20)
                                ))
                        .position(30, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTERNAL_PLATING, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTERNAL_PLATING))
                        .parent(ResearchNodes.SHOCK_GAUNTLET)
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_BLOCK), 2),
                                        Pair.of(Ingredient.of(ItemTags.WOOL), 20),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 8)
                                ))
                        .position(-30, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ADRENALINE_INJECTOR, context
                        )
                        .unlocksAugments(
                                List.of(Augments.ADRENALINE_INJECTOR))
                        .experience(100)
                        .parent(ResearchNodes.SHOCK_GAUNTLET)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.BLAZE_POWDER), 24),
                                        Pair.of(Ingredient.of(Items.SUGAR), 24),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 24)
                                ))
                        .position(-60, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CHEST_HARPOON, context
                        )
                        .unlocksAugments(
                                List.of(Augments.CHEST_HARPOON))
                        .parent(ResearchNodes.SHOCK_GAUNTLET)
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.CHAIN), 24),
                                        Pair.of(Ingredient.of(Items.GUNPOWDER), 16),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 6)
                                ))
                        .position(0, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.HAND_REPULSOR, context
                        )
                        .unlocksAugments(
                                List.of(Augments.HAND_REPULSOR))
                        .experience(150)
                        .parent(ResearchNodes.CHEST_HARPOON)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.GUNPOWDER), 15),
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 4),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8)
                                ))
                        .position(-30, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.FIRE_RESISTANT_TISSUE, context
                        )
                        .unlocksAugments(
                                List.of(Augments.FIRE_RESISTANT_TISSUE))
                        .experience(150)
                        .parent(ResearchNodes.CHEST_HARPOON)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.NETHERITE_SCRAP), 6),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 10),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 24)
                                ))
                        .position(60, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.BLOOD_TANK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.RESERVE_BLOOD_TANK))
                        .experience(75)
                        .parent(ResearchNodes.CHEST_HARPOON)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_IRON), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 8),
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_COPPER), 32)
                                ))
                        .position(30, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.AUXILIARY_VIEWLINK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.AUXILIARY_VIEWLINK))
                        .experience(150)
                        .parent(ResearchNodes.CHEST_HARPOON)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.GLASS_PANES), 16),
                                        Pair.of(Ingredient.of(Items.INK_SAC), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 6)
                                ))
                        .position(-60, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.TISSUE_AUGMENTS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.SHIMMER_LAYER, Augments.REPAIRING_TISSUE))
                        .experience(250)
                        .parent(ResearchNodes.FIRE_RESISTANT_TISSUE)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.GLASS_PANES), 32),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 48)
                                ))
                        .position(60, -30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.CAMOUFLAGE_INVISIBILITY, context
                        )
                        .unlocksAugments(
                                List.of(Augments.CAMOUFLAGE_INVISIBILITY))
                        .experience(100)
                        .parent(ResearchNodes.FIRE_RESISTANT_TISSUE)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.FERMENTED_SPIDER_EYE), 12),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_TISSUE.get()), 10),
                                        Pair.of(Ingredient.of(Items.ARMADILLO_SCUTE), 8)
                                ))
                        .position(90, -30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ENTITY_DETECTOR, context
                        )
                        .unlocksAugments(
                                List.of(Augments.ENTITY_DETECTOR))
                        .experience(250)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(Items.ECHO_SHARD), 8)
                                ))
                        .position(0, -30)
                        .parent(ResearchNodes.CHEST_HARPOON)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );


    }

    private static void createLinkedAugmentNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTEGRATED_REDSTONE_LINK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTEGRATED_REDSTONE_LINK))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 6),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 20),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 5)
                                ))
                        .position(200, -150)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.INTERNAL_CAMERA_LINK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTERNAL_CAMERA_LINK))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 6),
                                        Pair.of(Ingredient.of(Tags.Items.GLASS_PANES), 20),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 5)
                                ))
                        .parent(ResearchNodes.INTEGRATED_REDSTONE_LINK)
                        .position(200, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.DRONE_LINK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.DRONE_LINK))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 20),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8)
                                ))
                        .position(230, -60)
                        .parent(ResearchNodes.INTERNAL_CAMERA_LINK)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.PERMEABLE_LINK, context
                        )
                        .unlocksAugments(
                                List.of(Augments.PERMEABLE_LINK))
                        .experience(125)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.AMETHYST_SHARD), 16),
                                        Pair.of(Ingredient.of(Items.ENDER_EYE), 8),
                                        Pair.of(Ingredient.of(Items.ECHO_SHARD), 4)
                                ))
                        .position(170, -60)
                        .parent(ResearchNodes.INTERNAL_CAMERA_LINK)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.PERMEATOR, context
                        )
                        .unlocksAugments(
                                List.of(Augments.PERMEATOR))
                        .experience(500)
                        .parent(ResearchNodes.PERMEABLE_LINK)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 24),
                                        Pair.of(Ingredient.of(Items.NETHER_STAR), 1),
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 16)
                                ))
                        .position(170, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );

        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.LINKED_TELEPORTER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.LINKED_TELEPORTER))
                        .experience(300)
                        .parent(ResearchNodes.INTERNAL_CAMERA_LINK)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.CHORUS_FRUIT), 16)
                                ))
                        .position(200, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
    }
    private static void createUtilityAugmentNodes(BootstrapContext<ResearchNode> context) {
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ITEM_MAGNET, context
                        )
                        .unlocksAugments(
                                List.of(Augments.ITEM_MAGNET))
                        .experience(75)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 16)
                                ))
                        .position(400, -150)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SYNTHETIC_LINING, context
                        )
                        .unlocksAugments(
                                List.of(Augments.SYNTHETIC_LINING))
                        .experience(50)
                        .parent(ResearchNodes.ITEM_MAGNET)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 16)
                                ))
                        .position(460, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.REINFORCED_TENDONS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.REINFORCED_TENDONS))
                        .experience(75)
                        .parent(ResearchNodes.ITEM_MAGNET)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Tags.Items.INGOTS_IRON), 16),
                                        Pair.of(Ingredient.of(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 2)
                                ))
                        .position(430, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.MECHANICAL_BRUSH, context
                        )
                        .parent(ResearchNodes.ITEM_MAGNET)
                        .unlocksAugments(
                                List.of(Augments.MECHANICAL_BRUSH))
                        .experience(25)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 6),
                                        Pair.of(Ingredient.of(SyntheticsItems.IRON_GEAR.get()), 8),
                                        Pair.of(Ingredient.of(Items.FEATHER), 8)
                                ))
                        .position(370, -120)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.BLASTING_ARM, context
                        )
                        .parent(ResearchNodes.REINFORCED_TENDONS)
                        .unlocksAugments(
                                List.of(Augments.BLASTING_ARM))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.GUNPOWDER), 32),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 10),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 16)
                                ))
                        .position(430, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.EXTENDED_GRIP, context
                        )
                        .unlocksAugments(
                                List.of(Augments.EXTENDED_GRIP))
                        .experience(50)
                        .parent(ResearchNodes.ITEM_MAGNET)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.IRON_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.GOLD_INGOT), 12),
                                        Pair.of(Ingredient.of(Items.STRING), 30)
                                ))
                        .position(400, -90)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.FLUID_AUGMENTS, context
                        )
                        .unlocksAugments(
                                List.of(Augments.INTEGRATED_RESPIRATOR, Augments.VISION_CLARIFIER))
                        .experience(25)
                        .parent(ResearchNodes.EXTENDED_GRIP)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.COPPER_INGOT), 25),
                                        Pair.of(Ingredient.of(Items.BUCKET), 10),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 2)
                                ))
                        .position(430, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.NIGHT_VISION, context
                        )
                        .unlocksAugments(
                                List.of(Augments.NIGHT_VISION))
                        .experience(75)
                        .parent(ResearchNodes.EXTENDED_GRIP)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.GLOW_INK_SAC), 12),
                                        Pair.of(Ingredient.of(Tags.Items.GLASS_PANES), 32),
                                        Pair.of(Ingredient.of(Items.EMERALD), 8)
                                ))
                        .position(370, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.AUTOPILOT, context
                        )
                        .unlocksAugments(
                                List.of(Augments.MOTION_AUTOPILOT))
                        .experience(150)
                        .parent(ResearchNodes.EXTENDED_GRIP)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ECHO_SHARD), 4),
                                        Pair.of(Ingredient.of(Items.REDSTONE), 24),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 10)
                                ))
                        .position(340, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.NEURAL_CHAMBER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.NEURAL_CHAMBER))
                        .experience(150)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ECHO_SHARD), 4),
                                        Pair.of(Ingredient.of(Items.EMERALD), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 6)
                                ))
                        .position(340, -30)
                        .parent(ResearchNodes.AUTOPILOT)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.UNIVERSAL_TRANSLATOR, context
                        )
                        .unlocksAugments(
                                List.of(Augments.UNIVERSAL_TRANSLATOR))
                        .experience(150)
                        .parent(ResearchNodes.EXTENDED_GRIP)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.EMERALD), 25),
                                        Pair.of(Ingredient.of(Items.DIAMOND), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 8)
                                ))
                        .position(460, -60)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.ORE_DOWSING, context
                        )
                        .unlocksAugments(
                                List.of(Augments.ORE_DOWSING))
                        .experience(250)
                        .parent(ResearchNodes.SHRINKIFIER)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.LODESTONE), 1),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 12),
                                        Pair.of(Ingredient.of(Items.SCULK), 16)
                                ))
                        .position(400, 30)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
        register(context,
                ResearchNode.Builder.of(
                                ResearchNodes.SHRINKIFIER, context
                        )
                        .unlocksAugments(
                                List.of(Augments.SHRINKIFIER))
                        .experience(150)
                        .parent(ResearchNodes.EXTENDED_GRIP)
                        .requiredItems(
                                List.of(
                                        Pair.of(Ingredient.of(Items.ENDER_PEARL), 8),
                                        Pair.of(Ingredient.of(SyntheticsItems.BASIC_CIRCUIT.get()), 16),
                                        Pair.of(Ingredient.of(Items.CHORUS_FRUIT), 16)
                                ))
                        .position(400, 0)
                        .tab(ResearchNodes.TAB_AUGMENTS)
        );
    }

    private static void createAugmentNodes(BootstrapContext<ResearchNode> context) {
        createEnergyAugmentNodes(context);
        createMobilityAugmentNodes(context);
        createCombatAugmentNodes(context);
        createLinkedAugmentNodes(context);
        createUtilityAugmentNodes(context);
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
        ResearchNode node = builder.build();
        context.register(builder.key(), node);
        nodes.put(builder.key(), node);
    }

    // used for position offsets, needs listed node to already be registered
    private static @Nullable ResearchNode getNodeUnsafe(ResourceKey<ResearchNode> node) {
        return nodes.get(node);
    }

    private static Vector2i above(ResourceKey<ResearchNode> nodeKey, int amount) {
        ResearchNode node = getNodeUnsafe(nodeKey);
        return new Vector2i(node.x(), node.y() - amount);
    }

    private static Vector2i above(ResourceKey<ResearchNode> nodeKey) {
        return above(nodeKey, 30);
    }

    private static Vector2i left(ResourceKey<ResearchNode> nodeKey, int amount) {
        ResearchNode node = getNodeUnsafe(nodeKey);
        return new Vector2i(node.x() - amount, node.y());
    }

    private static Vector2i right(ResourceKey<ResearchNode> nodeKey, int amount) {
        return left(nodeKey, -amount);
    }

    private static Vector2i right(ResourceKey<ResearchNode> nodeKey) {
        return right(nodeKey, 30);
    }

    private static Vector2i left(ResourceKey<ResearchNode> nodeKey) {
        return left(nodeKey, -30);
    }

    private static Vector2i offset(ResourceKey<ResearchNode> nodeKey, int x, int y) {
        ResearchNode node = getNodeUnsafe(nodeKey);
        return new Vector2i(node.x() + x, node.y() + y);
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
