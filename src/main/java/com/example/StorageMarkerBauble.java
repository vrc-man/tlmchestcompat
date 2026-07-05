package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;

public class StorageMarkerBauble implements IMaidBauble {
    private static final int INTERVAL = 20;
    private static final int HUNGER_THRESHOLD = 15;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide) return;
        if (maid.tickCount % INTERVAL != 0) return;

        boolean backpackFull = hasBackpackBauble(maid) && isBackpackFull(maid);

        var tag = baubleItem.getTag();
        if (tag == null || !tag.contains("BoundPos")) return;

        var pos = readBoundPos(tag);
        if (pos == null) return;

        String dim = tag.getString("BoundDim");
        if (!dim.equals(maid.level().dimension().location().toString())) return;
        if (!maid.level().hasChunkAt(pos)) return;

        var be = maid.level().getBlockEntity(pos);
        if (be == null || be.isRemoved()) { clearBinding(baubleItem, maid); return; }

        var opt = be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (opt.isEmpty()) { clearBinding(baubleItem, maid); return; }

        var containerInv = opt.get();
        var maidInv = maid.getMaidInv();

        // Take food if hungry
        if (maid.getHunger() < HUNGER_THRESHOLD && !hasFood(maidInv)) {
            for (int i = 0; i < containerInv.getSlots(); i++) {
                var stack = containerInv.getStackInSlot(i);
                if (stack.isEmpty() || !stack.getItem().isEdible()) continue;
                var taken = containerInv.extractItem(i, 1, false);
                if (taken.isEmpty()) continue;
                for (int j = 0; j < maidInv.getSlots(); j++) {
                    if (maidInv.getStackInSlot(j).isEmpty()) {
                        maidInv.setStackInSlot(j, taken);
                        return;
                    }
                }
                containerInv.insertItem(i, taken, false);
                return;
            }
        }

        // Deposit items (only if backpack is full or not present)
        if (!backpackFull) return;

        for (int i = 0; i < maidInv.getSlots(); i++) {
            var stack = maidInv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (isBauble(stack)) continue;
            var remaining = ItemHandlerHelper.insertItemStacked(containerInv, stack, false);
            maidInv.setStackInSlot(i, remaining);
            if (remaining.isEmpty()) break;
        }
    }

    private boolean hasFood(net.minecraftforge.items.ItemStackHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            var s = inv.getStackInSlot(i);
            if (!s.isEmpty() && s.getItem().isEdible()) return true;
        }
        return false;
    }

    private boolean isBackpackFull(EntityMaid maid) {
        var opt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(maid).resolve();
        if (opt.isEmpty()) return false;
        try {
            var curios = opt.get().getClass().getMethod("getCurios").invoke(opt.get());
            if (curios instanceof java.util.Map) {
                var val = ((java.util.Map<?, ?>) curios).get("back");
                if (val != null) {
                    var stacks = val.getClass().getMethod("getStacks").invoke(val);
                    int slots = (int) stacks.getClass().getMethod("getSlots").invoke(stacks);
                    var getStack = stacks.getClass().getMethod("getStackInSlot", int.class);
                    for (int i = 0; i < slots; i++) {
                        var s = (ItemStack) getStack.invoke(stacks, i);
                        if (s.isEmpty() || s.getCount() < s.getMaxStackSize()) return false;
                    }
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    private boolean hasBackpackBauble(EntityMaid maid) {
        var handler = maid.getMaidBauble();
        for (int i = 0; i < handler.getSlots(); i++) {
            var stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.BACKPACK_BAUBLE.get()) return true;
        }
        return false;
    }

    private void clearBinding(ItemStack stack, EntityMaid maid) {
        var tag = stack.getTag();
        if (tag != null) {
            tag.remove("BoundPos");
            tag.remove("BoundDim");
            tag.remove("BoundBlock");
        }
        var owner = maid.getOwner();
        if (owner != null) {
            owner.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "\u00A7c[Storage Marker] \u7ED1\u5B9A\u7684\u5B58\u50A8\u65B9\u5757\u5DF2\u635F\u574F\uFF0C\u5DF2\u6E05\u9664\u7ED1\u5B9A"));
        }
    }

    public static BlockPos readBoundPos(CompoundTag tag) {
        var t = tag.getCompound("BoundPos");
        return new BlockPos(t.getInt("X"), t.getInt("Y"), t.getInt("Z"));
    }

    public static void writeBoundPos(CompoundTag tag, BlockPos pos) {
        var t = new CompoundTag();
        t.putInt("X", pos.getX());
        t.putInt("Y", pos.getY());
        t.putInt("Z", pos.getZ());
        tag.put("BoundPos", t);
    }

    private boolean isBauble(ItemStack stack) {
        var id = stack.getItem();
        return id == ModItems.BACKPACK_BAUBLE.get()
            || id == ModItems.TRUE_IMMORTAL_BAUBLE.get()
            || id == ModItems.EFFECT_IMMUNE_BAUBLE.get()
            || id == ModItems.STORAGE_MARKER.get();
    }
}
