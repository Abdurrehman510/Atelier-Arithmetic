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

    private static AppConfig instance;

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public AppConfig() {
        ensureDirectoryExists();
        load();
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

    public String getLastDailyChallengeDate() {
        return props.getProperty("lastDailyChallengeDate", "");
    }

    public void setLastDailyChallengeDate(String dateStr) {
        props.setProperty("lastDailyChallengeDate", dateStr);
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
            return Integer.parseInt(props.getProperty("starBalance", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setStarBalance(int balance) {
        props.setProperty("starBalance", Integer.toString(Math.max(0, balance)));
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


    // -------------------------------------------------------------------------
    // I/O helpers
    // -------------------------------------------------------------------------

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(APP_DIR));
        } catch (IOException ignored) {}
    }

    private void load() {
        File f = new File(CFG_FILE);
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
            try (FileOutputStream fos = new FileOutputStream(CFG_FILE)) {
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
