package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.ShockAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ShockAbilityInstance extends AbilityActiveInstance<ShockAbility> {

    public ShockAbilityInstance(ShockAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.slowDuration = activeData.slowDuration;
        this.energyDrain  = activeData.energyDrained;
    }

    private final int slowDuration;
    private final int energyDrain;

    public int slowDuration() {
        return slowDuration;
    }

    public int energyDrain() {
        return energyDrain;
    }

    public static class Data extends AbilityActiveInstance.Data {

        private final int slowDuration;
        private final int energyDrained;

        public Data(double factor, ActiveAbilityOptions options, int slowDuration, int energyDrained) {
            super(factor, options, Optional.empty(), Optional.empty());
            this.slowDuration = slowDuration;
            this.energyDrained = energyDrained;
        }

        public int slowDuration() {
            return slowDuration;
        }

        public int energyDrained() {
            return energyDrained;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.INT, Data::slowDuration,
                ByteBufCodecs.INT, Data::energyDrained,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("damage").forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                Codec.INT.fieldOf("slowing_duration").forGetter(Data::slowDuration),
                Codec.INT.fieldOf("energy_drained").forGetter(Data::energyDrained)
        ).apply(instance, Data::new));

        @Override
        public MapCodec<? extends AbilityActiveInstance.Data> codec() {
            return CODEC;

        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
            return STREAM_CODEC;
        }


    }
}
