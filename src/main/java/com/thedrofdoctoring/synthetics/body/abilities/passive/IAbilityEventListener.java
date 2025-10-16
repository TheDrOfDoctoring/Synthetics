package com.thedrofdoctoring.synthetics.body.abilities.passive;

import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;

public interface IAbilityEventListener {

    /**
     * Fires every 10 ticks
     */
    default void onTick(SyntheticAbilityPassiveInstance instance, SyntheticsPlayer player) {}
}
