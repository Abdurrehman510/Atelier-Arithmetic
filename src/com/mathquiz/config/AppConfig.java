package com.mathquiz.config;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Persists lightweight user preferences to a .properties file stored in the
 * user's home directory at ~/.atelier-arithmetic/config.properties.
 *
 * Current keys:
 *   tourSeen      = true/false   — whether the first-launch tour has been shown
 *   soundEnabled  = true/false   — sound effects toggle (used in Phase 3)
 */
public class AppConfig {

    private static final String APP_DIR  = System.getProperty("user.home") + File.separator + ".atelier-arithmetic";
    private static final String CFG_FILE = APP_DIR + File.separator + "config.properties";

    private final Properties props = new Properties();
    private final String cfgFile;

    private static AppConfig instance;

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public AppConfig() {
        this(CFG_FILE);
    }

    /** A config backed by an explicit file (tests). The file's folder is created if missing. */
    public AppConfig(String cfgFile) {
        this.cfgFile = cfgFile;
        ensureDirectoryExists();
        load();
        migrateSharedKeysToActiveProfile();
    }

    /** Keys that app 1.0.0 stored once for the whole computer and that are now per profile (BUG-05, Q2/Q4). */
    private static final String[] FORMERLY_SHARED_KEYS = {"starBalance", "lastDailyChallengeDate"};

    /**
     * One-time upgrade: a shared value from app 1.0.0 moves to the profile that is active at the first
     * start of this version (so no stars are lost); other profiles start fresh. The shared key is removed.
     */
    private void migrateSharedKeysToActiveProfile() {
        boolean changed = false;
        for (String key : FORMERLY_SHARED_KEYS) {
            String shared = props.getProperty(key);
            if (shared != null) {
                if (props.getProperty(profileKey(key)) == null) {
                    props.setProperty(profileKey(key), shared);
                }
                props.remove(key);
                changed = true;
            }
        }
        if (changed) {
            save();
        }
    }

    /** {@code base.<current profile>}, the convention for per-profile keys (rules.md R-DAT-05). */
    private String profileKey(String base) {
        return base + "." + getCurrentProfile();
    }


    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public boolean isTourSeen() {
        return Boolean.parseBoolean(props.getProperty("tourSeen", "false"));
    }

    public void setTourSeen(boolean seen) {
        props.setProperty("tourSeen", Boolean.toString(seen));
        save();
    }

    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(props.getProperty("soundEnabled", "true"));
    }

    public void setSoundEnabled(boolean enabled) {
        props.setProperty("soundEnabled", Boolean.toString(enabled));
        save();
    }

    public int getSoundVolume() {
        try {
            return Integer.parseInt(props.getProperty("soundVolume", "70"));
        } catch (NumberFormatException e) {
            return 70;
        }
    }

    public void setSoundVolume(int volume) {
        props.setProperty("soundVolume", Integer.toString(volume));
        save();
    }

    public boolean isDarkMode() {
        return Boolean.parseBoolean(props.getProperty("darkMode", "false"));
    }

    public void setDarkMode(boolean enabled) {
        props.setProperty("darkMode", Boolean.toString(enabled));
        save();
    }

    /** Day ({@code yyyyMMdd}) the current profile last completed the Daily Challenge; "" if never. */
    public String getLastDailyChallengeDate() {
        return props.getProperty(profileKey("lastDailyChallengeDate"), "");
    }

    public void setLastDailyChallengeDate(String dateStr) {
        props.setProperty(profileKey("lastDailyChallengeDate"), dateStr);
        save();
    }


    public String getCurrentProfile() {
        return props.getProperty("currentProfile", "Guest");
    }

    public void setCurrentProfile(String profile) {
        props.setProperty("currentProfile", profile);
        save();
    }

    public java.util.List<String> getProfiles() {
        String raw = props.getProperty("profiles", "Guest");
        String[] parts = raw.split(",");
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        if (list.isEmpty()) {
            list.add("Guest");
        }
        return list;
    }

    public void addProfile(String profile) {
        java.util.List<String> list = getProfiles();
        if (!list.contains(profile)) {
            list.add(profile);
            props.setProperty("profiles", String.join(",", list));
            save();
        }
    }

    public double getFontSizeScale() {
        try {
            return Double.parseDouble(props.getProperty("fontSizeScale", "1.25"));
        } catch (NumberFormatException e) {
            return 1.25;
        }
    }

    public void setFontSizeScale(double scale) {
        props.setProperty("fontSizeScale", Double.toString(scale));
        save();
    }

    // ── Star Reward Economy ───────────────────────────────────────────────────

    public int getStarBalance() {
        try {
            return Integer.parseInt(props.getProperty(profileKey("starBalance"), "0"));   // per profile (BUG-05)
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setStarBalance(int balance) {
        props.setProperty(profileKey("starBalance"), Integer.toString(Math.max(0, balance)));
        save();
    }

    /** Comma-separated list of unlocked shop item IDs for the current profile. */
    public java.util.List<String> getUnlockedItems() {
        String raw = props.getProperty("unlockedItems." + getCurrentProfile(), "");
        java.util.List<String> list = new java.util.ArrayList<>();
        if (!raw.isEmpty()) {
            for (String id : raw.split(",")) {
                String trimmed = id.trim();
                if (!trimmed.isEmpty()) list.add(trimmed);
            }
        }
        return list;
    }

    public void addUnlockedItem(String itemId) {
        java.util.List<String> list = getUnlockedItems();
        if (!list.contains(itemId)) {
            list.add(itemId);
            props.setProperty("unlockedItems." + getCurrentProfile(), String.join(",", list));
            save();
        }
    }

    public String getEquippedItem() {
        return props.getProperty("equippedItem." + getCurrentProfile(), "none");
    }

    public void setEquippedItem(String itemId) {
        props.setProperty("equippedItem." + getCurrentProfile(), itemId);
        save();
    }

    public void resetProfileCustomizations(String profile) {
        props.remove("unlockedItems." + profile);
        props.remove("equippedItem." + profile);
        save();
    }


    // -------------------------------------------------------------------------
    // I/O helpers
    // -------------------------------------------------------------------------

    private void ensureDirectoryExists() {
        try {
            Path parent = Paths.get(cfgFile).toAbsolutePath().getParent();
            Files.createDirectories(parent != null ? parent : Paths.get(APP_DIR));
        } catch (IOException ignored) {}
    }

    private void load() {
        File f = new File(cfgFile);
        if (!f.exists()) return;
        try {
            byte[] bytes = Files.readAllBytes(f.toPath());
            String fileContent = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            String decrypted = com.mathquiz.util.CryptoHelper.decrypt(fileContent);
            try (StringReader sr = new StringReader(decrypted)) {
                props.load(sr);
            }
        } catch (IOException e) {
            System.err.println("Failed to decrypt and load config: " + e.getMessage());
        }
    }

    private void save() {
        try {
            StringWriter sw = new StringWriter();
            props.store(sw, "Atelier Arithmetic — Secure Config");
            String plainText = sw.toString();
            String cipherText = com.mathquiz.util.CryptoHelper.encrypt(plainText);
            try (FileOutputStream fos = new FileOutputStream(cfgFile)) {
                fos.write(cipherText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.err.println("Failed to encrypt and save config: " + e.getMessage());
        }
    }

    /** Returns the path used to store app data (history, config). */
    public static String getAppDir() {
        return APP_DIR;
    }
}
