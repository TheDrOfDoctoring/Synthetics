package com.thedrofdoctoring.synthetics.client.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilityKeyManager {

    private Map<Integer, Holder<Ability>> intToAbilityMap;

    private static final Codec<Map<Integer, Holder<Ability>>> MAP_CODEC = Codec.unboundedMap(Codec.STRING.xmap(Integer::valueOf, i -> Integer.toString(i)), Ability.HOLDER_CODEC);

    public AbilityKeyManager(Map<Integer, Holder<Ability>> intToAbilityMap) {
        this.intToAbilityMap = new HashMap<>(intToAbilityMap);
    }

    public static final Codec<AbilityKeyManager> CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<AbilityKeyManager, T>> decode(DynamicOps<T> ops, T input) {
            return ops.withDecoder(MAP_CODEC).apply(input).map(pair -> new Pair<>(new AbilityKeyManager(pair.getFirst()), pair.getSecond()));
        }
        @Override
        public <T> DataResult<T> encode(AbilityKeyManager input, DynamicOps<T> ops, T prefix) {
            return ops.withEncoder(MAP_CODEC).apply(input.intToAbilityMap);
        }
    };



    public @Nullable Ability getBoundAbility(int index) {
        Holder<Ability> holder = intToAbilityMap.get(index);
        return holder == null ? null : holder.value();
    }

    public @Nullable Holder<Ability> getBoundAbilityHolder(int index) {
        return intToAbilityMap.get(index);
    }

    public void setAbilityBinds(@NotNull Map<Integer, Holder<Ability>> binds) {
        this.intToAbilityMap = binds;
    }



}
