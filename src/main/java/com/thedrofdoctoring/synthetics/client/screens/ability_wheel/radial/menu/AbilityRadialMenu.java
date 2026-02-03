package com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.menu;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.AbilityManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.AbilityWheel;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.IRadialMenuHost;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AbilityRadialMenu extends GenericRadialMenu<AbilityRadialSlot> {


    private final AbilityManager abilityManager;

    private List<List<AbilityRadialSlot>> allSlots;

    public AbilityRadialMenu(Minecraft minecraft, IRadialMenuHost host) {
        super(minecraft, host);
        List<AbilityWheel> wheels = SyntheticsClient.getInstance().getAdvancedClientConfig().levelWheels();

        this.abilityManager = SyntheticsPlayer.get(Objects.requireNonNull(minecraft.player)).getAbilityManager();
        allSlots = wheels.stream().map(wheel ->
                wheel.slots()
                        .stream()
                        .map(slot -> new AbilityRadialSlot(this, slot))
                        .toList())
                .toList();
        this.visibleItems = allSlots.stream().findFirst().orElse(Collections.emptyList());
        if(this.visibleItems.isEmpty()) {
            this.selectedWheel = -1;
        } else {
            this.selectedWheel = 0;
        }
    }

    @Override
    public List<List<AbilityRadialSlot>> getWheels() {
        return allSlots;
    }

    @Override
    public void setWheels(List<List<AbilityRadialSlot>> wheels) {
        this.allSlots = wheels;
        if(this.selectedWheel < wheels.size() && !wheels.isEmpty() && this.selectedWheel > -1) {
            this.visibleItems = this.allSlots.get(selectedWheel);
        } else if(!wheels.isEmpty()) {
            this.selectedWheel = wheels.size() - 1;
            this.visibleItems = this.allSlots.getFirst();
        }
    }


    @Override
    public void onClickOutside() {}

    @Override
    public void swapWheel(int directionAmount) {
        if(selectedWheel > -1) {
            selectedWheel = Math.clamp(selectedWheel + directionAmount, 0, allSlots.size() - 1);
            this.visibleItems = allSlots.get(selectedWheel);
        }
    }

    @Override
    public int totalWheels() {
        return this.allSlots.size();
    }

    @Override
    protected void drawPieArc(AbilityRadialSlot slot, BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int colour) {
        Ability selected = slot.getSelectedAbility();
        if(selected != null) {

            if(!this.abilityManager.hasAbility(selected)) {
                int colourBase = slot.isHovered() ? 200 : 160;
                int colourA = (60) | (60 << 8) | (colourBase << 16) | (100 << 24);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, colourA);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusIn + ((radiusOut - radiusIn)), startAngle, endAngle, colourA);
                return;
            }

            AbilityActiveInstance<?> instance = abilityManager.getActiveAbilityInstanceById(selected.abilityType().getAbilityID());
            if(instance == null) {
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, colour);
                return;
            }

            if(selected.abilityType() instanceof ActiveAbilityType<?> active && abilityManager.isAbilityActive(active)) {
                float actionPercentage = abilityManager.getPercentageForAbilityTime(instance);
                int colourBase = slot.isHovered() ? 200 : 160;
                int colourA = 60 | (colourBase << 8) | (colourBase << 16) | (100 << 24);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, colourA);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusIn + ((radiusOut - radiusIn) * actionPercentage), startAngle, endAngle, colourA);
                return;
            } else if(selected.abilityType() instanceof ActiveAbilityType<?> active && abilityManager.isAbilityOnCooldown(active)) {
                float actionPercentage = -abilityManager.getPercentageForAbilityTime(instance);
                int colourBase = slot.isHovered() ? 200 : 160;
                int colourA = (60) | (60 << 8) | (colourBase << 16) | (100 << 24);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, colourA);
                super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusIn + ((radiusOut - radiusIn) * actionPercentage), startAngle, endAngle, colourA);
                return;
            }

        }
        super.drawPieArc(slot, buffer, x, y, z, radiusIn, radiusOut, startAngle, endAngle, colour);
    }
}
