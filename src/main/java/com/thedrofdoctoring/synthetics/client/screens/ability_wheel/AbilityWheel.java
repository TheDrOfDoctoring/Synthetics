package com.thedrofdoctoring.synthetics.client.screens.ability_wheel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.client.screens.ability_wheel.radial.slot.AbilityRadialSlot;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.HolderSet;

import java.util.List;

public record AbilityWheel(List<Slot> slots) implements IWheel<AbilityRadialSlot, Ability> {

    public static final MapCodec<AbilityWheel> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Slot.CODEC.codec().listOf().fieldOf("slots").forGetter(AbilityWheel::slots)
    ).apply(instance, AbilityWheel::new));

    public static final Codec<List<AbilityWheel>> LIST_CODEC = CODEC.codec().listOf();


    public record Slot(HolderSet<Ability> abilities) {
        public static final MapCodec<Slot> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ability.SET_CODEC.fieldOf("abilities").forGetter(Slot::abilities)
        ).apply(instance, Slot::new));


    }

    public static final Slot EMPTY_SLOT = new Slot(HolderSet.empty());


}
