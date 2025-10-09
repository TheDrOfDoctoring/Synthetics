package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IBodyInstallable<T extends IBodyInstallable<T>> {


    Optional<HolderSet<SyntheticAbility>> abilities();

    ResourceLocation id();

    ResourceKey<Registry<T>> getType();

    @NotNull ItemStack createDefaultItemStack();

    default ResourceLocation texture() {
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/item/installables/" + getType().location().getPath()+ "/" + id().getPath() +".png");
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

}
