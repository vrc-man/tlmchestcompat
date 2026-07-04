package com.example.client;

import com.example.network.MaidDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {
    public static volatile float tps = 20.0f;
    public static MaidDataPacket cachedMaidData;

    public static void onMaidData(MaidDataPacket packet) {
        cachedMaidData = packet;
        var mc = Minecraft.getInstance();
        if (mc.screen instanceof InfoScreen) {
            mc.screen.onClose();
        }
        mc.setScreen(new InfoScreen(packet));
    }
}
