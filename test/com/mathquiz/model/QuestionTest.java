package com.mathquiz.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    @Test
    void customConstructorUsesCustomSkillAndNoSteps() {
        Question q = new Question("5 * 12 + 6", 66);
        assertEquals(Skill.CUSTOM, q.getSkill());
        assertTrue(q.getSolution().isEmpty());
    }

    @Test
    void solutionMustEndAtTheAnswer() {
        assertThrows(IllegalArgumentException.class, () -> new Question("47 + 38", 86,
                Skill.ADD_2DIGIT_2TERMS, List.of(SolutionStep.of('+', 47, 38))));
    }

    @Test
    void solutionIsUnmodifiable() {
        Question q = new Question("47 + 38", 85, Skill.ADD_2DIGIT_2TERMS, List.of(SolutionStep.of('+', 47, 38)));
        assertThrows(UnsupportedOperationException.class, () -> q.getSolution().add(SolutionStep.of('+', 1, 1)));
    }

    @Test
    void checkOfNullIsEmpty() {
        assertEquals(AnswerCheck.Status.EMPTY, new Question("6 * 7", 42).check(null).status());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', value = {
            "42|CORRECT|42", "\" 42 \"|CORRECT|42", "042|CORRECT|42", "+42|CORRECT|42", "٤٢|CORRECT|42",
            "41|INCORRECT|41", "-42|INCORRECT|-42",
            "\"\"|EMPTY|0", "\"   \"|EMPTY|0",
            "abc|NOT_A_WHOLE_NUMBER|0", "4 2|NOT_A_WHOLE_NUMBER|0", "3.5|NOT_A_WHOLE_NUMBER|0",
            "99999999999|NOT_A_WHOLE_NUMBER|0"})
    void checkMatchesTodaysParsingRules(String input, AnswerCheck.Status status, int value) {
        AnswerCheck check = new Question("6 * 7", 42).check(input);
        assertEquals(status, check.status(), () -> "input [" + input + "]");
        if (check.isAnswered()) {
            assertEquals(value, check.value(), () -> "input [" + input + "]");
        }
    }
}
