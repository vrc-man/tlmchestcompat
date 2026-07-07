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
    private double flightSpeed = 1.0;
    private boolean flightEnabled = true;
    private boolean slowFalling = false;
    private boolean nightVision = false;
    private int px, py;

    public PlayerImmortalScreen(double flightSpeed, boolean flightEnabled, boolean slowFalling, boolean nightVision) {
        super(Component.literal(""));
        this.flightSpeed = flightSpeed;
        this.flightEnabled = flightEnabled;
        this.slowFalling = slowFalling;
        this.nightVision = nightVision;
    }

    @Override
    protected void init() {
        int pw = 200, ph = 170;
        px = (width - pw) / 2;
        py = (height - ph) / 2;

        addRenderableWidget(Button.builder(
            Component.literal("\u00A7" + (flightEnabled ? "a\u98DE\u884C: ON" : "c\u98DE\u884C: OFF")),
            b -> { flightEnabled = !flightEnabled; b.setMessage(Component.literal("\u00A7" + (flightEnabled ? "a\u98DE\u884C: ON" : "c\u98DE\u884C: OFF"))); })
            .bounds(px + 10, py + 24, 120, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A77\u901F\u5EA6: \u00A7f" + String.format("%.1f", flightSpeed) + "x"),
            b -> {                 flightSpeed = flightSpeed >= 10.0 ? 0.5 : flightSpeed + 0.5; b.setMessage(Component.literal("\u00A77\u901F\u5EA6: \u00A7f" + String.format("%.1f", flightSpeed) + "x")); })
            .bounds(px + 10, py + 48, 120, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A7" + (slowFalling ? "a\u7F13\u964D: ON" : "c\u7F13\u964D: OFF")),
            b -> { slowFalling = !slowFalling; b.setMessage(Component.literal("\u00A7" + (slowFalling ? "a\u7F13\u964D: ON" : "c\u7F13\u964D: OFF"))); })
            .bounds(px + 10, py + 72, 120, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A7" + (nightVision ? "a\u591C\u89C6: ON" : "c\u591C\u89C6: OFF")),
            b -> { nightVision = !nightVision; b.setMessage(Component.literal("\u00A7" + (nightVision ? "a\u591C\u89C6: ON" : "c\u591C\u89C6: OFF"))); })
            .bounds(px + 10, py + 96, 120, 18).build());

        addRenderableWidget(Button.builder(
            Component.literal("\u00A7a[\u4FDD\u5B58]"),
            b -> {
                ModNetwork.CHANNEL.sendToServer(new PlayerImmortalConfigPacket(flightSpeed, flightEnabled, slowFalling, nightVision));
                onClose();
            }).bounds(px + 10, py + 130, 55, 18).build());

        addRenderableWidget(Button.builder(Component.literal("\u00A77[\u53D6\u6D88]"), b -> onClose())
            .bounds(px + 70, py + 130, 55, 18).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        int pw = 200, ph = 170;
        g.fill(px, py, px + pw, py + ph, 0xFF555555);
        g.fill(px + 1, py + 1, px + pw - 1, py + ph - 1, 0xFFC6C6C6);
        drawC(g, "\u00A70\u00A7l\u2726 Player Immortal", px + pw / 2, py + 6, 0xFF404040);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
