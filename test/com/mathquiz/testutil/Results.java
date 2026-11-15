package com.mathquiz.testutil;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.Skill;

/**
 * Builds {@link QuestionResult}s for tests. This is the only place tests construct results,
 * so a change to the QuestionResult constructor is absorbed here.
 */
public final class Results {

    private Results() {
    }

    /** A correctly answered Easy addition question, one second, no hints. */
    public static QuestionResult correct(String expression, int answer) {
        return of(expression, answer, answer, Skill.ADD_2DIGIT_2TERMS, "Easy", 0);
    }

    /** A wrongly answered Easy addition question, one second, no hints. */
    public static QuestionResult wrong(String expression, int answer, int given) {
        return of(expression, answer, given, Skill.ADD_2DIGIT_2TERMS, "Easy", 0);
    }

    /** A fully specified result that took one second; correct when {@code given == answer}. */
    public static QuestionResult of(String expression, int answer, int given, Skill skill,
                                    String difficulty, int hints) {
        return new QuestionResult(expression, answer, given, given == answer, 1000L, skill, difficulty, hints);
    }
}
