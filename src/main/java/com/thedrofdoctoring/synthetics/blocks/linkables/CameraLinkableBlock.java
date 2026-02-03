package com.thedrofdoctoring.synthetics.blocks.linkables;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.CameraLinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class CameraLinkableBlock extends LinkableBlock {

    public static final MapCodec<CameraLinkableBlock> CODEC = simpleCodec(CameraLinkableBlock::new);

    private static final VoxelShape SHAPE;

    static {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.9375, 0.125, 0.8125, 1, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.375, 0.625, 0.9375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.625, 0.3125, 0.5625, 0.8125, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.625, 0.4375, 0.6875, 0.8125, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.625, 0.4375, 0.375, 0.8125, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.625, 0.625, 0.5625, 0.8125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5625, 0.4375, 0.5625, 0.625, 0.5625), BooleanOp.OR);
        SHAPE = shape;
    }

    public CameraLinkableBlock(Properties properties) {
        super(properties);
    }

    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }
    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof CameraLinkableBlockEntity camera) {
            camera.onPlace();
        }
    }

    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, BlockPos pos) {
        BlockPos blockPos = pos.above();
        return !level.getBlockState(blockPos).getCollisionShape(level, pos).isEmpty();
    }

    @Override
    public BiFunction<BlockPos, BlockState, CameraLinkableBlockEntity> createLinkableBlockEntity() {
        return CameraLinkableBlockEntity::new;
    }

    @Override
    protected @NotNull MapCodec<CameraLinkableBlock> codec() {
        return CODEC;
    }
}
