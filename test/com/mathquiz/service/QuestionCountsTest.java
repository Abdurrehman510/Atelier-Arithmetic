package com.mathquiz.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** BUG-15 / decision Q6: a quiz has 1–100 questions. */
class QuestionCountsTest {

    @Test
    void acceptsOneToOneHundred() {
        assertNull(QuestionCounts.validate("1"));
        assertNull(QuestionCounts.validate(" 10 "));
        assertNull(QuestionCounts.validate("100"));
    }

    @Test
    void refusesAboveTheCapWithAFriendlyMessage() {
        assertEquals("That's a lot! Please choose 100 questions or fewer.", QuestionCounts.validate("101"));
        assertEquals("That's a lot! Please choose 100 questions or fewer.", QuestionCounts.validate("99999"));
    }

    @Test
    void keepsTheExistingMessagesForBadInput() {
        assertEquals("Please enter a number for how many questions you want!", QuestionCounts.validate("abc"));
        assertEquals("Please enter a number for how many questions you want!", QuestionCounts.validate("99999999999"));
        assertEquals("Please enter at least 1 question!", QuestionCounts.validate("0"));
        assertEquals("Please enter at least 1 question!", QuestionCounts.validate("-3"));
    }
}
