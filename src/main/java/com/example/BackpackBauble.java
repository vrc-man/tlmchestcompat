package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.example.SlotLockHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class BackpackBauble implements IMaidBauble {
    private static final int TRANSFER_INTERVAL = 20;
    private static final int HUNGER_THRESHOLD = 15;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide) return;
        if (maid.tickCount % TRANSFER_INTERVAL != 0) return;

        ItemStack backpack = findBackpack(maid);
        if (backpack.isEmpty()) return;

        backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(backpackInv -> {
            ItemStackHandler maidInv = maid.getMaidInv();
            tryFeed(maid, maidInv, backpackInv);
            tryStore(maidInv, backpackInv, maid);
            tryRestock(maidInv, backpackInv);
        });
    }

    private void tryFeed(EntityMaid maid, ItemStackHandler maidInv, IItemHandler backpackInv) {
        if (maid.getHunger() >= HUNGER_THRESHOLD) return;
        if (hasFood(maidInv)) return;

        for (int i = 0; i < backpackInv.getSlots(); i++) {
            ItemStack stack = backpackInv.getStackInSlot(i);
            if (stack.isEmpty() || !stack.getItem().isEdible()) continue;

            ItemStack food = backpackInv.extractItem(i, 1, false);
            if (food.isEmpty()) continue;

            for (int j = 0; j < maidInv.getSlots(); j++) {
                if (maidInv.getStackInSlot(j).isEmpty()) {
                    maidInv.setStackInSlot(j, food);
                    return;
                }
            }
            backpackInv.insertItem(i, food, false);
            return;
        }
    }

    private void tryStore(ItemStackHandler maidInv, IItemHandler backpackInv, EntityMaid maid) {
        autoStore(maidInv, backpackInv, maid);
    }

    private void autoStore(ItemStackHandler maidInv, IItemHandler targetInv, EntityMaid maid) {
        boolean[] locks = maid == null ? new boolean[0] : SlotLockHelper.getLocks(maid);
        for (int i = 0; i < maidInv.getSlots(); i++) {
            if (i < locks.length && locks[i]) continue; // skip locked slot
            ItemStack stack = maidInv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (isSkippable(stack)) continue;

            ItemStack remaining = ItemHandlerHelper.insertItemStacked(targetInv, stack, false);
            maidInv.setStackInSlot(i, remaining);
            if (remaining.isEmpty()) break;
        }
    }

    private boolean hasFood(ItemStackHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (!s.isEmpty() && s.getItem().isEdible()) return true;
        }
        return false;
    }

    private ItemStack findBackpack(EntityMaid maid) {
        Optional<?> curiosOpt = CuriosApi.getCuriosInventory(maid).resolve();
        if (curiosOpt.isPresent()) {
            Object handler = curiosOpt.get();
            try {
                java.lang.reflect.Method getMap = handler.getClass().getMethod("getCurios");
                Object map = getMap.invoke(handler);
                if (map instanceof java.util.Map) {
                    Object val = ((java.util.Map<?, ?>) map).get("back");
                    if (val != null) {
                        java.lang.reflect.Method getStacks = val.getClass().getMethod("getStacks");
                        Object stacks = getStacks.invoke(val);
                        int slots = (int) stacks.getClass().getMethod("getSlots").invoke(stacks);
                        java.lang.reflect.Method getSlot = stacks.getClass().getMethod("getStackInSlot", int.class);
                        for (int i = 0; i < slots; i++) {
                            ItemStack s = (ItemStack) getSlot.invoke(stacks, i);
                            if (!s.isEmpty() && isSophBackpack(s)) return s;
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        ItemStackHandler maidInv = maid.getMaidInv();
        for (int i = 0; i < maidInv.getSlots(); i++) {
            ItemStack stack = maidInv.getStackInSlot(i);
            if (!stack.isEmpty() && isSophBackpack(stack)) return stack;
        }
        return ItemStack.EMPTY;
    }

    private boolean isSophBackpack(ItemStack stack) {
        return stack.getItem().getClass().getName().toLowerCase().contains("sophisticatedbackpacks");
    }

    private void tryRestock(ItemStackHandler maidInv, IItemHandler backpackInv) {
        for (int i = 0; i < backpackInv.getSlots(); i++) {
            var stack = backpackInv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem().isEdible()) continue;
            if (!isSophBackpack(stack) && isSkippable(stack)) continue;

            int maidCount = 0;
            for (int j = 0; j < maidInv.getSlots(); j++) {
                var s = maidInv.getStackInSlot(j);
                if (!s.isEmpty() && ItemStack.isSameItem(s, stack)) maidCount += s.getCount();
            }
            if (maidCount >= 4) continue;

            int needed = Math.min(16 - maidCount, stack.getCount());
            if (needed <= 0) continue;

            var taken = backpackInv.extractItem(i, needed, false);
            if (taken.isEmpty()) continue;

            // Merge with existing or find empty slot
            boolean added = false;
            for (int j = 0; j < maidInv.getSlots() && !taken.isEmpty(); j++) {
                var s = maidInv.getStackInSlot(j);
                if (!s.isEmpty() && ItemStack.isSameItem(s, taken) && s.getCount() < s.getMaxStackSize()) {
                    int space = s.getMaxStackSize() - s.getCount();
                    int transfer = Math.min(space, taken.getCount());
                    s.grow(transfer);
                    taken.shrink(transfer);
                    if (taken.isEmpty()) added = true;
                }
            }
            for (int j = 0; j < maidInv.getSlots() && !taken.isEmpty(); j++) {
                if (maidInv.getStackInSlot(j).isEmpty()) {
                    maidInv.setStackInSlot(j, taken);
                    added = true;
                    break;
                }
            }
            if (!added) backpackInv.insertItem(i, taken, false);
        }
    }

    private boolean isSkippable(ItemStack stack) {
        String name = stack.getItem().getClass().getName().toLowerCase();
        return name.contains("sophisticatedbackpacks") || name.contains("bauble") || name.contains("charm");
    }
}
