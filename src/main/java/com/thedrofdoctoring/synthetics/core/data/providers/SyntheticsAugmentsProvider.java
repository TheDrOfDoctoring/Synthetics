package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.tags.AugmentTags;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.Augment;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.stream.Collectors;

public class SyntheticsAugmentsProvider {

    public static void createAugments(BootstrapContext<Augment> context) {

        HolderGetter<BodyPart> partLookup = context.lookup(SyntheticsData.BODY_PARTS);
        HolderGetter<Ability> abilityLookup = context.lookup(SyntheticsData.ABILITIES);


        register(context,
                Augment.Builder.of(context, Augments.CYBERNETIC_INERTIAL_DAMPENERS, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(5)
                        .groupedWith(AugmentTags.DAMPENERS)
                        .maxCopies(2, 1)
                        .abilities(
                                Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE,
                                Abilities.INERTIAL_DAMPENERS_SAFE_FALL
                        )
        );
        register(context,
                Augment.Builder.of(context, Augments.BASIC_INERTIAL_DAMPENERS, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(1)
                        .maxCopies(2, 1)
                        .groupedWith(AugmentTags.DAMPENERS)
                        .abilities(
                                Abilities.BASIC_DAMPENERS_FALL_DAMAGE,
                                Abilities.BASIC_DAMPENERS_SAFE_FALL
                        )
        );
        register(context,
                Augment.Builder.of(context, Augments.MECHANICAL_INERTIAL_DAMPENERS, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .groupedWith(AugmentTags.DAMPENERS)
                        .maxCopies(2, 1)
                        .abilities(
                                Abilities.MECHANICAL_DAMPENERS_FALL_DAMAGE,
                                Abilities.MECHANICAL_DAMPENERS_SAFE_FALL
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.LAUNCH_BOOT, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(
                                Abilities.LAUNCHBOOT_LAUNCH
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.HEART_BATTERY, getPart(partLookup, BodyParts.HEART_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(2, 2)
                        .abilities(
                                Abilities.HEART_BATTERY
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.SOLAR_TISSUE, getPart(partLookup, BodyParts.TISSUE_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(5, 2)
                        .abilities(
                                Abilities.TISSUE_SOLAR_POWER
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.ADVANCED_SOLAR_TISSUE, getPart(partLookup, BodyParts.CYBERNETIC_TISSUE))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(5, 2)
                        .abilities(
                                Abilities.ADVANCED_TISSUE_SOLAR_POWER
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.VISION_CLARIFIER, getPart(partLookup, BodyParts.EYES_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(
                                Abilities.VISION_CLARIFIER_VIEW
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.INTEGRATED_RESPIRATOR, getPart(partLookup, BodyParts.LUNGS_MAIN))
                        .complexity(3)
                        .powerCost(2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.RESPIRATOR_BREATH
                        )))
        );

        register(context,
                Augment.Builder.of(context, Augments.METABOLIC_CONVERTER, getPart(partLookup, BodyParts.ORGANIC_STOMACH))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(
                                Abilities.METABOLIC_CONVERTER
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.MAGNETIC_FEET_IMPLANTS, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .abilities(
                                Abilities.BASIC_WALL_CLIMB
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.INTEGRATED_EXOSKELETON, getPart(partLookup, BodyParts.TIBIA_MAIN))
                        .complexity(2)
                        .powerCost(2)
                        .maxCopies(2, 2)
                        .abilities(
                                Abilities.INTEGRATED_EXOSKELETON_SWIM,
                                Abilities.INTEGRATED_EXOSKELETON_WALK
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.INTERNAL_PLATING, getPart(partLookup, BodyParts.ALL_BONES))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(3, 1)
                        .abilities(
                                Abilities.INTERNAL_PLATING_KNOCKBACK,
                                Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.EXTENDED_GRIP, getPart(partLookup, BodyParts.NON_ORGANIC_HANDS))
                        .complexity(3)
                        .powerCost(2)
                        .abilities(
                                Abilities.EXTEND_GRIP_BLOCK_REACH
                        )
        );

        register(context,
                Augment.Builder.of(context, Augments.MOTION_AUTOPILOT, getPart(partLookup, BodyParts.BRAINS_MAIN))
                        .complexity(3)
                        .powerCost(5)
                        .abilities(
                                Abilities.AUTOPILOT_STEP_ASSIST
                        )
        );
        register(context,
                Augment.Builder.of(context, Augments.UNIVERSAL_TRANSLATOR, getPart(partLookup, BodyParts.SKULL_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(
                                Abilities.UNIVERSAL_TRANSLATOR_TRADE_PRICES
                        )
        );
        register(context,
                Augment.Builder.of(context, Augments.HAND_FLAMETHROWER, getPart(partLookup, BodyParts.NON_ORGANIC_HANDS))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(Abilities.HAND_FLAMETHROWER)
        );
        register(context,
                Augment.Builder.of(context, Augments.INTEGRATED_REDSTONE_LINK, getPart(partLookup, BodyParts.BRAINS_MAIN))
                        .complexity(3)
                        .powerCost(1)
                        .abilities(Abilities.CYBERNETIC_REDSTONE_LINK)
        );
        register(context,
                Augment.Builder.of(context, Augments.INTERNAL_CAMERA_LINK, getPart(partLookup, BodyParts.EYES_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(Abilities.CYBERNETIC_CAMERA_LINK)
        );
        register(context,
                Augment.Builder.of(context, Augments.HAND_REPULSOR, getPart(partLookup, BodyParts.NON_ORGANIC_HANDS))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(Abilities.HAND_REPULSOR)
        );
        register(context,
                Augment.Builder.of(context, Augments.AUXILIARY_VIEWLINK, getPart(partLookup, BodyParts.EYES_MAIN))
                        .complexity(3)
                        .powerCost(2)
                        .abilities(Abilities.VIEWLINK_BLINDNESS_IMMUNITY)
        );
        register(context,
                Augment.Builder.of(context, Augments.REINFORCED_TENDONS, getPart(partLookup, BodyParts.ARM_MUSCLE_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(3, 2)
                        .abilities(Abilities.REINFORCED_TENDONS_MINING)
        );
        register(context,
                Augment.Builder.of(context, Augments.SYNTHETIC_LINING, getPart(partLookup, BodyParts.STOMACH_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(2, 2)
                        .abilities(Abilities.SYNTHETIC_LINING_EXHAUSTION)
        );
        register(context,
                Augment.Builder.of(context, Augments.RESERVE_BLOOD_TANK, getPart(partLookup, BodyParts.HEART_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(3, 3)
                        .abilities(Abilities.BLOOD_TANK_HEALTH)
        );
        register(context,
                Augment.Builder.of(context, Augments.NEURAL_CHAMBER, getPart(partLookup, BodyParts.BRAINS_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(1, 1)
                        .abilities(Abilities.NEURAL_CHAMBER_EXPERIENCE)
        );
        register(context,
                Augment.Builder.of(context, Augments.SHIMMER_LAYER, getPart(partLookup, BodyParts.TISSUE_MAIN))
                        .complexity(4)
                        .powerCost(0)
                        .maxCopies(1, 1)
                        .abilities(Abilities.SHIMMER_INVISIBILITY)
        );
        register(context,
                Augment.Builder.of(context, Augments.REPAIRING_TISSUE, getPart(partLookup, BodyParts.TISSUE_MAIN))
                        .complexity(4)
                        .powerCost(0)
                        .maxCopies(2, 2)
                        .abilities(Abilities.HEALING_TISSUE)
        );
        register(context,
                Augment.Builder.of(context, Augments.FIRE_RESISTANT_TISSUE, getPart(partLookup, BodyParts.TISSUE_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(2, 2)
                        .abilities(Abilities.FIRE_RESISTANT_TISSUE)
        );
    }

    private static void register(BootstrapContext<Augment> context, Augment.Builder builder) {
        context.register(builder.key(), builder.build());
    }


    public static HolderSet<BodyPart> getPart(HolderGetter<BodyPart> lookup, ResourceKey<BodyPart> part) {
        return HolderSet.direct(lookup.getOrThrow(part));
    }
    public static HolderSet<BodyPart> getPart(HolderGetter<BodyPart> lookup, TagKey<BodyPart> part) {
        return lookup.getOrThrow(part);
    }

    public static HolderSet<Ability> getAbility(HolderGetter<Ability> lookup, List<ResourceKey<Ability>> abilities) {

        List<Holder<Ability>> abilityHolders = abilities.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(abilityHolders);
    }
}
