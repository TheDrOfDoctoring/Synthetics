package com.thedrofdoctoring.synthetics.abilities.active.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.active.ActiveAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.LastingAbilityType;
import com.thedrofdoctoring.synthetics.abilities.active.instances.AbilityActiveInstance;
import com.thedrofdoctoring.synthetics.abilities.active.instances.LastingEffectAbilityInstance;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LastingEffectAbility extends LastingAbilityType<LastingEffectAbilityInstance> {
    public LastingEffectAbility(ResourceLocation id) {
        super(id);
    }

    private static final int REFRESH_TIME = 100;

    @Override
    public boolean onTick(SyntheticsPlayer syntheticsPlayer, LastingEffectAbilityInstance instance) {
        if(syntheticsPlayer.getEntity().tickCount % REFRESH_TIME == 0) {
            addTickEffects(syntheticsPlayer.getEntity(), instance.onTickEffects());
        }
        return false;
    }

    @Override
    public void onRestoreActivate(SyntheticsPlayer syntheticsPlayer, LastingEffectAbilityInstance instance) {

    }

    @Override
    public void onAbilityDeactivated(SyntheticsPlayer syntheticsPlayer, LastingEffectAbilityInstance instance) {
        if(syntheticsPlayer.getEntity().isAlive()) {
            for(EffectDetails details : instance.onDeactivateEffects()) {
                syntheticsPlayer.getEntity().addEffect(new MobEffectInstance(details.effect, details.duration, details.amplifier, !details.shouldShow, details.shouldShow, details.shouldShow));
            }
        }
    }

    @Override
    public void activateClient(SyntheticsPlayer syntheticsPlayer, LastingEffectAbilityInstance instance) {

    }

    private void addTickEffects(Player player, List<EffectDetails> effectDetails) {
        for(EffectDetails details : effectDetails) {
            int duration = REFRESH_TIME + 10;
            if(details.duration > duration) {
                duration = details.duration;
            }
            player.addEffect(new MobEffectInstance(details.effect, duration, details.amplifier, !details.shouldShow, details.shouldShow, details.shouldShow));
        }
    }

    @Override
    public boolean activate(SyntheticsPlayer syntheticsPlayer, LastingEffectAbilityInstance abilityData) {
        addTickEffects(syntheticsPlayer.getEntity(), abilityData.onTickEffects());
        return true;
    }

    @Override
    public boolean canBeUsed(SyntheticsPlayer syntheticsPlayer) {
        return true;
    }

    @Override
    public void addDescriptionInfo(Ability ability, List<Component> description) {
        ActiveAbilityType.activeAbilityDescription(ability, description);
        if(ability.abilityData() instanceof LastingEffectAbilityInstance.Data data) {
            List<EffectDetails> onTickEffects = data.onTickEffects();
            List<EffectDetails> onDeactivateEffects = data.onDeactivateEffects();

            if(!onTickEffects.isEmpty()) {
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect_on_tick").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.UNDERLINE));
            }

            for(EffectDetails onTick : onTickEffects) {
                MobEffect effect = onTick.effect().value();
                ChatFormatting colour = getDisplayColour(effect);
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect.effect", effect.getDisplayName()).withStyle(colour));
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect.amplifier", onTick.amplifier).withStyle(colour));
            }
            for(EffectDetails onDisable : onDeactivateEffects) {
                MobEffect effect = onDisable.effect().value();
                ChatFormatting colour = getDisplayColour(effect);
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect.effect", effect.getDisplayName()).withStyle(colour));
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect.amplifier", onDisable.amplifier).withStyle(colour));
                description.add(Component.translatable("abilities.synthetics.description.lasting_effect.duration", onDisable.duration).withStyle(colour));
            }

        }
    }

    private ChatFormatting getDisplayColour(MobEffect effect) {
        return effect.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED;
    }

    @Override
    public Component title(AbilityData data) {
        return super.title(data);
    }

    public Optional<AbilityActiveInstance<? extends ActiveAbilityType<?>>> createInstance(SyntheticsPlayer player, AbilityData data, ResourceLocation instanceID) {
        if(data instanceof LastingEffectAbilityInstance.Data activeData) {
            return Optional.of(new LastingEffectAbilityInstance(this, activeData, player, instanceID));
        }
        return Optional.empty();
    }

    public static LastingEffectAbilityInstance.Data create(ActiveAbilityOptions options, List<EffectDetails> onTickEffects, List<EffectDetails> onDeactivateEffects, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new LastingEffectAbilityInstance.Data(options, onTickEffects, onDeactivateEffects, Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }

    public static LastingEffectAbilityInstance.Data create(ActiveAbilityOptions options, List<EffectDetails> onTickEffects, String titlePathOverride, ResourceLocation texturePathOverride) {
        return new LastingEffectAbilityInstance.Data(options, onTickEffects, Collections.emptyList(), Optional.of(titlePathOverride), Optional.of(texturePathOverride));
    }

    public static EffectDetails detailsFrom(Holder<MobEffect> effect, int duration, int amplifier) {
        return new EffectDetails(effect, duration, amplifier, true);
    }
    public static EffectDetails detailsFrom(Holder<MobEffect> effect, int duration, int amplifier, boolean shouldShow) {
        return new EffectDetails(effect, duration, amplifier, shouldShow);
    }

    public record EffectDetails(Holder<MobEffect> effect, int duration, int amplifier, boolean shouldShow) {

        public static EffectDetails of(Holder<MobEffect> effect, int duration, int amplifier) {
            return new EffectDetails(effect, duration, amplifier, true);
        }
        public static EffectDetails of(Holder<MobEffect> effect, int duration, int amplifier, boolean shouldShow) {
            return new EffectDetails(effect, duration, amplifier, shouldShow);
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, EffectDetails> STREAM_CODEC = StreamCodec.composite(
                MobEffect.STREAM_CODEC, EffectDetails::effect,
                ByteBufCodecs.INT, EffectDetails::duration,
                ByteBufCodecs.INT, EffectDetails::amplifier,
                ByteBufCodecs.BOOL, EffectDetails::shouldShow,
                EffectDetails::new);

        public static final MapCodec<EffectDetails> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MobEffect.CODEC.fieldOf("mob_effect").forGetter(EffectDetails::effect),
                Codec.INT.fieldOf("duration").forGetter(EffectDetails::duration),
                Codec.INT.fieldOf("amplifier").forGetter(EffectDetails::amplifier),
                Codec.BOOL.fieldOf("should_show").forGetter(EffectDetails::shouldShow)
        ).apply(instance, EffectDetails::new));

    }
}
