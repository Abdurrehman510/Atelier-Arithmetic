package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.model.Skill;
import com.mathquiz.service.SoundService;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the real GamePanel through its public controls (no window is shown) to prove the
 * Phase 0 wiring: hint presses before submitting are counted, presses after submitting are not,
 * empty input is not recorded, and every result carries its skill and generation difficulty.
 */
class GamePanelWiringTest {

    /** This test writes the config file through AppConfig: never against a real home (R-TST-07). */
    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    @Test
    void savedResultsCarrySkillDifficultyAndHintPresses() throws Exception {
        AppConfig config = AppConfig.getInstance();   // lives in the throw-away test home
        boolean soundWasEnabled = config.isSoundEnabled();
        config.setSoundEnabled(false);
        try {
            playThreeQuestionsAndCheck(config);
        } finally {
            config.setSoundEnabled(soundWasEnabled);
        }
    }

    private static void playThreeQuestionsAndCheck(AppConfig config) throws Exception {
        QuizNavigator nav = (QuizNavigator) Proxy.newProxyInstance(
                QuizNavigator.class.getClassLoader(), new Class<?>[]{QuizNavigator.class}, (proxy, method, args) -> null);
        QuizSession session = new QuizSession(3, "Easy", "Addition");

        SwingUtilities.invokeAndWait(() -> {
            GamePanel panel = new GamePanel(nav, new SoundService(config));
            panel.startSession(session);

            // Q1: three hint opens (button; F1 and Ctrl+H route through the same doClick), then correct
            panel.getHintButton().doClick();
            panel.getHintButton().doClick();
            panel.getHintButton().doClick();
            answer(panel, true);
            panel.getSubmitButton().doClick();          // NEXT QUESTION

            // Q2: correct, then a hint opened after submitting must not count
            answer(panel, true);
            panel.getHintButton().doClick();
            panel.getSubmitButton().doClick();          // NEXT QUESTION

            // Q3: blank input is rejected and not recorded, then a wrong answer
            panel.getAnswerField().setText("   ");
            panel.getSubmitButton().doClick();
            answer(panel, false);
        });

        List<QuestionResult> results = session.getResults();
        assertEquals(3, results.size());
        assertEquals(3, results.get(0).getHintsUsed());
        assertEquals(0, results.get(1).getHintsUsed());
        assertEquals(0, results.get(2).getHintsUsed());
        assertTrue(results.get(0).isCorrect());
        assertTrue(results.get(1).isCorrect());
        assertFalse(results.get(2).isCorrect());
        for (QuestionResult r : results) {
            assertEquals(Skill.ADD_2DIGIT_2TERMS, r.getSkill());
            assertEquals("Easy", r.getDifficulty());
        }
    }

    /** Reads "a + b = ?" from the screen and types the right (or a wrong) answer, then submits. */
    private static void answer(GamePanel panel, boolean correct) {
        String[] parts = panel.getExpressionLabel().getText().replace(" = ?", "").split(" \\+ ");
        int sum = Integer.parseInt(parts[0].trim()) + Integer.parseInt(parts[1].trim());
        panel.getAnswerField().setText(String.valueOf(correct ? sum : sum + 1));
        panel.getSubmitButton().doClick();
    }
}
