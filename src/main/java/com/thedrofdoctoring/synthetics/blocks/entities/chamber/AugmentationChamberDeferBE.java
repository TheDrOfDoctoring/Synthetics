package com.thedrofdoctoring.synthetics.blocks.entities.chamber;

import com.thedrofdoctoring.synthetics.blocks.AugmentationChamber;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlockEntities;
import com.thedrofdoctoring.synthetics.menus.AugmentationChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AugmentationChamberDeferBE extends BaseContainerBlockEntity implements IAugmentationChamber {

    private final @NotNull NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    private AugmentationChamberBlockEntity master;

    public AugmentationChamberDeferBE(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.AUGMENTATION_CHAMBER_DEFERRED.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.synthetics.augmentation_chamber");
    }

    public @Nullable AugmentationChamberBlockEntity getMaster() {
        if (this.master != null) {
            return this.master;
        }
        if (this.level != null) {
            BlockState below = this.level.getBlockState(worldPosition.below());
            if(below.getValue(AugmentationChamber.PART) == AugmentationChamber.Part.BOTTOM) {
                if(this.level.getBlockEntity(worldPosition.below()) instanceof AugmentationChamberBlockEntity be) {
                    this.master = be;
                    return be;
                }
            }
        }

        return null;
    }


    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        if(getMaster() == null) {
            return NonNullList.create();
        }
        return getMaster().getItems();
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        if(getMaster() == null) {
            return;
        }
        getMaster().setItems(items);

    }

    public Player getActivePlayer() {

        if(getMaster() == null) {
            return null;
        }
        return getMaster().getActivePlayer();
    }
    public void setActivePlayer(Player player) {
        if(getMaster() == null) {
            return;
        }
        getMaster().setActivePlayer(player);
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new AugmentationChamberMenu(id, player, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition.below()));
    }

    @Override
    public int getContainerSize() {
        if(getMaster() == null) {
            return 0;
        }
        return getMaster().getContainerSize();
    }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        if(getMaster() == null) {
            return ClientboundBlockEntityDataPacket.create(this);
        }
        return getMaster().getUpdatePacket();
    }
}
