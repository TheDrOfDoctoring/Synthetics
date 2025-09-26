package com.thedrofdoctoring.synthetics.util;

import com.thedrofdoctoring.synthetics.Synthetics;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class Helper {

    public static <T> T retrieveDataObject(ResourceLocation location, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup, boolean shouldThrow) {
        if(location == null) return null;
        var objectOpt = lookup.get(ResourceKey.create(registryKey, location));
        if(objectOpt.isPresent()) {
            return objectOpt.get().value();
        } else if(shouldThrow) {
            Synthetics.LOGGER.warn("Unable to retrieve data registry object with resource location {}", location);

        }
        return null;

    }
    public static <T> T retrieveDataObject(ResourceLocation location, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup) {
        return retrieveDataObject(location, registryKey, lookup, true);
    }
    public static <T> T retrieveDataObject(String string, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup) {
        ResourceLocation location = ResourceLocation.tryParse(string);
        return retrieveDataObject(location, registryKey, lookup);
    }
    public static <T> T retrieveDataObject(String string, ResourceKey<Registry<T>> registryKey, HolderGetter<T> lookup, boolean shouldThrow) {
        ResourceLocation location = ResourceLocation.tryParse(string);
        return retrieveDataObject(location, registryKey, lookup, shouldThrow);
    }

}
