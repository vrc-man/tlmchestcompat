package com.example.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MaidDataPacket {
    public String uuid, name, ownerName;
    public float health, maxHealth, baseHp;
    public float armor, baseArmor;
    public float toughness, baseToughness;
    public float damage, baseDamage;
    public int explosion, thorns, tough, crit, dig, resist, eatHP;
    public double reach, speed, regenPct;

    public MaidDataPacket() {}

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(uuid);
        buf.writeUtf(name);
        buf.writeUtf(ownerName);
        buf.writeFloat(health);
        buf.writeFloat(maxHealth);
        buf.writeFloat(baseHp);
        buf.writeFloat(armor);
        buf.writeFloat(baseArmor);
        buf.writeFloat(toughness);
        buf.writeFloat(baseToughness);
        buf.writeFloat(damage);
        buf.writeFloat(baseDamage);
        buf.writeInt(explosion);
        buf.writeInt(thorns);
        buf.writeInt(tough);
        buf.writeInt(crit);
        buf.writeInt(dig);
        buf.writeInt(resist);
        buf.writeInt(eatHP);
        buf.writeDouble(reach);
        buf.writeDouble(speed);
        buf.writeDouble(regenPct);
    }

    public static MaidDataPacket decode(FriendlyByteBuf buf) {
        MaidDataPacket d = new MaidDataPacket();
        d.uuid = buf.readUtf();
        d.name = buf.readUtf();
        d.ownerName = buf.readUtf();
        d.health = buf.readFloat();
        d.maxHealth = buf.readFloat();
        d.baseHp = buf.readFloat();
        d.armor = buf.readFloat();
        d.baseArmor = buf.readFloat();
        d.toughness = buf.readFloat();
        d.baseToughness = buf.readFloat();
        d.damage = buf.readFloat();
        d.baseDamage = buf.readFloat();
        d.explosion = buf.readInt();
        d.thorns = buf.readInt();
        d.tough = buf.readInt();
        d.crit = buf.readInt();
        d.dig = buf.readInt();
        d.resist = buf.readInt();
        d.eatHP = buf.readInt();
        d.reach = buf.readDouble();
        d.speed = buf.readDouble();
        d.regenPct = buf.readDouble();
        return d;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            com.example.client.ClientProxy.onMaidData(this)));
        ctx.get().setPacketHandled(true);
    }
}
