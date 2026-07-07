package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MaidReflectBauble implements IMaidBauble {
    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide) return;

        // Remove all negative effects
        maid.removeEffect(MobEffects.POISON);
        maid.removeEffect(MobEffects.WITHER);
        maid.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        maid.removeEffect(MobEffects.DIG_SLOWDOWN);
        maid.removeEffect(MobEffects.BLINDNESS);
        maid.removeEffect(MobEffects.HUNGER);
        maid.removeEffect(MobEffects.WEAKNESS);
        maid.removeEffect(MobEffects.LEVITATION);
        maid.removeEffect(MobEffects.UNLUCK);
        maid.removeEffect(MobEffects.BAD_OMEN);
        maid.removeEffect(MobEffects.DARKNESS);

        // Knockback resistance
        var kbAttr = maid.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) kbAttr.setBaseValue(1.0);
    }
}
