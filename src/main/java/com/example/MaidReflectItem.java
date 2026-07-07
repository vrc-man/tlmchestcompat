package com.example;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MaidReflectItem extends BaubleItem {
    public MaidReflectItem() {
        super(
            "\u00A77\u5973\u4EC6\u4E13\u7528\uFF0C\u5C06\u6240\u53D7\u4F24\u5BB3\u6307\u5B9A\u500D\u7387\u53CD\u5C04\u7ED9\u653B\u51FB\u8005",
            "\u00A77\u514D\u75AB\u4E00\u5207\u4F24\u5BB3\u548C\u8D1F\u9762\u6548\u679C\uFF0C\u62B5\u5FA1\u51FB\u9000",
            "\u00A7e\u53F3\u952E\u7A7A\u6C14\u6253\u5F00\u53CD\u4F24\u500D\u7387\u8BBE\u7F6E");
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
            new com.example.client.MaidReflectScreen(
                tag.contains("reflectMult") ? tag.getDouble("reflectMult") : 1.0));
    }
}
