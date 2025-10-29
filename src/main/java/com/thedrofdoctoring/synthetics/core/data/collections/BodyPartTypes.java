package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.minecraft.resources.ResourceKey;

public class BodyPartTypes {

    public static final ResourceKey<BodyPartType> HEART = createType("heart");
    public static final ResourceKey<BodyPartType> LUNGS = createType("lungs");
    public static final ResourceKey<BodyPartType> BRAIN = createType("brain");
    public static final ResourceKey<BodyPartType> TISSUE =  createType("tissue");
    public static final ResourceKey<BodyPartType> STOMACH =  createType("stomach");
    public static final ResourceKey<BodyPartType> RIBCAGE =  createType("ribcage");
    public static final ResourceKey<BodyPartType> SKULL =  createType("skull");
    public static final ResourceKey<BodyPartType> TIBIA =  createType("tibia");
    public static final ResourceKey<BodyPartType> ARM_MUSCLE =  createType("arm_muscle");
    public static final ResourceKey<BodyPartType> LEFT_HAND = createType("left_hand");
    public static final ResourceKey<BodyPartType> RIGHT_HAND = createType("right_hand");
    public static final ResourceKey<BodyPartType> LEFT_EYE = createType("left_eye");
    public static final ResourceKey<BodyPartType> RIGHT_EYE = createType("right_eye");
    public static final ResourceKey<BodyPartType> LEFT_FOOT = createType("left_fot");
    public static final ResourceKey<BodyPartType> RIGHT_FOOT = createType("right_foot");


    private static ResourceKey<BodyPartType> createType(String name) {
        return ResourceKey.create(
                SyntheticsData.BODY_PART_TYPES,
                Synthetics.rl(name)
        );
    }

}
