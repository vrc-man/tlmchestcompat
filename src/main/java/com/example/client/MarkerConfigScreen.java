package com.example.client;

import com.example.StorageMarkerBauble;
import com.example.network.MarkerConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class MarkerConfigScreen extends Screen {
    private String filterType = "blacklist";
    private Set<String> filterItems = new HashSet<>();
    private List<ResourceLocation> allItems = new ArrayList<>();
    private int scrollOffset = 0;
    private String statusText = "";

    public MarkerConfigScreen(CompoundTag tag) {
        super(Component.literal(""));
        if (tag != null) {
            if (tag.contains("FilterType")) filterType = tag.getString("FilterType");
            filterItems = StorageMarkerBauble.readFilter(tag);
        }
        if (tag != null && tag.contains("BoundPos")) {
            var p = StorageMarkerBauble.readBoundPos(tag);
            String bn = tag.getString("BoundBlock");
            statusText = "\u00A7a\u2713 " + bn + " @ " + p.getX() + "," + p.getY() + "," + p.getZ();
        } else {
            statusText = "\u00A78\u672A\u7ED1\u5B9A";
        }

        // Collect all item IDs for the filter grid
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            allItems.add(entry.getKey().location());
        }
    }

    @Override
    protected void init() {
        int cx = width / 2;

        // Status
        addRenderableWidget(Button.builder(Component.literal(statusText), b -> {})
            .bounds(cx - 80, 10, 160, 16).build());

        // Filter type toggle
        addRenderableWidget(Button.builder(
            Component.literal("\u00A77\u8FC7\u6EE4: " + (filterType.equals("whitelist") ? "\u00A7a\u767D\u540D\u5355" : "\u00A74\u9ED1\u540D\u5355")),
            b -> {
                filterType = filterType.equals("whitelist") ? "blacklist" : "whitelist";
                b.setMessage(Component.literal("\u00A77\u8FC7\u6EE4: " + (filterType.equals("whitelist") ? "\u00A7a\u767D\u540D\u5355" : "\u00A74\u9ED1\u540D\u5355")));
            }).bounds(cx - 80, 34, 160, 18).build());

        // Add current held item to filter
        addRenderableWidget(Button.builder(
            Component.literal("\u00A77[\u6DFB\u52A0\u624B\u6301\u7269\u54C1]"),
            b -> {
                var mc = Minecraft.getInstance();
                if (mc.player != null) {
                    var held = mc.player.getMainHandItem();
                    if (!held.isEmpty() && held.getItem() != com.example.ModItems.STORAGE_MARKER.get()) {
                        var id = ForgeRegistries.ITEMS.getKey(held.getItem());
                        if (id != null) filterItems.add(id.toString());
                    }
                }
            }).bounds(cx - 80, 78, 160, 16).build());

        // Save button
        addRenderableWidget(Button.builder(Component.literal("\u00A7a[\u4FDD\u5B58]"), b -> {
            var pkt = new com.example.network.MarkerConfigPacket(filterType, filterItems);
            com.example.ModNetwork.CHANNEL.sendToServer(pkt);
            onClose();
        }).bounds(cx - 40, height - 30, 80, 18).build());

        // Remove last filter item
        addRenderableWidget(Button.builder(Component.literal("\u00A7c[\u5220\u9664\u6700\u540E\u4E00\u4E2A]"), b -> {
            if (!filterItems.isEmpty()) {
                var last = filterItems.stream().reduce((a, b2) -> b2).orElse(null);
                if (last != null) filterItems.remove(last);
            }
        }).bounds(cx - 80, 98, 160, 16).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);

        int cx = width / 2;
        g.drawString(font, "\u00A7b\u00A7l\u2726 Storage Marker \u8BBE\u7F6E", cx - font.width("\u00A7b\u00A7l\u2726 Storage Marker \u8BBE\u7F6E") / 2, 120, 0xFFFFFF);

        // Filter items display
        int y = 140;
        g.drawString(font, "\u00A77\u8FC7\u6EE4\u5217\u8868 (" + filterItems.size() + "):", cx - 80, y, 0x888888);
        y += 12;

        int col = 0;
        for (var id : filterItems) {
            int x = cx - 80 + col * 100;
            g.drawString(font, "\u00A7f" + (id.length() > 18 ? id.substring(0, 17) + ".." : id), x, y, 0xAAAAAA);
            col++;
            if (col >= 3) { col = 0; y += 10; }
        }

        // Current held item preview
        var mc = Minecraft.getInstance();
        if (mc.player != null) {
            var held = mc.player.getMainHandItem();
            if (!held.isEmpty() && held.getItem() != com.example.ModItems.STORAGE_MARKER.get()) {
                g.drawString(font, "\u00A77\u624B\u6301: " + held.getHoverName().getString(), cx - 80, height - 60, 0xFFFFFF);
            }
        }
    }
}
