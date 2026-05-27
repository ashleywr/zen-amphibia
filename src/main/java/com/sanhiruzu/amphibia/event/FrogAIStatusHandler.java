package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogAIStatusHandler {

    @SubscribeEvent
    public static void onFrogTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Frog frog)) return;
        if (frog.level().isClientSide) return;
        if (frog.tickCount % 20 != 0) return;

        String next = buildAIStatus(frog);
        if (!next.equals(frog.getData(AmphibiaAttachments.CURRENT_AI_STATUS))) {
            frog.setData(AmphibiaAttachments.CURRENT_AI_STATUS, next);
        }

        // Grow scale from genome base toward genome*1.05 over MATURATION_TICKS
        long birthTick = frog.getData(AmphibiaAttachments.BIRTH_GAME_TIME);
        if (birthTick > 0L) {
            AmphibiaFrog af = AmphibiaFrog.of(frog);
            float progress = af.getMaturityProgress(frog.level().getGameTime());
            if (progress < 1.0f) {
                float genomeScale = frog.getData(AmphibiaAttachments.FROG_GENOME).getScale();
                af.setScale(genomeScale * (1.0f + 0.05f * progress));
            }
        }
    }

    private static String buildAIStatus(Frog frog) {
        List<String> parts = new ArrayList<>();
        var brain = frog.getBrain();

        // Primary non-core activity: idle, swim, tongue (eating), fight, lay_spawn, etc.
        brain.getActiveNonCoreActivity().ifPresent(a -> parts.add("[" + a.getName() + "]"));

        // Brain-tracked combat target (tongue prey or brain-initiated fight)
        brain.getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target ->
            parts.add("target: " + target.getType().getDescription().getString()));

        // Courting another frog
        if (brain.hasMemoryValue(MemoryModuleType.BREED_TARGET)) {
            parts.add("courting");
        }

        // Tempted by player holding food
        if (brain.hasMemoryValue(MemoryModuleType.TEMPTING_PLAYER)) {
            parts.add("tempted");
        }

        // Goal-based combat from FrogHostileTargetGoal (not tracked in brain)
        LivingEntity goalTarget = frog.getTarget();
        if (goalTarget != null && brain.getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty()) {
            parts.add("targeting: " + goalTarget.getType().getDescription().getString());
        }

        return parts.isEmpty() ? "idle" : String.join(", ", parts);
    }
}
