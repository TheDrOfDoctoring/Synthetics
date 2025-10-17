package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ActiveAbilityOptions(int cooldown, int duration, int powerCost, int powerDrain) {
    public static final MapCodec<ActiveAbilityOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("cooldown").forGetter(ActiveAbilityOptions::cooldown),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(ActiveAbilityOptions::duration),
            Codec.INT.optionalFieldOf("power_cost", 0).forGetter(ActiveAbilityOptions::powerCost),
            Codec.INT.optionalFieldOf("power_drain", 0).forGetter(ActiveAbilityOptions::powerDrain)
    ).apply(instance, ActiveAbilityOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ActiveAbilityOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ActiveAbilityOptions::cooldown,
            ByteBufCodecs.VAR_INT, ActiveAbilityOptions::duration,
            ByteBufCodecs.VAR_INT, ActiveAbilityOptions::powerCost,
            ByteBufCodecs.VAR_INT, ActiveAbilityOptions::powerDrain,
            ActiveAbilityOptions::new);

    public static ActiveAbilityOptions options(int cooldown, int duration, int powerCost, int powerDrain) {
        return new ActiveAbilityOptions(cooldown, duration, powerCost, powerDrain);
    }
    public static ActiveAbilityOptions options(int cooldown, int duration, int powerCost) {
        return new ActiveAbilityOptions(cooldown, duration, powerCost, 0);
    }
    public static ActiveAbilityOptions options(int cooldown, int duration) {
        return new ActiveAbilityOptions(cooldown, duration, 0, 0);
    }
    public static ActiveAbilityOptions options(int cooldown) {
        return new ActiveAbilityOptions(cooldown, 0, 0, 0);
    }
}
