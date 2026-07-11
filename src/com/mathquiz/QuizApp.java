package com.mathquiz;

import com.mathquiz.view.QuizFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Atelier Arithmetic desktop application.
 * Applies system look-and-feel, then launches QuizFrame on the EDT.
 */
public class QuizApp {
    public static void main(String[] args) {
        // Global Uncaught Exception Handler (captures EDT and thread errors, logs to disk, and alerts user)
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                java.io.File logDir = new java.io.File(com.mathquiz.config.AppConfig.getAppDir() + java.io.File.separator + "logs");
                if (!logDir.exists()) logDir.mkdirs();
                java.io.File logFile = new java.io.File(logDir, "error.log");
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(logFile, true))) {
                    pw.println("=================================================================");
                    pw.println("UNCAUGHT EXCEPTION IN THREAD: " + thread.getName());
                    pw.println("Timestamp: " + new java.util.Date().toString());
                    pw.println("=================================================================");
                    throwable.printStackTrace(pw);
                    pw.println();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Oops! Archie the Owl encountered a tiny calculation hiccup.\n" +
                        "We have saved a report. You can restart or continue playing!\n\n" +
                        "Error Details: " + throwable.getMessage(),
                        "Archie's Code Hiccup 🦉",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            });
        });

        // Enable smooth sub-pixel rendering on supported systems
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            QuizFrame frame = new QuizFrame();
            frame.setVisible(true);
        });
    }
}
