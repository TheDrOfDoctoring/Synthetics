package com.thedrofdoctoring.synthetics.capabilities.interfaces;

import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISyntheticsEntity {

    boolean canAddInstallable(IBodyInstallable<?> installable);

    boolean isInstalled(IBodyInstallable<?> installable);

    List<IBodyInstallable<?>> addOrReplaceInstallable(@NotNull IBodyInstallable<?> installable);

    boolean removeInstallable(IBodyInstallable<?> installable);

    void onTick();

    LivingEntity getEntity();

    IPartManager parts();

    void onUpdate(boolean sync);

    boolean sync(boolean all);
}
