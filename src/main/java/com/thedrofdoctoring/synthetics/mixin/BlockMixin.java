package com.thedrofdoctoring.synthetics.mixin;

import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    // For the block highlighter, for some reason block break event doesnt work clientside
    @Inject(method = "destroy", at = @At("HEAD"))
    private void onDestroy(LevelAccessor level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if(level.isClientSide()) {
            SyntheticsClientEventHandler.onBlockBreak(pos);
        }
    }
}
