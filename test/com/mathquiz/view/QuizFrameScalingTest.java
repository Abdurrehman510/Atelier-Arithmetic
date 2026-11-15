package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BUG-14: screens that rebuild their content when opened (badges, analytics, shop, smart practice)
 * must show it at the chosen text size. The frame is built but never shown.
 */
class QuizFrameScalingTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    private static <T extends Component> T find(Container root, Class<T> type) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) {
                return type.cast(c);
            }
            if (c instanceof Container) {
                T found = find((Container) c, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void collectLabels(Container root, List<JLabel> out) {
        for (Component c : root.getComponents()) {
            if (c instanceof JLabel) {
                out.add((JLabel) c);
            }
            if (c instanceof Container) {
                collectLabels((Container) c, out);
            }
        }
    }

    @Test
    void rebuiltBadgeCardsUseTheChosenTextSize() throws Exception {
        AppConfig config = AppConfig.getInstance();
        boolean tourSeen = config.isTourSeen();
        boolean sound = config.isSoundEnabled();
        double scale = config.getFontSizeScale();
        config.setTourSeen(true);          // no tour overlay
        config.setSoundEnabled(false);     // silent test
        config.setFontSizeScale(1.5);
        List<String> unscaled = new ArrayList<>();
        try {
            QuizFrame[] holder = new QuizFrame[1];
            // 1. Build the window; its start-up theme/scale pass is queued on the EDT (as in the app)
            SwingUtilities.invokeAndWait(() -> holder[0] = new QuizFrame());
            // 2. Later, the child opens the badges screen
            SwingUtilities.invokeAndWait(() -> {
                QuizFrame frame = holder[0];
                try {
                    frame.showAchievements();                       // rebuilds the badge cards
                    AchievementsPanel badges = find(frame.getContentPane(), AchievementsPanel.class);
                    assertNotNull(badges, "badges screen not found");
                    List<JLabel> labels = new ArrayList<>();
                    collectLabels(badges, labels);
                    assertFalse(labels.isEmpty());
                    for (JLabel label : labels) {
                        if (label.getFont() != null && ((JComponent) label).getClientProperty("baseFontSize") == null) {
                            unscaled.add(label.getText());
                        }
                    }
                } finally {
                    frame.dispose();
                }
            });
        } finally {
            config.setFontSizeScale(scale);
            config.setSoundEnabled(sound);
            config.setTourSeen(tourSeen);
        }
        assertTrue(unscaled.isEmpty(), "labels shown at base size instead of 150 %: " + unscaled);
    }
}
