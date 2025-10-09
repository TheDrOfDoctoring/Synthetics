package com.thedrofdoctoring.synthetics.client.core;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.client.overlay.AbilityOverlay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class SyntheticsClientSetup {

    public static void register(IEventBus modbus){
        modbus.addListener(SyntheticsClientSetup::registerOverlays);
        modbus.addListener(SyntheticsEntitiesClient::registerLayers);
        modbus.addListener(SyntheticsEntitiesClient::registerRenderers);
        modbus.addListener(SyntheticsBEClient::registerBlockEntityRenderers);
        modbus.addListener(SyntheticsSkulls::registerSkullModels);

    }


    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.ARMOR_LEVEL, Synthetics.rl("ability_overlay"), new AbilityOverlay());
    }
}
