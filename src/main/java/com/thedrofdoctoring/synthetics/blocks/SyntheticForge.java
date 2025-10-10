package com.thedrofdoctoring.synthetics.blocks;

import com.mojang.serialization.MapCodec;
import com.thedrofdoctoring.synthetics.blocks.entities.forge.ISyntheticForge;
import com.thedrofdoctoring.synthetics.blocks.entities.forge.SyntheticForgeBlockEntity;
import com.thedrofdoctoring.synthetics.blocks.entities.forge.SyntheticForgeDeferBE;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class SyntheticForge extends TableBlock implements EntityBlock {

    public static final MapCodec<SyntheticForge> CODEC = simpleCodec(SyntheticForge::new);

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
        BASE = Shapes.box(0, 0.5625, 0, 1, 0.6875, 1);
        LEG_NORTH_WEST = Shapes.box(0, 0, 0.8125, 0.1875, 0.5625, 1);
        LEG_SOUTH_WEST = Shapes.box(0, 0, 0, 0.1875, 0.5625, 0.1875);
        LEG_NORTH_EAST = Shapes.box(0.8125, 0, 0.8125, 1, 0.5625, 1);
        LEG_SOUTH_EAST = Shapes.box(0.8125, 0, 0, 1, 0.5625, 0.1875);
        NORTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
        SOUTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
        WEST_SHAPE  = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
        EAST_SHAPE  = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);

    }

    public SyntheticForge(Properties properties) {
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == SyntheticsBlockEntities.SYNTHETIC_FORGE.get() ? SyntheticForgeBlockEntity::serverTick : null;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        ISyntheticForge be = (ISyntheticForge) level.getBlockEntity(pos);
        if(stack.is(Items.LAVA_BUCKET) && be != null) {
            SyntheticForgeBlockEntity master = be.getMaster();
            if(master != null) {
                IFluidHandler tank = master.getFluidCap(hitResult.getDirection());
                FluidStack fluid = new FluidStack(Fluids.LAVA, 1000);
                if (tank != null && tank.fill(fluid, IFluidHandler.FluidAction.SIMULATE) == 1000) {
                    tank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    ItemStack result = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET));
                    master.setChanged();
                    player.setItemInHand(hand, result);
                    level.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS);
                    return ItemInteractionResult.CONSUME_PARTIAL;
                } else {
                    return ItemInteractionResult.FAIL;
                }
            }

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuprovider = this.getMenuProvider(state, level , pos);
            if (menuprovider != null) {
                player.openMenu(menuprovider);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        if(level.getBlockEntity(pos) instanceof ISyntheticForge forge) {
            if(forge.getActivePlayer() != null) {
                return null;
            }
            return (new SimpleMenuProvider(
                    (containerId, playerInventory, player) -> {
                        forge.setActivePlayer(player);
                        return forge.createMenu(containerId, playerInventory);
                    }, Component.translatable("menu.title.synthetics.synthetic_forge")

            ));
        }
        return null;

    }

    @Override
    protected @NotNull MapCodec<SyntheticForge> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if(state.getValue(TableBlock.PART) == TablePart.HEAD) {
            return new SyntheticForgeBlockEntity(pos, state);
        } else {
            return new SyntheticForgeDeferBE(pos, state);
        }
    }
}
