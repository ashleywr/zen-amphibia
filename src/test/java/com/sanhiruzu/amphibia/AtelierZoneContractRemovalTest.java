package com.sanhiruzu.amphibia;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AtelierZoneContractRemovalTest {
    private static final Path PROJECT_ROOT = Path.of("").toAbsolutePath();

    @Test
    void activeSourceDoesNotCallRemovedRoomLookupApi() throws IOException {
        List<Path> offenders = javaFilesContaining(
            "getRoomAt",
            "getZoneTypeId",
            "setZoneQualityModifier",
            "ZoneDataStore",
            "getZoneAt(",
            "isZoneType(",
            "com.sanhiruzu.atelier.space.zone"
        );
        assertTrue(offenders.isEmpty(), () -> "Removed room/zone APIs still referenced:\n" + offenders);
    }

    @Test
    void resourcesDoNotAdvertiseAtelierTerrariumRooms() throws IOException {
        List<Path> offenders = resourceFilesContaining(
            "room HUD",
            "zone quality",
            "zone type",
            "Atelier room",
            "getZoneTypeId",
            "frog_terrarium"
        );
        assertTrue(offenders.isEmpty(), () -> "Old terrarium room language remains:\n" + offenders);
    }

    private static List<Path> javaFilesContaining(String... needles) throws IOException {
        try (var walk = Files.walk(PROJECT_ROOT.resolve("src/main/java"))) {
            return walk
                .filter(path -> path.toString().endsWith(".java"))
                .filter(path -> containsAny(path, needles))
                .toList();
        }
    }

    private static List<Path> resourceFilesContaining(String... needles) throws IOException {
        try (var walk = Files.walk(PROJECT_ROOT.resolve("src/main/resources"))) {
            return walk
                .filter(path -> path.toString().endsWith(".json"))
                .filter(path -> containsAny(path, needles))
                .toList();
        }
    }

    private static boolean containsAny(Path path, String[] needles) {
        try {
            String text = Files.readString(path);
            for (String needle : needles) {
                if (text.contains(needle)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path, e);
        }
    }
}
