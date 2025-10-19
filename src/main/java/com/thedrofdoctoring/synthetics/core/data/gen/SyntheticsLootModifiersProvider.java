package com.thedrofdoctoring.synthetics.core.data.gen;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class SyntheticsLootModifiersProvider extends GlobalLootModifierProvider {

    public SyntheticsLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Synthetics.MODID);
    }
    public static void register(DataGenerator gen, GatherDataEvent event, CompletableFuture<HolderLookup.Provider> future, PackOutput output) {
        gen.addProvider(event.includeServer(), new SyntheticsLootModifiersProvider(output, future));
    }

        @Override
    protected void start() {

        this.add(
                LootTables.MODIFIED_TRIAL_CHAMBER.location().getPath(),
                new AddTableLootModifier(new LootItemCondition[] {
                        AnyOfCondition.anyOf(
                                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/reward"))
                        ).build()

                }, LootTables.MODIFIED_TRIAL_CHAMBER)
        );
        this.add(
                LootTables.MODIFIED_TRIAL_CHAMBER_OMINOUS.location().getPath(),
                new AddTableLootModifier(new LootItemCondition[] {
                        AnyOfCondition.anyOf(
                                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/reward_ominous"))
                        ).build()

                }, LootTables.MODIFIED_TRIAL_CHAMBER_OMINOUS)
        );
    }
}
