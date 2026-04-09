package com.thedrofdoctoring.synthetics.abilities.passive.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.types.HealingAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class HealingAbilityInstance extends AbilityPassiveInstance<HealingAbilityType> {

    private final int   powerDraw;
    private final float healingAmount;

    public HealingAbilityInstance(HealingAbilityType ability, SyntheticsPlayer player, Data data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
        this.powerDraw = data.powerDraw;
        this.healingAmount = data.healingAmount;
    }

    public int powerDraw() {
        return powerDraw;
    }

    public float healingAmount() {
        return healingAmount;
    }

    public static class Data extends AbilityData {

        private final int powerDraw;
        private final float healingAmount;

        public Data(double factor, int powerDraw, float healingAmount) {
            super(factor);
            this.powerDraw = powerDraw;
            this.healingAmount = healingAmount;
        }

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("ticks_per_heal", 1d).forGetter(Data::factor),
                Codec.INT.fieldOf("power_draw_per_heal").forGetter(Data::powerDraw),
                Codec.FLOAT.fieldOf("healing_amount").forGetter(Data::healingAmount)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ByteBufCodecs.INT,    Data::powerDraw,
                ByteBufCodecs.FLOAT,  Data::healingAmount,
                Data::new);

        public int powerDraw() {
            return powerDraw;
        }

        public float healingAmount() {
            return healingAmount;
        }

        @Override
        public MapCodec<Data> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
