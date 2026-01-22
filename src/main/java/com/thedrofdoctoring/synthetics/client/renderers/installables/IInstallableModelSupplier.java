package com.thedrofdoctoring.synthetics.client.renderers.installables;

import com.thedrofdoctoring.synthetics.client.core.assets.InstallableModelLoader;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IInstallableModelSupplier {
    Optional<IInstallableModel> getInstallableModel();

    static Optional<IInstallableModel> getInstallableModel(ResourceLocation location) {
        return InstallableModelLoader.INSTANCE.getOptional(location);
    }

    static <T extends IInstallableModelSupplier> Optional<IInstallableModel> getInstallableModel(Holder<T> holder) {
        return holder.unwrap()
                .mapLeft(key -> IInstallableModelSupplier.getInstallableModel(key.location()))
                .mapRight(IInstallableModelSupplier::getInstallableModel)
                .orThrow();
    }

}
