package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.Augments;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.stream.Collectors;

public class SyntheticsAugmentsProvider {



    private static ResourceKey<SyntheticAugment> create(String name) {
        return ResourceKey.create(
                SyntheticsData.AUGMENTS,
                Synthetics.rl(name)
        );
    }

    public static void createAugments(BootstrapContext<SyntheticAugment> context) {

        HolderGetter<BodyPart> partLookup = context.lookup(SyntheticsData.BODY_PARTS);
        HolderGetter<SyntheticAbility> abilityLookup = context.lookup(SyntheticsData.ABILITIES);


        context.register(
                Augments.CYBERNETIC_INERTIAL_DAMPENERS,
                SyntheticAugment.create(
                        3,
                        1,
                        getPart(partLookup, BodyParts.FEET_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE, Abilities.INERTIAL_DAMPENERS_SAFE_FALL)),
                        Augments.CYBERNETIC_INERTIAL_DAMPENERS.location()
                )
        );
        context.register(
                Augments.LAUNCH_BOOT,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.FEET_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.LAUNCHBOOT_LAUNCH)),
                        Augments.LAUNCH_BOOT.location()
                )
        );
        context.register(
                Augments.HEART_BATTERY,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.HEART_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.HEART_BATTERY)),
                        Augments.HEART_BATTERY.location()
                )
        );
        context.register(
                Augments.SOLAR_TISSUE,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.TISSUE_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.TISSUE_SOLAR_POWER)),
                        Augments.SOLAR_TISSUE.location()
                )
        );
        context.register(
                Augments.ADVANCED_SOLAR_TISSUE,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.CYBERNETIC_TISSUE),
                        getAbility(abilityLookup, List.of(Abilities.ADVANCED_TISSUE_SOLAR_POWER)),
                        Augments.ADVANCED_SOLAR_TISSUE.location()
                )
        );
        context.register(
                Augments.VISION_CLARIFIER,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.EYES_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.VISION_CLARIFIER_VIEW)),
                        Augments.VISION_CLARIFIER.location()
                )
        );
        context.register(
                Augments.INTEGRATED_RESPIRATOR,
                SyntheticAugment.create(
                        3,
                        2,
                        getPart(partLookup, BodyParts.LUNGS_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.RESPIRATOR_BREATH)),
                        Augments.INTEGRATED_RESPIRATOR.location()
                )
        );
        context.register(
                Augments.METABOLIC_CONVERTER,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.ORGANIC_STOMACH),
                        getAbility(abilityLookup, List.of(Abilities.METABOLIC_CONVERTER)),
                        Augments.METABOLIC_CONVERTER.location()
                )
        );
        context.register(
                Augments.EMITTABLE_ADHESIVE,
                SyntheticAugment.create(
                        2,
                        0,
                        getPart(partLookup, BodyParts.HANDS_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.HAND_WALL_CLIMB)),
                        Augments.EMITTABLE_ADHESIVE.location()
                )
        );
        context.register(
                Augments.INTEGRATED_EXOSKELETON,
                SyntheticAugment.create(
                        4,
                        1,
                        getPart(partLookup, BodyParts.TIBIA_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.INTEGRATED_EXOSKELETON_SWIM, Abilities.INTEGRATED_EXOSKELETON_WALK)),
                        Augments.INTEGRATED_EXOSKELETON.location()
                )
        );
        context.register(
                Augments.INTERNAL_PLATING,
                SyntheticAugment.create(
                        2,
                        0,
                        getPart(partLookup, BodyParts.RIBCAGE_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.INTERNAL_PLATING_KNOCKBACK, Abilities.INTERNAL_PLATING_ARMOUR_TOUGHNESS)),
                        Augments.INTERNAL_PLATING.location()
                )
        );
        context.register(
                Augments.EXTENDED_GRIP,
                SyntheticAugment.create(
                        3,
                        2,
                        getPart(partLookup, BodyParts.HANDS_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.EXTEND_GRIP_BLOCK_REACH)),
                        Augments.EXTENDED_GRIP.location()
                )
        );
    }

    public static HolderSet<BodyPart> getPart(HolderGetter<BodyPart> lookup, ResourceKey<BodyPart> part) {
        return HolderSet.direct(lookup.getOrThrow(part));
    }
    public static HolderSet<BodyPart> getPart(HolderGetter<BodyPart> lookup, TagKey<BodyPart> part) {
        return lookup.getOrThrow(part);
    }

    public static HolderSet<SyntheticAbility> getAbility(HolderGetter<SyntheticAbility> lookup, ResourceKey<SyntheticAbility> ability) {
        return HolderSet.direct(lookup.getOrThrow(ability));
    }

    public static HolderSet<SyntheticAbility> getAbility(HolderGetter<SyntheticAbility> lookup, List<ResourceKey<SyntheticAbility>> abilities) {

        List<Holder<SyntheticAbility>> abilityHolders = abilities.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(abilityHolders);
    }

    public static HolderSet<SyntheticAbility> getAbility(HolderGetter<SyntheticAbility> lookup, TagKey<SyntheticAbility> ability) {
        return lookup.getOrThrow(ability);
    }
}
