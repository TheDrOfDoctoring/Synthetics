package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen;

import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.IRadialMenuHost;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.AbilityRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AbilityWheelScreen extends GenericWheelScreen<AbilityRadialSlot> {

    public AbilityWheelScreen(@NotNull LocalPlayer player) {
        super(player);
        this.menu = new AbilityRadialMenu(Minecraft.getInstance(), new IRadialMenuHost() {
            @Override
            public void renderTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
                graphics.renderTooltip(font, stack, mouseX, mouseY);
            }

            @Override
            public Screen getScreen() {
                return AbilityWheelScreen.this;
            }

            @Override
            public Font getFontRenderer() {
                return font;
            }
        });
    }

    public static void show() {
        if(Minecraft.getInstance().player != null) {
            Minecraft.getInstance().setScreen(new AbilityWheelScreen(Minecraft.getInstance().player));
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {

        RadialMenuSlot hoveredSlot = this.menu.getHoveredItem();

        if(scrollY < 0 || scrollX > 0) {
            if(hoveredSlot != null) {
                hoveredSlot.setSelectedIndex(hoveredSlot.selectedIndex() + 1);
            }
        } else {
            if(hoveredSlot != null) {
                hoveredSlot.setSelectedIndex(hoveredSlot.selectedIndex() - 1);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
