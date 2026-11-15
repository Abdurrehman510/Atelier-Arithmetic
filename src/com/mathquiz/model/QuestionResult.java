package com.mathquiz.model;

import java.util.Objects;

/**
 * Immutable record of a single question's outcome within a quiz session.
 * Captures the expression shown, both the correct and user-provided answers,
 * whether it was correct, how long the user took to respond, the skill practised,
 * the difficulty the question was generated at, and how many times the hint was opened.
 */
public class QuestionResult {

    private final String expression;
    private final int correctAnswer;
    private final int userAnswer;
    private final boolean correct;
    private final long timeSpentMs;
    private final Skill skill;
    private final String difficulty;
    private final int hintsUsed;

    /**
     * @throws NullPointerException     if {@code skill} or {@code difficulty} is null
     * @throws IllegalArgumentException if {@code hintsUsed} is negative
     */
    public QuestionResult(String expression, int correctAnswer, int userAnswer, boolean correct,
                          long timeSpentMs, Skill skill, String difficulty, int hintsUsed) {
        if (hintsUsed < 0) {
            throw new IllegalArgumentException("hintsUsed must not be negative: " + hintsUsed);
        }
        this.expression    = expression;
        this.correctAnswer = correctAnswer;
        this.userAnswer    = userAnswer;
        this.correct       = correct;
        this.timeSpentMs   = timeSpentMs;
        this.skill         = Objects.requireNonNull(skill, "skill");
        this.difficulty    = Objects.requireNonNull(difficulty, "difficulty");
        this.hintsUsed     = hintsUsed;
    }

    public String getExpression()    { return expression; }
    public int    getCorrectAnswer() { return correctAnswer; }
    public int    getUserAnswer()    { return userAnswer; }
    public boolean isCorrect()       { return correct; }
    public long   getTimeSpentMs()   { return timeSpentMs; }
    /** The micro-skill the question practised. */
    public Skill  getSkill()         { return skill; }
    /** The difficulty this question was generated at (may differ from the session's final level). */
    public String getDifficulty()    { return difficulty; }
    /** How many times the hint was opened before the answer was submitted. */
    public int    getHintsUsed()     { return hintsUsed; }

    /** Returns the time taken formatted as a short human-readable string, e.g. "3.4s". */
    public String getTimeFormatted() {
        return String.format("%.1fs", timeSpentMs / 1000.0);
    }
}
