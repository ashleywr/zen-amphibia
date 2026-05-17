package com.sanhiruzu.amphibia;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AmphibiaConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> OPTIMAL_BREEDING_ATMOSPHERE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Genetics");
        
        OPTIMAL_BREEDING_ATMOSPHERE = builder
            .comment("The specific Zen Zones atmosphere ID required for frogs to breed and produce raw genetic fluid.")
            .define("optimal_breeding_atmosphere", "monsoon_chamber");
            
        builder.pop();

        SPEC = builder.build();
    }
}
