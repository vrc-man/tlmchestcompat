package com.example;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = "fundationbufull";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> PLAYER_IMMORTAL_BAUBLE = ITEMS.register(
        "player_immortal_bauble", PlayerImmortalItem::new
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
