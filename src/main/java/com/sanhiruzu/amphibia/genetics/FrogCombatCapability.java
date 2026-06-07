package com.sanhiruzu.amphibia.genetics;

public class FrogCombatCapability {

    public static FrogAttackType getAttackType(FrogGenome genome) {
        if (genome == null) return FrogAttackType.NONE;

        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.POWER));

        return switch(damageGrade) {
            case D, C -> FrogAttackType.NONE;
            case B -> FrogAttackType.BITE;
            case A -> FrogAttackType.TONGUE;
            case S -> FrogAttackType.BOTH;
        };
    }

    public static boolean canFight(FrogGenome genome) {
        return getAttackType(genome) != FrogAttackType.NONE;
    }

    public static boolean canPoison(FrogGenome genome) {
        if (genome == null) return false;
        FrogGradeCalculator.Grade viscosityGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
        return viscosityGrade.ordinal() >= FrogGradeCalculator.Grade.B.ordinal();
    }

    public static double getHealthBonus(FrogGenome genome) {
        if (genome == null) return 0.0;
        FrogGradeCalculator.Grade healthGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.HARDINESS));
        return FrogGradeCalculator.getHealthBonus(healthGrade);
    }

    public static double getDamageBonus(FrogGenome genome) {
        if (genome == null) return 0.0;
        FrogGradeCalculator.Grade damageGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.POWER));
        return FrogGradeCalculator.getDamageBonus(damageGrade);
    }
}
