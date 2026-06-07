package com.sanhiruzu.amphibia.genetics;

import net.minecraft.ChatFormatting;

public enum Gene {
	POWER(0, "power", "Power", Layer.APTITUDE, ChatFormatting.RED),
	HARDINESS(1, "hardiness", "Hardiness", Layer.APTITUDE, ChatFormatting.GOLD),
	QUICKNESS(2, "quickness", "Quickness", Layer.APTITUDE, ChatFormatting.AQUA),
	CUNNING(3, "cunning", "Cunning", Layer.APTITUDE, ChatFormatting.DARK_GREEN),
	AWARENESS(4, "awareness", "Awareness", Layer.APTITUDE, ChatFormatting.YELLOW),
	TEMPERAMENT(5, "temperament", "Temperament", Layer.APTITUDE, ChatFormatting.LIGHT_PURPLE),
	AFFINITY(6, "affinity", "Affinity", Layer.APTITUDE, ChatFormatting.BLUE),
	ATTUNEMENT(7, "attunement", "Attunement", Layer.APTITUDE, ChatFormatting.DARK_PURPLE),

	SIZE(8, "size", "Size", Layer.PHENOTYPE, ChatFormatting.LIGHT_PURPLE),
	COLORATION(9, "coloration", "Coloration", Layer.PHENOTYPE, ChatFormatting.GREEN),
	TONGUE_LENGTH(10, "tongue_length", "Tongue Length", Layer.PHENOTYPE, ChatFormatting.AQUA),
	SLIME_YIELD(11, "slime_yield", "Slime Yield", Layer.PHENOTYPE, ChatFormatting.GREEN),

	HEAT_TOLERANCE(12, "heat_tolerance", "Heat Tolerance", Layer.ECOLOGY, ChatFormatting.GOLD),
	HUMIDITY_TOLERANCE(13, "humidity_tolerance", "Humidity Tolerance", Layer.ECOLOGY, ChatFormatting.BLUE);

	public final int index;
	public final String id;
	public final String displayName;
	public final Layer layer;
	public final ChatFormatting color;

	Gene(int index, String id, String displayName, Layer layer, ChatFormatting color) {
		this.index = index;
		this.id = id;
		this.displayName = displayName;
		this.layer = layer;
		this.color = color;
	}

	public static Gene fromString(String name) {
		for (Gene gene : values()) {
			if (gene.id.equalsIgnoreCase(name)
				|| gene.name().equalsIgnoreCase(name)
				|| gene.displayName.equalsIgnoreCase(name)) {
				return gene;
			}
		}
		return null;
	}

	public enum Layer {
		APTITUDE("Aptitudes", ChatFormatting.GOLD),
		PHENOTYPE("Phenotypes", ChatFormatting.GREEN),
		ECOLOGY("Ecology", ChatFormatting.AQUA);

		public final String displayName;
		public final ChatFormatting color;

		Layer(String displayName, ChatFormatting color) {
			this.displayName = displayName;
			this.color = color;
		}
	}
}
