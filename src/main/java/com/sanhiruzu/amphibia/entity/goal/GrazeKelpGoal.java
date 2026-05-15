package com.sanhiruzu.amphibia.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class GrazeKelpGoal extends Goal {
    private final PathfinderMob mob;
    private final int searchRadius;
    private BlockPos targetPlant;
    private int timeToRecalculate;

    public GrazeKelpGoal(PathfinderMob mob, int searchRadius) {
        this.mob = mob;
        this.searchRadius = searchRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.timeToRecalculate > 0) {
            this.timeToRecalculate--;
            return false;
        }

        this.targetPlant = this.findNearestPlant();
        return this.targetPlant != null;
    }

    @Override
    public void tick() {
        if (this.targetPlant == null) {
            this.stop();
            return;
        }

        Level level = this.mob.level();
        BlockState state = level.getBlockState(this.targetPlant);

        if (!isKelpOrSeagrass(state)) {
            this.stop();
            return;
        }

        // Move towards the top of the plant
        BlockPos topPos = findTopOfPlant(this.targetPlant);
        this.mob.getNavigation().moveTo(topPos.getX() + 0.5, topPos.getY() + 0.5, topPos.getZ() + 0.5, 1.0);

        // If close enough, eat the plant
        if (this.mob.distanceToSqr(topPos.getCenter()) < 2.0) {
            eatPlant(topPos);
        }
    }

    @Override
    public void stop() {
        this.targetPlant = null;
        this.timeToRecalculate = 10;
        this.mob.getNavigation().stop();
    }

    private BlockPos findNearestPlant() {
        BlockPos mobPos = this.mob.blockPosition();
        BlockPos bestPos = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -this.searchRadius; x <= this.searchRadius; x++) {
            for (int y = -this.searchRadius; y <= this.searchRadius; y++) {
                for (int z = -this.searchRadius; z <= this.searchRadius; z++) {
                    BlockPos checkPos = mobPos.offset(x, y, z);
                    BlockState state = this.mob.level().getBlockState(checkPos);

                    if (isKelpOrSeagrass(state)) {
                        double distance = this.mob.distanceToSqr(checkPos.getCenter());
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            bestPos = checkPos;
                        }
                    }
                }
            }
        }

        return bestPos;
    }

    private BlockPos findTopOfPlant(BlockPos pos) {
        Level level = this.mob.level();
        BlockPos current = pos;

        while (current.getY() < level.getMaxBuildHeight()) {
            BlockState aboveState = level.getBlockState(current.above());
            if (isKelpOrSeagrass(aboveState)) {
                current = current.above();
            } else {
                break;
            }
        }

        return current;
    }

    private void eatPlant(BlockPos pos) {
        Level level = this.mob.level();
        BlockState state = level.getBlockState(pos);

        if (!isKelpOrSeagrass(state)) return;

        BlockPos rootPos = findRootOfPlant(pos);
        boolean hasProtectedRoot = isRootProtected(rootPos, level);

        if (hasProtectedRoot) {
            // Don't eat, but mark as visited by removing some blocks below
            return;
        }

        // Remove the entire stalk
        removePlantFromTop(pos);
    }

    private BlockPos findRootOfPlant(BlockPos pos) {
        Level level = this.mob.level();
        BlockPos current = pos;

        while (current.getY() > level.getMinBuildHeight()) {
            BlockState belowState = level.getBlockState(current.below());
            if (isKelpOrSeagrass(belowState)) {
                current = current.below();
            } else {
                break;
            }
        }

        return current.below(); // Return the block the plant is rooted in
    }

    private boolean isRootProtected(BlockPos rootPos, Level level) {
        BlockState rootState = level.getBlockState(rootPos);
        return rootState.is(BlockTags.create(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("amphibia", "growth_accelerators")));
    }

    private void removePlantFromTop(BlockPos pos) {
        Level level = this.mob.level();
        if (!level.isClientSide) {
            level.destroyBlock(pos, false);
        }
    }

    private boolean isKelpOrSeagrass(BlockState state) {
        return state.getBlock() instanceof KelpBlock ||
               state.getBlock() instanceof SeagrassBlock ||
               state.is(Blocks.TALL_SEAGRASS);
    }
}
