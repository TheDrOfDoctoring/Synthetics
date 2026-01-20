package com.thedrofdoctoring.synthetics.client.renderers.installables;

import net.minecraft.client.resources.model.BakedModel;
import org.joml.Vector3fc;

public interface IInstallableModel {
    BakedModel model();
    Vector3fc translation();
    String renderLocation();

}
