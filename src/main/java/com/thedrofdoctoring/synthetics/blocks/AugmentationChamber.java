package com.thedrofdoctoring.synthetics.blocks;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.chamber.AugmentationChamberBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.chamber.AugmentationChamberDeferBE;
import com.thedrofdoctoring.synthetics.blocks.entities.chamber.IAugmentationChamber;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import com.thedrofdoctoring.synthetics.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AugmentationChamber extends BaseEntityBlock {

    public static final MapCodec<AugmentationChamber> CODEC = simpleCodec(AugmentationChamber::new);

    public static final EnumProperty<AugmentationChamber.Part> PART = EnumProperty.create("augmentation_chamber_part", AugmentationChamber.Part.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape BASE;
    protected static VoxelShape ROOF;

    protected static VoxelShape SIDE_WEST;
    protected static VoxelShape SIDE_EAST;
    protected static VoxelShape SIDE_SOUTH;

    protected static VoxelShape SIDE_WEST_TOP;
    protected static VoxelShape SIDE_NORTH_TOP;
    protected static VoxelShape SIDE_EAST_TOP;


    protected static VoxelShape NORTH_SHAPE;
    protected static VoxelShape SOUTH_SHAPE;
    protected static VoxelShape WEST_SHAPE;
    protected static VoxelShape EAST_SHAPE;

    protected static VoxelShape NORTH_SHAPE_TOP;
    protected static VoxelShape SOUTH_SHAPE_TOP;
    protected static VoxelShape WEST_SHAPE_TOP;
    protected static VoxelShape EAST_SHAPE_TOP;
    static {

        // bottom sides

        SIDE_EAST = Shapes.box(0, 0.0625, 0, 0.0625, 1, 0.9375);
        SIDE_SOUTH = Shapes.box(0, 0.0625, 0.9375, 1, 1, 1);
        SIDE_WEST = Shapes.box(0.9375, 0.0625, 0, 1, 1, 0.9375);

        // roof
        ROOF = Shapes.box(0, 0.9375, 0, 1, 1, 1);

        // top sides

        SIDE_EAST_TOP = Shapes.box(0, 0, 0.9375, 1, 1, 1);
        SIDE_NORTH_TOP = Shapes.box(0, 0, 0, 0.0625, 1, 0.9375);
        SIDE_WEST_TOP = Shapes.box(0.9375, 0, 0, 1, 1, 0.9375);


        BASE = Shapes.box(0, 0, 0, 1, 0.0625, 1);

        SOUTH_SHAPE = Shapes.or(BASE, SIDE_EAST, SIDE_SOUTH, SIDE_WEST);
        NORTH_SHAPE = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.NORTH, SOUTH_SHAPE);
        WEST_SHAPE  = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.WEST, SOUTH_SHAPE);
        EAST_SHAPE  = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.EAST, SOUTH_SHAPE);

        SOUTH_SHAPE_TOP = Shapes.or(ROOF, SIDE_NORTH_TOP, SIDE_EAST_TOP, SIDE_WEST_TOP);
        NORTH_SHAPE_TOP = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.NORTH, SOUTH_SHAPE_TOP);
        WEST_SHAPE_TOP  = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.WEST, SOUTH_SHAPE_TOP);
        EAST_SHAPE_TOP  = Helper.rotateShapeAroundY(Direction.SOUTH, Direction.EAST, SOUTH_SHAPE_TOP);
    }


    public AugmentationChamber(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, Part.BOTTOM));
    }


    @Override
    protected @NotNull MapCodec<AugmentationChamber> codec() {
        return CODEC;
    }
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if(state.getValue(PART) == Part.BOTTOM) {
            return new AugmentationChamberBlockEntity(pos, state);
        } else {
            return new AugmentationChamberDeferBE(pos, state);
        }
    }




    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof IAugmentationChamber chamber) {
            Player activePlayer = chamber.getActivePlayer();
            if(activePlayer == null) {
                BlockPos position;
                if(state.getValue(PART) == Part.TOP) {
                    position = pos.below();
                } else {
                    position = pos;
                }
                return (new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> {
                            chamber.setActivePlayer(player);
                            return new AugmentationChamberMenu(containerId, playerInventory, ContainerLevelAccess.create(level, position));

                        }, Component.translatable("menu.title.synthetics.augmentation_menu")

                ));
            }

        }
        return null;

    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult result) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos));
        }

        return InteractionResult.SUCCESS;
    }
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos blockpos = pos.relative(Direction.UP);
            level.setBlock(blockpos, state.setValue(PART, Part.TOP), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    protected @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(PART, Part.BOTTOM);
        } else {
            return null;
        }
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        Part part = state.getValue(PART);
        return part == Part.BOTTOM ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
    }

    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
        Part part = state.getValue(PART);
        switch (direction) {
            case NORTH -> {
                if(part == Part.BOTTOM) {
                    return NORTH_SHAPE;
                } else {
                    return NORTH_SHAPE_TOP;
                }
            }
            case SOUTH -> {
                if(part == Part.BOTTOM) {
                    return SOUTH_SHAPE;
                } else {
                    return SOUTH_SHAPE_TOP;
                }
            }
            case WEST -> {
                if(part == Part.BOTTOM) {
                    return WEST_SHAPE;
                } else {
                    return WEST_SHAPE_TOP;
                }
            }
            default -> {
                if(part == Part.BOTTOM) {
                    return EAST_SHAPE;
                } else {
                    return EAST_SHAPE_TOP;
                }
            }
        }
    }
    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && player.isCreative()) {
            Part part = state.getValue(PART);
            if (part == Part.BOTTOM) {
                BlockPos blockpos = pos.relative(Direction.UP);
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == Part.TOP) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            } else {
                BlockPos blockpos = pos.relative(Direction.DOWN);
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == Part.BOTTOM) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        if(state.getValue(PART) == Part.TOP && blockstate.is(this)) {
            return blockstate.getValue(PART) == Part.BOTTOM;
        }
        return state.getValue(PART) == Part.BOTTOM;
    }

    protected @NotNull BlockState updateShape(BlockState state, Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        Part part = state.getValue(PART);
        if (facing.getAxis() == Direction.Axis.Y && part == Part.BOTTOM == (facing == Direction.UP)) {
            return facingState.is(this) && facingState.getValue(PART) != part ? facingState.setValue(PART, part) : Blocks.AIR.defaultBlockState();
        } else {
            return part == Part.BOTTOM && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    public enum Part implements StringRepresentable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Part(String name) {
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
