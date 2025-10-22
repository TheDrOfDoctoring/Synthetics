package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.AugmentBuilder;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.Augment;
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
                AugmentBuilder.of(Augments.CYBERNETIC_INERTIAL_DAMPENERS, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(1)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE,
                                Abilities.INERTIAL_DAMPENERS_SAFE_FALL
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.LAUNCH_BOOT, getPart(partLookup, BodyParts.FEET_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.LAUNCHBOOT_LAUNCH
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.HEART_BATTERY, getPart(partLookup, BodyParts.HEART_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(2, 2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.HEART_BATTERY
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.SOLAR_TISSUE, getPart(partLookup, BodyParts.TISSUE_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(5, 2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.TISSUE_SOLAR_POWER
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.ADVANCED_SOLAR_TISSUE, getPart(partLookup, BodyParts.CYBERNETIC_TISSUE))
                        .complexity(3)
                        .powerCost(0)
                        .maxCopies(5, 2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.ADVANCED_TISSUE_SOLAR_POWER
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.VISION_CLARIFIER, getPart(partLookup, BodyParts.EYES_MAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.VISION_CLARIFIER_VIEW
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.INTEGRATED_RESPIRATOR, getPart(partLookup, BodyParts.LUNGS_MAIN))
                        .complexity(3)
                        .powerCost(2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.RESPIRATOR_BREATH
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.METABOLIC_CONVERTER, getPart(partLookup, BodyParts.ORGANIC_STOMACH))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.METABOLIC_CONVERTER
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.EMITTABLE_ADHESIVE, getPart(partLookup, BodyParts.HANDS_MAIN))
                        .complexity(2)
                        .powerCost(0)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.HAND_WALL_CLIMB
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.INTEGRATED_EXOSKELETON, getPart(partLookup, BodyParts.TIBIA_MAIN))
                        .complexity(2)
                        .powerCost(2)
                        .maxCopies(2, 2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.INTEGRATED_EXOSKELETON_SWIM,
                                Abilities.INTEGRATED_EXOSKELETON_WALK
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.INTERNAL_PLATING, getPart(partLookup, BodyParts.ALL_BONES))
                        .complexity(2)
                        .powerCost(0)
                        .maxCopies(3, 1)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.INTERNAL_PLATING_KNOCKBACK,
                                Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.EXTENDED_GRIP, getPart(partLookup, BodyParts.HANDS_MAIN))
                        .complexity(3)
                        .powerCost(2)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.EXTEND_GRIP_BLOCK_REACH
                        )))
        );

        register(context,
                AugmentBuilder.of(Augments.MOTION_AUTOPILOT, getPart(partLookup, BodyParts.ORGANIC_BRAIN))
                        .complexity(3)
                        .powerCost(0)
                        .abilities(getAbility(abilityLookup, List.of(
                                Abilities.AUTOPILOT_STEP_ASSIST
                        )))
        );
    }

    private static void register(BootstrapContext<Augment> context, AugmentBuilder builder) {
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
