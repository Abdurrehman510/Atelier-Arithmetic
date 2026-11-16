package com.mathquiz.view;

import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Badges screen: every badge description must be fully visible at every text size (the owner's
 * screenshot showed second lines such as "under 2 seconds!" cut off at 125 %).
 */
class AchievementsLayoutTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    private static List<String> badgeProblems(int width, int height, double scale) throws Exception {
        return FrameHarness.inspect(width, height, scale, frame -> {
            frame.showAchievements();
            frame.validate();
            frame.validate();
            AchievementsPanel badges = FrameHarness.find(frame.getContentPane(), AchievementsPanel.class);
            assertNotNull(badges, "badges screen not found");
            return FrameHarness.clippedComponents(badges);
        });
    }

    @Test
    void badgeTextIsFullyVisibleOnAMaximisedWindowAt125Percent() throws Exception {
        List<String> problems = badgeProblems(1366, 728, 1.25);       // the owner's maximised window
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void badgeTextIsFullyVisibleAt150Percent() throws Exception {
        List<String> problems = badgeProblems(1024, 700, 1.5);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void badgeTextIsFullyVisibleInTheSmallestWindow() throws Exception {
        List<String> problems = badgeProblems(720, 550, 1.25);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }
}
