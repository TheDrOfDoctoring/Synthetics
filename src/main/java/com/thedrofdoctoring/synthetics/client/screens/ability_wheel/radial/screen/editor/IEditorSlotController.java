package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.screen.editor;

import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.ISlotEntry;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.RadialMenuSlot;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface IEditorSlotController<T extends RadialMenuSlot, U extends ISlotEntry<T>> {

    List<U> getPossibleSlotEntries(Player player);

    T createEmptySlot(GenericRadialMenu<T> owner);

    void save(List<List<T>> wheels, Player player);

    ObjectSelectionList<?> createSelectionList(WheelEditorScreen<U, T> editor, Player player);
}
