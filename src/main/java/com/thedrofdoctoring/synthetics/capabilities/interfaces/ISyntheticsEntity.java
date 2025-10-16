package com.thedrofdoctoring.synthetics.capabilities.interfaces;

import com.thedrofdoctoring.synthetics.body.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.types.body.AugmentInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISyntheticsEntity {

    boolean canAddAugment(AugmentInstance augment);

    boolean canAddInstallable(IBodyInstallable<?> installable);

    boolean isInstalled(IBodyInstallable<?> installable);


    List<IBodyInstallable<?>> addOrReplaceInstallable(@NotNull IBodyInstallable<?> installable);

    void addAugment(AugmentInstance augment, boolean sync);

    void removeAugment(AugmentInstance augment);


    List<AugmentInstance> getInstalledAugments();

    void onTick();

    LivingEntity getEntity();

    IPartManager getPartManager();

    void onUpdate(boolean sync);

    void sync(boolean all);
}
