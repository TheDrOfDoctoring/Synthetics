package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class Augments {

    public static final ResourceKey<Augment> CYBERNETIC_INERTIAL_DAMPENERS = create("inertial_dampeners");
    public static final ResourceKey<Augment> MECHANICAL_INERTIAL_DAMPENERS = create("mechanical_inertial_dampeners");
    public static final ResourceKey<Augment> BASIC_INERTIAL_DAMPENERS = create("basic_inertial_dampeners");
    public static final ResourceKey<Augment> LAUNCH_BOOT = create("launch_boot");
    public static final ResourceKey<Augment> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<Augment> SOLAR_TISSUE = create("solar_tissue");
    public static final ResourceKey<Augment> ADVANCED_SOLAR_TISSUE = create("advanced_solar_tissue");
    public static final ResourceKey<Augment> INTEGRATED_RESPIRATOR = create("integrated_respirator");
    public static final ResourceKey<Augment> VISION_CLARIFIER = create("vision_clarifier");
    public static final ResourceKey<Augment> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<Augment> MAGNETIC_FEET_IMPLANTS = create("magnetic_feet_implants");
    public static final ResourceKey<Augment> INTEGRATED_EXOSKELETON = create("integrated_exoskeleton");
    public static final ResourceKey<Augment> INTERNAL_PLATING = create("internal_plating");
    public static final ResourceKey<Augment> EXTENDED_GRIP = create("extended_grip");
    public static final ResourceKey<Augment> MOTION_AUTOPILOT = create("motion_autopilot");
    public static final ResourceKey<Augment> UNIVERSAL_TRANSLATOR = create("universal_translator");
    public static final ResourceKey<Augment> HAND_FLAMETHROWER = create("hand_flamethrower");
    public static final ResourceKey<Augment> INTEGRATED_REDSTONE_LINK = create("integrated_redstone_link");
    public static final ResourceKey<Augment> INTERNAL_CAMERA_LINK = create("internal_camera_link");
    public static final ResourceKey<Augment> HAND_REPULSOR = create("hand_repulsor");
    public static final ResourceKey<Augment> AUXILIARY_VIEWLINK = create("auxiliary_viewlink");
    public static final ResourceKey<Augment> REINFORCED_TENDONS = create("reinforced_tendons");
    public static final ResourceKey<Augment> SYNTHETIC_LINING = create("synthetic_lining");
    public static final ResourceKey<Augment> RESERVE_BLOOD_TANK = create("blood_tank");
    public static final ResourceKey<Augment> NEURAL_CHAMBER = create("neural_chamber");
    public static final ResourceKey<Augment> SHIMMER_LAYER = create("shimmer_layer");
    public static final ResourceKey<Augment> REPAIRING_TISSUE = create("repairing_tissue");
    public static final ResourceKey<Augment> FIRE_RESISTANT_TISSUE = create("fire_resistant_tissue");



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
