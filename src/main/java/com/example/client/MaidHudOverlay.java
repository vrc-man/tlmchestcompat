package com.example.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class MaidHudOverlay {
    private static final int CW = 180, CH_SMALL = 40, CH_BIG = 72;

    public static final IGuiOverlay HUD = (gui, g, partialTick, width, height) -> {
        if (!HudConfig.enabled) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var maids = Lists.newArrayList(mc.level.entitiesForRendering()).stream()
            .filter(e -> e instanceof net.minecraft.world.entity.TamableAnimal)
            .map(e -> (net.minecraft.world.entity.TamableAnimal) e)
            .filter(m -> m.getOwner() == mc.player)
            .toList();

        // If tracking a specific maid, show her regardless of distance
        var closest = maids.stream()
            .filter(m -> ClientProxy.trackedMaidUUID == null || m.getUUID().toString().equals(ClientProxy.trackedMaidUUID))
            .filter(m -> ClientProxy.trackedMaidUUID != null || m.distanceTo(mc.player) < 16)
            .findFirst();

        boolean hasMaid = closest.isPresent();
        float s = HudConfig.scale;
        int ph = Math.round((hasMaid ? CH_BIG : CH_SMALL) * s);
        int pw = Math.round(CW * s);
        int baseX, baseY;

        switch (HudConfig.position) {
            case 0: baseX = 5 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 1: baseX = (width - pw) / 2 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 2: baseX = width - pw - 5 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 3: baseX = 5 + HudConfig.xOffset; baseY = height - ph - 5 + HudConfig.yOffset; break;
            default: baseX = width - pw - 5 + HudConfig.xOffset; baseY = height - ph - 5 + HudConfig.yOffset; break;
        }

        // Background
        int alpha = Math.round(HudConfig.opacity * 255);
        int bgColor = (Math.min(255, Math.max(0, alpha)) << 24) | 0x000000;
        g.fill(baseX - 2, baseY - 2, baseX + pw + 2, baseY + ph + 2, bgColor);

        g.pose().pushPose();
        g.pose().translate(baseX, baseY, 0);
        g.pose().scale(s, s, 1);

        if (hasMaid) {
            var maid = closest.get();
            var d = maid.getPersistentData();

            g.drawString(mc.font, "\u00A7b\u2726 " + maid.getName().getString(), 4, 2, 0xFFFFFF);

            float hpPct = maid.getHealth() / Math.max(maid.getMaxHealth(), 1);
            g.fill(4, 14, 4 + 164, 14 + 8, 0xFF333333);
            int barColor = hpPct > 0.3f ? 0xFF00AA00 : 0xFFFF4444;
            g.fill(4, 14, 4 + Math.round(164 * hpPct), 14 + 8, barColor);
            String hpText = "\u00A7f" + Math.round(maid.getHealth()) + "/" + Math.round(maid.getMaxHealth());
            g.drawString(mc.font, hpText, 4 + (164 - mc.font.width(hpText)) / 2, 15, 0xFFFFFF);

            g.drawString(mc.font, "\u00A77\u62A4\u7532:\u00A7f" + maid.getArmorValue(), 4, 27, 0xFFFFFF);
            g.drawString(mc.font, "\u00A77\u8FDB\u98DF:\u00A7f" + d.getInt("eatHP"), 90, 27, 0xFFFFFF);

            drawInfo(g, mc, 39);
        } else {
            g.drawString(mc.font, "\u00A77\u672A\u68C0\u6D4B\u5230\u5973\u4EC6", 4, 2, 0x888888);
            drawInfo(g, mc, 16);
        }

        g.pose().popPose();
    };

    private static void drawInfo(net.minecraft.client.gui.GuiGraphics g, Minecraft mc, int y) {
        float tps = ClientProxy.tps;
        String tpsCol = tps > 18 ? "a" : (tps > 10 ? "e" : "c");
        g.drawString(mc.font, "\u00A77TPS:\u00A7" + tpsCol + String.format("%.1f", tps), 4, y, 0xFFFFFF);
        g.drawString(mc.font, "\u00A77FPS:\u00A7f" + String.format("%.0f", (float) mc.getFps()), 90, y, 0xFFFFFF);

        if (mc.level != null) {
            long time = mc.level.getDayTime();
            int hour = (int) ((time / 1000 + 6) % 24);
            int minute = (int) ((time % 1000) * 60 / 1000);
            g.drawString(mc.font, "\u00A77\u65F6\u95F4:\u00A7f" + String.format("%02d:%02d", hour, minute), 4, y + 11, 0xFFFFFF);
        }
        g.drawString(mc.font, "\u00A77S:\u00A7f" + HudConfig.xOffset + "\u00A77,\u00A7f" + HudConfig.yOffset, 90, y + 11, 0xFFFFFF);
    }
}
