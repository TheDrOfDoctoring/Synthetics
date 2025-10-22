package com.thedrofdoctoring.synthetics.abilities.passive.types;

import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BatteryAbilityType extends PassiveAbilityType {
    public BatteryAbilityType(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onAbilityAdded(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {
        int additionalStorage = (int) instance.factor() * PowerManager.BATTERY_STORAGE_BASE;
        player.getPowerManager().setMaxPower(player.getPowerManager().getMaxPower() + additionalStorage);
    }

    @Override
    public void onAbilityRemoved(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {
        int additionalStorage = (int) instance.factor() * PowerManager.BATTERY_STORAGE_BASE;
        player.getPowerManager().setMaxPower(player.getPowerManager().getMaxPower() - additionalStorage);
    }

    @Override
    public void addDescriptionInfo(AbilityData data, List<Component> description) {
        description.add(Component.translatable("abilities.synthetics.description.energy_storage", data.factor() * PowerManager.BATTERY_STORAGE_BASE).withStyle(ChatFormatting.BLUE));
    }
}
