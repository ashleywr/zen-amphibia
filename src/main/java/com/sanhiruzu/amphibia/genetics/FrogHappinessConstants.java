package com.sanhiruzu.amphibia.genetics;

public final class FrogHappinessConstants {
    private FrogHappinessConstants() {}

    // How often happiness is recalculated, in ticks
    public static final int HAPPINESS_UPDATE_INTERVAL = 40;
    // How much happiness decays per interval when outside a terrarium
    public static final float HAPPINESS_DECAY_PER_INTERVAL = 0.05f;

    // Happiness formula weights (must sum to 1.0)
    public static final float ZONE_QUALITY_WEIGHT    = 0.30f;
    public static final float WATER_RATIO_WEIGHT     = 0.25f;
    public static final float CLIMATE_SCORE_WEIGHT   = 0.25f;
    public static final float PLANT_SCORE_WEIGHT     = 0.15f;
    public static final float SIZE_SCORE_WEIGHT      = 0.05f;

    // Number of frog-friendly plants for a full plant score
    public static final int   PLANT_FULL_SCORE_COUNT         = 8;
    // Water coverage is counted relative to (volume * this factor)
    public static final float WATER_RATIO_DENOMINATOR_FACTOR = 0.5f;

    // Per-locus allele upgrade chance multiplier when breeding with happiness bonus
    public static final float BREEDING_HAPPINESS_CHANCE_FACTOR = 0.15f;
}
