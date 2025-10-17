package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.body.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.AttributeAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.types.body.ActiveAbilityOptions;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SyntheticsAbilitiesProvider {


    public static void createAbilities(BootstrapContext<SyntheticAbility> context) {

        context.register(
                Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE,
                SyntheticAbility.create(
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
                SyntheticAbility.create(
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
                SyntheticAbility.create(
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
                SyntheticAbility.create(
                        SyntheticAbilities.BATTERY.get(),
                        PassiveAbilityType.create(1000d),
                        Abilities.HEART_BATTERY.location()
                )
        );
        context.register(
                Abilities.TISSUE_SOLAR_POWER,
                SyntheticAbility.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        PassiveAbilityType.create(10d),
                        Abilities.TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.ADVANCED_TISSUE_SOLAR_POWER,
                SyntheticAbility.create(
                        SyntheticAbilities.SOLAR_GENERATOR.get(),
                        PassiveAbilityType.create(30d),
                        Abilities.ADVANCED_TISSUE_SOLAR_POWER.location()
                )
        );
        context.register(
                Abilities.VISION_CLARIFIER_VIEW,
                SyntheticAbility.create(
                        SyntheticAbilities.UNDERWATER_VISION.get(),
                        PassiveAbilityType.create(15d),
                        Abilities.VISION_CLARIFIER_VIEW.location()
                )
        );
        context.register(
                Abilities.RESPIRATOR_BREATH,
                SyntheticAbility.create(
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
                SyntheticAbility.create(
                        SyntheticAbilities.FOOD_GENERATOR.get(),
                        PassiveAbilityType.create(250d),
                        Abilities.METABOLIC_CONVERTER.location()
                )
        );
        context.register(
                Abilities.HAND_WALL_CLIMB,
                SyntheticAbility.create(
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
                SyntheticAbility.create(
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
                SyntheticAbility.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                0.35d,
                                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                                Attributes.MOVEMENT_SPEED
                        ),
                        Abilities.INTEGRATED_EXOSKELETON_WALK.location()
                )
        );
        context.register(
                Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS,
                SyntheticAbility.create(
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
                SyntheticAbility.create(
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
                SyntheticAbility.create(
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
                SyntheticAbility.create(
                        SyntheticAbilities.ATTRIBUTE_ABILITY.get(),
                        AttributeAbilityType.create(
                                1.75d,
                                AttributeModifier.Operation.ADD_VALUE,
                                Attributes.BLOCK_INTERACTION_RANGE
                        ),
                        Abilities.EXTEND_GRIP_BLOCK_REACH.location()
                )
        );
    }
}
