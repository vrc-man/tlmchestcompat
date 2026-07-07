package com.example;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BaubleItem extends Item implements ICurioItem {
    private final String[] tooltips;

    public BaubleItem(String... tooltips) {
        super(new Properties().stacksTo(1));
        this.tooltips = tooltips;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        for (var t : tooltips) {
            list.add(Component.literal(t));
        }
    }
}
