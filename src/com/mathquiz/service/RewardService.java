package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuizSession;

import java.util.List;

/**
 * Stars reward economy engine for Atelier Arithmetic.
 *
 * Calculates and persists earned stars based on quiz performance,
 * grade bonuses, daily challenge completion, streaks, and achievement unlocks.
 * All balance data is stored encrypted in AppConfig (config.properties).
 *
 * Reward Formula:
 *   Base         : +5  stars per completed quiz
 *   Correct Ans  : +1  star per correct answer
 *   Perfect 100% : +10 bonus stars
 *   Grade A+/A++ : +5  bonus stars (if not perfect)
 *   Grade A      : +3  bonus stars
 *   Daily Chall. : +8  bonus stars
 *   Streak bonus : +2  per consecutive day (max +10 for 5+ days)
 *   Achievement  : +15 stars on first unlock (called externally)
 */
public class RewardService {

    private final AppConfig config;

    public RewardService(AppConfig config) {
        this.config = config;
    }

    // ── Balance API ───────────────────────────────────────────────────────────

    /** Returns the current star balance for the active profile. */
    public int getBalance() {
        return config.getStarBalance();
    }

    /** Adds stars to the balance. Always non-negative. */
    public void addStars(int amount) {
        if (amount <= 0) return;
        config.setStarBalance(config.getStarBalance() + amount);
    }

    /**
     * Attempts to spend stars. Returns true if successful (sufficient balance),
     * false if the balance is insufficient (no deduction occurs).
     */
    public boolean spendStars(int amount) {
        if (amount <= 0) return true;
        int current = config.getStarBalance();
        if (current < amount) return false;
        config.setStarBalance(current - amount);
        return true;
    }

    // ── Shop API ─────────────────────────────────────────────────────────────

    public List<String> getUnlockedItems() {
        return config.getUnlockedItems();
    }

    public boolean isItemUnlocked(String itemId) {
        return config.getUnlockedItems().contains(itemId);
    }

    public void unlockItem(String itemId) {
        config.addUnlockedItem(itemId);
    }

    public String getEquippedItem() {
        return config.getEquippedItem();
    }

    public void equipItem(String itemId) {
        config.setEquippedItem(itemId);
    }

    public void unequipItem() {
        config.setEquippedItem("none");
    }

    // ── Reward Calculation ────────────────────────────────────────────────────

    /**
     * Computes stars earned for a completed quiz session,
     * awards them immediately, and returns the breakdown as a RewardResult.
     */
    public RewardResult calculateAndAwardQuizReward(QuizSession session,
                                                    AnalyticsService analytics) {
        int base = 5;
        int correctBonus = session.getCorrectAnswersCount();
        int gradeBonus = 0;
        int perfectBonus = 0;
        int dailyBonus = 0;
        int streakBonus = 0;

        double pct = session.getPercentage();

        // Perfect score bonus
        if (pct >= 100.0) {
            perfectBonus = 10;
        } else if (pct >= 85.0) {
            // A+ / A++
            gradeBonus = 5;
        } else if (pct >= 78.0) {
            // A
            gradeBonus = 3;
        }

        // Daily challenge bonus
        if (session.getCategory().equalsIgnoreCase("Daily Challenge")) {
            dailyBonus = 8;
        }

        // Streak bonus: 2 stars per consecutive day, max 10
        int streak = analytics.getCurrentStreak();
        streakBonus = Math.min(streak * 2, 10);

        int total = base + correctBonus + gradeBonus + perfectBonus + dailyBonus + streakBonus;
        addStars(total);

        return new RewardResult(base, correctBonus, gradeBonus, perfectBonus, dailyBonus, streakBonus, total);
    }

    /** Award stars for unlocking a new achievement (called externally when detected). */
    public void awardAchievementBonus(String achievementId) {
        addStars(15);
    }

    // ── Inner Result DTO ─────────────────────────────────────────────────────

    /** Immutable data holder describing a quiz reward breakdown. */
    public static class RewardResult {
        public final int base;
        public final int correctBonus;
        public final int gradeBonus;
        public final int perfectBonus;
        public final int dailyBonus;
        public final int streakBonus;
        public final int total;

        public RewardResult(int base, int correctBonus, int gradeBonus,
                            int perfectBonus, int dailyBonus, int streakBonus, int total) {
            this.base = base;
            this.correctBonus = correctBonus;
            this.gradeBonus = gradeBonus;
            this.perfectBonus = perfectBonus;
            this.dailyBonus = dailyBonus;
            this.streakBonus = streakBonus;
            this.total = total;
        }

        /** Returns a multiline summary string for display in tooltips or toasts. */
        public String toSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("\u2605 +" + total + " Stars Earned!\n");
            sb.append("Base: +" + base + "\n");
            if (correctBonus > 0) sb.append("Correct answers: +" + correctBonus + "\n");
            if (perfectBonus > 0) sb.append("Perfect score bonus: +" + perfectBonus + "\n");
            if (gradeBonus > 0)   sb.append("Grade bonus: +" + gradeBonus + "\n");
            if (dailyBonus > 0)   sb.append("Daily challenge: +" + dailyBonus + "\n");
            if (streakBonus > 0)  sb.append("Streak bonus: +" + streakBonus);
            return sb.toString().trim();
        }
    }
}
