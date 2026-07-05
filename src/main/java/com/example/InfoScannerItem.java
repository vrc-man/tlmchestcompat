package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InfoScannerItem extends Item {
    public InfoScannerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player.isCrouching()) {
            player.sendSystemMessage(Component.literal("§7[HUD] Toggle not yet implemented"));
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    public static void scanMaid(ServerPlayer player, LivingEntity target) {
        if (!(target instanceof TamableAnimal maid)) return;
        if (!player.isCreative() && maid.getOwner() != player) return;

        var d = maid.getPersistentData();
        var attr = maid.getAttributes();

        var hp = (int) maid.getHealth();
        var maxHp = (int) maid.getMaxHealth();
        var baseHp = (int) attr.getBaseValue(Attributes.MAX_HEALTH);
        var hpMult = baseHp > 0 ? String.format("%.2f", (double) maxHp / baseHp) : "0";

        var armor = (int) attr.getValue(Attributes.ARMOR);
        var baseArmor = (int) attr.getBaseValue(Attributes.ARMOR);
        var armorMult = baseArmor > 0 ? String.format("%.2f", (double) armor / baseArmor) : "0";

        var dmg = (float) attr.getValue(Attributes.ATTACK_DAMAGE);
        var baseDmg = (float) attr.getBaseValue(Attributes.ATTACK_DAMAGE);
        var dmgMult = baseDmg > 0 ? String.format("%.2f", dmg / baseDmg) : "0";

        var tough = (int) attr.getValue(Attributes.ARMOR_TOUGHNESS);
        var baseTough = (int) attr.getBaseValue(Attributes.ARMOR_TOUGHNESS);
        var toughMult = baseTough > 0 ? String.format("%.2f", (double) tough / baseTough) : "0";

        var ownerName = maid.getOwner() instanceof ServerPlayer o ? o.getName().getString() : "none";

        player.sendSystemMessage(Component.literal("§8=============================="));
        player.sendSystemMessage(Component.literal("     §b§l✦ Maid Info Panel"));
        player.sendSystemMessage(Component.literal("§8=============================="));
        var uuidText = "§7UUID: §f§n" + maid.getUUID();
        player.sendSystemMessage(Component.literal(uuidText)
            .withStyle(s -> s.withClickEvent(
                new net.minecraft.network.chat.ClickEvent(
                    net.minecraft.network.chat.ClickEvent.Action.COPY_TO_CLIPBOARD,
                    maid.getUUID().toString()))
                .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                    net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                    Component.literal("§e\u70B9\u51FB\u590D\u5236UUID")))));
        player.sendSystemMessage(Component.literal("§7Name: §f" + maid.getName().getString()));
        player.sendSystemMessage(Component.literal("§7Owner: §f" + ownerName));
        player.sendSystemMessage(Component.literal("§8------------------------------"));
        player.sendSystemMessage(Component.literal("§cHP: §f" + hp + " / " + maxHp + "  §7(base:" + baseHp + " x" + hpMult + ")"));
        player.sendSystemMessage(Component.literal("§bArmor: §f" + armor + "  §7(x" + armorMult + ")"));
        player.sendSystemMessage(Component.literal("§7Toughness: §f" + tough + "  §7(x" + toughMult + ")"));
        player.sendSystemMessage(Component.literal("§cDamage: §f" + Math.round(dmg * 10) / 10.0 + "  §7(x" + dmgMult + ")"));
        player.sendSystemMessage(Component.literal("§8------------------------------"));
        player.sendSystemMessage(Component.literal("§aExplosion: §fLv." + d.getInt("attrExplosion")));
        player.sendSystemMessage(Component.literal("§aThorns: §fLv." + d.getInt("attrThorns")));
        player.sendSystemMessage(Component.literal("§aTough+: §f+" + d.getInt("attrTough")));
        player.sendSystemMessage(Component.literal("§aReach: §f+" + String.format("%.1f", d.getDouble("attrReach"))));
        player.sendSystemMessage(Component.literal("§aSpeed: §f+" + String.format("%.2f", d.getDouble("attrSpeed"))));
        player.sendSystemMessage(Component.literal("§aCrit: §fLv." + d.getInt("attrCrit")));
        player.sendSystemMessage(Component.literal("§aDig: §f+" + d.getInt("attrDig")));
        player.sendSystemMessage(Component.literal("§aRegen: §f+" + Math.round(d.getDouble("attrRegenPct") * 100) + "%"));
        player.sendSystemMessage(Component.literal("§8=============================="));
        player.sendSystemMessage(Component.literal("§7[Sneak+Click: +9000 all stats]"));
    }
}
