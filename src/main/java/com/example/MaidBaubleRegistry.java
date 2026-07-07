package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;

@LittleMaidExtension
public class MaidBaubleRegistry implements ILittleMaid {
    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(ModItems.BACKPACK_BAUBLE.get(), new BackpackBauble());
        manager.bind(ModItems.TRUE_IMMORTAL_BAUBLE.get(), new TrueImmortalBauble());
        manager.bind(ModItems.EFFECT_IMMUNE_BAUBLE.get(), new EffectImmuneBauble());
        manager.bind(ModItems.STORAGE_MARKER.get(), new StorageMarkerBauble());
        manager.bind(ModItems.MAID_REFLECT_BAUBLE.get(), new MaidReflectBauble());
    }
}
