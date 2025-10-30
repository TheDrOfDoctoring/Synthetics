package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber.AugmentationChamberScreen;
import com.thedrofdoctoring.synthetics.client.screens.research.ResearchScreen;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SyntheticsClientManager {

    public int selectedAbility = 0;
    public boolean displayAbilities = true;
    public boolean displayEnergy = true;

    public List<ResearchNode> allResearch = new ArrayList<>();
    public Map<ResearchTab, List<ResearchNode>> rootNodes = new HashMap<>();
    public List<BodyPartType> partTypes = new ArrayList<>();

    public Map<ResearchNode, List<ResearchNode>> parentToChildrenMap;

    public void setResearch(List<ResearchNode> allNodes) {
        this.allResearch = new ArrayList<>(allNodes);
        if(parentToChildrenMap != null) {
            parentToChildrenMap.clear();
        } else {
            parentToChildrenMap = new Object2ObjectOpenHashMap<>();
        }
        rootNodes = allNodes.stream()
                            .filter(node -> node.parent().isEmpty())
                            .collect(Collectors.groupingBy(node -> node.tab().value()));

        parentToChildrenMap = allNodes.stream()
                                      .filter(node -> node.parent().isPresent())
                                      .collect(Collectors.groupingBy(
                                              node -> node.parent().get().value())
                                      );
    }

    public static void updateScreen() {
        if(Minecraft.getInstance().screen instanceof AugmentationChamberScreen screen) {
            screen.update();
        }
    }
    public static void setResearchScreen() {
        Minecraft.getInstance().setScreen(new ResearchScreen());
    }

    public void updateResearch(HolderLookup.RegistryLookup<ResearchNode> nodes) {
        this.setResearch(nodes.listElements().map(Holder.Reference::value).toList());
    }

    public void updatePartTypes(HolderLookup.RegistryLookup<BodyPartType> partTypes) {
        this.partTypes = (partTypes.listElements().map(Holder.Reference::value).toList());
    }

}
