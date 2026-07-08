package com.example.client;

import com.example.network.MaidDataPacket;
import com.example.network.SlotLockResponsePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {
    public static volatile float tps = 20.0f;
    public static MaidDataPacket cachedMaidData;
    public static String trackedMaidUUID; // UUID of maid to track in HUD, null = nearest

    public static void onMaidData(MaidDataPacket packet) {
        cachedMaidData = packet;
        trackedMaidUUID = packet.uuid; // auto-track when scanned
        var mc = Minecraft.getInstance();
        if (mc.screen instanceof InfoScreen) {
            mc.screen.onClose();
        }
        mc.setScreen(new InfoScreen(packet));
    }

    public static void onBaubleLockResponse(SlotLockResponsePacket packet) {
        var mc = Minecraft.getInstance();
        mc.setScreen(new SlotLockScreen(packet.locks, packet.maidUuid, packet.baubleSlot));
    }
}
