package com.thedrofdoctoring.synthetics.blocks.entities.forge;

import com.thedrofdoctoring.synthetics.blocks.TableBlock;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.menus.SyntheticForgeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SyntheticForgeDeferBE extends BaseContainerBlockEntity implements ISyntheticForge {

    private final @NotNull NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    private SyntheticForgeBlockEntity master;
    private Player player;

    public SyntheticForgeDeferBE(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.SYNTHETIC_FORGE_DEFERRED.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.synthetics.synthetic_forge");
    }

    public @Nullable SyntheticForgeBlockEntity getMaster() {
        if (this.master != null) {
            return this.master;
        }
        if (this.level != null) {
            Direction facing = TableBlock.getConnectedDirection(this.getBlockState());
            BlockState facingState = this.level.getBlockState(worldPosition.relative(facing));
            if(facingState.getValue(TableBlock.PART) == TableBlock.TablePart.HEAD) {
                if(this.level.getBlockEntity(worldPosition.relative(facing)) instanceof SyntheticForgeBlockEntity be) {
                    this.master = be;
                    return be;
                }
            }
        }

        return null;
    }
    public @Nullable IFluidHandler getFluidCap(Direction side) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return null;
        }
        return master.getFluidCap(side);
    }

    @NotNull
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        if(master == null) {
            return new SyntheticForgeMenu(id, player);
        }
        return master.createMenu(id, player);
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return NonNullList.create();
        }
        return master.getItems();
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        SyntheticForgeBlockEntity master = getMaster();

        if(master == null) {
            return;
        }
        master.setItems(items);

    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return false;
        }
        return master.stillValid(player);

    }
    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return;
        }
        master.setItem(index, stack);
    }

    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return false;
        }
        return master.canPlaceItem(index, stack);
    }

    @NotNull
    @Override
    public ItemStack getItem(int index) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return ItemStack.EMPTY;
        }
        return master.getItem(index);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int index, int amount) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return ItemStack.EMPTY;
        }
        return master.removeItem(index, amount);
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int index) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return ItemStack.EMPTY;
        }
        return master.removeItemNoUpdate(index);
    }

    public Player getActivePlayer() {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return null;
        }
        return master.getActivePlayer();
    }
    public void setActivePlayer(Player player) {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return;
        }
        master.setActivePlayer(player);
    }

    @Override
    public int getRecipeTime() {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return 0;
        }
        return master.getRecipeTime();
    }


    @Override
    public int getContainerSize() {
        SyntheticForgeBlockEntity master = getMaster();
        if(master == null) {
            return 0;
        }
        return master.getContainerSize();
    }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        SyntheticForgeBlockEntity master = getMaster();

        if(master == null) {
            return ClientboundBlockEntityDataPacket.create(this);
        }
        return master.getUpdatePacket();
    }
}
