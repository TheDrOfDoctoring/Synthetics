package com.thedrofdoctoring.synthetics.core.data.collections.tags;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class AugmentTags {

    public static final TagKey<Augment> DAMPENERS = create("dampeners");

    private static @NotNull TagKey<Augment> create(@NotNull String name) {
        return TagKey.create(SyntheticsData.AUGMENTS, Synthetics.rl(name));
    }
    private static @NotNull TagKey<Augment> create(@NotNull String namespace, @NotNull String path) {
        return TagKey.create(SyntheticsData.AUGMENTS, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

}
