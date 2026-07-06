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
