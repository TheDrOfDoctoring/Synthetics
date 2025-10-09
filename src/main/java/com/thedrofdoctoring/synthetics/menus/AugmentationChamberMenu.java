package com.thedrofdoctoring.synthetics.menus;

import com.thedrofdoctoring.synthetics.blocks.entities.AugmentationChamberBlockEntity;
import com.thedrofdoctoring.synthetics.core.SyntheticsBlocks;
import com.thedrofdoctoring.synthetics.items.InstallableItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AugmentationChamberMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;

    protected final SimpleContainer resultsContainer = new SimpleContainer(8);
    protected final SimpleContainer inputContainer = new SimpleContainer(1);


    public AugmentationChamberMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public AugmentationChamberMenu(int containerId, @NotNull Inventory playerInventory, ContainerLevelAccess access) {
        super(SyntheticsMenus.AUGMENTATION_CHAMBER.get(), containerId);
        this.addSlot(new InstallableSlot(inputContainer, 0, 32, 13));
        this.addResultSlots(resultsContainer);
        this.addPlayerSlots(playerInventory);
        this.access = access;
    }
    protected void addResultSlots(Container container) {
        int baseX = 8;
        int baseY = 50;

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new ResultSlot(container, i * 3 + j, baseX + j * 18, baseY + i * 18));
            }
        }
    }



    public SimpleContainer getResultsContainer() {
        return resultsContainer;
    }

    public SimpleContainer getInputContainer() {
        return inputContainer;
    }

    public void removed(@NotNull Player player) {
        super.removed(player);

        this.access.execute((level, pos) -> {
            this.clearContainer(player, this.resultsContainer);
            this.clearContainer(player, this.inputContainer);
            if(level.getBlockEntity(pos) instanceof AugmentationChamberBlockEntity chamber) {
                chamber.setActivePlayer(null);
            }
        });
    }

    protected void addPlayerSlots(@NotNull Inventory playerInventory, int baseX, int baseY) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, baseX + j * 18, baseY + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, baseX + i * 18, baseY + 58));
        }
    }

    // Overrides right clicking behaviour for input slot
    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId == 0) {
            if(button == 0 || clickType != ClickType.PICKUP) {
                super.clicked(slotId, button, clickType, player);
            }
        } else {
            super.clicked(slotId, button, clickType, player);
        }

    }

    protected void addPlayerSlots(@NotNull Inventory playerInventory) {
        this.addPlayerSlots(playerInventory, 8, 84 + 29);
    }


    @Override

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, SyntheticsBlocks.AUGMENTATION_CHAMBER.get());
    }


    private static class InstallableSlot extends Slot {
        public InstallableSlot(@NotNull Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }



        public boolean mayPlace(@NotNull ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(@NotNull ItemStack stack) {
            return stack.getItem() instanceof InstallableItem<?>;
        }

        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class ResultSlot extends Slot {

        public ResultSlot(@NotNull Container inventory, int slotId, int xPos, int yPos) {
            super(inventory, slotId, xPos, yPos);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }
}
