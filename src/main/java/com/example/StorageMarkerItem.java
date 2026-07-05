package com.example;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class StorageMarkerItem extends Item {
    public StorageMarkerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        var player = ctx.getPlayer();
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var stack = ctx.getItemInHand();

        if (level.isClientSide) return InteractionResult.SUCCESS;

        // Check if the block has inventory capability
        var be = level.getBlockEntity(pos);
        if (be == null) {
            player.sendSystemMessage(Component.literal("\u00A7c\u6B64\u65B9\u5757\u4E0D\u652F\u6301\u5B58\u50A8"));
            return InteractionResult.FAIL;
        }

        var cap = be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (cap.isEmpty()) {
            player.sendSystemMessage(Component.literal("\u00A7c\u6B64\u65B9\u5757\u4E0D\u652F\u6301\u5B58\u50A8"));
            return InteractionResult.FAIL;
        }

        // Sneak + right-click to clear binding
        if (player.isCrouching()) {
            var tag = stack.getTag();
            if (tag != null) {
                tag.remove("BoundPos");
                tag.remove("BoundDim");
                tag.remove("BoundBlock");
            }
            player.sendSystemMessage(Component.literal("\u00A77[Storage Marker] \u7ED1\u5B9A\u5DF2\u6E05\u9664"));
            return InteractionResult.SUCCESS;
        }

        // Save binding
        var tag = stack.getOrCreateTag();
        StorageMarkerBauble.writeBoundPos(tag, pos);
        tag.putString("BoundDim", level.dimension().location().toString());
        tag.putString("BoundBlock", be.getBlockState().getBlock().getName().getString());

        var blockName = be.getBlockState().getBlock().getName().getString();
        player.sendSystemMessage(Component.literal(
            "\u00A7a[Storage Marker] \u5DF2\u7ED1\u5B9A: \u00A7f" + blockName
            + " \u00A77@ " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
        player.sendSystemMessage(Component.literal(
            "\u00A77\u6CE8: \u5973\u4EC6\u4F1A\u4F18\u5148\u4F7F\u7528\u80CC\u5305\uFF0C\u7136\u540E\u624D\u7528\u6B64\u5B58\u50A8\u65B9\u5757"));

        return InteractionResult.SUCCESS;
    }
}
