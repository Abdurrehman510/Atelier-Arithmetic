package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import com.mathquiz.model.SolutionStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.mathquiz.model.Skill.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generator invariants over 10,000 samples per category × difficulty (R-DOM-11, R-TST-03):
 * well-formed questions, the expected skills, and an exact solution that ends at the answer
 * (premium roadmap A1, A8).
 */
class QuestionGeneratorInvariantTest {

    private static final int SAMPLES = 10_000;
    private static final List<String> CORE = List.of("Addition", "Difference", "Multiplication", "Division");

    /** category → difficulty → exact set of skills seen over the samples. */
    private static final Map<String, Map<String, Set<Skill>>> EXPECTED_SKILLS = new HashMap<>();

    static {
        expect("Addition", "Easy", ADD_2DIGIT_2TERMS);
        expect("Addition", "Medium", ADD_3DIGIT_3TERMS);
        expect("Addition", "Hard", ADD_4DIGIT_4TERMS);
        expect("Difference", "Easy", SUB_2DIGIT_2TERMS);
        expect("Difference", "Medium", SUB_3DIGIT_3TERMS);
        expect("Difference", "Hard", SUB_4DIGIT_4TERMS);
        expect("Multiplication", "Easy", MUL_2DIGIT_BY_1DIGIT);
        expect("Multiplication", "Medium", MUL_2DIGIT_3FACTORS);
        expect("Multiplication", "Hard", MUL_3DIGIT_3FACTORS);
        expect("Division", "Easy", DIV_BASIC);
        expect("Division", "Medium", DIV_CHAINED);
        expect("Division", "Hard", DIV_2DIGIT_QUOTIENT);

        for (String difficulty : GoldenMasterWriter.DIFFICULTIES) {
            expect("Special", difficulty, ORD_SUM_TIMES_DIFF, ORD_DIFF_OVER_SUM, ORD_QUOTIENT_SUM,
                    ORD_PRODUCT_OVER_PRODUCT, ORD_QUOTIENT_MINUS_PRODUCT);
        }
        expect("Fractions", "Easy", FRAC_OF_QUANTITY);
        expect("Fractions", "Medium", FRAC_MIXED_TO_IMPROPER, FRAC_SIMPLIFY, FRAC_OF_QUANTITY);
        expect("Fractions", "Hard", PCT_OF_QUANTITY, RATIO_MISSING_PART, FRAC_PRODUCT_OF_QUANTITY);

        expect("Patterns", "Easy", PAT_ARITHMETIC, PAT_ALTERNATING_STEPS);
        expect("Patterns", "Medium", PAT_GROWING_STEP, PAT_GEOMETRIC, PAT_ALTERNATING_OPS);
        expect("Patterns", "Hard", PAT_FIBONACCI, PAT_SQUARES_OFFSET, PAT_INTERLEAVED, PAT_POLYNOMIAL);

        expect("Algebra", "Easy", ALG_ONE_STEP_ADD, ALG_ONE_STEP_SUB, ALG_ONE_STEP_SUB_FROM, ALG_ONE_STEP_MUL);
        expect("Algebra", "Medium", ALG_TWO_STEP, ALG_TWO_STEP_SUB_FROM, ALG_TWO_STEP_DIV);
        expect("Algebra", "Hard", ALG_VARS_BOTH_SIDES, ALG_SIMULTANEOUS_SUM_DIFF, ALG_SIMULTANEOUS_ELIMINATION);

        expect("Measurement", "Easy", MEAS_CONVERT_LENGTH, MEAS_CONVERT_MASS, MEAS_CONVERT_CAPACITY);
        expect("Measurement", "Medium", TIME_CONVERT, TIME_CONVERT_COMPOUND, GEO_PERIMETER_RECT,
                GEO_AREA_SQUARE, GEO_AREA_RECT);
        expect("Measurement", "Hard", MEAS_CONVERT_LENGTH_COMPOUND, MEAS_CONVERT_CAPACITY_COMPOUND,
                GEO_AREA_FROM_PERIMETER, GEO_PERIMETER_FROM_AREA, GEO_VOLUME_PRISM);
    }

    private static void expect(String category, String difficulty, Skill... skills) {
        Set<Skill> set = EnumSet.noneOf(Skill.class);
        set.addAll(List.of(skills));
        EXPECTED_SKILLS.computeIfAbsent(category, c -> new HashMap<>()).put(difficulty, set);
    }

    private static Set<Skill> expectedFor(String category, String difficulty) {
        if (category.equals("Mixed")) {
            Set<Skill> union = EnumSet.noneOf(Skill.class);
            for (String core : CORE) {
                union.addAll(EXPECTED_SKILLS.get(core).get(difficulty));
            }
            return union;
        }
        return EXPECTED_SKILLS.get(category).get(difficulty);
    }

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
        Set<Skill> seen = EnumSet.noneOf(Skill.class);
        for (int i = 0; i < SAMPLES; i++) {
            Question q = generator.generateSeeded(difficulty, category, 1_000_000L + i, i % 10);
            String expression = q.getExpression();
            assertFalse(expression.isBlank(), "blank expression");
            assertTrue(q.getCorrectAnswer() >= 1, () -> "answer below 1: " + expression + " = " + q.getCorrectAnswer());
            assertTrue(expression.chars().noneMatch(c -> c == '×' || c == '÷' || c == '−'),
                    () -> "non-ASCII operator (R-DOM-03): " + expression);
            assertNotEquals(Skill.CUSTOM, q.getSkill(), expression);
            List<SolutionStep> steps = q.getSolution();
            assertFalse(steps.isEmpty(), () -> "no solution steps: " + expression);
            assertEquals(q.getCorrectAnswer(), steps.get(steps.size() - 1).result(), expression);
            seen.add(q.getSkill());
        }
        assertEquals(expectedFor(category, difficulty), seen, category + " " + difficulty);
    }

    @Test
    void everySkillExceptCustomIsProducedSomewhere() {
        Set<Skill> union = EnumSet.noneOf(Skill.class);
        EXPECTED_SKILLS.values().forEach(byDifficulty -> byDifficulty.values().forEach(union::addAll));
        assertEquals(EnumSet.complementOf(EnumSet.of(Skill.CUSTOM)), union);
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

    @Test
    void unknownCategoryUsesAnAdditionSkill() {
        assertEquals(Skill.ADD_2DIGIT_2TERMS, new QuestionGenerator().generateSeeded("Easy", "Geometry", 7L, 0).getSkill());
    }
}
