package com.thedrofdoctoring.synthetics.blocks;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.PermeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermeableBlock extends BaseEntityBlock {

    public static final MapCodec<PermeableBlock> CODEC = simpleCodec(PermeableBlock::new);

    private static final VoxelShape FULL = Shapes.block();
    private static final VoxelShape EMPTY = Shapes.empty();


    public PermeableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<PermeableBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PermeableBlockEntity(blockPos, blockState);
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return FULL;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos) {
        return FULL;
    }

    @Override
    public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return FULL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext) {
        return EMPTY;
    }

    @Override
    public boolean canEntityDestroy(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Entity entity) {
        return false;
    }

    @Override
    protected boolean canBeReplaced(@NotNull BlockState state, @NotNull Fluid fluid) {
        return false;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        Vec3 centre = pos.getCenter();
        level.addParticle(ParticleTypes.PORTAL, centre.x + 0.35f, centre.y - 0.2f, centre.z - 0.35f, 0, 0,0);
        level.addParticle(ParticleTypes.PORTAL, centre.x - 0.35f, centre.y - 0.2f, centre.z + 0.35f, 0, 0,0);
        level.addParticle(ParticleTypes.PORTAL, centre.x, centre.y - 0.2f, centre.z, 0, 0,0);
        super.animateTick(state, level, pos, random);
    }
}
