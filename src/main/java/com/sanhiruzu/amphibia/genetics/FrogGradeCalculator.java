package com.sanhiruzu.amphibia.genetics;

public class FrogGradeCalculator {
    public enum Grade {
        D("D", 0xFF6B6B),    // Red
        C("C", 0xFFA500),    // Orange
        B("B", 0xFFD700),    // Gold
        A("A", 0x32CD32),    // Lime Green
        S("S", 0xFF1493);    // Deep Pink (legendary)

        public final String label;
        public final int color;

        Grade(String label, int color) {
            this.label = label;
            this.color = color;
        }
    }

    public static Grade calculateGrade(FrogGenome.Trait trait) {
        int points = getGeneValue(trait.geneA()) + getGeneValue(trait.geneB());
        return pointsToGrade(points);
    }

    private static int getGeneValue(String gene) {
        return switch (gene) {
            case "w" -> 0;
            case "A" -> 1;
            case "B" -> 2;
            case "C" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            case "F" -> 6;
            case "G" -> 7;
            default -> 0;
        };
    }

    private static Grade pointsToGrade(int totalPoints) {
        return switch (totalPoints) {
            case 0, 1 -> Grade.D;
            case 2, 3 -> Grade.C;
            case 4, 5 -> Grade.B;
            case 6, 7 -> Grade.A;
            case 8, 9, 10, 11, 12, 13, 14 -> Grade.S;
            default -> Grade.D;
        };
    }

    // Stat calculations based on grades
    public static double getHealthBonus(Grade grade) {
        return switch (grade) {
            case D -> 0;
            case C -> 2;
            case B -> 4;
            case A -> 6;
            case S -> 8;
        };
    }

    public static double getDamageBonus(Grade grade) {
        return switch (grade) {
            case D -> 0;
            case C -> 0.5;
            case B -> 1.0;
            case A -> 1.5;
            case S -> 2.5;
        };
    }

    public static String getGradeDescription(Grade grade) {
        return switch (grade) {
            case D -> "Poor";
            case C -> "Common";
            case B -> "Uncommon";
            case A -> "Rare";
            case S -> "Legendary";
        };
    }
}
