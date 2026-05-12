package com.thedrofdoctoring.synthetics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thedrofdoctoring.synthetics.SyntheticsClient;
import com.thedrofdoctoring.synthetics.capabilities.SyntheticsPlayer;
import com.thedrofdoctoring.synthetics.core.data.types.body.installables.IBodyInstallable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {


    @Inject(method = "renderHand", at = @At("RETURN"))
    private void renderHandInstallables(PoseStack poseStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, ModelPart rendererArm, ModelPart rendererArmwear, CallbackInfo ci) {
        SyntheticsPlayer syntheticsPlayer = SyntheticsPlayer.get(player);
        Collection<IBodyInstallable<?>> allInstallables = syntheticsPlayer.parts().installedAll();
        allInstallables.forEach(installable -> {
            ResourceLocation texture = installable.entityLayerTexture(false);
            if(!SyntheticsClient.getInstance().getManager().canDrawInstallable(texture)) {
                return;
            }
            rendererArm.render(poseStack, buffer.getBuffer(RenderType.entityCutout(texture)), combinedLight, OverlayTexture.NO_OVERLAY);
        });
    }
}
