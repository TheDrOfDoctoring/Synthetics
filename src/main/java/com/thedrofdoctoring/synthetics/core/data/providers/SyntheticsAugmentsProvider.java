package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAbility;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAugment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.stream.Collectors;

public class SyntheticsAugmentsProvider {

    public static final ResourceKey<SyntheticAugment> CYBERNETIC_INERTIAL_DAMPENERS = create("inertial_dampeners");
    public static final ResourceKey<SyntheticAugment> LAUNCH_BOOT = create("launch_boot");

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
                CYBERNETIC_INERTIAL_DAMPENERS,
                SyntheticAugment.create(
                        5,
                        0,
                        getPart(partLookup, BodyParts.FEET_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.INERTIAL_DAMPENERS_FALL_DAMAGE, Abilities.INERTIAL_DAMPENERS_SAFE_FALL)),
                        CYBERNETIC_INERTIAL_DAMPENERS.location()
                )
        );
        context.register(
                LAUNCH_BOOT,
                SyntheticAugment.create(
                        3,
                        0,
                        getPart(partLookup, BodyParts.FEET_MAIN),
                        getAbility(abilityLookup, List.of(Abilities.LAUNCHBOOT_LAUNCH)),
                        LAUNCH_BOOT.location()
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
