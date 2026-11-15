package com.mathquiz.service;

/**
 * How many questions a quiz may have (PRD FR-SET-02, decision Q6, BUG-15).
 */
public final class QuestionCounts {

    /** Fewest questions in a quiz. */
    public static final int MIN = 1;
    /** Most questions in a quiz (decision Q6: keeps sessions, history and reports manageable). */
    public static final int MAX = 100;

    private QuestionCounts() {
    }

    /**
     * Checks what was typed in the "how many questions" field.
     *
     * @return {@code null} when acceptable, otherwise the child-friendly message to show
     */
    public static String validate(String raw) {
        int count;
        try {
            count = Integer.parseInt(raw == null ? "" : raw.trim());
        } catch (NumberFormatException e) {
            return "Please enter a number for how many questions you want!";
        }
        if (count < MIN) {
            return "Please enter at least 1 question!";
        }
        if (count > MAX) {
            return "That's a lot! Please choose 100 questions or fewer.";
        }
        return null;
    }
}
