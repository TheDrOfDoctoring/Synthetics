package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.body.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAugment;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ResearchNodeUnlocks(Optional<HolderSet<SyntheticAugment>> augments, Optional<HolderSet<BodyPart>> parts) {

    public static final MapCodec<ResearchNodeUnlocks> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SyntheticAugment.SET_CODEC.optionalFieldOf("augments").forGetter(ResearchNodeUnlocks::augments),
            BodyPart.SET_CODEC.optionalFieldOf("parts").forGetter(ResearchNodeUnlocks::parts)
    ).apply(instance, ResearchNodeUnlocks::new));


    public List<IBodyInstallable<?>> getAllUnlocked() {
        List<IBodyInstallable<?>> list = new ArrayList<>();
        augments.ifPresent(holders -> list.addAll(holders.stream().filter(Holder::isBound).map(Holder::value).toList()));
        parts.ifPresent(holders -> list.addAll(holders.stream().filter(Holder::isBound).map(Holder::value).toList()));
        return list;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNodeUnlocks> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.AUGMENTS)), ResearchNodeUnlocks::augments,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS)), ResearchNodeUnlocks::parts,
            ResearchNodeUnlocks::new);

}
