package com.thedrofdoctoring.synthetics.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.renderers.entities.models.DroneEntityModel;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DroneEntityRenderer extends LivingEntityRenderer<DroneEntity, DroneEntityModel> {

    private static final ResourceLocation TEXTURE = Synthetics.rl("textures/entity/drone.png");
    private static ItemRenderer itemRenderer;

    public DroneEntityRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new DroneEntityModel(context.bakeLayer(DroneEntityModel.LAYER_LOCATION)), 0.3f);
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull DroneEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        ItemStack right = entity.getMainHandItem();
        ItemStack left = entity.getOffhandItem();
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getPreciseBodyRotation(partialTicks)));

        poseStack.translate(0, 0.25f, 0);
        poseStack.scale(0.4f, 0.4f, 0.4f);

        if(!right.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, -0.5);
            itemRenderer.renderStatic(right, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
            poseStack.popPose();
        }
        if(!left.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0, 0, +0.5);
            itemRenderer.renderStatic(left, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DroneEntity droneEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(@NotNull DroneEntity entity) {
        return false;
    }
}
