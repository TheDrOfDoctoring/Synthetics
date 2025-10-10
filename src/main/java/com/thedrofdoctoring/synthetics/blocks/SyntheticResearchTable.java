package com.thedrofdoctoring.synthetics.blocks;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
@SuppressWarnings("unused")
public class SyntheticResearchTable extends TableBlock {

    public static final MapCodec<SyntheticResearchTable> CODEC = simpleCodec(SyntheticResearchTable::new);

    protected static final VoxelShape BASE;
    protected static final VoxelShape LEG_NORTH_WEST;
    protected static final VoxelShape LEG_SOUTH_WEST;
    protected static final VoxelShape LEG_NORTH_EAST;
    protected static final VoxelShape LEG_SOUTH_EAST;
    protected static final VoxelShape NORTH_SHAPE;
    protected static final VoxelShape SOUTH_SHAPE;
    protected static final VoxelShape WEST_SHAPE;
    protected static final VoxelShape EAST_SHAPE;

    static {
        BASE = Shapes.box(0, 0.75, 0, 1, 0.875, 1);
        LEG_NORTH_WEST = Shapes.box(0, 0, 0.8125, 0.1875, 0.75, 1);
        LEG_SOUTH_WEST = Shapes.box(0, 0, 0, 0.1875, 0.75, 0.1875);
        LEG_NORTH_EAST = Shapes.box(0.8125, 0, 0.8125, 1, 0.75, 1);
        LEG_SOUTH_EAST = Shapes.box(0.8125, 0, 0, 1, 0.75, 0.1875);

        NORTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
        SOUTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
        WEST_SHAPE  = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
        EAST_SHAPE  = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);

    }

    public SyntheticResearchTable(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, TablePart.HEAD));
    }


    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = getConnectedDirection(state).getOpposite();
        switch (direction) {
            case NORTH -> {
                return NORTH_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            case WEST -> {
                return WEST_SHAPE;
            }
            default -> {
                return EAST_SHAPE;
            }
        }
    }



    @Override
    protected @NotNull MapCodec<SyntheticResearchTable> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(player.getCommandSenderWorld().isClientSide) {
            SyntheticsClientManager.setResearchScreen();
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

}
