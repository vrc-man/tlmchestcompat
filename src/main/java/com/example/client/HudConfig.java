package com.example.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HudConfig {
    public static boolean enabled = true;
    public static int updateInterval = 20; // ticks
    public static float scale = 1.0f;
    public static float opacity = 0.85f;
    public static int position = 0; // 0=TL, 1=TC, 2=TR, 3=BL, 4=BR
    public static int xOffset = 0;
    public static int yOffset = 0;
}
