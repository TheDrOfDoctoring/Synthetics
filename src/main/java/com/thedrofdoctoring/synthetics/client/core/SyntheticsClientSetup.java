package com.thedrofdoctoring.synthetics.client.core;

import com.mojang.logging.LogUtils;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.core.assets.InstallableModelLoader;
import com.thedrofdoctoring.synthetics.client.overlay.EnergyOverlay;
import com.thedrofdoctoring.synthetics.client.particles.SyntheticsClientParticles;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.Collection;

public class SyntheticsClientSetup {

    public static void register(IEventBus modbus){
        modbus.addListener(SyntheticsClientSetup::registerOverlays);
        modbus.addListener(SyntheticsEntitiesClient::registerLayers);
        modbus.addListener(SyntheticsClientSetup::registerClientReloadListeners);
        modbus.addListener(SyntheticsClientSetup::registerAdditionalModels);
        modbus.addListener(SyntheticsEntitiesClient::registerRenderers);
        modbus.addListener(SyntheticsEntitiesClient::addRenderLayers);
        modbus.addListener(SyntheticsBEClient::registerBlockEntityRenderers);
        modbus.addListener(SyntheticsSkulls::registerSkullModels);
        modbus.addListener(SyntheticsClientParticles::registerParticles);

    }
    
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.ARMOR_LEVEL, Synthetics.rl("energy_overlay"), new EnergyOverlay());

    }

    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(InstallableModelLoader.INSTANCE);
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        Collection<ResourceLocation> requestedModels = InstallableModelLoader.INSTANCE.requestedModels();
        LogUtils.getLogger().debug("Registering {} models referenced by InstallableModels", requestedModels.size());
        for (ResourceLocation model : requestedModels) {
            event.register(ModelResourceLocation.standalone(model));
        }
    }

}
