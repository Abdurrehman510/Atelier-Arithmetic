package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-17: level-change prompts would change the Daily Challenge's seeded questions mid-run. */
class AdaptiveDifficultyEngineTest {

    @Test
    void dailyChallengeNeverOffersALevelChange() {
        assertFalse(AdaptiveDifficultyEngine.appliesTo(new QuizSession(10, "Medium", "Daily Challenge")));
        assertFalse(AdaptiveDifficultyEngine.appliesTo(new QuizSession(10, "Medium", "daily challenge")));
    }

    @Test
    void regularQuizzesStillAdapt() {
        assertTrue(AdaptiveDifficultyEngine.appliesTo(new QuizSession(10, "Easy", "Addition")));
        assertTrue(AdaptiveDifficultyEngine.appliesTo(new QuizSession(10, "Medium", "Mixed")));
    }
}
