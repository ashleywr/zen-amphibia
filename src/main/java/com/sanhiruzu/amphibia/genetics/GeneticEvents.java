package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.item.BottledFrogspawnItem;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "amphibia")
public class GeneticEvents {

    @SubscribeEvent
    public static void onBreed(BabyEntitySpawnEvent event) {
        if (event.getParentA() instanceof Frog mom && event.getParentB() instanceof Frog dad && event.getChild() instanceof Frog baby) {
            FrogGenome momGenome = mom.getData(AmphibiaAttachments.FROG_GENOME);
            FrogGenome dadGenome = dad.getData(AmphibiaAttachments.FROG_GENOME);
            FrogGenome childGenome = FrogGenetics.breed(momGenome, dadGenome, mom.getRandom());
            baby.setData(AmphibiaAttachments.FROG_GENOME, childGenome);
        }
    }

    @SubscribeEvent
    public static void onPlaceSpawn(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().is(Blocks.FROGSPAWN) && event.getEntity() instanceof Frog mom) {
            if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;

            com.sanhiruzu.zen_zones.zone.ZoneManager manager = com.sanhiruzu.zen_zones.zone.ZoneManager.get(serverLevel);
            if (manager != null) {
                com.sanhiruzu.zen_zones.api.IAtmosphere atmosphere = manager.getAt(event.getPos());
                if (atmosphere instanceof com.sanhiruzu.zen_zones.zone.StandardZone zone) {
                    if (com.sanhiruzu.zen_zones.zone.AtmosphereManager.determineAtmosphere(zone).equals(com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_ATMOSPHERE.get())) {
                        FrogGenome genome = mom.getData(AmphibiaAttachments.OFFSPRING_GENOME);

                        // Place fluid instead of eggs
                        serverLevel.setBlock(event.getPos(), com.sanhiruzu.amphibia.register.AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get().defaultBlockState(), 3);

                        // Encode genome to NBT
                        net.minecraft.nbt.CompoundTag genomeTag = (net.minecraft.nbt.CompoundTag) FrogGenome.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, genome).getOrThrow();

                        // Store in WildGeneticsRegistry (for extraction trigger)
                        com.sanhiruzu.zen_zones.zone.WildGeneticsRegistry.get(serverLevel).put(event.getPos(), genomeTag);

                        // Also upload to ledger immediately if in a zone
                        net.minecraft.nbt.CompoundTag ledgerTag = manager.getGenetics(zone.getId());
                        if (ledgerTag == null) ledgerTag = new net.minecraft.nbt.CompoundTag();

                        net.minecraft.nbt.ListTag discovered = ledgerTag.getList("DiscoveredGenomes", net.minecraft.nbt.Tag.TAG_COMPOUND);
                        discovered.add(genomeTag);
                        ledgerTag.put("DiscoveredGenomes", discovered);

                        manager.saveGenetics(zone.getId(), ledgerTag);

                        mom.setData(AmphibiaAttachments.OFFSPRING_GENOME, FrogGenome.createDefault());
                        event.setCanceled(true);
                        return;
                    }
                }
            }

            // Fallback for non-optimal zones: normal genetic frogspawn
            FrogGenome genome = mom.getData(AmphibiaAttachments.OFFSPRING_GENOME);
            serverLevel.setBlock(event.getPos(), AmphibiaBlocks.GENETIC_FROGSPAWN.get().defaultBlockState(), 3);

            if (serverLevel.getBlockEntity(event.getPos()) instanceof GeneticFrogspawnBlockEntity be) {
                be.setGenome(genome);
            }

