package com.thedrofdoctoring.synthetics.networking;

import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundRequestUpdatePacket;
import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundPlayerUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class NetworkingEventHandler {

    @SubscribeEvent
    public static void onTrack(PlayerEvent.StartTracking event) {
        if(event.getTarget() instanceof Player player && event.getEntity() instanceof ServerPlayer serverPlayer) {
            ClientboundPlayerUpdatePacket packet = ClientboundPlayerUpdatePacket.create(player, false);
            serverPlayer.connection.send(packet);
        }
    }
    @SubscribeEvent
    public static void onPlayerLoggedInClient(ClientPlayerNetworkEvent.LoggingIn event) {
        event.getPlayer().connection.send(ServerboundRequestUpdatePacket.getInstance());
    }

    @SubscribeEvent
    public static void onPlayerRespawnedClient(ClientPlayerNetworkEvent.Clone event) {
        event.getPlayer().connection.send(ServerboundRequestUpdatePacket.getInstance());
    }


}
