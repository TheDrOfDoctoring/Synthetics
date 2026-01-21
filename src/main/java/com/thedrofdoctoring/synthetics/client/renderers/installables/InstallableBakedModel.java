package com.thedrofdoctoring.synthetics.client.renderers.installables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public record InstallableBakedModel(ResourceLocation model, Vector3f translation) implements IInstallableModel {
    public static final Codec<InstallableBakedModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(InstallableBakedModel::model),
            ExtraCodecs.VECTOR3F.fieldOf("translation").forGetter(InstallableBakedModel::translation)
    ).apply(instance, InstallableBakedModel::new));

}
