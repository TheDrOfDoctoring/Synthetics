package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public interface IRadialMenuHost {

    Screen getScreen();

    Font getFontRenderer();

    void renderTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY);
}
