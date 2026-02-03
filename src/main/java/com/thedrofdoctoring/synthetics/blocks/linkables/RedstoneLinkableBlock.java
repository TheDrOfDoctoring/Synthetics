package com.thedrofdoctoring.synthetics.blocks.linkables;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.RedstoneLinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class RedstoneLinkableBlock extends LinkableBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final MapCodec<RedstoneLinkableBlock> CODEC = simpleCodec(RedstoneLinkableBlock::new);

    public RedstoneLinkableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    public void toggle(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockState newState = state.cycle(RedstoneLinkableBlock.ACTIVE);
        level.setBlock(pos, newState, 3);
        level.updateNeighborsAt(pos, this);
    }


    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public BiFunction<BlockPos, BlockState, ? extends LinkableBlockEntity> createLinkableBlockEntity() {
        return RedstoneLinkableBlockEntity::new;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected boolean isSignalSource(@NotNull BlockState state) {
        return state.getValue(ACTIVE);
    }

    protected int getSignal(@NotNull BlockState state, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return state.getValue(ACTIVE) ? 15 : 0;
    }
}
