package com.example.client;

import com.example.ModNetwork;
import com.example.network.BaubleLockRequestPacket;
import com.example.network.MaidDataPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfoScreen extends Screen {
    private final MaidDataPacket data;
    private static final int W = 240, H = 270;
    private int uuidY, uuidEndX;

    public InfoScreen(MaidDataPacket data) {
        super(Component.literal(""));
        this.data = data;
    }

    @Override
    protected void init() {
        int bx = width / 2 - 112, by = height / 2 + 115;

        addRenderableWidget(Button.builder(Component.literal("\u00A77[HUD]"), b ->
            this.minecraft.setScreen(new HudConfigScreen())).bounds(bx, by, 72, 16).build());

        addRenderableWidget(Button.builder(Component.literal("\u00A7e[UUID]"), b -> {
            if (data != null) {
                this.minecraft.keyboardHandler.setClipboard(data.uuid);
                this.minecraft.player.sendSystemMessage(
                    Component.literal("\u00A7aUUID\u5DF2\u590D\u5236\u5230\u526A\u8D34\u677F"));
            }
        }).bounds(bx + 76, by, 72, 16).build());

        // Row 2: Track button
        int by2 = by + 20;
        boolean isTracking = ClientProxy.trackedMaidUUID != null && data != null && ClientProxy.trackedMaidUUID.equals(data.uuid);
        addRenderableWidget(Button.builder(
            Component.literal(isTracking ? "\u00A7a[\u8FFD\u8E2A\u4E2D]" : "\u00A77[\u8FFD\u8E2A]"),
            b -> {
                if (ClientProxy.trackedMaidUUID != null && ClientProxy.trackedMaidUUID.equals(data.uuid)) {
                    ClientProxy.trackedMaidUUID = null; // 取消追踪
                } else {
                    ClientProxy.trackedMaidUUID = data.uuid; // 追踪这个女仆
                }
                // Refresh screen to update button text
                this.minecraft.setScreen(new InfoScreen(data));
            }).bounds(bx + 76, by2, 72, 16).build());

        // Row 3: Slot lock buttons for equipped baubles
        int by3 = by2 + 20;
        int slotX = bx;
        if (data != null && (data.equippedBaubles & 1) != 0) {
            addRenderableWidget(Button.builder(Component.literal("\u00A7c[\u69FD\u4F4D\u80CC\u5305]"), b -> {
                ModNetwork.CHANNEL.sendToServer(new BaubleLockRequestPacket(data.uuid, findBaubleSlot(0)));
            }).bounds(slotX, by3, 72, 16).build());
            slotX += 76;
        }
        if (data != null && (data.equippedBaubles & 2) != 0) {
            addRenderableWidget(Button.builder(Component.literal("\u00A7c[\u69FD\u4F4D\u5B58\u50A8]"), b -> {
                ModNetwork.CHANNEL.sendToServer(new BaubleLockRequestPacket(data.uuid, findBaubleSlot(1)));
            }).bounds(slotX, by3, 72, 16).build());
        }
    }

    private int findBaubleSlot(int type) {
        if (data == null) return -1;
        // 0=backpack, 1=storage_marker
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null) return -1;
        for (var e : mc.level.entitiesForRendering()) {
            if (e.getUUID().toString().equals(data.uuid) && e instanceof com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid maid) {
                var bauble = maid.getMaidBauble();
                for (int i = 0; i < bauble.getSlots(); i++) {
                    var item = bauble.getStackInSlot(i).getItem();
                    if (type == 0 && item == com.example.ModItems.BACKPACK_BAUBLE.get()) return i;
                    if (type == 1 && item == com.example.ModItems.STORAGE_MARKER.get()) return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);
        if (data == null) return;

        int px = (width - W) / 2, py = (height - H) / 2;

        // Background
        fill(g, px, py, px + W, py + H, 0xCC1A1A2E);
        drawBorder(g, px, py, W, H, 0xFF4A4A8A);

        // Title
        drawC(g, "\u00A7b\u00A7l\u2726 \u5973\u4EC6\u4FE1\u606F\u9762\u677F", px + W / 2, py + 12, 0xFFFFFF);

        sep(g, px + 10, py + 25, W - 20);

        int y = py + 36, lh = 12;
        int c1 = px + 15, c2 = px + 75;

        // Basic info
        drawKV(g, "UUID", c1, y, trunc(data.uuid, 22), c2, y, 0xAAAAAA); y += lh;
        drawKV(g, "\u540D\u79F0", c1, y, data.name, c2, y, 0xFFFFAA); y += lh;
        drawKV(g, "\u4E3B\u4EBA", c1, y, data.ownerName, c2, y, 0x55FF55); y += lh;

        sep(g, px + 10, y, W - 20); y += 7;

        // Stats
        drawKV(g, "\u751F\u547D\u503C", c1, y, f0(data.health) + "/" + f0(data.maxHealth) + " \u00A77(base:" + f0(data.baseHp) + " x" + mult(data.maxHealth, data.baseHp) + ")", c2, y, 0xFF5555); y += lh;
        drawKV(g, "\u62A4\u7532", c1, y, statStr(data.armor, data.baseArmor), c2, y, 0x55AAFF); y += lh;
        drawKV(g, "\u97E7\u6027", c1, y, statStr(data.toughness, data.baseToughness), c2, y, 0xAAAAAA); y += lh;
        drawKV(g, "\u4F24\u5BB3", c1, y, statStr(data.damage, data.baseDamage), c2, y, 0xFF5555); y += lh;

        sep(g, px + 10, y, W - 20); y += 7;

        // Growth - 4 per row
        drawG(g, "\u7206\u70B8 Lv." + data.explosion, c1, y, 0x55FF55);
        drawG(g, "\u53CD\u5F39 Lv." + data.thorns, c1 + 100, y, 0x55FF55); y += lh;
        drawG(g, "\u62A4\u7532\u97E7\u6027 +" + data.tough, c1, y, 0x55FF55);
        drawG(g, "\u653B\u51FB\u8DDD\u79BB +" + f1(data.reach), c1 + 100, y, 0x55FF55); y += lh;
        drawG(g, "\u901F\u5EA6 +" + f2(data.speed), c1, y, 0x55FF55);
        drawG(g, "\u66B4\u51FB Lv." + data.crit, c1 + 100, y, 0x55FF55); y += lh;
        drawG(g, "\u6316\u6398 +" + data.dig, c1, y, 0x55FF55);
        drawG(g, "\u56DE\u8840 " + Math.round(data.regenPct * 100) + "%", c1 + 100, y, 0x55FF55); y += lh;
        drawG(g, "\u8FDB\u98DF\u6B21\u6570 " + data.eatHP, c1, y, 0x55FF55);

        sep(g, px + 10, y + 10, W - 20);

        drawC(g, "\u00A77[\u6F5C\u884C+\u53F3\u952E: \u5168\u5C5E\u6027+9000]", px + W / 2, py + H - 36, 0x888888);
    }

    private void drawKV(GuiGraphics g, String k, int x1, int y1, String v, int x2, int y2, int vc) {
        g.drawString(font, "\u00A77" + k + "\u00A7r", x1, y1, 0xFFFFFF, false);
        g.drawString(font, "\u00A7f" + v, x2, y2, vc, false);
    }

    private void drawG(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, "\u00A7a" + t, x, y, c, false);
    }

    private void drawC(GuiGraphics g, String t, int x, int y, int c) {
        g.drawString(font, t, x - font.width(t) / 2, y, c, false);
    }

    private void fill(GuiGraphics g, int x1, int y1, int x2, int y2, int c) {
        g.fill(x1, y1, x2, y2, c);
    }

    private void sep(GuiGraphics g, int x, int y, int w) {
        g.fill(x, y, x + w, y + 1, 0xFF4A4A8A);
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int c) {
        g.fill(x, y, x + w, y + 1, c);
        g.fill(x, y + h - 1, x + w, y + h, c);
        g.fill(x, y, x + 1, y + h, c);
        g.fill(x + w - 1, y, x + w, y + h, c);
    }

    private String f0(float v) { return String.valueOf(Math.round(v)); }
    private String f1(double v) { return String.format("%.1f", v); }
    private String f2(double v) { return String.format("%.2f", v); }
    private String mult(float v, float b) { return b <= 0 ? "0" : String.format("%.2f", v / b); }
    private String statStr(float val, float base) {
        if (base > 0) return f0(val) + " \u00A77(x" + mult(val, base) + ")";
        return f0(val) + " \u00A77(+" + f0(val) + ")";
    }
    private String trunc(String s, int n) { return s.length() > n ? s.substring(0, n) + ".." : s; }
}
