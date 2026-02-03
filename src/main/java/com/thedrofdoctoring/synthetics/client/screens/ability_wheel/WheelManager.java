package com.thedrofdoctoring.synthetics.client.screens.ability_wheel;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Right now, we only support Ability Wheels, but if that needs to change in the future it shouldn't be too difficult.
 */
public class WheelManager {

    private List<AbilityWheel> levelWheels;

    public WheelManager(List<AbilityWheel> wheels) {
        this.levelWheels = new LinkedList<>(wheels);
    }

    public static final Codec<WheelManager> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<WheelManager, T>> decode(DynamicOps<T> ops, T input) {
            return ops.withDecoder(AbilityWheel.LIST_CODEC).apply(input).map(pair -> new Pair<>(new WheelManager(pair.getFirst()), pair.getSecond()));
        }

        @Override
        public <T> DataResult<T> encode(WheelManager input, DynamicOps<T> ops, T prefix) {
            return ops.withEncoder(AbilityWheel.LIST_CODEC).apply(input.levelWheels);
        }
    };


    public void setWheelsForLevel(List<AbilityWheel> wheels) {
        this.levelWheels = new LinkedList<>(wheels);
    }

    public @NotNull List<AbilityWheel> levelWheels() {
        return levelWheels;
    }
}
