package com.thedrofdoctoring.synthetics.abilities.passive.instances;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.abilities.AbilityData;
import com.thedrofdoctoring.synthetics.abilities.passive.types.PassiveInvisibilityType;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public class PassiveInvisibilityInstance extends AbilityPassiveInstance<PassiveInvisibilityType> {
    public PassiveInvisibilityInstance(PassiveInvisibilityType ability, SyntheticsPlayer player, Data data, ResourceLocation instanceID, boolean powerDraw) {
        super(ability, player, data, instanceID, powerDraw);
    }

    public static class Data extends AbilityData {

        private final PassiveInvisibilityType.InvisibilityType type;

        public Data(PassiveInvisibilityType.InvisibilityType type) {
            super(1);
            this.type = type;
        }

        public PassiveInvisibilityType.InvisibilityType type() {
            return type;
        }


        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StringRepresentable.fromEnum(PassiveInvisibilityType.InvisibilityType::values).fieldOf("invis_type").forGetter(Data::type)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                PassiveInvisibilityType.InvisibilityType.STREAM_CODEC, Data::type,
                Data::new);


        @Override
        public MapCodec<Data> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, Data> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
