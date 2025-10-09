package com.thedrofdoctoring.synthetics.body.abilities;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.core.synthetics.SyntheticAbilities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public abstract class SyntheticAbilityType {

    public static final MapCodec<SyntheticAbilityType> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(ResourceLocation.CODEC.fieldOf("ability_id").forGetter(SyntheticAbilityType::getAbilityID))
                    .apply(ins, b -> SyntheticAbilities.ABILITY_REGISTRY.getOptional(b).orElseThrow()));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAbilityType> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, SyntheticAbilityType::getAbilityID, SyntheticAbilities.ABILITY_REGISTRY::get);

    public abstract ResourceLocation getAbilityID();

    public Component title() {
        return Component.translatable("abilities." + getAbilityID().getNamespace() + "." + getAbilityID().getPath());
    }

}
