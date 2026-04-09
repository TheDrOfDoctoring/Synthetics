package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.LastingEffectAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class LastingEffectAbilityInstance extends AbilityActiveInstance<LastingEffectAbility> {

    private final List<LastingEffectAbility.EffectDetails> onTickEffects;
    private final List<LastingEffectAbility.EffectDetails> onDeactivateEffects;


    public LastingEffectAbilityInstance(LastingEffectAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.onDeactivateEffects = activeData.onDeactivateEffects;
        this.onTickEffects = activeData.onTickEffects;
    }

    public List<LastingEffectAbility.EffectDetails> onTickEffects() {
        return onTickEffects;
    }

    public List<LastingEffectAbility.EffectDetails> onDeactivateEffects() {
        return onDeactivateEffects;
    }

    public static class Data extends AbilityActiveInstance.Data {

        private final List<LastingEffectAbility.EffectDetails> onTickEffects;
        private final List<LastingEffectAbility.EffectDetails> onDeactivateEffects;

        public Data(ActiveAbilityOptions options,
                    List<LastingEffectAbility.EffectDetails> onTickEffects,
                    List<LastingEffectAbility.EffectDetails> onDeactivateEffects,
                    Optional<String> titlePathOverride, Optional<ResourceLocation> texturePathOverride) {
            super(1, options, titlePathOverride, texturePathOverride);
            this.onTickEffects = onTickEffects;
            this.onDeactivateEffects = onDeactivateEffects;
        }

        public List<LastingEffectAbility.EffectDetails> onTickEffects() {
            return onTickEffects;
        }

        public List<LastingEffectAbility.EffectDetails> onDeactivateEffects() {
            return onDeactivateEffects;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                LastingEffectAbility.EffectDetails.STREAM_CODEC.apply(ByteBufCodecs.list()), Data::onTickEffects,
                LastingEffectAbility.EffectDetails.STREAM_CODEC.apply(ByteBufCodecs.list()), Data::onDeactivateEffects,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::titlePathOverride,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), Data::texturePathOverride,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                LastingEffectAbility.EffectDetails.CODEC.codec().listOf().fieldOf("on_tick").forGetter(Data::onTickEffects),
                LastingEffectAbility.EffectDetails.CODEC.codec().listOf().fieldOf("on_deactivate").forGetter(Data::onDeactivateEffects),
                Codec.STRING.optionalFieldOf("title_path_override").forGetter(Data::titlePathOverride),
                ResourceLocation.CODEC.optionalFieldOf("texture_path_override").forGetter(Data::texturePathOverride)

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
