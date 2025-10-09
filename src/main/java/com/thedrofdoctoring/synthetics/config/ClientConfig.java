package com.thedrofdoctoring.synthetics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.IntValue abilityCarouselMiddleX;
    public static final ModConfigSpec.IntValue abilityCarouselMiddleY;
    public static final ModConfigSpec.IntValue abilityCarouselRotation;

    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        abilityCarouselMiddleX = CLIENT_BUILDER.comment("X-Offset of the ability carousel from the center in pixels").defineInRange("abilityCarouselMiddleX", -150, -250, 250);
        abilityCarouselMiddleY = CLIENT_BUILDER.comment("Y-Offset of the ability carousel from the bottom in pixels").defineInRange("abilityCarouselMiddleY", 22, 0, 270);
        abilityCarouselRotation = CLIENT_BUILDER.comment("Rotation of the ability carousel in degrees").defineInRange("abilityCarouselRotation", 0, 0, 360);

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }


}
