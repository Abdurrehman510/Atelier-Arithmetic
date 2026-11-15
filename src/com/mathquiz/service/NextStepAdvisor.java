package com.mathquiz.service;

import com.mathquiz.model.QuizSession;

/**
 * Chooses the Results screen's "Suggested next step" (PRD FR-RES-04, BR-08). Moved out of
 * {@code ResultsPanel} so the rule is testable (rules.md R-ARC-03).
 */
public final class NextStepAdvisor {

    /** Score (percent) at or above which the child is invited to step up (PRD BR-08). */
    static final double STEP_UP_THRESHOLD = 85.0;

    /** What the next-step button does. */
    public enum Kind { STEP_UP, DAILY_CHALLENGE, SMART_PRACTICE }

    /**
     * @param targetDifficulty the difficulty to pre-select on Home for {@link Kind#STEP_UP}; otherwise null
     */
    public record NextStep(Kind kind, String prompt, String buttonLabel, String targetDifficulty) {
    }

    private NextStepAdvisor() {
    }

    /**
     * @param dailyChallengeAvailable false once the active child has completed today's Daily Challenge
     *                                (it is never offered twice in a day, BUG-04)
     */
    public static NextStep recommend(QuizSession session, boolean dailyChallengeAvailable) {
        if (session.getPercentage() >= STEP_UP_THRESHOLD) {
            if ("Easy".equalsIgnoreCase(session.getDifficulty())) {
                return new NextStep(Kind.STEP_UP, "You are ready for the next challenge!",
                        "STEP UP TO MEDIUM DIFFICULTY", "Medium");
            }
            if ("Medium".equalsIgnoreCase(session.getDifficulty())) {
                return new NextStep(Kind.STEP_UP, "Incredible skills! Level up to master.",
                        "STEP UP TO HARD DIFFICULTY", "Hard");
            }
            if (dailyChallengeAvailable) {
                return new NextStep(Kind.DAILY_CHALLENGE, "Master class quizzer! Try the seeded daily run.",
                        "PLAY TODAY'S DAILY CHALLENGE", null);
            }
            return new NextStep(Kind.SMART_PRACTICE, "Master class quizzer! Sharpen another skill next.",
                    "START SMART PRACTICE MODE", null);
        }
        return new NextStep(Kind.SMART_PRACTICE, "Review weaknesses to improve mastery!",
                "START SMART PRACTICE MODE", null);
    }
}
