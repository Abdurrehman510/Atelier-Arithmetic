package com.mathquiz.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** BUG-05 / decision Q2 (stars per child) and decision Q4 (Daily Challenge date per child). */
class AppConfigTest {

    @TempDir
    Path dir;

    private AppConfig config() {
        return new AppConfig(dir.resolve("config.properties").toString());
    }

    @Test
    void eachChildHasTheirOwnStars() {
        AppConfig config = config();
        config.addProfile("Asha");
        config.addProfile("Ben");
        config.setCurrentProfile("Asha");
        config.setStarBalance(30);

        config.setCurrentProfile("Ben");
        assertEquals(0, config.getStarBalance());
        config.setStarBalance(5);

        config.setCurrentProfile("Asha");
        assertEquals(30, config.getStarBalance());
        assertEquals(30, config().getStarBalance(), "survives a restart");
    }

    @Test
    void eachChildHasTheirOwnDailyChallengeDate() {
        AppConfig config = config();
        config.setCurrentProfile("Asha");
        config.setLastDailyChallengeDate("20261115");
        config.setCurrentProfile("Ben");
        assertEquals("", config.getLastDailyChallengeDate());
        config.setCurrentProfile("Asha");
        assertEquals("20261115", config.getLastDailyChallengeDate());
    }

    @Test
    void theOldSharedBalanceAndDateMoveToTheActiveChildOnce() throws Exception {
        // a config written by app 1.0.0: shared keys, plain text (load() accepts unencrypted files)
        Files.writeString(dir.resolve("config.properties"),
                "profiles=Asha,Ben\ncurrentProfile=Asha\nstarBalance=42\nlastDailyChallengeDate=20261114\n",
                StandardCharsets.UTF_8);

        AppConfig upgraded = config();
        assertEquals(42, upgraded.getStarBalance(), "legacy stars preserved for the active child");
        assertEquals("20261114", upgraded.getLastDailyChallengeDate());
        upgraded.setCurrentProfile("Ben");
        assertEquals(0, upgraded.getStarBalance(), "other children start from zero");
        upgraded.setCurrentProfile("Asha");

        AppConfig restarted = config();
        assertEquals(42, restarted.getStarBalance(), "migrated once, not twice");
        restarted.setCurrentProfile("Ben");
        assertEquals(0, restarted.getStarBalance());
    }
}
