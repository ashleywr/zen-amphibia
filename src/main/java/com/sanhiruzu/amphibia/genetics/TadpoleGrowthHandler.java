package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class TadpoleGrowthHandler {
    private static final int CHECK_INTERVAL = 80;
    private static final int DENSITY_RADIUS = 5;
    private static final int DENSITY_VERTICAL_RADIUS = 2;
    private static final int OVERCROWDED_TADPOLES = 7;
    private static final int FAILING_TADPOLES = 10;
    private static final int SURVIVAL_GRACE_TICKS = 1200;
    private static final double FAILING_SURVIVAL_CHANCE = 0.06;

    @SubscribeEvent
    public static void onTadpoleTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Tadpole tadpole)) return;
        if (tadpole.level().isClientSide) return;
        if (tadpole.tickCount % CHECK_INTERVAL != 0) return;

        Level level = tadpole.level();
        BlockPos pos = tadpole.blockPosition();

        tadpole.setData(AmphibiaAttachments.STUNTED_GROWTH, false);
        tadpole.setData(AmphibiaAttachments.ACCELERATED_GROWTH, false);

        if (level instanceof ServerLevel serverLevel && applyLocalDensityPressure(tadpole, serverLevel, pos)) {
            return;
        }

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

    private static boolean applyLocalDensityPressure(Tadpole tadpole, ServerLevel level, BlockPos pos) {
        AABB bounds = new AABB(pos).inflate(DENSITY_RADIUS, DENSITY_VERTICAL_RADIUS, DENSITY_RADIUS);
        int nearbyTadpoles = level.getEntities(
            EntityType.TADPOLE,
            bounds,
            nearby -> nearby.isAlive() && nearby.isInWater()
        ).size();

        if (nearbyTadpoles < OVERCROWDED_TADPOLES) {
            return false;
        }

        applyStuntedGrowth(tadpole);
        spawnOvercrowdingParticles(tadpole, level, nearbyTadpoles >= FAILING_TADPOLES);

        if (nearbyTadpoles >= FAILING_TADPOLES
            && tadpole.tickCount > SURVIVAL_GRACE_TICKS
            && !tadpole.hasCustomName()
            && level.random.nextDouble() < FAILING_SURVIVAL_CHANCE) {
            level.sendParticles(
                ParticleTypes.POOF,
                tadpole.getX(), tadpole.getY() + 0.1, tadpole.getZ(),
                6,
                0.2, 0.1, 0.2,
                0.01
            );
            tadpole.discard();
            return true;
        }

        return false;
    }

    private static void spawnOvercrowdingParticles(Tadpole tadpole, ServerLevel level, boolean failing) {
        if (level.random.nextInt(failing ? 2 : 4) != 0) {
            return;
        }

        level.sendParticles(
            ParticleTypes.BUBBLE,
            tadpole.getX(), tadpole.getY() + 0.1, tadpole.getZ(),
            failing ? 8 : 4,
            0.25, 0.15, 0.25,
            0.02
        );

        if (failing) {
            spawnFailingWaterParticles(tadpole, level);
        }
    }

    private static void spawnFailingWaterParticles(Tadpole tadpole, ServerLevel level) {
        BlockPos surfacePos = findWaterSurface(level, tadpole.blockPosition());
        double x = surfacePos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;
        double y = surfacePos.getY() + 0.08;
        double z = surfacePos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2.5;

        level.sendParticles(
            ParticleTypes.WITCH,
            x, y, z,
            2,
            0.35, 0.05, 0.35,
            0.01
        );

        if (level.random.nextInt(3) == 0) {
            level.sendParticles(
                ParticleTypes.SMOKE,
                x, y + 0.02, z,
                1,
                0.15, 0.03, 0.15,
                0.003
            );
        }
    }

    private static BlockPos findWaterSurface(ServerLevel level, BlockPos start) {
        BlockPos.MutableBlockPos mutable = start.mutable();

        for (int i = 0; i < 5; i++) {
            if (!level.getBlockState(mutable.above()).is(Blocks.WATER)) {
                return mutable.immutable().above();
            }
            mutable.move(0, 1, 0);
        }

        return start.above();
    }

    private static void applyStuntedGrowth(Tadpole tadpole) {
        tadpole.setData(AmphibiaAttachments.STUNTED_GROWTH, true);

        FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
        if (genome != null && genome.mutations().size() > 0 && tadpole.level().random.nextDouble() < 0.01) {
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
