package com.thedrofdoctoring.synthetics.capabilities.interfaces;

import com.thedrofdoctoring.synthetics.abilities.IBodyInstallable;
import com.thedrofdoctoring.synthetics.core.data.types.body.augments.AppliedAugmentInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISyntheticsEntity {

    boolean canAddAugment(AppliedAugmentInstance augment);

    boolean canAddInstallable(IBodyInstallable<?> installable);

    boolean isInstalled(IBodyInstallable<?> installable);


    List<IBodyInstallable<?>> addOrReplaceInstallable(@NotNull IBodyInstallable<?> installable);

    void addAugment(AppliedAugmentInstance augment, boolean sync);

    void removeAugment(AppliedAugmentInstance augment);


    List<AppliedAugmentInstance> getInstalledAugments();

    void onTick();

    LivingEntity getEntity();

    IPartManager getPartManager();

    void onUpdate(boolean sync);

    void sync(boolean all);
}
