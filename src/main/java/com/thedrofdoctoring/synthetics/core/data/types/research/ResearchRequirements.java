package com.thedrofdoctoring.synthetics.core.data.types.research;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public record ResearchRequirements(int experienceCost, Optional<List<Pair<Ingredient, Integer>>> requiredItems) {

    public static final MapCodec<ResearchRequirements> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("experience_cost", 0).forGetter(ResearchRequirements::experienceCost),
            Codec.pair(
                    Ingredient.CODEC.fieldOf("item").codec(),
                    Codec.INT.fieldOf("amount").codec()
            ).listOf().optionalFieldOf("required_items").forGetter(ResearchRequirements::requiredItems)
    ).apply(instance, ResearchRequirements::new));


    public static ResearchRequirements create(int experienceCost) {
        return new ResearchRequirements(experienceCost, Optional.empty());
    }
    public static ResearchRequirements create(int experienceCost, List<Pair<Ingredient, Integer>> requiredItems) {
        return new ResearchRequirements(experienceCost, Optional.of(requiredItems));
    }
    public static ResearchRequirements create(List<Pair<Ingredient, Integer>> requiredItems) {
        return new ResearchRequirements(0, Optional.of(requiredItems));
    }


}
