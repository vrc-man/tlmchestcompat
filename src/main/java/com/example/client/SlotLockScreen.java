package com.example.client;

import com.example.ModNetwork;
import com.example.network.SlotLockPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlotLockScreen extends Screen {
    private static final int COLS = 6, ROWS = 6;
    private final boolean[] locks;
    private int panelX, panelY;

    public SlotLockScreen(boolean[] locks) {
        super(Component.literal(""));
        this.locks = locks;
    }

    @Override
    protected void init() {
        int pw = COLS * 18 + 28, ph = ROWS * 18 + 66;
        panelX = (width - pw) / 2;
        panelY = (height - ph) / 2;

        int idx = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                final int i = idx;
                addRenderableWidget(new SlotButton(panelX + 14 + c * 18, panelY + 26 + r * 18, locks, i));
                idx++;
            }
        }

        int cx = panelX + pw / 2;
        addRenderableWidget(Button.builder(
            Component.literal("\u00A7r\u4FDD\u5B58"),
            b -> {
                ModNetwork.CHANNEL.sendToServer(new SlotLockPacket(locks));
                onClose();
            }).bounds(cx - 62, panelY + ROWS * 18 + 34, 58, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A77\u53D6\u6D88"),
            b -> onClose())
            .bounds(cx + 4, panelY + ROWS * 18 + 34, 58, 18).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        int pw = COLS * 18 + 28, ph = ROWS * 18 + 66;

        // Vanilla-style panel background
        g.fill(panelX, panelY, panelX + pw, panelY + ph, 0xFF555555);
        g.fill(panelX + 1, panelY + 1, panelX + pw - 1, panelY + ph - 1, 0xFFC6C6C6);

        // Title
        drawC(g, "\u00A70\u69FD\u4F4D\u9501\u5B9A", panelX + pw / 2, panelY + 8, 0xFF404040);

        // Draw slot backgrounds
        int idx = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int sx = panelX + 14 + c * 18;
                int sy = panelY + 26 + r * 18;
                if (locks[idx]) {
                    g.fill(sx, sy, sx + 16, sy + 16, 0xFF994444);
                } else {
                    g.fill(sx, sy, sx + 16, sy + 16, 0xFF8B8B8B);
                    g.fill(sx + 1, sy + 1, sx + 15, sy + 15, 0xFFC6C6C6);
                }
                String sym = locks[idx] ? "\u00A7c\u26D7" : "\u00A7a\u25CB";
                g.drawString(font, sym, sx + 4, sy + 4, 0xFFFFFF, false);
                idx++;
            }
        }

        drawC(g, "\u00A78\u70B9\u51FB\u5207\u6362\u9501\u5B9A", panelX + pw / 2, panelY + ROWS * 18 + 22, 0xFF555555);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }

    private static class SlotButton extends Button {
        private final boolean[] locks;
        private final int index;

        SlotButton(int x, int y, boolean[] locks, int index) {
            super(Button.builder(Component.literal(""), b -> {
                locks[index] = !locks[index];
            }).bounds(x, y, 16, 16));
            this.locks = locks;
            this.index = index;
        }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            // Custom rendering handled in the parent screen's render
        }
    }
}
