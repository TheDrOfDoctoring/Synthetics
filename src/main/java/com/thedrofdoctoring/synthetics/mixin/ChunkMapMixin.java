package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.capabilities.cache.SyntheticsPlayerCache;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Shadow
    abstract int getPlayerViewDistance(ServerPlayer player);

    @Shadow
    protected abstract void applyChunkTrackingView(ServerPlayer player, ChunkTrackingView chunkTrackingView);

    @Inject(method = "updateChunkTracking", at = @At(value = "HEAD"), cancellable = true)
    private void trackLinkedViewChunk(ServerPlayer player, CallbackInfo ci) {
        if(!player.getCamera().equals(player) && SyntheticsPlayerCache.get(player).isNotViewingSelf) {
            this.applyChunkTrackingView(player, ChunkTrackingView.of(player.getCamera().chunkPosition(), this.getPlayerViewDistance(player)));
            ci.cancel();
        }
    }
}
