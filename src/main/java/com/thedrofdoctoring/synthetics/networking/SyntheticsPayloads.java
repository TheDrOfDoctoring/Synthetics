package com.thedrofdoctoring.synthetics.networking;

import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundRequestUpdatePacket;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SyntheticsPayloads {

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(
                ClientboundPlayerUpdatePacket.TYPE,
                ClientboundPlayerUpdatePacket.CODEC,
                ClientboundPlayerUpdatePacket::handleDataPacket
        );


        registrar.playToServer(
                ServerboundRequestUpdatePacket.TYPE,
                ServerboundRequestUpdatePacket.CODEC,
                ServerboundRequestUpdatePacket::handleUpdatePacket
        );
    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsPayloads::registerPayloads);
    }

}
