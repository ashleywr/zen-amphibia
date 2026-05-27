package com.sanhiruzu.amphibia;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AmphibiaConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> OPTIMAL_BREEDING_ZONE_TYPE;
    public static final ModConfigSpec.BooleanValue GIVE_PATCHOULI_GUIDE_ON_FIRST_JOIN;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("Guide Book");

        GIVE_PATCHOULI_GUIDE_ON_FIRST_JOIN = builder
            .comment("If true, players receive the Amphibia Field Guide on first login when Patchouli is installed.")
            .define("give_patchouli_guide_on_first_join", true);

        builder.pop();

        builder.push("Genetics");

        OPTIMAL_BREEDING_ZONE_TYPE = builder
            .comment("The Atelier zone type ID (or partial match) required for frogs to breed and produce raw genetic fluid. Matches against RoomData.getZoneTypeId(). Example: 'terrarium' matches zen_atelier:terrarium, etc.")
            .define("optimal_breeding_zone_type", "terrarium");

        builder.pop();

        SPEC = builder.build();
    }
}
