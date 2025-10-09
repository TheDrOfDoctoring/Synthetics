package com.thedrofdoctoring.synthetics.client.core.items;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;

public class SyntheticsClientItems {

    public void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(SyntheticItemModelLoader.SyntheticGeometryLoader.ID, SyntheticItemModelLoader.SyntheticGeometryLoader.INSTANCE);
    }

    public static void register(IEventBus bus) {
        SyntheticsClientItems clientItems = new SyntheticsClientItems();
        bus.addListener(clientItems::registerGeometryLoaders);
    }
}
