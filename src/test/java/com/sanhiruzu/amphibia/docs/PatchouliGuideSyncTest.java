package com.sanhiruzu.amphibia.docs;

import com.sanhiruzu.amphibia.genetics.FrogDropConstants;
import com.sanhiruzu.amphibia.genetics.FrogHappinessConstants;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PatchouliGuideSyncTest {
    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();
    private static final Path GUIDE_ROOT = PROJECT_ROOT.resolve("src/main/resources/assets/zen_amphibia/patchouli_books/field_guide/en_us");

    @Test
    void guideMentionsEveryGeneDisplayName() throws IOException {
        String guideText = readGuideText();
        String geneSource = Files.readString(PROJECT_ROOT.resolve("src/main/java/com/sanhiruzu/amphibia/genetics/Gene.java"));

        var matcher = Pattern.compile("\\b[A-Z_]+\\(\\d+,\\s*\"[^\"]+\",\\s*\"([^\"]+)\"").matcher(geneSource);
        int checked = 0;
        while (matcher.find()) {
            checked++;
            String displayName = normalize(matcher.group(1));
            assertTrue(guideText.contains(displayName), () -> "Patchouli guide does not mention gene: " + matcher.group(1));
        }

        assertTrue(checked > 0, "No genes were discovered in Gene.java");
    }

    @Test
    void guideMentionsEveryMutationDisplayName() throws IOException {
        String guideText = readGuideText();
        String mutationSource = Files.readString(PROJECT_ROOT.resolve("src/main/java/com/sanhiruzu/amphibia/genetics/FrogMutation.java"));

        var matcher = Pattern.compile("new FrogMutation\\(\\s*\"([^\"]+)\",\\s*Component\\.literal\\(\"([^\"]+)\"\\)").matcher(mutationSource);
        int checked = 0;
        while (matcher.find()) {
            checked++;
            String id = normalize(matcher.group(1).replace('_', ' '));
            String displayName = normalize(matcher.group(2));
            assertTrue(guideText.contains(id) || guideText.contains(displayName),
                () -> "Patchouli guide does not mention mutation: " + matcher.group(1));
        }

        assertTrue(checked > 0, "No mutations were discovered in FrogMutation.java");
    }

    @Test
    void dropConstantsAreMirroredInGuide() throws IOException {
        String raw = readRawGuideText();

        assertMentioned(raw, Float.toString(FrogDropConstants.GRADE_GATE_THRESHOLD),
            "GRADE_GATE_THRESHOLD (" + FrogDropConstants.GRADE_GATE_THRESHOLD + ")");
        assertMentioned(raw, Float.toString(FrogDropConstants.BONUS_THRESHOLD),
            "BONUS_THRESHOLD (" + FrogDropConstants.BONUS_THRESHOLD + ")");
        assertMentioned(raw, Float.toString(FrogDropConstants.ENHANCED_DROP_THRESHOLD),
            "ENHANCED_DROP_THRESHOLD (" + FrogDropConstants.ENHANCED_DROP_THRESHOLD + ")");
        assertMentioned(raw, Integer.toString(FrogDropConstants.GRADE_B_DROP_COUNT),
            "GRADE_B_DROP_COUNT (" + FrogDropConstants.GRADE_B_DROP_COUNT + ")");
        assertMentioned(raw, Integer.toString(FrogDropConstants.GRADE_A_DROP_COUNT),
            "GRADE_A_DROP_COUNT (" + FrogDropConstants.GRADE_A_DROP_COUNT + ")");
        assertMentioned(raw, Integer.toString(FrogDropConstants.GRADE_S_DROP_COUNT),
            "GRADE_S_DROP_COUNT (" + FrogDropConstants.GRADE_S_DROP_COUNT + ")");
    }

    @Test
    void happinessConstantsAreMirroredInGuide() throws IOException {
        String raw = readRawGuideText();

        assertMentionedAsPct(raw, FrogHappinessConstants.ZONE_QUALITY_WEIGHT,   "ZONE_QUALITY_WEIGHT");
        assertMentionedAsPct(raw, FrogHappinessConstants.WATER_RATIO_WEIGHT,    "WATER_RATIO_WEIGHT");
        assertMentionedAsPct(raw, FrogHappinessConstants.PLANT_SCORE_WEIGHT,    "PLANT_SCORE_WEIGHT");
        assertMentionedAsPct(raw, FrogHappinessConstants.SIZE_SCORE_WEIGHT,     "SIZE_SCORE_WEIGHT");
        assertMentionedAsPct(raw, FrogHappinessConstants.BREEDING_HAPPINESS_CHANCE_FACTOR,
            "BREEDING_HAPPINESS_CHANCE_FACTOR");
    }

    private static void assertMentioned(String text, String expected, String constantName) {
        assertTrue(text.contains(expected),
            () -> "Patchouli guide must mention " + constantName + " — expected to find '" + expected + "' in guide text");
    }

    private static void assertMentionedAsPct(String text, float weight, String constantName) {
        String pct = Math.round(weight * 100) + "%";
        assertTrue(text.contains(pct),
            () -> "Patchouli guide must mention " + constantName + " as '" + pct + "'");
    }

    private static String readRawGuideText() throws IOException {
        StringBuilder builder = new StringBuilder();
        try (var paths = Files.walk(GUIDE_ROOT)) {
            paths.filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .forEach(path -> {
                    try {
                        builder.append(Files.readString(path)).append('\n');
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed to read " + path, e);
                    }
                });
        }
        return builder.toString();
    }

    private static String readGuideText() throws IOException {
        return normalize(readRawGuideText());
    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }
}
