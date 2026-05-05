package com.thedrofdoctoring.synthetics.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec.IntValue energyOverlayMiddleX;
    public static final ModConfigSpec.IntValue energyOverlayMiddleY;
    public static final ModConfigSpec.IntValue energyOverlayRotation;
    public static final ModConfigSpec.BooleanValue showComplexityGear;
    static {
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        energyOverlayMiddleX = CLIENT_BUILDER.comment("X-Offset of the energy overlay from the center in pixels").defineInRange("energyOverlayMiddleX", -150, -250, 250);
        energyOverlayMiddleY = CLIENT_BUILDER.comment("Y-Offset of the energy overlay from the bottom in pixels").defineInRange("energyOverlayMiddleY", 12, 0, 270);
        energyOverlayRotation = CLIENT_BUILDER.comment("Rotation of the energy overlay in degrees").defineInRange("energyOverlayRotation", 0, 0, 360);
        showComplexityGear = CLIENT_BUILDER.comment("Whether the part complexity gear is displayed in the augmentation chamber").define("showComplexityGear", true);


        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }


}
