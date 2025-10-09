package com.thedrofdoctoring.synthetics.client.renderers.entities;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsEntitiesClient;
import com.thedrofdoctoring.synthetics.entities.OrganDisplayMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OrganDisplayMobRenderer extends SkeletonRenderer<OrganDisplayMob> {

    private static final ResourceLocation TEXTURE_LOCATION = Synthetics.rl("textures/entity/organ_mob.png");

    public OrganDisplayMobRenderer(EntityRendererProvider.Context context) {
        super(context, SyntheticsEntitiesClient.ORGAN_MOB, SyntheticsEntitiesClient.ORGAN_INNER_LAYER, SyntheticsEntitiesClient.ORGAN_OUTER_LAYER);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull OrganDisplayMob entity) {
        return TEXTURE_LOCATION;
    }
}
