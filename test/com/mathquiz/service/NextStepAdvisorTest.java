package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** PRD BR-08 / FR-RES-04: the Results "next step" recommendation. */
class NextStepAdvisorTest {

    private static QuizSession session(String difficulty, int correct, int total) {
        QuizSession s = new QuizSession(total, difficulty, "Addition");
        for (int i = 0; i < total; i++) {
            s.recordResult(i < correct ? Results.correct("1 + 1", 2) : Results.wrong("1 + 1", 2, 3));
        }
        return s;
    }

    @Test
    void easyAtEightyFivePercentStepsUpToMedium() {
        NextStepAdvisor.NextStep step = NextStepAdvisor.recommend(session("Easy", 17, 20));
        assertEquals(NextStepAdvisor.Kind.STEP_UP, step.kind());
        assertEquals("Medium", step.targetDifficulty());
        assertEquals("STEP UP TO MEDIUM DIFFICULTY", step.buttonLabel());
    }

    @Test
    void mediumStepsUpToHard() {
        NextStepAdvisor.NextStep step = NextStepAdvisor.recommend(session("Medium", 19, 20));
        assertEquals(NextStepAdvisor.Kind.STEP_UP, step.kind());
        assertEquals("Hard", step.targetDifficulty());
    }

    @Test
    void hardOffersTheDailyChallenge() {
        NextStepAdvisor.NextStep step = NextStepAdvisor.recommend(session("Hard", 19, 20));
        assertEquals(NextStepAdvisor.Kind.DAILY_CHALLENGE, step.kind());
        assertNull(step.targetDifficulty());
    }

    @Test
    void belowEightyFivePercentOffersSmartPractice() {
        assertEquals(NextStepAdvisor.Kind.SMART_PRACTICE, NextStepAdvisor.recommend(session("Easy", 84, 100)).kind());
    }
}
