package com.example;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class SlotLockHelper {
    public static final int SLOT_COUNT = 36;

    public static boolean[] getLocks(LivingEntity maid) {
        var d = maid.getPersistentData();
        var tag = d.getCompound("SlotLock");
        var locks = new boolean[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; i++) {
            locks[i] = tag.getBoolean("s" + i);
        }
        return locks;
    }

    public static void setLocks(LivingEntity maid, boolean[] locks) {
        var d = maid.getPersistentData();
        var tag = new CompoundTag();
        for (int i = 0; i < Math.min(locks.length, SLOT_COUNT); i++) {
            tag.putBoolean("s" + i, locks[i]);
        }
        d.put("SlotLock", tag);
    }
}
