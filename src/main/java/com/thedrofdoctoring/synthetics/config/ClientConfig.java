package com.thedrofdoctoring.synthetics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.IntValue abilityCarouselMiddleX;
    public static final ModConfigSpec.IntValue abilityCarouselMiddleY;
    public static final ModConfigSpec.IntValue abilityCarouselRotation;


    public static final ModConfigSpec.IntValue energyOverlayMiddleX;
    public static final ModConfigSpec.IntValue energyOverlayMiddleY;
    public static final ModConfigSpec.IntValue energyOverlayRotation;
    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
        abilityCarouselMiddleX = CLIENT_BUILDER.comment("X-Offset of the ability carousel from the center in pixels").defineInRange("abilityCarouselMiddleX", -150, -250, 250);
        abilityCarouselMiddleY = CLIENT_BUILDER.comment("Y-Offset of the ability carousel from the bottom in pixels").defineInRange("abilityCarouselMiddleY", 22, 0, 270);
        abilityCarouselRotation = CLIENT_BUILDER.comment("Rotation of the ability carousel in degrees").defineInRange("abilityCarouselRotation", 0, 0, 360);

        energyOverlayMiddleX = CLIENT_BUILDER.comment("X-Offset of the energy ovverlay from the center in pixels").defineInRange("energyOverlayMiddleX", -150, -250, 250);
        energyOverlayMiddleY = CLIENT_BUILDER.comment("Y-Offset of the energy overlay from the bottom in pixels").defineInRange("energyOverlayMiddleY", 34, 0, 270);
        energyOverlayRotation = CLIENT_BUILDER.comment("Rotation of the energy overlay in degrees").defineInRange("energyOverlayRotation", 0, 0, 360);


        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }


}
