package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BackpackBaubleItem extends BaubleItem {
    public BackpackBaubleItem() {
        super(
            "\u00A77\u81EA\u52A8\u5B58\u53D6\u5973\u4EC6\u80CC\u5305\u7269\u54C1",
            "\u00A77\u914D\u5408\u7CBE\u5236\u80CC\u5305\u4F7F\u7528",
            "\u00A77\u53F3\u952E\u7A7A\u6C14\u6253\u5F00\u69FD\u4F4D\u9501\u5B9A");
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
        net.minecraft.client.Minecraft.getInstance().setScreen(
            new com.example.client.SlotLockScreen(stack, false));
    }
}
