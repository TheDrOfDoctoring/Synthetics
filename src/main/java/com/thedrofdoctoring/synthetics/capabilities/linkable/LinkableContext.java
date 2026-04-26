package com.thedrofdoctoring.synthetics.capabilities.linkable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.level.block.Block;

public record LinkableContext(Component name, Holder<Block> block) {
    public static final MapCodec<LinkableContext> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(LinkableContext::name),
            RegistryFixedCodec.create(Registries.BLOCK).fieldOf("block").forGetter(LinkableContext::block)
    ).apply(instance, LinkableContext::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LinkableContext> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, LinkableContext::name,
            ByteBufCodecs.holderRegistry(Registries.BLOCK), LinkableContext::block,
            LinkableContext::new);
}
