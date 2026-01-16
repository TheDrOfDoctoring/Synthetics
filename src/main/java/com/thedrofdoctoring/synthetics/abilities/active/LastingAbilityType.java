package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class LastingAbilityType<T extends AbilityActiveInstance.Data> extends ActiveAbilityType<T> {
    public LastingAbilityType(ResourceLocation id) {
        super(id);
    }

    public abstract boolean onTick(SyntheticsPlayer syntheticsPlayer, T abilityData);

    public abstract void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, T abilityData);

    public abstract void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer);


    @SuppressWarnings("unchecked")
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityData abilityData) {
        try {
            T t = (T) abilityData;
            onRestoreActivate(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability data {} is wrong for type", abilityData.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityData abilityData) {
        try {
            T t = (T) abilityData;
            return onTick(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability data {} is wrong for type", abilityData.toString());
        }
        return false;
    }
}
