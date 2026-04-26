package com.thedrofdoctoring.synthetics.networking;

import com.thedrofdoctoring.synthetics.networking.from_client.*;
import com.thedrofdoctoring.synthetics.networking.from_server.*;
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
        registrar.playToServer(
                ServerboundRequestLinkableUpdatePacket.TYPE,
                ServerboundRequestLinkableUpdatePacket.CODEC,
                ServerboundRequestLinkableUpdatePacket::handle
        );
        registrar.playToServer(
                ServerboundClimbPacket.TYPE,
                ServerboundClimbPacket.CODEC,
                ServerboundClimbPacket::handle
        );
        registrar.playToServer(
                ServerboundLinkedInputPacket.TYPE,
                ServerboundLinkedInputPacket.CODEC,
                ServerboundLinkedInputPacket::handle
        );
        registrar.playToServer(
                ServerboundLinkedInteractPacket.TYPE,
                ServerboundLinkedInteractPacket.CODEC,
                ServerboundLinkedInteractPacket::handle
        );
        registrar.playToServer(
                ServerboundLinkedUsePacket.TYPE,
                ServerboundLinkedUsePacket.CODEC,
                ServerboundLinkedUsePacket::handle
        );
        registrar.playToServer(
                ServerboundResetCameraPacket.TYPE,
                ServerboundResetCameraPacket.CODEC,
                ServerboundResetCameraPacket::handle
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
        registrar.playToClient(
                ClientboundLevelUUIDPacket.TYPE,
                ClientboundLevelUUIDPacket.CODEC,
                ClientboundLevelUUIDPacket::handle
        );
        registrar.playToClient(
                ClientboundLinkableUpdatePacket.TYPE,
                ClientboundLinkableUpdatePacket.CODEC,
                ClientboundLinkableUpdatePacket::handle
        );
        registrar.playToClient(
                ClientboundLinkedInteractPacket.TYPE,
                ClientboundLinkedInteractPacket.CODEC,
                ClientboundLinkedInteractPacket::handle
        );
        registrar.playToClient(
                ClientboundWallClimbPacket.TYPE,
                ClientboundWallClimbPacket.CODEC,
                ClientboundWallClimbPacket::handle
        );
        registrar.playToClient(
                ClientboundViewLinkPacket.TYPE,
                ClientboundViewLinkPacket.CODEC,
                ClientboundViewLinkPacket::handle
        );

    }

    public static void register(IEventBus bus) {
        bus.addListener(SyntheticsPayloads::registerPayloads);
    }

}
