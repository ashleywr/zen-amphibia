package com.sanhiruzu.amphibia.event;

import com.sanhiruzu.amphibia.register.AmphibiaItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

public class FrogSlimeEquipmentHandler {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(FrogSlimeEquipmentHandler::onLivingFall);
    }

    private static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(AmphibiaItems.FROG_SLIME_BOOTS.get())) {
            return;
        }

        float distance = event.getDistance();
        if (entity.isSuppressingBounce()) {
            event.setDamageMultiplier(event.getDamageMultiplier() * 0.35F);
            return;
        }

        event.setDamageMultiplier(0.0F);
        entity.fallDistance = 0.0F;

        if (distance >= 3.0F) {
            Vec3 movement = entity.getDeltaMovement();
            double bounce = Math.min(0.9D, 0.35D + distance * 0.045D);
            entity.setDeltaMovement(movement.x, bounce, movement.z);
            entity.hasImpulse = true;
            boots.hurtAndBreak(1, entity, EquipmentSlot.FEET);
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.SLIME_BLOCK_FALL, SoundSource.PLAYERS, 0.8F, 1.1F);
        }
    }
}
