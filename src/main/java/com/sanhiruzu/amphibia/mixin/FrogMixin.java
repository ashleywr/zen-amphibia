package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.AmphibiaConfig;
import com.sanhiruzu.amphibia.config.EstivationConfig;
import com.sanhiruzu.amphibia.config.EstivationConfigManager;
import com.sanhiruzu.amphibia.genetics.FrogEggLayingHandler;
import com.sanhiruzu.amphibia.genetics.FrogGenetics;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.OutdoorZoneData;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3f;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frog.class)
public abstract class FrogMixin {

    private static final String ESTIVATING_KEY = "amphibia:estivating";
    private static final String LAST_REVIVAL_KEY = "amphibia:last_revival_tick";

    @Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
    private void amphibia$mixGenomesOnBreed(ServerLevel level, Animal partner, CallbackInfo ci) {
        if (!(partner instanceof Frog partnerFrog)) return;
        Frog self = (Frog) (Object) this;
        FrogGenome selfGenome = self.getData(AmphibiaAttachments.FROG_GENOME);
        FrogGenome partnerGenome = partnerFrog.getData(AmphibiaAttachments.FROG_GENOME);
        FrogGenome childGenome = FrogGenetics.breed(selfGenome, partnerGenome, self.getRandom());
        partnerFrog.setData(AmphibiaAttachments.OFFSPRING_GENOME, childGenome);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void amphibia$checkEstivation(CallbackInfo ci) {
        Frog frog = (Frog) (Object) this;
        Level level = frog.level();
        if (level == null || level.isClientSide) return;

        if (level instanceof ServerLevel serverLevel) {
            FrogEggLayingHandler.tickEggLaying(frog, serverLevel);
        }

        if (!ModList.get().isLoaded("zen_atelier")) return;

        long gameTime = level.getGameTime();
        boolean isEstivating = frog.getPersistentData().getBoolean(ESTIVATING_KEY);

        if (isEstivating) {
            handleEstivationTick(frog, level, gameTime);
            return;
        }

        if (gameTime % 40 != 0) return;

        BlockPos pos = frog.blockPosition();
        ZoneData zone = ZoneAPI.getZoneAt(level, pos);

        float temp;
        float hum;
        if (zone instanceof OutdoorZoneData outdoor) {
            temp = outdoor.getTemperature(level) * 20.0f;
            hum = outdoor.hasPrecipitation(level) ? 80.0f : 20.0f;
        } else {
            var biome = level.getBiome(pos);
            temp = biome.value().getBaseTemperature() * 20.0f;
            hum = biome.value().hasPrecipitation() ? 80.0f : 20.0f;
        }

        String optimalType = AmphibiaConfig.OPTIMAL_BREEDING_ZONE_TYPE.get();
        boolean isOptimalZone = zone != null && ZoneAPI.isZoneType(zone, optimalType);

        if (isOptimalZone) {
            if (frog.getAge() == 0 && !frog.isInLove()) {
                boolean hasTannins = false;
                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                for (int x = -3; x <= 3; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -3; z <= 3; z++) {
                            mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                            BlockState s = level.getBlockState(mutable);
                            if (s.is(net.minecraft.tags.BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH)
                                || s.is(net.minecraft.world.level.block.Blocks.MUDDY_MANGROVE_ROOTS)) {
                                hasTannins = true;
                                break;
                            }
                        }
                    }
                }
                if (hasTannins) {
                    frog.setInLove(null);
                }
            }
        } else {
            EstivationConfig config = EstivationConfigManager.getConfig();
            if (config.shouldEstivate(temp, hum)) {
                long lastRevival = frog.getPersistentData().getLong(LAST_REVIVAL_KEY);
                if (gameTime - lastRevival > config.cooledownTicksAfterRevival()) {
                    startEstivation(frog);
                }
            }
        }
    }

    private void startEstivation(Frog frog) {
        frog.getPersistentData().putBoolean(ESTIVATING_KEY, true);
        frog.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, false, false, false));
    }

    private void handleEstivationTick(Frog frog, Level level, long gameTime) {
        if (checkShouldRevive(frog, level, gameTime)) {
            reviveFrog(frog, gameTime);
            return;
        }

        if (gameTime % 10 == 0) {
            spawnEstivationParticles(frog, level);
        }

        if (gameTime % 200 == 0) {
            frog.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 3, false, false, false));
        }
    }

    private boolean checkShouldRevive(Frog frog, Level level, long gameTime) {
        if (frog.isInWater()) return true;

        if (gameTime % 40 != 0) return false;

        BlockPos pos = frog.blockPosition();
        ZoneData zone = ZoneAPI.getZoneAt(level, pos);

        float temp;
        float hum;
        if (zone instanceof OutdoorZoneData outdoor) {
            temp = outdoor.getTemperature(level) * 20.0f;
            hum = outdoor.hasPrecipitation(level) ? 80.0f : 20.0f;
        } else {
            var biome = level.getBiome(pos);
            temp = biome.value().getBaseTemperature() * 20.0f;
            hum = biome.value().hasPrecipitation() ? 80.0f : 20.0f;
        }

        EstivationConfig config = EstivationConfigManager.getConfig();
        return !config.shouldEstivate(temp, hum);
    }

    private void reviveFrog(Frog frog, long gameTime) {
        frog.getPersistentData().putBoolean(ESTIVATING_KEY, false);
        frog.getPersistentData().putLong(LAST_REVIVAL_KEY, gameTime);
        frog.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    private void spawnEstivationParticles(Frog frog, Level level) {
        ServerLevel serverLevel = (ServerLevel) level;
        double x = frog.getX();
        double y = frog.getY() + 0.5;
        double z = frog.getZ();

        // Natural earthy dust (brownish)
        serverLevel.sendParticles(
            new DustParticleOptions(new Vector3f(0.55f, 0.42f, 0.25f), 1.0f),
            x, y, z,
            3,
            0.3, 0.2, 0.3,
            0.01
        );

        // Genetic-colored sparkle
        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        if (genome != null) {
            int color = genome.getColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(r, g, b), 1.0f),
                x, y, z,
                1,
                0.3, 0.2, 0.3,
                0.02
            );
        }
    }
}
