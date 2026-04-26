package com.thedrofdoctoring.synthetics.mixin.client;

import com.thedrofdoctoring.synthetics.networking.from_client.ServerboundResetCameraPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;
    // if the camera is null client-side, server side it gets stuck thinking the camera is still on the camera entity
    @Inject(method = "handleSetCamera", at = @At(value = "RETURN"))
    private void resetWhenNoCamera(ClientboundSetCameraPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity(this.level);
        if(entity == null && Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(ServerboundResetCameraPacket.getInstance());
        }
    }
}
