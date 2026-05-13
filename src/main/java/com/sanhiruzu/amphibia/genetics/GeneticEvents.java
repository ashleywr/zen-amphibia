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
        if (event.getParentA() instanceof Frog mom && event.getParentB() instanceof Frog dad && event.getChild() instanceof Frog baby) {
            FrogDNA momDNA = mom.getData(AmphibiaAttachments.FROG_DNA);
            FrogDNA dadDNA = dad.getData(AmphibiaAttachments.FROG_DNA);
            FrogDNA childDNA = FrogDNA.mix(momDNA, dadDNA, mom.getRandom());
            baby.setData(AmphibiaAttachments.FROG_DNA, childDNA);
        }
    }

    @SubscribeEvent
    public static void onPlaceSpawn(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().is(Blocks.FROGSPAWN) && event.getEntity() instanceof Frog mom) {
            if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
            
            com.sanhiruzu.zonectrl.zone.FactoryZoneManager manager = com.sanhiruzu.zonectrl.zone.FactoryZoneManager.get(serverLevel);
            if (manager != null) {
                com.sanhiruzu.zonectrl.api.IAtmosphere atmosphere = manager.getAt(event.getPos());
                if (atmosphere instanceof com.sanhiruzu.zonectrl.zone.FactoryZone zone) {
                    if (com.sanhiruzu.zonectrl.zone.AtmosphereManager.determineAtmosphere(zone).equals(com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_ATMOSPHERE.get())) {
                        FrogDNA dna = mom.getData(AmphibiaAttachments.OFFSPRING_DNA);
                        
                        // Place fluid instead of eggs
                        serverLevel.setBlock(event.getPos(), com.sanhiruzu.amphibia.register.AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get().defaultBlockState(), 3);
                        
                        // Upload to ledger
                        net.minecraft.nbt.CompoundTag ledgerTag = manager.getGenetics(zone.getId());
                        if (ledgerTag == null) ledgerTag = new net.minecraft.nbt.CompoundTag();
                        
                        net.minecraft.nbt.ListTag discovered = ledgerTag.getList("DiscoveredDNA", net.minecraft.nbt.Tag.TAG_COMPOUND);
                        // Simple serialization for MVP
                        net.minecraft.nbt.CompoundTag dnaTag = (net.minecraft.nbt.CompoundTag) FrogDNA.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, dna).getOrThrow();
                        discovered.add(dnaTag);
                        ledgerTag.put("DiscoveredDNA", discovered);
                        
                        manager.saveGenetics(zone.getId(), ledgerTag);
                        
                        mom.setData(AmphibiaAttachments.OFFSPRING_DNA, FrogDNA.createDefault());
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            // Fallback for non-optimal zones: normal genetic frogspawn
            FrogDNA dna = mom.getData(AmphibiaAttachments.OFFSPRING_DNA);
            serverLevel.setBlock(event.getPos(), AmphibiaBlocks.GENETIC_FROGSPAWN.get().defaultBlockState(), 3);
            
            if (serverLevel.getBlockEntity(event.getPos()) instanceof GeneticFrogspawnBlockEntity be) {
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
