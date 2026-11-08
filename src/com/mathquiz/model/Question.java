package com.mathquiz.model;

import java.util.List;
import java.util.Objects;

/**
 * A single quiz question: the expression shown to the child, its exact {@code int} answer,
 * the {@link Skill} it practises, and an exact step-by-step solution (premium roadmap A1, A8).
 * Immutable (rules.md R-COD-05).
 *
 * <p>Questions typed into the Quiz Builder or loaded from a custom quiz use the two-argument
 * constructor: their skill is {@link Skill#CUSTOM} and their solution is empty.</p>
 */
public final class Question {
    private final String expression;
    private final int correctAnswer;
    private final Skill skill;
    private final List<SolutionStep> solution;

    /** A custom question (Quiz Builder / loaded quiz): skill {@link Skill#CUSTOM}, no solution steps. */
    public Question(String expression, int correctAnswer) {
        this(expression, correctAnswer, Skill.CUSTOM, List.of());
    }

    /**
     * A generated question.
     *
     * @throws NullPointerException     if {@code skill} or {@code solution} is null
     * @throws IllegalArgumentException if a non-empty solution does not end at {@code correctAnswer}
     */
    public Question(String expression, int correctAnswer, Skill skill, List<SolutionStep> solution) {
        this.expression = expression;
        this.correctAnswer = correctAnswer;
        this.skill = Objects.requireNonNull(skill, "skill");
        this.solution = List.copyOf(Objects.requireNonNull(solution, "solution"));
        if (!this.solution.isEmpty()
                && this.solution.get(this.solution.size() - 1).result() != correctAnswer) {
            throw new IllegalArgumentException("solution of '" + expression + "' does not end at " + correctAnswer);
        }
    }

    public String getExpression() {
        return expression;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    /** The micro-skill this question practises. */
    public Skill getSkill() {
        return skill;
    }

    /** The exact worked solution, in order; empty for custom questions. Unmodifiable. */
    public List<SolutionStep> getSolution() {
        return solution;
    }

    /**
     * Checks typed input with the rules the game screen has always used: surrounding spaces are
     * ignored, the rest must parse with {@link Integer#parseInt(String)}.
     */
    public AnswerCheck check(String rawInput) {
        String input = rawInput == null ? "" : rawInput.trim();
        if (input.isEmpty()) {
            return new AnswerCheck(AnswerCheck.Status.EMPTY, 0);
        }
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return new AnswerCheck(AnswerCheck.Status.NOT_A_WHOLE_NUMBER, 0);
        }
        return new AnswerCheck(value == correctAnswer ? AnswerCheck.Status.CORRECT : AnswerCheck.Status.INCORRECT,
                value);
    }
}
