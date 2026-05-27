package com.sanhiruzu.amphibia;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Ensures every translation key used by this mod exists in en_us.json.
 * Catches missing translations for new blocks, items, UI strings, and data files.
 * Fix by adding the missing keys to src/main/resources/assets/zen_amphibia/lang/en_us.json.
 */
class TranslationCompletenessTest {

    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();
    private static final Path LANG_FILE = PROJECT_ROOT.resolve(
            "src/main/resources/assets/zen_amphibia/lang/en_us.json");
    private static final Path JAVA_SRC = PROJECT_ROOT.resolve("src/main/java");
    private static final Path DATA_DIR = PROJECT_ROOT.resolve("src/main/resources/data/zen_amphibia");

    /** Item classes whose translation key comes from their associated block, not from item.<ns>.<name>. */
    private static final Set<String> BLOCK_ITEM_CLASSES = Set.of(
            "BlockItem", "FrogspawnBlockItem", "FrogportBlockItem");

    /**
     * Keys legitimately present in the lang file but not referenced by mod source code.
     * These are used by convention (Create display source naming), external mod tag display,
     * or NeoForge key-binding category lookup.
     */
    private static final Set<String> EXTERNAL_KEYS = Set.of(
        "zen_amphibia.display_source.frog_genetics",
        "key.categories.zen_amphibia",
        "Zen Amphibia",
        "tag.item.c.ingots.frog_slime",
        "tag.item.c.spawn_creatures",
        "tag.item.c.spawn_eggs"
    );

    @Test
    void allTranslationKeysAreDefined() throws IOException {
        Set<String> defined = loadLangKeys();
        Set<String> required = collectRequiredKeys();

        Set<String> missing = new TreeSet<>(required);
        missing.removeAll(defined);

        if (!missing.isEmpty()) {
            fail("Missing translation keys in en_us.json:\n" +
                    missing.stream().map(k -> "  - " + k).collect(Collectors.joining("\n")));
        }
    }

    @Test
    void noOrphanedTranslationKeys() throws IOException {
        Set<String> defined = loadLangKeys();
        Set<String> referenced = collectRequiredKeys();

        Set<String> orphaned = new TreeSet<>(defined);
        orphaned.removeAll(referenced);
        orphaned.removeAll(EXTERNAL_KEYS);

        if (!orphaned.isEmpty()) {
            fail("Orphaned translation keys in en_us.json (not referenced anywhere in source):\n" +
                    orphaned.stream().map(k -> "  - " + k).collect(Collectors.joining("\n")));
        }
    }

    private Set<String> loadLangKeys() throws IOException {
        String json = Files.readString(LANG_FILE);
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("\"([^\"]+)\"\\s*:").matcher(json);
        while (m.find()) {
            keys.add(m.group(1));
        }
        return keys;
    }

    private Set<String> collectRequiredKeys() throws IOException {
        List<Path> javaSources = listFiles(JAVA_SRC, ".java");

        // Must collect block item names from all files before deriving item keys
        Set<String> blockItemNames = new HashSet<>();
        for (Path src : javaSources) {
            blockItemNames.addAll(extractBlockItemNames(Files.readString(src)));
        }

        Set<String> keys = new TreeSet<>();
        for (Path src : javaSources) {
            String text = Files.readString(src);
            keys.addAll(extractBlockRegistryKeys(text));
            // Block helper methods (e.g. registerGeneticFroglight) only appear in AmphibiaBlocks.java
            if (src.getFileName().toString().equals("AmphibiaBlocks.java")) {
                keys.addAll(extractBlockHelperKeys(text));
            }
            keys.addAll(extractItemRegistryKeys(text, blockItemNames));
            keys.addAll(extractLiteralTranslationKeys(text));
        }

        if (Files.exists(DATA_DIR)) {
            for (Path json : listFiles(DATA_DIR, ".json")) {
                keys.addAll(extractJsonDisplayNameKeys(Files.readString(json)));
            }
        }

        return keys;
    }

    private List<Path> listFiles(Path root, String suffix) throws IOException {
        try (var walk = Files.walk(root)) {
            return walk.filter(p -> p.toString().endsWith(suffix)).collect(Collectors.toList());
        }
    }

    /** BLOCKS.register("name", ...) → block.zen_amphibia.name */
    private Set<String> extractBlockRegistryKeys(String src) {
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("BLOCKS\\.register\\(\"([^\"]+)\"").matcher(src);
        while (m.find()) {
            keys.add("block.zen_amphibia." + m.group(1));
        }
        return keys;
    }

    /**
     * Identifies item registration names whose class is a known BlockItem subclass,
     * or that are registered via a registerBlockItem() helper call.
     * These inherit their translation key from the block and do not need an item.* key.
     */
    private Set<String> extractBlockItemNames(String src) {
        Set<String> names = new HashSet<>();

        Matcher helper = Pattern.compile("registerBlockItem\\(\"([^\"]+)\"").matcher(src);
        while (helper.find()) {
            names.add(helper.group(1));
        }

        // ITEMS.register("name", () -> new BlockItemSubclass(...)
        Matcher direct = Pattern.compile("ITEMS\\.register\\(\"([^\"]+)\",\\s*\\(\\)\\s*->\\s*new\\s+(\\w+)").matcher(src);
        while (direct.find()) {
            if (BLOCK_ITEM_CLASSES.contains(direct.group(2))) {
                names.add(direct.group(1));
            }
        }
        return names;
    }

    /** ITEMS.register("name", ...) → item.zen_amphibia.name for all non-block-item registrations. */
    private Set<String> extractItemRegistryKeys(String src, Set<String> blockItemNames) {
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("ITEMS\\.register\\(\"([^\"]+)\"").matcher(src);
        while (m.find()) {
            String name = m.group(1);
            if (!blockItemNames.contains(name)) {
                keys.add("item.zen_amphibia." + name);
            }
        }
        return keys;
    }

    /**
     * Catches every string literal in source that looks like one of our translation keys,
     * regardless of call context (Component.translatable, descriptionId, new KeyMapping,
     * ternary expressions, etc.).  Pattern matches any dot-separated identifier that contains
     * "zen_amphibia" as one of its segments.
     */
    private Set<String> extractLiteralTranslationKeys(String src) {
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("\"([a-z_][a-z_.]*\\.zen_amphibia\\.[a-z_.]+)\"").matcher(src);
        while (m.find()) {
            keys.add(m.group(1));
        }
        return keys;
    }

    /**
     * Finds blocks registered via helper methods like registerGeneticFroglight("name", ...)
     * whose first string argument is the block name but where BLOCKS.register is called
     * internally with a variable (not a string literal), so extractBlockRegistryKeys misses them.
     * Any register*("name", ...) call → block.zen_amphibia.name.
     */
    private Set<String> extractBlockHelperKeys(String src) {
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("register[A-Z]\\w*\\(\\s*\"([^\"]+)\"").matcher(src);
        while (m.find()) {
            keys.add("block.zen_amphibia." + m.group(1));
        }
        return keys;
    }

    /** "display_name": "key" fields in Atelier zone/room_type JSON data files. */
    private Set<String> extractJsonDisplayNameKeys(String json) {
        Set<String> keys = new TreeSet<>();
        Matcher m = Pattern.compile("\"display_name\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
        while (m.find()) {
            String key = m.group(1);
            if (key.contains("zen_amphibia")) {
                keys.add(key);
            }
        }
        return keys;
    }
}
