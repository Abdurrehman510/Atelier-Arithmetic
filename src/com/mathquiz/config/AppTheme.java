package com.mathquiz.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JComponent;

/**
 * Handles semantic color tokens for Atelier Arithmetic.
 * Switches between Light and Dark palettes based on the dark mode setting.
 *
 * Phase 3 — Engagement & Gamification.
 */
public class AppTheme {

    private static boolean darkMode = false;

    // ── Light Palette (Premium Editorial Off-White) ───────────────────────────
    public static final Color LIGHT_BG_PRIMARY    = new Color(250, 249, 246); // Cream parchment
    public static final Color LIGHT_BG_CARD       = Color.WHITE;
    public static final Color LIGHT_TEXT_DARK     = new Color(28, 25, 23);    // Warm charcoal
    public static final Color LIGHT_TEXT_MUTED    = new Color(120, 113, 108); // Warm stone grey
    public static final Color LIGHT_BORDER_CLR    = new Color(230, 227, 220);
    public static final Color LIGHT_ACCENT_GOLD   = new Color(184, 150, 110); // Bronze gold

    // ── Dark Palette (Premium Luxury Dark) ────────────────────────────────────
    public static final Color DARK_BG_PRIMARY     = new Color(24, 24, 28);    // Rich graphite charcoal
    public static final Color DARK_BG_CARD        = new Color(36, 36, 40);    // Warm dark grey card
    public static final Color DARK_TEXT_DARK      = new Color(242, 242, 247); // Clean white-smoke
    public static final Color DARK_TEXT_MUTED     = new Color(156, 163, 175); // Muted silver grey
    public static final Color DARK_BORDER_CLR     = new Color(63, 63, 70);    // Subtle boundary line
    public static final Color DARK_ACCENT_GOLD    = new Color(220, 185, 115); // Luxurious gold accent


    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    // ── Semantic Selectors ────────────────────────────────────────────────────

    public static Color getBgPrimary()  { return darkMode ? DARK_BG_PRIMARY  : LIGHT_BG_PRIMARY; }
    public static Color getBgCard()     { return darkMode ? DARK_BG_CARD     : LIGHT_BG_CARD; }
    public static Color getTextDark()   { return darkMode ? DARK_TEXT_DARK   : LIGHT_TEXT_DARK; }
    public static Color getTextMuted()  { return darkMode ? DARK_TEXT_MUTED  : LIGHT_TEXT_MUTED; }
    public static Color getBorderClr()  { return darkMode ? DARK_BORDER_CLR  : LIGHT_BORDER_CLR; }
    public static Color getAccentGold() { return darkMode ? DARK_ACCENT_GOLD : LIGHT_ACCENT_GOLD; }

    /** Recursively scales all component fonts and attaches hover/click sound effects. */
    public static void scaleComponentFont(Component c, double scale, com.mathquiz.service.SoundService sound) {
        if (c == null) return;

        // Automatically instrument button hover and clicks with low-latency synthetic sounds
        if (c instanceof javax.swing.JButton) {
            javax.swing.JButton btn = (javax.swing.JButton) c;
            if (btn.getClientProperty("soundAttached") == null) {
                btn.putClientProperty("soundAttached", Boolean.TRUE);
                btn.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (btn.isEnabled() && sound != null) {
                            sound.playHover();
                        }
                    }
                    @Override
                    public void mousePressed(java.awt.event.MouseEvent e) {
                        if (btn.isEnabled() && sound != null) {
                            sound.playClick();
                        }
                    }
                });
            }
        }

        Font f = c.getFont();
        if (f != null && c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Integer baseSize = (Integer) jc.getClientProperty("baseFontSize");
            if (baseSize == null) {
                baseSize = f.getSize();
                jc.putClientProperty("baseFontSize", baseSize);
            }
            int newSize = (int) Math.round(baseSize * scale);
            // Apply font if size changed
            if (f.getSize() != newSize) {
                jc.setFont(new Font(f.getName(), f.getStyle(), newSize));
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                scaleComponentFont(child, scale, sound);
            }
        }
    }
}
