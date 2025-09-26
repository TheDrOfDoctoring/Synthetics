package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.BodyPartType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class BodyParts {

    public static final ResourceKey<BodyPart> ORGANIC_EYES = create("organic_eyes");
    public static final ResourceKey<BodyPart> CYBERNETIC_EYES = create("cybernetic_eyes");
    public static final ResourceKey<BodyPart> ORGANIC_FEET = create("organic_feet");
    public static final ResourceKey<BodyPart> ORGANIC_HEART = create("organic_heart");
    public static final ResourceKey<BodyPart> ORGANIC_LUNGS = create("organic_lungs");
    public static final ResourceKey<BodyPart> ORGANIC_BRAIN = create("organic_brain");


    public static final ResourceKey<BodyPartType> EYES = createType("eyes");
    public static final ResourceKey<BodyPartType> FEET = createType("feet");
    public static final ResourceKey<BodyPartType> HEART = createType("heart");
    public static final ResourceKey<BodyPartType> LUNGS = createType("lungs");
    public static final ResourceKey<BodyPartType> BRAIN = createType("brain");


    public static final TagKey<BodyPart> EYES_MAIN = tag("eyes");
    public static final TagKey<BodyPart> FEET_MAIN = tag("feet");

    private static ResourceKey<BodyPart> create(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_PARTS,
                Synthetics.rl(name)
        );
    }
    private static ResourceKey<BodyPartType> createType(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_PART_TYPES,
                Synthetics.rl(name)
        );
    }
    private static @NotNull TagKey<BodyPart> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.BODY_PARTS, Synthetics.rl(name));
    }

}
