package com.example.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerImmortalConfigPacket {
    public final double flightSpeed;
    public final boolean flightEnabled;
    public final boolean slowFalling;
    public final boolean nightVision;

    public PlayerImmortalConfigPacket(double flightSpeed, boolean flightEnabled, boolean slowFalling, boolean nightVision) {
        this.flightSpeed = flightSpeed;
        this.flightEnabled = flightEnabled;
        this.slowFalling = slowFalling;
        this.nightVision = nightVision;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(flightSpeed);
        buf.writeBoolean(flightEnabled);
        buf.writeBoolean(slowFalling);
        buf.writeBoolean(nightVision);
    }

    public static PlayerImmortalConfigPacket decode(FriendlyByteBuf buf) {
        return new PlayerImmortalConfigPacket(
            buf.readDouble(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.PlayerImmortalItem)) {
                stack = player.getOffhandItem();
                if (stack.isEmpty() || !(stack.getItem() instanceof com.example.PlayerImmortalItem)) return;
            }
            var tag = stack.getOrCreateTag();
            tag.putDouble("flightSpeed", flightSpeed);
            tag.putBoolean("flightEnabled", flightEnabled);
            tag.putBoolean("slowFalling", slowFalling);
            tag.putBoolean("nightVision", nightVision);
        });
        ctx.get().setPacketHandled(true);
    }
}
