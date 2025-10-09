package com.thedrofdoctoring.synthetics.blocks;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.client.core.SyntheticsClientManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
@SuppressWarnings("unused")
public class SyntheticResearchTable extends HorizontalDirectionalBlock {

    public static final MapCodec<SyntheticResearchTable> CODEC = simpleCodec(SyntheticResearchTable::new);

    public static final EnumProperty<TablePart> PART = EnumProperty.create("synthetic_research_table_part", TablePart.class);
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
        LEG_NORTH_WEST = Shapes.box(0, 0, 0, 0.1875, 0.75, 1);
        LEG_SOUTH_WEST = Shapes.box(0, 0, 0, 0.1875, 0.75, 0.1875);
        LEG_NORTH_EAST = Shapes.box(0.8125, 0, 0, 1, 0.75, 1);
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

    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            level.setBlock(blockpos, state.setValue(PART, TablePart.FOOT), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1) ? this.defaultBlockState().setValue(FACING, direction) : null;
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

    public static Direction getConnectedDirection(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(PART) == TablePart.FOOT ? direction.getOpposite() : direction;
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        TablePart part = state.getValue(PART);
        return part == TablePart.FOOT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }


    protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (facing != getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        } else {
            return facingState.is(this) && facingState.getValue(PART) != state.getValue(PART) ? state : Blocks.AIR.defaultBlockState();
        }
    }

    private static Direction getNeighbourDirection(TablePart part, Direction direction) {
        return part == TablePart.HEAD ? direction : direction.getOpposite();
    }


    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative()) {
            TablePart tablePart = state.getValue(PART);
            if (tablePart == TablePart.HEAD) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(tablePart, state.getValue(FACING).getOpposite()));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == TablePart.FOOT) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }


    @SuppressWarnings("deprecation")
    protected long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockpos = pos.relative(state.getValue(FACING), state.getValue(PART) == TablePart.FOOT ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), pos.getY(), blockpos.getZ());
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


    public enum TablePart implements StringRepresentable {
        HEAD("head"),
        FOOT("foot");

        private final String name;

        TablePart(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
