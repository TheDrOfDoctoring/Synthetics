package com.thedrofdoctoring.synthetics.client.renderers.installables;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.PartManager;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.SyntheticsAttachments;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
public class InstallableSkinRenderLayer<E extends Player, M extends HumanoidModel<E>> extends RenderLayer<E, M> {
    public InstallableSkinRenderLayer(RenderLayerParent<E, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, E entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        SyntheticsPlayer data = entity.getData(SyntheticsAttachments.SYNTHETICS_MANAGER);
        PartManager partManager = data.parts();
        Collection<IBodyInstallable<?>> allInstallables = partManager.installedAll();
        boolean isSlim;
        if(entity instanceof AbstractClientPlayer player) {
            isSlim = player.getSkin().model() == PlayerSkin.Model.SLIM;
        } else {
            isSlim = false;
        }
        allInstallables.forEach(installable -> {
            ResourceLocation texture = installable.entityLayerTexture(isSlim);
            if(!SyntheticsClient.getInstance().getManager().canDrawInstallable(texture)) {
                return;
            }
            RenderLayer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.getParentModel(), texture,
                    poseStack, bufferSource, packedLight,
                    entity, limbSwing, limbSwingAmount,
                    ageInTicks, netHeadYaw, headPitch, partialTick,
                    -1
            );
        });

    }

}
