package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.List;
import java.util.Random;

/**
 * Fractions questions, including percentages and ratios (PRD §9). Random draws match the
 * original generator exactly, including the coprime retry loops (R-DOM-04). {@code diff} is lower-case.
 */
final class FractionQuestions {

    private FractionQuestions() {
    }

    static Question fractions(String diff, Random rng) {
        if (diff.equals("easy")) {
            // a/b of c: denominator b in [2, 10], a coprime with b, multiplier in [2, 15]
            int b = rng.nextInt(9) + 2;
            int a = rng.nextInt(b - 1) + 1;
            while (gcd(a, b) != 1) {
                a = rng.nextInt(b - 1) + 1;
            }
            int multiplier = rng.nextInt(14) + 2;
            int c = b * multiplier;
            return new Question("What is " + a + "/" + b + " of " + c + "?", a * multiplier,
                    Skill.FRAC_OF_QUANTITY, fractionOf(c, b, a));
        } else if (diff.equals("medium")) {
            int subType = rng.nextInt(3);
            if (subType == 0) {
                // mixed number A B/C → improper numerator A*C + B
                int c = rng.nextInt(8) + 3;
                int b = rng.nextInt(c - 1) + 1;
                while (gcd(b, c) != 1) {
                    b = rng.nextInt(c - 1) + 1;
                }
                int a = rng.nextInt(9) + 1;
                SolutionStep whole = SolutionStep.of('*', a, c);
                return new Question("What is the numerator of " + a + " " + b + "/" + c + " as an improper fraction?",
                        a * c + b, Skill.FRAC_MIXED_TO_IMPROPER,
                        List.of(whole, SolutionStep.of('+', whole.result(), b)));
            } else if (subType == 1) {
                // simplify (a*d)/(b*d); the answer is the simplified denominator b
                int b = rng.nextInt(9) + 2;
                int a = rng.nextInt(b - 1) + 1;
                while (gcd(a, b) != 1) {
                    a = rng.nextInt(b - 1) + 1;
                }
                int d = rng.nextInt(7) + 2;
                return new Question("If you simplify " + (a * d) + "/" + (b * d) + " to simplest form, what is the denominator?",
                        b, Skill.FRAC_SIMPLIFY, List.of(SolutionStep.of('/', b * d, d)));
            } else {
                // a/b of a larger total
                int b = rng.nextInt(11) + 5;
                int a = rng.nextInt(b - 1) + 1;
                while (gcd(a, b) != 1) {
                    a = rng.nextInt(b - 1) + 1;
                }
                int mult = rng.nextInt(16) + 5;
                return new Question("Calculate " + a + "/" + b + " of " + (b * mult), a * mult,
                        Skill.FRAC_OF_QUANTITY, fractionOf(b * mult, b, a));
            }
        } else {
            int subType = rng.nextInt(3);
            if (subType == 0) {
                // p% of x, always an exact integer
                int[] pcts = {5, 10, 15, 20, 25, 30, 40, 50, 60, 70, 75, 80, 90, 110, 120, 150};
                int p = pcts[rng.nextInt(pcts.length)];
                int multiplier;
                if (p % 25 == 0) {
                    multiplier = (rng.nextInt(20) + 2) * 4;
                } else if (p % 10 == 0) {
                    multiplier = (rng.nextInt(20) + 2) * 10;
                } else {
                    multiplier = (rng.nextInt(12) + 2) * 20;
                }
                int x = multiplier;
                SolutionStep product = SolutionStep.of('*', x, p);
                return new Question("What is " + p + "% of " + x + "?", (x * p) / 100, Skill.PCT_OF_QUANTITY,
                        List.of(product, SolutionStep.of('/', product.result(), 100)));
            } else if (subType == 1) {
                // ratio a:b, given the red count find the blue count
                int b = rng.nextInt(8) + 2;
                int a = rng.nextInt(8) + 2;
                while (gcd(a, b) != 1 || a == b) {
                    a = rng.nextInt(8) + 2;
                }
                int factor = rng.nextInt(14) + 2;
                int valA = a * factor;
                int valB = b * factor;
                SolutionStep scale = SolutionStep.of('/', valA, a);
                return new Question("The ratio of red to blue marbles is " + a + ":" + b + ". If there are " + valA
                        + " red marbles, how many blue marbles are there?", valB, Skill.RATIO_MISSING_PART,
                        List.of(scale, SolutionStep.of('*', scale.result(), b)));
            } else {
                // (a/b) * (c/d) of E
                int b = rng.nextInt(4) + 2;
                int d = rng.nextInt(4) + 2;
                int a = rng.nextInt(b - 1) + 1;
                int c = rng.nextInt(d - 1) + 1;
                int multiplier = rng.nextInt(9) + 2;
                int e = (b * d) * multiplier;
                SolutionStep denominators = SolutionStep.of('*', b, d);
                SolutionStep share = SolutionStep.of('/', e, denominators.result());
                SolutionStep numerators = SolutionStep.of('*', a, c);
                return new Question("What is (" + a + "/" + b + ") * (" + c + "/" + d + ") of " + e + "?",
                        a * c * multiplier, Skill.FRAC_PRODUCT_OF_QUANTITY,
                        List.of(denominators, share, numerators,
                                SolutionStep.of('*', share.result(), numerators.result())));
            }
        }
    }

    /** numerator/denominator of total: total ÷ denominator, then × numerator. */
    private static List<SolutionStep> fractionOf(int total, int denominator, int numerator) {
        SolutionStep share = SolutionStep.of('/', total, denominator);
        return List.of(share, SolutionStep.of('*', share.result(), numerator));
    }

    static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
