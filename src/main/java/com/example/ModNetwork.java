package com.example;

import com.example.network.BaubleLockRequestPacket;
import com.example.network.MaidDataPacket;
import com.example.network.MarkerConfigPacket;
import com.example.network.PlayerImmortalConfigPacket;
import com.example.network.SlotLockPacket;
import com.example.network.SlotLockResponsePacket;
import com.example.network.TpsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("removal")
public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation("tlmchestcompat:main"),
        () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, MaidDataPacket.class,
            MaidDataPacket::encode, MaidDataPacket::decode, MaidDataPacket::handle);
        CHANNEL.registerMessage(id++, TpsPacket.class,
            TpsPacket::encode, TpsPacket::decode, TpsPacket::handle);
        CHANNEL.registerMessage(id++, MarkerConfigPacket.class,
            MarkerConfigPacket::encode, MarkerConfigPacket::decode, MarkerConfigPacket::handle);
        CHANNEL.registerMessage(id++, SlotLockPacket.class,
            SlotLockPacket::encode, SlotLockPacket::decode, SlotLockPacket::handle);
        CHANNEL.registerMessage(id++, PlayerImmortalConfigPacket.class,
            PlayerImmortalConfigPacket::encode, PlayerImmortalConfigPacket::decode, PlayerImmortalConfigPacket::handle);
        CHANNEL.registerMessage(id++, com.example.network.MaidReflectPacket.class,
            com.example.network.MaidReflectPacket::encode, com.example.network.MaidReflectPacket::decode, com.example.network.MaidReflectPacket::handle);
        CHANNEL.registerMessage(id++, BaubleLockRequestPacket.class,
            BaubleLockRequestPacket::encode, BaubleLockRequestPacket::decode, BaubleLockRequestPacket::handle);
        CHANNEL.registerMessage(id++, SlotLockResponsePacket.class,
            SlotLockResponsePacket::encode, SlotLockResponsePacket::decode, SlotLockResponsePacket::handle);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
