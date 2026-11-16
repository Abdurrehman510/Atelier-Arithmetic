package com.mathquiz.view;

import com.mathquiz.config.AppTheme;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import java.awt.Color;

/**
 * Dark "primary" buttons (Start, Submit, Add Question, Back…): the theme's dark colour behind light text.
 * The button paints its own background because the Windows look-and-feel ignores setBackground on a
 * filled button, which showed white text on light grey (BUG-20, BUG-21). Hover and press lighten it
 * slightly. Safe to call again from applyTheme: it re-reads the theme and adds its listener only once.
 */
final class PrimaryButtonStyle {

    private static final String STYLED = "primaryButtonStyle";

    private PrimaryButtonStyle() {
    }

    static void apply(AbstractButton btn) {
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setForeground(AppTheme.getBgCard());
        if (btn.getClientProperty(STYLED) == null) {
            btn.putClientProperty(STYLED, Boolean.TRUE);
            btn.getModel().addChangeListener(e -> shade(btn));
        }
        shade(btn);
    }

    private static void shade(AbstractButton btn) {
        ButtonModel m = btn.getModel();
        double towardCard = m.isPressed() ? 0.30 : (m.isRollover() ? 0.15 : 0.0);
        Color target = mix(AppTheme.getTextDark(), AppTheme.getBgCard(), towardCard);
        if (!target.equals(btn.getBackground())) {
            btn.setBackground(target);
        }
    }

    /** {@code a} moved {@code t} (0–1) of the way toward {@code b}. */
    private static Color mix(Color a, Color b, double t) {
        return new Color(
                (int) Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
    }
}
