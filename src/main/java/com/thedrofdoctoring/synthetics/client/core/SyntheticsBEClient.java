package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

public class SyntheticsBEClient {

    public static void registerBlockEntityRenderers(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerBlockEntityRenderer(SyntheticsBlockEntities.ORGAN_SKULL.get(), SkullBlockRenderer::new);
    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsBEClient::registerBlockEntityRenderers);
    }
}
