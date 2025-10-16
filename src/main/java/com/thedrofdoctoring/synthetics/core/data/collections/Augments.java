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
    public static final ResourceKey<SyntheticAugment> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<SyntheticAugment> SOLAR_TISSUE = create("solar_tissue");
    public static final ResourceKey<SyntheticAugment> ADVANCED_SOLAR_TISSUE = create("advanced_solar_tissue");
    public static final ResourceKey<SyntheticAugment> INTEGRATED_RESPIRATOR = create("integrated_respirator");
    public static final ResourceKey<SyntheticAugment> VISION_CLARIFIER = create("vision_clarifier");
    public static final ResourceKey<SyntheticAugment> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<SyntheticAugment> EMITTABLE_ADHESIVE = create("emittable_adhesive");
    public static final ResourceKey<SyntheticAugment> INTEGRATED_EXOSKELETON = create("integrated_exoskeleton");
    public static final ResourceKey<SyntheticAugment> INTERNAL_PLATING = create("internal_plating");
    public static final ResourceKey<SyntheticAugment> EXTENDED_GRIP = create("extended_grip");


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