            mom.setData(AmphibiaAttachments.OFFSPRING_GENOME, FrogGenome.createDefault());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemIncubation(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof ItemEntity itemEntity && !event.getEntity().level().isClientSide) {
            ItemStack stack = itemEntity.getItem();

            if (stack.getItem() instanceof BottledFrogspawnItem) {
                FrogGenome genome = stack.get(AmphibiaDataComponents.FROG_DNA.get());
                if (genome == null) return;

                CompoundTag tag = itemEntity.getPersistentData();
                int incubationTicks = tag.getInt("IncubationTicks");

                if (isInHeatedCauldron(itemEntity)) {
                    tag.putInt("IncubationTicks", incubationTicks + 1);

                    if (incubationTicks + 1 >= 3000) {
                        spawnTadpoleFromIncubation(itemEntity, genome, tag);
                        itemEntity.discard();
                    }
                }
            }
        }
    }

    private static boolean isInHeatedCauldron(ItemEntity itemEntity) {
        var level = itemEntity.level();
        var pos = itemEntity.blockPosition();
        var blockState = level.getBlockState(pos);

        if (blockState.is(Blocks.WATER_CAULDRON)) {
            var firePos = pos.below();
            var fireState = level.getBlockState(firePos);
            return fireState.is(Blocks.CAMPFIRE) && fireState.getValue(CampfireBlock.LIT);
        }

        return false;
    }

    private static void spawnTadpoleFromIncubation(ItemEntity itemEntity, FrogGenome genome, CompoundTag tag) {
        var level = itemEntity.level();
        var pos = itemEntity.blockPosition();

        Tadpole tadpole = new Tadpole(net.minecraft.world.entity.EntityType.TADPOLE, level);
        tadpole.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        FrogGenome incubatedGenome = genome;

        int lapis = tag.getInt("CatalystLapis");
        if (lapis > 0 && level.random.nextDouble() < 0.2) {
            incubatedGenome = applyMutation(incubatedGenome, "arcane_receptacle");
        }

        int slime = tag.getInt("CatalystSlime");
        if (slime > 0) {
            // Increase SLIME_VISCOSITY trait
            incubatedGenome = applySlimeMutation(incubatedGenome, slime);
        }

        tadpole.setData(AmphibiaAttachments.FROG_GENOME, incubatedGenome);
        level.addFreshEntity(tadpole);
    }

    private static FrogGenome applyMutation(FrogGenome genome, String mutationId) {
        var mutations = new java.util.ArrayList<>(genome.mutations());
        if (!mutations.contains(mutationId)) {
            mutations.add(mutationId);
        }
        return new FrogGenome(genome.genes(), mutations);
    }

    private static FrogGenome applySlimeMutation(FrogGenome genome, int count) {
        // For now, just add slime viscosity mutation if count > 0
        var mutations = new java.util.ArrayList<>(genome.mutations());
        if (!mutations.contains("slime_viscosity") && count > 0) {
            mutations.add("slime_viscosity");
        }
        return new FrogGenome(genome.genes(), mutations);
    }

    @SubscribeEvent
    public static void onMetamorphosis(LivingConversionEvent.Post event) {
        if (event.getEntity() instanceof Tadpole tadpole && event.getOutcome() instanceof Frog frog) {
            FrogGenome genome = tadpole.getData(AmphibiaAttachments.FROG_GENOME);
            frog.setData(AmphibiaAttachments.FROG_GENOME, genome);
        }
    }

    @SubscribeEvent
    public static void onBucketInteract(BlockEvent.FluidPlaceBlockEvent event) {
        // Handle logic for placing fluid with NBT if needed
    }

    @SubscribeEvent
    public static void onBlockInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        
        net.minecraft.world.level.Level level = event.getLevel();
        BlockPos pos = event.getPos();
        net.minecraft.world.item.ItemStack stack = event.getItemStack();
        
        // 1. Draining Bio-Broth into a bucket (Pick up)
        if (stack.is(net.minecraft.world.item.Items.BUCKET)) {
            BlockState state = level.getBlockState(pos);
            if (state.is(com.sanhiruzu.amphibia.register.AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get())) {
                com.sanhiruzu.zen_zones.zone.WildGeneticsRegistry wildRegistry = com.sanhiruzu.zen_zones.zone.WildGeneticsRegistry.get(level);
                net.minecraft.nbt.CompoundTag genetics = wildRegistry.remove(pos);
                
                if (genetics != null) {
                    // We need to wait for the bucket to be filled to apply NBT. 
                    // But BucketItem.use handles the swap.
                    // This is tricky. Let's use a Mixin on BucketItem instead for robustness.
                }
            }
        }
    }
}
