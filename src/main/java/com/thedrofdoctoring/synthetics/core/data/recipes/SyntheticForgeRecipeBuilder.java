package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SyntheticForgeRecipeBuilder implements RecipeBuilder {

    private final Item result;
    private final int count;
    private final ItemStack resultStack;
    private final List<String> rows;
    private final Map<Character, Ingredient> key;
    private final Map<String, Criterion<?>> criteria;
    private Holder<ResearchNode> requiredResearch;
    private int lavaCost = 50;
    private int recipeTime = 100;

    private String group;


    public SyntheticForgeRecipeBuilder(ItemStack result) {
        this.rows = Lists.newArrayList();
        this.key = Maps.newLinkedHashMap();
        this.criteria = new LinkedHashMap<>();
        this.result = result.getItem();
        this.count = result.getCount();
        this.resultStack = result;

    }
    public static SyntheticForgeRecipeBuilder create(ItemStack result, int count) {
        result.setCount(count);
        return new SyntheticForgeRecipeBuilder(result);
    }

    private ShapedRecipePattern ensureValid(ResourceLocation location) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + location);
        } else {
            return ShapedRecipePattern.of(this.key, this.rows);
        }
    }

    public SyntheticForgeRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(tag));
    }
    public SyntheticForgeRecipeBuilder lavaCost(int lava) {
        this.lavaCost = lava;
        return this;
    }
    public SyntheticForgeRecipeBuilder recipeTime(int recipeTime) {
        this.recipeTime = recipeTime;
        return this;
    }

    public SyntheticForgeRecipeBuilder requiredResearch(HolderLookup<ResearchNode> lookup, ResourceKey<ResearchNode> requiredResearch) {
        this.requiredResearch = lookup.getOrThrow(requiredResearch);
        return this;
    }
    public SyntheticForgeRecipeBuilder requiredResearch(HolderLookup.Provider lookup, ResourceKey<ResearchNode> requiredResearch) {
        this.requiredResearch = lookup.lookupOrThrow(SyntheticsData.RESEARCH_NODES).getOrThrow(requiredResearch);
        return this;
    }

    public SyntheticForgeRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    public SyntheticForgeRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredient);
            return this;
        }
    }

    public SyntheticForgeRecipeBuilder pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != (this.rows.getFirst()).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }
    public @NotNull SyntheticForgeRecipeBuilder unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public @NotNull SyntheticForgeRecipeBuilder group(@javax.annotation.Nullable String groupName) {
        this.group = groupName;
        return this;
    }


    @Override
    public @NotNull Item getResult() {
        return result;
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapedRecipePattern shapedrecipepattern = this.ensureValid(id);

        Map<String, Criterion<?>> criteria = this.criteria;
        criteria.forEach(builder::addCriterion);
        SyntheticForgeRecipe recipe = new SyntheticForgeRecipe(Objects.requireNonNullElse(this.group,""), shapedrecipepattern, resultStack, lavaCost, recipeTime, requiredResearch);

        recipeOutput.accept(id.withPrefix("synthetic_forge/"), recipe, builder.build(id.withPrefix("recipes/synthetic_forge/")));

    }

}
