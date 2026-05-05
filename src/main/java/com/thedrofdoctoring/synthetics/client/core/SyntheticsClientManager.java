package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.client.screens.LinkedInteractScreen;
import com.thedrofdoctoring.synthetics.client.screens.menu_screens.augmentation_chamber.AugmentationChamberScreen;
import com.thedrofdoctoring.synthetics.client.screens.research.ResearchScreen;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchTab;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SyntheticsClientManager  {

    public Map<ResearchTab, List<ResearchNode>> rootNodes = new HashMap<>();
    public List<BodyPartType> partTypes = new ArrayList<>();
    public Map<ResearchNode, List<ResearchNode>> parentToChildrenMap;

    private final Set<ResourceLocation> installablesWithTextures = new HashSet<>();

    public void setResearch(List<ResearchNode> allNodes) {
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

    private static void updateScreen() {
        if(Minecraft.getInstance().screen instanceof AugmentationChamberScreen screen) {
            screen.update();
        }
    }

    public static void onUpdate() {
        updateScreen();
    }
    public static void setResearchScreen() {
        Minecraft.getInstance().setScreen(new ResearchScreen());
    }
    public static void setLinkableScreen() {
        Minecraft.getInstance().setScreen(new LinkedInteractScreen());
    }



    private void updateResearch(HolderLookup.RegistryLookup<ResearchNode> nodes) {
        this.setResearch(nodes.listElements()
                .map(Holder.Reference::value)
                .filter(node -> !node.hidden())
                .toList());
    }

    private void updatePartTypes(HolderLookup.RegistryLookup<BodyPartType> partTypes) {
        this.partTypes = (partTypes.listElements().map(Holder.Reference::value).toList());
    }

    public void onReload(@NotNull ResourceManager resourceManager, RegistryAccess registryAccess) {
        installablesWithTextures.clear();
        addInstallableTextures(SyntheticsData.AUGMENTS, registryAccess, resourceManager);
        addInstallableTextures(SyntheticsData.BODY_PARTS, registryAccess, resourceManager);
        addInstallableTextures(SyntheticsData.BODY_SEGMENTS, registryAccess, resourceManager);
        updatePartTypes(registryAccess.lookupOrThrow(SyntheticsData.BODY_PART_TYPES));
        updateResearch(registryAccess.lookupOrThrow(SyntheticsData.RESEARCH_NODES));
    }

    public boolean canDrawInstallable(ResourceLocation installableLayerTextureLocation) {
        return installablesWithTextures.contains(installableLayerTextureLocation);
    }

    private <T extends IBodyInstallable<T>> void addInstallableTextures(ResourceKey<Registry<T>> key, RegistryAccess registryAccess, ResourceManager resourceManager) {
        List<ResourceLocation> locations = registryAccess.lookup(key).map(lookup ->
                lookup.listElements()
                        .map(ref -> ref.value().entityLayerTexture(true))
                        .filter(location -> resourceManager.getResource(location).isPresent())
                        .toList()
        ).orElse(Collections.emptyList());
        installablesWithTextures.addAll(locations);
    }
}
