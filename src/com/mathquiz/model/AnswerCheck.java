package com.mathquiz.model;

/**
 * The result of checking what a child typed against a question's answer.
 * {@code value} is the parsed number and is meaningful only when {@link #isAnswered()} is true.
 */
public record AnswerCheck(Status status, int value) {

    /** What the typed text turned out to be. */
    public enum Status {
        /** Nothing typed (or only spaces). */
        EMPTY,
        /** Text that is not a whole number that fits in an {@code int}. */
        NOT_A_WHOLE_NUMBER,
        /** A whole number equal to the answer. */
        CORRECT,
        /** A whole number different from the answer. */
        INCORRECT
    }

    /** True when the input was a whole number (correct or not). */
    public boolean isAnswered() {
        return status == Status.CORRECT || status == Status.INCORRECT;
    }
}
