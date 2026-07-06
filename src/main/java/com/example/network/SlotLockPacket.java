package com.example.network;

import com.example.SlotLockHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SlotLockPacket {
    private final boolean[] locks;
    private final String uuid;

    public SlotLockPacket(boolean[] locks, String uuid) {
        this.locks = locks;
        this.uuid = uuid;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(uuid);
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            buf.writeBoolean(locks[i]);
        }
    }

    public static SlotLockPacket decode(FriendlyByteBuf buf) {
        var uuid = buf.readUtf();
        var locks = new boolean[SlotLockHelper.SLOT_COUNT];
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            locks[i] = buf.readBoolean();
        }
        return new SlotLockPacket(locks, uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            try {
                var u = UUID.fromString(uuid);
                for (var e : player.serverLevel().getEntities().getAll()) {
                    if (e.getUUID().equals(u) && e instanceof net.minecraft.world.entity.LivingEntity living) {
                        SlotLockHelper.setLocks(living, locks);
                        return;
                    }
                }
            } catch (Exception ignored) {}
        });
        ctx.get().setPacketHandled(true);
    }
}
