package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public record DrawingContext(GuiGraphics graphics, int width, int height, float x, float y, float z, Font font) {
}
