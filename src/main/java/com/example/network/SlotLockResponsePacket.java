package com.example.network;

import com.example.SlotLockHelper;
import com.example.client.ClientProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SlotLockResponsePacket {
    public final boolean[] locks;
    public final String maidUuid;
    public final int baubleSlot;

    public SlotLockResponsePacket(String maidUuid, int baubleSlot, boolean[] locks) {
        this.maidUuid = maidUuid;
        this.baubleSlot = baubleSlot;
        this.locks = locks;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(maidUuid);
        buf.writeInt(baubleSlot);
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            buf.writeBoolean(locks[i]);
        }
    }

    public static SlotLockResponsePacket decode(FriendlyByteBuf buf) {
        var maidUuid = buf.readUtf();
        var baubleSlot = buf.readInt();
        var locks = new boolean[SlotLockHelper.SLOT_COUNT];
        for (int i = 0; i < SlotLockHelper.SLOT_COUNT; i++) {
            locks[i] = buf.readBoolean();
        }
        return new SlotLockResponsePacket(maidUuid, baubleSlot, locks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            ClientProxy.onBaubleLockResponse(this)));
        ctx.get().setPacketHandled(true);
    }
}
