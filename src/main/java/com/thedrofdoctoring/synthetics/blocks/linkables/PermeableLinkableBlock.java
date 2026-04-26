package com.thedrofdoctoring.synthetics.blocks.linkables;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.LinkableBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.linkables.PermeableLinkableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class PermeableLinkableBlock extends LinkableBlock {

    public static final MapCodec<PermeableLinkableBlock> CODEC = simpleCodec(PermeableLinkableBlock::new);
    public static final BooleanProperty HAS_DISGUISE = BooleanProperty.create("has_disguise");


    private static final VoxelShape FULL  = Shapes.block();
    private static final VoxelShape EMPTY = Shapes.empty();

    public PermeableLinkableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_DISGUISE, false));
    }

    @Override
    public BiFunction<BlockPos, BlockState, ? extends LinkableBlockEntity> createLinkableBlockEntity() {
        return PermeableLinkableBlockEntity::new;
    }

    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HAS_DISGUISE);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(!state.getValue(HAS_DISGUISE)) {
            return FULL;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof PermeableLinkableBlockEntity linkable && linkable.disguisedAs() != null) {
            //noinspection DataFlowIssue
            return linkable.disguisedAs().getShape(level, pos);
        }
        return FULL;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(context instanceof EntityCollisionContext con) {
            return getShapeForEntity(pos, level, con.getEntity());
        }
        return FULL;
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if(context instanceof EntityCollisionContext con) {
            return getShapeForEntity(pos, level, con.getEntity());
        }
        return EMPTY;
    }

    private VoxelShape getShapeForEntity(BlockPos pos, BlockGetter level, Entity entity) {
        BlockEntity be = level.getBlockEntity(pos);
        if(be instanceof PermeableLinkableBlockEntity linkable && entity instanceof Player player) {

            if(!linkable.isLinked(player)) return FULL;

            if(!isAbove(entity, FULL, pos) || entity.isDescending()) {
                return EMPTY;
            }
        }
        return FULL;
    }



    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(level instanceof ServerLevel && level.getBlockEntity(pos) instanceof PermeableLinkableBlockEntity be && be.isLinked(player)) {
            if(!player.getItemInHand(hand).isEmpty()) {
                return be.onLinkedUseItem(player, stack, hand, hitResult);
            }
        } else if(level.isClientSide && !player.isShiftKeyDown()) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    @Override
    protected boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public @NotNull SoundType getSoundType(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos, @Nullable Entity entity) {
        if(level.getBlockEntity(pos) instanceof PermeableLinkableBlockEntity linkable) {
            BlockState disguisedAs = linkable.disguisedAs();
            if(disguisedAs != null) {
                return disguisedAs.getSoundType(level, pos, entity);
            }
        }
        return super.getSoundType(state, level, pos, entity);
    }

    private boolean isAbove(Entity entity, VoxelShape shape, BlockPos pos) {
        return entity.getY() > pos.getY() + shape.max(Direction.Axis.Y) - (entity.onGround() ? 8.05/16.0 : 0.0015);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
