package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.BlockHighlightAbility;
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

public class BlockHighlightAbilityInstance extends AbilityActiveInstance<BlockHighlightAbility> {

    private final Data highlightData;

    public BlockHighlightAbilityInstance(BlockHighlightAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.highlightData = activeData;
    }

    public HolderSet<Block> toHighlight() {
        return highlightData.whitelistedBlocks;
    }

    public double radius() {
        return factor();
    }


    public static class Data extends AbilityActiveInstance.Data {

        private final HolderSet<Block> whitelistedBlocks;

        public Data(double radius, ActiveAbilityOptions options, HolderSet<Block> whitelistedBlocks, Optional<String> titlePathOverride, Optional<ResourceLocation> texturePathOverride) {
            super(radius, options, titlePathOverride, texturePathOverride);
            this.whitelistedBlocks = whitelistedBlocks;
        }

        public HolderSet<Block> whitelistedBlocks() {
            return whitelistedBlocks;
        }

        public double radius() {
            return factor;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Data::radius,
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                ByteBufCodecs.holderSet(Registries.BLOCK), Data::whitelistedBlocks,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::titlePathOverride,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), Data::texturePathOverride,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.DOUBLE.fieldOf("radius").forGetter(Data::radius),
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("whitelisted_blocks").forGetter(Data::whitelistedBlocks),
                Codec.STRING.optionalFieldOf("title_path_override").forGetter(Data::titlePathOverride),
                ResourceLocation.CODEC.optionalFieldOf("texture_path_override").forGetter(Data::texturePathOverride)
        ).apply(instance, Data::new));

        @Override
        public MapCodec<? extends AbilityActiveInstance.Data> codec() {
            return CODEC;

        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BlockHighlightAbilityInstance.Data> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
