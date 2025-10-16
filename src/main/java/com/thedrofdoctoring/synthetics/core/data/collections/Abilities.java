package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class Abilities {

    public static final ResourceKey<SyntheticAbility> INERTIAL_DAMPENERS_FALL_DAMAGE = create("inertial_dampener_fall_damage");
    public static final ResourceKey<SyntheticAbility> INERTIAL_DAMPENERS_SAFE_FALL = create("inertial_dampener_safe_fall");
    public static final ResourceKey<SyntheticAbility> LAUNCHBOOT_LAUNCH = create("launchboot_launch");
    public static final ResourceKey<SyntheticAbility> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<SyntheticAbility> TISSUE_SOLAR_POWER = create("tissue_solar");
    public static final ResourceKey<SyntheticAbility> ADVANCED_TISSUE_SOLAR_POWER = create("advanced_tissue_solar");
    public static final ResourceKey<SyntheticAbility> RESPIRATOR_BREATH = create("respirator_breath");
    public static final ResourceKey<SyntheticAbility> VISION_CLARIFIER_VIEW = create("vision_clarifier");
    public static final ResourceKey<SyntheticAbility> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<SyntheticAbility> HAND_WALL_CLIMB = create("hand_wall_climb");
    public static final ResourceKey<SyntheticAbility> INTEGRATED_EXOSKELETON_WALK = create("integrated_exoskeleton_walk_speed");
    public static final ResourceKey<SyntheticAbility> INTEGRATED_EXOSKELETON_SWIM = create("integrated_exoskeleton_swim_speed");
    public static final ResourceKey<SyntheticAbility> INTERNAL_PLATING_KNOCKBACK = create("internal_plating_knockback");
    public static final ResourceKey<SyntheticAbility> INTERNAL_PLATING_ARMOUR_TOUGHNESS = create("internal_plating_armour_toughness");
    public static final ResourceKey<SyntheticAbility> CYBERNETIC_HAND_DAMAGE = create("cybernetic_hand_damage");
    public static final ResourceKey<SyntheticAbility> EXTEND_GRIP_BLOCK_REACH = create("extend_grip_block_reach");


    private static ResourceKey<SyntheticAbility> create(String name) {
        return ResourceKey.create(
                SyntheticsData.ABILITIES,
                Synthetics.rl(name)
        );
    }
    private static @NotNull TagKey<SyntheticAbility> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.ABILITIES, Synthetics.rl(name));
    }
}
