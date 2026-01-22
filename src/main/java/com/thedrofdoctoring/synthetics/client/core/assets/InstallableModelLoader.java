package com.thedrofdoctoring.synthetics.client.core.assets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.thedrofdoctoring.synthetics.client.renderers.installables.IInstallableModel;
import com.thedrofdoctoring.synthetics.client.renderers.installables.InstallableBakedModel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InstallableModelLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final InstallableModelLoader INSTANCE = new InstallableModelLoader();

    private final Map<ResourceLocation, IInstallableModel> installableModels = new HashMap<>();
    private final Set<ResourceLocation> requestedModels = new HashSet<>();
    private final Map<ResourceLocation, ModelResourceLocation> modelLocationCache = new HashMap<>();

    private final Map<ResourceLocation, ResourceLocation> augmentLookup = new HashMap<>();
    private final Map<ResourceLocation, ResourceLocation> segmentLookup = new HashMap<>();
    private final Map<ResourceLocation, ResourceLocation> bodyPartLookup = new HashMap<>();

    private InstallableModelLoader() {
        super(new Gson(), "synthetics/models");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> modelJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        clear();
        int loaded = 0;

        for (var entry : modelJsons.entrySet()) {
            try {
                var installable = InstallableBakedModel.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                        .getOrThrow(JsonParseException::new);

                installableModels.put(entry.getKey(), installable);
                requestedModels.add(installable.model());

                loaded++;
            } catch (Exception e) {
                LOGGER.error("Failed loading installable model {}", entry.getKey(), e);
            }
        }

        LOGGER.info("Loaded {} installable models", loaded);
    }

    protected void clear() {
        modelLocationCache.clear();
        installableModels.clear();
        requestedModels.clear();
        augmentLookup.clear();
        segmentLookup.clear();
        bodyPartLookup.clear();
    }

    public ResourceLocation getAugmentLocation(ResourceLocation location) {
        return augmentLookup.computeIfAbsent(location, l -> l.withPrefix("augment/"));
    }

    public ResourceLocation getSegmentLocation(ResourceLocation location) {
        return segmentLookup.computeIfAbsent(location, l -> l.withPrefix("segment/"));
    }

    public ResourceLocation getBodyPartLocation(ResourceLocation location) {
        return bodyPartLookup.computeIfAbsent(location, l -> l.withPrefix("bodypart/"));
    }

    public @Nullable IInstallableModel get(ResourceLocation location) {
        return installableModels.get(location);
    }

    public Optional<IInstallableModel> getOptional(ResourceLocation location) {
        return Optional.ofNullable(get(location));
    }

    public Collection<ResourceLocation> requestedModels() {
        return requestedModels;
    }

    public ModelResourceLocation getModelLocation(ResourceLocation location) {
        return modelLocationCache.computeIfAbsent(location, ModelResourceLocation::standalone);
    }
}
