package com.example.client;

import com.example.ModNetwork;
import com.example.network.PlayerImmortalConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerImmortalScreen extends Screen {
    private static final int PW = 320, PH = 440, BH = 36, BH2 = 50;
    private double flightSpeed = 1.0;
    private boolean flightEnabled = true;
    private boolean slowFalling = false;
    private boolean nightVision = false;
    private double reflectMult = 1.0;
    private int px, py;
    private Button btnFlight, btnSlow, btnNight, speedVal, reflectVal;

    public PlayerImmortalScreen(double flightSpeed, boolean flightEnabled, boolean slowFalling, boolean nightVision, double reflectMult) {
        super(Component.literal(""));
        this.flightSpeed = flightSpeed;
        this.flightEnabled = flightEnabled;
        this.slowFalling = slowFalling;
        this.nightVision = nightVision;
        this.reflectMult = reflectMult;
    }

    @Override
    protected void init() {
        px = (width - PW) / 2;
        py = (height - PH) / 2;
        int x = px + 20, xw = PW - 40;

        int y = py + 40;

        // Row 1: Flight — ON/OFF raised rectangular button
        btnFlight = addRowBtn(x, y, "飞行", flightEnabled, b -> {
            flightEnabled = !flightEnabled;
            b.setMessage(lbl(flightEnabled));
        });

        // Row 2: Speed
        y += 64;
        addLabel("飞行速度", x, y);
        addRenderableWidget(Button.builder(Component.literal("\u00A7c\u00A7l-"), b -> {
            flightSpeed = Math.max(0.5, flightSpeed - 0.5);
            speedVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(flightSpeed)));
        }).bounds(x + 90, y, 50, BH).build());
        speedVal = Button.builder(Component.literal("\u00A70\u00A7l" + nf(flightSpeed)), b -> {}).bounds(x + 144, y, 76, BH).build();
        addRenderableWidget(speedVal);
        addRenderableWidget(Button.builder(Component.literal("\u00A7a\u00A7l+"), b -> {
            flightSpeed = Math.min(10.0, flightSpeed + 0.5);
            speedVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(flightSpeed)));
        }).bounds(x + 224, y, 50, BH).build());

        // Row 3: Slow Fall
        y += 64;
        btnSlow = addRowBtn(x, y, "缓降", slowFalling, b -> {
            slowFalling = !slowFalling;
            b.setMessage(lbl(slowFalling));
        });

        // Row 4: Night Vision
        y += 64;
        btnNight = addRowBtn(x, y, "夜视", nightVision, b -> {
            nightVision = !nightVision;
            b.setMessage(lbl(nightVision));
        });

        // Row 5: Reflect
        y += 64;
        addLabel("反伤倍率", x, y);
        addRenderableWidget(Button.builder(Component.literal("\u00A7c\u00A7l-"), b -> {
            reflectMult = Math.max(0, reflectMult - 0.5);
            reflectVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(reflectMult)));
        }).bounds(x + 90, y, 50, BH).build());
        reflectVal = Button.builder(Component.literal("\u00A70\u00A7l" + nf(reflectMult)), b -> {}).bounds(x + 144, y, 76, BH).build();
        addRenderableWidget(reflectVal);
        addRenderableWidget(Button.builder(Component.literal("\u00A7a\u00A7l+"), b -> {
            reflectMult = Math.min(10.0, reflectMult + 0.5);
            reflectVal.setMessage(Component.literal("\u00A70\u00A7l" + nf(reflectMult)));
        }).bounds(x + 224, y, 50, BH).build());

        // Row 6: Save
        y += 58;
        addRenderableWidget(Button.builder(Component.literal("\u00A74\u00A7l\u2714 保存"), b -> {
            ModNetwork.CHANNEL.sendToServer(new PlayerImmortalConfigPacket(flightSpeed, flightEnabled, slowFalling, nightVision, reflectMult));
            onClose();
        }).bounds(x, y, xw, BH).build());
    }

    private Button addRowBtn(int x, int y, String label, boolean state, Button.OnPress onPress) {
        addLabel(label, x, y);
        var btn = Button.builder(lbl(state), onPress).bounds(x + 70, y, 120, BH2).build();
        addRenderableWidget(btn);
        return btn;
    }

    private void addLabel(String text, int x, int y) {
        addRenderableWidget(Button.builder(Component.literal("\u00A78\u00A7l" + text), b -> {}).bounds(x, y, 70, BH).build());
    }

    private Component lbl(boolean state) {
        return Component.literal("\u00A7l" + (state ? "\u00A7a\u2714 ON" : "\u00A78\u2718 OFF"));
    }

    private String nf(double v) { return String.format("%.1f", v); }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);

        g.fill(px - 3, py - 3, px + PW + 3, py + PH + 3, 0xFFCC8899);
        g.fill(px, py, px + PW, py + PH, 0xFFFFF0F2);
        g.fill(px, py, px + PW, py + 30, 0xFFD4A0B0);
        drawC(g, "\u00A70\u00A7l\u2726 Player Immortal Charm", px + PW / 2, py + 10, 0xFF442222);
        g.fill(px + 5, py + 30, px + PW - 5, py + 31, 0xFFCC8899);

        super.render(g, mx, my, pt);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
