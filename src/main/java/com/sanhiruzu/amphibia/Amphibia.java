package com.sanhiruzu.amphibia;

import com.sanhiruzu.amphibia.infrastructure.FrogClipboardHandler;
import com.sanhiruzu.amphibia.item.FrogBucketItem;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("zen_amphibia")
public class Amphibia {
    private static final Logger LOGGER = LoggerFactory.getLogger(Amphibia.class);

    public Amphibia(IEventBus modEventBus, net.neoforged.fml.ModContainer modContainer) {
        modContainer.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON, AmphibiaConfig.SPEC);
        modEventBus.addListener(this::setup);
        com.sanhiruzu.amphibia.register.AmphibiaEntityTypes.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaFluids.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaBlocks.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaItems.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaBlockEntities.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaAttachments.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaDataComponents.register(modEventBus);
        modEventBus.register(com.sanhiruzu.amphibia.infrastructure.display.AmphibiaDisplaySources.class);
        modEventBus.addListener(FMLCommonSetupEvent.class,
            event -> com.sanhiruzu.amphibia.infrastructure.display.AmphibiaDisplaySources.registerAll());
        modEventBus.addListener(this::addCreative);

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onItemCrafted);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onBlockPlaced);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onReloadListeners);
        com.sanhiruzu.amphibia.event.CommandEvents.register();
    }

    private void onItemCrafted(net.neoforged.neoforge.event.entity.player.PlayerEvent.ItemCraftedEvent event) {
        net.minecraft.world.item.ItemStack crafted = event.getCrafting();
        if (crafted.is(com.simibubi.create.AllBlocks.PACKAGE_FROGPORT.get().asItem())) {
            net.minecraft.world.Container inv = event.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                net.minecraft.world.item.ItemStack slotStack = inv.getItem(i);
                if (slotStack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET.get())) {
                    FrogGenome genome = slotStack.get(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get());
                    if (genome != null) {
                        crafted.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), genome);
                        net.minecraft.world.item.component.CustomData.update(
                            net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, crafted, tag -> {
                                try {
                                    tag.put("AmphibiaGenome", FrogGenome.CODEC.encodeStart(
                                        net.minecraft.nbt.NbtOps.INSTANCE, genome).getOrThrow());
                                } catch (Exception e) {}
                            });
                        crafted.setCount(1);
                    }
                    break;
                }
            }
        }
    }

    private void onRightClickBlock(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        net.minecraft.world.item.ItemStack stack = event.getItemStack();
        if (stack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET.get())) {
            net.minecraft.core.Direction face = event.getFace();
            if (face == null) return;
            FrogBucketItem.onPlaceFrog(event.getLevel(), stack, event.getPos().relative(face));
            if (!event.getEntity().isCreative()) {
                event.getEntity().setItemInHand(event.getHand(),
                    new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));
            }
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (stack.getItem() instanceof com.simibubi.create.content.equipment.clipboard.ClipboardBlockItem) {
            net.minecraft.world.level.block.entity.BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
            if (be instanceof com.sanhiruzu.amphibia.duck.IFrogportDNA duck) {
                FrogGenome genome = duck.amphibia$getGenome();
                if (genome != null) {
                    FrogClipboardHandler.addDNAToClipboard(stack, genome, "Frogport Genetics:");
                    event.getEntity().displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Genetics Diagnostics Copied!")
                            .withStyle(net.minecraft.ChatFormatting.AQUA), true);
                    event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    private void onEntityInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        if (event.getTarget() instanceof net.minecraft.world.entity.animal.frog.Frog frog && !frog.isBaby()) {
            net.minecraft.world.item.ItemStack stack = event.getItemStack();
            if (stack.is(net.minecraft.world.item.Items.BUCKET)) {
                net.minecraft.world.entity.player.Player player = event.getEntity();

                net.minecraft.world.item.ItemStack frogBucket = new net.minecraft.world.item.ItemStack(
                    com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET.get());
                FrogGenome genome = frog.getData(com.sanhiruzu.amphibia.register.AmphibiaAttachments.FROG_GENOME);
                if (genome != null) {
                    frogBucket.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get(), genome);
                }

                frog.playSound(net.minecraft.sounds.SoundEvents.BUCKET_FILL_FISH, 1.0f, 1.0f);
                frog.discard();

                if (stack.getCount() == 1) {
                    player.setItemInHand(event.getHand(), frogBucket);
                } else {
                    stack.shrink(1);
                    if (!player.getInventory().add(frogBucket)) {
                        player.drop(frogBucket, false);
                    }
                }
                event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            if (stack.getItem() instanceof com.simibubi.create.content.equipment.clipboard.ClipboardBlockItem) {
                FrogGenome genome = frog.getData(com.sanhiruzu.amphibia.register.AmphibiaAttachments.FROG_GENOME);
                if (genome != null) {
                    FrogClipboardHandler.addDNAToClipboard(stack, genome, "Frog Genetics:");
                    event.getEntity().displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Genetics Sequenced!")
                            .withStyle(net.minecraft.ChatFormatting.AQUA), true);
                    event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    private void addCreative(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS) {
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET);
        }
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaItems.GENETIC_FROGSPAWN);
        }
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.INGREDIENTS) {
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaFluids.RAW_GENETIC_FLUID_BUCKET.get());
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaFluids.REFINED_GENETIC_FLUID_BUCKET.get());
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaFluids.SEQUENCED_GENETIC_FLUID_BUCKET.get());
        }
    }

    private void onBlockPlaced(net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel)) return;
        if (!event.getPlacedBlock().is(com.simibubi.create.AllBlocks.PACKAGE_FROGPORT.get())) return;

        net.minecraft.world.item.ItemStack stack = event.getEntity() instanceof net.minecraft.world.entity.player.Player player
            ? player.getMainHandItem() : null;

        if (stack != null && !stack.isEmpty()) {
            FrogGenome genome = stack.get(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA.get());
            if (genome != null) {
                net.minecraft.world.level.block.entity.BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
                if (be instanceof com.sanhiruzu.amphibia.duck.IFrogportDNA duck) {
                    duck.amphibia$setGenome(genome);
                    be.setChanged();
                }
            }
        }
    }

    private void onReloadListeners(net.neoforged.neoforge.event.AddReloadListenerEvent event) {
        event.addListener(new com.sanhiruzu.amphibia.config.EstivationConfigManager());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Amphibia (Frog Breeding) initialized!");
        com.sanhiruzu.amphibia.compat.patchouli.PatchouliCompat.checkPatchouliLoaded();
        com.sanhiruzu.amphibia.api.AmphibiaPluginRegistry.loadAll();
    }
}
