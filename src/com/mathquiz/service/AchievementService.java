package com.mathquiz.service;

import java.util.*;

/**
 * Achievement tracking service — Phase 3 Gamification.
 *
 * Computes 10 achievements dynamically by scanning the session repository history.
 * Requires zero extra file state, meaning achievements unlock naturally when
 * history changes or is cleared.
 */
public class AchievementService {

    private final SessionRepository repository;
    private final AnalyticsService analytics;

    public static class Achievement {
        public final String id;
        public final String name;
        public final String description;
        public final String emoji;
        public final boolean unlocked;

        public Achievement(String id, String name, String description, String emoji, boolean unlocked) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.emoji = emoji;
            this.unlocked = unlocked;
        }
    }

    public AchievementService(SessionRepository repository, AnalyticsService analytics) {
        this.repository = repository;
        this.analytics = analytics;
    }

    /** Returns all 10 achievements with their dynamic unlocked state. */
    @SuppressWarnings("unchecked")
    public List<Achievement> calculateAchievements() {
        List<Map<String, Object>> sessions = repository.loadRaw();
        List<Achievement> list = new ArrayList<>();

        // ── Rule checks ──────────────────────────────────────────────────────
        boolean hasFirstQuiz = !sessions.isEmpty();
        boolean hasPerfectScore = false;
        boolean hasSpeedDemon = false;
        boolean hasMathMaven = sessions.size() >= 10;
        boolean hasAdditionAce = false;
        boolean hasSubtractionChamp = false;
        boolean hasMultiMaster = false;
        boolean hasDivWizard = false;
        boolean hasSpecialist = false;
        boolean hasConsistencyChamp = analytics.getLongestStreak() >= 3;

        for (Map<String, Object> s : sessions) {
            double pct = 0.0;
            Object pctVal = s.get("percentage");
            if (pctVal instanceof Number) pct = ((Number) pctVal).doubleValue();

            String cat = (String) s.get("category");
            String diff = (String) s.get("difficulty");

            if (pct >= 100.0) hasPerfectScore = true;

            if (cat != null) {
                if (cat.equalsIgnoreCase("Addition") && pct >= 90.0) hasAdditionAce = true;
                if (cat.equalsIgnoreCase("Difference") && pct >= 90.0) hasSubtractionChamp = true;
                if (cat.equalsIgnoreCase("Multiplication") && pct >= 90.0) hasMultiMaster = true;
                if (cat.equalsIgnoreCase("Division") && pct >= 90.0) hasDivWizard = true;
                if (cat.equalsIgnoreCase("Special") && diff != null && diff.equalsIgnoreCase("Hard") && pct >= 80.0) {
                    hasSpecialist = true;
                }
            }

            // Check question-level logs for speed demon
            List<Map<String, Object>> questions = (List<Map<String, Object>>) s.get("questions");
            if (questions != null) {
                for (Map<String, Object> q : questions) {
                    Boolean correct = (Boolean) q.get("correct");
                    Number timeMs = (Number) q.get("timeMs");
                    if (correct != null && correct && timeMs != null && timeMs.longValue() < 2000 && timeMs.longValue() > 0) {
                        hasSpeedDemon = true;
                    }
                }
            }
        }

        // ── Construct list ───────────────────────────────────────────────────
        list.add(new Achievement("first_quiz", "First Steps", "Completed your very first math quiz!", "🌱", hasFirstQuiz));
        list.add(new Achievement("perfect_score", "Arithmetic Genius", "Scored a perfect 100% on any quiz!", "👑", hasPerfectScore));
        list.add(new Achievement("speed_demon", "Speed Demon", "Answered any question correctly in under 2 seconds!", "⚡", hasSpeedDemon));
        list.add(new Achievement("math_maven", "Math Maven", "Completed 10 or more quiz sessions!", "📚", hasMathMaven));
        list.add(new Achievement("addition_ace", "Addition Ace", "Scored 90%+ on an Addition quiz!", "➕", hasAdditionAce));
        list.add(new Achievement("sub_champ", "Difference Champion", "Scored 90%+ on a Difference quiz!", "➖", hasSubtractionChamp));
        list.add(new Achievement("multi_master", "Multiplication Master", "Scored 90%+ on a Multiplication quiz!", "✖️", hasMultiMaster));
        list.add(new Achievement("div_wizard", "Division Wizard", "Scored 90%+ on a Division quiz!", "➗", hasDivWizard));
        list.add(new Achievement("specialist", "Special Specialist", "Scored 80%+ on a Hard Special expression quiz!", "🔬", hasSpecialist));
        list.add(new Achievement("consistency", "Consistency Champion", "Maintained a practice streak of 3+ consecutive days!", "🔥", hasConsistencyChamp));

        return list;
    }
}
