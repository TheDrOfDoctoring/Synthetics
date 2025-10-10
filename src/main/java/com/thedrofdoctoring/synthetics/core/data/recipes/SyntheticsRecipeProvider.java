package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.ResearchNodes;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
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
        SyntheticForgeRecipeBuilder.create(createAugmentResult(lookup, Augments.CYBERNETIC_INERTIAL_DAMPENERS), 1)
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
                .group("re")
                .save(output, Synthetics.rl("inertial_dampeners")
                );
        SyntheticForgeRecipeBuilder.create(createAugmentResult(lookup, Augments.LAUNCH_BOOT), 1)
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
                .group("re")
                .save(output, Synthetics.rl("launch_boots")
                );
    }


    private ItemStack createAugmentResult(HolderLookup.Provider provider, ResourceKey<SyntheticAugment> augmentKey) {
        return provider.lookupOrThrow(SyntheticsData.AUGMENTS).get(augmentKey).orElseThrow().value().createDefaultItemStack(provider);
    }

    private void createShapedRecipes(RecipeOutput output) {

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
