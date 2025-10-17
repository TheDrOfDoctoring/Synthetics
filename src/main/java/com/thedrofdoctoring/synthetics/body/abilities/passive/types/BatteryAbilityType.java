package com.thedrofdoctoring.synthetics.body.abilities.passive.types;

import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.PowerManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

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
}
