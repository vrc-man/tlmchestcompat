package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.item.bauble.BaubleManager;

@LittleMaidExtension
public class MaidBaubleRegistry implements ILittleMaid {
    @Override
    public void bindMaidBauble(BaubleManager manager) {
        manager.bind(ModItems.BACKPACK_BAUBLE.get(), new BackpackBauble());
    }
}
