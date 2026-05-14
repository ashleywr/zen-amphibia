package com.sanhiruzu.amphibia.compat.patchouli;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Patchouli integration for Amphibia books.
 * Allows external mods to register book data via datapacks.
 *
 * To create a Patchouli book for Amphibia:
 * 1. Create a book JSON at: src/main/resources/data/modid/patchouli_books/bookname.json
 * 2. Add categories and entries in: src/main/resources/data/modid/patchouli_categories/ and entries/
 * 3. Amphibia will auto-detect and load it if Patchouli is present
 */
public class PatchouliCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatchouliCompat.class);
    private static final List<String> DETECTED_BOOKS = new ArrayList<>();
    private static boolean PATCHOULI_LOADED = false;

    public static void checkPatchouliLoaded() {
        try {
            Class.forName("vazkii.patchouli.api.PatchouliAPI");
            PATCHOULI_LOADED = true;
            LOGGER.info("Patchouli detected - data-driven book integration enabled");
        } catch (ClassNotFoundException e) {
            PATCHOULI_LOADED = false;
        }
    }

    /**
     * Check if Patchouli is loaded and available.
     */
    public static boolean isPatchouliLoaded() {
        return PATCHOULI_LOADED;
    }

    /**
     * Register a book datapack location. Plugins should add their book JSON files to:
     * src/main/resources/data/modid/patchouli_books/
     */
    public static void registerBookDatapack(String modId, String bookName) {
        if (!PATCHOULI_LOADED) {
            LOGGER.debug("Patchouli not loaded, book data {} will be ignored", modId + ":" + bookName);
            return;
        }
        DETECTED_BOOKS.add(modId + ":" + bookName);
        LOGGER.debug("Patchouli book data registered: {}", modId + ":" + bookName);
    }

    /**
     * Get list of registered book datapacks.
     */
    public static List<String> getRegisteredBooks() {
        return new ArrayList<>(DETECTED_BOOKS);
    }
}
