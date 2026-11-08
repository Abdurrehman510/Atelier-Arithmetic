package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.List;
import java.util.Random;

/**
 * "Special" compound bracket (BODMAS) questions (PRD §9). Random draws match the original
 * generator exactly (R-DOM-04). {@code diff} is lower-case.
 */
final class OrderOfOperationsQuestions {

    private OrderOfOperationsQuestions() {
    }

    static Question special(String diff, Random rng) {
        int type = rng.nextInt(5);
        int scale = diff.equals("easy") ? 10 : (diff.equals("medium") ? 50 : 150);

        switch (type) {
            case 0: { // (A + B) * (C - D)
                int a = rng.nextInt(scale) + 3;
                int b = rng.nextInt(scale) + 3;
                int c = rng.nextInt(scale) + 10;
                int d = rng.nextInt(c - 2) + 1;
                SolutionStep left = SolutionStep.of('+', a, b);
                SolutionStep right = SolutionStep.of('-', c, d);
                return new Question("(" + a + " + " + b + ") * (" + c + " - " + d + ")", (a + b) * (c - d),
                        Skill.ORD_SUM_TIMES_DIFF,
                        List.of(left, right, SolutionStep.of('*', left.result(), right.result())));
            }
            case 1: { // (A - B) / (C + D), exact
                int denominator = rng.nextInt(9) + 2;
                int quotient = rng.nextInt(12) + 2;
                int numerator = denominator * quotient;
                int a = numerator + rng.nextInt(scale) + 5;
                int b = a - numerator;
                int c = denominator / 2 + 1;
                int d = denominator - c;
                SolutionStep left = SolutionStep.of('-', a, b);
                SolutionStep right = SolutionStep.of('+', c, d);
                return new Question("(" + a + " - " + b + ") / (" + c + " + " + d + ")", quotient,
                        Skill.ORD_DIFF_OVER_SUM,
                        List.of(left, right, SolutionStep.of('/', left.result(), right.result())));
            }
            case 2: { // (A / B) + (C / D), exact
                int b = rng.nextInt(8) + 2;
                int q1 = rng.nextInt(10) + 2;
                int a = b * q1;
                int d = rng.nextInt(8) + 2;
                int q2 = rng.nextInt(10) + 2;
                int c = d * q2;
                SolutionStep left = SolutionStep.of('/', a, b);
                SolutionStep right = SolutionStep.of('/', c, d);
                return new Question("(" + a + " / " + b + ") + (" + c + " / " + d + ")", q1 + q2,
                        Skill.ORD_QUOTIENT_SUM,
                        List.of(left, right, SolutionStep.of('+', left.result(), right.result())));
            }
            case 3: { // (A * B) / (C * D), exact
                int c = rng.nextInt(5) + 2;
                int d = rng.nextInt(5) + 2;
                int quotient = rng.nextInt(8) + 2;
                int a = c * quotient;
                int b = d;
                SolutionStep left = SolutionStep.of('*', a, b);
                SolutionStep right = SolutionStep.of('*', c, d);
                return new Question("(" + a + " * " + b + ") / (" + c + " * " + d + ")", quotient,
                        Skill.ORD_PRODUCT_OVER_PRODUCT,
                        List.of(left, right, SolutionStep.of('/', left.result(), right.result())));
            }
            case 4:
            default: { // (A / B) - (C * D)
                int b = rng.nextInt(8) + 2;
                int q1 = rng.nextInt(15) + 10;
                int a = b * q1;
                int d = rng.nextInt(4) + 1;
                int c = rng.nextInt(q1 / d - 1) + 1;
                SolutionStep left = SolutionStep.of('/', a, b);
                SolutionStep right = SolutionStep.of('*', c, d);
                return new Question("(" + a + " / " + b + ") - (" + c + " * " + d + ")", q1 - (c * d),
                        Skill.ORD_QUOTIENT_MINUS_PRODUCT,
                        List.of(left, right, SolutionStep.of('-', left.result(), right.result())));
            }
        }
    }
}
