package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAbility;
import net.minecraft.core.HolderSet;

import java.util.Optional;

public interface IAbilityHolder {

    Optional<HolderSet<SyntheticAbility>> abilities();
}
