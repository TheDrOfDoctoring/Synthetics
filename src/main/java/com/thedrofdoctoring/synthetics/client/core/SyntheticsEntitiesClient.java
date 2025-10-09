package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.renderers.entities.OrganDisplayMobRenderer;
import com.thedrofdoctoring.synthetics.core.SyntheticsEntities;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.model.geom.LayerDefinitions.INNER_ARMOR_DEFORMATION;
import static net.minecraft.client.model.geom.LayerDefinitions.OUTER_ARMOR_DEFORMATION;

public class SyntheticsEntitiesClient {

    public static final ModelLayerLocation ORGAN_HEAD = new ModelLayerLocation(Synthetics.rl("organ_head"), "main");
    public static final ModelLayerLocation ORGAN_MOB = new ModelLayerLocation(Synthetics.rl("organ_mod"), "main");
    public static final ModelLayerLocation ORGAN_OUTER_LAYER = new ModelLayerLocation(Synthetics.rl("organ_outer_layer"), "inner_armor");
    public static final ModelLayerLocation ORGAN_INNER_LAYER = new ModelLayerLocation(Synthetics.rl("organ_inner_layer"), "outer_armor");


    public static void registerLayers(EntityRenderersEvent.@NotNull RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ORGAN_HEAD, SkullModel::createHumanoidHeadLayer);

        event.registerLayerDefinition(ORGAN_INNER_LAYER, () -> LayerDefinition.create(HumanoidArmorModel.createBodyLayer(INNER_ARMOR_DEFORMATION), 64, 32));
        event.registerLayerDefinition(ORGAN_OUTER_LAYER, () -> LayerDefinition.create(HumanoidArmorModel.createBodyLayer(OUTER_ARMOR_DEFORMATION), 64, 32));

        event.registerLayerDefinition(ORGAN_MOB, () -> (LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)));

    }
    public static void registerRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerEntityRenderer(SyntheticsEntities.ORGAN_DISPLAY_MOB.get(), OrganDisplayMobRenderer::new);
    }
}
