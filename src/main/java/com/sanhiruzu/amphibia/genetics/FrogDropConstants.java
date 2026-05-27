package com.sanhiruzu.amphibia.genetics;

public final class FrogDropConstants {
    private FrogDropConstants() {}

    // Minimum happiness for grade-based drops to activate
    public static final float GRADE_GATE_THRESHOLD = 0.4f;
    // Happiness at which grade-based drops gain +1 bonus
    public static final float BONUS_THRESHOLD = 0.9f;
    // Minimum happiness for the additional probabilistic drop
    public static final float ENHANCED_DROP_THRESHOLD = 0.6f;
    // Base chance at ENHANCED_DROP_THRESHOLD, scales to 1.0 at max happiness
    public static final float ENHANCED_DROP_BASE_CHANCE = 0.3f;
    // (1.0 - BASE) / (1.0 - THRESHOLD) = 0.7 / 0.4
    public static final float ENHANCED_DROP_SCALE = 1.75f;

    // Guaranteed drop counts by Slime Viscosity grade (Grade B+)
    public static final int GRADE_B_DROP_COUNT = 1;
    public static final int GRADE_A_DROP_COUNT = 2;
    public static final int GRADE_S_DROP_COUNT = 3;
}
