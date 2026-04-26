package com.thedrofdoctoring.synthetics.client.renderers.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.renderers.entities.models.HarpoonProjectileEntityModel;
import com.thedrofdoctoring.synthetics.entities.HarpoonProjectileEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HarpoonProjectileEntityRenderer extends EntityRenderer<HarpoonProjectileEntity> {

    private static final ResourceLocation TEXTURE = Synthetics.rl("textures/entity/projectile/harpoon.png");

    private final EntityModel<HarpoonProjectileEntity> model;
    private final BlockRenderDispatcher dispatcher;

    public HarpoonProjectileEntityRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
        this.model = new HarpoonProjectileEntityModel(context.bakeLayer(HarpoonProjectileEntityModel.LAYER_LOCATION));
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HarpoonProjectileEntity harpoonProjectileEntity) {
        return TEXTURE;
    }

    @Override
    public void render(@NotNull HarpoonProjectileEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        LivingEntity hit = entity.getHitEntity();

        poseStack.popPose();
        if(hit != null && entity.getOwner() != null) {
            poseStack.pushPose();
            Vector3f fromHitToOwner = entity.getOwner().position().subtract(entity.position()).add(0, entity.getOwner().getEyeHeight() / 2, 0).toVector3f();
            Vector3f fromHitToOwnerNormalised = new Vector3f(fromHitToOwner).normalize();

            Quaternionf quat = new Quaternionf();
            float dist = entity.getOwner().distanceTo(entity);
            Vec3 vec = entity.getOnPos().getCenter().normalize();
            quat = quat.rotationTo(0, 1,0, fromHitToOwnerNormalised.x, fromHitToOwnerNormalised.y, fromHitToOwnerNormalised.z);
            poseStack.mulPose(quat);
            poseStack.scale(1.5f, dist, 1.5f);
            poseStack.translate(vec.x+0.3f, vec.y-0.15f, -0.5f);


            dispatcher.renderSingleBlock(Blocks.CHAIN.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected boolean shouldShowName(@NotNull HarpoonProjectileEntity entity) {
        return false;
    }
}
