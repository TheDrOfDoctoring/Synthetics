package com.thedrofdoctoring.synthetics.core.data.collections.tags;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public class SyntheticsItemTags {

    private static @NotNull TagKey<Item> create(@NotNull String name) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), Synthetics.rl(name));
    }
    private static @NotNull TagKey<Item> create(@NotNull String namespace, @NotNull String path) {
        return TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static final TagKey<Item> IRON_GEARS = create("c", "gears/iron");

}
