package com.example;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class MarkerContainer extends AbstractContainerMenu {
    public final FilterItemHandler filterHandler;
    private final ItemStack markerStack;

    protected MarkerContainer(int containerId, Inventory playerInv, ItemStack markerStack) {
        super(MarkerMenuType.MARKER_MENU.get(), containerId);
        this.markerStack = markerStack;
        this.filterHandler = new FilterItemHandler(markerStack);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new SlotItemHandler(filterHandler, row * 3 + col, 62 + col * 18, 17 + row * 18));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var slot = slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        var stack = slot.getItem();
        var original = stack.copy();

        if (slotIndex < 9) {
            // Filter → player inv
            if (!moveItemStackTo(stack, 9, 45, true)) return ItemStack.EMPTY;
        } else {
            // Player inv → filter
            if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        filterHandler.save();
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return !markerStack.isEmpty() && markerStack.getItem() instanceof StorageMarkerItem;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        filterHandler.save();
    }
}
