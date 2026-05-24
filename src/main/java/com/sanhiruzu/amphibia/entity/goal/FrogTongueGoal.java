package com.sanhiruzu.amphibia.entity.goal;

import com.sanhiruzu.amphibia.genetics.FrogCombatCapability;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.EnumSet;

public class FrogTongueGoal extends Goal {
    private final Frog frog;
    private final double damageBonus;
    private LivingEntity target;
    private int cooldown;

    public FrogTongueGoal(Frog frog, double damageBonus) {
        this.frog = frog;
        this.damageBonus = damageBonus;
        this.cooldown = 0;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = frog.getTarget();
        if (owner == null) return false;
        if (!frog.hasLineOfSight(owner)) return false;
        if (cooldown > 0) return false;
        double dist = frog.distanceToSqr(owner);
        return dist > 4.0 && dist < 64.0;
    }

    @Override
    public void start() {
        target = frog.getTarget();
        cooldown = 0;
    }

    @Override
    public void tick() {
        if (target == null || !target.isAlive()) {
            stop();
            return;
        }

        frog.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        double dist = frog.distanceToSqr(target);
        if (dist < 64.0 && frog.hasLineOfSight(target)) {
            var attackDamageAttr = frog.getAttribute(Attributes.ATTACK_DAMAGE);
            float baseDamage = attackDamageAttr != null ? (float) attackDamageAttr.getValue() : 1.0f;
            float damage = baseDamage + (float) damageBonus;

            target.hurt(frog.damageSources().mobAttack(frog), damage);

            if (FrogCombatCapability.canPoison(frog.getData(AmphibiaAttachments.FROG_GENOME))) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0, false, false));
            }

            cooldown = 20;
        }
    }

    @Override
    public void stop() {
        target = null;
    }
}
