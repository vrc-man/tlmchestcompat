package com.example.client;

import com.example.MarkerContainer;
import com.example.ModNetwork;
import com.example.network.MarkerConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class MarkerScreen extends AbstractContainerScreen<MarkerContainer> {
    private boolean isBlacklist = true;

    public MarkerScreen(MarkerContainer container, Inventory inv, Component title) {
        super(container, inv, Component.literal(""));
        this.imageWidth = 176;
        this.imageHeight = 166;
        // Hide default "Inventory" label — we draw our own status text
        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;
    }

    @Override
    protected void init() {
        super.init();
        // Filter button at top-right, above everything
        addRenderableWidget(Button.builder(
            Component.literal(isBlacklist ? "\u00A7c\u9ED1" : "\u00A7a\u767D"),
            b -> {
                isBlacklist = !isBlacklist;
                b.setMessage(Component.literal(isBlacklist ? "\u00A7c\u9ED1" : "\u00A7a\u767D"));
                ModNetwork.CHANNEL.sendToServer(new MarkerConfigPacket(
                    isBlacklist ? "blacklist" : "whitelist", Collections.emptySet()));
            }).bounds(leftPos + imageWidth - 24, topPos + 4, 20, 14).build());
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        int x = leftPos, y = topPos;

        // Light background
        g.fill(x - 5, y - 5, x + imageWidth + 5, y + imageHeight + 5, 0xFFC6C6C6);
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFFE0E0E0);

        // Title row: left = text, right = filter button label
        g.drawString(font, "\u00A70\u00A7lStorage Marker", x + 8, y + 5, 0);
        g.drawString(font, "\u00A77\u7B5B\u9009", x + imageWidth - 46, y + 6, 0x555555);

        // Separator
        g.fill(x + 7, y + 15, x + imageWidth - 7, y + 16, 0xFF999999);

        // Filter grid 3x3 at (62, 17) — matches container
        drawGrid(g, x + 62, y + 17, 3, 3);

        // Binding status (between filter grid end y+71 and player inv start y+84)
        var tag = menu.filterHandler.boundItem.getTag();
        if (tag != null && tag.contains("BoundPos")) {
            String bn = tag.getString("BoundBlock");
            g.drawString(font, "\u00A72\u2713 " + bn, x + 8, y + 75, 0x006600);
        } else {
            g.drawString(font, "\u00A78\u672A\u7ED1\u5B9A", x + 8, y + 75, 0x888888);
        }

        // Player inventory 9x3 at (8, 84)
        drawGrid(g, x + 8, y + 84, 9, 3);

        // Hotbar 9x1 at (8, 142)
        drawGrid(g, x + 8, y + 142, 9, 1);
    }

    private void drawGrid(GuiGraphics g, int ox, int oy, int cols, int rows) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int sx = ox + c * 18;
                int sy = oy + r * 18;
                g.fill(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B);
                g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFFC6C6C6);
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.render(g, mx, my, pt);
    }
}
