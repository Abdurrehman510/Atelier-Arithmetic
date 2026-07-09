package com.mathquiz.service;

import com.mathquiz.model.QuestionResult;

import java.util.List;

/**
 * Adaptive Difficulty Engine — Phase 2 Intelligence Layer.
 *
 * Evaluates the most recent answers in a live quiz session and recommends
 * whether the difficulty should be upgraded, downgraded, or maintained.
 *
 * Rules (configurable via constants below):
 *   UPGRADE  — last UPGRADE_WINDOW answers are all correct (≥ UPGRADE_THRESHOLD ratio)
 *   DOWNGRADE — last DOWNGRADE_WINDOW answers have fewer than DOWNGRADE_THRESHOLD correct
 *   MAINTAIN — everything else
 *
 * GamePanel calls evaluate() after every answer and shows a non-intrusive
 * banner if the recommendation is not MAINTAIN.
 */
public class AdaptiveDifficultyEngine {

    // ── Tuning constants ──────────────────────────────────────────────────────

    /** Minimum questions answered before the engine gives any recommendation. */
    private static final int MIN_QUESTIONS = 4;

    /** Window size for detecting an upward-difficulty streak. */
    private static final int UPGRADE_WINDOW = 5;
    /** Ratio of correct answers in the upgrade window needed to suggest an upgrade. */
    private static final double UPGRADE_THRESHOLD = 1.0;  // 100% correct in window

    /** Window size for detecting a downward-difficulty slump. */
    private static final int DOWNGRADE_WINDOW = 3;
    /** Maximum ratio correct in the downgrade window to suggest a downgrade. */
    private static final double DOWNGRADE_THRESHOLD = 0.0; // 0 correct in window (all wrong)

    // ── Recommendation enum ───────────────────────────────────────────────────

    public enum Recommendation {
        /** Child is excelling — suggest stepping up the challenge. */
        UPGRADE,
        /** Child is struggling — suggest stepping down. */
        DOWNGRADE,
        /** Performance is in a comfortable range — no change needed. */
        MAINTAIN
    }

    // ── Difficulty ordering ───────────────────────────────────────────────────

    private static final String[] DIFFICULTY_ORDER = {"Easy", "Medium", "Hard"};

    // ── State ─────────────────────────────────────────────────────────────────

    /**
     * Once a recommendation has been surfaced we suppress further suggestions
     * for COOLDOWN_QUESTIONS answers to avoid spamming the child.
     */
    private static final int COOLDOWN_QUESTIONS = 5;
    private int questionsSinceLastSuggestion = COOLDOWN_QUESTIONS; // start ready

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Evaluates the results so far and returns a {@link Recommendation}.
     *
     * @param allResults      full list of results for the current session (oldest first)
     * @param currentDifficulty the difficulty currently in use ("Easy"|"Medium"|"Hard")
     * @return MAINTAIN if too few questions answered, or if in cooldown, or if
     *         current difficulty is already at the boundary for the suggested direction
     */
    public Recommendation evaluate(List<QuestionResult> allResults, String currentDifficulty) {
        int total = allResults.size();

        // Always count toward cooldown
        questionsSinceLastSuggestion++;

        if (total < MIN_QUESTIONS || questionsSinceLastSuggestion < COOLDOWN_QUESTIONS) {
            return Recommendation.MAINTAIN;
        }

        // ── Check for UPGRADE ─────────────────────────────────────────────────
        if (total >= UPGRADE_WINDOW && canUpgrade(currentDifficulty)) {
            List<QuestionResult> window = allResults.subList(total - UPGRADE_WINDOW, total);
            long correctInWindow = window.stream().filter(QuestionResult::isCorrect).count();
            double ratio = (double) correctInWindow / UPGRADE_WINDOW;
            if (ratio >= UPGRADE_THRESHOLD) {
                questionsSinceLastSuggestion = 0;
                return Recommendation.UPGRADE;
            }
        }

        // ── Check for DOWNGRADE ───────────────────────────────────────────────
        if (total >= DOWNGRADE_WINDOW && canDowngrade(currentDifficulty)) {
            List<QuestionResult> window = allResults.subList(total - DOWNGRADE_WINDOW, total);
            long correctInWindow = window.stream().filter(QuestionResult::isCorrect).count();
            double ratio = (double) correctInWindow / DOWNGRADE_WINDOW;
            if (ratio <= DOWNGRADE_THRESHOLD) {
                questionsSinceLastSuggestion = 0;
                return Recommendation.DOWNGRADE;
            }
        }

        return Recommendation.MAINTAIN;
    }

    /**
     * Resets the cooldown counter so evaluations restart fresh on a new session.
     * Call this when a new quiz session begins.
     */
    public void reset() {
        questionsSinceLastSuggestion = COOLDOWN_QUESTIONS;
    }

    // =========================================================================
    // Difficulty navigation helpers
    // =========================================================================

    /** Returns the difficulty one step harder, or the same if already at Hard. */
    public static String upgrade(String current) {
        for (int i = 0; i < DIFFICULTY_ORDER.length - 1; i++) {
            if (DIFFICULTY_ORDER[i].equalsIgnoreCase(current)) return DIFFICULTY_ORDER[i + 1];
        }
        return current; // already Hard
    }

    /** Returns the difficulty one step easier, or the same if already at Easy. */
    public static String downgrade(String current) {
        for (int i = DIFFICULTY_ORDER.length - 1; i > 0; i--) {
            if (DIFFICULTY_ORDER[i].equalsIgnoreCase(current)) return DIFFICULTY_ORDER[i - 1];
        }
        return current; // already Easy
    }

    /** Returns true if an upgrade is possible from the given difficulty. */
    public static boolean canUpgrade(String current) {
        return !DIFFICULTY_ORDER[DIFFICULTY_ORDER.length - 1].equalsIgnoreCase(current);
    }

    /** Returns true if a downgrade is possible from the given difficulty. */
    public static boolean canDowngrade(String current) {
        return !DIFFICULTY_ORDER[0].equalsIgnoreCase(current);
    }
}
