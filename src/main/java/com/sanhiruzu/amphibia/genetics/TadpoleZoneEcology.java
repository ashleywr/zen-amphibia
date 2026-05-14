package com.sanhiruzu.amphibia.genetics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class TadpoleZoneEcology {
    private static final int VOLUME_CAP = 250;
    private static final int SEARCH_RADIUS = 16;

    public static WaterVolumeResult calculateWaterVolume(BlockPos startPos, Level level) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);

        int waterBlockCount = 0;

        while (!queue.isEmpty() && visited.size() < VOLUME_CAP * 2) {
            BlockPos current = queue.poll();

            if (level.getBlockState(current).is(Blocks.WATER)) {
                waterBlockCount++;

                if (waterBlockCount >= VOLUME_CAP) {
                    return new WaterVolumeResult(VOLUME_CAP, true);
                }

                // Add adjacent blocks to queue
                for (BlockPos adjacent : getAdjacentPositions(current)) {
                    if (!visited.contains(adjacent) && visited.size() < VOLUME_CAP * 2) {
                        visited.add(adjacent);
                        queue.add(adjacent);
                    }
                }
            }
        }

        return new WaterVolumeResult(waterBlockCount, waterBlockCount >= VOLUME_CAP);
    }

    private static BlockPos[] getAdjacentPositions(BlockPos pos) {
        return new BlockPos[]{
            pos.above(), pos.below(),
            pos.north(), pos.south(),
            pos.east(), pos.west()
        };
    }

    public static class WaterVolumeResult {
        public final int blockCount;
        public final boolean isOpenWater;

        WaterVolumeResult(int blockCount, boolean isOpenWater) {
            this.blockCount = blockCount;
            this.isOpenWater = isOpenWater;
        }
    }
}
