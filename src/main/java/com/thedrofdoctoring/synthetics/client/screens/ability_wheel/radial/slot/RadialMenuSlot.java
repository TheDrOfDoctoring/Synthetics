package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot;

import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.DrawingContext;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnusedReturnValue")
public abstract class RadialMenuSlot {

    private boolean hovered;

    protected final GenericRadialMenu<?> owner;
    protected Component centralText;
    protected int selectedIndex;


    protected RadialMenuSlot(GenericRadialMenu<?> owner) {
        this.owner = owner;
        this.selectedIndex = 0;
    }


    @Nullable
    public abstract Component getCentralText();


    public int selectedIndex() {
        return selectedIndex;
    }

    public abstract RadialMenuSlot removeSelectedItem();

    public abstract int size();

    public void setSelectedIndex(int selectedIndex) {
        if(size() == 0) {
            this.selectedIndex = -1;
            return;
        }
        this.selectedIndex = Math.clamp(selectedIndex, 0, size() - 1);
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public abstract void draw(DrawingContext context);

    public abstract void drawTooltips(DrawingContext context);

    public abstract boolean isEmpty();

    public boolean onClick() {
        return false;
    }


}
