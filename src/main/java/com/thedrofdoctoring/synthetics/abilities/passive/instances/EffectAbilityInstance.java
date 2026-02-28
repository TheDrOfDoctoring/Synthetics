package com.thedrofdoctoring.synthetics.abilities.passive.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.passive.types.EffectAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class EffectAbilityInstance extends AbilityPassiveInstance<EffectAbilityType> {

    private final HolderSet<MobEffect> effects;

    public EffectAbilityInstance(EffectAbilityType ability, SyntheticsPlayer player, Data data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
        this.effects = data.effects;
    }

    public HolderSet<MobEffect> effects() {
        return effects;
    }

    public static class Data extends AbilityData {

        private final HolderSet<MobEffect> effects;

        public Data(double factor, HolderSet<MobEffect> effects) {
            super(factor);
            this.effects = effects;
        }


        public HolderSet<MobEffect> effects() {
            return effects;
        }

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(Data::factor),
                RegistryCodecs.homogeneousList(BuiltInRegistries.MOB_EFFECT.key()).fieldOf("effects").forGetter(Data::effects)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ByteBufCodecs.holderSet(BuiltInRegistries.MOB_EFFECT.key()), Data::effects,
                Data::new);

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
