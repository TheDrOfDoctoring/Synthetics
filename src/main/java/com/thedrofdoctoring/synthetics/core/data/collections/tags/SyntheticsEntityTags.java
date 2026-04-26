package com.thedrofdoctoring.synthetics.core.data.collections.tags;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class SyntheticsEntityTags {

    private static @NotNull TagKey<EntityType<?>> create(@NotNull String name) {
        return TagKey.create(Registries.ENTITY_TYPE, Synthetics.rl(name));
    }
    private static @NotNull TagKey<EntityType<?>> create(@NotNull String namespace, @NotNull String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static final TagKey<EntityType<?>> COMMON_ENTITY_HIGHLIGHT_BLACKLIST = create("common_entity_highlighter_blacklist");

}
