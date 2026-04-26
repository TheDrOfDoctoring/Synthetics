package com.thedrofdoctoring.synthetics.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.PermeableLinkableBlockEntity;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @ModifyReturnValue(method = "getOverlayBlock", at = @At("RETURN"))
    private static Pair<BlockState, BlockPos> hidePermeableOverlay(Pair<BlockState, BlockPos> original, Player player) {
        if(original != null && player.level().getBlockEntity(original.getRight()) instanceof PermeableLinkableBlockEntity linkable) {
            if(linkable.isLinked(player)) {
                return null;
            }
        }
        return original;
    }
}
