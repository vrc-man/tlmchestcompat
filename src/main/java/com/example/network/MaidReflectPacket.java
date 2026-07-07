package com.example.network;

import com.example.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MaidReflectPacket {
    public final double reflectMult;

    public MaidReflectPacket(double reflectMult) {
        this.reflectMult = reflectMult;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(reflectMult);
    }

    public static MaidReflectPacket decode(FriendlyByteBuf buf) {
        return new MaidReflectPacket(buf.readDouble());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;

            var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.MaidReflectItem)) {
                stack = player.getOffhandItem();
                if (stack.isEmpty() || !(stack.getItem() instanceof com.example.MaidReflectItem)) {
                    stack = findInCurios(player);
                }
            }
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.MaidReflectItem)) return;

            var tag = stack.getOrCreateTag();
            tag.putDouble("reflectMult", reflectMult);
        });
        ctx.get().setPacketHandled(true);
    }

    private ItemStack findInCurios(net.minecraft.world.entity.player.Player player) {
        try {
            var opt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).resolve();
            if (opt.isPresent()) {
                var curios = opt.get().getClass().getMethod("getCurios").invoke(opt.get());
                if (curios instanceof java.util.Map) {
                    for (var val : ((java.util.Map<?, ?>) curios).values()) {
                        if (val != null) {
                            var stacks = val.getClass().getMethod("getStacks").invoke(val);
                            int slots = (int) stacks.getClass().getMethod("getSlots").invoke(stacks);
                            var getStk = stacks.getClass().getMethod("getStackInSlot", int.class);
                            for (int i = 0; i < slots; i++) {
                                var s = (ItemStack) getStk.invoke(stacks, i);
                                if (s.getItem() instanceof com.example.MaidReflectItem) return s;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return ItemStack.EMPTY;
    }
}
