package com.sanhiruzu.amphibia;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AmphibiaConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue OPTIMAL_BREEDING_HABITAT_THRESHOLD;
    public static final ModConfigSpec.BooleanValue GIVE_PATCHOULI_GUIDE_ON_FIRST_JOIN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Guide Book");

        GIVE_PATCHOULI_GUIDE_ON_FIRST_JOIN = builder
            .comment("If true, players receive the Amphibia Field Guide on first login when Patchouli is installed.")
            .define("give_patchouli_guide_on_first_join", true);

        builder.pop();

        builder.push("Genetics");

        OPTIMAL_BREEDING_HABITAT_THRESHOLD = builder
            .comment("Minimum frog habitat suitability required for frogs to produce raw genetic fluid instead of normal genetic frogspawn.")
            .defineInRange("optimal_breeding_habitat_threshold", 0.5D, 0.0D, 1.0D);

        builder.pop();

        SPEC = builder.build();
    }
}
