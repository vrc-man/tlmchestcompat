package com.example;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IChestType;
import com.github.tartaricacid.touhoulittlemaid.inventory.chest.ChestManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

@LittleMaidExtension
public class SopChestExtension implements ILittleMaid {
    @Override
    public void addChestType(ChestManager manager) {
        manager.add(new IChestType() {
            @Override
            public boolean isChest(BlockEntity te) {
                return te != null && te.getClass().getName().toLowerCase().contains("sophisticated");
            }

            @Override
            public boolean canOpenByPlayer(BlockEntity te, Player player) {
                BlockPos pos = te.getBlockPos();
                return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
            }

            @Override
            public int getOpenCount(BlockGetter level, BlockPos pos, BlockEntity te) {
                return 0;
            }
        });
    }
}
