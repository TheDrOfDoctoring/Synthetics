package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
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

    }

    private void createForgeRecipes(RecipeOutput output, HolderLookup.@NotNull Provider lookup) {
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
                .define('A', Items.REDSTONE)
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
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.MEDIUM_BATTERY.get() , 1)
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
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.ARTIFICIAL_CAPILLARY.get() , 1)
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
        SyntheticForgeRecipeBuilder.create(SyntheticsItems.ARTIFICIAL_TISSUE.get() , 2)
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
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_FEET) , 1)
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
                .save(output, Synthetics.rl("organic_feet")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_FEET) , 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_FEET))
                .define('B', Items.IRON_INGOT)
                .define('C', Items.GOLD_INGOT)
                .pattern("ABB")
                .pattern("C  ")
                .pattern("   ")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_FEET)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_feet")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.SOLAR_TISSUE) , 1)
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
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_TISSUE) , 1)
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
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_TISSUE) , 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_TISSUE))
                .define('B', Items.IRON_INGOT)
                .define('C', Items.GOLD_INGOT)
                .define('D', Items.COPPER_INGOT)
                .pattern("ABB")
                .pattern("CCD")
                .pattern("   ")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.ARTIFICIAL_SKIN)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_tissue")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.ADVANCED_SOLAR_TISSUE) , 1)
                .define('D', augmentIngredient(lookup, Augments.SOLAR_TISSUE))
                .define('A', Items.DIAMOND)
                .define('B', Items.LAPIS_LAZULI)
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
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTEGRATED_RESPIRATOR) , 1)
                .define('A', Items.COPPER_INGOT)
                .define('B', Items.LAPIS_LAZULI)
                .pattern("AAA")
                .pattern("B B")
                .pattern("AAA")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.FLUID_AUGMENTS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("integrated_respirator")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.VISION_CLARIFIER) , 1)
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
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.METABOLIC_CONVERTER) , 1)
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
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.EMITTABLE_ADHESIVE) , 1)
                .define('A', Items.SPIDER_EYE)
                .define('B', Items.SLIME_BALL)
                .define('C', Tags.Items.INGOTS_IRON)
                .pattern("A A")
                .pattern("BCB")
                .pattern("A A")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.HAND_WALL_CLIMB)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("emittable_adhesive")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTEGRATED_EXOSKELETON) , 1)
                .define('A', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.GEMS_DIAMOND)
                .define('C', Items.BONE)
                .pattern("  C")
                .pattern(" B ")
                .pattern("AA ")
                .lavaCost(50)
                .recipeTime(100)
                .requiredResearch(lookup, ResearchNodes.INTEGRATED_EXOSKELETON)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("integrated_exoskeleton")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.INTERNAL_PLATING) , 1)
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
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.ORGANIC_HANDS) , 1)
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
                .save(output, Synthetics.rl("organic_hands")
                );
        SyntheticForgeRecipeBuilder.create(createPart(lookup, BodyParts.CYBERNETIC_HANDS) , 1)
                .define('A', partIngredient(lookup, BodyParts.ORGANIC_HANDS))
                .define('B', Items.IRON_INGOT)
                .define('C', Items.GOLD_INGOT)
                .pattern(" BB")
                .pattern(" A ")
                .pattern("BBC")
                .lavaCost(50)
                .recipeTime(150)
                .requiredResearch(lookup, ResearchNodes.CYBERNETIC_HANDS)
                .unlockedBy("has_capillary", has(SyntheticsItems.ARTIFICIAL_CAPILLARY.get()))
                .save(output, Synthetics.rl("cybernetic_hands")
                );
        SyntheticForgeRecipeBuilder.create(createAugment(lookup, Augments.EXTENDED_GRIP) , 1)
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
    }


    private ItemStack createAugment(HolderLookup.Provider provider, ResourceKey<SyntheticAugment> augmentKey) {
        return provider.lookupOrThrow(SyntheticsData.AUGMENTS).get(augmentKey).orElseThrow().value().createDefaultItemStack(provider);
    }
    private ItemStack createPart(HolderLookup.Provider provider, ResourceKey<BodyPart> partKey) {
        return provider.lookupOrThrow(SyntheticsData.BODY_PARTS).get(partKey).orElseThrow().value().createDefaultItemStack(provider);
    }
    public static Ingredient partIngredient(HolderLookup.Provider provider, ResourceKey<BodyPart> partKey) {
        DataComponentMap map = DataComponentMap.builder().set(SyntheticsDataComponents.BODY_PART,  provider.lookupOrThrow(SyntheticsData.BODY_PARTS).getOrThrow(partKey)).build();
        return DataComponentIngredient.of(false, map, SyntheticsItems.BODY_PART_INSTALLABLE.get());
    }

    public static Ingredient augmentIngredient(HolderLookup.Provider provider, ResourceKey<SyntheticAugment> partKey) {
        DataComponentMap map = DataComponentMap.builder().set(SyntheticsDataComponents.AUGMENT,  provider.lookupOrThrow(SyntheticsData.AUGMENTS).getOrThrow(partKey)).build();
        return DataComponentIngredient.of(false, map, SyntheticsItems.AUGMENT_INSTALLABLE.get());
    }

    private void createShapedRecipes(RecipeOutput output) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SyntheticsItems.BLUEPRINT.get())
                .requires(Items.PAPER)
                .requires(Items.BLUE_DYE, 2)
                .requires(Items.WHITE_DYE)
                .unlockedBy("has_blue_dye", has(Items.BLUE_DYE)).save(output, Synthetics.rl("blueprint"));

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
                .define('A', Items.IRON_BLOCK)
                .define('B', Tags.Items.GLASS_BLOCKS)
                .define('C', Items.POLISHED_DIORITE)
                .define('D', Items.COPPER_BLOCK)
                .define('E', Items.REDSTONE)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("EAE")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output, Synthetics.rl("synthetic_forge")
                );

    }
}
