package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegment;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodySegmentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class BodySegments {

    public static final ResourceKey<BodySegmentType> TORSO = createType("torso");
    public static final ResourceKey<BodySegmentType> LOWER_BODY = createType("lower_body");
    public static final ResourceKey<BodySegmentType> HEAD = createType("head");
    public static final ResourceKey<BodySegmentType> ARMS = createType("arms");


    public static final ResourceKey<BodySegment> ORGANIC_TORSO = create("organic_torso");
    public static final ResourceKey<BodySegment> ORGANIC_LOWER_BODY = create("organic_lower_body");
    public static final ResourceKey<BodySegment> ORGANIC_HEAD = create("organic_head");
    public static final ResourceKey<BodySegment> ORGANIC_ARMS = create("organic_arms");

    public static final TagKey<BodySegment> TORSO_MAIN = tag("torso");
    public static final TagKey<BodySegment> LOWER_BODY_MAIN = tag("lower_body");
    public static final TagKey<BodySegment> HEAD_MAIN = tag("head");
    public static final TagKey<BodySegment> ARMS_MAIN = tag("arms");


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
