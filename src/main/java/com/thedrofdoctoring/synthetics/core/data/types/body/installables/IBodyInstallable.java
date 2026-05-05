package com.thedrofdoctoring.synthetics.core.data.types.body.installables;

import com.thedrofdoctoring.synthetics.core.data.types.body.ability.Ability;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public sealed interface IBodyInstallable<T extends IBodyInstallable<T>> permits BodyPart, BodySegment, Augment, AppliedAugmentInstance {


    Optional<HolderSet<Ability>> abilities();

    /**
     * @return - A unique, resource location ID, that matches the containing json
     * For example, an augment within the datapack "synthetics", with the json file name "example_augment", should have an id of synthetics:example_augment
     */
    ResourceLocation id();

    ResourceKey<Registry<T>> getType();

    /**
     * Prefer {@link #createDefaultItemStack(HolderLookup.Provider)}
     * @return An ItemStack with a direct holder version of the installable type component.
     */
    @SuppressWarnings("unused")
    @NotNull ItemStack createDefaultItemStack();

    /**
     * @return An ItemStack with a reference holder version of the installable type component.
     */
    @NotNull ItemStack createDefaultItemStack(HolderLookup.Provider provider);

    default ResourceLocation texture() {
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/item/installables/" + getType().location().getPath()+ "/" + id().getPath() +".png");
    }

    default ResourceLocation entityLayerTexture(boolean slim) {
        if(slim) {
            return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/entity/installables/skin/slim/" + getType().location().getPath()+ "/" + id().getPath() +".png");
        }
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/entity/installables/skin/wide/" + getType().location().getPath()+ "/" + id().getPath() +".png");

    }

    default ResourceLocation itemTexture() {
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "item/installables/" + getType().location().getPath()+ "/" + id().getPath());
    }
    default Component title() {
        return Component.translatable("installables." + id().getNamespace() + "." + getType().location().getPath() + "." + id().getPath());
    }
    default Component typeTitleID() {
        return Component.translatable("installables." + id().getNamespace() + "." + getType().location().getPath() + "." + "type_title");
    }
    //TODO:
    // Some kind of dynamic tooltip system to determine hover information based on installable instance, to replace hardcoded screen & hover text.

}
