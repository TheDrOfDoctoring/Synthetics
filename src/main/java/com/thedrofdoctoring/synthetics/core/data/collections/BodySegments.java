package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class BodySegments {

    public static final ResourceKey<BodySegmentType> TORSO = createType("torso");
    public static final ResourceKey<BodySegmentType> RIGHT_LEG = createType("right_leg");
    public static final ResourceKey<BodySegmentType> LEFT_LEG = createType("left_leg");
    public static final ResourceKey<BodySegmentType> HEAD = createType("head");
    public static final ResourceKey<BodySegmentType> LEFT_ARM = createType("arms");
    public static final ResourceKey<BodySegmentType> RIGHT_ARM = createType("left_arm");

    public static final ResourceKey<BodySegment> ORGANIC_TORSO = create("organic_torso");
    public static final ResourceKey<BodySegment> ORGANIC_RIGHT_LEG = create("organic_right_leg");
    public static final ResourceKey<BodySegment> ORGANIC_LEFT_LEG = create("organic_left_leg");
    public static final ResourceKey<BodySegment> ORGANIC_HEAD = create("organic_head");
    public static final ResourceKey<BodySegment> ORGANIC_LEFT_ARM = create("organic_left_arm");
    public static final ResourceKey<BodySegment> ORGANIC_RIGHT_ARM = create("organic_right_arm");


    public static final TagKey<BodySegment> TORSO_MAIN = tag("torso");
    public static final TagKey<BodySegment> LEFT_LEG_MAIN = tag("left_leg");
    public static final TagKey<BodySegment> RIGHT_LEG_MAIN = tag("right_leg");
    public static final TagKey<BodySegment> HEAD_MAIN = tag("head");
    public static final TagKey<BodySegment> LEFT_ARM_MAIN = tag("left_arm");
    public static final TagKey<BodySegment> RIGHT_ARM_MAIN = tag("right_arm");


    private static @NotNull TagKey<BodySegment> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.BODY_SEGMENTS, Synthetics.rl(name));
    }

    private static ResourceKey<BodySegment> create(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_SEGMENTS,
                Synthetics.rl(name)
        );
    }
    private static ResourceKey<BodySegmentType> createType(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_SEGMENT_TYPES,
                Synthetics.rl(name)
        );
    }
}
