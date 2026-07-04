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
    private static final String[] POS_NAMES = {"左上", "中上", "右上", "左下", "右下"};
    private EditBox intervalBox, scaleBox, xBox, yBox;

    public HudConfigScreen() {
        super(Component.literal("HUD Config"));
    }

    @Override
    protected void init() {
        int cx = width / 2, cy = height / 2;

        addRenderableWidget(Button.builder(
            Component.literal("\u00A7bHUD: " + (HudConfig.enabled ? "\u00A7aON" : "\u00A7cOFF")),
            b -> {
                HudConfig.enabled = !HudConfig.enabled;
                b.setMessage(Component.literal("\u00A7bHUD: " + (HudConfig.enabled ? "\u00A7aON" : "\u00A7cOFF")));
            }).bounds(cx - 80, cy - 80, 160, 20).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A77\u4F4D\u7F6E: " + POS_NAMES[HudConfig.position]),
            b -> {
                HudConfig.position = (HudConfig.position + 1) % 5;
                b.setMessage(Component.literal("\u00A77\u4F4D\u7F6E: " + POS_NAMES[HudConfig.position]));
            }).bounds(cx - 80, cy - 50, 160, 20).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A77\u7F29\u653E: \u00A7f" + String.format("%.1f", HudConfig.scale)),
            b -> {
                HudConfig.scale = HudConfig.scale >= 2.0f ? 0.5f : HudConfig.scale + 0.1f;
                b.setMessage(Component.literal("\u00A77\u7F29\u653E: \u00A7f" + String.format("%.1f", HudConfig.scale)));
            }).bounds(cx - 80, cy - 20, 160, 20).build());

        intervalBox = new EditBox(font, cx - 80, cy + 10, 160, 18, Component.literal(""));
        intervalBox.setValue(String.valueOf(HudConfig.updateInterval));
        intervalBox.setResponder(s -> {
            try { int v = Integer.parseInt(s); if (v > 0) HudConfig.updateInterval = v; } catch (Exception e) {}
        });
        addRenderableWidget(intervalBox);

        xBox = new EditBox(font, cx - 80, cy + 40, 75, 18, Component.literal(""));
        xBox.setValue(String.valueOf(HudConfig.xOffset));
        xBox.setResponder(s -> {
            try { HudConfig.xOffset = Integer.parseInt(s); } catch (Exception e) {}
        });
        addRenderableWidget(xBox);

        yBox = new EditBox(font, cx + 5, cy + 40, 75, 18, Component.literal(""));
        yBox.setValue(String.valueOf(HudConfig.yOffset));
        yBox.setResponder(s -> {
            try { HudConfig.yOffset = Integer.parseInt(s); } catch (Exception e) {}
        });
        addRenderableWidget(yBox);

        addRenderableWidget(Button.builder(Component.literal("\u00A7a[\u4FDD\u5B58]"), b -> onClose())
            .bounds(cx - 40, cy + 75, 80, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        int cx = width / 2, cy = height / 2;

        g.fill(cx - 95, cy - 95, cx + 95, cy + 108, 0xCC1A1A2E);
        g.drawString(font, "\u00A7b\u00A7l\u2726 HUD \u8BBE\u7F6E", cx - font.width("\u00A7b\u00A7l\u2726 HUD \u8BBE\u7F6E") / 2, cy - 90, 0xFFFFFF);

        g.drawString(font, "\u00A77\u66F4\u65B0\u9891\u7387(\u5657\u514B):", cx - 80, cy + 1, 0x888888);
        g.drawString(font, "\u00A77X\u504F\u79FB:", cx - 80, cy + 31, 0x888888);
        g.drawString(font, "\u00A77Y\u504F\u79FB:", cx + 5, cy + 31, 0x888888);
    }
}
