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
                        2,
                        0,
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
