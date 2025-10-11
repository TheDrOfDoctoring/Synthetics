package com.thedrofdoctoring.synthetics.core.data.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BatteryComponentOptions(int capacity, int maxReceive, int maxExtract) {

    public static final MapCodec<BatteryComponentOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("capacity").forGetter(BatteryComponentOptions::capacity),
            Codec.INT.fieldOf("max_receive").forGetter(BatteryComponentOptions::maxReceive),
            Codec.INT.fieldOf("max_extract").forGetter(BatteryComponentOptions::maxExtract)
    ).apply(instance, BatteryComponentOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BatteryComponentOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BatteryComponentOptions::capacity,
            ByteBufCodecs.VAR_INT, BatteryComponentOptions::maxReceive,
            ByteBufCodecs.VAR_INT, BatteryComponentOptions::maxExtract,
            BatteryComponentOptions::new);

}
