package com.thedrofdoctoring.synthetics.capabilities.linkable;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record LinkableBlockLocation(GlobalPos pos, Optional<LinkableContext> context) {

    public static final StreamCodec<RegistryFriendlyByteBuf, LinkableBlockLocation> STREAM_CODEC = StreamCodec.composite(
            GlobalPos.STREAM_CODEC, LinkableBlockLocation::pos,
            ByteBufCodecs.optional(LinkableContext.STREAM_CODEC), LinkableBlockLocation::context,
            LinkableBlockLocation::new);

}
