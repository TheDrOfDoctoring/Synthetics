package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class Augments {

    public static final ResourceKey<Augment> CYBERNETIC_INERTIAL_DAMPENERS = create("inertial_dampeners");
    public static final ResourceKey<Augment> LAUNCH_BOOT = create("launch_boot");
    public static final ResourceKey<Augment> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<Augment> SOLAR_TISSUE = create("solar_tissue");
    public static final ResourceKey<Augment> ADVANCED_SOLAR_TISSUE = create("advanced_solar_tissue");
    public static final ResourceKey<Augment> INTEGRATED_RESPIRATOR = create("integrated_respirator");
    public static final ResourceKey<Augment> VISION_CLARIFIER = create("vision_clarifier");
    public static final ResourceKey<Augment> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<Augment> EMITTABLE_ADHESIVE = create("emittable_adhesive");
    public static final ResourceKey<Augment> INTEGRATED_EXOSKELETON = create("integrated_exoskeleton");
    public static final ResourceKey<Augment> INTERNAL_PLATING = create("internal_plating");
    public static final ResourceKey<Augment> EXTENDED_GRIP = create("extended_grip");
    public static final ResourceKey<Augment> MOTION_AUTOPILOT = create("motion_autopilot");


    private static ResourceKey<Augment> create(String name) {
        return ResourceKey.create(
                SyntheticsData.AUGMENTS,
                Synthetics.rl(name)
        );
    }
    @SuppressWarnings("unused")
    private static @NotNull TagKey<Augment> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.AUGMENTS, Synthetics.rl(name));
    }
}
