package com.example;

import com.example.network.MaidDataPacket;
import com.example.network.TpsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("removal")
@Mod("tlmchestcompat")
public class TlmChestCompat {
    private int tpsTick = 0;

    public TlmChestCompat() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(bus);
        ModCreativeTab.register(bus);
        ModNetwork.register();
        MinecraftForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            com.example.client.ClientInit.init();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tpsTick++;
        if (tpsTick < 100) return;
        tpsTick = 0;

        MinecraftServer server = event.getServer();
        double mspt = server.getAverageTickTime();
        float tps = (float) Math.min(1000.0 / Math.max(mspt, 1.0), 20.0);

        TpsPacket pkt = new TpsPacket(tps);
        for (var player : server.getPlayerList().getPlayers()) {
            ModNetwork.sendToPlayer(player, pkt);
        }
    }

    @SubscribeEvent
    public void onInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        var player = event.getEntity();
        var stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != ModItems.INFO_SCANNER.get()) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;
        if (!(target instanceof TamableAnimal maid)) return;
        if (!player.isCreative() && maid.getOwner() != player) return;

        if (player.isCrouching()) {
            var d = maid.getPersistentData();
            d.putInt("eatHP", d.getInt("eatHP") + 9000);
            d.putInt("attrExplosion", d.getInt("attrExplosion") + 9000);
            d.putInt("attrThorns", d.getInt("attrThorns") + 9000);
            d.putInt("attrTough", d.getInt("attrTough") + 9000);
            d.putInt("attrCrit", d.getInt("attrCrit") + 9000);
            d.putInt("attrDig", d.getInt("attrDig") + 9000);
            d.putInt("attrResist", d.getInt("attrResist") + 9000);
            d.putInt("attrArmor", d.getInt("attrArmor") + 18000);
            d.putInt("attrDmg", d.getInt("attrDmg") + 4500);
            d.putDouble("attrReach", d.getDouble("attrReach") + 4500);
            d.putDouble("attrSpeed", d.getDouble("attrSpeed") + 2700);
            d.putDouble("attrRegenPct", d.getDouble("attrRegenPct") + 450);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("\u00A7a[Boost] All +9000!"));
        } else {
            sendMaidData((ServerPlayer) player, maid);
        }
        event.setCanceled(true);
    }

    private void sendMaidData(ServerPlayer player, TamableAnimal maid) {
        var d = maid.getPersistentData();
        var attr = maid.getAttributes();

        MaidDataPacket pkt = new MaidDataPacket();
        pkt.uuid = maid.getUUID().toString();
        pkt.name = maid.getName().getString();
        pkt.ownerName = maid.getOwner() instanceof ServerPlayer o ? o.getName().getString() : "none";
        pkt.health = maid.getHealth();
        pkt.maxHealth = maid.getMaxHealth();
        pkt.baseHp = (float) attr.getValue(Attributes.MAX_HEALTH);
        pkt.armor = maid.getArmorValue();
        pkt.baseArmor = (float) attr.getValue(Attributes.ARMOR);
        pkt.toughness = (float) attr.getValue(Attributes.ARMOR_TOUGHNESS);
        pkt.baseToughness = (float) attr.getValue(Attributes.ARMOR_TOUGHNESS);
        pkt.damage = (float) attr.getValue(Attributes.ATTACK_DAMAGE);
        pkt.baseDamage = (float) attr.getValue(Attributes.ATTACK_DAMAGE);
        pkt.explosion = d.getInt("attrExplosion");
        pkt.thorns = d.getInt("attrThorns");
        pkt.tough = d.getInt("attrTough");
        pkt.crit = d.getInt("attrCrit");
        pkt.dig = d.getInt("attrDig");
        pkt.resist = d.getInt("attrResist");
        pkt.eatHP = d.getInt("eatHP");
        pkt.reach = d.getDouble("attrReach");
        pkt.speed = d.getDouble("attrSpeed");
        pkt.regenPct = d.getDouble("attrRegenPct");

        ModNetwork.sendToPlayer(player, pkt);
    }
}
