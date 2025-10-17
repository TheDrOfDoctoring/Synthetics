package com.thedrofdoctoring.synthetics.body.abilities.active;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.AbilityInstance;
import com.thedrofdoctoring.synthetics.body.abilities.passive.instances.AbilityData;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class AbilityActiveInstance<T extends ActiveAbilityType> extends AbilityInstance<T> {

    private final ActiveAbilityData active;

    public AbilityActiveInstance(T ability, ActiveAbilityData activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
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

    public static class ActiveAbilityData extends AbilityData {

        private final ActiveAbilityOptions options;

        public static final MapCodec<ActiveAbilityData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("factor").forGetter(ActiveAbilityData::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_data").forGetter(ActiveAbilityData::options)
        ).apply(instance, ActiveAbilityData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ActiveAbilityData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, ActiveAbilityData::factor,
                ActiveAbilityOptions.STREAM_CODEC, ActiveAbilityData::options,
                ActiveAbilityData::new);

        protected ActiveAbilityData(double factor, ActiveAbilityOptions options) {
            super(factor);
            this.options = options;
        }

        @Override
        public MapCodec<? extends AbilityData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends AbilityData> streamCodec() {
            return STREAM_CODEC;
        }
        public ActiveAbilityOptions options() {
            return options;
        }


    }
}
