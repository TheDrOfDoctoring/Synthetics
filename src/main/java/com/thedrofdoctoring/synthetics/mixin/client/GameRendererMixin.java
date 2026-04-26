package com.thedrofdoctoring.synthetics.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import com.thedrofdoctoring.synthetics.entities.linkable.DroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    Minecraft minecraft;

    @ModifyReturnValue(method = "shouldRenderBlockOutline", at = @At("RETURN"))
    private boolean linkedBlockOutlineOverride(boolean orig) {
        if(minecraft.player != null) {
            SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(minecraft.player);
            if(cache.isNotViewingSelf && minecraft.cameraEntity instanceof DroneEntity) {
                return true;
            }
        }

        return orig;
    }
}
