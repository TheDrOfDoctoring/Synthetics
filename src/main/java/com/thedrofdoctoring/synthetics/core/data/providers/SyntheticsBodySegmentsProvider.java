package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.client.renderers.installables.BodyPosition;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class SyntheticsBodySegmentsProvider {

    public static void createBodySegments(BootstrapContext<BodySegment> context) {

        HolderGetter<BodySegmentType> types = context.lookup(SyntheticsData.BODY_SEGMENT_TYPES);

        context.register(
                BodySegments.ORGANIC_TORSO,
                new BodySegment(
                        1000,
                        getSegmentType(types, BodySegments.TORSO),
                        BodySegments.ORGANIC_TORSO.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_HEAD,
                new BodySegment(
                        1000,
                        getSegmentType(types, BodySegments.HEAD),
                        BodySegments.ORGANIC_HEAD.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_ARMS,
                new BodySegment(
                        1000,
                        getSegmentType(types, BodySegments.ARMS),
                        BodySegments.ORGANIC_ARMS.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_LOWER_BODY,
                new BodySegment(
                        1000,
                        getSegmentType(types, BodySegments.LOWER_BODY),
                        BodySegments.ORGANIC_LOWER_BODY.location()
                )
        );
    }
    public static void createBodySegmentTypes(BootstrapContext<BodySegmentType> context) {
        context.register(
                BodySegments.HEAD,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_HEAD),
                        BodySegments.HEAD.location(),
                        BodyPosition.HEAD
                )
        );
        context.register(
                BodySegments.TORSO,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_TORSO),
                        BodySegments.TORSO.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodySegments.ARMS,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_ARMS),
                        BodySegments.ARMS.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodySegments.LOWER_BODY,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_LOWER_BODY),
                        BodySegments.LOWER_BODY.location(),
                        BodyPosition.BODY
                )
        );

    }

    private static Holder<BodySegment> getSegment(BootstrapContext<?> context, ResourceKey<BodySegment> segment) {
        return context.lookup(SyntheticsData.BODY_SEGMENTS).getOrThrow(segment);
    }

    private static Holder<BodySegmentType> getSegmentType(HolderGetter<BodySegmentType> lookup, ResourceKey<BodySegmentType> partType) {
        return lookup.getOrThrow(partType);
    }


}
