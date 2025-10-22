package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.minecraft.data.worldgen.BootstrapContext;

import java.util.List;

public class SyntheticsBodyPartsProvider {


    public static void createBodyParts(BootstrapContext<BodyPart> context) {
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_EYES)
                        .partType(BodyParts.EYES)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(5)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_FEET)
                        .partType(BodyParts.FEET)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(3)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_FEET)
                        .partType(BodyParts.FEET)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(9)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_BRAIN)
                        .partType(BodyParts.BRAIN)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LUNGS)
                        .partType(BodyParts.LUNGS)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(5)
        );


        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_HEART)
                        .partType(BodyParts.HEART)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(3)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_HANDS)
                        .partType(BodyParts.HANDS)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TISSUE)
                        .partType(BodyParts.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_TISSUE)
                        .partType(BodyParts.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(12)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_STOMACH)
                        .partType(BodyParts.STOMACH)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(6)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIBCAGE)
                        .partType(BodyParts.RIBCAGE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_SKULL)
                        .partType(BodyParts.SKULL)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TIBIA)
                        .partType(BodyParts.TIBIA)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(4)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_HANDS)
                        .partType(BodyParts.HANDS)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_HAND_DAMAGE))
                        .maxComplexity(10)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_ARM_MUSCLE)
                        .partType(BodyParts.ARM_MUSCLE)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(4)
        );
    }

    private static void register(BootstrapContext<BodyPart> context, BodyPart.Builder builder) {
        context.register(builder.key(), builder.build());
    }


    public static void createBodyPartTypes(BootstrapContext<BodyPartType> context) {
        context.register(
                BodyParts.TISSUE,
                new BodyPartType(
                        BodyParts.ORGANIC_TISSUE,
                        -18, 110,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.TISSUE.location()
                )
        );
        context.register(
                BodyParts.EYES,
                new BodyPartType(
                        BodyParts.ORGANIC_EYES,
                        -18, 28,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.EYES.location()
                )
        );
        context.register(
                BodyParts.FEET,
                new BodyPartType(
                        BodyParts.ORGANIC_FEET,
                        3, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.FEET.location()
                )
        );
        context.register(
                BodyParts.BRAIN,
                new BodyPartType(
                        BodyParts.ORGANIC_BRAIN,
                        3, 10,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.BRAIN.location()
                )
        );
        context.register(
                BodyParts.HEART,
                new BodyPartType(
                        BodyParts.ORGANIC_HEART,
                        10, 80,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.HEART.location()
                )
        );
        context.register(
                BodyParts.LUNGS,
                new BodyPartType(
                        BodyParts.ORGANIC_LUNGS,
                        -15, 90,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.LUNGS.location()
                )
        );
        context.register(
                BodyParts.HANDS,
                new BodyPartType(
                        BodyParts.ORGANIC_HANDS,
                        40, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.HANDS.location()
                )
        );
        context.register(
                BodyParts.STOMACH,
                new BodyPartType(
                        BodyParts.ORGANIC_STOMACH,
                        -15, 120,
                        BodyPartType.Layer.ORGANS,
                        BodyParts.STOMACH.location()
                )
        );
        context.register(
                BodyParts.RIBCAGE,
                new BodyPartType(
                        BodyParts.ORGANIC_RIBCAGE,
                        -7, 100,
                        BodyPartType.Layer.BONE,
                        BodyParts.RIBCAGE.location()
                )
        );
        context.register(
                BodyParts.SKULL,
                new BodyPartType(
                        BodyParts.ORGANIC_SKULL,
                        -3, 35,
                        BodyPartType.Layer.BONE,
                        BodyParts.SKULL.location()
                )
        );
        context.register(
                BodyParts.TIBIA,
                new BodyPartType(
                        BodyParts.ORGANIC_TIBIA,
                        7, 180,
                        BodyPartType.Layer.BONE,
                        BodyParts.TIBIA.location()
                )
        );

        context.register(
                BodyParts.ARM_MUSCLE,
                new BodyPartType(
                        BodyParts.ORGANIC_ARM_MUSCLE,
                        35, 70,
                        BodyPartType.Layer.EXTERIOR,
                        BodyParts.ARM_MUSCLE.location()
                )
        );
    }

}
