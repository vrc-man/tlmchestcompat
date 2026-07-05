package com.example.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MarkerConfigPacket {
    public String filterType;
    public Set<String> filterItems;

    public MarkerConfigPacket() {}

    public MarkerConfigPacket(String filterType, Set<String> filterItems) {
        this.filterType = filterType;
        this.filterItems = filterItems;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(filterType);
        buf.writeVarInt(filterItems.size());
        for (var id : filterItems) {
            buf.writeUtf(id);
        }
    }

    public static MarkerConfigPacket decode(FriendlyByteBuf buf) {
        var pkt = new MarkerConfigPacket();
        pkt.filterType = buf.readUtf();
        int size = buf.readVarInt();
        pkt.filterItems = new HashSet<>();
        for (int i = 0; i < size; i++) {
            pkt.filterItems.add(buf.readUtf());
        }
        return pkt;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.StorageMarkerItem)) {
                stack = player.getOffhandItem();
                if (stack.isEmpty() || !(stack.getItem() instanceof com.example.StorageMarkerItem)) return;
            }

            var tag = stack.getOrCreateTag();
            tag.putString("FilterType", filterType);

            var list = new net.minecraft.nbt.ListTag();
            for (var id : filterItems) {
                list.add(net.minecraft.nbt.StringTag.valueOf(id));
            }
            tag.put("FilterItems", list);
        });
        ctx.get().setPacketHandled(true);
    }
}
