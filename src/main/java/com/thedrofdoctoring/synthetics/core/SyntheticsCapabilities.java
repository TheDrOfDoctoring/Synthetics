package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.blocks.entities.forge.SyntheticForgeDeferBE;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class SyntheticsCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SyntheticsBlockEntities.SYNTHETIC_FORGE_DEFERRED.get(), SyntheticForgeDeferBE::getFluidCap);

    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsCapabilities::registerCapabilities);
    }
}
