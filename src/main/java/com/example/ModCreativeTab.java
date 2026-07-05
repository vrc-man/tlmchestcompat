package com.example;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModItems.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab", () -> CreativeModeTab.builder()
        .title(Component.literal("TLM Chest Compat"))
        .icon(() -> new ItemStack(ModItems.INFO_SCANNER.get()))
        .displayItems((params, output) -> {
            output.accept(ModItems.INFO_SCANNER.get());
            output.accept(ModItems.BACKPACK_BAUBLE.get());
            output.accept(ModItems.TRUE_IMMORTAL_BAUBLE.get());
            output.accept(ModItems.EFFECT_IMMUNE_BAUBLE.get());
            output.accept(ModItems.STORAGE_MARKER.get());
        })
        .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}