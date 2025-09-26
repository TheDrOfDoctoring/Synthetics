package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.resources.ResourceLocation;

public class SyntheticAbilityActiveInstance extends SyntheticAbilityInstance<SyntheticActiveAbilityType> {


    public SyntheticAbilityActiveInstance(SyntheticActiveAbilityType ability, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, player, instanceID);
    }
}
