package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.client.screens.ResearchScreen;
import com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber.AugmentationChamberScreen;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;

import java.util.ArrayList;
import java.util.List;

public class SyntheticsClientManager {

    public int selectedAbility = 0;
    public boolean displayAbilities = true;

    public List<ResearchNode> allResearch = new ArrayList<>();
    public List<ResearchNode> rootNodes = new ArrayList<>();
    public List<BodyPartType> partTypes = new ArrayList<>();

    public Object2ObjectOpenHashMap<ResearchNode, ArrayList<ResearchNode>> parentToChildrenMap;

    public void setResearch(List<ResearchNode> nodes) {
        this.allResearch = new ArrayList<>(nodes);
        if(parentToChildrenMap != null) {
            parentToChildrenMap.clear();
        } else {
            parentToChildrenMap = new Object2ObjectOpenHashMap<>();
        }
        this.rootNodes = new ArrayList<>(nodes);
        for(ResearchNode node : nodes) {
            node.parent().ifPresent(parentNode -> {
                rootNodes.remove(node);
                ResearchNode parent = parentNode.value();
                parentToChildrenMap
                        .computeIfAbsent(parent, k -> new ArrayList<>())
                        .add(node);
            });
        }
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
