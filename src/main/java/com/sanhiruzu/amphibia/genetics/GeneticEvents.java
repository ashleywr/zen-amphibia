package com.sanhiruzu.amphibia.genetics;

import com.sanhiruzu.amphibia.AmphibiaConfig;
import com.sanhiruzu.amphibia.block.GeneticFrogspawnBlockEntity;
import com.sanhiruzu.amphibia.event.FrogSpawnHandler;
import com.sanhiruzu.amphibia.genetics.WildGeneticsRegistry;
import com.sanhiruzu.amphibia.item.BottledFrogspawnItem;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.amphibia.register.AmphibiaDataComponents;
import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "zen_amphibia")
public class GeneticEvents {

    @SubscribeEvent
    public static void onPlaceSpawn(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().is(Blocks.FROGSPAWN) && event.getEntity() instanceof Frog mom) {
            if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

            boolean isOptimalZone = false;
            ZoneData zone = null;

            if (ModList.get().isLoaded("zen_atelier")) {
                zone = ZoneAPI.getZoneAt(serverLevel, event.getPos());
                if (zone != null) {
                    String optimalType = AmphibiaConfig.OPTIMAL_BREEDING_ZONE_TYPE.get();
                    isOptimalZone = ZoneAPI.isZoneType(zone, optimalType);
                }
            }

            if (isOptimalZone) {
                FrogGenome genome = mom.getData(AmphibiaAttachments.OFFSPRING_GENOME);

                CompoundTag genomeTag = (CompoundTag) FrogGenome.CODEC.encodeStart(NbtOps.INSTANCE, genome).getOrThrow();

                serverLevel.setBlock(event.getPos(),
                    com.sanhiruzu.amphibia.register.AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get().defaultBlockState(), 3);

                WildGeneticsRegistry.get(serverLevel).put(event.getPos(), genomeTag);

                // Upload to zone genetics ledger via ZoneDataStore
                if (zone != null) {
                    CompoundTag ledgerTag = ZoneAPI.ZoneDataStore.get(
                        zone.getRegionId(), "amphibia_genetics_ledger", CompoundTag.class);
                    if (ledgerTag == null) ledgerTag = new CompoundTag();

                    ListTag discovered = ledgerTag.getList("DiscoveredGenomes", Tag.TAG_COMPOUND);

                    boolean exists = false;
                    for (int i = 0; i < discovered.size(); i++) {
                        if (discovered.getCompound(i).equals(genomeTag)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        discovered.add(genomeTag.copy());
                        ledgerTag.put("DiscoveredGenomes", discovered);
                        ZoneAPI.ZoneDataStore.set(zone.getRegionId(), "amphibia_genetics_ledger", ledgerTag);
                    }
                }

                mom.setData(AmphibiaAttachments.OFFSPRING_GENOME, FrogGenome.createDefault());
                event.setCanceled(true);
                return;
            }

            // Fallback: normal genetic frogspawn
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
            frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, false);
            FrogSpawnHandler.applyGeneticsToFrog(frog, genome);
            frog.setData(AmphibiaAttachments.FROG_GENETICS_APPLIED, true);
        }
    }

    @SubscribeEvent
    public static void onBucketInteract(BlockEvent.FluidPlaceBlockEvent event) {
        // Handled via BucketItemMixin for NBT transfer
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().is(AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get())
            && event.getLevel() instanceof ServerLevel serverLevel) {
            WildGeneticsRegistry.get(serverLevel).remove(event.getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            WildGeneticsRegistry.unload(serverLevel);
        }
    }
}
