package com.thedrofdoctoring.synthetics.core.data.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.body.abilities.SyntheticAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.active.SyntheticActiveAbilityType;
import com.thedrofdoctoring.synthetics.body.abilities.passive.SyntheticPassiveAbilityType;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Optional;

public record SyntheticAbility(SyntheticAbilityType abilityType, double factor,
                               AttributeModifier.Operation operation, Optional<ActiveAbilityOptions> options, ResourceLocation id) {

    public static final MapCodec<SyntheticAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SyntheticAbilityType.CODEC.fieldOf("ability_type").forGetter(SyntheticAbility::abilityType),
            Codec.DOUBLE.optionalFieldOf("factor", 1d).forGetter(SyntheticAbility::factor),
            AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(SyntheticAbility::operation),
            ActiveAbilityOptions.CODEC.codec().optionalFieldOf("active_options").forGetter(SyntheticAbility::options),
            ResourceLocation.CODEC.fieldOf("id").forGetter(SyntheticAbility::id)
    ).apply(instance, SyntheticAbility::new));

    public static SyntheticAbility create(SyntheticPassiveAbilityType abilityType, ActiveAbilityOptions options, ResourceLocation id) {
        return new SyntheticAbility(abilityType, 1d, AttributeModifier.Operation.ADD_VALUE, Optional.of(options),id);

    }
    public static SyntheticAbility create(SyntheticPassiveAbilityType abilityType, ResourceLocation id, double factor, AttributeModifier.Operation operation) {
        return new SyntheticAbility(abilityType, factor, operation, Optional.empty(), id);
    }
    public static SyntheticAbility create(SyntheticActiveAbilityType abilityType, ActiveAbilityOptions options, ResourceLocation id) {
        return new SyntheticAbility(abilityType, 1d, AttributeModifier.Operation.ADD_VALUE, Optional.of(options), id);
    }
    public static SyntheticAbility create(SyntheticActiveAbilityType abilityType, double factor, ActiveAbilityOptions options, ResourceLocation id) {
        return new SyntheticAbility(abilityType, factor, AttributeModifier.Operation.ADD_VALUE, Optional.of(options), id);
    }

    public static ActiveAbilityOptions options(int cooldown, int duration, int powerCost, int powerDrain) {
        return new ActiveAbilityOptions(cooldown, duration, powerCost, powerDrain);
    }
    public static ActiveAbilityOptions options(int cooldown, int duration, int powerCost) {
        return new ActiveAbilityOptions(cooldown, duration, powerCost, 0);
    }
    public static ActiveAbilityOptions options(int cooldown, int duration) {
        return new ActiveAbilityOptions(cooldown, duration, 0, 0);
    }
    public static ActiveAbilityOptions options(int cooldown) {
        return new ActiveAbilityOptions(cooldown, 0, 0, 0);
    }

    public static final Codec<HolderSet<SyntheticAbility>> SET_CODEC = RegistryCodecs.homogeneousList(SyntheticsData.ABILITIES, CODEC.codec());

    public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticAbility> STREAM_CODEC = StreamCodec.composite(
            SyntheticAbilityType.STREAM_CODEC, SyntheticAbility::abilityType,
            ByteBufCodecs.DOUBLE, SyntheticAbility::factor,
            AttributeModifier.Operation.STREAM_CODEC, SyntheticAbility::operation,
            ByteBufCodecs.optional(ActiveAbilityOptions.STREAM_CODEC), SyntheticAbility::options,
            ResourceLocation.STREAM_CODEC, SyntheticAbility::id,
            SyntheticAbility::new);
}
