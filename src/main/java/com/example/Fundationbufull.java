package com.example;

import com.example.network.TpsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@SuppressWarnings("removal")
@Mod("fundationbufull")
public class Fundationbufull {
    private int tpsTick = 0;
    private net.minecraft.world.entity.LivingEntity lastAttacker;
    private long lastAttackTime;

    public Fundationbufull() {
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
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && hasPlayerBauble(player)) {
            event.setCanceled(true);
            var src = event.getSource();
            lastAttacker = src.getEntity() instanceof net.minecraft.world.entity.LivingEntity le && le != player ? le : null;
            lastAttackTime = player.tickCount;
            var tag = getPlayerBaubleTag(player);
            double reflectMult = (tag != null && tag.contains("reflectMult")) ? tag.getDouble("reflectMult") : 1.0;
            boolean lightning = tag == null || !tag.contains("lightning") || tag.getBoolean("lightning");
            if (reflectMult > 0) {
                var attacker = src.getEntity();
                if (attacker != null && attacker != player) {
                    float dmg = event.getAmount() * (float) reflectMult;
                    attacker.hurt(attacker.damageSources().magic(), dmg * 0.5f);
                    attacker.hurt(attacker.damageSources().explosion(null, attacker), dmg * 0.3f);
                    attacker.setSecondsOnFire((int) Math.round(reflectMult * 2));
                    if (lightning) {
                        var bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(attacker.level());
                        if (bolt != null) {
                            bolt.moveTo(attacker.getX(), attacker.getY(), attacker.getZ());
                            attacker.level().addFreshEntity(bolt);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKnockBack(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof Player player && hasPlayerBauble(player)) {
            event.setStrength(0);
        }
    }

    @SubscribeEvent
    public void onMobEffectAdded(net.minecraftforge.event.entity.living.MobEffectEvent.Added event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasPlayerBauble(player)) return;
        var inst = event.getEffectInstance();
        if (inst == null || inst.getEffect().getCategory() != net.minecraft.world.effect.MobEffectCategory.HARMFUL) return;
        event.setCanceled(true);
        if (lastAttacker != null && !lastAttacker.isRemoved() && lastAttacker != player
            && player.tickCount - lastAttackTime < 100) {
            lastAttacker.addEffect(new net.minecraft.world.effect.MobEffectInstance(inst));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var player = event.player;
        if (player.level().isClientSide) return;

        boolean hasBauble = hasPlayerBauble(player);

        var kbAttr = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            if (hasBauble) {
                kbAttr.setBaseValue(1.0);
            } else if (kbAttr.getBaseValue() >= 1.0) {
                kbAttr.setBaseValue(0.0);
            }
        }

        if (!hasBauble) return;

        var tag = getPlayerBaubleTag(player);
        if (tag == null) return;

        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.SATURATION, 200, 1, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.REGENERATION, 200, 1, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.ABSORPTION, 200, 3, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, 0, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.WATER_BREATHING, 200, 0, false, false));
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 200, 3, false, false));

        if (tag.getBoolean("nightVision")) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        var negativeEffects = new net.minecraft.world.effect.MobEffect[]{
            net.minecraft.world.effect.MobEffects.POISON,
            net.minecraft.world.effect.MobEffects.WITHER,
            net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,
            net.minecraft.world.effect.MobEffects.DIG_SLOWDOWN,
            net.minecraft.world.effect.MobEffects.BLINDNESS,
            net.minecraft.world.effect.MobEffects.HUNGER,
            net.minecraft.world.effect.MobEffects.WEAKNESS,
            net.minecraft.world.effect.MobEffects.LEVITATION,
            net.minecraft.world.effect.MobEffects.UNLUCK,
            net.minecraft.world.effect.MobEffects.BAD_OMEN,
            net.minecraft.world.effect.MobEffects.DARKNESS,
            net.minecraft.world.effect.MobEffects.CONFUSION
        };
        for (var eff : negativeEffects) {
            if (eff != null) player.removeEffect(eff);
        }
        var hostEffects = new String[]{
            "l2hostility:gravity", "l2hostility:moonwalk", "l2hostility:antibuild",
            "l2complements:flame", "l2complements:ice", "l2complements:curse", "l2complements:stone_cage"
        };
        for (var id : hostEffects) {
            var eff = net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(new net.minecraft.resources.ResourceLocation(id));
            if (eff != null) player.removeEffect(eff);
        }

