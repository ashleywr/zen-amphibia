package com.sanhiruzu.amphibia.entity.goal;

import com.sanhiruzu.amphibia.genetics.FrogCombatCapability;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

public class FrogBiteGoal extends MeleeAttackGoal {
    private final Frog frog;
    private final double damageBonus;

    public FrogBiteGoal(Frog frog, double damageBonus) {
        super(frog, 1.0, false);
        this.frog = frog;
        this.damageBonus = damageBonus;
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = frog.getTarget();
        if (target != null && frog.isWithinMeleeAttackRange(target)) {
            var attackDamageAttr = frog.getAttribute(Attributes.ATTACK_DAMAGE);
            float baseDamage = attackDamageAttr != null ? (float) attackDamageAttr.getValue() : 1.0f;
            float damage = baseDamage + (float) damageBonus;

            target.hurt(frog.damageSources().mobAttack(frog), damage);

            if (FrogCombatCapability.canPoison(frog.getData(AmphibiaAttachments.FROG_GENOME))) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0, false, false));
            }
        }
    }
}
