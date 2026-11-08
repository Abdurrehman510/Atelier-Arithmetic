package com.mathquiz.model;

/**
 * One exact arithmetic step of a worked solution (premium roadmap A8), e.g. {@code 47 + 38 = 85}.
 * A question's steps, applied in order, end at its answer.
 *
 * <p>Invariants (checked on construction): {@code op} is one of {@code + - * /};
 * {@code result} equals {@code left op right} exactly, with no {@code int} overflow;
 * division has a non-zero divisor and no remainder. Negative values are allowed
 * (algebra questions can show negative numbers).</p>
 */
public record SolutionStep(char op, int left, int right, int result) {

    public SolutionStep {
        if (apply(op, left, right) != result) {
            throw new IllegalArgumentException(
                    "step does not add up: " + left + " " + op + " " + right + " != " + result);
        }
    }

    /** Builds a step and computes its result; throws if the operation is not exact. */
    public static SolutionStep of(char op, int left, int right) {
        return new SolutionStep(op, left, right, apply(op, left, right));
    }

    private static int apply(char op, int left, int right) {
        switch (op) {
            case '+':
                return Math.addExact(left, right);
            case '-':
                return Math.subtractExact(left, right);
            case '*':
                return Math.multiplyExact(left, right);
            case '/':
                if (right == 0 || left % right != 0) {
                    throw new IllegalArgumentException("inexact division: " + left + " / " + right);
                }
                return left / right;
            default:
                throw new IllegalArgumentException("unsupported operator: " + op);
        }
    }
}
