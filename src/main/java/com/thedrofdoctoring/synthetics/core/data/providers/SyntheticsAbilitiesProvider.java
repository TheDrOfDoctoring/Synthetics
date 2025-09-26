package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SyntheticsAbilitiesProvider {


    public static void createAbilities(BootstrapContext<SyntheticAbility> context) {

        context.register(
                Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE,
                SyntheticAbility.create(
                        SyntheticAbilities.FALL_DAMAGE_ABILITY.get(),
                        Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE.location(),
                        -0.5d,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
        context.register(
                Abilities.INERTIAL_DAMPENERS_SAFE_FALL,
                SyntheticAbility.create(
                        SyntheticAbilities.SAFE_FALL_ABILITY.get(),
                        Abilities.INERTIAL_DAMPENERS_SAFE_FALL.location(),
                        0.5d,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );

    }
}
