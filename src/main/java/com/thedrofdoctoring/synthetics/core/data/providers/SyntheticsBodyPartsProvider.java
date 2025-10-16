package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.stream.Collectors;

public class SyntheticsBodyPartsProvider {


    public static void createBodyParts(BootstrapContext<BodyPart> context) {
        HolderGetter<BodySegment> lookup = context.lookup(SyntheticsData.BODY_SEGMENTS);
        HolderGetter<BodyPartType> types = context.lookup(SyntheticsData.BODY_PART_TYPES);
        HolderGetter<SyntheticAbility> abilityLookup = context.lookup(SyntheticsData.ABILITIES);

        context.register(
                BodyParts.ORGANIC_EYES,
                BodyPart.create(
                        5,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.EYES),
                        BodyParts.ORGANIC_EYES.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_FEET,
                BodyPart.create(
                        3,
                        getSegment(lookup, BodySegments.LOWER_BODY_MAIN),
                        getPartType(types, BodyParts.FEET),
                        BodyParts.ORGANIC_FEET.location()
                )
        );
        context.register(
                BodyParts.CYBERNETIC_FEET,
                BodyPart.create(
                        9,
                        getSegment(lookup, BodySegments.LOWER_BODY_MAIN),
                        getPartType(types, BodyParts.FEET),
                        BodyParts.CYBERNETIC_FEET.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_BRAIN,
                BodyPart.create(
                        8,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.BRAIN),
                        BodyParts.ORGANIC_BRAIN.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_LUNGS,
                BodyPart.create(
                        5,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.LUNGS),
                        BodyParts.ORGANIC_LUNGS.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_HEART,
                BodyPart.create(
                        3,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.HEART),
                        BodyParts.ORGANIC_HEART.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_HANDS,
                BodyPart.create(
                        4,
                        getSegment(lookup, BodySegments.ARMS_MAIN),
                        getPartType(types, BodyParts.HANDS),
                        BodyParts.ORGANIC_HANDS.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_TISSUE,
                BodyPart.create(
                        6,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.TISSUE),
                        BodyParts.ORGANIC_TISSUE.location()
                )
        );
        context.register(
                BodyParts.CYBERNETIC_TISSUE,
                BodyPart.create(
                        12,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.TISSUE),
                        BodyParts.CYBERNETIC_TISSUE.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_STOMACH,
                BodyPart.create(
                        6,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.STOMACH),
                        BodyParts.ORGANIC_STOMACH.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_RIBCAGE,
                BodyPart.create(
                        4,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.RIBCAGE),
                        BodyParts.ORGANIC_RIBCAGE.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_SKULL,
                BodyPart.create(
                        4,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.SKULL),
                        BodyParts.ORGANIC_SKULL.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_TIBIA,
                BodyPart.create(
                        4,
                        getSegment(lookup, BodySegments.LOWER_BODY_MAIN),
                        getPartType(types, BodyParts.TIBIA),
                        BodyParts.ORGANIC_TIBIA.location()
                )
        );
        context.register(
                BodyParts.CYBERNETIC_HANDS,
                BodyPart.create(
                        10,
                        getSegment(lookup, BodySegments.ARMS_MAIN),
                        getPartType(types, BodyParts.HANDS),
                        getAbility(abilityLookup, List.of(Abilities.CYBERNETIC_HAND_DAMAGE)),
                        BodyParts.CYBERNETIC_HANDS.location()
                )
        );
    }

    public static void createBodyPartTypes(BootstrapContext<BodyPartType> context) {
        context.register(
                BodyParts.TISSUE,
                new BodyPartType(
                        BodyParts.ORGANIC_TISSUE,
                        -18, 110,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.TISSUE.location()
                )
        );
        context.register(
                BodyParts.EYES,
                new BodyPartType(
                        BodyParts.ORGANIC_EYES,
                        -18, 28,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.EYES.location()
                )
        );
        context.register(
                BodyParts.FEET,
                new BodyPartType(
                        BodyParts.ORGANIC_FEET,
                        3, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.FEET.location()
                )
        );
        context.register(
                BodyParts.BRAIN,
                new BodyPartType(
                        BodyParts.ORGANIC_BRAIN,
                        3, 10,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.BRAIN.location()
                )
        );
        context.register(
                BodyParts.HEART,
                new BodyPartType(
                        BodyParts.ORGANIC_HEART,
                        10, 80,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.HEART.location()
                )
        );
        context.register(
                BodyParts.LUNGS,
                new BodyPartType(
                        BodyParts.ORGANIC_LUNGS,
                        -15, 90,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.LUNGS.location()
                )
        );
        context.register(
                BodyParts.HANDS,
                new BodyPartType(
                        BodyParts.ORGANIC_HANDS,
                        40, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.HANDS.location()
                )
        );
        context.register(
                BodyParts.STOMACH,
                new BodyPartType(
                        BodyParts.ORGANIC_STOMACH,
                        -15, 120,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.STOMACH.location()
                )
        );
        context.register(
                BodyParts.RIBCAGE,
                new BodyPartType(
                        BodyParts.ORGANIC_RIBCAGE,
                        -7, 100,
                        BodyPartType.Layer.BONE,
                        BodyParts.RIBCAGE.location()
                )
        );
        context.register(
                BodyParts.SKULL,
                new BodyPartType(
                        BodyParts.ORGANIC_SKULL,
                        -3, 35,
                        BodyPartType.Layer.BONE,
                        BodyParts.SKULL.location()
                )
        );
        context.register(
                BodyParts.TIBIA,
                new BodyPartType(
                        BodyParts.ORGANIC_TIBIA,
                        7, 180,
                        BodyPartType.Layer.BONE,
                        BodyParts.TIBIA.location()
                )
        );
    }

    public static HolderSet<BodySegment> getSegment(HolderGetter<BodySegment> lookup, ResourceKey<BodySegment> part) {
        return HolderSet.direct(lookup.getOrThrow(part));
    }
    public static HolderSet<BodySegment> getSegment(HolderGetter<BodySegment> lookup, TagKey<BodySegment> part) {
        return lookup.getOrThrow(part);
    }
    public static Holder<BodyPartType> getPartType(HolderGetter<BodyPartType> lookup, ResourceKey<BodyPartType> partType) {
        return lookup.getOrThrow(partType);
    }
    public static HolderSet<SyntheticAbility> getAbility(HolderGetter<SyntheticAbility> lookup, List<ResourceKey<SyntheticAbility>> abilities) {

        List<Holder<SyntheticAbility>> abilityHolders = abilities.stream().map(lookup::getOrThrow).collect(Collectors.toUnmodifiableList());
        return HolderSet.direct(abilityHolders);
    }

}
