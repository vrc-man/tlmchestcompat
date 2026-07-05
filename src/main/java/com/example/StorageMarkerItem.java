package com.example;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class StorageMarkerItem extends Item {
    public StorageMarkerItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.literal("\u00A77\u6F5C\u884C+\u53F3\u952E\u65B9\u5757\uFF1A\u7ED1\u5B9A/\u89E3\u9664\u7ED1\u5B9A"));
        list.add(Component.literal("\u00A77\u53F3\u952E\u7A7A\u6C14\uFF1A\u6253\u5F00\u8FC7\u6EE4\u8BBE\u7F6E"));
        list.add(Component.literal("\u00A77\u5973\u4EC6\u81EA\u52A8\u53CC\u5411\u5B58\u53D6\uFF0C\u997F\u4E86\u53D6\u98DF\u7269"));

        var tag = stack.getTag();
        if (tag != null && tag.contains("BoundPos")) {
            var pp = StorageMarkerBauble.readBoundPos(tag);
            String bn = tag.getString("BoundBlock");
            list.add(Component.literal("\u00A7a\u2713 \u5DF2\u7ED1\u5B9A: \u00A7f" + bn));
            list.add(Component.literal("\u00A77  @ " + pp.getX() + ", " + pp.getY() + ", " + pp.getZ()));
            String ft = tag.contains("FilterType") ? tag.getString("FilterType") : "blacklist";
            list.add(Component.literal("\u00A77\u8FC7\u6EE4: " + (ft.equals("whitelist") ? "\u767D\u540D\u5355" : "\u9ED1\u540D\u5355")));
        } else {
            list.add(Component.literal("\u00A78\u672A\u7ED1\u5B9A"));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.isCrouching()) return InteractionResultHolder.pass(stack);

        if (!level.isClientSide) {
            player.openMenu(new net.minecraft.world.MenuProvider() {
                @Override
                public Component getDisplayName() { return Component.literal(""); }
                @Override
                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new MarkerContainer(id, inv, stack);
                }
            });
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
        var player = ctx.getPlayer();
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();

        if (player == null || !player.isCrouching()) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

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

        var tag = stack.getOrCreateTag();

        // Toggle: if already bound to this same position → unbind
        if (tag.contains("BoundPos")) {
            var bp = StorageMarkerBauble.readBoundPos(tag);
            if (bp.equals(pos) && tag.getString("BoundDim").equals(level.dimension().location().toString())) {
                tag.remove("BoundPos");
                tag.remove("BoundDim");
                tag.remove("BoundBlock");
                player.sendSystemMessage(Component.literal("\u00A77[Storage Marker] \u5DF2\u89E3\u9664\u7ED1\u5B9A"));
                return InteractionResult.SUCCESS;
            }
        }

        // Bind new
        StorageMarkerBauble.writeBoundPos(tag, pos);
        tag.putString("BoundDim", level.dimension().location().toString());
        tag.putString("BoundBlock", be.getBlockState().getBlock().getName().getString());

        var blockName = be.getBlockState().getBlock().getName().getString();
        player.sendSystemMessage(Component.literal(
            "\u00A7a[Storage Marker] \u5DF2\u7ED1\u5B9A: \u00A7f" + blockName
            + " \u00A77@ " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));

        return InteractionResult.SUCCESS;
    }
}
