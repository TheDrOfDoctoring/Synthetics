package com.thedrofdoctoring.synthetics.client.renderers.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class HighlightedBlocksRenderer {

    public static void render(PoseStack poseStack, Camera camera) {

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.level == null || minecraft.player == null) return;
        if(!SyntheticsPlayerCache.get(minecraft.player).highlightCache.shouldDraw) return;

        poseStack.pushPose();

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        VertexConsumer buffer = bufferSource.getBuffer(SyntheticsRenderTypes.HIGHLIGHT_BLOCKS);
        Set<BlockPos> toDraw = SyntheticsPlayerCache.get(minecraft.player).highlightCache.toHighlight();

        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        for(BlockPos pos : toDraw) {
            renderHighlightedBlock(poseStack, buffer, pos);
        }
        poseStack.popPose();

    }

    private static void renderHighlightedBlock(PoseStack poseStack, VertexConsumer buffer, BlockPos pos) {
        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();
        if(Minecraft.getInstance().level == null) return;
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());


        float startX = 0, startY = 0, startZ = 0, endX = 1, endY = 1, endZ = 1;
        float red = 0f, green = 0.15f, blue = 0.86f, alpha = 0.8f;
        // Bottom
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   startY, startZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   startY, endZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);

        // Top
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   endY, startZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   endY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   endY, endZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, startX, endY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY, startZ).setColor(red, green, blue, alpha);

        // Side edges
        buffer.addVertex(pose, startX, startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY,   startZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   startY, startZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   endY,   startZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, endX,   startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, endX,   endY,   endZ).setColor(red, green, blue, alpha);

        buffer.addVertex(pose, startX, startY, endZ).setColor(red, green, blue, alpha);
        buffer.addVertex(pose, startX, endY,   endZ).setColor(red, green, blue, alpha);


        poseStack.popPose();
    }
}
