package com.example.client;

import com.example.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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

    @SubscribeEvent
    public void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(
            com.example.MarkerMenuType.MARKER_MENU.get(),
            com.example.client.MarkerScreen::new));
    }

    @SubscribeEvent
    public void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "maid_hud", MaidHudOverlay.HUD);
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
            if (stack.isEmpty() || stack.getItem() != ModItems.INFO_SCANNER.get()) return;
            if (mc.hitResult == null || mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.MISS) {
                if (!mc.player.isCrouching()) {
                    event.setCanceled(true);
                    mc.setScreen(new HudConfigScreen());
                } else {
                    HudConfig.enabled = !HudConfig.enabled;
                }
            }
        }
    }
}
