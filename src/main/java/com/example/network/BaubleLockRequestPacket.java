package com.example.network;

import com.example.ModItems;
import com.example.ModNetwork;
import com.example.SlotLockHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class BaubleLockRequestPacket {
    private final String maidUuid;
    private final int baubleType; // 0=backpack, 1=storage_marker

    public BaubleLockRequestPacket(String maidUuid, int baubleType) {
        this.maidUuid = maidUuid;
        this.baubleType = baubleType;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(maidUuid);
        buf.writeInt(baubleType);
    }

    public static BaubleLockRequestPacket decode(FriendlyByteBuf buf) {
        return new BaubleLockRequestPacket(buf.readUtf(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;
            try {
                var uuid = UUID.fromString(maidUuid);
                var server = player.getServer();
                if (server != null) {
                    for (var level : server.getAllLevels()) {
                        for (var e : level.getEntities().getAll()) {
                            if (e.getUUID().equals(uuid) && e instanceof com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid maid) {
                                if (!player.isCreative() && !player.hasPermissions(2) && maid.getOwner() != player) return;
                                var handler = maid.getMaidBauble();
                                for (int i = 0; i < handler.getSlots(); i++) {
                                    var stack = handler.getStackInSlot(i);
                                    if (stack.isEmpty()) continue;
                                    var item = stack.getItem();
                                    if (baubleType == 0 && item == ModItems.BACKPACK_BAUBLE.get()
                                        || baubleType == 1 && item == ModItems.STORAGE_MARKER.get()) {
                                        var locks = SlotLockHelper.getLocks(stack);
                                        ModNetwork.CHANNEL.sendTo(
                                            new SlotLockResponsePacket(maidUuid, i, locks),
                                            ((ServerPlayer) player).connection.connection,
                                            NetworkDirection.PLAY_TO_CLIENT);
                                        break;
                                    }
                                }
                                return;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        });
        ctx.get().setPacketHandled(true);
    }
}
