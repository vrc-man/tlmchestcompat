package com.example;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = "tlmchestcompat";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> BACKPACK_BAUBLE = ITEMS.register(
        "backpack_bauble", () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> TRUE_IMMORTAL_BAUBLE = ITEMS.register(
        "true_immortal_bauble", () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> INFO_SCANNER = ITEMS.register(
        "info_scanner", InfoScannerItem::new
    );

    public static final RegistryObject<Item> EFFECT_IMMUNE_BAUBLE = ITEMS.register(
        "effect_immune_bauble", () -> new Item(new Item.Properties().stacksTo(1))
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
