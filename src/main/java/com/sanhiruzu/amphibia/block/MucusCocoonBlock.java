package com.sanhiruzu.amphibia.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MucusCocoonBlock extends Block {
    public MucusCocoonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.PASS;

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() == Items.POTION || mainHand.getItem() == Items.GLASS_BOTTLE) {
            Frog frog = EntityType.FROG.create(level);
            if (frog != null) {
                frog.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                frog.getPersistentData().putLong("amphibia:last_cocoon_tick", level.getGameTime());
                level.addFreshEntity(frog);
            }

            if (!player.isCreative()) {
                mainHand.shrink(1);
                ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.addItem(glassBottle)) {
                    level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, glassBottle));
                }
            }

            level.destroyBlock(pos, false);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
