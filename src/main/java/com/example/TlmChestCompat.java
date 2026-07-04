package com.example;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tlmchestcompat")
public class TlmChestCompat {
    public TlmChestCompat() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(bus);
        ModCreativeTab.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
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

        var d = maid.getPersistentData();

        if (player.isCrouching()) {
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
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a[Boost] +9000 all!"));
        } else {
            InfoScannerItem.scanMaid((net.minecraft.server.level.ServerPlayer) player, target);
        }
        event.setCanceled(true);
    }
}
