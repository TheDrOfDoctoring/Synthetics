package com.thedrofdoctoring.synthetics.client.renderers.entities;

import com.thedrofdoctoring.synthetics.entities.linkable.DummyCameraEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DummyCameraEntityRenderer extends EntityRenderer<DummyCameraEntity> {
    public DummyCameraEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DummyCameraEntity dummyCameraEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(@NotNull DummyCameraEntity livingEntity, @NotNull Frustum camera, double camX, double camY, double camZ) {
        return false;
    }
}
