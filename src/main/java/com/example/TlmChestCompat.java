package com.example;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tlmchestcompat")
public class TlmChestCompat {
    @SuppressWarnings("removal")
    public TlmChestCompat() {
        ModItems.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
