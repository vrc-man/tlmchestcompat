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
        .title(Component.literal("Fundationbufull"))
        .icon(() -> new ItemStack(ModItems.PLAYER_IMMORTAL_BAUBLE.get()))
        .displayItems((params, output) -> {
            output.accept(ModItems.PLAYER_IMMORTAL_BAUBLE.get());
        })
        .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
