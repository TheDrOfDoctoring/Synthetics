package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class BodyParts {

    public static final ResourceKey<BodyPart> CYBERNETIC_LEFT_FOOT = create("cybernetic_left_foot");
    public static final ResourceKey<BodyPart> ORGANIC_LEFT_FOOT = create("organic_left_foot");
    public static final ResourceKey<BodyPart> CYBERNETIC_RIGHT_FOOT = create("cybernetic_right_foot");
    public static final ResourceKey<BodyPart> ORGANIC_RIGHT_FOOT = create("organic_right_foot");
    public static final ResourceKey<BodyPart> CYBERNETIC_LEFT_HAND = create("cybernetic_left_hand");
    public static final ResourceKey<BodyPart> ORGANIC_LEFT_HAND = create("organic_left_hand");
    public static final ResourceKey<BodyPart> CYBERNETIC_RIGHT_HAND = create("cybernetic_right_hand");
    public static final ResourceKey<BodyPart> ORGANIC_RIGHT_HAND = create("organic_right_hand");
    public static final ResourceKey<BodyPart> ORGANIC_LEFT_EYE = create("organic_left_eye");
    public static final ResourceKey<BodyPart> ORGANIC_RIGHT_EYE = create("organic_right_eye");
    public static final ResourceKey<BodyPart> ORGANIC_HEART = create("organic_heart");
    public static final ResourceKey<BodyPart> ORGANIC_LUNGS = create("organic_lungs");
    public static final ResourceKey<BodyPart> ORGANIC_BRAIN = create("organic_brain");
    public static final ResourceKey<BodyPart> ORGANIC_TISSUE = create("organic_tissue");
    public static final ResourceKey<BodyPart> CYBERNETIC_TISSUE = create("cybernetic_tissue");
    public static final ResourceKey<BodyPart> ORGANIC_STOMACH = create("organic_stomach");
    public static final ResourceKey<BodyPart> ORGANIC_RIBCAGE = create("organic_ribcage");
    public static final ResourceKey<BodyPart> ORGANIC_SKULL = create("organic_skull");
    public static final ResourceKey<BodyPart> ORGANIC_TIBIA = create("organic_tibia");
    public static final ResourceKey<BodyPart> ORGANIC_ARM_MUSCLE = create("organic_arm_muscle");

    public static final TagKey<BodyPart> EYES_MAIN = tag("eyes");
    public static final TagKey<BodyPart> TISSUE_MAIN = tag("tissue");
    public static final TagKey<BodyPart> LUNGS_MAIN = tag("lungs");
    public static final TagKey<BodyPart> FEET_MAIN = tag("feet");
    public static final TagKey<BodyPart> HEART_MAIN = tag("heart");
    public static final TagKey<BodyPart> HANDS_MAIN = tag("hands");
    public static final TagKey<BodyPart> TIBIA_MAIN = tag("tibia");
    public static final TagKey<BodyPart> SKULL_MAIN = tag("skull");
    public static final TagKey<BodyPart> RIBCAGE_MAIN = tag("ribcage");
    public static final TagKey<BodyPart> ARM_MUSCLE_MAIN = tag("arm_muscle");


    public static final TagKey<BodyPart> ALL_BONES = tag("bones");
    public static final TagKey<BodyPart> ALL_MUSCLES = tag("muscles");



    private static ResourceKey<BodyPart> create(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_PARTS,
                Synthetics.rl(name)
        );
    }

    private static @NotNull TagKey<BodyPart> tag(@NotNull String name) {
        return TagKey.create(SyntheticsData.BODY_PARTS, Synthetics.rl(name));
    }

}
