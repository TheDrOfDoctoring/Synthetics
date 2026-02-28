package com.thedrofdoctoring.synthetics.abilities.active;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.IAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public abstract class LastingAbilityType<T extends IAbilityInstance> extends ActiveAbilityType<T> {
    public LastingAbilityType(ResourceLocation id) {
        super(id);
    }

    public abstract boolean onTick(SyntheticsPlayer syntheticsPlayer, T instance);

    public abstract void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, T instance);

    public abstract void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer);


    @SuppressWarnings("unchecked")
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        try {
            T t = (T) instance;
            onRestoreActivate(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        try {
            T t = (T) instance;
            return onTick(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.toString());
        }
        return false;
    }

    public abstract void activateClient(SyntheticsPlayer syntheticsPlayer, T instance);

    @SuppressWarnings("unchecked")
    public void activateClient(SyntheticsPlayer syntheticsPlayer, AbilityActiveInstance<?> instance) {
        try {
            T t = (T) instance;
            activateClient(syntheticsPlayer, t);

        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.toString());
        }
    }
}
