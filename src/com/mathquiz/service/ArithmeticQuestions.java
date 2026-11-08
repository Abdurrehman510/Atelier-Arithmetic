package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Addition, Difference, Multiplication and Division questions (PRD §9).
 * Random numbers are drawn in exactly the order and ranges of the original generator, so seeded
 * output is unchanged (R-DOM-04); skills and solution steps come from the values already drawn.
 * {@code diff} is lower-case: "easy", "medium", anything else means hard.
 */
final class ArithmeticQuestions {

    private ArithmeticQuestions() {
    }

    static Question addition(String diff, Random rng) {
        if (diff.equals("easy")) {
            int a = rng.nextInt(90) + 10;
            int b = rng.nextInt(90) + 10;
            return new Question(a + " + " + b, a + b, Skill.ADD_2DIGIT_2TERMS, chain('+', a, b));
        } else if (diff.equals("medium")) {
            int a = rng.nextInt(900) + 100;
            int b = rng.nextInt(900) + 100;
            int c = rng.nextInt(900) + 100;
            return new Question(a + " + " + b + " + " + c, a + b + c, Skill.ADD_3DIGIT_3TERMS, chain('+', a, b, c));
        } else {
            int a = rng.nextInt(9000) + 1000;
            int b = rng.nextInt(9000) + 1000;
            int c = rng.nextInt(900) + 100;
            int d = rng.nextInt(900) + 100;
            return new Question(a + " + " + b + " + " + c + " + " + d, a + b + c + d,
                    Skill.ADD_4DIGIT_4TERMS, chain('+', a, b, c, d));
        }
    }

    static Question difference(String diff, Random rng) {
        if (diff.equals("easy")) {
            int a = rng.nextInt(90) + 10;
            int b = rng.nextInt(a - 5) + 5; // ensures a positive result
            return new Question(a + " - " + b, a - b, Skill.SUB_2DIGIT_2TERMS, chain('-', a, b));
        } else if (diff.equals("medium")) {
            int a = rng.nextInt(900) + 100;
            int b = rng.nextInt(a / 2) + 10;
            int c = rng.nextInt(a - b - 10) + 5;
            return new Question(a + " - " + b + " - " + c, a - b - c, Skill.SUB_3DIGIT_3TERMS, chain('-', a, b, c));
        } else {
            int a = rng.nextInt(9000) + 1000;
            int b = rng.nextInt(a / 3) + 100;
            int c = rng.nextInt(a / 3) + 50;
            int d = rng.nextInt(a - b - c - 50) + 10;
            return new Question(a + " - " + b + " - " + c + " - " + d, a - b - c - d,
                    Skill.SUB_4DIGIT_4TERMS, chain('-', a, b, c, d));
        }
    }

    static Question multiplication(String diff, Random rng) {
        if (diff.equals("easy")) {
            int a = rng.nextInt(89) + 10;
            int b = rng.nextInt(9) + 2;
            return new Question(a + " * " + b, a * b, Skill.MUL_2DIGIT_BY_1DIGIT, chain('*', a, b));
        } else if (diff.equals("medium")) {
            int a = rng.nextInt(89) + 10;
            int b = rng.nextInt(8) + 2;
            int c = rng.nextInt(4) + 2;
            return new Question(a + " * " + b + " * " + c, a * b * c, Skill.MUL_2DIGIT_3FACTORS, chain('*', a, b, c));
        } else {
            int a = rng.nextInt(899) + 100;
            int b = rng.nextInt(8) + 2;
            int c = rng.nextInt(8) + 2;
            return new Question(a + " * " + b + " * " + c, a * b * c, Skill.MUL_3DIGIT_3FACTORS, chain('*', a, b, c));
        }
    }

    static Question division(String diff, Random rng) {
        if (diff.equals("easy")) {
            int divisor = rng.nextInt(12) + 2;
            int quotient = rng.nextInt(15) + 2;
            int dividend = divisor * quotient;
            return new Question(dividend + " / " + divisor, quotient, Skill.DIV_BASIC, chain('/', dividend, divisor));
        } else if (diff.equals("medium")) {
            // (A / B) / C, all exact
            int extra = rng.nextInt(8) + 2;
            int q2 = rng.nextInt(10) + 2;
            int middle = extra * q2;
            int divisor = rng.nextInt(8) + 2;
            int dividend = middle * divisor;
            return new Question("(" + dividend + " / " + divisor + ") / " + extra, q2,
                    Skill.DIV_CHAINED, chain('/', dividend, divisor, extra));
        } else {
            int divisor = rng.nextInt(48) + 3;
            int quotient = rng.nextInt(90) + 10;
            int dividend = divisor * quotient;
            return new Question(dividend + " / " + divisor, quotient,
                    Skill.DIV_2DIGIT_QUOTIENT, chain('/', dividend, divisor));
        }
    }

    /** Left-to-right steps: ((first op x1) op x2) ... */
    private static List<SolutionStep> chain(char op, int first, int... rest) {
        List<SolutionStep> steps = new ArrayList<>();
        int running = first;
        for (int operand : rest) {
            SolutionStep step = SolutionStep.of(op, running, operand);
            steps.add(step);
            running = step.result();
        }
        return steps;
    }
}
