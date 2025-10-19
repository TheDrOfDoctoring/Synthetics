package com.thedrofdoctoring.synthetics.core.data.gen;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class LootTables {


    public static final ResourceKey<LootTable> MODIFIED_TRIAL_CHAMBER = create("chests/modified_trial_chamber");
    public static final ResourceKey<LootTable> MODIFIED_TRIAL_CHAMBER_OMINOUS = create("chests/modified_trial_chamber_ominous");
    static @NotNull ResourceKey<LootTable> create(@NotNull String resourceName) {
        return register(Synthetics.rl(resourceName));
    }
    static @NotNull ResourceKey<LootTable> register(@NotNull ResourceLocation resourceLocation) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, resourceLocation);
        SyntheticsLootTableProvider.LOOT_TABLES.add(key);
        return key;
    }
}
