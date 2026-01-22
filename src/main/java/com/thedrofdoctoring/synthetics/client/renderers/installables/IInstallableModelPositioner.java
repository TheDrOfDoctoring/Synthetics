package com.thedrofdoctoring.synthetics.client.renderers.installables;

import net.minecraft.core.Holder;

public interface IInstallableModelPositioner {
    String getModelPosition();

    static <T extends IInstallableModelPositioner> String getModelPosition(Holder<T> holder) {
        return holder.value().getModelPosition();
    }
}
