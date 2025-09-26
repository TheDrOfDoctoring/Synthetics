package com.thedrofdoctoring.synthetics.capabilities;

import com.thedrofdoctoring.synthetics.core.data.types.AugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.SyntheticAugment;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ISyntheticsEntity {

    boolean canAddAugment(SyntheticAugment augment);

    void addAugment(SyntheticAugment augment, boolean sync);

    void removeAugment(SyntheticAugment augment);

    boolean isAugmentInstalled(SyntheticAugment augment);

    List<AugmentInstance> getInstalledAugments();

    void onTick();

    LivingEntity getEntity();

    void onUpdate(boolean sync);

    void sync(boolean all);
}
