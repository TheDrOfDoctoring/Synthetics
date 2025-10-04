package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.resources.ResourceKey;

public class ResearchNodes {

    public static final ResourceKey<ResearchNode> INERTIAL_DAMPENERS = create("inertial_dampeners_research");
    public static final ResourceKey<ResearchNode> LAUNCH_BOOTS = create("launch_boot_research");



    private static ResourceKey<ResearchNode> create(String name) {
        return ResourceKey.create(
                SyntheticsData.RESEARCH_NODES,
                Synthetics.rl(name)
        );
    }
}
