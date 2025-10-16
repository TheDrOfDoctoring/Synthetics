package com.thedrofdoctoring.synthetics.menus;

import com.thedrofdoctoring.synthetics.blocks.entities.forge.ISyntheticForge;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.items.BlueprintItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SyntheticForgeMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    private final Container forge;
    private final ContainerData data;



    public SyntheticForgeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(11), new SimpleContainerData(4), ContainerLevelAccess.NULL);
    }

    public SyntheticForgeMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Container forge, ContainerData data, ContainerLevelAccess access) {
        super(SyntheticsMenus.SYNTHETIC_FORGE.get(), containerId);
        this.data = data;
        this.access = access;
        this.forge = forge;
        this.addSlot(new ResultSlot(forge, 0, 124, 35));
        this.addSlot(new BlueprintSlot(forge, 1, 123, 9));


        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new IngredientSlot(forge, 2 + j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
        this.addDataSlots(data);
    }


    public int getRecipeTime() {
        return this.data.get(0);
    }
    public int getTotalRecipeTime() {
        return this.data.get(1);
    }
    public int getLavaAmount() {
        return this.data.get(2);
    }
    public int getLavaMaxAmount() {
        return this.data.get(3);
    }


    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 11, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 10 && index < 46) {
                if(itemstack1.getItem() instanceof BlueprintItem) {
                    if (!this.moveItemStackTo(itemstack1, 1, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (!this.moveItemStackTo(itemstack1, 2, 11, false)) {
                    if (index < 37) {
                        if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 11, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 11, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            if (index == 0) {
                player.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    public void removed(@NotNull Player player) {
        super.removed(player);

        this.access.execute((level, pos) -> {
            if(level.getBlockEntity(pos) instanceof ISyntheticForge forgeEntity) {
                forgeEntity.setActivePlayer(null);
            }
        });
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, SyntheticsBlocks.SYNTHETIC_FORGE.get());
    }

    static class IngredientSlot extends Slot {

        public IngredientSlot(@NotNull Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);

        }

        public boolean mayPlace(@NotNull ItemStack stack) {
            return true;
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class ResultSlot extends Slot {

        public ResultSlot(@NotNull Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }
    static class BlueprintSlot extends Slot {

        public BlueprintSlot(@NotNull Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.getItem() instanceof BlueprintItem;
        }
    }

}
