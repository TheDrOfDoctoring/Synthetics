package com.thedrofdoctoring.synthetics.core.data.collections;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPartType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class BodyParts {

    public static final ResourceKey<BodyPart> ORGANIC_EYES = create("organic_eyes");
    public static final ResourceKey<BodyPart> CYBERNETIC_FEET = create("cybernetic_feet");
    public static final ResourceKey<BodyPart> ORGANIC_FEET = create("organic_feet");
    public static final ResourceKey<BodyPart> ORGANIC_HEART = create("organic_heart");
    public static final ResourceKey<BodyPart> ORGANIC_LUNGS = create("organic_lungs");
    public static final ResourceKey<BodyPart> ORGANIC_BRAIN = create("organic_brain");
    public static final ResourceKey<BodyPart> ORGANIC_HANDS = create("organic_hands");
    public static final ResourceKey<BodyPart> ORGANIC_TISSUE = create("organic_tissue");
    public static final ResourceKey<BodyPart> CYBERNETIC_TISSUE = create("cybernetic_tissue");
    public static final ResourceKey<BodyPart> ORGANIC_STOMACH = create("organic_stomach");
    public static final ResourceKey<BodyPart> ORGANIC_RIBCAGE = create("organic_ribcage");
    public static final ResourceKey<BodyPart> ORGANIC_SKULL = create("organic_skull");
    public static final ResourceKey<BodyPart> ORGANIC_TIBIA = create("organic_tibia");
    public static final ResourceKey<BodyPart> CYBERNETIC_HANDS = create("cybernetic_hands");



    public static final ResourceKey<BodyPartType> EYES = createType("eyes");
    public static final ResourceKey<BodyPartType> FEET = createType("feet");
    public static final ResourceKey<BodyPartType> HEART = createType("heart");
    public static final ResourceKey<BodyPartType> LUNGS = createType("lungs");
    public static final ResourceKey<BodyPartType> BRAIN = createType("brain");
    public static final ResourceKey<BodyPartType> HANDS = createType("hands");
    public static final ResourceKey<BodyPartType> TISSUE =  createType("tissue");
    public static final ResourceKey<BodyPartType> STOMACH =  createType("stomach");
    public static final ResourceKey<BodyPartType> RIBCAGE =  createType("ribcage");
    public static final ResourceKey<BodyPartType> SKULL =  createType("skull");
    public static final ResourceKey<BodyPartType> TIBIA =  createType("tibia");


    public static final TagKey<BodyPart> EYES_MAIN = tag("eyes");
    public static final TagKey<BodyPart> TISSUE_MAIN = tag("tissue");
    public static final TagKey<BodyPart> LUNGS_MAIN = tag("lungs");
    public static final TagKey<BodyPart> FEET_MAIN = tag("feet");
    public static final TagKey<BodyPart> HEART_MAIN = tag("heart");
    public static final TagKey<BodyPart> HANDS_MAIN = tag("hands");
    public static final TagKey<BodyPart> TIBIA_MAIN = tag("tibia");
    public static final TagKey<BodyPart> SKULL_MAIN = tag("skull");
    public static final TagKey<BodyPart> RIBCAGE_MAIN = tag("ribcage");


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
