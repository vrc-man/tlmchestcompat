package com.example.network;

import com.example.SlotLockHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SlotLockPacket {
    private final boolean[] locks;

    public SlotLockPacket(boolean[] locks) {
        this.locks = locks;
    }

    public void encode(FriendlyByteBuf buf) {
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            buf.writeBoolean(locks[i]);
        }
    }

    public static SlotLockPacket decode(FriendlyByteBuf buf) {
        var locks = new boolean[SlotLockHelper.SLOT_COUNT];
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            locks[i] = buf.readBoolean();
        }
        return new SlotLockPacket(locks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            // Apply to the nearest maid
            var aabb = player.getBoundingBox().inflate(16);
            player.level().getEntitiesOfClass(
                net.minecraft.world.entity.TamableAnimal.class, aabb,
                m -> m.getOwner() == player).stream()
                .findFirst().ifPresent(maid -> SlotLockHelper.setLocks(maid, locks));
        });
        ctx.get().setPacketHandled(true);
    }
}
