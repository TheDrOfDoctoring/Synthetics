package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.ItemStack;

public record ResearchTab(ItemStack displayIcon) {

    public static final MapCodec<ResearchTab> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("display_icon").forGetter(ResearchTab::displayIcon)
    ).apply(instance, ResearchTab::new));

    public static final Codec<Holder<ResearchTab>> HOLDER_CODEC = RegistryFileCodec.create(SyntheticsData.RESEARCH_TABS, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchTab> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC  ,  ResearchTab::displayIcon,
            ResearchTab::new);
}
