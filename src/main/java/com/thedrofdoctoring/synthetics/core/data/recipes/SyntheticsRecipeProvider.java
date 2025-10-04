package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.data.gen.SyntheticsLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SyntheticsRecipeProvider extends RecipeProvider {

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new SyntheticsRecipeProvider(output, future));
    }

    public SyntheticsRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }
    @Override
    protected void buildRecipes(@NotNull RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SyntheticsBlocks.RESEARCH_TABLE.get())
                .define('A', Tags.Items.STRIPPED_LOGS)
                .define('B', Items.POLISHED_DIORITE)
                .define('C', Items.BLUE_DYE)
                .define('D', Items.PAPER)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("A A")
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(pRecipeOutput, Synthetics.rl("synthetic_research_table")
                );
    }
}
