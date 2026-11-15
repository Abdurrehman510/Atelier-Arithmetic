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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BUG-14: screens that rebuild their content (badges, analytics, shop, smart practice) must show it
 * at the chosen text size — when opened, and when they rebuild in place (shop Equip/Buy).
 * The frame is built but never shown.
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

    /**
     * Builds the main window at 150 % (no tour, no sound), lets its start-up pass run as in the app,
     * then runs {@code action} (which returns the screen to inspect) and lists labels left at base size.
     */
    private static List<String> unscaledLabelsAfter(Function<QuizFrame, Container> action) throws Exception {
        AppConfig config = AppConfig.getInstance();
        boolean tourSeen = config.isTourSeen();
        boolean sound = config.isSoundEnabled();
        double scale = config.getFontSizeScale();
        config.setTourSeen(true);
        config.setSoundEnabled(false);
        config.setFontSizeScale(1.5);
        List<String> unscaled = new ArrayList<>();
        try {
            QuizFrame[] holder = new QuizFrame[1];
            SwingUtilities.invokeAndWait(() -> holder[0] = new QuizFrame());
            SwingUtilities.invokeAndWait(() -> {
                try {
                    Container screen = action.apply(holder[0]);
                    assertNotNull(screen, "screen not found");
                    List<JLabel> labels = new ArrayList<>();
                    collectLabels(screen, labels);
                    assertFalse(labels.isEmpty());
                    for (JLabel label : labels) {
                        if (label.getFont() != null && ((JComponent) label).getClientProperty("baseFontSize") == null) {
                            unscaled.add(label.getText());
                        }
                    }
                } finally {
                    holder[0].dispose();
                }
            });
        } finally {
            config.setFontSizeScale(scale);
            config.setSoundEnabled(sound);
            config.setTourSeen(tourSeen);
        }
        return unscaled;
    }

    @Test
    void rebuiltBadgeCardsUseTheChosenTextSize() throws Exception {
        List<String> unscaled = unscaledLabelsAfter(frame -> {
            frame.showAchievements();                                  // rebuilds the badge cards
            return find(frame.getContentPane(), AchievementsPanel.class);
        });
        assertTrue(unscaled.isEmpty(), "labels shown at base size instead of 150 %: " + unscaled);
    }

    @Test
    void shopCardsKeepTheTextSizeAfterAnInPlaceRebuild() throws Exception {
        List<String> unscaled = unscaledLabelsAfter(frame -> {
            frame.showMascotShop();
            MascotShopPanel shop = find(frame.getContentPane(), MascotShopPanel.class);
            shop.refresh();                                            // what Equip / Buy do inside the shop
            return shop;
        });
        assertTrue(unscaled.isEmpty(), "shop labels shown at base size after Equip/Buy: " + unscaled);
    }
}
