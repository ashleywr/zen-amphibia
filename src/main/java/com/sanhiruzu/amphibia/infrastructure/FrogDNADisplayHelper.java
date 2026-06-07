package com.sanhiruzu.amphibia.infrastructure;

import com.sanhiruzu.amphibia.event.FrogSlimeHarvestHandler;
import com.sanhiruzu.amphibia.genetics.FrogGenome;
import com.sanhiruzu.amphibia.genetics.FrogGradeCalculator;
import com.sanhiruzu.amphibia.genetics.FrogMutation;
import com.sanhiruzu.amphibia.genetics.FrogState;
import com.sanhiruzu.amphibia.genetics.Gene;
import com.sanhiruzu.amphibia.profession.WardenRoleHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.animal.frog.Frog;

public class FrogDNADisplayHelper {

    public static List<Component> getDNATooltip(FrogGenome genome, boolean includeHeader) {
        List<Component> lines = new ArrayList<>();
        if (includeHeader) {
            lines.add(Component.translatable("gui.goggles.zen_amphibia.frog_genetics").withStyle(ChatFormatting.GREEN));
        }

        if (genome == null) {
            lines.add(Component.literal("  No genome data").withStyle(ChatFormatting.RED));
            return lines;
        }

        addGeneLines(lines, genome, true, false);
        return lines;
    }

    public static List<Component> getComprehensiveDNATooltip(FrogGenome genome) {
        List<Component> lines = new ArrayList<>();
        addMutationsSection(lines, genome);
        addGeneticsSection(lines, genome);
        return lines;
    }

