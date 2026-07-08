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
        "backpack_bauble", BackpackBaubleItem::new
    );

    public static final RegistryObject<Item> TRUE_IMMORTAL_BAUBLE = ITEMS.register(
        "true_immortal_bauble", () -> new BaubleItem(
            "\u00A77\u5973\u4EC6\u4E13\u7528\uFF0C\u514D\u75AB\u4E00\u5207\u4F24\u5BB3\u5305\u62EC/kill")
    );

    public static final RegistryObject<Item> INFO_SCANNER = ITEMS.register(
        "info_scanner", InfoScannerItem::new
    );

    public static final RegistryObject<Item> EFFECT_IMMUNE_BAUBLE = ITEMS.register(
        "effect_immune_bauble", () -> new BaubleItem(
            "\u00A77\u5973\u4EC6\u4E13\u7528\uFF0C\u514D\u75AB\u5931\u660E/\u75B2\u52B3/\u7F13\u6162")
    );

    public static final RegistryObject<Item> STORAGE_MARKER = ITEMS.register(
        "storage_marker", StorageMarkerItem::new
    );

    public static final RegistryObject<Item> PLAYER_IMMORTAL_BAUBLE = ITEMS.register(
        "player_immortal_bauble", PlayerImmortalItem::new
    );

    public static final RegistryObject<Item> MAID_REFLECT_BAUBLE = ITEMS.register(
        "maid_reflect_bauble", MaidReflectItem::new
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
