package com.mathquiz.testutil;

import com.mathquiz.model.QuestionResult;

/**
 * Builds {@link QuestionResult}s for tests. This is the only place tests construct results,
 * so a change to the QuestionResult constructor is absorbed here.
 */
public final class Results {

    private Results() {
    }

    /** A correctly answered question that took one second. */
    public static QuestionResult correct(String expression, int answer) {
        return new QuestionResult(expression, answer, answer, true, 1000L);
    }

    /** A wrongly answered question that took one second. */
    public static QuestionResult wrong(String expression, int answer, int given) {
        return new QuestionResult(expression, answer, given, false, 1000L);
    }
}
