package com.thedrofdoctoring.synthetics.networking;

import com.thedrofdoctoring.synthetics.networking.from_client.*;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundLeapPacket;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundUpdateDataCachePacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SyntheticsPayloads {

    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");
        registerToServerPackets(registrar);
        registerToClientPackets(registrar);



    }

    private static void registerToServerPackets(PayloadRegistrar registrar) {
        registrar.playToServer(
                ServerboundRequestUpdatePacket.TYPE,
                ServerboundRequestUpdatePacket.CODEC,
                ServerboundRequestUpdatePacket::handle
        );
        registrar.playToServer(
                ServerboundActivateAbilityPacket.TYPE,
                ServerboundActivateAbilityPacket.CODEC,
                ServerboundActivateAbilityPacket::handle
        );
        registrar.playToServer(
                ServerboundResearchPacket.TYPE,
                ServerboundResearchPacket.CODEC,
                ServerboundResearchPacket::handle
        );
        registrar.playToServer(
                ServerboundInstallableMenuPacket.TYPE,
                ServerboundInstallableMenuPacket.CODEC,
                ServerboundInstallableMenuPacket::handle
        );
        registrar.playToServer(
                ServerboundRemoveAugmentPacket.TYPE,
                ServerboundRemoveAugmentPacket.CODEC,
                ServerboundRemoveAugmentPacket::handle
        );
    }
    private static void registerToClientPackets(PayloadRegistrar registrar) {
        registrar.playToClient(
                ClientboundPlayerUpdatePacket.TYPE,
                ClientboundPlayerUpdatePacket.CODEC,
                ClientboundPlayerUpdatePacket::handle
        );
        registrar.playToClient(
                ClientboundLeapPacket.TYPE,
                ClientboundLeapPacket.CODEC,
                ClientboundLeapPacket::handle
        );
        registrar.playToClient(
                ClientboundUpdateDataCachePacket.TYPE,
                ClientboundUpdateDataCachePacket.CODEC,
                ClientboundUpdateDataCachePacket::handle
        );
    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsPayloads::registerPayloads);
    }

}
