package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ActiveAbilityOptions;
import net.minecraft.resources.ResourceLocation;

public class SyntheticAbilityActiveInstance extends SyntheticAbilityInstance<SyntheticActiveAbilityType> {


    private final int cooldown, duration, powerCost, powerDrain;

    public SyntheticAbilityActiveInstance(SyntheticActiveAbilityType ability, SyntheticsPlayer player, double abilityFactor, ActiveAbilityOptions options, ResourceLocation instanceID) {
        super(ability, player, abilityFactor, instanceID);
        this.cooldown = options.cooldown();
        this.duration = options.duration();
        this.powerCost = options.powerCost();
        this.powerDrain = options.powerDrain();
    }
    public int getCooldown() {
        return cooldown;
    }

    public int getDuration() {
        return duration;
    }

    public int getPowerCost() {
        return powerCost;
    }

    public int getPowerDrain() {
        return powerDrain;
    }
}
