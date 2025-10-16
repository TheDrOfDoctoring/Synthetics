package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.datafixers.util.Either;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ResearchNodeUnlocks(Optional<Ingredient> item, Optional<HolderSet<SyntheticAugment>> augments, Optional<HolderSet<BodyPart>> parts) {

    public static final MapCodec<ResearchNodeUnlocks> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.optionalFieldOf("item").forGetter(ResearchNodeUnlocks::item),
            SyntheticAugment.SET_CODEC.optionalFieldOf("augments").forGetter(ResearchNodeUnlocks::augments),
            BodyPart.SET_CODEC.optionalFieldOf("parts").forGetter(ResearchNodeUnlocks::parts)
    ).apply(instance, ResearchNodeUnlocks::new));


    public List<IBodyInstallable<?>> getAllUnlocked() {
        List<IBodyInstallable<?>> list = new ArrayList<>();
        augments.ifPresent(holders -> list.addAll(holders.stream().filter(Holder::isBound).map(Holder::value).toList()));
        parts.ifPresent(holders -> list.addAll(holders.stream().filter(Holder::isBound).map(Holder::value).toList()));
        return list;
    }
    public Either<Ingredient, IBodyInstallable<?>> getDisplayed() {
        if(item.isPresent()) {
            return Either.left(item.get());
        }
        if(augments.isPresent()) {
            return Either.right(augments.get().get(0).value());
        }
        //noinspection OptionalIsPresent
        if(parts.isPresent()) {
            return Either.right(parts.get().get(0).value());
        }
        return null;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNodeUnlocks> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), ResearchNodeUnlocks::item,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.AUGMENTS)), ResearchNodeUnlocks::augments,
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(SyntheticsData.BODY_PARTS)), ResearchNodeUnlocks::parts,
            ResearchNodeUnlocks::new);

}
