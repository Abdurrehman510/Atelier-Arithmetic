package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;

import java.util.List;
import java.util.Random;

/**
 * "Solve for x" questions (PRD §9). Random draws match the original generator exactly,
 * including its adjustment loops (R-DOM-04). The solution undoes the operations around x.
 * {@code diff} is lower-case.
 */
final class AlgebraQuestions {

    private AlgebraQuestions() {
    }

    static Question algebra(String diff, Random rng) {
        if (diff.equals("easy")) {
            int subType = rng.nextInt(4);
            if (subType == 0) { // x + A = B
                int x = rng.nextInt(14) + 2;
                int a = rng.nextInt(19) + 2;
                return new Question("Solve for x: x + " + a + " = " + (x + a), x, Skill.ALG_ONE_STEP_ADD,
                        List.of(SolutionStep.of('-', x + a, a)));
            } else if (subType == 1) { // x - A = B  (B may be negative)
                int x = rng.nextInt(14) + 2;
                int a = rng.nextInt(19) + 2;
                return new Question("Solve for x: x - " + a + " = " + (x - a), x, Skill.ALG_ONE_STEP_SUB,
                        List.of(SolutionStep.of('+', x - a, a)));
            } else if (subType == 2) { // A - x = B
                int x = rng.nextInt(14) + 2;
                int a = x + rng.nextInt(19) + 2;
                return new Question("Solve for x: " + a + " - x = " + (a - x), x, Skill.ALG_ONE_STEP_SUB_FROM,
                        List.of(SolutionStep.of('-', a, a - x)));
            } else { // Ax = B
                int x = rng.nextInt(11) + 2;
                int a = rng.nextInt(9) + 2;
                return new Question("Solve for x: " + a + "x = " + (a * x), x, Skill.ALG_ONE_STEP_MUL,
                        List.of(SolutionStep.of('/', a * x, a)));
            }
        } else if (diff.equals("medium")) {
            int subType = rng.nextInt(4);
            if (subType == 0) { // Ax + B = C
                int x = rng.nextInt(9) + 2;
                int a = rng.nextInt(7) + 2;
                int b = rng.nextInt(15) + 1;
                SolutionStep undo = SolutionStep.of('-', a * x + b, b);
                return new Question("Solve for x: " + a + "x + " + b + " = " + (a * x + b), x, Skill.ALG_TWO_STEP,
                        List.of(undo, SolutionStep.of('/', undo.result(), a)));
            } else if (subType == 1) { // Ax - B = C
                int x = rng.nextInt(9) + 2;
                int a = rng.nextInt(7) + 2;
                int b = rng.nextInt(15) + 1;
                SolutionStep undo = SolutionStep.of('+', a * x - b, b);
                return new Question("Solve for x: " + a + "x - " + b + " = " + (a * x - b), x, Skill.ALG_TWO_STEP,
                        List.of(undo, SolutionStep.of('/', undo.result(), a)));
            } else if (subType == 2) { // B - Ax = C, where B = Ax + C
                int x = rng.nextInt(9) + 2;
                int a = rng.nextInt(7) + 2;
                int c = rng.nextInt(15) + 1;
                SolutionStep undo = SolutionStep.of('-', a * x + c, c);
                return new Question("Solve for x: " + (a * x + c) + " - " + a + "x = " + c, x,
                        Skill.ALG_TWO_STEP_SUB_FROM, List.of(undo, SolutionStep.of('/', undo.result(), a)));
            } else { // (x + B) / A = C  →  x = A*C - B
                int x = rng.nextInt(14) + 2;
                int a = rng.nextInt(5) + 2;
                int c = rng.nextInt(7) + 2;
                int b = a * c - x;
                while (b <= 0) {
                    c++;
                    b = a * c - x;
                }
                SolutionStep undo = SolutionStep.of('*', a, c);
                return new Question("Solve for x: (x + " + b + ") / " + a + " = " + c, x, Skill.ALG_TWO_STEP_DIV,
                        List.of(undo, SolutionStep.of('-', undo.result(), b)));
            }
        } else {
            int subType = rng.nextInt(4);
            if (subType == 0) { // Ax + B = Cx + D  →  (A-C)x = D-B
                int x = rng.nextInt(7) + 2;
                int c = rng.nextInt(5) + 2;
                int a = c + rng.nextInt(5) + 1;
                int b = rng.nextInt(20) + 1;
                int d = b + (a - c) * x;
                SolutionStep k = SolutionStep.of('-', a, c);
                SolutionStep t = SolutionStep.of('-', d, b);
                return new Question("Solve for x: " + a + "x + " + b + " = " + c + "x + " + d, x,
                        Skill.ALG_VARS_BOTH_SIDES, List.of(k, t, SolutionStep.of('/', t.result(), k.result())));
            } else if (subType == 1) { // Ax - B = Cx + D  →  (A-C)x = D+B
                int x = rng.nextInt(7) + 2;
                int c = rng.nextInt(5) + 2;
                int a = c + rng.nextInt(5) + 1;
                int d = rng.nextInt(20) + 1;
                int b = (a - c) * x - d;
                while (b <= 0) {
                    x++;
                    b = (a - c) * x - d;
                }
                SolutionStep k = SolutionStep.of('-', a, c);
                SolutionStep t = SolutionStep.of('+', d, b);
                return new Question("Solve for x: " + a + "x - " + b + " = " + c + "x + " + d, x,
                        Skill.ALG_VARS_BOTH_SIDES, List.of(k, t, SolutionStep.of('/', t.result(), k.result())));
            } else if (subType == 2) { // x + y = S and x - y = D
                int x = rng.nextInt(16) + 5;
                int y = rng.nextInt(x - 2) + 1;
                int sum = x + y;
                int difference = x - y;
                SolutionStep added = SolutionStep.of('+', sum, difference);
                return new Question("If x + y = " + sum + " and x - y = " + difference + ", what is x?", x,
                        Skill.ALG_SIMULTANEOUS_SUM_DIFF, List.of(added, SolutionStep.of('/', added.result(), 2)));
            } else { // x + y = S and 2x + y = T
                int x = rng.nextInt(13) + 3;
                int y = rng.nextInt(15) + 1;
                int sum = x + y;
                int doubleSum = 2 * x + y;
                return new Question("If x + y = " + sum + " and 2x + y = " + doubleSum + ", what is x?", x,
                        Skill.ALG_SIMULTANEOUS_ELIMINATION, List.of(SolutionStep.of('-', doubleSum, sum)));
            }
        }
    }
}
