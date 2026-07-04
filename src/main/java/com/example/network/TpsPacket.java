package com.example.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TpsPacket {
    public float tps;

    public TpsPacket() {}
    public TpsPacket(float tps) { this.tps = tps; }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(tps);
    }

    public static TpsPacket decode(FriendlyByteBuf buf) {
        return new TpsPacket(buf.readFloat());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            com.example.client.ClientProxy.tps = tps));
        ctx.get().setPacketHandled(true);
    }
}
