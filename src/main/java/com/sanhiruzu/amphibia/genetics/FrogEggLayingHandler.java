package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;

public class FrogEggLayingHandler {

    public static void tickEggLaying(Frog frog, ServerLevel level) {
        if (!frog.isInLove()) return;

        BlockPos frogPos = frog.blockPosition();

        if (frog.isInWater()) {
            tryLayEggs(frog, level, frogPos);
        } else {
            navigateToWater(frog, level, frogPos);
        }
    }

    private static void navigateToWater(Frog frog, ServerLevel level, BlockPos frogPos) {
        BlockPos nearestWater = findNearestWater(level, frogPos);
        if (nearestWater != null && frog.getRandom().nextInt(10) == 0) {
            frog.getNavigation().moveTo(
                nearestWater.getX() + 0.5,
                nearestWater.getY() + 0.5,
                nearestWater.getZ() + 0.5,
                1.0
            );
        }
    }

    private static void tryLayEggs(Frog frog, ServerLevel level, BlockPos frogPos) {
        FrogGenome offspringGenome = frog.getData(AmphibiaAttachments.OFFSPRING_GENOME);
        if (offspringGenome == null || offspringGenome == FrogGenome.createDefault()) {
            return;
        }

        BlockPos spawnPos = findWaterBlockNear(level, frogPos);
        if (spawnPos == null) return;

        boolean isOptimalZone = false;
        ZoneData zone = null;

        if (ModList.get().isLoaded("zen_atelier")) {
            zone = ZoneAPI.getZoneAt(level, spawnPos);
            if (zone != null) {
                String optimalType = com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_ZONE_TYPE.get();
                isOptimalZone = ZoneAPI.isZoneType(zone, optimalType);
            }
        }

        CompoundTag genomeTag = (CompoundTag) FrogGenome.CODEC.encodeStart(NbtOps.INSTANCE, offspringGenome).getOrThrow();

        if (isOptimalZone) {
            level.setBlock(spawnPos, AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get().defaultBlockState(), 3);
            WildGeneticsRegistry.get(level).put(spawnPos, genomeTag);

            if (zone != null) {
                CompoundTag ledgerTag = ZoneAPI.ZoneDataStore.get(
                    zone.getRegionId(), "amphibia_genetics_ledger", CompoundTag.class);
                if (ledgerTag == null) ledgerTag = new CompoundTag();

                ListTag discovered = ledgerTag.getList("DiscoveredGenomes", Tag.TAG_COMPOUND);

                boolean exists = false;
                for (int i = 0; i < discovered.size(); i++) {
                    if (discovered.getCompound(i).equals(genomeTag)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    discovered.add(genomeTag.copy());
                    ledgerTag.put("DiscoveredGenomes", discovered);
                    ZoneAPI.ZoneDataStore.set(zone.getRegionId(), "amphibia_genetics_ledger", ledgerTag);
                }
            }
        } else {
            level.setBlock(spawnPos, AmphibiaBlocks.GENETIC_FROGSPAWN.get().defaultBlockState(), 3);

            if (level.getBlockEntity(spawnPos) instanceof com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity be) {
                be.setGenome(offspringGenome);
            }
        }

        frog.setInLove(null);
        frog.setData(AmphibiaAttachments.OFFSPRING_GENOME, FrogGenome.createDefault());
    }

    private static BlockPos findWaterBlockNear(ServerLevel level, BlockPos center) {
        for (int y = -1; y <= 1; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (level.getBlockState(pos).is(Blocks.WATER)) {
                        BlockPos above = pos.above();
                        if (level.getBlockState(above).canBeReplaced()) {
                            return above;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static BlockPos findNearestWater(ServerLevel level, BlockPos center) {
        BlockPos nearest = null;
        double minDist = Double.MAX_VALUE;
        int searchRadius = 32;

        for (int x = -searchRadius; x <= searchRadius; x++) {
            for (int y = -searchRadius; y <= searchRadius; y++) {
                for (int z = -searchRadius; z <= searchRadius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    if (!level.getBlockState(pos).getFluidState().isEmpty()) {
                        double dist = center.distSqr(pos);
                        if (dist < minDist) {
                            minDist = dist;
                            nearest = pos;
                        }
                    }
                }
            }
        }

        return nearest;
    }
}
