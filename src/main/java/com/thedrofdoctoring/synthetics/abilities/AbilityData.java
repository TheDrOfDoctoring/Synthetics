package com.thedrofdoctoring.synthetics.abilities;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

public abstract class AbilityData {

    protected final double factor;

    protected AbilityData(double factor) {
        this.factor = factor;
    }

    public double factor() {
        return this.factor;
    }


    /**
     * Used to override an ability's title in data, rather than in code
     * @return The "path" in the title's component location, that is, the last dot-separated element of the title component
     */
    public Optional<String> titlePathOverride() {
        return Optional.empty();
    }

    /**
     * Used to override an ability's texture in data
     * @return Replaces the ability ID identifier in its texture path
     */
    public Optional<ResourceLocation> texturePathOverride() {
        return Optional.empty();
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
