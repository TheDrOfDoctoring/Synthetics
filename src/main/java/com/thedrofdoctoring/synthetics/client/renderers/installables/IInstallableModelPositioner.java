package com.thedrofdoctoring.synthetics.client.renderers.installables;

import net.minecraft.core.Holder;

public interface IInstallableModelPositioner {
    IBodyPosition getModelPosition();

    static <T extends IInstallableModelPositioner> IBodyPosition getModelPosition(Holder<T> holder) {
        return holder.value().getModelPosition();
    }
}
