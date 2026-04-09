package com.thedrofdoctoring.synthetics.client.renderers.block_entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.blocks.entities.PermeableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;


/**
 * Based off the "Portable Hole" renderer, <a href="https://github.com/Fuzss/portablehole/blob/main/1.21.1/Common/src/main/java/fuzs/portablehole/client/renderer/blockentity/TemporaryHoleRenderer.java">...</a>
 * Licensed under MPL 2.0, <a href="https://github.com/Fuzss/portablehole/tree/main?tab=MPL-2.0-1-ov-file">...</a>
 */
public class PermeableBlockEntityRenderer implements BlockEntityRenderer<PermeableBlockEntity> {

    public PermeableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    public void render(@NotNull PermeableBlockEntity blockEntity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Matrix4f matrix4f = poseStack.last().pose();
        this.renderCube(blockEntity, matrix4f, bufferSource.getBuffer(RenderType.endGateway()));
    }

    private void renderCube(PermeableBlockEntity blockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 0.9995F, 0.9995F, 0.9995F, 0.9995F, Direction.SOUTH);
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0005F, 0.0005F, 0.0005F, 0.0005F, Direction.NORTH);
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.9995F, 0.9995F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.0005F, 0.0005F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0005F, 0.0005F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        this.renderFace(blockEntity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.9995F, 0.9995F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderFace(PermeableBlockEntity blockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3, Direction direction) {
        if (!shouldRenderFace(blockEntity, direction)) {
            vertexConsumer.addVertex(matrix4f, x0, y1, z3);
            vertexConsumer.addVertex(matrix4f, x1, y1, z2);
            vertexConsumer.addVertex(matrix4f, x1, y0, z1);
            vertexConsumer.addVertex(matrix4f, x0, y0, z0);
        }
    }

    private static boolean shouldRenderFace(PermeableBlockEntity blockEntity, Direction direction) {
        if(blockEntity.getLevel() == null) return false;
        BlockPos blockPos = blockEntity.getBlockPos();
        return Block.shouldRenderFace(blockEntity.getBlockState(), blockEntity.getLevel(), blockPos, direction, blockPos.relative(direction));
    }

}
