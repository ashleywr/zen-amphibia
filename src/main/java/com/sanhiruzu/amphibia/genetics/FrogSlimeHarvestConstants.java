package com.sanhiruzu.amphibia.genetics;

public final class FrogSlimeHarvestConstants {
    private FrogSlimeHarvestConstants() {}

    // Minimum happiness for readiness to progress (pauses below this)
    public static final float MIN_HAPPINESS = 0.4f;

    // Happiness multiplier breakpoints
    public static final float HAPPINESS_BONUS_1 = 0.6f;
    public static final float HAPPINESS_BONUS_2 = 0.8f;
    public static final float HAPPINESS_BONUS_3 = 0.9f;

    // Readiness/recovery cycle durations in game ticks (B=2 days, A=1.5, S=1)
    public static final int TICKS_B = 48000;
    public static final int TICKS_A = 36000;
    public static final int TICKS_S = 24000;

    // Overcrowding: > OVERCROWD_MAX frogs (including self) within this radius suppresses readiness
    public static final double OVERCROWD_RADIUS = 5.0;
    public static final int OVERCROWD_MAX = 4;

    // S-grade bonus yield: at high happiness, small chance of +1
    public static final float S_BONUS_HAPPINESS = 0.9f;
    public static final float S_BONUS_CHANCE = 0.30f;

    // How often (in game ticks) the readiness ticker fires per frog
    public static final int TICK_INTERVAL = 20;

    public static float progressPerInterval(FrogGradeCalculator.Grade grade) {
        int cycleTicks = switch (grade) {
            case B -> TICKS_B;
            case A -> TICKS_A;
            case S -> TICKS_S;
            default -> Integer.MAX_VALUE;
        };
        return (float) TICK_INTERVAL / cycleTicks;
    }

    public static float happinessMultiplier(float happiness) {
        if (happiness < HAPPINESS_BONUS_1) return 1.0f;
        if (happiness < HAPPINESS_BONUS_2) return 1.15f;
        if (happiness < HAPPINESS_BONUS_3) return 1.25f;
        return 1.35f;
    }
}
