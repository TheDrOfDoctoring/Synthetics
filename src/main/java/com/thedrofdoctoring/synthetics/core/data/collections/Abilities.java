package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.resources.ResourceKey;

public class Abilities {

    public static final ResourceKey<Ability> INERTIAL_DAMPENERS_FALL_DAMAGE = create("inertial_dampener_fall_damage");
    public static final ResourceKey<Ability> INERTIAL_DAMPENERS_SAFE_FALL = create("inertial_dampener_safe_fall");
    public static final ResourceKey<Ability> MECHANICAL_DAMPENERS_FALL_DAMAGE = create("mechanical_dampener_fall_damage");
    public static final ResourceKey<Ability> MECHANICAL_DAMPENERS_SAFE_FALL = create("mechanical_dampener_safe_fall");
    public static final ResourceKey<Ability> BASIC_DAMPENERS_FALL_DAMAGE = create("basic_dampener_fall_damage");
    public static final ResourceKey<Ability> BASIC_DAMPENERS_SAFE_FALL = create("basic_dampener_safe_fall");
    public static final ResourceKey<Ability> LAUNCHBOOT_LAUNCH = create("launchboot_launch");
    public static final ResourceKey<Ability> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<Ability> TISSUE_SOLAR_POWER = create("tissue_solar");
    public static final ResourceKey<Ability> ADVANCED_TISSUE_SOLAR_POWER = create("advanced_tissue_solar");
    public static final ResourceKey<Ability> RESPIRATOR_BREATH = create("respirator_breath");
    public static final ResourceKey<Ability> VISION_CLARIFIER_VIEW = create("vision_clarifier");
    public static final ResourceKey<Ability> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<Ability> BASIC_WALL_CLIMB = create("basic_wall_climb");
    public static final ResourceKey<Ability> INTEGRATED_EXOSKELETON_WALK = create("integrated_exoskeleton_walk_speed");
    public static final ResourceKey<Ability> INTEGRATED_EXOSKELETON_SWIM = create("integrated_exoskeleton_swim_speed");
    public static final ResourceKey<Ability> INTERNAL_PLATING_KNOCKBACK = create("internal_plating_knockback");
    public static final ResourceKey<Ability> INTERNAL_PLATING_ARMOUR_TOUGHNESS = create("internal_plating_armour_toughness");
    public static final ResourceKey<Ability> CYBERNETIC_HAND_DAMAGE = create("cybernetic_hand_damage");
    public static final ResourceKey<Ability> EXTEND_GRIP_BLOCK_REACH = create("extend_grip_block_reach");
    public static final ResourceKey<Ability> AUTOPILOT_STEP_ASSIST = create("autopilot_step_assist");
    public static final ResourceKey<Ability> UNIVERSAL_TRANSLATOR_TRADE_PRICES = create("universal_translator_trade_prices");
    public static final ResourceKey<Ability> HAND_FLAMETHROWER = create("hand_flamethrower");
    public static final ResourceKey<Ability> MECHANICAL_HAND_ATTACK_SPEED = create("mechanical_hand_attack_speed");
    public static final ResourceKey<Ability> CYBERNETIC_EYE_VIEW = create("cybernetic_eye_view");
    public static final ResourceKey<Ability> CYBERNETIC_REDSTONE_LINK = create("cybernetic_redstone_link");
    public static final ResourceKey<Ability> CYBERNETIC_CAMERA_LINK = create("cybernetic_camera_link");
    public static final ResourceKey<Ability> HAND_REPULSOR = create("hand_repulsor");
    public static final ResourceKey<Ability> REMOTE_LINKED_INTERACTION = create("remote_linked_interaction");
    public static final ResourceKey<Ability> VIEWLINK_BLINDNESS_IMMUNITY = create("viewlink_blindness_immunity");
    public static final ResourceKey<Ability> REINFORCED_TENDONS_MINING = create("reinforced_tendons_mining");
    public static final ResourceKey<Ability> SYNTHETIC_LINING_EXHAUSTION = create("synthetic_lining_exhaustion");
    public static final ResourceKey<Ability> BLOOD_TANK_HEALTH = create("blood_tank_health");
    public static final ResourceKey<Ability> NEURAL_CHAMBER_EXPERIENCE = create("neural_chamber_experience");
    public static final ResourceKey<Ability> SHIMMER_INVISIBILITY = create("shimmer_invisibility");
    public static final ResourceKey<Ability> HEALING_TISSUE = create("healing_tissue");
    public static final ResourceKey<Ability> FIRE_RESISTANT_TISSUE = create("fire_resistant_tissue");
    public static final ResourceKey<Ability> ADRENALINE_INJECTOR = create("adrenaline_injector");
    public static final ResourceKey<Ability> CHEST_HARPOON = create("chest_harpoon");
    public static final ResourceKey<Ability> PERMEATOR = create("permeator");
    public static final ResourceKey<Ability> CLOSE_PERMEATOR = create("close_permeated");
    public static final ResourceKey<Ability> LINKED_TELEPORT = create("linked_teleport");
    public static final ResourceKey<Ability> ORE_DOWSING = create("ore_dowsing");
    public static final ResourceKey<Ability> NIGHT_VISION = create("night_vision");


    private static ResourceKey<Ability> create(String name) {
        return ResourceKey.create(
                SyntheticsData.ABILITIES,
                Synthetics.rl(name)
        );
    }
}
