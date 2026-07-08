package com.example.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("removal")
public class ClientInit {
    @SuppressWarnings("removal")
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(new ClientInit());
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
    }

    @OnlyIn(Dist.CLIENT)
    public static class ForgeEvents {
        private long lastNano = System.nanoTime();
        private float smoothedTps = 20.0f;

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            long now = System.nanoTime();
            long dt = now - lastNano;
            lastNano = now;
            float tickTime = dt / 1_000_000f;
            float instantTps = tickTime > 0 ? 1000f / tickTime : 20f;
            smoothedTps = smoothedTps * 0.95f + Math.min(instantTps, 20f) * 0.05f;
            ClientProxy.tps = smoothedTps;
        }

        @SubscribeEvent
        public void onClick(InputEvent.InteractionKeyMappingTriggered event) {
            if (!event.isUseItem()) return;
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;
            var stack = mc.player.getMainHandItem();
            if (stack.isEmpty()) return;
            if (stack.getItem() == com.example.ModItems.PLAYER_IMMORTAL_BAUBLE.get()) {
                if (mc.hitResult == null || mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS) {
                    if (mc.player.isCrouching()) {
                        HudConfig.enabled = !HudConfig.enabled;
                    }
                }
            }
        }
    }
}
