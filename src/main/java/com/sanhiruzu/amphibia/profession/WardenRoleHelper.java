package com.sanhiruzu.amphibia.profession;

import com.sanhiruzu.amphibia.genetics.FrogCombatCapability;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.register.AmphibiaAttachments;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;

public final class WardenRoleHelper {
    public static final String TAMED_BY_KEY = "zen_amphibia:tamed_by";
    public static final String WARDEN_ROLE_KEY = "zen_amphibia:warden_role";
    public static final String WARDEN_ANCHOR_X_KEY = "zen_amphibia:warden_anchor_x";
    public static final String WARDEN_ANCHOR_Y_KEY = "zen_amphibia:warden_anchor_y";
    public static final String WARDEN_ANCHOR_Z_KEY = "zen_amphibia:warden_anchor_z";
    public static final String SELECTED_WARDEN_KEY = "zen_amphibia:selected_warden";
    public static final int WARDEN_RADIUS = 12;
    public static final int WARDEN_ASSIGNMENT_RANGE = 32;

    private WardenRoleHelper() {
    }

    public static boolean canBecomeWarden(Frog frog) {
        FrogGenome genome = frog.getData(AmphibiaAttachments.FROG_GENOME);
        return FrogCombatCapability.canFight(genome);
    }

    public static boolean isWarden(Frog frog) {
        return frog.getPersistentData().getBoolean(WARDEN_ROLE_KEY);
    }

    public static void markAsWarden(Frog frog) {
        frog.getPersistentData().putBoolean(WARDEN_ROLE_KEY, true);
        frog.setPersistenceRequired();
    }

    public static Optional<UUID> getOwnerUuid(Frog frog) {
        String owner = frog.getPersistentData().getString(TAMED_BY_KEY);
        if (owner.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(owner));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static boolean isOwnedBy(Frog frog, Player player) {
        return getOwnerUuid(frog).map(player.getUUID()::equals).orElse(false);
    }

    public static boolean canClaim(Player player, Frog frog) {
        return getOwnerUuid(frog).isEmpty() || isOwnedBy(frog, player);
    }

    public static void claimFor(Player player, Frog frog) {
        frog.getPersistentData().putString(TAMED_BY_KEY, player.getUUID().toString());
        frog.setPersistenceRequired();
    }

    public static void setSelectedWarden(Player player, Frog frog) {
        player.getPersistentData().putUUID(SELECTED_WARDEN_KEY, frog.getUUID());
    }

    public static Optional<Frog> getSelectedWarden(ServerLevel level, Player player) {
        if (!player.getPersistentData().hasUUID(SELECTED_WARDEN_KEY)) {
            return Optional.empty();
        }

        UUID selectedId = player.getPersistentData().getUUID(SELECTED_WARDEN_KEY);
        Entity entity = level.getEntity(selectedId);
        if (entity instanceof Frog frog && isWarden(frog) && isOwnedBy(frog, player)) {
            return Optional.of(frog);
        }

        return Optional.empty();
    }

    public static void assignAnchor(Frog frog, BlockPos pos) {
        frog.getPersistentData().putInt(WARDEN_ANCHOR_X_KEY, pos.getX());
        frog.getPersistentData().putInt(WARDEN_ANCHOR_Y_KEY, pos.getY());
        frog.getPersistentData().putInt(WARDEN_ANCHOR_Z_KEY, pos.getZ());
        applyAnchorRestriction(frog);
    }

    public static Optional<BlockPos> getAnchor(Frog frog) {
        if (!frog.getPersistentData().contains(WARDEN_ANCHOR_X_KEY)) {
            return Optional.empty();
        }

        return Optional.of(new BlockPos(
            frog.getPersistentData().getInt(WARDEN_ANCHOR_X_KEY),
            frog.getPersistentData().getInt(WARDEN_ANCHOR_Y_KEY),
            frog.getPersistentData().getInt(WARDEN_ANCHOR_Z_KEY)
        ));
    }

    public static void applyAnchorRestriction(Frog frog) {
        Optional<BlockPos> anchor = getAnchor(frog);
        if (anchor.isPresent()) {
            frog.restrictTo(anchor.get(), WARDEN_RADIUS);
        } else {
            frog.clearRestriction();
        }
    }

    public static boolean targetWithinAnchor(Frog frog, Entity target) {
        return getAnchor(frog)
            .map(anchor -> anchor.closerToCenterThan(target.position(), WARDEN_RADIUS + 1.0))
            .orElse(true);
    }

    public static boolean hasGoal(GoalSelector selector, Class<? extends Goal> goalClass) {
        for (WrappedGoal wrappedGoal : selector.getAvailableGoals()) {
            if (goalClass.isInstance(wrappedGoal.getGoal())) {
                return true;
            }
        }

        return false;
    }
}
