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
        "backpack_bauble", () -> new BaubleItem(
            "\u00A77\u9700\u914D\u5408\u4E0B\u754C\u5408\u91D1\u80CC\u5305\u653E\u5728\u5973\u4EC6 Curios \u80CC\u9970\u69FD\u4F4D\u4F7F\u7528",
            "\u00A77\u81EA\u52A8\u5B58\u5165\uFF0C\u9965\u996F\u65F6\u81EA\u52A8\u53D6\u98DF\u7269",
            "\u00A7e\u6CA1\u6709\u80CC\u5305\u65F6\u4E0D\u4F1A\u4F5C\u7528\uFF0C\u8BF7\u786E\u4FDD\u88C5\u5907\u80CC\u5305")
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
        "player_immortal_bauble", () -> new BaubleItem(
            "\u00A77\u73A9\u5BB6\u4E13\u7528\uFF0C\u514D\u75AB\u4E00\u5207\u4F24\u5BB3\u5305\u62EC/kill",
            "\u00A7e\u653E\u5165 Curios \u9970\u54C1\u69FD\u6216\u4E3B\u624B\u4FBF\u53EF\u751F\u6548")
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
