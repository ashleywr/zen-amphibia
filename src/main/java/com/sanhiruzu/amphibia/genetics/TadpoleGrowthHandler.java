package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class TadpoleGrowthHandler {
    private static final int CHECK_INTERVAL = 40;

    @SubscribeEvent
    public static void onTadpoleTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Tadpole tadpole)) return;
        if (tadpole.level().isClientSide) return;
        if (tadpole.tickCount % CHECK_INTERVAL != 0) return;

        Level level = tadpole.level();
        BlockPos pos = tadpole.blockPosition();

        if (!ModList.get().isLoaded("zen_atelier")) {
            applyOpenWaterGrowthModifier(tadpole, level, pos);
            return;
        }

        ZoneData zone = ZoneAPI.getZoneAt(level, pos);

        if (zone != null && zone.hasSpatialExtent()) {
            applyZoneGrowthModifier(tadpole, zone, level);
        } else {
            applyOpenWaterGrowthModifier(tadpole, level, pos);
        }
    }

    private static void applyZoneGrowthModifier(Tadpole tadpole, ZoneData zone, Level level) {
        TadpoleZoneEcology.WaterVolumeResult volumeResult = TadpoleZoneEcology.calculateWaterVolume(tadpole.blockPosition(), level);
        int waterVolume = volumeResult.blockCount;

        int tadpoleCount = countTadpolesInZone(level, zone);
        if (tadpoleCount == 0) tadpoleCount = 1;

        double biomassRatio = (double) waterVolume / tadpoleCount;

        if (biomassRatio < 10) {
            applyStuntedGrowth(tadpole);
        } else if (biomassRatio > 50) {
            applyAcceleratedGrowth(tadpole);
        }
    }

    private static void applyOpenWaterGrowthModifier(Tadpole tadpole, Level level, BlockPos pos) {
        TadpoleZoneEcology.WaterVolumeResult volumeResult = TadpoleZoneEcology.calculateWaterVolume(pos, level);
        if (volumeResult.isOpenWater) {
            if (level.random.nextDouble() < 0.0083) {
                tadpole.discard();
            }
        }
    }

    private static int countTadpolesInZone(Level level, ZoneData zone) {
        if (!zone.hasSpatialExtent()) return 1;

        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
            zone.getMinX(), zone.getMinY(), zone.getMinZ(),
            zone.getMaxX() + 1, zone.getMaxY() + 1, zone.getMaxZ() + 1
        );

        int count = 0;
        for (Tadpole t : level.getEntities(
            net.minecraft.world.entity.EntityType.TADPOLE,
            aabb,
            t -> zone.contains(t.blockPosition())
        )) {
            count++;
        }
        return count;
    }

    private static void applyStuntedGrowth(Tadpole tadpole) {
        tadpole.setData(AmphibiaAttachments.STUNTED_GROWTH, true);

        FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
        if (genome != null && genome.mutations().size() > 0 && tadpole.level().random.nextDouble() < 0.1) {
            var mutations = new java.util.ArrayList<>(genome.mutations());
            if (!mutations.isEmpty()) {
                mutations.remove(tadpole.level().random.nextInt(mutations.size()));
                FrogGenome modifiedGenome = new FrogGenome(genome.genes(), mutations);
                tadpole.setData(AmphibiaAttachments.FROG_GENOME, modifiedGenome);
            }
        }
    }

    private static void applyAcceleratedGrowth(Tadpole tadpole) {
        tadpole.setData(AmphibiaAttachments.ACCELERATED_GROWTH, true);
    }
}
