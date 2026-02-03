package com.thedrofdoctoring.synthetics.client.renderers.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.capabilities.linkable.BlockLinkingPlayer;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.List;


public class LinkableBlocksRenderer {

    public static void render(PoseStack poseStack, Camera camera) {

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null) return;
        if(!SyntheticsPlayerCache.get(minecraft.player).canViewLinked) return;
        
        poseStack.pushPose();

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(SyntheticsRenderTypes.LINKABLE_BLOCKS);

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Vec3 pos = minecraft.player.position();
        Vec3i currentPos = new Vec3i((int) pos.x, (int) pos.y, (int) pos.z);
        List<BlockPos> positions = BlockLinkingPlayer.get(minecraft.player).getLinkedPositionsInDimension(minecraft.player.level().dimension())
                .stream()
                .filter(p -> p.closerThan(currentPos, 100d))
                .toList();
        for(BlockPos blockPos : positions) {
            renderLinkedBlock(poseStack, buffer, blockPos);
        }
        bufferSource.endBatch(SyntheticsRenderTypes.LINKABLE_BLOCKS);
        for(BlockPos blockPos : positions) {
            BlockEntity be = minecraft.level.getBlockEntity(blockPos);
            if(be != null) {
                renderLinkedName(poseStack, camera.rotation(), bufferSource, be);
            }
        }
        poseStack.popPose();
    }

    private static void renderLinkedName(PoseStack poseStack, Quaternionf cameraRotation, MultiBufferSource bufferSource, BlockEntity entity) {
        Component customName = entity.components().get(DataComponents.CUSTOM_NAME);
        if(customName != null) {
            poseStack.pushPose();

            PoseStack.Pose pose = poseStack.last();
            if(Minecraft.getInstance().level == null) return;
            Vec3 centre = entity.getBlockPos().getCenter();
            poseStack.translate(centre.x, centre.y + 1, centre.z);
            poseStack.mulPose(cameraRotation);
            poseStack.scale(0.025F, -0.025F, 0.025F);
            Font font = Minecraft.getInstance().font;
            float width = (float) -font.width(customName) / 2;
            int background = 255;
            font.drawInBatch(customName, width, 0f, 0xFFFFFFFF, false, pose.pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, background, 0);
            poseStack.popPose();
        }

    }



    private static void renderLinkedBlock(PoseStack poseStack, VertexConsumer buffer, BlockPos pos) {
        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();
        if(Minecraft.getInstance().level == null) return;
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.scale(1.02f, 1.02f, 1.02f);


        float startX = 0, startY = 0, startZ = 0, endX = 1, endY = 1, endZ = 1;
        double time = (Minecraft.getInstance().level.getGameTime()) * 0.01;
        float rPulse = (float) Math.abs(Math.sin(time));
        int packedRGB = Mth.hsvToArgb(rPulse, 0.75f, 0.75f, 255);
        float red   = 1.0f * FastColor.ARGB32.red(packedRGB);
        float green = 1.0f * FastColor.ARGB32.green(packedRGB);
        float blue  = 1.0f * FastColor.ARGB32.blue(packedRGB);
        float alpha = 0.5f;
        //down
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);

        //up
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);

        //east
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);

        //west
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);

        //south
        buffer.addVertex(pose, endX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX, startY, endZ).setColor(red, green, blue, alpha);

        //north
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);


        poseStack.popPose();
    }
}
