package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.zen_zones.zone.AtmosphereManager;
import com.sanhiruzu.zen_zones.zone.ZoneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frog.class)
public abstract class FrogMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void amphibia$checkEstivation(CallbackInfo ci) {
        Frog frog = (Frog) (Object) this;
        Level level = frog.level();
        if (level == null || level.isClientSide) return;

        // Genetics are applied in FrogSpawnHandler.onFrogJoinLevel() before first render
        // Check breeding conditions every 40 ticks
        if (level.getGameTime() % 40 != 0) return;

        ZoneManager manager = ZoneManager.get(level);
        if (manager == null) return;

        BlockPos pos = frog.blockPosition();
        com.sanhiruzu.zen_zones.api.IAtmosphere atmosphereZone = manager.getAt(pos);
        if (atmosphereZone == null) return;

        float temp = atmosphereZone.getTemperature();
        float hum = atmosphereZone.getHumidity();
        String atmosphere = AtmosphereManager.determineAtmosphere(atmosphereZone);

        if (atmosphere.equals(com.sanhiruzu.amphibia.AmphibiaConfig.OPTIMAL_BREEDING_ATMOSPHERE.get())) {
            if (frog.getAge() == 0 && !frog.isInLove()) {
                boolean hasTannins = false;
                BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                for (int x = -3; x <= 3; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -3; z <= 3; z++) {
                            mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                            BlockState s = level.getBlockState(mutable);
                            if (s.is(net.minecraft.tags.BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH) || s.is(net.minecraft.world.level.block.Blocks.MUDDY_MANGROVE_ROOTS)) {
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
            com.sanhiruzu.amphibia.config.EstivationConfig config = com.sanhiruzu.amphibia.config.EstivationConfigManager.getConfig();
            if (config.shouldEstivate(temp, hum)) {
                long lastCocoonTick = frog.getPersistentData().getLong("amphibia:last_cocoon_tick");
                long ticksSinceCocoon = level.getGameTime() - lastCocoonTick;
                if (ticksSinceCocoon > config.cooledownTicksAfterRevival()) {
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlockAndUpdate(pos, AmphibiaBlocks.MUCUS_COCOON.get().defaultBlockState());
                        frog.discard();
                    }
                }
            }
        }
    }

}
