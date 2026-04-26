package com.thedrofdoctoring.synthetics.blocks.linkables;

import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class LinkableBlock extends BaseEntityBlock {


    protected LinkableBlock(Properties properties) {
        super(properties);
    }

    public abstract BiFunction<BlockPos, BlockState, ? extends LinkableBlockEntity> createLinkableBlockEntity();

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return createLinkableBlockEntity().apply(pPos, pState);
    }

    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack tool) {
        if(level instanceof ServerLevel serverLevel && blockEntity instanceof LinkableBlockEntity be) {
            be.onRemoveLinkable(serverLevel);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock() && level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof LinkableBlockEntity be) {
            be.onRemoveLinkable(serverLevel);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }



    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level instanceof ServerLevel && level.getBlockEntity(pos) instanceof LinkableBlockEntity be) {
            if (be.isLinked(player)) {
                be.onLinkedInteract(player, null, InteractionHand.MAIN_HAND);
            } else {
                be.onNonLinkedInteract(player);
            }

        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(level instanceof ServerLevel && level.getBlockEntity(pos) instanceof LinkableBlockEntity be && be.isLinked(player)) {
            if(!player.getItemInHand(hand).isEmpty()) {
                return be.onLinkedUseItem(player, stack, hand, hitResult);
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
