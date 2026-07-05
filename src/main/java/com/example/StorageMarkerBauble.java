package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;

public class StorageMarkerBauble implements IMaidBauble {
    private static final int INTERVAL = 20;

    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide) return;
        if (maid.tickCount % INTERVAL != 0) return;

        // Check if Backpack Storage Core is also active → backpack has priority
        if (hasBackpackBauble(maid)) return;

        var tag = baubleItem.getTag();
        if (tag == null || !tag.contains("BoundPos")) return;

        var pos = readBoundPos(tag);
        if (pos == null) return;

        // Check dimension match
        var levelKey = maid.level().dimension();
        String dim = tag.getString("BoundDim");
        if (!dim.equals(levelKey.location().toString())) return;

        // Check if chunk is loaded
        if (!maid.level().hasChunkAt(pos)) return;

        // Check if block entity still exists and has inventory
        var be = maid.level().getBlockEntity(pos);
        if (be == null || be.isRemoved()) {
            clearBinding(baubleItem, maid);
            return;
        }

        var opt = be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (opt.isEmpty()) {
            clearBinding(baubleItem, maid);
            return;
        }

        var containerInv = opt.get();
        var maidInv = maid.getMaidInv();

        for (int i = 0; i < maidInv.getSlots(); i++) {
            var stack = maidInv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (isBauble(stack)) continue;

            var remaining = ItemHandlerHelper.insertItemStacked(containerInv, stack, false);
            maidInv.setStackInSlot(i, remaining);
            if (remaining.isEmpty()) break;
        }
    }

    private boolean hasBackpackBauble(EntityMaid maid) {
        var handler = maid.getMaidBauble();
        // Check if any equipped item is the backpack_bauble from our mod
        for (int i = 0; i < handler.getSlots(); i++) {
            var stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.BACKPACK_BAUBLE.get()) {
                return true;
            }
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
