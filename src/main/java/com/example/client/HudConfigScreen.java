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
    private static final int PW = 300, PH = 200, LH = 22, C1 = 15, C2 = 155;
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

        // Row 0
        btn2(t, 0, "\u00A7bHUD", "\u00A7a" + (HudConfig.enabled ? "ON" : "OFF"), b -> {
            HudConfig.enabled = !HudConfig.enabled;
            b.setMessage(Component.literal(HudConfig.enabled ? "\u00A7aON" : "\u00A7cOFF"));
        });
        btn2(t, 1, "\u00A77\u4F4D\u7F6E", POS[HudConfig.position], b -> {
            HudConfig.position = (HudConfig.position + 1) % 5;
            b.setMessage(Component.literal(POS[HudConfig.position]));
        });

        // Row 1
        t += LH;
        addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            HudConfig.scale = Math.max(0.5f, HudConfig.scale - 0.1f);
        }).bounds(px + C1, t, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            HudConfig.scale = Math.min(2.0f, HudConfig.scale + 0.1f);
        }).bounds(px + C1 + 100, t, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            HudConfig.opacity = Math.max(0.05f, HudConfig.opacity - 0.05f);
        }).bounds(px + C2, t, 18, 18).build());
        addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            HudConfig.opacity = Math.min(1.0f, HudConfig.opacity + 0.05f);
        }).bounds(px + C2 + 100, t, 18, 18).build());

        // Row 2
        t += LH;
        addRenderableWidget(Button.builder(Component.literal("\u2190"), b -> {
            HudConfig.xOffset = Math.max(-200, HudConfig.xOffset - 5);
        }).bounds(px + C1, t, 22, 18).build());
        addRenderableWidget(Button.builder(Component.literal("\u2192"), b -> {
            HudConfig.xOffset = Math.min(200, HudConfig.xOffset + 5);
        }).bounds(px + C1 + 100, t, 22, 18).build());
        addRenderableWidget(Button.builder(Component.literal("\u2191"), b -> {
            HudConfig.yOffset = Math.max(-200, HudConfig.yOffset - 5);
        }).bounds(px + C2, t, 22, 18).build());
        addRenderableWidget(Button.builder(Component.literal("\u2193"), b -> {
            HudConfig.yOffset = Math.min(200, HudConfig.yOffset + 5);
        }).bounds(px + C2 + 100, t, 22, 18).build());

        // Row 3
        t += LH;
        intervalBox = new EditBox(font, px + C1, t, 120, 16, Component.literal(""));
        intervalBox.setValue(String.valueOf(HudConfig.updateInterval / 20));
        intervalBox.setResponder(s -> {
            try { int v = Integer.parseInt(s); HudConfig.updateInterval = Math.max(3, Math.min(600, v)) * 20; } catch (Exception e) {}
        });
        addRenderableWidget(intervalBox);

        // Row 4
        t += LH + 6;
        addRenderableWidget(Button.builder(Component.literal("\u00A7a[\u8FD4\u56DE]"), b -> {
            if (ClientProxy.cachedMaidData != null) this.minecraft.setScreen(new InfoScreen(ClientProxy.cachedMaidData));
            else onClose();
        }).bounds(px + PW / 2 - 35, t, 70, 18).build());
    }

    private void btn2(int y, int col, String label, String val, Button.OnPress cb) {
        int x = col == 0 ? px + C1 : px + C2;
        addRenderableWidget(Button.builder(Component.literal(label + ": " + val), cb)
            .bounds(x, y, 130, 18).build());
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
        g.drawString(font, "\u00A77\u66F4\u65B0(\u79D2) [3~600]", px + C1, t - 2, 0x888888);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