        var abilities = player.getAbilities();
        if (tag.getBoolean("flightEnabled") && !player.isCreative()) {
            if (!abilities.mayfly) {
                abilities.mayfly = true;
                ((ServerPlayer) player).onUpdateAbilities();
            }
            float speed = (float) (0.05 * tag.getDouble("flightSpeed"));
            if (abilities.getFlyingSpeed() != speed) {
                abilities.setFlyingSpeed(speed);
                ((ServerPlayer) player).onUpdateAbilities();
            }
        } else if (!player.isCreative()) {
            if (abilities.mayfly) {
                abilities.mayfly = false;
                abilities.flying = false;
                ((ServerPlayer) player).onUpdateAbilities();
            }
        }

        if (tag.getBoolean("slowFalling") && !player.hasEffect(net.minecraft.world.effect.MobEffects.SLOW_FALLING)) {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SLOW_FALLING, 200, 0, false, false));
        }

        if (tag.getBoolean("waterWalk")) {
            if (player.isInWater()) {
                var motion = player.getDeltaMovement();
                if (motion.y < 0) {
                    player.setDeltaMovement(motion.x, Math.min(motion.y + 0.04, 0.1), motion.z);
                }
                player.setAirSupply(300);
                if (!player.hasEffect(net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE)) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DOLPHINS_GRACE, 200, 0, false, false));
                }
            }
        }

        if (tag.getBoolean("lavaWalk") && player.isInLava()) {
            var motion = player.getDeltaMovement();
            if (motion.y < 0) {
                player.setDeltaMovement(motion.x, Math.min(motion.y + 0.04, 0.1), motion.z);
            }
        }

        var speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double target = tag.contains("groundSpeed") ? tag.getDouble("groundSpeed") : 1.0;
            var toRemove = new java.util.ArrayList<java.util.UUID>();
            for (var m : speedAttr.getModifiers()) {
                if (String.valueOf(m.getName()).equals("immortal_ground_speed")) toRemove.add(m.getId());
            }
            for (var id : toRemove) speedAttr.removeModifier(id);
            if (target != 1.0) {
                speedAttr.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                    java.util.UUID.randomUUID(), "immortal_ground_speed", target - 1.0,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        for (var slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            var stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamageableItem() && stack.getDamageValue() > 0) {
                stack.setDamageValue(0);
            }
        }

        if (player.tickCount % 10 == 0) {
            var inv = player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                var stack = inv.getItem(i);
                if (stack.isEmpty()) continue;
                var st = stack.getTag();
                if (st == null) continue;

                if (st.contains("l2hostility_enchantment", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                    var dis = st.getCompound("l2hostility_enchantment");
                    if (dis.contains("originalEnchantments", net.minecraft.nbt.Tag.TAG_LIST)) {
                        var oldList = dis.getList("originalEnchantments", net.minecraft.nbt.Tag.TAG_COMPOUND);
                        if (!oldList.isEmpty()) {
                            var enchList = st.getList("Enchantments", net.minecraft.nbt.Tag.TAG_COMPOUND);
                            for (var e : oldList) {
                                if (e instanceof net.minecraft.nbt.CompoundTag ct) {
                                    boolean found = false;
                                    for (var existing : enchList) {
                                        if (existing instanceof net.minecraft.nbt.CompoundTag et && et.getString("id").equals(ct.getString("id"))) {
                                            found = true; break;
                                        }
                                    }
                                    if (!found) enchList.add(ct);
                                }
                            }
                            st.put("Enchantments", enchList);
                        }
                    }
                    st.remove("l2hostility_enchantment");
                }

                var itemId = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
                if (itemId != null && itemId.toString().equals("l2hostility:sealed_item")) {
                    var data = st.getCompound("sealedItem");
                    if (!data.isEmpty()) {
                        var restored = net.minecraft.world.item.ItemStack.of(data);
                        if (!restored.isEmpty()) inv.setItem(i, restored);
                    }
                }
            }
        }
    }

    private net.minecraft.nbt.CompoundTag getPlayerBaubleTag(Player player) {
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
                                if (s.getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get() && s.hasTag()) return s.getTag();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        var hand = player.getMainHandItem();
        if (hand.getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get() && hand.hasTag()) return hand.getTag();
        var off = player.getOffhandItem();
        if (off.getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get() && off.hasTag()) return off.getTag();
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            var s = inv.getItem(i);
            if (s.getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get() && s.hasTag()) return s.getTag();
        }
        return null;
    }

    private boolean hasPlayerBauble(Player player) {
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
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() == ModItems.PLAYER_IMMORTAL_BAUBLE.get()) return true;
        }
        return false;
    }
}
