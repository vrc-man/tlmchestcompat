package com.example;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FilterItemHandler extends ItemStackHandler {
    private static final int SIZE = 9;
    public final ItemStack boundItem;

    public FilterItemHandler(ItemStack boundItem) {
        super(SIZE);
        this.boundItem = boundItem;
        deserializeNBT(boundItem.getOrCreateTagElement("FilterItems"));
    }

    public void save() {
        boundItem.getOrCreateTag().put("FilterItems", serializeNBT());
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty();
    }
}
