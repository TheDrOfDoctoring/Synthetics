package com.thedrofdoctoring.synthetics.core.data.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SyntheticForgeRecipe extends ShapedRecipe {

    private final ItemStack result;
    private final ShapedRecipePattern pattern;
    private final int lavaCost;
    private final int recipeTime;
    private final Holder<ResearchNode> requiredResearch;

    public SyntheticForgeRecipe(String group, ShapedRecipePattern pattern, ItemStack result, int lavaCost, int recipeTime, Holder<ResearchNode> requiredResearch) {
        super(group, CraftingBookCategory.MISC, pattern, result, false);
        this.result = result;
        this.pattern = pattern;
        this.lavaCost = lavaCost;
        this.recipeTime = recipeTime;
        this.requiredResearch = requiredResearch;
    }

    public int getRecipeTime() {
        return recipeTime;
    }

    public int getLavaCost() {
        return lavaCost;
    }
    public ItemStack getResult() {
        return result;
    }

    public Holder<ResearchNode> getRequiredResearch() {
        return requiredResearch;
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    @Override
    public @NotNull RecipeType<SyntheticForgeRecipe> getType() {
        return SyntheticsRecipes.SYNTHETIC_FORGE_RECIPE.get();
    }

    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public @NotNull RecipeSerializer<SyntheticForgeRecipe> getSerializer() {
        return SyntheticsRecipes.SYNTHETIC_FORGE_SERIALISER.get();
    }

    public static class Serializer implements RecipeSerializer<SyntheticForgeRecipe> {
        public static final MapCodec<SyntheticForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedRecipe::getGroup),
                ShapedRecipePattern.MAP_CODEC.forGetter(SyntheticForgeRecipe::getPattern),
                ItemStack.CODEC.fieldOf("result").forGetter(SyntheticForgeRecipe::getResult),
                Codec.INT.fieldOf("lava_cost").forGetter(SyntheticForgeRecipe::getLavaCost),
                Codec.INT.fieldOf("recipe_time").forGetter(SyntheticForgeRecipe::getRecipeTime),
                ResearchNode.HOLDER_CODEC.fieldOf("required_research").forGetter(SyntheticForgeRecipe::getRequiredResearch)
        ).apply(instance, SyntheticForgeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SyntheticForgeRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public @NotNull MapCodec<SyntheticForgeRecipe> codec() {
            return CODEC;
        }

        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SyntheticForgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static final Holder<ResearchNode> NULL_RESEARCH = Holder.direct(new ResearchNode(Optional.empty(), null, null, 0, 0, Synthetics.rl("null_research_node")));
        private static SyntheticForgeRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            String s = buffer.readUtf();
            ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int lavaCost = buffer.readInt();
            int recipeTime = buffer.readInt();
            ResourceLocation research = buffer.readResourceLocation();
            Holder<ResearchNode> node = buffer.registryAccess().lookupOrThrow(SyntheticsData.RESEARCH_NODES).getOrThrow(ResourceKey.create(SyntheticsData.RESEARCH_NODES, research));

            return new SyntheticForgeRecipe(s, pattern, result, lavaCost, recipeTime, node);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, SyntheticForgeRecipe recipe) {
            buffer.writeUtf(recipe.getGroup());
            ShapedRecipePattern.STREAM_CODEC.encode(buffer, recipe.pattern);
            ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            buffer.writeInt(recipe.lavaCost);
            buffer.writeInt(recipe.recipeTime);
            buffer.writeResourceLocation(recipe.requiredResearch.value().id());


        }
    }
}
