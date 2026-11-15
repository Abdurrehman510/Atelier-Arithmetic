package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuizSession;
import com.mathquiz.service.SessionRepository;
import com.mathquiz.testutil.Results;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-13 / decision Q3: Reset History deletes only quiz history, and says exactly that. */
class QuizFrameResetTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    @Test
    void resetKeepsStarsAndAccessoriesAndClearsHistory() throws Exception {
        AppConfig config = AppConfig.getInstance();
        boolean tourSeen = config.isTourSeen();
        boolean sound = config.isSoundEnabled();
        int stars = config.getStarBalance();
        String equipped = config.getEquippedItem();
        config.setTourSeen(true);
        config.setSoundEnabled(false);
        try {
            config.setStarBalance(30);
            config.addUnlockedItem("hat_top");
            config.setEquippedItem("hat_top");
            QuizSession session = new QuizSession(1, "Easy", "Addition");
            session.recordResult(Results.correct("1 + 1", 2));
            new SessionRepository().save(session);

            QuizFrame[] holder = new QuizFrame[1];
            SwingUtilities.invokeAndWait(() -> holder[0] = new QuizFrame());
            SwingUtilities.invokeAndWait(() -> {
                try {
                    holder[0].resetAllData();
                } finally {
                    holder[0].dispose();
                }
            });

            assertEquals(0, new SessionRepository().getSessionCount(), "quiz history must be deleted");
            assertEquals(30, config.getStarBalance(), "stars must be kept");
            assertTrue(config.getUnlockedItems().contains("hat_top"), "bought accessories must be kept");
            assertEquals("hat_top", config.getEquippedItem(), "the equipped accessory must be kept");
        } finally {
            config.resetProfileCustomizations(config.getCurrentProfile());
            config.setEquippedItem(equipped);
            config.setStarBalance(stars);
            config.setSoundEnabled(sound);
            config.setTourSeen(tourSeen);
        }
    }

    @Test
    void theWarningSaysExactlyWhatIsDeletedAndWhatIsKept() {
        String warning = AnalyticsPanel.resetWarning("Asha");
        assertTrue(warning.contains("Asha's quiz history"), warning);
        assertTrue(warning.contains("badges"), "badges are recomputed from history, so they reset too");
        assertTrue(warning.contains("Asha's stars and Archie's accessories are kept"), warning);
        assertTrue(warning.contains("cannot be undone"), warning);
        assertFalse(warning.toLowerCase().contains("stars will be"), warning);
    }
}
