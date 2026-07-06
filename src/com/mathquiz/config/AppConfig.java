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
        try (InputStream in = new FileInputStream(f)) {
            props.load(in);
        } catch (IOException ignored) {}
    }

    private void save() {
        try (OutputStream out = new FileOutputStream(CFG_FILE)) {
            props.store(out, "Atelier Arithmetic — User Configuration");
        } catch (IOException ignored) {}
    }

    /** Returns the path used to store app data (history, config). */
    public static String getAppDir() {
        return APP_DIR;
    }
}
