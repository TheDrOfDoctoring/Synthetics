package com.thedrofdoctoring.synthetics.core.data.providers;

import com.thedrofdoctoring.synthetics.client.renderers.installables.BodyPosition;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.collections.Abilities;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyPartTypes;
import com.thedrofdoctoring.synthetics.core.data.collections.BodyParts;
import com.thedrofdoctoring.synthetics.core.data.collections.BodySegments;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.parts.BodyPartType;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public class SyntheticsBodyPartsProvider {


    public static void createBodyParts(BootstrapContext<BodyPart> context) {

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_BRAIN)
                        .partType(BodyPartTypes.BRAIN)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(50)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LUNGS)
                        .partType(BodyPartTypes.LUNGS)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(40)
        );


        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_HEART)
                        .partType(BodyPartTypes.HEART)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(40)
        );


        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TISSUE)
                        .partType(BodyPartTypes.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(50)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_TISSUE)
                        .partType(BodyPartTypes.TISSUE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(120)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_STOMACH)
                        .partType(BodyPartTypes.STOMACH)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(40)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIBCAGE)
                        .partType(BodyPartTypes.RIBCAGE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(30)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_SKULL)
                        .partType(BodyPartTypes.SKULL)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(30)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_TIBIA)
                        .partType(BodyPartTypes.TIBIA)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(30)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_LEFT_HAND)
                        .partType(BodyPartTypes.LEFT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_HAND_DAMAGE))
                        .maxComplexity(90)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_RIGHT_HAND)
                        .partType(BodyPartTypes.RIGHT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_HAND_DAMAGE))
                        .maxComplexity(90)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_HAND)
                        .partType(BodyPartTypes.LEFT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(40)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_HAND)
                        .partType(BodyPartTypes.RIGHT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(40)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.MECHANICAL_LEFT_HAND)
                        .partType(BodyPartTypes.LEFT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.MECHANICAL_HAND_ATTACK_SPEED))
                        .maxComplexity(70)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.MECHANICAL_RIGHT_HAND)
                        .partType(BodyPartTypes.RIGHT_HAND)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .abilities(List.of(Abilities.MECHANICAL_HAND_ATTACK_SPEED))
                        .maxComplexity(70)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_EYE)
                        .partType(BodyPartTypes.LEFT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(40)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_EYE)
                        .partType(BodyPartTypes.RIGHT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .maxComplexity(40)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_LEFT_FOOT)
                        .partType(BodyPartTypes.LEFT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(40)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_RIGHT_FOOT)
                        .partType(BodyPartTypes.RIGHT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(40)
        );

        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_LEFT_FOOT)
                        .partType(BodyPartTypes.LEFT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(80)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_RIGHT_FOOT)
                        .partType(BodyPartTypes.RIGHT_FOOT)
                        .validSegments(BodySegments.LOWER_BODY_MAIN)
                        .maxComplexity(80)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ORGANIC_ARM_MUSCLE)
                        .partType(BodyPartTypes.ARM_MUSCLE)
                        .validSegments(BodySegments.ARMS_MAIN)
                        .maxComplexity(40)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_LEFT_EYE)
                        .partType(BodyPartTypes.LEFT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_EYE_VIEW))
                        .maxComplexity(80)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_RIGHT_EYE)
                        .partType(BodyPartTypes.RIGHT_EYE)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .abilities(List.of(Abilities.CYBERNETIC_EYE_VIEW))
                        .maxComplexity(80)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_BRAIN)
                        .partType(BodyPartTypes.BRAIN)
                        .validSegments(BodySegments.HEAD_MAIN)
                        .abilities(List.of(Abilities.REMOTE_LINKED_INTERACTION))
                        .maxComplexity(150)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.MECHANICAL_HEART)
                        .partType(BodyPartTypes.HEART)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(70)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.CYBERNETIC_HEART)
                        .partType(BodyPartTypes.HEART)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(120)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.ARMOURED_RIBCAGE)
                        .partType(BodyPartTypes.RIBCAGE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .abilities(Abilities.ARMOURED_RIB_KNOCKBACK, Abilities.ARMOURED_RIB_ARMOUR_TOUGHNESS)
                        .maxComplexity(50)
        );
        register(context,
                BodyPart.Builder.of(context, BodyParts.SYNTHETIC_RIBCAGE)
                        .partType(BodyPartTypes.RIBCAGE)
                        .validSegments(BodySegments.TORSO_MAIN)
                        .maxComplexity(100)
        );
    }

    private static void register(BootstrapContext<BodyPart> context, BodyPart.Builder builder) {
        context.register(builder.key(), builder.build());
    }


    public static void createBodyPartTypes(BootstrapContext<BodyPartType> context) {
        context.register(
                BodyPartTypes.TISSUE,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_TISSUE),
                        -18, 110,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.TISSUE.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodyPartTypes.LEFT_EYE,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_LEFT_EYE),
                        -15, 29,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.LEFT_EYE.location(),
                        BodyPosition.HEAD
                )
        );
        context.register(
                BodyPartTypes.RIGHT_EYE,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_RIGHT_EYE),
                        9, 29,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.RIGHT_EYE.location(),
                        BodyPosition.HEAD
                )
        );
        context.register(
                BodyPartTypes.RIGHT_FOOT,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_RIGHT_FOOT),
                        7, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.RIGHT_FOOT.location(),
                        BodyPosition.RIGHT_LEG
                )
        );
        context.register(
                BodyPartTypes.LEFT_FOOT,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_LEFT_FOOT),
                        -26, 220,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.LEFT_FOOT.location(),
                        BodyPosition.LEFT_LEG
                )
        );
        context.register(
                BodyPartTypes.BRAIN,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_BRAIN),
                        3, 10,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.BRAIN.location(),
                        BodyPosition.HEAD
                )
        );
        context.register(
                BodyPartTypes.HEART,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_HEART),
                        10, 80,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.HEART.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodyPartTypes.LUNGS,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_LUNGS),
                        -15, 90,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.LUNGS.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodyPartTypes.RIGHT_HAND,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_RIGHT_HAND),
                        -53, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.RIGHT_HAND.location(),
                        BodyPosition.RIGHT_ARM
                )
        );
        context.register(
                BodyPartTypes.LEFT_HAND,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_LEFT_HAND),
                        36, 130,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.LEFT_HAND.location(),
                        BodyPosition.LEFT_ARM
                )
        );
        context.register(
                BodyPartTypes.STOMACH,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_STOMACH),
                        -15, 120,
                        BodyPartType.Layer.ORGANS,
                        BodyPartTypes.STOMACH.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodyPartTypes.RIBCAGE,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_RIBCAGE),
                        -7, 100,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.RIBCAGE.location(),
                        BodyPosition.BODY
                )
        );
        context.register(
                BodyPartTypes.SKULL,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_SKULL),
                        -3, 35,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.SKULL.location(),
                        BodyPosition.HEAD
                )
        );
        context.register(
                BodyPartTypes.TIBIA,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_TIBIA),
                        7, 180,
                        BodyPartType.Layer.BONE,
                        BodyPartTypes.TIBIA.location(),
                        BodyPosition.LEFT_LEG
                )
        );

        context.register(
                BodyPartTypes.ARM_MUSCLE,
                new BodyPartType(
                        getPart(context, BodyParts.ORGANIC_ARM_MUSCLE),
                        35, 70,
                        BodyPartType.Layer.EXTERIOR,
                        BodyPartTypes.ARM_MUSCLE.location(),
                        BodyPosition.LEFT_ARM
                )
        );
    }

    private static Holder<BodyPart> getPart(BootstrapContext<?> context, ResourceKey<BodyPart> part) {
        return context.lookup(SyntheticsData.BODY_PARTS).getOrThrow(part);
    }

}
