package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPartType;
import com.thedrofdoctoring.synthetics.core.data.types.BodySegment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public class SyntheticsBodyPartsProvider {


    public static void createBodyParts(BootstrapContext<BodyPart> context) {
        HolderGetter<BodySegment> lookup = context.lookup(SyntheticsData.BODY_SEGMENTS);
        HolderGetter<BodyPartType> types = context.lookup(SyntheticsData.BODY_PART_TYPES);

        context.register(
                BodyParts.ORGANIC_EYES,
                new BodyPart(
                        5,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.EYES),
                        BodyParts.ORGANIC_EYES.location()
                )
        );
        context.register(
                BodyParts.CYBERNETIC_EYES,
                new BodyPart(
                        4,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.EYES),
                        BodyParts.CYBERNETIC_EYES.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_FEET,
                new BodyPart(
                        5,
                        getSegment(lookup, BodySegments.LOWER_BODY_MAIN),
                        getPartType(types, BodyParts.FEET),
                        BodyParts.ORGANIC_FEET.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_BRAIN,
                new BodyPart(
                        8,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.BRAIN),
                        BodyParts.ORGANIC_BRAIN.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_LUNGS,
                new BodyPart(
                        5,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.LUNGS),
                        BodyParts.ORGANIC_LUNGS.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_HEART,
                new BodyPart(
                        3,
                        getSegment(lookup, BodySegments.TORSO_MAIN),
                        getPartType(types, BodyParts.HEART),
                        BodyParts.ORGANIC_HEART.location()
                )
        );
    }
    public static void createBodyPartTypes(BootstrapContext<BodyPartType> context) {
        context.register(
                BodyParts.EYES,
                new BodyPartType(
                        BodyParts.ORGANIC_EYES,
                        BodyParts.EYES.location()
                )
        );
        context.register(
                BodyParts.FEET,
                new BodyPartType(
                        BodyParts.ORGANIC_FEET,
                        BodyParts.FEET.location()
                )
        );
        context.register(
                BodyParts.BRAIN,
                new BodyPartType(
                        BodyParts.ORGANIC_BRAIN,
                        BodyParts.BRAIN.location()
                )
        );
        context.register(
                BodyParts.HEART,
                new BodyPartType(
                        BodyParts.ORGANIC_HEART,
                        BodyParts.HEART.location()
                )
        );
        context.register(
                BodyParts.LUNGS,
                new BodyPartType(
                        BodyParts.ORGANIC_LUNGS,
                        BodyParts.LUNGS.location()
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
}
