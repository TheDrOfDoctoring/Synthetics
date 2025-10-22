package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.types.AttributeAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SyntheticsAbilitiesProvider {


    public static void createAbilities(BootstrapContext<Ability> context) {

        context.register(
                Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                -0.2d,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                Attributes.FALL_DAMAGE_MULTIPLIER
                        ),
                        Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE.location()
                )
        );
        context.register(
                Abilities.INERTIAL_DAMPENERS_SAFE_FALL,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                4d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.SAFE_FALL_DISTANCE
                        ),
                        Abilities.INERTIAL_DAMPENERS_SAFE_FALL.location()
                )
        );
        context.register(
                Abilities.LAUNCHBOOT_LAUNCH,
                Ability.create(
                        SyntheticAbilities.LEAP.get(),
                        ActiveAbilityType.create(
                                2.5d,
                                ActiveAbilityOptions.options(15, 5, 100)
                        ),
                        Abilities.LAUNCHBOOT_LAUNCH.location()
                )
        );
        context.register(
                Abilities.HEART_BATTERY,
                Ability.create(
                        SyntheticAbilities.BATTERY.get(),
                        PassiveAbilityType.create(1000d),
                        Abilities.HEART_BATTERY.location()
                )
        );
        context.register(
                Abilities.TISSUE_SOLAR_POWER,
                Ability.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        PassiveAbilityType.create(10d),
                        Abilities.TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.ADVANCED_TISSUE_SOLAR_POWER,
                Ability.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        PassiveAbilityType.create(30d),
                        Abilities.ADVANCED_TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.VISION_CLARIFIER_VIEW,
                Ability.create(
                        SyntheticAbilities.UNDERWATER_VISION.get(),
                        PassiveAbilityType.create(15d),
                        Abilities.VISION_CLARIFIER_VIEW.location()
                )
        );
        context.register(
                Abilities.RESPIRATOR_BREATH,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                5d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.OXYGEN_BONUS
                        ),
                        Abilities.RESPIRATOR_BREATH.location()
                )
        );
        context.register(
                Abilities.METABOLIC_CONVERTER,
                Ability.create(
                        SyntheticAbilities.FOOD_GENERATOR.get(),
                        PassiveAbilityType.create(250d),
                        Abilities.METABOLIC_CONVERTER.location()
                )
        );
        context.register(
                Abilities.HAND_WALL_CLIMB,
                Ability.create(
                        SyntheticAbilities.WALL_CLIMB.get(),
                        ActiveAbilityType.create(
                                1.0d,
                                ActiveAbilityOptions.options(30, 30, 30, 5)
                        ),
                        Abilities.HAND_WALL_CLIMB.location()
                )
        );
        context.register(
                Abilities.INTEGRATED_EXOSKELETON_SWIM,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                1d,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                Attributes.WATER_MOVEMENT_EFFICIENCY
                        ),
                        Abilities.INTEGRATED_EXOSKELETON_SWIM.location()
                )
        );
        context.register(
                Abilities.INTEGRATED_EXOSKELETON_WALK,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                0.25d,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                Attributes.MOVEMENT_SPEED
                        ),
                        Abilities.INTEGRATED_EXOSKELETON_WALK.location()
                )
        );
        context.register(
                Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                1.5d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.ARMOR_TOUGHNESS
                        ),
                        Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS.location()
                )
        );
        context.register(
                Abilities.INTERNAL_PLATING_KNOCKBACK,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                0.15d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.KNOCKBACK_RESISTANCE
                        ),
                        Abilities.INTERNAL_PLATING_KNOCKBACK.location()
                )
        );
        context.register(
                Abilities.CYBERNETIC_HAND_DAMAGE,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                1d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.ATTACK_DAMAGE
                        ),
                        Abilities.CYBERNETIC_HAND_DAMAGE.location()
                )
        );
        context.register(
                Abilities.EXTEND_GRIP_BLOCK_REACH,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                1.75d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.BLOCK_INTERACTION_RANGE
                        ),
                        Abilities.EXTEND_GRIP_BLOCK_REACH.location()
                )
        );
        context.register(
                Abilities.AUTOPILOT_STEP_ASSIST,
                Ability.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                0.5d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.STEP_HEIGHT
                        ),
                        Abilities.AUTOPILOT_STEP_ASSIST.location()
                )
        );
    }
}
