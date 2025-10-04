package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class Augments {

    public static final ResourceKey<SyntheticAugment> CYBERNETIC_INERTIAL_DAMPENERS = create("inertial_dampeners");
    public static final ResourceKey<SyntheticAugment> LAUNCH_BOOT = create("launch_boot");

    private static ResourceKey<SyntheticAugment> create(String name) {
        return ResourceKey.create(
                SyntheticsData.AUGMENTS,
                Synthetics.rl(name)
        );
    }
    private static @NotNull TagKey<SyntheticAugment> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.AUGMENTS, Synthetics.rl(name));
    }
}
