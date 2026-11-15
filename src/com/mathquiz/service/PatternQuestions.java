package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.List;
import java.util.Random;

/**
 * "Find the next term" pattern questions (PRD §9). Random draws match the original generator
 * exactly (R-DOM-04). The solution computes the missing term from the shown terms and the rule.
 * {@code diff} is lower-case.
 */
final class PatternQuestions {

    private PatternQuestions() {
    }

    static Question patterns(String diff, Random rng) {
        if (diff.equals("easy")) {
            int subType = rng.nextInt(2);
            if (subType == 0) {
                // constant step in [-10, -2] or [2, 10]
                int start = rng.nextInt(30) + 10;
                int step = rng.nextInt(9) + 2;
                if (rng.nextBoolean()) step = -step;
                if (step < 0 && start + 4 * step < 2) {
                    start = start - 4 * step + 5; // keep every term positive
                }
                int s1 = start;
                int s2 = start + step;
                int s3 = start + 2 * step;
                int s4 = start + 3 * step;
                SolutionStep next = step < 0 ? SolutionStep.of('-', s4, -step) : SolutionStep.of('+', s4, step);
                return new Question(pattern(s1, s2, s3, s4), s4 + step, Skill.PAT_ARITHMETIC, List.of(next));
            } else {
                // +a, -b, +a, -b ... with a > b >= 1
                int start = rng.nextInt(20) + 5;
                int a = rng.nextInt(7) + 3;
                int b = rng.nextInt(a - 2) + 1;
                int s1 = start;
                int s2 = start + a;
                int s3 = s2 - b;
                int s4 = s3 + a;
                int s5 = s4 - b;
                return new Question(pattern(s1, s2, s3, s4, s5), s5 + a, Skill.PAT_ALTERNATING_STEPS,
                        List.of(SolutionStep.of('+', s5, a)));
            }
        } else if (diff.equals("medium")) {
            int subType = rng.nextInt(3);
            if (subType == 0) {
                // the step grows by k each time
                int start = rng.nextInt(15) + 5;
                int d = rng.nextInt(5) + 1;
                int k = rng.nextInt(3) + 1;
                int s1 = start;
                int s2 = s1 + d;
                int s3 = s2 + (d + k);
                int s4 = s3 + (d + 2 * k);
                int s5 = s4 + (d + 3 * k);
                SolutionStep last = SolutionStep.of('-', s5, s4);
                SolutionStep nextStep = SolutionStep.of('+', last.result(), k);
                return new Question(pattern(s1, s2, s3, s4, s5), s5 + (d + 4 * k), Skill.PAT_GROWING_STEP,
                        List.of(last, nextStep, SolutionStep.of('+', s5, nextStep.result())));
            } else if (subType == 1) {
                // geometric: start * r^n
                int start = rng.nextInt(5) + 1;
                int r = rng.nextInt(3) + 2;
                int s1 = start;
                int s2 = start * r;
                int s3 = s2 * r;
                int s4 = s3 * r;
                return new Question(pattern(s1, s2, s3, s4), s4 * r, Skill.PAT_GEOMETRIC,
                        List.of(SolutionStep.of('*', s4, r)));
            } else {
                // alternating ×2, +k
                int start = rng.nextInt(4) + 2;
                int r = 2;
                int k = rng.nextInt(5) + 2;
                int s1 = start;
                int s2 = start * r;
                int s3 = s2 + k;
                int s4 = s3 * r;
                int s5 = s4 + k;
                return new Question(pattern(s1, s2, s3, s4, s5), s5 * r, Skill.PAT_ALTERNATING_OPS,
                        List.of(SolutionStep.of('*', s5, r)));
            }
        } else {
            int subType = rng.nextInt(4);
            if (subType == 0) {
                // Fibonacci-like
                int s1 = rng.nextInt(9) + 1;
                int s2 = rng.nextInt(9) + 1;
                int s3 = s1 + s2;
                int s4 = s2 + s3;
                int s5 = s3 + s4;
                int s6 = s4 + s5;
                return new Question(pattern(s1, s2, s3, s4, s5, s6), s5 + s6, Skill.PAT_FIBONACCI,
                        List.of(SolutionStep.of('+', s5, s6)));
            } else if (subType == 1) {
                // n^2 + c or n^2 - c
                int c = rng.nextInt(6) + 1;
                boolean sign = rng.nextBoolean();
                int offset = sign ? c : -c;
                SolutionStep square = SolutionStep.of('*', 6, 6);
                SolutionStep next = sign ? SolutionStep.of('+', square.result(), c)
                        : SolutionStep.of('-', square.result(), c);
                return new Question(pattern(1 + offset, 4 + offset, 9 + offset, 16 + offset, 25 + offset),
                        36 + offset, Skill.PAT_SQUARES_OFFSET, List.of(square, next));
            } else if (subType == 2) {
                // two interleaved sequences: A, B, A+d, B+d, A+2d, B+2d
                int a = rng.nextInt(10) + 5;
                int b = rng.nextInt(20) + 20;
                int d = rng.nextInt(5) + 2;
                int s5 = a + 2 * d;
                return new Question(pattern(a, b, a + d, b + d, s5, b + 2 * d), s5 + d, Skill.PAT_INTERLEAVED,
                        List.of(SolutionStep.of('+', s5, d)));
            } else {
                // n(n+1) or n^2 - n
                boolean isMul = rng.nextBoolean();
                if (isMul) {
                    return new Question(pattern(2, 6, 12, 20, 30), 42, Skill.PAT_POLYNOMIAL,
                            List.of(SolutionStep.of('*', 6, 7)));
                } else {
                    SolutionStep square = SolutionStep.of('*', 6, 6);
                    return new Question(pattern(0, 2, 6, 12, 20), 30, Skill.PAT_POLYNOMIAL,
                            List.of(square, SolutionStep.of('-', square.result(), 6)));
                }
            }
        }
    }

    /** "Pattern: t1, t2, …, _" */
    private static String pattern(int... terms) {
        StringBuilder sb = new StringBuilder("Pattern: ");
        for (int term : terms) {
            sb.append(term).append(", ");
        }
        return sb.append('_').toString();
    }
}
