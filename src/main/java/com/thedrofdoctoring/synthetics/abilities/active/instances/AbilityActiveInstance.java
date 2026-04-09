package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.AbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class AbilityActiveInstance<T extends ActiveAbilityType<?>> extends AbilityInstance<T> {

    private final Data active;

    public AbilityActiveInstance(T ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, player, activeData, instanceID);
        this.active = activeData;
    }
    public int getCooldown() {
        return active.options.cooldown();
    }

    public int getDuration() {
        return active.options.duration();
    }

    public int getPowerCost() {
        return active.options.powerCost();
    }

    public int getPowerDrain() {
        return active.options.powerDrain();
    }

    public static class Data extends AbilityData {

        protected final ActiveAbilityOptions options;
        protected final Optional<String> titleOverride;
        protected final Optional<ResourceLocation> texturePathOverride;

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_data").forGetter(Data::options),
                Codec.STRING.optionalFieldOf("title_path_override").forGetter(Data::titlePathOverride),
                ResourceLocation.CODEC.optionalFieldOf("texture_path_override").forGetter(Data::texturePathOverride)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::titlePathOverride,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), Data::texturePathOverride,
                Data::new);

        public Data(double factor, ActiveAbilityOptions options, Optional<String> titleOverride, Optional<ResourceLocation> texturePathOverride) {
            super(factor);
            this.options = options;
            this.titleOverride = titleOverride;
            this.texturePathOverride = texturePathOverride;
        }

        @Override
        public Optional<String> titlePathOverride() {
            return titleOverride;
        }

        @Override
        public Optional<ResourceLocation> texturePathOverride() {
            return texturePathOverride;
        }

        @Override
        public MapCodec<? extends Data> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends Data> streamCodec() {
            return STREAM_CODEC;
        }
        public ActiveAbilityOptions options() {
            return options;
        }


    }
}
