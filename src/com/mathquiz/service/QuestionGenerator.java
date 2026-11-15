package com.mathquiz.service;

import com.mathquiz.model.Question;
import java.util.Random;

/**
 * Generates quiz questions for a difficulty ("Easy", "Medium", "Hard") and a category
 * (Addition, Difference, Multiplication, Division, Mixed, Special, Fractions, Patterns,
 * Algebra, Measurement). Each category lives in its own package-private class; this facade
 * owns the random source, the Mixed choice and the unknown-category fallback (PRD FR-QGN-03/04).
 *
 * <p>{@link #generateSeeded} is a pure function of its arguments (rules.md R-DOM-04); its output
 * is pinned by the golden-master test.</p>
 */
public class QuestionGenerator {
    private Random random = new Random();

    public Question generateSeeded(String difficulty, String category, long dateSeed, int questionIndex) {
        Random oldRandom = this.random;
        this.random = new Random(dateSeed * 100 + questionIndex);
        try {
            return generate(difficulty, category);
        } finally {
            this.random = oldRandom;
        }
    }

    public Question generate(String difficulty, String category) {

        String diffLower = difficulty.toLowerCase();
        String catLower = category.toLowerCase();

        // If Mixed is selected, randomly choose one of the core categories (excluding Special)
        if (catLower.equals("mixed") || catLower.equals("mix")) {
            String[] choices = {"addition", "difference", "multiplication", "division"};
            catLower = choices[random.nextInt(choices.length)];
        }

        switch (catLower) {
            case "addition":
                return ArithmeticQuestions.addition(diffLower, random);
            case "difference":
                return ArithmeticQuestions.difference(diffLower, random);
            case "multiplication":
                return ArithmeticQuestions.multiplication(diffLower, random);
            case "division":
                return ArithmeticQuestions.division(diffLower, random);
            case "special":
                return OrderOfOperationsQuestions.special(diffLower, random);
            case "fractions":
                return FractionQuestions.fractions(diffLower, random);
            case "patterns":
                return PatternQuestions.patterns(diffLower, random);
            case "algebra":
                return AlgebraQuestions.algebra(diffLower, random);
            case "measurement":
                return MeasurementQuestions.measurement(diffLower, random);
            default:
                return ArithmeticQuestions.addition(diffLower, random);
        }
    }
}
