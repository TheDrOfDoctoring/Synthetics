package com.thedrofdoctoring.synthetics.body.abilities.passive;

import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityPassiveInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;

public interface IAbilityEventListener {

    /**
     * Fires every 10 ticks
     */
    default void onTick(AbilityPassiveInstance<?> instance, int instanceCount, SyntheticsPlayer player) {}


}
