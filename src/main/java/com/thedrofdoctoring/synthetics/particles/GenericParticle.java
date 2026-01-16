package com.thedrofdoctoring.synthetics.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.SyntheticsParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GenericParticle extends ParticleType<GenericParticle.Options> {


    public GenericParticle(boolean overrideLimitter) {
        super(overrideLimitter);
    }



    @Override
    public @NotNull MapCodec<Options> codec() {
        return Options.CODEC;
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, Options> streamCodec() {
        return Options.STREAM_CODEC;
    }

    public static Options of(ResourceLocation texture, int maxAge, int colour, float speed) {
        return new Options(texture, maxAge, colour, speed);
    }

    public record Options(ResourceLocation texture, int maxAge, int colour, float speed) implements ParticleOptions {

        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst
                .group(
                        ResourceLocation.CODEC.fieldOf("texture").forGetter(Options::texture),
                        Codec.INT.fieldOf("max_age").forGetter(Options::maxAge),
                        Codec.INT.fieldOf("colour").forGetter(Options::colour),
                        Codec.FLOAT.fieldOf("speed").forGetter(Options::speed))
                .apply(inst, Options::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, Options::texture,
                ByteBufCodecs.VAR_INT, Options::maxAge,
                ByteBufCodecs.VAR_INT, Options::colour,
                ByteBufCodecs.FLOAT, Options::speed,
                Options::new);

        @NotNull
        @Override
        public ParticleType<?> getType() {
            return SyntheticsParticles.GENERIC_PARTICLE.get();
        }
    }
}
