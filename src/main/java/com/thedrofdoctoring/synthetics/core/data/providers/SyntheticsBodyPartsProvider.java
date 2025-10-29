package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyPartTypes;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.minecraft.data.worldgen.BootstrapContext;

import java.util.List;

public class SyntheticsBodyPartsProvider {


    public static void createBodyParts(BootstrapContext<BodyPart> context) {

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_BRAIN)
                        .partType(BodyPartTypes.BRAIN)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LUNGS)
                        .partType(BodyPartTypes.LUNGS)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(5)
        );


        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_HEART)
                        .partType(BodyPartTypes.HEART)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(3)
        );


        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TISSUE)
                        .partType(BodyPartTypes.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_TISSUE)
                        .partType(BodyPartTypes.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(12)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_STOMACH)
                        .partType(BodyPartTypes.STOMACH)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIBCAGE)
                        .partType(BodyPartTypes.RIBCAGE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_SKULL)
                        .partType(BodyPartTypes.SKULL)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TIBIA)
                        .partType(BodyPartTypes.TIBIA)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_LEFT_HAND)
                        .partType(BodyPartTypes.LEFT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_HAND_DAMAGE))
                        .maxComplexity(5)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_RIGHT_HAND)
                        .partType(BodyPartTypes.RIGHT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_HAND_DAMAGE))
                        .maxComplexity(5)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_HAND)
                        .partType(BodyPartTypes.LEFT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(3)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_HAND)
                        .partType(BodyPartTypes.RIGHT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(3)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_EYE)
                        .partType(BodyPartTypes.LEFT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(3)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_EYE)
                        .partType(BodyPartTypes.RIGHT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(3)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_FOOT)
                        .partType(BodyPartTypes.LEFT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(3)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_FOOT)
                        .partType(BodyPartTypes.RIGHT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(3)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_LEFT_FOOT)
                        .partType(BodyPartTypes.LEFT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(5)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_RIGHT_FOOT)
                        .partType(BodyPartTypes.RIGHT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(5)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_ARM_MUSCLE)
                        .partType(BodyPartTypes.ARM_MUSCLE)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(4)
        );
    }

    private static void register(BootstrapContext<BodyPart> context, BodyPart.Builder builder) {
        context.register(builder.key(), builder.build());
    }


    public static void createBodyPartTypes(BootstrapContext<BodyPartType> context) {
        context.register(
                BodyPartTypes.TISSUE,
                new BodyPartType(
                        BodyParts.ORGANIC_TISSUE,
                        -18, 110,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.TISSUE.location()
                )
        );
        context.register(
                BodyPartTypes.LEFT_EYE,
                new BodyPartType(
                        BodyParts.ORGANIC_LEFT_EYE,
                        -15, 29,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.LEFT_EYE.location()
                )
        );
        context.register(
                BodyPartTypes.RIGHT_EYE,
                new BodyPartType(
                        BodyParts.ORGANIC_RIGHT_EYE,
                        9, 29,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.RIGHT_EYE.location()
                )
        );
        context.register(
                BodyPartTypes.RIGHT_FOOT,
                new BodyPartType(
                        BodyParts.ORGANIC_RIGHT_FOOT,
                        7, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.RIGHT_FOOT.location()
                )
        );
        context.register(
                BodyPartTypes.LEFT_FOOT,
                new BodyPartType(
                        BodyParts.ORGANIC_LEFT_FOOT,
                        -26, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.LEFT_FOOT.location()
                )
        );
        context.register(
                BodyPartTypes.BRAIN,
                new BodyPartType(
                        BodyParts.ORGANIC_BRAIN,
                        3, 10,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.BRAIN.location()
                )
        );
        context.register(
                BodyPartTypes.HEART,
                new BodyPartType(
                        BodyParts.ORGANIC_HEART,
                        10, 80,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.HEART.location()
                )
        );
        context.register(
                BodyPartTypes.LUNGS,
                new BodyPartType(
                        BodyParts.ORGANIC_LUNGS,
                        -15, 90,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.LUNGS.location()
                )
        );
        context.register(
                BodyPartTypes.RIGHT_HAND,
                new BodyPartType(
                        BodyParts.ORGANIC_RIGHT_HAND,
                        36, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.RIGHT_HAND.location()
                )
        );
        context.register(
                BodyPartTypes.LEFT_HAND,
                new BodyPartType(
                        BodyParts.ORGANIC_LEFT_HAND,
                        -53, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.LEFT_HAND.location()
                )
        );
        context.register(
                BodyPartTypes.STOMACH,
                new BodyPartType(
                        BodyParts.ORGANIC_STOMACH,
                        -15, 120,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.STOMACH.location()
                )
        );
        context.register(
                BodyPartTypes.RIBCAGE,
                new BodyPartType(
                        BodyParts.ORGANIC_RIBCAGE,
                        -7, 100,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.RIBCAGE.location()
                )
        );
        context.register(
                BodyPartTypes.SKULL,
                new BodyPartType(
                        BodyParts.ORGANIC_SKULL,
                        -3, 35,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.SKULL.location()
                )
        );
        context.register(
                BodyPartTypes.TIBIA,
                new BodyPartType(
                        BodyParts.ORGANIC_TIBIA,
                        7, 180,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.TIBIA.location()
                )
        );

        context.register(
                BodyPartTypes.ARM_MUSCLE,
                new BodyPartType(
                        BodyParts.ORGANIC_ARM_MUSCLE,
                        35, 70,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.ARM_MUSCLE.location()
                )
        );
    }

}
