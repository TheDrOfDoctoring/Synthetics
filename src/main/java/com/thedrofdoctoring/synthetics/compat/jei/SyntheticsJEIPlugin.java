package com.thedrofdoctoring.synthetics.compat.jei;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.core.data.recipes.SyntheticForgeRecipe;
import com.thedrofdoctoring.synthetics.core.data.recipes.SyntheticsRecipes;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import com.thedrofdoctoring.synthetics.menus.SyntheticForgeMenu;
import com.thedrofdoctoring.synthetics.menus.SyntheticsMenus;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("unused")
@JeiPlugin
public class SyntheticsJEIPlugin implements IModPlugin {

    private static final ResourceLocation SYNTHETICS_JEI = Synthetics.rl("synthetics_jei");
    public static final RecipeType<SyntheticForgeRecipe> FORGE_CATEGORY = RecipeType.create(Synthetics.MODID, "synthetic_forge", SyntheticForgeRecipe.class);
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return SYNTHETICS_JEI;
    }


    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SyntheticForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            RecipeManager recipeManager = world.getRecipeManager();
            registration.addRecipes(FORGE_CATEGORY, recipeManager.getAllRecipesFor(SyntheticsRecipes.SYNTHETIC_FORGE_RECIPE.get()).stream().map(RecipeHolder::value).toList());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(SyntheticsBlocks.SYNTHETIC_FORGE.get()), FORGE_CATEGORY);
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(SyntheticForgeMenu.class, SyntheticsMenus.SYNTHETIC_FORGE.get(), FORGE_CATEGORY, 1, 9, 10, 36);

    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SyntheticsItems.AUGMENT_INSTALLABLE.get(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context) {
                ingredient.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
                if(ingredient.getItem() instanceof InstallableItem<?> item) {
                    return item.getInstallableComponent(ingredient);
                }

                return null;
            }

            @Override
            public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
                return "augment_subtype";
            }
        });
        registration.registerSubtypeInterpreter(SyntheticsItems.BODY_PART_INSTALLABLE.get(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context) {
                ingredient.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
                if(ingredient.getItem() instanceof InstallableItem<?> item) {
                    return item.getInstallableComponent(ingredient);
                }

                return null;
            }

            @Override
            public @NotNull String getLegacyStringSubtypeInfo(@NotNull ItemStack ingredient, @NotNull UidContext context) {
                return "body_part_subtype";
            }
        });
    }
}
