package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.habitat.FrogHabitatEvaluator;
import com.sanhiruzu.amphibia.habitat.FrogHabitatReading;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class TerrariumHappinessHandler {
    private static final int HAPPINESS_UPDATE_INTERVAL = FrogHappinessConstants.HAPPINESS_UPDATE_INTERVAL;
    private static final float HAPPINESS_DECAY_PER_INTERVAL = FrogHappinessConstants.HAPPINESS_DECAY_PER_INTERVAL;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long gameTime = event.getServer().getTickCount();
        if (gameTime % HAPPINESS_UPDATE_INTERVAL != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Frog frog : level.getEntities(EntityType.FROG, Frog::isAlive)) {
                updateFrogHappiness(frog, level);
            }
        }
    }

    private static void updateFrogHappiness(Frog frog, Level level) {
        if (level.isClientSide) return;

        float newHappiness = computeHappiness(frog, level);
        float finalHappiness;

        if (newHappiness > 0) {
            finalHappiness = newHappiness;
        } else {
            float current = frog.getData(AmphibiaAttachments.FROG_HAPPINESS);
            finalHappiness = Math.max(0, current - HAPPINESS_DECAY_PER_INTERVAL);
        }

        frog.setData(AmphibiaAttachments.FROG_HAPPINESS, finalHappiness);
        applyJumpSuppression(frog, finalHappiness);
    }

    private static float computeHappiness(Frog frog, Level level) {
        FrogHabitatReading reading = FrogHabitatEvaluator.evaluate(level, frog.blockPosition(), frog);
        return reading.suitability();
    }

    // Happy frogs spend more time idle in their habitats instead of escaping.
    private static void applyJumpSuppression(Frog frog, float happiness) {
        if (happiness < FrogHappinessConstants.JUMP_SUPPRESS_THRESHOLD) return;

        int minCooldown = (int)(happiness * FrogHappinessConstants.HAPPY_JUMP_COOLDOWN_MAX);
        int current = frog.getBrain()
            .getMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS)
            .orElse(0);

        if (current < minCooldown) {
            frog.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, minCooldown);
        }
    }
}
