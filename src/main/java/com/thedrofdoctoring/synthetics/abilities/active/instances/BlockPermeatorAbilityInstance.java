package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.BlockPermeatorAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class BlockPermeatorAbilityInstance extends AbilityActiveInstance<BlockPermeatorAbility> {

    private final Data permeatorData;

    public BlockPermeatorAbilityInstance(BlockPermeatorAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.permeatorData = activeData;
    }

    public HolderSet<Block> blacklistedBlocks() {
        return permeatorData.blacklistedBlocks;
    }

    public float radius() {
        return permeatorData.radius();
    }
    public double length() {
        return factor();
    }


    public static class Data extends AbilityActiveInstance.Data {

        private final float radius;
        private final HolderSet<Block> blacklistedBlocks;

        public Data(double length, ActiveAbilityOptions options, float radius, HolderSet<Block> blacklistedBlocks) {
            super(length, options, Optional.empty(), Optional.empty());
            this.radius = radius;
            this.blacklistedBlocks = blacklistedBlocks;
        }

        public HolderSet<Block> blacklistedBlocks() {
            return blacklistedBlocks;
        }

        public float radius() {
            return radius;
        }
        public double length() {
            return factor;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::length,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.FLOAT, Data::radius,
                ByteBufCodecs.holderSet(Registries.BLOCK), Data::blacklistedBlocks,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("length").forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                Codec.FLOAT.fieldOf("radius").forGetter(Data::radius),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blacklisted_blocks").forGetter(Data::blacklistedBlocks)
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
