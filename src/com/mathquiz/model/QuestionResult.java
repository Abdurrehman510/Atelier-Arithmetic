package com.mathquiz.model;

/**
 * Immutable record of a single question's outcome within a quiz session.
 * Captures the expression shown, both the correct and user-provided answers,
 * whether it was correct, and how long the user took to respond.
 */
public class QuestionResult {

    private final String expression;
    private final int correctAnswer;
    private final int userAnswer;
    private final boolean correct;
    private final long timeSpentMs;

    public QuestionResult(String expression, int correctAnswer, int userAnswer,
                          boolean correct, long timeSpentMs) {
        this.expression  = expression;
        this.correctAnswer = correctAnswer;
        this.userAnswer  = userAnswer;
        this.correct     = correct;
        this.timeSpentMs = timeSpentMs;
    }

    public String getExpression()    { return expression; }
    public int    getCorrectAnswer() { return correctAnswer; }
    public int    getUserAnswer()    { return userAnswer; }
    public boolean isCorrect()       { return correct; }
    public long   getTimeSpentMs()   { return timeSpentMs; }

    /** Returns the time taken formatted as a short human-readable string, e.g. "3.4s". */
    public String getTimeFormatted() {
        return String.format("%.1fs", timeSpentMs / 1000.0);
    }
}
