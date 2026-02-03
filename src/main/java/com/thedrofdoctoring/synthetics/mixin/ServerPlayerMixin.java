package com.thedrofdoctoring.synthetics.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.CameraLinkableBlockEntity;
import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setCamera(Lnet/minecraft/world/entity/Entity;)V"))
    public void updateCamera(CallbackInfo ci) {

        ServerPlayer player = ((ServerPlayer) (Object) this);
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(player);
        if(cache != null) {
            if(cache.isNotViewingSelf) {
                CameraLinkableBlockEntity.sendViewPacket(player, false);
            }
            cache.isNotViewingSelf = false;
        }
    }
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V"
            )
    )
    @SuppressWarnings("ConstantConditions")
    private void preventPossessedMove(ServerPlayer instance, double x, double y, double z, float a, float b, Operation<Void> original) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(instance);
        if(cache != null && cache.isNotViewingSelf) {
            return;
        }
        original.call(instance, x, y, z, a, b);
    }

    @WrapOperation(
            method = "setCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"
            )
    )
    @SuppressWarnings("ConstantConditions")
    private boolean preventPossessedTeleport(ServerPlayer instance, ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot, Operation<Boolean> original) {
        SyntheticsPlayerCache cache = SyntheticsPlayerCache.get(instance);
        if (cache != null && cache.isNotViewingSelf) {
            return false;
        }
        return original.call(instance, level, x, y, z, relativeMovements, yRot, xRot);
    }


}
