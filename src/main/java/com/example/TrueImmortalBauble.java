package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public class TrueImmortalBauble implements IMaidBauble {
    @Override
    public boolean onDeath(EntityMaid maid, ItemStack stack, DamageSource source) {
        maid.setHealth(maid.getMaxHealth());
        return true;
    }
}
