package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot;

import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.DrawingContext;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu.GenericRadialMenu;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundActivateAbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;


public class AbilityRadialSlot extends RadialMenuSlot {

    private final AbilityWheel.Slot slotData;
    private final int size;

    public AbilityRadialSlot(GenericRadialMenu<AbilityRadialSlot> owner, AbilityWheel.Slot slotData) {
        super(owner);
        this.slotData = slotData;
        this.size = slotData.abilities().size();
        if(slotData.abilities().size() == 0) {
            this.selectedIndex = -1;
        }
    }

    public AbilityWheel.Slot slotData() {
        return slotData;
    }

    @Override
    public AbilityRadialSlot removeSelectedItem() {
        if(this.selectedIndex > -1) {
            Holder<Ability> toRemove = this.slotData.abilities().get(selectedIndex);
            HolderSet<Ability> set = HolderSet.direct(this.slotData.abilities()
                    .stream()
                    .sorted(Comparator.comparing(t -> t == toRemove))
                    .skip(1)
                    .toList());
            //noinspection unchecked
            return new AbilityRadialSlot((GenericRadialMenu<AbilityRadialSlot>) owner, new AbilityWheel.Slot(set));
        }
        return this;
    }

    @Override
    public @Nullable Component getCentralText() {
        if(selectedIndex > -1) {
            Ability ability = slotData.abilities().get(selectedIndex).value();
            return ability.abilityType().title(ability.abilityData());
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return selectedIndex == -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void draw(DrawingContext context) {
        if(selectedIndex > -1) {
            Ability ability = slotData.abilities().get(selectedIndex).value();
            context.graphics().blit(ability.textureLocation(), (int) (context.x() - 8), (int) (context.y() - 8), 0, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override
    public void drawTooltips(DrawingContext context) {

    }

    public @Nullable Ability getSelectedAbility() {
        if(selectedIndex > -1) {
            return slotData.abilities().get(selectedIndex).value();
        }
        return null;
    }

    @Override
    public boolean onClick() {
        if(selectedIndex > -1 && Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundActivateAbilityPacket(slotData.abilities().get(selectedIndex).value().abilityType()));
            owner.close();
        }

        return false;
    }
}
