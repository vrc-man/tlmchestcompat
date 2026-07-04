package com.example.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class MaidHudOverlay {
    public static final IGuiOverlay HUD = (gui, g, partialTick, width, height) -> {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var closest = Lists.newArrayList(mc.level.entitiesForRendering()).stream()
            .filter(e -> e instanceof TamableAnimal)
            .map(e -> (TamableAnimal) e)
            .filter(m -> m.getOwner() == mc.player && m.distanceTo(mc.player) < 16)
            .findFirst();

        if (closest.isEmpty()) return;

        var maid = closest.get();
        var d = maid.getPersistentData();

        int x = 5, y = 5, w = 170, lh = 10;
        g.fill(x - 2, y - 2, x + w + 2, y + 60, 0xAA000000);

        g.drawString(mc.font, "\u00A7b\u2726 " + maid.getName().getString(), x, y, 0xFFFFFF);
        y += lh;

        float hpPct = maid.getHealth() / Math.max(maid.getMaxHealth(), 1);
        int barW = 160, barH = 7;
        g.fill(x, y, x + barW, y + barH, 0xFF333333);
        int barColor = hpPct > 0.3f ? 0xFF00AA00 : 0xFFFF4444;
        g.fill(x, y, x + Math.round(barW * hpPct), y + barH, barColor);
        String hpText = "\u00A7f" + Math.round(maid.getHealth()) + "/" + Math.round(maid.getMaxHealth());
        g.drawString(mc.font, hpText, x + (barW - mc.font.width(hpText)) / 2, y, 0xFFFFFF);
        y += lh + 2;

        g.drawString(mc.font, "\u00A77\u62A4\u7532:\u00A7f" + maid.getArmorValue() + "  \u00A77\u8FDB\u98DF:\u00A7f" + d.getInt("eatHP"), x, y, 0xFFFFFF);
        y += lh;

        g.drawString(mc.font, "\u00A77TPS:\u00A7e" + String.format("%.1f", mc.getFps()) + " FPS", x, y, 0xFFFFFF);
    };
}
