package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "amphibia")
public class GeneticEvents {

    @SubscribeEvent
    public static void onBreed(BabyEntitySpawnEvent event) {
        if (event.getParentA() instanceof Frog mom && event.getParentB() instanceof Frog dad) {
            FrogDNA momDNA = mom.getData(AmphibiaAttachments.FROG_DNA);
            FrogDNA dadDNA = dad.getData(AmphibiaAttachments.FROG_DNA);
            FrogDNA childDNA = FrogDNA.mix(momDNA, dadDNA, mom.getRandom());
            mom.setData(AmphibiaAttachments.OFFSPRING_DNA, childDNA);
        }
    }

    @SubscribeEvent
    public static void onPlaceSpawn(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().is(Blocks.FROGSPAWN) && event.getEntity() instanceof Frog mom) {
            FrogDNA dna = mom.getData(AmphibiaAttachments.OFFSPRING_DNA);
            
            event.getLevel().setBlock(event.getPos(), AmphibiaBlocks.GENETIC_FROGSPAWN.get().defaultBlockState(), 3);
            
            if (event.getLevel().getBlockEntity(event.getPos()) instanceof GeneticFrogspawnBlockEntity be) {
                be.setDna(dna);
            }
            
            mom.setData(AmphibiaAttachments.OFFSPRING_DNA, FrogDNA.createDefault());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMetamorphosis(LivingConversionEvent.Post event) {
        if (event.getEntity() instanceof Tadpole tadpole && event.getOutcome() instanceof Frog frog) {
            FrogDNA dna = tadpole.getData(AmphibiaAttachments.FROG_DNA);
            frog.setData(AmphibiaAttachments.FROG_DNA, dna);
        }
    }
}
