package com.thedrofdoctoring.synthetics.body.abilities;

import com.thedrofdoctoring.synthetics.core.data.types.body.SyntheticAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IBodyInstallable {


    Optional<HolderSet<SyntheticAbility>> abilities();

    ResourceLocation id();


    static ResourceLocation texture(ResourceLocation location) {
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/installables/" + location.getPath() +".png");
    }
    default ResourceLocation texture() {
        return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), "textures/installables/" + id().getPath() +".png");
    }
    default Component title() {
        return Component.translatable("installables." + id().getNamespace() + "." + id().getPath()).withStyle(ChatFormatting.GRAY);
    }

}
