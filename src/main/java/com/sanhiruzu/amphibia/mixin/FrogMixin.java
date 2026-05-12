package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.register.AmphibiaBlocks;
import com.sanhiruzu.zonectrl.zone.AtmosphereManager;
import com.sanhiruzu.zonectrl.zone.FactoryZone;
import com.sanhiruzu.zonectrl.zone.FactoryZoneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = Frog.class, remap = false)
public abstract class FrogMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void amphibia$checkEstivation(CallbackInfo ci) {
        Frog frog = (Frog) (Object) this;
        Level level = frog.level();
        if (level == null || level.isClientSide) return;
        if (level.getGameTime() % 40 != 0) return;

        FactoryZoneManager manager = FactoryZoneManager.get(level);
        if (manager == null) return;

        BlockPos pos = frog.blockPosition();
        Optional<FactoryZone> zoneOpt = manager.getAt(pos);
        if (zoneOpt.isEmpty()) return;

        FactoryZone zone = zoneOpt.get();
        float temp = zone.getTemperature();
        float hum = zone.getHumidity();
        String atmosphere = AtmosphereManager.determineAtmosphere(zone);

        if (atmosphere.equals("monsoon_chamber")) {
            // MVP: Breeding trigger requires Tannins (Peat Moss or Mangrove Logs nearby)
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
        } else if (temp > 35.0f || hum < 20.0f) {
            // Estivation trigger
            if (level.getBlockState(pos).isAir()) {
                level.setBlockAndUpdate(pos, AmphibiaBlocks.MUCUS_COCOON.get().defaultBlockState());
                // In a real mod, we'd store the frog's DNA in a BlockEntity here
                frog.discard();
            }
        }
    }
}
