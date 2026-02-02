package com.thedrofdoctoring.synthetics.client.core.assets.gen;

import com.mojang.serialization.JsonOps;
import com.thedrofdoctoring.synthetics.client.renderers.installables.IInstallableModelSupplier;
import com.thedrofdoctoring.synthetics.client.renderers.installables.InstallableBakedModel;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class InstallableModelProvider implements DataProvider {

    public static final String MODEL_PATH = "synthetics/models/";

    private final SubProvider<?>[] subProviders;

    public InstallableModelProvider(SubProvider<?>... subProviders ) {
        this.subProviders = subProviders;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {

        return CompletableFuture.allOf(
                Arrays.stream(subProviders)
                        .map(SubProvider::generate)
                        .map(p -> p.saveAll(output) )
                        .toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "Installable Models";
    }

    public static abstract class SubProvider<I extends IInstallableModelSupplier> {
        private final PackOutput.PathProvider pathProvider;
        private final Map<ResourceLocation, InstallableBakedModel> models;

        public SubProvider(PackOutput output, String subPath) {
            this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, MODEL_PATH + subPath);
            models = new HashMap<>();
        }

        public abstract void addModels();

        public SubProvider<I> generate() {
            addModels();

            return this;
        }

        public void model(ResourceLocation installable, ResourceLocation model, Vector3f translation) {
            models.put(installable, new InstallableBakedModel(model, translation));
        }

        public void model(String installableLocation, String modelLocation, Vector3f translation) {
            models.put(ResourceLocation.parse(installableLocation), new InstallableBakedModel(ResourceLocation.parse(modelLocation), translation));
        }

        public CompletableFuture<?> saveAll(CachedOutput output) {
            return CompletableFuture.allOf(models.entrySet().stream()
                    .map(entry -> saveEntry(output, entry)
                    ).toArray(CompletableFuture[]::new));
        }

        protected CompletableFuture<?> saveEntry(CachedOutput output, Map.Entry<ResourceLocation, InstallableBakedModel> entry) {
            return DataProvider.saveStable(output,
                    InstallableBakedModel.CODEC
                            .encodeStart(JsonOps.INSTANCE, entry.getValue())
                            .getOrThrow(),
                    pathProvider.json(entry.getKey())
            );
        }
    }




}
