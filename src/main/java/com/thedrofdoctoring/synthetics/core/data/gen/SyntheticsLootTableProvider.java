package com.thedrofdoctoring.synthetics.core.data.gen;

import com.google.common.collect.Sets;
import com.thedrofdoctoring.synthetics.blocks.AugmentationChamber;
import com.thedrofdoctoring.synthetics.blocks.SyntheticForge;
import com.thedrofdoctoring.synthetics.blocks.SyntheticResearchTable;
import com.thedrofdoctoring.synthetics.blocks.TableBlock;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class SyntheticsLootTableProvider {

    public static final Set<ResourceKey<LootTable>> LOOT_TABLES = Sets.newHashSet();

    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new LootTableProvider(output, LOOT_TABLES, List.of(
                new LootTableProvider.SubProviderEntry(SyntheticsBlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(SyntheticsChestLootTables::new, LootContextParamSets.CHEST)
        ), future
        ));
    }

    private static class SyntheticsBlockLootTables extends BlockLootSubProvider {

        protected SyntheticsBlockLootTables(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {

            this.add(SyntheticsBlocks.SYNTHETIC_FORGE.get(), block -> createSinglePropConditionTable(block, SyntheticForge.PART, TableBlock.TablePart.FOOT));
            this.add(SyntheticsBlocks.RESEARCH_TABLE.get(), block -> createSinglePropConditionTable(block, SyntheticResearchTable.PART, TableBlock.TablePart.FOOT));
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

    private record SyntheticsChestLootTables(HolderLookup.Provider registries) implements LootTableSubProvider {


        @Override
        public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> builder) {
            builder.accept(LootTables.MODIFIED_TRIAL_CHAMBER, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 3f))
                            .add(LootItem.lootTableItem(SyntheticsItems.ANCIENT_SCRAP.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 1.0F))))
                    )
            );
            builder.accept(LootTables.MODIFIED_TRIAL_CHAMBER_OMINOUS, LootTable.lootTable()
                    .withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0f, 2f))
                            .add(LootItem.lootTableItem(SyntheticsItems.ANCIENT_SCRAP.get()).setWeight(5).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 4.0F))))
                    )
            );

        }
    }

}
