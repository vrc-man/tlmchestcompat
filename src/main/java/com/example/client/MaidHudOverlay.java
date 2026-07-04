package com.example.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class MaidHudOverlay {
    public static final IGuiOverlay HUD = (gui, g, partialTick, width, height) -> {
        if (!HudConfig.enabled) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var closest = Lists.newArrayList(mc.level.entitiesForRendering()).stream()
            .filter(e -> e instanceof net.minecraft.world.entity.TamableAnimal)
            .map(e -> (net.minecraft.world.entity.TamableAnimal) e)
            .filter(m -> m.getOwner() == mc.player && m.distanceTo(mc.player) < 16)
            .findFirst();

        if (closest.isEmpty()) return;

        var maid = closest.get();
        var d = maid.getPersistentData();

        float s = HudConfig.scale;
        int pw = Math.round(180 * s), ph = Math.round(70 * s);
        int baseX, baseY;

        switch (HudConfig.position) {
            case 0: baseX = 5 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 1: baseX = (width - pw) / 2 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 2: baseX = width - pw - 5 + HudConfig.xOffset; baseY = 5 + HudConfig.yOffset; break;
            case 3: baseX = 5 + HudConfig.xOffset; baseY = height - ph - 5 + HudConfig.yOffset; break;
            default: baseX = width - pw - 5 + HudConfig.xOffset; baseY = height - ph - 5 + HudConfig.yOffset; break;
        }

        g.pose().pushPose();
        g.pose().scale(s, s, 1);

        int gx = Math.round(baseX / s), gy = Math.round(baseY / s);
        g.fill(gx - 2, gy - 2, gx + pw + 2, gy + ph + 2, 0xAA000000);

        g.drawString(mc.font, "\u00A7b\u2726 " + maid.getName().getString(), gx + 4, gy + 2, 0xFFFFFF);
        gy += 12;

        float hpPct = maid.getHealth() / Math.max(maid.getMaxHealth(), 1);
        int barW = 160, barH = 7;
        g.fill(gx + 4, gy, gx + 4 + barW, gy + barH, 0xFF333333);
        int barColor = hpPct > 0.3f ? 0xFF00AA00 : 0xFFFF4444;
        g.fill(gx + 4, gy, gx + 4 + Math.round(barW * hpPct), gy + barH, barColor);
        String hpText = "\u00A7f" + Math.round(maid.getHealth()) + "/" + Math.round(maid.getMaxHealth());
        g.drawString(mc.font, hpText, gx + 4 + (barW - mc.font.width(hpText)) / 2, gy + 1, 0xFFFFFF);
        gy += 11;

        g.drawString(mc.font, "\u00A77\u62A4\u7532:\u00A7f" + maid.getArmorValue() + "  \u00A77\u8FDB\u98DF:\u00A7f" + d.getInt("eatHP"), gx + 4, gy, 0xFFFFFF);
        gy += 10;

        float tps = ClientProxy.tps;
        int tpsColor = tps > 18 ? 0xFF55FF55 : (tps > 10 ? 0xFFFFAA00 : 0xFFFF4444);
        g.drawString(mc.font, "\u00A77TPS:\u00A7" + (tps > 18 ? "a" : (tps > 10 ? "e" : "c")) + String.format("%.1f", tps), gx + 4, gy, 0xFFFFFF);
        gy += 10;

        float fps = mc.getFps();
        g.drawString(mc.font, "\u00A77FPS:\u00A7f" + String.format("%.0f", fps), gx + 80, gy - 10, 0xFFFFFF);

        g.pose().popPose();
    };
}
