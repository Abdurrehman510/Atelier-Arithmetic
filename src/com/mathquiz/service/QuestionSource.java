package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.QuizSession;

import java.util.List;

/**
 * Picks the next question for a running session (architecture §6.5), moved out of
 * {@code GamePanel} so the rule is testable (rules.md R-ARC-03). Precedence:
 * custom question list → Daily Challenge (seeded Mixed) → generator by category.
 */
public final class QuestionSource {

    /** The next question and the difficulty it was generated at. */
    public record Prepared(Question question, String difficulty) {
    }

    private final QuestionGenerator generator;

    public QuestionSource(QuestionGenerator generator) {
        this.generator = generator;
    }

    /**
     * @param customQuestions the custom quiz's questions, or {@code null} for generated quizzes
     * @param todaySeed       {@code yyyyMMdd} as a number, used only for the Daily Challenge
     */
    public Prepared next(QuizSession session, List<Question> customQuestions, long todaySeed) {
        String difficulty = session.getDifficulty();
        int index = session.getCurrentQuestionIndex();
        Question question;
        if (customQuestions != null) {
            question = customQuestions.get(index);
        } else if (session.getCategory().equalsIgnoreCase("Daily Challenge")) {
            question = generator.generateSeeded(difficulty, "Mixed", todaySeed, index);
        } else {
            question = generator.generate(difficulty, session.getCategory());
        }
        return new Prepared(question, difficulty);
    }
}
