package com.thedrofdoctoring.synthetics.client.renderers.entities;

import com.thedrofdoctoring.synthetics.entities.FlameProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class FlameProjectileEntityRenderer extends EntityRenderer<FlameProjectileEntity> {

    private static final ResourceLocation FLAME = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/particle/flame.png");

    public FlameProjectileEntityRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);

    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull FlameProjectileEntity entity) {
        return FLAME;
    }
}
