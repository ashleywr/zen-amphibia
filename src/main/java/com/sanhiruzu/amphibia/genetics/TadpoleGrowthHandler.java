package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.zen_zones.zone.StandardZone;
import com.sanhiruzu.zen_zones.zone.ZoneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "amphibia")
public class TadpoleGrowthHandler {
    private static final int CHECK_INTERVAL = 40;

    @SubscribeEvent
    public static void onTadpoleTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Tadpole tadpole)) return;
        if (tadpole.level().isClientSide) return;

        if (tadpole.tickCount % CHECK_INTERVAL != 0) return;

        Level level = tadpole.level();
        BlockPos pos = tadpole.blockPosition();

        ZoneManager manager = ZoneManager.get(level);
        if (manager == null) return;

        com.sanhiruzu.zen_zones.zone.Zone genericZone = manager.getZoneAt(pos);
        StandardZone zone = genericZone instanceof StandardZone fz ? fz : null;

        if (zone != null) {
            applyZoneGrowthModifier(tadpole, zone, level, pos);
        } else {
            applyOpenWaterGrowthModifier(tadpole, level, pos);
        }
    }

    private static void applyZoneGrowthModifier(Tadpole tadpole, StandardZone zone, Level level, BlockPos pos) {
        // Calculate water volume within zone bounds
        TadpoleZoneEcology.WaterVolumeResult volumeResult = calculateZoneWaterVolume(zone, level);
        int waterVolume = volumeResult.blockCount;

        // Count tadpoles in zone
        int tadpoleCount = countTadpolesInZone(level, zone);

        if (tadpoleCount == 0) tadpoleCount = 1; // Avoid division by zero
        double biomassRatio = (double) waterVolume / tadpoleCount;

        // Apply growth modifiers based on biomass ratio
        if (biomassRatio < 10) {
            // Stunted growth: half growth rate, 10% chance to lose random trait
            applyStuntedGrowth(tadpole);
        } else if (biomassRatio > 50) {
            // Accelerated growth
            applyAcceleratedGrowth(tadpole);
        }
        // Normal growth (10-50): no modifier needed
    }

    private static void applyOpenWaterGrowthModifier(Tadpole tadpole, Level level, BlockPos pos) {
        TadpoleZoneEcology.WaterVolumeResult volumeResult = TadpoleZoneEcology.calculateWaterVolume(pos, level);

        if (volumeResult.isOpenWater) {
            // 5% chance per minute to despawn in open water
            if (level.random.nextDouble() < 0.0083) { // 0.0083 ≈ 5% per 1200 ticks (1 minute)
                tadpole.discard();
            }
        }
    }

    private static TadpoleZoneEcology.WaterVolumeResult calculateZoneWaterVolume(StandardZone zone, Level level) {
        BlockPos center = new BlockPos(
            (zone.getBounds().minX() + zone.getBounds().maxX()) / 2,
            (zone.getBounds().minY() + zone.getBounds().maxY()) / 2,
            (zone.getBounds().minZ() + zone.getBounds().maxZ()) / 2
        );
        return TadpoleZoneEcology.calculateWaterVolume(center, level);
    }

    private static int countTadpolesInZone(Level level, StandardZone zone) {
        int count = 0;
        net.minecraft.world.level.levelgen.structure.BoundingBox bounds = zone.getBounds();
        net.minecraft.world.phys.AABB aabb = new net.minecraft.world.phys.AABB(
            bounds.minX(), bounds.minY(), bounds.minZ(),
            bounds.maxX() + 1, bounds.maxY() + 1, bounds.maxZ() + 1
        );

        for (Tadpole tadpole : level.getEntities(
            net.minecraft.world.entity.EntityType.TADPOLE,
            aabb,
            (t) -> zone.contains(t.blockPosition())
        )) {
            count++;
        }
        return count;
    }

    private static void applyStuntedGrowth(Tadpole tadpole) {
        // Store stunted flag on tadpole data
        tadpole.setData(AmphibiaAttachments.STUNTED_GROWTH, true);

        // 10% chance to lose random trait
        FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
        if (genome != null && genome.mutations().size() > 0 && tadpole.level().random.nextDouble() < 0.1) {
            // Randomly remove a mutation
            var mutations = new java.util.ArrayList<>(genome.mutations());
            if (!mutations.isEmpty()) {
                mutations.remove(tadpole.level().random.nextInt(mutations.size()));
                FrogGenome modifiedGenome = new FrogGenome(genome.genes(), mutations);
                tadpole.setData(AmphibiaAttachments.FROG_GENOME, modifiedGenome);
            }
        }
    }

    private static void applyAcceleratedGrowth(Tadpole tadpole) {
        // Mark for accelerated growth (could be used in metamorphosis logic)
        tadpole.setData(AmphibiaAttachments.ACCELERATED_GROWTH, true);
    }
}
