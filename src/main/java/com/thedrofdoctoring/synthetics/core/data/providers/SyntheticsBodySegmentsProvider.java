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
                BodySegments.ORGANIC_LEFT_ARM,
                new BodySegment(
                        500,
                        getSegmentType(types, BodySegments.LEFT_ARM),
                        BodySegments.ORGANIC_LEFT_ARM.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_RIGHT_ARM,
                new BodySegment(
                        500,
                        getSegmentType(types, BodySegments.RIGHT_ARM),
                        BodySegments.ORGANIC_RIGHT_ARM.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_LEFT_LEG,
                new BodySegment(
                        500,
                        getSegmentType(types, BodySegments.LEFT_LEG),
                        BodySegments.ORGANIC_LEFT_LEG.location()
                )
        );
        context.register(
                BodySegments.ORGANIC_RIGHT_LEG,
                new BodySegment(
                        500,
                        getSegmentType(types, BodySegments.RIGHT_LEG),
                        BodySegments.ORGANIC_RIGHT_LEG.location()
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
                BodySegments.LEFT_ARM,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_LEFT_ARM),
                        BodySegments.LEFT_ARM.location(),
                        BodyPosition.LEFT_ARM
                )
        );
        context.register(
                BodySegments.RIGHT_ARM,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_RIGHT_ARM),
                        BodySegments.RIGHT_ARM.location(),
                        BodyPosition.RIGHT_ARM
                )
        );
        context.register(
                BodySegments.LEFT_LEG,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_LEFT_LEG),
                        BodySegments.LEFT_LEG.location(),
                        BodyPosition.LEFT_LEG
                )
        );
        context.register(
                BodySegments.RIGHT_LEG,
                new BodySegmentType(
                        getSegment(context, BodySegments.ORGANIC_RIGHT_LEG),
                        BodySegments.RIGHT_LEG.location(),
                        BodyPosition.RIGHT_LEG
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
