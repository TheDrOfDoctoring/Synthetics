package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.FlamethrowerAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class FlamethrowerAbilityInstance extends AbilityActiveInstance<FlamethrowerAbility> {

    private final Data flameData;

    public FlamethrowerAbilityInstance(FlamethrowerAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.flameData = activeData;
    }

    public int flameLifeTime() {
        return flameData.flameLifeTime();
    }

    public static class Data extends AbilityActiveInstance.Data {

        private final int flameLifeTime;

        public Data(double factor, ActiveAbilityOptions options, int flameTime) {
            super(factor, options);
            this.flameLifeTime = flameTime;
        }

        public int flameLifeTime() {
            return flameLifeTime;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, FlamethrowerAbilityInstance.Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, FlamethrowerAbilityInstance.Data::options,
                ByteBufCodecs.INT, FlamethrowerAbilityInstance.Data::flameLifeTime,
                FlamethrowerAbilityInstance.Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("damage").forGetter(FlamethrowerAbilityInstance.Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(FlamethrowerAbilityInstance.Data::options),
                Codec.INT.fieldOf("flame_time").forGetter(FlamethrowerAbilityInstance.Data::flameLifeTime)
        ).apply(instance, FlamethrowerAbilityInstance.Data::new));

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
