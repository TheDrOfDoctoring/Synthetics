package com.thedrofdoctoring.synthetics.client.renderers.entities.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.entities.HarpoonProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;


public class HarpoonProjectileEntityModel extends EntityModel<HarpoonProjectileEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Synthetics.rl("harpoon"), "main");
    private final ModelPart main;

    public HarpoonProjectileEntityModel(ModelPart root) {
        this.main = root.getChild("main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(3, 2).addBox(-2.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(3, 2).addBox(1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 1).addBox(0.0F, -1.0F, -2.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 1).addBox(0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition b4_r1 = main.addOrReplaceChild("b4_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition b3_r1 = main.addOrReplaceChild("b3_r1", CubeListBuilder.create().texOffs(1, 1).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0, -1.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition b2_r1 = main.addOrReplaceChild("b2_r1", CubeListBuilder.create().texOffs(0, 1).addBox(-2.0F, -5.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0, 1.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition b1_r1 = main.addOrReplaceChild("b1_r1", CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 0, 0.0F, 0.0F, 0.0F, -0.5236F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(@NotNull HarpoonProjectileEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int i, int i1, int i2) {
        main.render(poseStack, vertexConsumer, i, i1, i2);
    }
}