    public static List<Component> getBreedingSummaryTooltip(FrogGenome genome) {
        List<Component> lines = new ArrayList<>();
        if (genome == null) return lines;

        List<Gene> rankedGenes = new ArrayList<>(currentBreedingTargets());
        rankedGenes.sort(Comparator
            .comparing((Gene gene) -> FrogGradeCalculator.calculateGrade(genome.getGene(gene)).ordinal())
            .reversed()
            .thenComparing(gene -> gene.displayName));

        lines.add(Component.literal("Breeding targets").withStyle(ChatFormatting.GOLD));
        rankedGenes.stream()
            .filter(gene -> FrogGradeCalculator.calculateGrade(genome.getGene(gene)).ordinal() >= FrogGradeCalculator.Grade.B.ordinal())
            .limit(3)
            .forEach(gene -> {
                FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(genome.getGene(gene));
                MutableComponent line = Component.literal("  ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(gene.displayName + " " + grade.label)
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(grade.color))))
                    .append(Component.literal(" - " + breedingUse(gene)).withStyle(ChatFormatting.DARK_GRAY));
                lines.add(line);
            });

        if (lines.size() == 1) {
            lines.add(Component.literal("  No standout traits yet").withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("  Breed for B, A, or S grades").withStyle(ChatFormatting.GRAY));
        }

        return lines;
    }

    public static List<Component> getBucketPersonalityTooltip(FrogGenome genome) {
        List<Component> lines = new ArrayList<>();
        if (genome == null) return lines;

        addLineIf(lines, gradeAtLeast(genome, Gene.QUICKNESS, FrogGradeCalculator.Grade.A), "Restless in the bucket.");
        addLineIf(lines, gradeAtLeast(genome, Gene.TEMPERAMENT, FrogGradeCalculator.Grade.A), "Calm in the bucket.");
        addLineIf(lines, gradeAtLeast(genome, Gene.SIZE, FrogGradeCalculator.Grade.A), "This bucket feels heavy.");
        addLineIf(lines, gradeAtLeast(genome, Gene.SLIME_YIELD, FrogGradeCalculator.Grade.A), "The inside is suspiciously glossy.");
        addLineIf(lines, gradeAtLeast(genome, Gene.COLORATION, FrogGradeCalculator.Grade.A), "Its colors catch the light.");

        long standoutCount = currentBreedingTargets().stream()
            .filter(gene -> gradeAtLeast(genome, gene, FrogGradeCalculator.Grade.B))
            .count();
        if (hasGrade(genome, FrogGradeCalculator.Grade.S)) {
            addLineIf(lines, true, "Exceptional breeding stock.");
        } else if (standoutCount >= 3) {
            addLineIf(lines, true, "Promising line founder.");
        }

        return lines.stream().limit(3).toList();
    }

    public static List<Component> getFrogDebugTooltip(FrogState state) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("=== FROG INFO ===").withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.literal("Age: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(state.age + " ticks").withStyle(ChatFormatting.YELLOW)));
        lines.add(Component.literal("Scale: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%.2f", state.scale)).withStyle(ChatFormatting.YELLOW)));
        lines.add(Component.literal("Status: ").withStyle(ChatFormatting.GRAY)
            .append(modStatus(state)));
        lines.add(Component.literal("Slime: ").withStyle(ChatFormatting.GRAY)
            .append(state.slimeReady
                ? Component.literal("Ready").withStyle(ChatFormatting.GREEN)
                : Component.literal("Not ready").withStyle(ChatFormatting.DARK_GRAY)));
        if (state.genome != null) {
            Gene dominant = FrogSlimeHarvestHandler.getDominantGene(state.genome);
            String secretionLabel = secretionTypeLabel(state.genome, dominant);
            lines.add(Component.literal("Secretion: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(secretionLabel).withStyle(
                    dominant == Gene.COLORATION ? ChatFormatting.YELLOW : ChatFormatting.WHITE)));
        }
        lines.add(Component.literal("AI: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(state.aiStatus).withStyle(ChatFormatting.DARK_GRAY)));

        addProfessionSection(lines, state.entity);
        addPersistentDataSection(lines, state.entity);
        addMutationsSection(lines, state.genome);
        addGeneticsSection(lines, state.genome);

        return lines;
    }

    public static int getGenomeColor(FrogGenome genome) {
        return genome.getColor();
    }

    static void addMutationsSection(List<Component> lines, FrogGenome genome) {
        if (genome == null || genome.mutations().isEmpty()) return;
        lines.add(Component.empty());
        lines.add(Component.literal("=== MUTATIONS ===").withStyle(ChatFormatting.LIGHT_PURPLE));
        for (String mutationId : genome.mutations()) {
            FrogMutation mutation = FrogMutation.getById(mutationId);
            if (mutation != null) {
                lines.add(Component.literal("  ").append(
                    Component.literal(mutation.displayName().getString())
                        .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(mutation.color())))));
            } else {
                lines.add(Component.literal("  " + mutationId).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    static void addGeneticsSection(List<Component> lines, FrogGenome genome) {
        lines.add(Component.empty());
        lines.add(Component.literal("=== GENETICS ===").withStyle(ChatFormatting.GREEN));

        if (genome == null) {
            lines.add(Component.literal("No genome data").withStyle(ChatFormatting.RED));
            return;
        }

        addGeneLines(lines, genome, false, true);

        int colorHash = getGenomeColor(genome);
        int r = (colorHash >> 16) & 0xFF;
        int g = (colorHash >> 8) & 0xFF;
        int b = colorHash & 0xFF;
        lines.add(Component.literal("Coloration: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(colorName(r, g, b) + " (RGB:" + r + "," + g + "," + b + ")").withStyle(ChatFormatting.WHITE)));
        lines.add(Component.literal("Derived Scale: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.format("%.2f", genome.getScale())).withStyle(ChatFormatting.WHITE)));
    }

    private static void addGeneLines(List<Component> lines, FrogGenome genome, boolean compact, boolean includeGrade) {
        Gene.Layer currentLayer = null;
        for (Gene gene : Gene.values()) {
            if (gene.layer != currentLayer) {
                currentLayer = gene.layer;
                lines.add(Component.literal((compact ? "  " : "") + currentLayer.displayName + ":").withStyle(currentLayer.color));
            }

            var trait = genome.getGene(gene);
            MutableComponent line = Component.literal(compact ? "    " : "  ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(gene.displayName + ": ").withStyle(ChatFormatting.GRAY));
            line = line.append(Component.literal(trait.geneA() + " / " + trait.geneB()).withStyle(gene.color));

            if (includeGrade) {
                FrogGradeCalculator.Grade grade = FrogGradeCalculator.calculateGrade(trait);
                line = line.append(Component.literal(" [" + grade.label + "]")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(grade.color))));
            }

            lines.add(line);
        }
    }

    private static void addProfessionSection(List<Component> lines, Frog frog) {
        lines.add(Component.empty());
        lines.add(Component.literal("=== ROLES ===").withStyle(ChatFormatting.GOLD));

        if (WardenRoleHelper.isWarden(frog)) {
            lines.add(Component.literal("Warden: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("active").withStyle(ChatFormatting.AQUA)));
            WardenRoleHelper.getAnchor(frog).ifPresent(anchor ->
                lines.add(Component.literal("Anchor: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(anchor.getX() + ", " + anchor.getY() + ", " + anchor.getZ())
                        .withStyle(ChatFormatting.YELLOW))));
        } else {
            lines.add(Component.literal("Warden: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("inactive").withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    private static void addPersistentDataSection(List<Component> lines, Frog frog) {
        List<String> keys = frog.getPersistentData().getAllKeys().stream()
            .filter(key -> key.startsWith("zen_amphibia:"))
            .sorted(Comparator.naturalOrder())
            .toList();

        if (keys.isEmpty()) return;

        lines.add(Component.empty());
        lines.add(Component.literal("=== DEBUG DATA ===").withStyle(ChatFormatting.BLUE));
        for (String key : keys) {
            Tag tag = frog.getPersistentData().get(key);
            String shortKey = key.substring("zen_amphibia:".length());
            lines.add(Component.literal(shortKey + ": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(tag == null ? "<null>" : tag.toString()).withStyle(ChatFormatting.WHITE)));
        }
    }

    private static Component modStatus(FrogState state) {
        if (state.maturityProgress < 1.0f) {
            int pct = (int)(state.maturityProgress * 100);
            return Component.literal("Maturing " + pct + "%").withStyle(ChatFormatting.YELLOW);
        }
        if (state.estivating) {
            return Component.literal("Estivating").withStyle(ChatFormatting.AQUA);
        }
        if (state.hasEgg && state.inWater) {
            return Component.literal("Laying eggs").withStyle(ChatFormatting.GREEN);
        }
        if (state.hasEgg) {
            return Component.literal("Finding water to lay eggs").withStyle(ChatFormatting.YELLOW);
        }
        if (state.inLove) {
            return Component.literal("In love").withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        return Component.literal("Idle").withStyle(ChatFormatting.WHITE);
    }

    private static String colorName(int r, int g, int b) {
        if (r > g && r > b) return "Reddish";
        if (g > r && g > b) return "Greenish";
        if (b > r && b > g) return "Bluish";
        if (r > 150 && g > 150 && b < 100) return "Yellowish";
        if (r > 150 && g < 100 && b > 150) return "Magenta";
        if (r < 100 && g > 150 && b > 150) return "Cyan";
        if (r < 100 && g < 100 && b < 100) return "Dark";
        return "Neutral";
    }

    private static String breedingUse(Gene gene) {
        return switch (gene) {
            case SLIME_YIELD -> "more Frog Slime";
            case SIZE -> "larger slime yields";
            case COLORATION -> "froglight colors";
            case QUICKNESS -> "faster workers";
            case TONGUE_LENGTH -> "longer reach";
            case TEMPERAMENT -> "reliable workers";
            case POWER -> "combat damage";
            case HARDINESS -> "more health";
            case HEAT_TOLERANCE -> "hot habitats";
            case HUMIDITY_TOLERANCE -> "wet habitats";
            case CUNNING -> "smart jobs";
            case AWARENESS -> "supervisor jobs";
            case AFFINITY -> "handling jobs";
            case ATTUNEMENT -> "rare jobs";
        };
    }

    private static String secretionTypeLabel(FrogGenome genome, Gene dominant) {
        if (dominant == null) return "Basic";
        return switch (dominant) {
            case COLORATION -> {
                FrogGradeCalculator.Grade yieldGrade = FrogGradeCalculator.calculateGrade(genome.getGene(Gene.SLIME_YIELD));
                yield yieldGrade.ordinal() >= FrogGradeCalculator.Grade.A.ordinal() ? "Luminous (Pigmented)" : "Luminous (White)";
            }
            // future: POWER/HEAT_TOLERANCE → Caustic, HARDINESS/AFFINITY → Preservative, ATTUNEMENT/CUNNING → Mutagenic
            default -> "Basic";
        };
    }

    private static void addLineIf(List<Component> lines, boolean condition, String text) {
        if (condition) {
            lines.add(Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    private static boolean gradeAtLeast(FrogGenome genome, Gene gene, FrogGradeCalculator.Grade minimum) {
        return FrogGradeCalculator.calculateGrade(genome.getGene(gene)).ordinal() >= minimum.ordinal();
    }

    private static boolean hasGrade(FrogGenome genome, FrogGradeCalculator.Grade grade) {
        return currentBreedingTargets().stream()
            .anyMatch(gene -> FrogGradeCalculator.calculateGrade(genome.getGene(gene)) == grade);
    }

    private static List<Gene> currentBreedingTargets() {
        return List.of(
            Gene.SLIME_YIELD,
            Gene.SIZE,
            Gene.COLORATION,
            Gene.QUICKNESS,
            Gene.TONGUE_LENGTH,
            Gene.TEMPERAMENT,
            Gene.POWER,
            Gene.HARDINESS,
            Gene.HEAT_TOLERANCE,
            Gene.HUMIDITY_TOLERANCE
        );
    }
}
