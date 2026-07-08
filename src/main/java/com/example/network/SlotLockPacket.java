package com.example.network;

import com.example.ModItems;
import com.example.SlotLockHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SlotLockPacket {
    private final boolean[] locks;
    private final String uuid;
    private final int baubleSlot; // -1 = hand item, 0+ = maid bauble slot index

    public SlotLockPacket(boolean[] locks, String uuid, int baubleSlot) {
        this.locks = locks;
        this.uuid = uuid;
        this.baubleSlot = baubleSlot;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(uuid);
        buf.writeInt(baubleSlot);
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            buf.writeBoolean(locks[i]);
        }
    }

    public static SlotLockPacket decode(FriendlyByteBuf buf) {
        var uuid = buf.readUtf();
        var baubleSlot = buf.readInt();
        var locks = new boolean[SlotLockHelper.SLOT_COUNT];
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            locks[i] = buf.readBoolean();
        }
        return new SlotLockPacket(locks, uuid, baubleSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            try {
                if (baubleSlot >= 0 && !uuid.isEmpty()) {
                    // Equipped on maid: find by UUID
                    var u = UUID.fromString(uuid);
                    var server = player.getServer();
                    if (server != null) {
                        for (var level : server.getAllLevels()) {
                            for (var e : level.getEntities().getAll()) {
                                if (e.getUUID().equals(u) && e instanceof com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid maid) {
                                    if (player.isCreative() || player.hasPermissions(2) || maid.getOwner() == player) {
                                        var handler = maid.getMaidBauble();
                                        if (baubleSlot < handler.getSlots()) {
                                            var baubleStack = handler.getStackInSlot(baubleSlot);
                                            if (!baubleStack.isEmpty()) {
                                                SlotLockHelper.setLocks(baubleStack, locks);
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                } else {
                    // Hand item mode: find the item in player's hand
                    var hand = player.getMainHandItem();
                    if (hand.getItem() == com.example.ModItems.BACKPACK_BAUBLE.get() || hand.getItem() == com.example.ModItems.STORAGE_MARKER.get()) {
                        SlotLockHelper.setLocks(hand, locks);
                        return;
                    }
                    hand = player.getOffhandItem();
                    if (hand.getItem() == com.example.ModItems.BACKPACK_BAUBLE.get() || hand.getItem() == com.example.ModItems.STORAGE_MARKER.get()) {
                        SlotLockHelper.setLocks(hand, locks);
                        return;
                    }
                }
            } catch (Exception ignored) {}
        });
        ctx.get().setPacketHandled(true);
    }
}