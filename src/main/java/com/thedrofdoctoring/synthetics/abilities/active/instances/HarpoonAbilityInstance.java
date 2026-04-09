package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.HarpoonAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class HarpoonAbilityInstance extends AbilityActiveInstance<HarpoonAbility> {

    private final Data harpoonData;

    public HarpoonAbilityInstance(HarpoonAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.harpoonData = activeData;
    }

    public Data harpoonData() {
        return harpoonData;
    }


    public static class Data extends AbilityActiveInstance.Data {

        private final float force;
        private final float speed;
        public Data(double factor, ActiveAbilityOptions options, float force, float speed) {
            super(factor, options, Optional.empty(), Optional.empty());
            this.force = force;
            this.speed = speed;
        }

        public float force() {
            return force;
        }

        public float speed() {
            return speed;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.FLOAT, Data::force,
                ByteBufCodecs.FLOAT, Data::speed,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("damage").forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                Codec.FLOAT.fieldOf("force").forGetter(Data::force),
                Codec.FLOAT.fieldOf("speed").forGetter(Data::speed)
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
