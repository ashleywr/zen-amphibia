package com.sanhiruzu.amphibia.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sanhiruzu.amphibia.genetics.WildGeneticsRegistry;
import com.sanhiruzu.amphibia.register.AmphibiaFluids;
import com.sanhiruzu.atelier.api.ZoneAPI;
import com.sanhiruzu.atelier.space.zone.ZoneData;
import com.simibubi.create.content.fluids.transfer.FluidDrainingBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(FluidDrainingBehaviour.class)
public abstract class FluidDrainingBehaviourMixin {

    @Inject(
        method = "pullNext",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z")
    )
    private void amphibia$onFluidDrained(BlockPos root, boolean simulate, CallbackInfoReturnable<Boolean> cir,
                                         @Local(name = "world") Level world,
                                         @Local(name = "currentPos") BlockPos currentPos,
                                         @Local(name = "blockState") BlockState blockState) {
        if (world instanceof ServerLevel serverLevel
            && blockState.is(AmphibiaFluids.RAW_GENETIC_FLUID_BLOCK.get())) {

            WildGeneticsRegistry wildRegistry = WildGeneticsRegistry.get(serverLevel);
            CompoundTag genetics = wildRegistry.get(currentPos);

            if (genetics != null) {
                wildRegistry.remove(currentPos);

                if (ModList.get().isLoaded("zen_atelier")) {
                    ZoneData zone = ZoneAPI.getZoneAt(serverLevel, currentPos);
                    if (zone != null) {
                        amphibia$uploadToLedger(zone.getRegionId(), genetics);
                    }
                }
            }
        }
    }

    @Unique
    private void amphibia$uploadToLedger(UUID zoneId, CompoundTag genomeTag) {
        if (!ModList.get().isLoaded("zen_atelier")) return;

        CompoundTag ledgerTag = ZoneAPI.ZoneDataStore.get(zoneId, "amphibia_genetics_ledger", CompoundTag.class);
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
            ZoneAPI.ZoneDataStore.set(zoneId, "amphibia_genetics_ledger", ledgerTag);
        }
    }
}
