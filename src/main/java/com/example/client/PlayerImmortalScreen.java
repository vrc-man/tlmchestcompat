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
    private double reflectMult = 0;
    private int px, py;

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
        int pw = 200, ph = 200;
        px = (width - pw) / 2;
        py = (height - ph) / 2;

        int y = py + 24;
        addRenderableWidget(btn("\u00A7" + (flightEnabled ? "a\u98DE\u884C: ON" : "c\u98DE\u884C: OFF"), y,
            () -> { flightEnabled = !flightEnabled; }));

        y += 26;
        addRenderableWidget(btn("\u00A77\u98DE\u884C\u901F\u5EA6: \u00A7f" + String.format("%.1f", flightSpeed) + "x", y,
            () -> { flightSpeed = flightSpeed >= 10.0 ? 0.5 : flightSpeed + 0.5; }));

        y += 26;
        addRenderableWidget(btn("\u00A7" + (slowFalling ? "a\u7F13\u964D: ON" : "c\u7F13\u964D: OFF"), y,
            () -> { slowFalling = !slowFalling; }));

        y += 26;
        addRenderableWidget(btn("\u00A7" + (nightVision ? "a\u591C\u89C6: ON" : "c\u591C\u89C6: OFF"), y,
            () -> { nightVision = !nightVision; }));

        y += 26;
        addRenderableWidget(btn("\u00A77\u53CD\u4F24: \u00A7f" + String.format("%.1f", reflectMult) + "x", y,
            () -> { reflectMult = reflectMult >= 10.0 ? 0 : reflectMult + 0.5; }));

        int by = py + ph - 28;
        addRenderableWidget(Button.builder(Component.literal("\u00A7a[\u4FDD\u5B58]"),
            b -> {
                ModNetwork.CHANNEL.sendToServer(new PlayerImmortalConfigPacket(flightSpeed, flightEnabled, slowFalling, nightVision, reflectMult));
                onClose();
            }).bounds(px + 10, by, 55, 18).build());
        addRenderableWidget(Button.builder(Component.literal("\u00A77[\u53D6\u6D88]"), b -> onClose())
            .bounds(px + 70, by, 55, 18).build());
    }

    private Button btn(String text, int y, Runnable toggle) {
        return Button.builder(Component.literal(text), b -> { toggle.run(); b.setMessage(Component.literal(text)); })
            .bounds(px + 10, y, 150, 18).build();
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);
        g.fill(px, py, px + 200, py + 200, 0xFF555555);
        g.fill(px + 1, py + 1, px + 199, py + 199, 0xFFC6C6C6);
        drawC(g, "\u00A70\u00A7l\u2726 Player Immortal", px + 100, py + 6, 0xFF404040);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }
}
