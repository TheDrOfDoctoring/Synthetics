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
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InstallableModelLoader implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final InstallableModelLoader INSTANCE = new InstallableModelLoader();

    private final Gson gson;
    private final String directory;

    private final Map<ResourceLocation, IInstallableModel> installableModels = new HashMap<>();
    private final Set<ResourceLocation> requestedModels = new HashSet<>();
    private final Map<ResourceLocation, ModelResourceLocation> modelLocationCache = new HashMap<>();

    private final Map<ResourceLocation, ResourceLocation> augmentLookup = new HashMap<>();
    private final Map<ResourceLocation, ResourceLocation> segmentLookup = new HashMap<>();
    private final Map<ResourceLocation, ResourceLocation> bodyPartLookup = new HashMap<>();

    private InstallableModelLoader() {
        this.gson = new Gson();
        this.directory = "synthetics/models";
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier stage,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(
                ()-> prepare(resourceManager, reloadProfiler),
                gameExecutor
        ).thenAcceptAsync(
                elements -> load(elements, reloadProfiler),
                gameExecutor
        ).thenCompose(stage::wait);
    }

    protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push("Synthetics Models Prepare");
        clear();
        Map<ResourceLocation, JsonElement> elements = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, this.directory, this.gson, elements);
        profiler.pop();
        return elements;
    }

    protected void load(Map<ResourceLocation, JsonElement> modelJsons, ProfilerFiller profiler) {
        int loaded = 0;

        profiler.push("Synthetics Models Load");
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
        profiler.pop();
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
