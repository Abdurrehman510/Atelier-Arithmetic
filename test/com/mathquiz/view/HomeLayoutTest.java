package com.mathquiz.view;

import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Container;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Home screen (both columns: title, greeting, quiz settings card and the right-hand cards) at the
 * smallest supported window and at larger text: nothing may be clipped (rules.md R-UI-07; the owner's
 * screenshot showed the settings card cut off on the right at 125 %).
 */
class HomeLayoutTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    private static List<String> leftColumnProblems(int width, int height, double scale) throws Exception {
        return FrameHarness.inspect(width, height, scale, frame -> {
            Container content = (Container) FrameHarness.findByName(frame.getContentPane(), "homeContent");
            assertNotNull(content, "Home content not found");
            return FrameHarness.clippedComponents(content);
        });
    }

    @Test
    void leftColumnFitsTheSmallestWindowAt125Percent() throws Exception {
        List<String> problems = leftColumnProblems(720, 550, 1.25);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void leftColumnFitsTheOwnersWindowAt125Percent() throws Exception {
        List<String> problems = leftColumnProblems(805, 610, 1.25);    // size in the owner's screenshot
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void leftColumnFitsAMediumWindowAt150Percent() throws Exception {
        List<String> problems = leftColumnProblems(800, 600, 1.5);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }
}
