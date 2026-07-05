package com.example;

import com.example.network.MaidDataPacket;
import com.example.network.MarkerConfigPacket;
import com.example.network.SlotLockPacket;
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
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
