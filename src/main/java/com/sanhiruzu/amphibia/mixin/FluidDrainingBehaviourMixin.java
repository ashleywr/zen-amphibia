package com.sanhiruzu.amphibia.mixin;

import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import com.sanhiruzu.zonectrl.zone.FactoryZoneManager;
import com.sanhiruzu.zonectrl.zone.WildGeneticsRegistry;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(value = FluidDrainingBehaviour.class, remap = false)
public abstract class FluidDrainingBehaviourMixin {

    @Inject(
        method = "pullNext",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void amphibia$onFluidDrained(BlockPos root, boolean simulate, CallbackInfoReturnable<Boolean> cir, 
                                        Level world, BlockPos currentPos, BlockState blockState, BlockState emptied) {
        
        if (world instanceof ServerLevel serverLevel && blockState.is(AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get())) {
            // Extraction phase: Upload DNA to local ledger
            WildGeneticsRegistry wildRegistry = WildGeneticsRegistry.get(serverLevel);
            CompoundTag genetics = wildRegistry.remove(currentPos);
            
            if (genetics != null) {
                FactoryZoneManager zoneManager = FactoryZoneManager.get(serverLevel);
                if (zoneManager != null) {
                    com.sanhiruzu.zonectrl.api.IAtmosphere atmosphere = zoneManager.getAt(currentPos);
                    if (atmosphere instanceof com.sanhiruzu.zonectrl.zone.FactoryZone zone) {
                        amphibia$uploadToLedger(zoneManager, zone.getId(), genetics);
                    }
                }
            }
        }
    }

    private void amphibia$uploadToLedger(FactoryZoneManager manager, UUID zoneId, CompoundTag genomeTag) {
        CompoundTag ledgerTag = manager.getGenetics(zoneId);
        if (ledgerTag == null) ledgerTag = new CompoundTag();

        ListTag discovered = ledgerTag.getList("DiscoveredGenomes", Tag.TAG_COMPOUND);
        
        // Avoid duplicates if possible (though breeding might produce same DNA)
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
            manager.saveGenetics(zoneId, ledgerTag);
        }
    }
}
