package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public class SyntheticsClientManager {

    public int selectedAbility = 0;

    public List<ResearchNode> allResearch = new ArrayList<>();
    public List<ResearchNode> rootNodes = new ArrayList<>();

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

}
