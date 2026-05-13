package com.sanhiruzu.amphibia.genetics;

import net.minecraft.ChatFormatting;

public enum Gene {
	HEAT_TOLERANCE(0, "Heat Tolerance", ChatFormatting.GOLD),
	SLIME_VISCOSITY(1, "Slime Viscosity", ChatFormatting.GREEN),
	GROWTH_RATE(2, "Growth Rate", ChatFormatting.AQUA),
	HEALTH(3, "Health", ChatFormatting.WHITE),
	DAMAGE(4, "Damage", ChatFormatting.WHITE),
	SIZE(5, "Size", ChatFormatting.LIGHT_PURPLE);

	public final int index;
	public final String displayName;
	public final ChatFormatting color;

	Gene(int index, String displayName, ChatFormatting color) {
		this.index = index;
		this.displayName = displayName;
		this.color = color;
	}

	public static Gene fromString(String name) {
		for (Gene gene : values()) {
			if (gene.displayName.equalsIgnoreCase(name)) {
				return gene;
			}
		}
		return null;
	}
}
