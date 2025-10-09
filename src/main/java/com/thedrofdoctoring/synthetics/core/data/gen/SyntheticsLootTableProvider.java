package com.thedrofdoctoring.synthetics.core.data.gen;

import com.google.common.collect.Sets;
import com.thedrofdoctoring.synthetics.blocks.AugmentationChamber;
import com.thedrofdoctoring.synthetics.blocks.SyntheticResearchTable;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SyntheticsLootTableProvider {

    private static final Set<ResourceKey<LootTable>> LOOT_TABLES = Sets.newHashSet();

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(
                SyntheticsBlockLootTables::new,
                LootContextParamSets.BLOCK
        )), future
        ));
    }

    private static class SyntheticsBlockLootTables extends BlockLootSubProvider {

    protected SyntheticsBlockLootTables(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
    }

    @Override
    protected void generate() {
        this.add(SyntheticsBlocks.RESEARCH_TABLE.get(), block -> createSinglePropConditionTable(block, SyntheticResearchTable.PART, SyntheticResearchTable.TablePart.HEAD));
        this.add(SyntheticsBlocks.AUGMENTATION_CHAMBER.get(), block -> createSinglePropConditionTable(block, AugmentationChamber.PART, AugmentationChamber.Part.BOTTOM));
        this.dropSelf(SyntheticsBlocks.ORGAN_SKULL.get());
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return SyntheticsBlocks.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

}
}
