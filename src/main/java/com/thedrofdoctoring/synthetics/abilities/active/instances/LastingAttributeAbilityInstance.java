package com.thedrofdoctoring.synthetics.abilities.active.instances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.active.types.LastingAttributeAbility;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.ActiveAbilityOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class LastingAttributeAbilityInstance extends AbilityActiveInstance<LastingAttributeAbility> {

    private final List<LastingAttributeAbility.Modifier> modifiers;

    public LastingAttributeAbilityInstance(LastingAttributeAbility ability, Data activeData, SyntheticsPlayer player, ResourceLocation instanceID) {
        super(ability, activeData, player, instanceID);
        this.modifiers = activeData.modifiers;
    }

    public Iterable<LastingAttributeAbility.Modifier> modifiers() {
        return modifiers;
    }

    public static class Data extends AbilityActiveInstance.Data {

        private final List<LastingAttributeAbility.Modifier> modifiers;

        public Data(ActiveAbilityOptions options, List<LastingAttributeAbility.Modifier> modifiers,
                    Optional<String> titlePathOverride, Optional<ResourceLocation> texturePathOverride) {
            super(1, options, titlePathOverride, texturePathOverride);
            this.modifiers = modifiers;
        }

        public List<LastingAttributeAbility.Modifier> modifiers() {
            return modifiers;
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ActiveAbilityOptions.STREAM_CODEC, Data::options,
                LastingAttributeAbility.Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), Data::modifiers,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Data::titlePathOverride,
                ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), Data::texturePathOverride,
                Data::new);

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ActiveAbilityOptions.CODEC.fieldOf("active_ability").forGetter(Data::options),
                LastingAttributeAbility.Modifier.CODEC.codec().listOf().fieldOf("modifiers").forGetter(Data::modifiers),
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
