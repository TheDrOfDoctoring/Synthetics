package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import net.minecraft.resources.ResourceKey;

public class ResearchNodes {

    public static final ResourceKey<ResearchNode> INERTIAL_DAMPENERS = create("inertial_dampeners");
    public static final ResourceKey<ResearchNode> MECHANICAL_DAMPENERS = create("mechanical_dampeners");
    public static final ResourceKey<ResearchNode> BASIC_DAMPENERS = create("basic_dampeners");

    public static final ResourceKey<ResearchNode> LAUNCH_BOOTS = create("launch_boot");
    public static final ResourceKey<ResearchNode> HEART_BATTERY = create("heart_battery");
    public static final ResourceKey<ResearchNode> ARTIFICIAL_NEURONS = create("artificial_neurons");
    public static final ResourceKey<ResearchNode> ARTIFICIAL_CAPILLARIES = create("artificial_capillaries");
    public static final ResourceKey<ResearchNode> ARTIFICIAL_TISSUE = create("artificial_tissue");
    public static final ResourceKey<ResearchNode> ORGANIC_FEET = create("organic_feet");
    public static final ResourceKey<ResearchNode> CYBERNETIC_FEET = create("cybernetic_feet");
    public static final ResourceKey<ResearchNode> SOLAR_TISSUE = create("solar_tissue");
    public static final ResourceKey<ResearchNode> ADVANCED_SOLAR_TISSUE = create("advanced_solar_tissue");
    public static final ResourceKey<ResearchNode> ARTIFICIAL_SKIN = create("artificial_skin");
    public static final ResourceKey<ResearchNode> FLUID_AUGMENTS = create("fluid_augments");
    public static final ResourceKey<ResearchNode> FEET_WALL_CLIMB = create("feet_wall_climb");
    public static final ResourceKey<ResearchNode> INTEGRATED_EXOSKELETON = create("integrated_exoskeleton");
    public static final ResourceKey<ResearchNode> INTERNAL_PLATING = create("internal_plating");
    public static final ResourceKey<ResearchNode> ORGANIC_HANDS = create("organic_hands");
    public static final ResourceKey<ResearchNode> CYBERNETIC_HANDS = create("cybernetic_hands");
    public static final ResourceKey<ResearchNode> EXTENDED_GRIP = create("extended_grip");
    public static final ResourceKey<ResearchNode> ORGANIC_BONES = create("organic_bones");
    public static final ResourceKey<ResearchNode> AUTOPILOT = create("motion_autopilot");
    public static final ResourceKey<ResearchNode> UNIVERSAL_TRANSLATOR = create("universal_translator");
    public static final ResourceKey<ResearchNode> HAND_FLAMETHROWER = create("hand_flamethrower");
    public static final ResourceKey<ResearchNode> MECHANICAL_PARTS = create("mechanical_parts");
    public static final ResourceKey<ResearchNode> ORGANIC_EYES = create("organic_eyes");
    public static final ResourceKey<ResearchNode> CYBERNETIC_EYES = create("cybernetic_eyes");
    public static final ResourceKey<ResearchNode> INTEGRATED_REDSTONE_LINK = create("integrated_redstone_link");
    public static final ResourceKey<ResearchNode> INTERNAL_CAMERA_LINK = create("internal_camera_link");
    public static final ResourceKey<ResearchNode> HAND_REPULSOR = create("hand_repulsor");
    public static final ResourceKey<ResearchNode> ORGANIC_BRAIN = create("organic_brain");
    public static final ResourceKey<ResearchNode> CYBERNETIC_BRAIN = create("cybernetic_brain");
    public static final ResourceKey<ResearchNode> AUXILIARY_VIEWLINK = create("auxiliary_viewlink");
    public static final ResourceKey<ResearchNode> REINFORCED_TENDONS = create("reinforced_tendons");
    public static final ResourceKey<ResearchNode> BLOOD_TANK = create("blood_tank");
    public static final ResourceKey<ResearchNode> MECHANICAL_HEART = create("mechanical_heart");
    public static final ResourceKey<ResearchNode> CYBERNETIC_HEART = create("cybernetic_heart");
    public static final ResourceKey<ResearchNode> NEURAL_CHAMBER = create("neural_chamber");
    public static final ResourceKey<ResearchNode> TISSUE_AUGMENTS = create("tissue_augments");
    public static final ResourceKey<ResearchNode> FIRE_RESISTANT_TISSUE = create("fire_resistant_tissue");
    public static final ResourceKey<ResearchNode> ADRENALINE_INJECTOR = create("adrenaline_injector");
    public static final ResourceKey<ResearchNode> CHEST_HARPOON = create("chest_harpoon");
    public static final ResourceKey<ResearchNode> PERMEATOR = create("permeator");
    public static final ResourceKey<ResearchNode> LINKED_TELEPORTER = create("linked_teleport");
    public static final ResourceKey<ResearchNode> ORE_DOWSING = create("ore_dowsing");
    public static final ResourceKey<ResearchNode> NIGHT_VISION = create("night_vision");
    public static final ResourceKey<ResearchNode> TELEPORT = create("teleport");
    public static final ResourceKey<ResearchNode> DRONE_LINK = create("drone_link");
    public static final ResourceKey<ResearchNode> PERMEABLE_LINK = create("permeable_link");
    public static final ResourceKey<ResearchNode> SHRINKIFIER = create("shrinkifier");
    public static final ResourceKey<ResearchNode> RIBCAGES = create("ribcages");
    public static final ResourceKey<ResearchNode> SHOCK_ABSORBER = create("shock_absorber");
    public static final ResourceKey<ResearchNode> CAMOUFLAGE_INVISIBILITY = create("camouflage_invisibility");
    public static final ResourceKey<ResearchNode> POSITION_LOCK = create("position_lock");
    public static final ResourceKey<ResearchNode> ENTITY_DETECTOR = create("entity_detector");
    public static final ResourceKey<ResearchNode> ROCKET_BOOTS = create("rocket_boots");
    public static final ResourceKey<ResearchNode> MECHANICAL_BRUSH = create("mechanical_brush");
    public static final ResourceKey<ResearchNode> WATERWALKING_BOOT = create("waterwalking_boot");
    public static final ResourceKey<ResearchNode> ITEM_MAGNET = create("item_magnet");
    public static final ResourceKey<ResearchNode> BLASTING_ARM = create("blasting_arm");
    public static final ResourceKey<ResearchNode> SHOCK_GAUNTLET = create("shock_gauntlet");
    public static final ResourceKey<ResearchNode> SYNTHETIC_LINING = create("synthetic_lining");
    public static final ResourceKey<ResearchNode> METABOLIC_CONVERTER = create("metabolic_converter");
    public static final ResourceKey<ResearchNode> FAT_CONVERTER = create("fat_converter");



    public static final ResourceKey<ResearchTab> TAB_AUGMENTS = createTab("augments");
    public static final ResourceKey<ResearchTab> TAB_BODY_PARTS = createTab("body_parts");



    private static ResourceKey<ResearchNode> create(String name) {
        return ResourceKey.create(
                SyntheticsData.RESEARCH_NODES,
                Synthetics.rl(name)
        );
    }

    private static ResourceKey<ResearchTab> createTab(String name) {
        return ResourceKey.create(
                SyntheticsData.RESEARCH_TABS,
                Synthetics.rl(name)
        );
    }
}
