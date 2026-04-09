package com.thedrofdoctoring.synthetics.abilities.passive.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.types.DamageResistanceAbilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageResistanceAbilityInstance extends AbilityPassiveInstance<DamageResistanceAbilityType> {

    private final HolderSet<DamageType> damageTypes;

    public DamageResistanceAbilityInstance(DamageResistanceAbilityType ability, SyntheticsPlayer player, Data data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
        this.damageTypes = data.damageTypes;
    }

    public HolderSet<DamageType> damageTypes() {
        return damageTypes;
    }

    public static class Data extends AbilityData {

        private final HolderSet<DamageType> damageTypes;

        public Data(double factor, HolderSet<DamageType> damageTypes) {
            super(factor);
            this.damageTypes = damageTypes;
        }

        public HolderSet<DamageType> damageTypes() {
            return damageTypes;
        }

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(Data::factor),
                RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).fieldOf("damage_types").forGetter(Data::damageTypes)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ByteBufCodecs.holderSet(Registries.DAMAGE_TYPE), Data::damageTypes,
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
