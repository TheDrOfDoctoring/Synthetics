package com.thedrofdoctoring.synthetics.abilities.passive.instances;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class AbilityData {

    protected final double factor;

    protected AbilityData(double factor) {
        this.factor = factor;
    }

    public double factor() {
        return this.factor;
    }


    public abstract MapCodec<? extends AbilityData> codec();

    public abstract StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData> streamCodec();


    public static final MapCodec<AbilityData> DISPATCH_CODEC = SyntheticAbilities.ABILITY_DATA_TYPE_REGISTRY.byNameCodec().dispatchMap(AbilityData::codec, Function.identity());

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityData> DISPATCH_STREAM =
            ByteBufCodecs.registry(SyntheticAbilities.ABILITY_DATA_STREAM_TYPE_KEY).dispatch(
                    AbilityData::streamCodec,
                    Function.identity()
            );

}
