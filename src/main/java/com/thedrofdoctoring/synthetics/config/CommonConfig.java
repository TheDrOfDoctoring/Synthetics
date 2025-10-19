package com.thedrofdoctoring.synthetics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CommonConfig {

    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.ConfigValue<List<? extends String>> modifiedArchaeologicalLootTables;
    public static final ModConfigSpec.IntValue fossilisedScrapWeight;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.comment("This config is primarily unused, as the mod relies on datapacks for its configuration");
        BUILDER.comment("Please check the wiki for information on how to configure this mod. https://github.com/TheDrOfDoctoring/Synthetics/wiki");

        modifiedArchaeologicalLootTables = BUILDER.comment("As we have special limitations when editing the archaeology loot tables, this is required over a tag or similar. A list of strings, matching the path of archaeological loot tables").defineList("modifiedArchaeologyTables", List.of("minecraft:archaeology/trail_ruins_common", "minecraft:archaeology/trail_ruins_rare", "minecraft:archaeology/desert_pyramid"), string -> string instanceof String);
        fossilisedScrapWeight = BUILDER.comment("Weight of fossilised scrap in modified loot tables").defineInRange("fossilisedScrapWeight", 10, 0, 1000);


        COMMON_CONFIG = BUILDER.build();
    }


}
