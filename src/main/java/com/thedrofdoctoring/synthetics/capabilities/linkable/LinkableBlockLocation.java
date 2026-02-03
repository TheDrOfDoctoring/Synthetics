package com.thedrofdoctoring.synthetics.capabilities.linkable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record LinkableBlockLocation(BlockPos position, ResourceKey<Level> dimension) {

    public static final StreamCodec<RegistryFriendlyByteBuf, LinkableBlockLocation> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC                        , LinkableBlockLocation::position,
            ResourceKey.streamCodec(Registries.DIMENSION), LinkableBlockLocation::dimension,
            LinkableBlockLocation::new);

}
