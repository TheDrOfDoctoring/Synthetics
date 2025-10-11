package com.thedrofdoctoring.synthetics.core;

import com.thedrofdoctoring.synthetics.blocks.entities.forge.SyntheticForgeDeferBE;
import com.thedrofdoctoring.synthetics.core.data.components.BatteryComponentOptions;
import com.thedrofdoctoring.synthetics.core.data.components.SyntheticsDataComponents;
import com.thedrofdoctoring.synthetics.items.RechargeableBatteryItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

public class SyntheticsCapabilities {

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SyntheticsBlockEntities.SYNTHETIC_FORGE_DEFERRED.get(), SyntheticForgeDeferBE::getFluidCap);
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> {
            if(stack.getItem() instanceof RechargeableBatteryItem && stack.has(SyntheticsDataComponents.ENERGY_COMPONENT) && stack.has(SyntheticsDataComponents.BATTERY_OPTIONS)) {
                BatteryComponentOptions options = stack.get(SyntheticsDataComponents.BATTERY_OPTIONS);
                assert options != null;
                return new ComponentEnergyStorage(stack, SyntheticsDataComponents.ENERGY_COMPONENT.get(), options.capacity(), options.maxReceive(), options.maxExtract());
            }
            return null;
        }, SyntheticsItems.MEDIUM_BATTERY.get());

    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsCapabilities::registerCapabilities);
    }
}
