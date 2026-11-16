package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.config.AppTheme;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every dark "primary" button on every screen (Start, Submit, Add Question, Back…) must really paint
 * dark under the look-and-feel the app uses, so its light text is readable. The Windows look-and-feel
 * ignores setBackground on a filled button, which painted white text on light grey (BUG-20/21).
 * Text contrast is measured on the painted pixels, at rest and on hover, in light and dark mode.
 */
class DarkButtonContrastTest {

    private static final double MIN_CONTRAST = 4.5;   // WCAG 2.1 AA for normal text

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    @Test
    void darkButtonsAreReadableInLightMode() throws Exception {
        List<String> problems = unreadableDarkButtons(false);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void darkButtonsAreReadableInDarkMode() throws Exception {
        List<String> problems = unreadableDarkButtons(true);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    private static List<String> unreadableDarkButtons(boolean darkMode) throws Exception {
        AppConfig config = AppConfig.getInstance();
        boolean oldDark = config.isDarkMode();
        LookAndFeel oldLaf = UIManager.getLookAndFeel();
        SwingUtilities.invokeAndWait(() -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        config.setDarkMode(darkMode);
        try {
            return FrameHarness.inspect(1024, 700, 1.0, frame -> {
                List<AbstractButton> buttons = new ArrayList<>();
                collectDarkButtons(frame.getContentPane(), buttons);
                List<String> problems = new ArrayList<>();
                List<String> names = new ArrayList<>();
                for (AbstractButton b : buttons) {
                    names.add(b.getText());
                }
                if (!names.contains("SUBMIT ANSWER") || buttons.size() < 6) {
                    problems.add("expected the dark buttons of every screen, found only " + names);
                }
                for (AbstractButton b : buttons) {
                    check(b, "at rest", problems);
                    b.getModel().setRollover(true);
                    check(b, "on hover", problems);
                    b.getModel().setRollover(false);
                }
                return problems;
            });
        } finally {
            config.setDarkMode(oldDark);
            SwingUtilities.invokeAndWait(() -> setLookAndFeel(oldLaf.getClass().getName()));
        }
    }

    private static void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Buttons the screens style as dark/primary: their background is the theme's dark text colour. */
    private static void collectDarkButtons(Container root, List<AbstractButton> out) {
        for (Component c : root.getComponents()) {
            if (c instanceof AbstractButton && AppTheme.getTextDark().equals(c.getBackground())) {
                out.add((AbstractButton) c);
            }
            if (c instanceof Container) {
                collectDarkButtons((Container) c, out);
            }
        }
    }

    /** Paints the button at its preferred size; its most common pixel is the background it really shows. */
    private static void check(AbstractButton b, String state, List<String> problems) {
        Dimension oldSize = b.getSize();
        Dimension size = b.getPreferredSize();
        b.setSize(size);
        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, size.width, size.height);
        b.paint(g);
        g.dispose();
        b.setSize(oldSize);

        Color painted = new Color(mostCommonPixel(img));
        double ratio = contrast(painted, b.getForeground());
        if (ratio < MIN_CONTRAST) {
            problems.add(String.format("'%s' %s: text %s on painted %s, contrast %.1f : 1 (needs %.1f)",
                    b.getText(), state, hex(b.getForeground()), hex(painted), ratio, MIN_CONTRAST));
        }
    }

    private static int mostCommonPixel(BufferedImage img) {
        Map<Integer, Integer> counts = new HashMap<>();
        int best = 0;
        int bestCount = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y) & 0xFFFFFF;
                int n = counts.merge(rgb, 1, Integer::sum);
                if (n > bestCount) {
                    best = rgb;
                    bestCount = n;
                }
            }
        }
        return best;
    }

    private static double contrast(Color a, Color b) {
        double la = luminance(a);
        double lb = luminance(b);
        return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
    }

    private static double luminance(Color c) {
        return 0.2126 * channel(c.getRed()) + 0.7152 * channel(c.getGreen()) + 0.0722 * channel(c.getBlue());
    }

    private static double channel(int v) {
        double s = v / 255.0;
        return s <= 0.03928 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4);
    }

    private static String hex(Color c) {
        return String.format("#%06X", c.getRGB() & 0xFFFFFF);
    }
}
