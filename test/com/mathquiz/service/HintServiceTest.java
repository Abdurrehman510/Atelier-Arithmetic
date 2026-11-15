package com.mathquiz.service;

import com.mathquiz.model.Question;
import com.mathquiz.model.Skill;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static com.mathquiz.model.Skill.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-07: every generated question gets the hint for its own skill. */
class HintServiceTest {

    /** A phrase (lower-case) the hint for each skill must contain. */
    private static final Map<Skill, String> KEY_PHRASE = new EnumMap<>(Skill.class);

    static {
        for (Skill s : EnumSet.of(ADD_2DIGIT_2TERMS, ADD_3DIGIT_3TERMS, ADD_4DIGIT_4TERMS)) KEY_PHRASE.put(s, "add");
        for (Skill s : EnumSet.of(SUB_2DIGIT_2TERMS, SUB_3DIGIT_3TERMS, SUB_4DIGIT_4TERMS)) KEY_PHRASE.put(s, "subtract");
        for (Skill s : EnumSet.of(MUL_2DIGIT_BY_1DIGIT, MUL_2DIGIT_3FACTORS, MUL_3DIGIT_3FACTORS)) KEY_PHRASE.put(s, "multiply");
        KEY_PHRASE.put(DIV_BASIC, "times");
        KEY_PHRASE.put(DIV_2DIGIT_QUOTIENT, "times");
        KEY_PHRASE.put(DIV_CHAINED, "bracket");
        for (Skill s : EnumSet.of(ORD_SUM_TIMES_DIFF, ORD_DIFF_OVER_SUM, ORD_QUOTIENT_SUM,
                ORD_PRODUCT_OVER_PRODUCT, ORD_QUOTIENT_MINUS_PRODUCT)) KEY_PHRASE.put(s, "bracket");
        KEY_PHRASE.put(FRAC_OF_QUANTITY, "bottom number");
        KEY_PHRASE.put(FRAC_PRODUCT_OF_QUANTITY, "bottom number");
        KEY_PHRASE.put(FRAC_MIXED_TO_IMPROPER, "whole number");
        KEY_PHRASE.put(FRAC_SIMPLIFY, "divides both");
        KEY_PHRASE.put(PCT_OF_QUANTITY, "100");
        KEY_PHRASE.put(RATIO_MISSING_PART, "ratio");
        for (Skill s : EnumSet.of(PAT_ARITHMETIC, PAT_ALTERNATING_STEPS, PAT_GROWING_STEP, PAT_GEOMETRIC,
                PAT_ALTERNATING_OPS, PAT_FIBONACCI, PAT_SQUARES_OFFSET, PAT_INTERLEAVED, PAT_POLYNOMIAL)) {
            KEY_PHRASE.put(s, "rule");
        }
        for (Skill s : EnumSet.of(ALG_ONE_STEP_ADD, ALG_ONE_STEP_SUB, ALG_ONE_STEP_SUB_FROM, ALG_ONE_STEP_MUL,
                ALG_TWO_STEP, ALG_TWO_STEP_SUB_FROM, ALG_TWO_STEP_DIV, ALG_VARS_BOTH_SIDES)) {
            KEY_PHRASE.put(s, "isolate x");
        }
        KEY_PHRASE.put(ALG_SIMULTANEOUS_SUM_DIFF, "equations");
        KEY_PHRASE.put(ALG_SIMULTANEOUS_ELIMINATION, "equations");
        KEY_PHRASE.put(MEAS_CONVERT_LENGTH, "unit fact");
        KEY_PHRASE.put(MEAS_CONVERT_MASS, "1 kilogram = 1000 grams");
        KEY_PHRASE.put(MEAS_CONVERT_CAPACITY, "1 liter = 1000 milliliters");
        KEY_PHRASE.put(TIME_CONVERT, "1 hour = 60 minutes");
        KEY_PHRASE.put(TIME_CONVERT_COMPOUND, "60");
        KEY_PHRASE.put(GEO_PERIMETER_RECT, "around");
        KEY_PHRASE.put(GEO_AREA_SQUARE, "side × side");
        KEY_PHRASE.put(GEO_AREA_RECT, "length × width");
        KEY_PHRASE.put(MEAS_CONVERT_LENGTH_COMPOUND, "100");
        KEY_PHRASE.put(MEAS_CONVERT_CAPACITY_COMPOUND, "1000");
        KEY_PHRASE.put(GEO_AREA_FROM_PERIMETER, "4 equal sides");
        KEY_PHRASE.put(GEO_PERIMETER_FROM_AREA, "area ÷ length");
        KEY_PHRASE.put(GEO_VOLUME_PRISM, "height");
    }

    @Test
    void everyGeneratedQuestionGetsTheHintForItsSkill() {
        QuestionGenerator generator = new QuestionGenerator();
        Set<Skill> seen = EnumSet.noneOf(Skill.class);
        for (String category : GoldenMasterWriter.CATEGORIES) {
            for (String difficulty : GoldenMasterWriter.DIFFICULTIES) {
                for (int i = 0; i < 2000; i++) {
                    Question q = generator.generateSeeded(difficulty, category, 5_000L + i, i % 10);
                    String hint = HintService.generateHint(q).toLowerCase();
                    String phrase = KEY_PHRASE.get(q.getSkill());
                    assertTrue(hint.contains(phrase), () -> q.getSkill() + " [" + q.getExpression() + "] hint lacks '" + phrase + "': " + hint);
                    if (q.getSkill() != PCT_OF_QUANTITY) {
                        assertFalse(hint.contains("percent"), () -> "percentage hint for " + q.getSkill() + ": " + q.getExpression());
                    }
                    seen.add(q.getSkill());
                }
            }
        }
        assertEquals(EnumSet.complementOf(EnumSet.of(CUSTOM)), seen);
    }

    @Test
    void customQuestionsAboutAreaDoNotGetThePercentageHint() {
        String hint = HintService.generateHint(new Question("Find the area of a square with side length 4", 16));
        assertFalse(hint.toLowerCase().contains("percent"), hint);
    }

    @Test
    void customFractionAndPercentQuestionsStillGetThoseHints() {
        assertTrue(HintService.generateHint(new Question("What is 2/5 of 40?", 16)).toLowerCase().contains("bottom number"));
        assertTrue(HintService.generateHint(new Question("What is 25% of 80?", 20)).toLowerCase().contains("percent"));
    }
}
