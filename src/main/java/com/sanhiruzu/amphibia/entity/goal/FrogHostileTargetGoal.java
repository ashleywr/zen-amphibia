package com.sanhiruzu.amphibia.entity.goal;

import com.sanhiruzu.amphibia.genetics.AmphibiaFrog;
import com.sanhiruzu.amphibia.profession.WardenRoleHelper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogHostileTargetGoal extends NearestAttackableTargetGoal<Monster> {

    public FrogHostileTargetGoal(Frog frog) {
        super(frog, Monster.class, true);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) return false;

        Frog frog = (Frog) this.mob;

        if (AmphibiaFrog.of(frog).isEstivating()) return false;

        // Don't target if in love (breeding takes priority)
        if (frog.isInLove()) {
            return false;
        }

        if (WardenRoleHelper.isWarden(frog) && this.target != null && !WardenRoleHelper.targetWithinAnchor(frog, this.target)) {
            return false;
        }

        return true;
    }
}
