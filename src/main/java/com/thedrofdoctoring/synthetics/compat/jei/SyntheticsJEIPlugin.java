package com.thedrofdoctoring.synthetics.compat.jei;

import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.core.SyntheticsItems;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
@SuppressWarnings("unused")
@JeiPlugin
public class SyntheticsJEIPlugin implements IModPlugin {

    private static final ResourceLocation SYNTHETICS_JEI = Synthetics.rl("synthetics_jei");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return SYNTHETICS_JEI;
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(SyntheticsItems.AUGMENT_INSTALLABLE.get(), new ISubtypeInterpreter<>() {
            @Override
            public @Nullable Object getSubtypeData(@NotNull ItemStack ingredient, @NotNull UidContext context) {

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
