package com.thedrofdoctoring.synthetics.compat.jei;

import com.thedrofdoctoring.synthetics.client.screens.menu_screens.SyntheticForgeScreen;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.data.recipes.SyntheticForgeRecipe;
import com.thedrofdoctoring.synthetics.core.data.types.research.ResearchNode;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SyntheticForgeRecipeCategory implements IRecipeCategory<SyntheticForgeRecipe> {

    private final IGuiHelper helper;
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final @NotNull IDrawableAnimated arrow;
    @Override
    public @NotNull RecipeType<SyntheticForgeRecipe> getRecipeType() {
        return SyntheticsJEIPlugin.FORGE_CATEGORY;

    }



    public SyntheticForgeRecipeCategory(IGuiHelper guiHelper) {
        this.helper = guiHelper;
        this.background = helper.drawableBuilder(SyntheticForgeScreen.BACKGROUND, 4, 4, 165, 78).addPadding(0, 0, 0, 0).build();
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SyntheticsBlocks.SYNTHETIC_FORGE.get()));
        this.arrow = helper.drawableBuilder(SyntheticForgeScreen.LAVA_ARROW, 0, 0, 22, 15).buildAnimated(600, IDrawableAnimated.StartDirection.LEFT, false);
    }

        @Override
    public @NotNull Component getTitle() {
        return Component.translatable("menu.title.synthetics.synthetic_forge");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SyntheticForgeRecipe recipe, @NotNull IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getPattern().ingredients();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 31).addItemStack(recipe.getResult());
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                builder.addSlot(RecipeIngredientRole.INPUT, 26 + j * 18, 13 + i * 18).addIngredients(ingredients.get(j + i * 3));
            }
        }
    }


    @Override
    public void draw(@NotNull SyntheticForgeRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {


        this.arrow.draw(guiGraphics, 0, 0);
        float lavaPercentage = Math.min(1, (float) recipe.getLavaCost() / 1000);
        int lavaHeight = SyntheticForgeScreen.LAVA_HEIGHT;
        int lavaStartY = (int) (lavaHeight + 13 - Math.ceil(lavaHeight * lavaPercentage));
        int lavaEndHeight = (int) Math.ceil(lavaHeight * lavaPercentage);


        guiGraphics.blit(SyntheticForgeScreen.LAVA_FLOW, 4, lavaStartY, 0, 20, SyntheticForgeScreen.LAVA_WIDTH, lavaEndHeight, 16, 320);
        Holder<ResearchNode> requiredResearch = recipe.requiredResearch();
        if(requiredResearch != null) {
            MutableComponent requirement = Component.translatable("jei.synthetics.research_required");
            requirement.append(requiredResearch.value().title());
            requirement.withStyle(ChatFormatting.DARK_GRAY);
            guiGraphics.pose().pushPose();

            guiGraphics.pose().scale(0.6f, 0.6f, 1f);
            guiGraphics.pose().translate(0, 0, 500);

            guiGraphics.drawString(Minecraft.getInstance().font, requirement, 2, 120, -1, false);

            guiGraphics.pose().popPose();
        }


        if(mouseX >= 4 && mouseX < 4 + SyntheticForgeScreen.LAVA_WIDTH && mouseY >= lavaStartY && mouseY < lavaStartY + lavaEndHeight) {
            Component component = Component.translatable("jei.synthetics.required_lava", recipe.getLavaCost()).withStyle(ChatFormatting.WHITE);
            guiGraphics.pose().pushPose();

            guiGraphics.fillGradient(4, 13, 4 + SyntheticForgeScreen.LAVA_WIDTH, 65, -2130706433, -2130706433);

            guiGraphics.pose().scale(0.8f, 0.8f, 1f);
            guiGraphics.pose().translate(0, 0, 500);
            guiGraphics.drawString(Minecraft.getInstance().font, component, 3, 5, -1);
            guiGraphics.pose().popPose();

        }



    }
}
