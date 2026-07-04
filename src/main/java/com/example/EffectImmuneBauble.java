package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public class EffectImmuneBauble implements IMaidBauble {
    @Override
    public void onTick(EntityMaid maid, ItemStack baubleItem) {
        if (maid.level().isClientSide) return;
        maid.removeEffect(MobEffects.BLINDNESS);
        maid.removeEffect(MobEffects.DIG_SLOWDOWN);
        maid.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }
}
