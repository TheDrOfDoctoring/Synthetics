package com.thedrofdoctoring.synthetics.client.renderers.installables;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3fc;

public interface IInstallableModel {
    ResourceLocation model();
    Vector3fc translation();

}
