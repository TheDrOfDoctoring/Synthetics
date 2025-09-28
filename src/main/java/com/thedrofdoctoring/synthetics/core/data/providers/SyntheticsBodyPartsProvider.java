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
                BodyPart.create(
                        5,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.EYES),
                        BodyParts.ORGANIC_EYES.location()
                )
        );
        context.register(
                BodyParts.CYBERNETIC_EYES,
                BodyPart.create(
                        4,
                        getSegment(lookup, BodySegments.HEAD_MAIN),
                        getPartType(types, BodyParts.EYES),
                        BodyParts.CYBERNETIC_EYES.location()
                )
        );
        context.register(
                BodyParts.ORGANIC_FEET,
                BodyPart.create(
                        5,
                        getSegment(lookup, BodySegments.LOWER_BODY_MAIN),
                        getPartType(types, BodyParts.FEET),
                        BodyParts.ORGANIC_FEET.location()
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
        context.register(
                BodyParts.HANDS,
                new BodyPartType(
                        BodyParts.ORGANIC_HANDS,
                        BodyParts.HANDS.location()
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
