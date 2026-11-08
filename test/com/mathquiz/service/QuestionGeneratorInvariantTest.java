package com.mathquiz.service;

import com.mathquiz.model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generator invariants over 10,000 samples per category × difficulty (R-DOM-11, R-TST-03).
 */
class QuestionGeneratorInvariantTest {

    private static final int SAMPLES = 10_000;

    static Stream<Arguments> categoriesAndDifficulties() {
        List<Arguments> combos = new ArrayList<>();
        for (String category : GoldenMasterWriter.CATEGORIES) {
            for (String difficulty : GoldenMasterWriter.DIFFICULTIES) {
                combos.add(Arguments.of(category, difficulty));
            }
        }
        return combos.stream();
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("categoriesAndDifficulties")
    void everyQuestionIsWellFormed(String category, String difficulty) {
        QuestionGenerator generator = new QuestionGenerator();
        for (int i = 0; i < SAMPLES; i++) {
            Question q = generator.generateSeeded(difficulty, category, 1_000_000L + i, i % 10);
            String expression = q.getExpression();
            assertFalse(expression.isBlank(), () -> "blank expression for seed index " + expression);
            assertTrue(q.getCorrectAnswer() >= 1, () -> "answer below 1: " + expression + " = " + q.getCorrectAnswer());
            assertTrue(expression.chars().noneMatch(c -> c == '×' || c == '÷' || c == '−'),
                    () -> "non-ASCII operator (R-DOM-03): " + expression);
        }
    }

    @Test
    void unknownCategoryFallsBackToAddition() {
        QuestionGenerator generator = new QuestionGenerator();
        for (long seed = 0; seed < 100; seed++) {
            Question unknown = generator.generateSeeded("Easy", "Geometry", seed, 0);
            Question addition = generator.generateSeeded("Easy", "Addition", seed, 0);
            assertEquals(addition.getExpression(), unknown.getExpression());
            assertEquals(addition.getCorrectAnswer(), unknown.getCorrectAnswer());
        }
    }
}
