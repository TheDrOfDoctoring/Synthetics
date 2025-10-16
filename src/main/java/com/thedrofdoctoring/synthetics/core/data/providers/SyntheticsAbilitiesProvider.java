package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
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
                        -0.2d,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
        context.register(
                Abilities.INERTIAL_DAMPENERS_SAFE_FALL,
                SyntheticAbility.create(
                        SyntheticAbilities.SAFE_FALL_ABILITY.get(),
                        Abilities.INERTIAL_DAMPENERS_SAFE_FALL.location(),
                        4d,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        context.register(
                Abilities.LAUNCHBOOT_LAUNCH,
                SyntheticAbility.create(
                        SyntheticAbilities.LEAP.get(),
                        2.5d,
                        SyntheticAbility.options(15, 5, 100),
                        Abilities.LAUNCHBOOT_LAUNCH.location()
                )
        );
        context.register(
                Abilities.HEART_BATTERY,
                SyntheticAbility.create(
                        SyntheticAbilities.BATTERY.get(),
                        1000d,
                        Abilities.HEART_BATTERY.location()
                )
        );
        context.register(
                Abilities.TISSUE_SOLAR_POWER,
                SyntheticAbility.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        10d,
                        Abilities.TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.ADVANCED_TISSUE_SOLAR_POWER,
                SyntheticAbility.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        30d,
                        Abilities.ADVANCED_TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.VISION_CLARIFIER_VIEW,
                SyntheticAbility.create(
                        SyntheticAbilities.UNDERWATER_VISION.get(),
                        15d,
                        Abilities.VISION_CLARIFIER_VIEW.location()
                )
        );
        context.register(
                Abilities.RESPIRATOR_BREATH,
                SyntheticAbility.create(
                        SyntheticAbilities.OXYGEN_BONUS.get(),
                        5d,
                        Abilities.RESPIRATOR_BREATH.location()
                )
        );
        context.register(
                Abilities.METABOLIC_CONVERTER,
                SyntheticAbility.create(
                        SyntheticAbilities.FOOD_GENERATOR.get(),
                        250d,
                        Abilities.METABOLIC_CONVERTER.location()
                )
        );
        context.register(
                Abilities.HAND_WALL_CLIMB,
                SyntheticAbility.create(
                        SyntheticAbilities.WALL_CLIMB.get(),
                        1.0,
                        SyntheticAbility.options(30, 30, 30, 5),
                        Abilities.HAND_WALL_CLIMB.location()
                )
        );
        context.register(
                Abilities.INTEGRATED_EXOSKELETON_SWIM,
                SyntheticAbility.create(
                        SyntheticAbilities.SWIM_SPEED.get(),
                        Abilities.INTEGRATED_EXOSKELETON_SWIM.location(),
                        1d,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
        context.register(
                Abilities.INTEGRATED_EXOSKELETON_WALK,
                SyntheticAbility.create(
                        SyntheticAbilities.MOVEMENT_SPEED.get(),
                        Abilities.INTEGRATED_EXOSKELETON_WALK.location(),
                        0.35d,
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                )
        );
        context.register(
                Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS,
                SyntheticAbility.create(
                        SyntheticAbilities.ARMOUR_TOUGHNESS.get(),
                        Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS.location(),
                        1.5d,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        context.register(
                Abilities.INTERNAL_PLATING_KNOCKBACK,
                SyntheticAbility.create(
                        SyntheticAbilities.ATTACK_KNOCKBACK.get(),
                        Abilities.INTERNAL_PLATING_KNOCKBACK.location(),
                        0.15d,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        context.register(
                Abilities.CYBERNETIC_HAND_DAMAGE,
                SyntheticAbility.create(
                        SyntheticAbilities.ATTACK_DAMAGE.get(),
                        Abilities.CYBERNETIC_HAND_DAMAGE.location(),
                        1d,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
        context.register(
                Abilities.EXTEND_GRIP_BLOCK_REACH,
                SyntheticAbility.create(
                        SyntheticAbilities.BLOCK_INTERACTION_RANGE.get(),
                        Abilities.EXTEND_GRIP_BLOCK_REACH.location(),
                        1.75d,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }
}
