package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import net.minecraft.resources.ResourceKey;

public class ResearchNodes {

    public static final ResourceKey<ResearchNode> INERTIAL_DAMPENERS = create("inertial_dampeners");
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
    public static final ResourceKey<ResearchNode> STOMACH_AUGMENTS = create("stomach_augments");
    public static final ResourceKey<ResearchNode> HAND_WALL_CLIMB = create("hand_wall_climb");
    public static final ResourceKey<ResearchNode> INTEGRATED_EXOSKELETON = create("integrated_exoskeleton");
    public static final ResourceKey<ResearchNode> INTERNAL_PLATING = create("internal_plating");
    public static final ResourceKey<ResearchNode> ORGANIC_HANDS = create("organic_hands");
    public static final ResourceKey<ResearchNode> CYBERNETIC_HANDS = create("cybernetic_hands");
    public static final ResourceKey<ResearchNode> EXTENDED_GRIP = create("extended_grip");
    public static final ResourceKey<ResearchNode> ORGANIC_BONES = create("organic_bones");
    public static final ResourceKey<ResearchNode> AUTOPILOT = create("motion_autopilot");

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
