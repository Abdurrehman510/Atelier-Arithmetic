package com.mathquiz.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the full state of an active quiz session.
 * Tracks questions, user score, timing, and per-question results.
 */
public class QuizSession {

    private final int    totalQuestions;
    private String       difficulty;
    private final String category;
    private final long   sessionStartTime;

    private int currentQuestionIndex = 0;
    private int correctAnswersCount  = 0;
    private final List<QuestionResult> results = new ArrayList<>();

    public QuizSession(int totalQuestions, String difficulty, String category) {
        this.totalQuestions   = totalQuestions;
        this.difficulty       = difficulty;
        this.category         = category;
        this.sessionStartTime = System.currentTimeMillis();
    }


    // -------------------------------------------------------------------------
    // Mutation
    // -------------------------------------------------------------------------

    /**
     * Records the outcome of the current question and advances the question index.
     * This replaces the old separate increment methods.
     */
    public void recordResult(QuestionResult result) {
        results.add(result);
        if (result.isCorrect()) correctAnswersCount++;
        currentQuestionIndex++;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public int    getTotalQuestions()       { return totalQuestions; }
    public String getDifficulty()           { return difficulty; }
    public void   setDifficulty(String diff) { this.difficulty = diff; }
    public String getCategory()             { return category; }

    public int    getCurrentQuestionIndex() { return currentQuestionIndex; }
    public int    getCorrectAnswersCount()  { return correctAnswersCount; }
    public long   getSessionStartTime()     { return sessionStartTime; }

    public List<QuestionResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    /** Total milliseconds elapsed since the session began. */
    public long getDurationMs() {
        return System.currentTimeMillis() - sessionStartTime;
    }

    /** Returns percentage of correct answers, 0–100. */
    public double getPercentage() {
        if (totalQuestions == 0) return 0;
        return ((double) correctAnswersCount / totalQuestions) * 100.0;
    }

    // -------------------------------------------------------------------------
    // Grade & Remarks  (child-friendly language — Feature 4)
    // -------------------------------------------------------------------------

    public String getGrade() {
        double pct = getPercentage();
        if (pct >= 95) return "A++";
        if (pct >= 85) return "A+";
        if (pct >= 78) return "A";
        if (pct >= 65) return "B+";
        if (pct >= 53) return "B";
        if (pct >= 40) return "C+";
        if (pct >= 33.33) return "C";
        return "D";
    }

    /**
     * Child-encouraging remarks that motivate continued learning.
     * Replaces the old punitive labels ("BAD", "VERY VERY BAD") with
     * growth-mindset language appropriate for ages 8–14.
     */
    public String getRemarks() {
        double pct = getPercentage();
        if (pct >= 95) return "Outstanding — True Mastery!";
        if (pct >= 85) return "Excellent — Impressive Skills!";
        if (pct >= 78) return "Very Good — Strong Performance!";
        if (pct >= 65) return "Good — Solid Foundation!";
        if (pct >= 53) return "Progressing — Keep Practicing!";
        if (pct >= 40) return "Developing — You're Learning!";
        if (pct >= 33.33) return "Getting Started — Stay Curious!";
        return "Room to Grow — Try an Easier Level!";
    }

    /** Emoji badge matching the grade, used in the results screen. */
    public String getGradeEmoji() {
        double pct = getPercentage();
        if (pct >= 95) return "🏆";
        if (pct >= 85) return "🌟";
        if (pct >= 78) return "💪";
        if (pct >= 65) return "👍";
        if (pct >= 53) return "📈";
        if (pct >= 40) return "🔄";
        if (pct >= 33.33) return "🌱";
        return "🚀";
    }
}
