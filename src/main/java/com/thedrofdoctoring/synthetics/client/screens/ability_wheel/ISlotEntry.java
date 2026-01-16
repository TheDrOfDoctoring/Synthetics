package com.thedrofdoctoring.synthetics.client.screens.ability_wheel;

import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.world.level.Level;

public interface ISlotEntry<T extends RadialMenuSlot> {

    T addToSlot(GenericRadialMenu<T> menu, T slot, Level level);
}
