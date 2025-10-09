package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.networking.from_server.ClientboundUpdateDataCachePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SyntheticsEventHandler {


    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        SyntheticsPlayer.get(event.getEntity()).onTick();
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {

        ClientboundUpdateDataCachePacket packet = ClientboundUpdateDataCachePacket.getInstance();
        if (event.getPlayer() != null) {
            event.getPlayer().connection.send(packet);
        } else {
            event.getPlayerList().getPlayers().forEach(p -> p.connection.send(packet));
        }

    }

}
