package com.thedrofdoctoring.synthetics.client.renderers.installables;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.thedrofdoctoring.synthetics.capabilities.PartManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.AppliedAugmentInstance;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodyPart;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.BodySegment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3fc;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class InstallableRenderLayer<E extends LivingEntity, M extends HumanoidModel<E>> extends RenderLayer<E, M> {

    private final Map<String, ModelPart> modelPartLookupCache = new HashMap<>();

    public InstallableRenderLayer(RenderLayerParent<E, M> renderer) {
        super(renderer);
        M model = renderer.getModel();
        modelPartLookupCache.put("head", model.head);
        modelPartLookupCache.put("body", model.body);
        modelPartLookupCache.put("left_arm", model.leftArm);
        modelPartLookupCache.put("right_arm", model.rightArm);
        modelPartLookupCache.put("left_leg", model.leftLeg);
        modelPartLookupCache.put("right_leg", model.rightLeg);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        SyntheticsPlayer data = entity.getData(SyntheticsAttachments.SYNTHETICS_MANAGER);
        PartManager partManager = data.getPartManager();
        Collection<BodyPart> parts = partManager.getInstalledParts();

        RenderType renderType = RenderType.cutout();
        VertexConsumer buf = bufferSource.getBuffer(renderType);
        parts.forEach(p ->
                p.installableModel()
                        .ifPresent(
                                model -> renderInstallable(poseStack, buf, packedLight, partialTick, model, model.renderLocation(), renderType)
                        )
        );
        Collection<BodySegment> segments = partManager.getInstalledSegments();
        segments.forEach(s ->
                s.installableModel()
                        .ifPresent(
                                model -> renderInstallable(poseStack, buf, packedLight, partialTick, model, model.renderLocation(), renderType)
                        )
        );
        List<AppliedAugmentInstance> augments = data.getInstalledAugments();
        augments.forEach(a ->
                a.installableModel()
                        .ifPresent(
                                model -> renderInstallable(poseStack, buf, packedLight, partialTick, model, model.renderLocation(), renderType)
                        )
        );
    }

    public void renderInstallable(PoseStack poseStack, VertexConsumer buf, int packedLight, float partialTick, IInstallableModel installableModel, String renderLocation, RenderType renderType) {
        poseStack.pushPose();

        ModelPart modelPart = getPart(renderLocation);
        BakedModel bakedModel = installableModel.model();
        // Move pose to ModelPart
        modelPart.translateAndRotate(poseStack);
        Vector3fc offset = installableModel.translation();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        Minecraft.getInstance()
                .getBlockRenderer()
                .getModelRenderer()
                .renderModel(poseStack.last(), buf, null, bakedModel, 1f, 1f, 1f, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);

        poseStack.popPose();
    }

    protected ModelPart getPart(String name) {
        return modelPartLookupCache.get(name);
    }
}
