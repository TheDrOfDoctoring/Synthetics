package com.thedrofdoctoring.synthetics.body.abilities.passive.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.passive.types.PassiveAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class GenericPassiveAbilityInstance<T extends PassiveAbilityType> extends AbilityPassiveInstance<T> {


    public GenericPassiveAbilityInstance(T ability, SyntheticsPlayer player, Data data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
    }


    public static class Data extends AbilityData {

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("ability_factor").forGetter(Data::factor)
        ).apply(instance, Data::new));


        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                Data::new);

        public Data(double factor) {
            super(factor);
        }

        @Override
        public MapCodec<? extends AbilityData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends AbilityData> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
