package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(modid = "zen_amphibia")
public class FrogDropHandler {

    @SubscribeEvent
    public static void onItemEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;
        if (event.getLevel().isClientSide()) return;
        if (!isVanillaFroglight(itemEntity.getItem().getItem())) return;

        Level level = event.getLevel();
        AABB box = new AABB(itemEntity.blockPosition()).inflate(2);
        List<Frog> nearby = level.getEntities(EntityType.FROG, box, Frog::isAlive);
        if (nearby.isEmpty()) return;

        Frog frog = nearby.stream()
            .min(Comparator.comparingDouble(f -> f.distanceToSqr(itemEntity)))
            .orElse(null);
        if (frog == null) return;

        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        Item geneticFroglight = froglightForGenome(genome);

        event.setCanceled(true);
        level.addFreshEntity(new ItemEntity(
            level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
            new ItemStack(geneticFroglight)
        ));
    }

    private static Item froglightForGenome(FrogGenome genome) {
        int color = genome.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        int spread = max - min;

        if (spread < 30) {
            int avg = (r + g + b) / 3;
            return avg > 170
                ? AmphibiaBlocks.PEARL_GENETIC_FROGLIGHT.get().asItem()
                : AmphibiaBlocks.UMBRAL_GENETIC_FROGLIGHT.get().asItem();
        }

        if (r == max) {
            return (g - b) > 20
                ? AmphibiaBlocks.AMBER_GENETIC_FROGLIGHT.get().asItem()
                : AmphibiaBlocks.ROSE_GENETIC_FROGLIGHT.get().asItem();
        }

        if (g == max) {
            return AmphibiaBlocks.VERDANT_GENETIC_FROGLIGHT.get().asItem();
        }

        // blue dominant
        return (r - g) > 20
            ? AmphibiaBlocks.VIOLET_GENETIC_FROGLIGHT.get().asItem()
            : AmphibiaBlocks.AZURE_GENETIC_FROGLIGHT.get().asItem();
    }

    private static boolean isVanillaFroglight(Item item) {
        return item == Items.OCHRE_FROGLIGHT
            || item == Items.VERDANT_FROGLIGHT
            || item == Items.PEARLESCENT_FROGLIGHT;
    }
}
