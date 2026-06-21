package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.config.EstivationConfig;
import com.sanhiruzu.amphibia.config.EstivationConfigManager;
import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.joml.Vector3f;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogEstivationHandler {

    @SubscribeEvent
    public static void onFrogTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        Level level = frog.level();
        if (level == null || level.isClientSide) return;

        AmphibiaFrog af = AmphibiaFrog.of(frog);
        long gameTime = level.getGameTime();

        if (af.isEstivating()) {
            handleEstivationTick(af, level, gameTime);
            return;
        }

        if (gameTime % 40 != 0) return;

        BlockPos pos = frog.blockPosition();
        FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(level, pos, frog);
        float[] tempHum = readTempHumidity(level, pos, habitat);
        float temp = tempHum[0];
        float hum = tempHum[1];

        if (habitat.isOptimalBreedingHabitat()) {
            tryInduceLoveFromTannins(frog, level, pos);
        } else {
            EstivationConfig config = EstivationConfigManager.getConfig();
            if (config.shouldEstivate(temp, hum)) {
                if (gameTime - af.getLastRevivalTick() > config.cooledownTicksAfterRevival()) {
                    af.startEstivation();
                }
            }
        }
    }

    private static void handleEstivationTick(AmphibiaFrog af, Level level, long gameTime) {
        if (checkShouldRevive(af.entity(), level, gameTime)) {
            af.revive(gameTime);
            return;
        }

        if (gameTime % 10 == 0) {
            spawnEstivationParticles(af, level);
        }

        if (gameTime % 200 == 0) {
            af.refreshEstivationEffect();
        }
    }

    private static boolean checkShouldRevive(Frog frog, Level level, long gameTime) {
        if (frog.isInWater()) return true;
        if (gameTime % 40 != 0) return false;

        BlockPos pos = frog.blockPosition();
        FrogHabitatReading habitat = FrogHabitatEvaluator.evaluate(level, pos, frog);
        float[] tempHum = readTempHumidity(level, pos, habitat);

        EstivationConfig config = EstivationConfigManager.getConfig();
        return !config.shouldEstivate(tempHum[0], tempHum[1]);
    }

    private static float[] readTempHumidity(Level level, BlockPos pos, FrogHabitatReading habitat) {
        var biome = level.getBiome(pos);
        float biomeTemp = biome.value().getBaseTemperature() * 20.0f;
        float biomeHum = biome.value().hasPrecipitation() ? 80.0f : 20.0f;

        float habitatCooling = (1.0f - habitat.climateScore()) * 6.0f;
        float waterHumidity = habitat.waterScore() * 40.0f;
        float temp = Math.max(0.0f, biomeTemp - habitatCooling);
        float hum = Math.min(100.0f, biomeHum + waterHumidity);
        return new float[]{temp, hum};
    }

    private static void tryInduceLoveFromTannins(Frog frog, Level level, BlockPos pos) {
        if (frog.getAge() != 0 || frog.isInLove()) return;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -3; z <= 3; z++) {
                    mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    BlockState s = level.getBlockState(mutable);
                    if (s.is(net.minecraft.tags.BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                            || s.is(net.minecraft.world.level.block.Blocks.MUDDY_MANGROVE_ROOTS)) {
                        frog.setInLove(null);
                        return;
                    }
                }
            }
        }
    }

    private static void spawnEstivationParticles(AmphibiaFrog af, Level level) {
        ServerLevel serverLevel = (ServerLevel) level;
        Frog frog = af.entity();
        double x = frog.getX();
        double y = frog.getY() + 0.5;
        double z = frog.getZ();

        serverLevel.sendParticles(
            new DustParticleOptions(new Vector3f(0.55f, 0.42f, 0.25f), 1.0f),
            x, y, z, 3, 0.3, 0.2, 0.3, 0.01
        );

        FrogGenome genome = af.getGenome();
        if (genome != null) {
            int color = genome.getColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(r, g, b), 1.0f),
                x, y, z, 1, 0.3, 0.2, 0.3, 0.02
            );
        }
    }
}
