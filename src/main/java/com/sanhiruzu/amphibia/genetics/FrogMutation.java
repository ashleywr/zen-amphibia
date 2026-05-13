package com.sanhiruzu.amphibia.genetics;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.frog.Frog;

public record FrogMutation(String id, Component displayName, int color) {
    public static final FrogMutation ENDER = new FrogMutation(
        "ender",
        Component.literal("Ender"),
        0xFF1493  // Deep Pink
    );

    public static final FrogMutation[] ALL_MUTATIONS = {
        ENDER
    };

    public static FrogMutation getById(String id) {
        for (FrogMutation mutation : ALL_MUTATIONS) {
            if (mutation.id.equals(id)) {
                return mutation;
            }
        }
        return null;
    }

    public void applyToFrog(Frog frog) {
        switch (this.id) {
            case "ender" -> applyEnderMutation(frog);
        }
    }

    private static void applyEnderMutation(Frog frog) {
        // Mark frog as Ender variant (can be used for rendering and AI)
        frog.getPersistentData().putBoolean("amphibia:ender_mutation", true);

        // Add Ender teleport AI goal if on server
        if (!frog.level().isClientSide) {
            com.sanhiruzu.amphibia.entity.goal.EnderFrogTeleportGoal goal =
                new com.sanhiruzu.amphibia.entity.goal.EnderFrogTeleportGoal(frog, 32.0);
            frog.goalSelector.addGoal(2, goal);
        }
    }

    public static boolean hasEnderMutation(Frog frog) {
        return frog.getPersistentData().getBoolean("amphibia:ender_mutation");
    }
}
