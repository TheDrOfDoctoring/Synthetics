package com.thedrofdoctoring.synthetics.core.data.gen;

import com.thedrofdoctoring.synthetics.config.CommonConfig;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.ArrayList;

@EventBusSubscriber
public class LootEventHandler {
    /*
    We have some limitations due to it being an archaeological loot table.
    As the Loot Table registry isn't built, we can't check tags either.
    This may change in the future to moving to a more data driven approach, especially if the scope of this expands.
    */
    @SubscribeEvent
    public static void onTableAdded(LootTableLoadEvent event) {

        if(CommonConfig.modifiedArchaeologicalLootTables.get().contains(event.getTable().getLootTableId().toString())) {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null) {
                ArrayList<LootPoolEntryContainer> entries = new ArrayList<>(pool.entries);
                entries.add(LootItem.lootTableItem(SyntheticsItems.FOSSILISED_SCRAP.get()).setWeight(CommonConfig.fossilisedScrapWeight.get()).build());
                pool.entries = entries;
            }
        }
    }
}
