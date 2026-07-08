package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerImmortalItem extends BaubleItem {
    public PlayerImmortalItem() {
        super(
            "\u00A77\u73A9\u5BB6\u4E13\u7528\uFF0C\u514D\u75AB\u4E00\u5207\u4F24\u5BB3\u5305\u62EC/kill",
            "\u00A77\u6301\u7EED\u56DE\u8840\u00B7\u9971\u98DF\u00B7\u5438\u6536\u00B7\u8010\u706B\u00B7\u6C34\u559D",
            "\u00A77\u514D\u75AB\u6240\u6709\u8D1F\u9762\u6548\u679C",
            "\u00A7e\u53F3\u952E\u7A7A\u6C14\u6253\u5F00\u98DE\u884C/\u7F13\u964D\u8BBE\u7F6E");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!level.isClientSide) return InteractionResultHolder.success(stack);
        openScreen(stack);
        return InteractionResultHolder.success(stack);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen(ItemStack stack) {
        var tag = stack.getOrCreateTag();
        net.minecraft.client.Minecraft.getInstance().setScreen(
            new com.example.client.PlayerImmortalScreen(
                tag.contains("flightSpeed") ? tag.getDouble("flightSpeed") : 1.0,
                tag.getBoolean("flightEnabled"),
                tag.getBoolean("slowFalling"),
                tag.getBoolean("nightVision"),
                !tag.contains("lightning") || tag.getBoolean("lightning"),
                tag.contains("reflectMult") ? tag.getDouble("reflectMult") : 0,
                tag.getBoolean("waterWalk"),
                tag.getBoolean("lavaWalk"),
                tag.contains("groundSpeed") ? tag.getDouble("groundSpeed") : 1.0));
    }
}
