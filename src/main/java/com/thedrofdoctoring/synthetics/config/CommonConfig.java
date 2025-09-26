package com.thedrofdoctoring.synthetics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {

    public static final ModConfigSpec COMMON_CONFIG;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        COMMON_CONFIG = BUILDER.build();
    }


}
