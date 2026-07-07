package com.example.client;

import com.example.ModNetwork;
import com.example.network.MaidReflectPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaidReflectScreen extends Screen {
    private static final int PW = 240, PH = 180;
    private double reflectMult = 1.0;
    private int px, py;
    private Button reflectVal;

    public MaidReflectScreen(double reflectMult) {
        super(Component.literal(""));
        this.reflectMult = reflectMult;
    }

    @Override
    protected void init() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
        int x = px + 20, xw = PW - 40;

        int y = py + 50;

        addRenderableWidget(Button.builder(Component.literal("\u00A78\u00A7l反伤倍率"), b -> {}).bounds(x, y, 70, 36).build());
        addRenderableWidget(Button.builder(Component.literal("\u00A7c\u00A7l-"), b -> {
            reflectMult = Math.max(0, reflectMult - 0.5);
            reflectVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(reflectMult)));
        }).bounds(x + 80, y, 40, 36).build());
        reflectVal = Button.builder(Component.literal("\u00A70\u00A7l" + nf(reflectMult)), b -> {}).bounds(x + 124, y, 56, 36).build();
        addRenderableWidget(reflectVal);
        addRenderableWidget(Button.builder(Component.literal("\u00A7a\u00A7l+"), b -> {
            reflectMult = Math.min(10.0, reflectMult + 0.5);
            reflectVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(reflectMult)));
        }).bounds(x + 184, y, 40, 36).build());

        y += 50;
        addRenderableWidget(Button.builder(Component.literal("\u00A74\u00A7l\u2714 保存"), b -> {
            ModNetwork.CHANNEL.sendToServer(new MaidReflectPacket(reflectMult));
            onClose();
        }).bounds(x, y, xw, 36).build());
    }

    private String nf(double v) { return String.format("%.1f", v); }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);

        g.fill(px - 3, py - 3, px + PW + 3, py + PH + 3, 0xFFCC8899);
        g.fill(px, py, px + PW, py + PH, 0xFFFFF0F2);
        g.fill(px, py, px + PW, py + 30, 0xFFD4A0B0);
        drawC(g, "\u00A70\u00A7l\u2726 女仆反伤护符", px + PW / 2, py + 10, 0xFF442222);
        g.fill(px + 5, py + 30, px + PW - 5, py + 31, 0xFFCC8899);

        super.render(g, mx, my, pt);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
