package com.thedrofdoctoring.synthetics.abilities.passive;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.abilities.IAbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public interface IAbilityEventListener<T extends IAbilityInstance> {

    default void onTick(T instance, int instanceCount, SyntheticsPlayer player) {}

    @SuppressWarnings("unchecked")
    default void onTick(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onTick(t, instanceCount, player);
        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.toString());
        }
    }

    default void onDamage(LivingDamageEvent.Pre event, T instance, int instanceCount, SyntheticsPlayer player) {}

    @SuppressWarnings("unchecked")
    default void onDamage(LivingDamageEvent.Pre event, AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {
        try {
            T t = (T) instance;
            onDamage(event, t, instanceCount, player);
        } catch (ClassCastException e) {
            Synthetics.LOGGER.error("Given ability instance {} is wrong for type", instance.toString());
        }
    }
}
