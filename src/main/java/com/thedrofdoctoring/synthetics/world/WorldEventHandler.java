package com.thedrofdoctoring.synthetics.world;

import com.thedrofdoctoring.synthetics.world.data.PermeableBlocksData;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class WorldEventHandler {

    @SubscribeEvent
    public static void tick(LevelTickEvent.Post event) {
        if(event.getLevel() instanceof ServerLevel level && event.getLevel().getGameTime() % 10 == 0) {
            PermeableBlocksData data = PermeableBlocksData.getData(level.getServer(), level.dimension());
            if(data != null) {
                data.tick(level, 10);
            }
        }
    }
}
