package com.example.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerImmortalConfigPacket {
    public final double flightSpeed;
    public final boolean flightEnabled;
    public final boolean slowFalling;
    public final boolean nightVision;
    public final boolean lightning;
    public final double reflectMult;

    public PlayerImmortalConfigPacket(double flightSpeed, boolean flightEnabled, boolean slowFalling, boolean nightVision, boolean lightning, double reflectMult) {
        this.flightSpeed = flightSpeed;
        this.flightEnabled = flightEnabled;
        this.slowFalling = slowFalling;
        this.nightVision = nightVision;
        this.lightning = lightning;
        this.reflectMult = reflectMult;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(flightSpeed);
        buf.writeBoolean(flightEnabled);
        buf.writeBoolean(slowFalling);
        buf.writeBoolean(nightVision);
        buf.writeBoolean(lightning);
        buf.writeDouble(reflectMult);
    }

    public static PlayerImmortalConfigPacket decode(FriendlyByteBuf buf) {
        return new PlayerImmortalConfigPacket(
            buf.readDouble(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readDouble());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;

            // Try mainhand / offhand first
            var stack = player.getMainHandItem();
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.PlayerImmortalItem)) {
                stack = player.getOffhandItem();
                if (stack.isEmpty() || !(stack.getItem() instanceof com.example.PlayerImmortalItem)) {
                    // Try Curios slots
                    stack = findInCurios(player);
                }
            }
            if (stack.isEmpty() || !(stack.getItem() instanceof com.example.PlayerImmortalItem)) return;

            var tag = stack.getOrCreateTag();
            tag.putDouble("flightSpeed", flightSpeed);
            tag.putBoolean("flightEnabled", flightEnabled);
            tag.putBoolean("slowFalling", slowFalling);
            tag.putBoolean("nightVision", nightVision);
            tag.putBoolean("lightning", lightning);
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
                                if (s.getItem() instanceof com.example.PlayerImmortalItem) return s;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return ItemStack.EMPTY;
    }
}
