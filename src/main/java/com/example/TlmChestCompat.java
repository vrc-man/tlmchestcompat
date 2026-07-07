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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.items.ItemStackHandler;
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
        MarkerMenuType.register(bus);
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
    public void onLivingHurt(LivingHurtEvent event) {
        var entity = event.getEntity();

        // Maid immunity via Kill-Proof Charm
        if (entity instanceof com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid maid) {
            var bauble = maid.getMaidBauble();
            for (int i = 0; i < bauble.getSlots(); i++) {
                if (bauble.getStackInSlot(i).getItem() == ModItems.TRUE_IMMORTAL_BAUBLE.get()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // Player immunity via Player Immortal Bauble
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            if (hasPlayerBauble(player)) {
                event.setCanceled(true);
            }
        }
    }

    private boolean hasPlayerBauble(net.minecraft.world.entity.player.Player player) {
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
                                var s = (net.minecraft.world.item.ItemStack) getStk.invoke(stacks, i);
                                if (s.getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get()) return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        if (player.getMainHandItem().getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get()) return true;
        if (player.getOffhandItem().getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get()) return true;
        return false;
    }

    @SubscribeEvent
    public void onMaidRequestItem(com.github.tartaricacid.touhoulittlemaid.api.event.MaidRequestItemEvent event) {
        // When TLM AI needs an item (seed, arrow, etc.) and can't find it,
        // check if it's in the Sophisticated Backpack and pull it out
        var maid = event.getMaid();
        if (maid.level().isClientSide) return;

        // Check if any equipped bauble uses backpack storage
        var bauble = maid.getMaidBauble();
        boolean hasBackpackBauble = false;
        for (int i = 0; i < bauble.getSlots(); i++) {
            var item = bauble.getStackInSlot(i).getItem();
            if (item == ModItems.BACKPACK_BAUBLE.get() || item == ModItems.STORAGE_MARKER.get()) {
                hasBackpackBauble = true;
                break;
            }
        }
        if (!hasBackpackBauble) return;

        // Try to find the item in the curios back slot backpack
        try {
            var curiosOpt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(maid).resolve();
            if (curiosOpt.isPresent()) {
                var curios = curiosOpt.get().getClass().getMethod("getCurios").invoke(curiosOpt.get());
                if (curios instanceof java.util.Map) {
                    var val = ((java.util.Map<?, ?>) curios).get("back");
                    if (val != null) {
                        var stacks = val.getClass().getMethod("getStacks").invoke(val);
                        int slots = (int) stacks.getClass().getMethod("getSlots").invoke(stacks);
                        var getStk = stacks.getClass().getMethod("getStackInSlot", int.class);
                        for (int i = 0; i < slots; i++) {
                            var bpStack = (net.minecraft.world.item.ItemStack) getStk.invoke(stacks, i);
                            if (bpStack.isEmpty()) continue;
                            var cap = bpStack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER);
                            var resolved = cap.resolve();
                            if (resolved.isEmpty()) continue;

                            var filter = event.getItemFilter();
                            var handler = resolved.get();
                            for (int j = 0; j < handler.getSlots(); j++) {
                                var stack = handler.getStackInSlot(j);
                                if (stack.isEmpty() || !filter.test(stack)) continue;

                                var taken = handler.extractItem(j, 1, false);
                                if (taken.isEmpty()) continue;

                                var maidInv = maid.getMaidInv();
                                for (int k = 0; k < maidInv.getSlots(); k++) {
                                    if (maidInv.getStackInSlot(k).isEmpty()) {
                                        maidInv.setStackInSlot(k, taken);
                                        event.setRequestedItem(taken);
                                        return;
                                    }
                                }
                                // No empty slot, put it back
                                handler.insertItem(j, taken, false);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
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

        if (player.isCrouching() && player.isCreative()) {
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

            // Apply to actual entity attributes (same logic as KubeJS applyAttrs)
            applyAttrs(maid, d);

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
        pkt.baseHp = (float) attr.getBaseValue(Attributes.MAX_HEALTH);
        pkt.armor = (float) attr.getValue(Attributes.ARMOR);
        pkt.baseArmor = (float) attr.getBaseValue(Attributes.ARMOR);
        pkt.toughness = (float) attr.getValue(Attributes.ARMOR_TOUGHNESS);
        pkt.baseToughness = (float) attr.getBaseValue(Attributes.ARMOR_TOUGHNESS);
        pkt.damage = (float) attr.getValue(Attributes.ATTACK_DAMAGE);
        pkt.baseDamage = (float) attr.getBaseValue(Attributes.ATTACK_DAMAGE);
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

    public static void applyAttrs(LivingEntity maid, net.minecraft.nbt.CompoundTag d) {
        var OpADD = net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION;

        java.util.function.BiConsumer<net.minecraft.world.entity.ai.attributes.Attribute, Double> applyA = (attr, v) -> {
            if (attr == null || v <= 0) return;
            var inst = maid.getAttribute(attr);
            if (inst == null) return;
            // Collect UUIDs first to avoid ConcurrentModificationException
            var toRemove = new java.util.ArrayList<java.util.UUID>();
            for (var m : inst.getModifiers()) {
                if (m.getName().equals("kjs_hp") || m.getName().equals("kjs_armor") ||
                    m.getName().equals("kjs_tough") || m.getName().equals("kjs_dmg") ||
                    m.getName().equals("kjs_spd") || m.getName().equals("kjs_resist") ||
                    m.getName().equals("kjs_reach") || m.getName().equals("kjs_dig")) {
                    toRemove.add(m.getId());
                }
            }
            for (var id : toRemove) inst.removeModifier(id);
            inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                java.util.UUID.randomUUID(), "kjs_" + attr.getDescriptionId(), v, OpADD));
        };

        applyA.accept(Attributes.MAX_HEALTH, (double) Math.min(d.getInt("eatHP"), 900));
        if (d.getInt("eatHP") <= 900) maid.setHealth(maid.getMaxHealth());
        applyA.accept(Attributes.ARMOR, (double) d.getInt("attrArmor"));
        applyA.accept(Attributes.ARMOR_TOUGHNESS, (double) d.getInt("attrTough"));
        applyA.accept(Attributes.ATTACK_DAMAGE, (double) d.getInt("attrDmg"));
        applyA.accept(Attributes.ATTACK_SPEED, (double) d.getDouble("attrSpeed"));

        var resist = d.getInt("attrResist");
        if (resist > 0) {
            var inst = maid.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (inst != null) {
                var toRemove = new java.util.ArrayList<java.util.UUID>();
                for (var m : inst.getModifiers()) {
                    if (m.getName().equals("kjs_resist")) toRemove.add(m.getId());
                }
                for (var id : toRemove) inst.removeModifier(id);
                inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    java.util.UUID.randomUUID(), "kjs_resist", resist * 0.1, OpADD));
            }
        }

        try {
            var FM = Class.forName("net.minecraftforge.common.ForgeMod");
            var reachA = (net.minecraft.world.entity.ai.attributes.Attribute) FM.getField("ENTITY_REACH").get(null);
            var blockA = (net.minecraft.world.entity.ai.attributes.Attribute) FM.getField("BLOCK_REACH").get(null);

            double reach = d.getDouble("attrReach");
            if (reach > 0 && reachA != null) {
                var inst = maid.getAttribute(reachA);
                if (inst != null) {
                    var toRemove = new java.util.ArrayList<java.util.UUID>();
                    for (var m : inst.getModifiers()) {
                        if (m.getName().equals("kjs_reach")) toRemove.add(m.getId());
                    }
                    for (var id : toRemove) inst.removeModifier(id);
                    inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                        java.util.UUID.randomUUID(), "kjs_reach", reach, OpADD));
                }
            }
            double dig = d.getDouble("attrDig");
            if (dig > 0 && blockA != null) {
                var inst = maid.getAttribute(blockA);
                if (inst != null) {
                    var toRemove = new java.util.ArrayList<java.util.UUID>();
                    for (var m : inst.getModifiers()) {
                        if (m.getName().equals("kjs_dig")) toRemove.add(m.getId());
                    }
                    for (var id : toRemove) inst.removeModifier(id);
                    inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                        java.util.UUID.randomUUID(), "kjs_dig", dig, OpADD));
                }
            }
        } catch (Exception ignored) {}
    }
}
