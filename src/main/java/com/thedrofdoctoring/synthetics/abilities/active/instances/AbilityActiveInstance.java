package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityInstance;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

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

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_data").forGetter(Data::options)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                Data::new);

        public Data(double factor, ActiveAbilityOptions options) {
            super(factor);
            this.options = options;
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
