package com.thedrofdoctoring.synthetics.core.data.collections.tags;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class SyntheticsBlockTags {

    private static @NotNull TagKey<Block> create(@NotNull String name) {
        return TagKey.create(BuiltInRegistries.BLOCK.key(), Synthetics.rl(name));
    }
    private static @NotNull TagKey<Block> create(@NotNull String namespace, @NotNull String path) {
        return TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static final TagKey<Block> PERMEATOR_BLACKLIST = create("standard_permeator_blacklist");
    public static final TagKey<Block> ORE_DOWSING = create("ore_dowsing");

}
