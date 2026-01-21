package com.thedrofdoctoring.synthetics.client.core.assets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.thedrofdoctoring.synthetics.client.renderers.installables.InstallableBakedModel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InstallableModelLoader extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final InstallableModelLoader INSTANCE = new InstallableModelLoader();

    private final Map<ResourceLocation, InstallableBakedModel> installableModels = new HashMap<>();
    private final Set<ResourceLocation> requestedModels = new HashSet<>();
    private final Map<ResourceLocation, ModelResourceLocation> modelLocationCache = new HashMap<>();

    private InstallableModelLoader() {
        super(new Gson(), "synthetics/models");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> modelJsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        modelLocationCache.clear();
        installableModels.clear();
        requestedModels.clear();

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

    public InstallableBakedModel get(ResourceLocation location) {
        return installableModels.get(location);
    }

    public Collection<ResourceLocation> requestedModels() {
        return requestedModels;
    }

    public ModelResourceLocation getModelLocation(ResourceLocation location) {
        return modelLocationCache.computeIfAbsent(location, ModelResourceLocation::standalone);
    }

}
