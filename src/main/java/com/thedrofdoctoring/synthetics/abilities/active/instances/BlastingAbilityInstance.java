package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.BlastingAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class BlastingAbilityInstance extends AbilityActiveInstance<BlastingAbility> {

    private final double radius;
    private final double length;
    private final TagKey<Block> invalidBlocks;

    public BlastingAbilityInstance(BlastingAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.radius = activeData.radius;
        this.length = activeData.factor();
        this.invalidBlocks = activeData.tierInvalidBlockTag;
    }

    public double radius() {
        return radius;
    }

    public double length() {
        return length;
    }

    public TagKey<Block> invalidBlocks() {
        return invalidBlocks;
    }

    public static class Data extends AbilityActiveInstance.Data {

        private final double radius;
        private final TagKey<Block> tierInvalidBlockTag;

        public Data(double length, ActiveAbilityOptions options, double radius, TagKey<Block> tierTag) {
            super(length, options, Optional.empty(), Optional.empty());
            this.radius = radius;
            this.tierInvalidBlockTag = tierTag;
        }

        public double radius() {
            return radius;
        }

        public TagKey<Block> tierInvalidBlockTag() {
            return tierInvalidBlockTag;
        }

        private static <T> StreamCodec<ByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registry) {
            return ResourceLocation.STREAM_CODEC.map((p_203893_) -> TagKey.create(registry, p_203893_), TagKey::location);
        }

        private static final StreamCodec<ByteBuf, TagKey<Block>> BLOCK_TAG_STREAM_CODEC = tagKeyStreamCodec(Registries.BLOCK);

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::factor,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.DOUBLE, Data::radius,
                BLOCK_TAG_STREAM_CODEC, Data::tierInvalidBlockTag,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("length").forGetter(Data::factor),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                Codec.DOUBLE.fieldOf("radius").forGetter(Data::radius),
                TagKey.codec(Registries.BLOCK).fieldOf("tier").forGetter(Data::tierInvalidBlockTag)
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
