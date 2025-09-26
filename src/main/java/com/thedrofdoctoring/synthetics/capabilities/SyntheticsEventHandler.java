package com.thedrofdoctoring.synthetics.capabilities;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SyntheticsEventHandler {


    @SubscribeEvent
    public static void onTick(PlayerTickEvent.Post event) {
        SyntheticsPlayer.get(event.getEntity()).onTick();
    }

}
