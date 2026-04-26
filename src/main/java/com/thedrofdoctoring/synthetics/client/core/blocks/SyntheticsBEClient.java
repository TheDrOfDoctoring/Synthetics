package com.thedrofdoctoring.synthetics.client.core.blocks;

import com.thedrofdoctoring.synthetics.client.renderers.block_entities.PermeableBlockEntityRenderer;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.jetbrains.annotations.NotNull;

public class SyntheticsBEClient {

    public static void registerBlockEntityRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerBlockEntityRenderer(SyntheticsBlockEntities.ORGAN_SKULL.get(), SkullBlockRenderer::new);
        event.registerBlockEntityRenderer(SyntheticsBlockEntities.PERMEABLE.get(), PermeableBlockEntityRenderer::new);
    }

    public static void registerBlockModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(PermeableBlockModelLoader.ID, PermeableBlockModelLoader.INSTANCE);
    }

}
