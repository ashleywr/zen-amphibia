package com.sanhiruzu.amphibia.genetics;

import java.util.HashMap;
import java.util.Map;

public class FrogGeneRegistry {
    public static final String HEAT_TOLERANCE = "heat_tolerance";
    public static final String SLIME_VISCOSITY = "slime_viscosity";
    public static final String GROWTH_RATE = "growth_rate";
    public static final String HEALTH = "health";
    public static final String DAMAGE = "damage";
    public static final String SIZE = "size";

    private static final Map<String, String> GENE_DESCRIPTIONS = new HashMap<>();

    static {
        GENE_DESCRIPTIONS.put(HEAT_TOLERANCE, "Heat Tolerance");
        GENE_DESCRIPTIONS.put(SLIME_VISCOSITY, "Slime Viscosity");
        GENE_DESCRIPTIONS.put(GROWTH_RATE, "Growth Rate");
        GENE_DESCRIPTIONS.put(HEALTH, "Health");
        GENE_DESCRIPTIONS.put(DAMAGE, "Damage");
        GENE_DESCRIPTIONS.put(SIZE, "Size");
    }

    public static String getDisplayName(String geneId) {
        return GENE_DESCRIPTIONS.getOrDefault(geneId, geneId);
    }

    public static boolean isKnownGene(String geneId) {
        return GENE_DESCRIPTIONS.containsKey(geneId);
    }
}
