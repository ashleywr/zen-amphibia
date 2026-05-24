package com.sanhiruzu.amphibia;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AmphibiaConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> OPTIMAL_BREEDING_ZONE_TYPE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Genetics");

        OPTIMAL_BREEDING_ZONE_TYPE = builder
            .comment("The Atelier zone type ID (or partial match) required for frogs to breed and produce raw genetic fluid. Matches against RoomData.getZoneTypeId(). Example: 'terrarium' matches zen_atelier:terrarium, etc.")
            .define("optimal_breeding_zone_type", "terrarium");

        builder.pop();

        SPEC = builder.build();
    }
}
