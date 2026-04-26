package com.thedrofdoctoring.synthetics.client.renderers.entities.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class DroneEntityModel extends EntityModel<DroneEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Synthetics.rl("drone"), "main");
    private final ModelPart main;
    private final ModelPart[] blades;

    public DroneEntityModel(ModelPart root) {
        this.main = root.getChild("main");
        ModelPart rotor4 = this.main.getChild("rotor4");
        ModelPart rotorblade4 = rotor4.getChild("rotorblade4");
        ModelPart rotor3 = this.main.getChild("rotor3");
        ModelPart rotorblade3 = rotor3.getChild("rotorblade3");
        ModelPart rotor2 = this.main.getChild("rotor2");
        ModelPart rotorblade2 = rotor2.getChild("rotorblade2");
        ModelPart rotor1 = this.main.getChild("rotor1");
        ModelPart rotorblade1 = rotor1.getChild("rotorblade1");
        this.blades = new ModelPart[]{rotorblade1, rotorblade2, rotorblade3, rotorblade4};
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.4167F, -5.75F, 2.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(24, 32).addBox(-1.0F, -0.4167F, -6.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 22.4167F, -1.25F));

        PartDefinition rotor4 = main.addOrReplaceChild("rotor4", CubeListBuilder.create().texOffs(32, 13).addBox(-2.125F, 1.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 22).addBox(-1.125F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(0.875F, 1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.875F, -1.4167F, 5.75F));

        PartDefinition rotorblade4 = rotor4.addOrReplaceChild("rotorblade4", CubeListBuilder.create().texOffs(32, 0).addBox(-3.5F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 24).addBox(-0.5F, 0.0F, -3.5F, 1.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.625F, -2.0F, 0.0F));

        PartDefinition rotor3 = main.addOrReplaceChild("rotor3", CubeListBuilder.create().texOffs(32, 9).addBox(-0.875F, 1.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 32).addBox(0.125F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 32).addBox(-2.875F, 1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.875F, -1.4167F, 5.75F));

        PartDefinition rotorblade3 = rotor3.addOrReplaceChild("rotorblade3", CubeListBuilder.create().texOffs(0, 32).addBox(-3.5F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-0.5F, 0.0F, -3.5F, 1.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.625F, -2.0F, 0.0F));

        PartDefinition rotor2 = main.addOrReplaceChild("rotor2", CubeListBuilder.create().texOffs(32, 5).addBox(0.25F, -0.25F, -2.625F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 17).addBox(1.25F, -4.25F, -1.625F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 29).addBox(-1.75F, -0.25F, -1.625F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.75F, -0.1667F, -2.125F));

        PartDefinition rotorblade2 = rotor2.addOrReplaceChild("rotorblade2", CubeListBuilder.create().texOffs(16, 31).addBox(-3.5F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 17).addBox(-0.5F, 0.0F, -3.5F, 1.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(1.75F, -3.25F, -1.125F));

        PartDefinition rotor1 = main.addOrReplaceChild("rotor1", CubeListBuilder.create().texOffs(32, 1).addBox(-2.125F, 1.0F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 32).addBox(-1.125F, -3.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 27).addBox(0.875F, 1.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.875F, -1.4167F, -3.25F));

        PartDefinition rotorblade1 = rotor1.addOrReplaceChild("rotorblade1", CubeListBuilder.create().texOffs(0, 17).addBox(-0.5F, 0.0F, -3.5F, 1.0F, 0.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 31).addBox(-3.5F, 0.0F, -0.5F, 7.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.625F, -2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(@NotNull DroneEntity droneEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean isFlying = droneEntity.isFlying();
        if(isFlying) {
            for(int i = 0; i < blades.length; i++) {
                float rotSpeed = ageInTicks * 1.5f;
                blades[i].yRot = i < 2 ? rotSpeed : -rotSpeed;
            }

        } else {
            for(ModelPart blade : blades) {
                blade.yRot = 0;
            }
        }

    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int i, int i1, int i2) {
        main.render(poseStack, vertexConsumer, i, i1, i2);
    }
}
