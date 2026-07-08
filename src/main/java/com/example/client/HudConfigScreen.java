package com.example.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HudConfigScreen extends Screen {
    private static final String[] POS = {"\u5DE6\u4E0A","\u4E2D\u4E0A","\u53F3\u4E0A","\u5DE6\u4E0B","\u53F3\u4E0B"};
    private static final int PW = 280, PH = 180, LH = 22, C1 = 10, C2 = 145;
    private EditBox intervalBox;
    private int px, py;

    public HudConfigScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
        int t = py + 24;

        // Row 0: HUD toggle + Position
        btn2(t, 0, "HUD", HudConfig.enabled ? "\u00A7aON" : "\u00A7cOFF", b -> {
            HudConfig.enabled = !HudConfig.enabled;
            b.setMessage(Component.literal(HudConfig.enabled ? "\u00A7aON" : "\u00A7cOFF"));
        });
        btn2(t, 1, "\u4F4D\u7F6E", POS[HudConfig.position], b -> {
            HudConfig.position = (HudConfig.position + 1) % 5;
            b.setMessage(Component.literal(POS[HudConfig.position]));
        });

        // Row 1: Scale -/+ and Opacity -/+
        t += LH;
        mkBtn("-", px + C1, t, 18, 18, () -> HudConfig.scale = Math.max(0.5f, HudConfig.scale - 0.1f));
        mkBtn("+", px + C1 + 95, t, 18, 18, () -> HudConfig.scale = Math.min(2.0f, HudConfig.scale + 0.1f));
        mkBtn("-", px + C2, t, 18, 18, () -> HudConfig.opacity = Math.max(0.05f, HudConfig.opacity - 0.05f));
        mkBtn("+", px + C2 + 95, t, 18, 18, () -> HudConfig.opacity = Math.min(1.0f, HudConfig.opacity + 0.05f));

        // Row 2: X ←→ and Y ↑↓
        t += LH;
        mkBtn("\u2190", px + C1, t, 22, 18, () -> HudConfig.xOffset = Math.max(-200, HudConfig.xOffset - 5));
        mkBtn("\u2192", px + C1 + 95, t, 22, 18, () -> HudConfig.xOffset = Math.min(200, HudConfig.xOffset + 5));
        mkBtn("\u2191", px + C2, t, 22, 18, () -> HudConfig.yOffset = Math.max(-200, HudConfig.yOffset - 5));
        mkBtn("\u2193", px + C2 + 95, t, 22, 18, () -> HudConfig.yOffset = Math.min(200, HudConfig.yOffset + 5));

        // Row 3: Update interval
        t += LH;
        intervalBox = new EditBox(font, px + C1, t + 2, 120, 14, Component.literal(""));
        intervalBox.setValue(String.valueOf(HudConfig.updateInterval / 20));
        intervalBox.setResponder(s -> {
            try { int v = Integer.parseInt(s); HudConfig.updateInterval = Math.max(3, Math.min(600, v)) * 20; } catch (Exception e) {}
        });
        addRenderableWidget(intervalBox);

        // Row 4: Back button
        t += LH + 4;
        addRenderableWidget(Button.builder(Component.literal("\u00A7a[\u5173\u95ED]"), b -> onClose()).bounds(px + PW / 2 - 30, t, 60, 18).build());
    }

    private void btn2(int y, int col, String label, String val, Button.OnPress cb) {
        int x = col == 0 ? px + C1 : px + C2;
        addRenderableWidget(Button.builder(Component.literal("\u00A77" + label + ": " + val), cb)
            .bounds(x, y, 125, 18).build());
    }

    private void mkBtn(String label, int x, int y, int w, int h, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
            .bounds(x, y, w, h).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        g.fill(px, py, px + PW, py + PH, 0xCC1A1A2E);
        drawC(g, "\u00A7b\u00A7l\u2726 HUD \u8BBE\u7F6E", px + PW / 2, py + 6, 0xFFFFFF);

        int t = py + 24 + LH;
        drawC(g, "\u00A77\u7F29\u653E: \u00A7f" + String.format("%.1fx", HudConfig.scale), px + C1 + 55, t + 1, 0xFFFFFF);
        drawC(g, "\u00A77\u900F\u660E\u5EA6: \u00A7f" + Math.round(HudConfig.opacity * 100) + "%", px + C2 + 55, t + 1, 0xFFFFFF);

        t += LH;
        drawC(g, "X: \u00A7f" + HudConfig.xOffset, px + C1 + 55, t + 1, 0xFFFFFF);
        drawC(g, "Y: \u00A7f" + HudConfig.yOffset, px + C2 + 55, t + 1, 0xFFFFFF);

        t += LH;
        g.drawString(font, "\u00A77\u66F4\u65B0\u9891\u7387(\u79D2):", px + C1, t - 1, 0x888888);
        g.drawString(font, "\u00A77\u8303\u56F4 3~600", px + C1 + 84, t - 1, 0x555555);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
