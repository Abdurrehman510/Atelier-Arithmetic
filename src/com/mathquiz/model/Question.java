package com.mathquiz.model;

/**
 * Represents a single generated quiz question, holding the mathematical expression
 * and its pre-calculated correct integer answer.
 */
public class Question {
    private final String expression;
    private final int correctAnswer;

    public Question(String expression, int correctAnswer) {
        this.expression = expression;
        this.correctAnswer = correctAnswer;
    }

    public String getExpression() {
        return expression;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
