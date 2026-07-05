package com.example.client;

import com.example.ModNetwork;
import com.example.network.SlotLockPacket;
import net.minecraft.client.Minecraft;
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
        int pw = COLS * 18 + 20, ph = ROWS * 18 + 70;
        panelX = (width - pw) / 2;
        panelY = (height - ph) / 2;

        int idx = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                final int i = idx;
                var btn = new SlotButton(panelX + 10 + c * 18, panelY + 28 + r * 18, locks, i);
                addRenderableWidget(btn);
                idx++;
            }
        }

        int cx = panelX + (COLS * 18 + 20) / 2;
        addRenderableWidget(Button.builder(
            Component.literal("\u00A7a[\u4FDD\u5B58]"),
            b -> {
                ModNetwork.CHANNEL.sendToServer(new SlotLockPacket(locks));
                onClose();
            }).bounds(cx - 60, panelY + ROWS * 18 + 38, 55, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A77[\u53D6\u6D88]"),
            b -> onClose())
            .bounds(cx + 5, panelY + ROWS * 18 + 38, 55, 18).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        int pw = COLS * 18 + 20, ph = ROWS * 18 + 70;
        g.fill(panelX, panelY, panelX + pw, panelY + ph, 0xCC1A1A2E);
        drawC(g, "\u00A7b\u00A7l\u2726 \u69FD\u4F4D\u9501\u5B9A", panelX + pw / 2, panelY + 8, 0xFFFFFF);
        drawC(g, "\u00A77\u70B9\u51FB\u5207\u6362", panelX + pw / 2, panelY + ROWS * 18 + 24, 0x888888);
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
            int sx = getX(), sy = getY();
            g.fill(sx, sy, sx + width, sy + height, locks[index] ? 0xFF994444 : 0xFF444466);
            String sym = locks[index] ? "\u00A7c\u26D7" : "\u00A7a\u25CB";
            var mc = Minecraft.getInstance();
            g.drawString(mc.font, sym, sx + 4, sy + 3, 0xFFFFFF, false);
        }
    }
}
