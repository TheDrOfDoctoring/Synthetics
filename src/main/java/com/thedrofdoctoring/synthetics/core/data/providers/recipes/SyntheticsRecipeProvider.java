package com.thedrofdoctoring.synthetics.core.data.providers.recipes;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.SyntheticsItemTags;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SyntheticsRecipeProvider extends RecipeProvider {

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new SyntheticsRecipeProvider(output, future));
    }


    public SyntheticsRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput pRecipeOutput, HolderLookup.@NotNull Provider lookup) {

        createShapedRecipes(pRecipeOutput);
        createForgeRecipes(pRecipeOutput, lookup);
        createAugmentForgeRecipes(pRecipeOutput, lookup);
        createBodyPartForgeRecipes(pRecipeOutput, lookup);
        createPairRecipes(pRecipeOutput, lookup);

    }

    private void createForgeRecipes(RecipeOutput output, HolderLookup.@NotNull Provider lookup) {

        SyntheticForgeRecipeBuilder.create(SyntheticsItems.MEDIUM_BATTERY.get(), 1)
                .define('A', Items.DIAMOND)
                .define('B', Tags.Items.INGOTS_COPPER)
                .define('C', Tags.Items.INGOTS_GOLD)
                .pattern(" C ")
                .pattern("BAB")
                .pattern(" C ")
                .lavaCost(50)
                .recipeTime(100)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("medium_battery")
                );
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.ARTIFICIAL_NEURON.get(), 1)
                .define('A', Items.ROTTEN_FLESH)
                .define('B', Tags.Items.INGOTS_COPPER)
                .define('C', Items.REDSTONE)
                .pattern("AAA")
                .pattern("BCB")
                .pattern(" C ")
                .lavaCost(10)
                .recipeTime(50)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_NEURONS)

                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("artificial_neuron")
                );
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.ARTIFICIAL_CAPILLARY.get(), 1)
                .define('A', Items.ROTTEN_FLESH)
                .define('B', Tags.Items.INGOTS_COPPER)
                .pattern(" B ")
                .pattern("AAA")
                .pattern(" B ")
                .lavaCost(10)
                .recipeTime(50)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_CAPILLARIES)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(output, Synthetics.rl("artificial_capillary")
                );
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.ARTIFICIAL_TISSUE.get(), 2)
                .define('A', Items.ROTTEN_FLESH)
                .define('B', Items.STRING)
                .pattern(" B ")
                .pattern("AAA")
                .pattern(" B ")
                .lavaCost(10)
                .recipeTime(50)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_TISSUE)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("artificial_tissue")
                );

        SyntheticForgeRecipeBuilder.create(SyntheticsItems.IRON_GEAR.get(), 1)
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.NUGGETS_IRON)
                .pattern(" B ")
                .pattern("BAB")
                .pattern(" B ")
                .lavaCost(20)
                .recipeTime(50)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("iron_gear")
                );

    }

    private void createAugmentForgeRecipes(RecipeOutput output, HolderLookup.@NotNull Provider lookup) {
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.SOLAR_TISSUE), 1)
                .define('A', Tags.Items.GLASS_PANES)
                .define('B', Items.LAPIS_LAZULI)
                .define('C', Items.COPPER_INGOT)
                .pattern("AAA")
                .pattern("BBB")
                .pattern("CCC")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.SOLAR_TISSUE)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("solar_tissue")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.ADVANCED_SOLAR_TISSUE), 1)
                .define('D', augmentIngredient(lookup, Augments.SOLAR_TISSUE))
                .define('A', Items.DIAMOND)
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', Items.COPPER_INGOT)
                .pattern("AAA")
                .pattern("BDB")
                .pattern("CCC")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.ADVANCED_SOLAR_TISSUE)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("advanced_solar_tissue")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTEGRATED_RESPIRATOR), 1)
                .define('A', Items.COPPER_INGOT)
                .define('B', Items.LAPIS_LAZULI)
                .define('D', SyntheticsItems.BASIC_CIRCUIT.get())
                .pattern("AAA")
                .pattern("BDB")
                .pattern("AAA")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.FLUID_AUGMENTS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("integrated_respirator")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.VISION_CLARIFIER), 1)
                .define('A', Items.COPPER_INGOT)
                .define('B', Tags.Items.GLASS_PANES)
                .pattern("AAA")
                .pattern("B B")
                .pattern("AAA")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.FLUID_AUGMENTS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("vision_clarifier")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.METABOLIC_CONVERTER), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', Items.REDSTONE)
                .pattern("AAA")
                .pattern("BCB")
                .pattern("AAA")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.STOMACH_AUGMENTS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("metabolic_converter")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.MAGNETIC_FEET_IMPLANTS), 1)
                .define('A', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('B', Items.SLIME_BALL)
                .define('C', Tags.Items.INGOTS_IRON)
                .pattern("A A")
                .pattern("BCB")
                .pattern("A A")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.FEET_WALL_CLIMB)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("magnetic_feet_implants")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTEGRATED_EXOSKELETON), 1)
                .define('A', SyntheticsItems.ANCIENT_ALLOY.get())
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', SyntheticsItems.BASIC_CIRCUIT.get())
                .pattern("  C")
                .pattern(" B ")
                .pattern("A  ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INTEGRATED_EXOSKELETON)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("integrated_exoskeleton")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTERNAL_PLATING), 1)
                .define('A', Tags.Items.GEMS_DIAMOND)
                .define('B', Tags.Items.INGOTS_IRON)
                .pattern("BAB")
                .pattern("BBB")
                .pattern(" B ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INTERNAL_PLATING)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("internal_plating")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.CYBERNETIC_INERTIAL_DAMPENERS), 1)
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', ItemTags.WOOL)
                .define('C', Items.REDSTONE)
                .define('D', Items.PAPER)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("A A")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INERTIAL_DAMPENERS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("inertial_dampeners")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.LAUNCH_BOOT), 1)
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', ItemTags.WOOL)
                .define('C', Items.DIAMOND)
                .define('D', Items.PAPER)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("A A")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.LAUNCH_BOOTS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("launch_boots")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.HEART_BATTERY), 1)
                .define('A', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('B', Tags.Items.INGOTS_COPPER)
                .define('C', Tags.Items.INGOTS_GOLD)
                .pattern(" C ")
                .pattern("BAB")
                .pattern(" C ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.HEART_BATTERY)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("heart_battery")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.MOTION_AUTOPILOT), 1)
                .define('A', Items.REDSTONE)
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern(" B ")
                .pattern("ACA")
                .pattern(" B ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.AUTOPILOT)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("autopilot_motion")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.UNIVERSAL_TRANSLATOR), 1)
                .define('A', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('B', Items.EMERALD)
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" A ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.UNIVERSAL_TRANSLATOR)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("universal_translator")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.HAND_FLAMETHROWER), 1)
                .define('A', Items.BLAZE_POWDER)
                .define('B', Items.GUNPOWDER)
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" A ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.HAND_FLAMETHROWER)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("hand_flamethrower")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.EXTENDED_GRIP), 1)
                .define('A', Items.REDSTONE)
                .define('B', Items.CHAIN)
                .pattern(" B ")
                .pattern("ABA")
                .pattern(" B ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.EXTENDED_GRIP)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("extended_grip")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTEGRATED_REDSTONE_LINK), 1)
                .define('A', Items.ENDER_PEARL)
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" B ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INTEGRATED_REDSTONE_LINK)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("integrated_redstone_link")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTERNAL_CAMERA_LINK), 1)
                .define('A', Items.ENDER_PEARL)
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', Tags.Items.GLASS_PANES)
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" B ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INTERNAL_CAMERA_LINK)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("internal_camera_link")
                );
    }
    private void createBodyPartForgeRecipes(RecipeOutput output, HolderLookup.@NotNull Provider lookup) {

        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_LEFT_FOOT), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("  A")
                .pattern("  A")
                .pattern("CBB")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_FEET)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_left_foot")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_RIGHT_FOOT), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("A  ")
                .pattern("A  ")
                .pattern("BBC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_FEET)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_right_foot")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_LEFT_FOOT), 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_LEFT_FOOT))
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern("ABB")
                .pattern("C  ")
                .pattern("   ")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_FEET)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_left_foot")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_RIGHT_FOOT), 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_RIGHT_FOOT))
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern("BBA")
                .pattern("  C")
                .pattern("   ")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_FEET)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_right_foot")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_TISSUE), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("ABB")
                .pattern("BCC")
                .pattern("   ")
                .lavaCost(30)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_SKIN)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_tissue")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_TISSUE), 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_TISSUE))
                .define('B', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('C', Tags.Items.INGOTS_GOLD)
                .define('D', SyntheticsItems.ANCIENT_ALLOY.get())
                .pattern("ABB")
                .pattern("CCD")
                .pattern("   ")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_SKIN)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_tissue")
                );

        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_LEFT_HAND), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern(" AB")
                .pattern(" A ")
                .pattern("BBC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_HANDS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_left_hand")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_RIGHT_HAND), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("BA ")
                .pattern(" A ")
                .pattern("CCB")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_HANDS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_right_hand")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_LEFT_HAND), 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_LEFT_HAND))
                .define('B', SyntheticsItems.PURE_ANCIENT_ALLOY.get())
                .define('C', Tags.Items.INGOTS_GOLD)
                .define('D', SyntheticsItems.BASIC_CIRCUIT.get())
                .pattern("DD ")
                .pattern("BAB")
                .pattern("DCC")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_HANDS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_left_hand")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_RIGHT_HAND), 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_RIGHT_HAND))
                .define('B', SyntheticsItems.PURE_ANCIENT_ALLOY.get())
                .define('C', Tags.Items.INGOTS_GOLD)
                .define('D', SyntheticsItems.BASIC_CIRCUIT.get())
                .pattern(" DD")
                .pattern("BAB")
                .pattern("DCC")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_HANDS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_right_hand")
                );

        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.MECHANICAL_RIGHT_HAND), 1)
                .define('B', SyntheticsItems.ANCIENT_ALLOY.get())
                .define('C', SyntheticsItemTags.IRON_GEARS)
                .define('D', Items.REDSTONE)
                .define('A', Items.DIAMOND)
                .pattern(" DD")
                .pattern(" AB")
                .pattern("DCC")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.MECHANICAL_PARTS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("mechanical_right_hand")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.MECHANICAL_LEFT_HAND), 1)
                .define('B', SyntheticsItems.ANCIENT_ALLOY.get())
                .define('C', SyntheticsItemTags.IRON_GEARS)
                .define('D', Items.REDSTONE)
                .define('A', Items.DIAMOND)
                .pattern("DD ")
                .pattern("BA ")
                .pattern("DCC")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.MECHANICAL_PARTS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("mechanical_left_hand")
                );

        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_TIBIA), 1)
                .define('B', Items.BONE)
                .define('A', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("  B")
                .pattern(" A ")
                .pattern("B  ")
                .lavaCost(20)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_BONES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_tibia")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_SKULL), 1)
                .define('B', Items.BONE)
                .define('A', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("B B")
                .pattern("BAB")
                .pattern("BBB")
                .lavaCost(20)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_BONES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_skull")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_RIBCAGE), 1)
                .define('B', Items.BONE)
                .define('A', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .pattern("BAB")
                .pattern("BAB")
                .pattern("BAB")
                .lavaCost(20)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_BONES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_ribcage")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_LEFT_EYE), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .define('D', Tags.Items.GLASS_PANES)
                .pattern("AAB")
                .pattern(" D ")
                .pattern("CCC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_EYES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_left_eye")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_RIGHT_EYE), 1)
                .define('A', SyntheticsItems.ARTIFICIAL_NEURON.get())
                .define('B', SyntheticsItems.ARTIFICIAL_TISSUE.get())
                .define('C', SyntheticsItems.ARTIFICIAL_CAPILLARY.get())
                .define('D', Tags.Items.GLASS_PANES)
                .pattern("BAA")
                .pattern(" D ")
                .pattern("CCC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.ORGANIC_EYES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("organic_right_eye")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_LEFT_EYE), 1)
                .define('A', SyntheticsItems.ANCIENT_ALLOY.get())
                .define('C', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('D', partIngredient(lookup, BodyParts.ORGANIC_LEFT_EYE))
                .pattern("AA ")
                .pattern(" D ")
                .pattern("CCC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_EYES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_left_eye")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_RIGHT_EYE), 1)
                .define('A', SyntheticsItems.ANCIENT_ALLOY.get())
                .define('C', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('D', partIngredient(lookup, BodyParts.ORGANIC_LEFT_EYE))
                .pattern(" AA")
                .pattern(" D ")
                .pattern("CCC")
                .lavaCost(10)
                .recipeTime(80)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_EYES)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_right_eye")
                );
    }



    private ItemStack createAugment(HolderLookup.Provider provider, ResourceKey<Augment> augmentKey) {
        return provider.lookupOrThrow(SyntheticsData.AUGMENTS).get(augmentKey).orElseThrow().value().createDefaultItemStack(provider);
    }

    private ItemStack createPart(HolderLookup.Provider provider, ResourceKey<BodyPart> partKey) {
        return provider.lookupOrThrow(SyntheticsData.BODY_PARTS).get(partKey).orElseThrow().value().createDefaultItemStack(provider);
    }

    public static Ingredient partIngredient(HolderLookup.Provider provider, ResourceKey<BodyPart> partKey) {
        DataComponentMap map = DataComponentMap.builder().set(SyntheticsDataComponents.BODY_PART, provider.lookupOrThrow(SyntheticsData.BODY_PARTS).getOrThrow(partKey)).build();
        return DataComponentIngredient.of(false, map, SyntheticsItems.BODY_PART_INSTALLABLE.get());
    }

    public static Ingredient augmentIngredient(HolderLookup.Provider provider, ResourceKey<Augment> partKey) {
        DataComponentMap map = DataComponentMap.builder().set(SyntheticsDataComponents.AUGMENT, provider.lookupOrThrow(SyntheticsData.AUGMENTS).getOrThrow(partKey)).build();
        return DataComponentIngredient.of(false, map, SyntheticsItems.AUGMENT_INSTALLABLE.get());
    }

    private void createPairRecipes(RecipeOutput output, HolderLookup.@NotNull Provider lookup) {
        createBodyPartPairRecipe(output, lookup, BodyParts.ORGANIC_RIGHT_FOOT, BodyParts.ORGANIC_LEFT_FOOT);
        createBodyPartPairRecipe(output, lookup, BodyParts.CYBERNETIC_RIGHT_FOOT, BodyParts.CYBERNETIC_LEFT_FOOT);
        createBodyPartPairRecipe(output, lookup, BodyParts.ORGANIC_RIGHT_HAND, BodyParts.ORGANIC_LEFT_HAND);
        createBodyPartPairRecipe(output, lookup, BodyParts.CYBERNETIC_RIGHT_HAND, BodyParts.CYBERNETIC_LEFT_HAND);
        createBodyPartPairRecipe(output, lookup, BodyParts.ORGANIC_RIGHT_EYE, BodyParts.ORGANIC_LEFT_EYE);
        createBodyPartPairRecipe(output, lookup, BodyParts.MECHANICAL_RIGHT_HAND, BodyParts.MECHANICAL_LEFT_HAND);
        createBodyPartPairRecipe(output, lookup, BodyParts.CYBERNETIC_RIGHT_EYE, BodyParts.CYBERNETIC_LEFT_EYE);

    }

    private void createBodyPartPairRecipe(RecipeOutput output, HolderLookup.Provider lookup, ResourceKey<BodyPart> right, ResourceKey<BodyPart> left) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, createPart(lookup, right))
                .requires(partIngredient(lookup, left))
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("pair_swap/" + right.location().getPath()));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, createPart(lookup, left))
                .requires(partIngredient(lookup, right))
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("pair_swap/" + left.location().getPath()));
    }


    private void createShapedRecipes(RecipeOutput output) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SyntheticsItems.BLUEPRINT.get())
                .requires(Items.PAPER)
                .requires(Items.BLUE_DYE, 2)
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).save(output, Synthetics.rl("blueprint"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SyntheticsItems.ANCIENT_ALLOY.get())
                .requires(Tags.Items.INGOTS_IRON)
                .requires(Items.REDSTONE)
                .requires(SyntheticsItems.ANCIENT_SCRAP.get(), 2)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, Synthetics.rl("ancient_alloy"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SyntheticsItems.PURE_ANCIENT_ALLOY.get(), 2)
                .requires(Tags.Items.INGOTS_IRON)
                .requires(SyntheticsItems.FOSSILISED_SCRAP.get())
                .requires(SyntheticsItems.ANCIENT_SCRAP.get(), 3)
                .unlockedBy("has_iron", has(Items.IRON_INGOT)).save(output, Synthetics.rl("pure_ancient_alloy"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.REDSTONE_LINKABLE_BLOCK.get())
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Blocks.REDSTONE_BLOCK)
                .define('C', SyntheticsItems.BASIC_CIRCUIT.get())
                .pattern(" C ")
                .pattern("ABA")
                .pattern(" A ")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("redstone_linkable_block")
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.CAMERA_LINKABLE_BLOCK.get())
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.GLASS_PANES)
                .define('C', SyntheticsItems.BASIC_CIRCUIT.get())
                .define('D', Items.IRON_BLOCK)
                .pattern(" CB")
                .pattern("ABA")
                .pattern(" D ")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("camera_linkable_block")
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.RESEARCH_TABLE.get())
                .define('A', Tags.Items.STRIPPED_LOGS)
                .define('B', Items.POLISHED_DIORITE)
                .define('C', Items.BLUE_DYE)
                .define('D', Items.PAPER)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("A A")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("synthetic_research_table")
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.AUGMENTATION_CHAMBER.get())
                .define('A', Items.IRON_BLOCK)
                .define('B', Tags.Items.GLASS_BLOCKS)
                .define('C', Items.POLISHED_DIORITE)
                .define('D', Items.COPPER_BLOCK)
                .define('E', Items.REDSTONE)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("EAE")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("augmentation_chamber")
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.SYNTHETIC_FORGE.get())
                .define('A', Tags.Items.STRIPPED_LOGS)
                .define('B', Tags.Items.INGOTS_IRON)
                .define('C', Blocks.BLAST_FURNACE)
                .pattern(" C ")
                .pattern("BBB")
                .pattern("A A")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("synthetic_forge")
                );
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsItems.BASIC_CIRCUIT.get())
                .define('A', Tags.Items.INGOTS_COPPER)
                .define('B', Tags.Items.DYES_GREEN)
                .define('C', Items.REDSTONE)
                .pattern("CCC")
                .pattern("ABA")
                .pattern("CCC")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("basic_circuit")
                );

    }

}
