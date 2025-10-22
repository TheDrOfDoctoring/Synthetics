package com.thedrofdoctoring.synthetics.core.data.types.body.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

/**
 *
 * @param defaultPart - Default part used when loaded and the part type has no corresponding part
 * @param id - ID of the part type
 * @param x - X location of the part display on the Augmentation Chamber renderer
 *            X is annoying, as the width of the different render entities changes, so no consistent values.
 *            +-60 is roughly left and right hand,
 *            0 is roughly centre
 * @param y - Y location of the part display on the Augmentation Chamber renderer
 *            0 is top,
 *            220 is bottom
 *
 * I don't really want to keep these here since it's client only information, but right now I'm not sure of a better place, and I want to keep them configurable
 */
public record BodyPartType(ResourceKey<BodyPart> defaultPart, int x, int y, Layer bodyLayer, ResourceLocation id) {

    public static final MapCodec<BodyPartType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(SyntheticsData.BODY_PARTS).fieldOf("default_part").forGetter(BodyPartType::defaultPart),
            Codec.INT.fieldOf("x").forGetter(BodyPartType::x),
            Codec.INT.fieldOf("y").forGetter(BodyPartType::y),
            StringRepresentable.fromEnum(Layer::values).fieldOf("layer").forGetter(BodyPartType::bodyLayer),
            ResourceLocation.CODEC.fieldOf("id").forGetter(BodyPartType::id)

    ).apply(instance, BodyPartType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BodyPartType> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(SyntheticsData.BODY_PARTS), BodyPartType::defaultPart,
            ByteBufCodecs.VAR_INT, BodyPartType::x,
            ByteBufCodecs.VAR_INT, BodyPartType::y,
            Layer.STREAM_CODEC, BodyPartType::bodyLayer,
            ResourceLocation.STREAM_CODEC, BodyPartType::id,
            BodyPartType::new);

    public static final Codec<Holder<BodyPartType>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.BODY_PART_TYPES, CODEC.codec());


    public enum Layer implements StringRepresentable {

        EXTERIOR(0, "exterior"),
        ORGANS(1, "organs"),
        BONE(2, "bone");

        private final String representation;
        private final int id;

        private static final IntFunction<Layer> BY_ID = ByIdMap.continuous(Layer::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Layer> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Layer::getId);

        Layer(int id, String representation) {
            this.representation = representation;
            this.id = id;
        }
        public int getId() {
            return this.id;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.representation;
        }
    }


}
