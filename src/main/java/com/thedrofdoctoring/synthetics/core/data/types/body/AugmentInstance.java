package com.thedrofdoctoring.synthetics.core.data.types.body;

import com.mojang.datafixers.util.Pair;
import com.thedrofdoctoring.synthetics.Synthetics;
import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.SyntheticsData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public record AugmentInstance(SyntheticAugment augment, BodyPart appliedPart) implements IBodyInstallable<SyntheticAugment> {


    public String createSerialisationID() {
        return augment.augmentID().toString() + ';' + appliedPart.id().toString();
    }

    public static Pair<ResourceLocation, ResourceLocation> augmentPartSplitIdentifiers(String string) {

        String[] strings = string.split(";");
        if(strings.length < 2) {

            ResourceLocation possibleAugmentLocation = ResourceLocation.tryParse(string);
            if(possibleAugmentLocation != null && !possibleAugmentLocation.getNamespace().contains("minecraft")) {
                Synthetics.LOGGER.warn("Unable to parse body part data, resorting to default");
                return Pair.of(possibleAugmentLocation, null);
            } else {
                Synthetics.LOGGER.warn("Unable to parse augment instance data");
                return Pair.of(null, null);
            }

        }
        String augment = strings[0];
        String part = strings[1];
        ResourceLocation augmentLocation = ResourceLocation.tryParse(augment);
        ResourceLocation bodyPartLocation = ResourceLocation.tryParse(part);
        return Pair.of(augmentLocation, bodyPartLocation);
    }

    @Override
    public Optional<HolderSet<SyntheticAbility>> abilities() {
        return augment.abilities();
    }

    @Override
    public ResourceLocation id() {
        return Synthetics.rl("augment_instance");
    }

    @Override
    public ResourceKey<Registry<SyntheticAugment>> getType() {
        return SyntheticsData.AUGMENTS;
    }

    @Override
    public @NotNull ItemStack createDefaultItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack createDefaultItemStack(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }
}
