package com.sanhiruzu.amphibia;

import com.sanhiruzu.amphibia.item.FrogBucketItem;
import com.sanhiruzu.zonectrl.api.AtmosphereRegistry;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("amphibia")
public class Amphibia {
    private static final Logger LOGGER = LoggerFactory.getLogger(Amphibia.class);

    public Amphibia(IEventBus modEventBus) {
        registerDefaults();
        modEventBus.addListener(this::setup);
        com.sanhiruzu.amphibia.register.AmphibiaBlocks.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaItems.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaBlockEntities.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaAttachments.register(modEventBus);
        com.sanhiruzu.amphibia.register.AmphibiaDataComponents.register(modEventBus);
        modEventBus.register(com.sanhiruzu.amphibia.infrastructure.display.AmphibiaDisplaySources.class);
        modEventBus.addListener(FMLCommonSetupEvent.class, event -> com.sanhiruzu.amphibia.infrastructure.display.AmphibiaDisplaySources.registerAll());
        modEventBus.addListener(this::addCreative);

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onBucketUse);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void registerDefaults() {
        AtmosphereRegistry.registerDefault("monsoon_chamber",
            new AtmosphereRegistry.AtmosphereDef(
                "The Monsoon Chamber", 28.0f, 85.0f, 80.0f,
                "High rust risk for iron; required for Amphibian breeding."
            )
        );
    }

    private void onBucketUse(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        net.minecraft.world.item.ItemStack stack = event.getItemStack();
        if (stack.is(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET.get())) {
            net.minecraft.core.Direction face = event.getFace();
            if (face == null) return;
            FrogBucketItem.onPlaceFrog(event.getLevel(), stack, event.getPos().relative(face));
            if (!event.getEntity().isCreative()) {
                event.getEntity().setItemInHand(event.getHand(), new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BUCKET));
            }
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private void onEntityInteract(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) return;
        if (event.getTarget() instanceof net.minecraft.world.entity.animal.frog.Frog frog && !frog.isBaby()) {
            net.minecraft.world.item.ItemStack stack = event.getItemStack();
            if (stack.is(net.minecraft.world.item.Items.BUCKET)) {
                net.minecraft.world.entity.player.Player player = event.getEntity();
                
                net.minecraft.world.item.ItemStack frogBucket = new net.minecraft.world.item.ItemStack(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET.get());
                com.sanhiruzu.amphibia.genetics.FrogDNA dna = frog.getData(com.sanhiruzu.amphibia.register.AmphibiaAttachments.FROG_DNA);
                frogBucket.set(com.sanhiruzu.amphibia.register.AmphibiaDataComponents.FROG_DNA, dna);

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
            }
        }
    }

    private void addCreative(net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.SPAWN_EGGS) {
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaItems.FROG_BUCKET);
        }
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaItems.MUCUS_COCOON);
            event.accept(com.sanhiruzu.amphibia.register.AmphibiaItems.GENETIC_FROGSPAWN);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Amphibia (Frog Breeding) initialized!");
    }
}
