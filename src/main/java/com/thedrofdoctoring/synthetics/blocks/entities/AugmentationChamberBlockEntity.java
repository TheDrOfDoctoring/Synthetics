package com.thedrofdoctoring.synthetics.blocks.entities;

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

public class AugmentationChamberBlockEntity extends BaseContainerBlockEntity {

    private @NotNull NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);

    private Player player;

    public AugmentationChamberBlockEntity(BlockPos pos, BlockState blockState) {
        super(SyntheticsBlockEntities.AUGMENTATION_CHAMBER.get(), pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.synthetics.augmentation_chamber");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;

    }

    public Player getActivePlayer() {
        return this.player;
    }
    public void setActivePlayer(Player player) {
        this.player = player;
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new AugmentationChamberMenu(id, player, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
