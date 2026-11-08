package com.mathquiz.testutil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestHomeTest {

    @Test
    void acceptsTheThrowAwayHomeWithEitherSeparator() {
        assertDoesNotThrow(() -> TestHome.check("D:/MathQuizApp/build/test/home"));
        assertDoesNotThrow(() -> TestHome.check("D:\\MathQuizApp\\build\\test\\home"));
    }

    @Test
    void rejectsARealHomeFolder() {
        assertThrows(IllegalStateException.class, () -> TestHome.check("C:/Users/HP"));
        assertThrows(IllegalStateException.class, () -> TestHome.check("/home/parent"));
        assertThrows(IllegalStateException.class, () -> TestHome.check(null));
    }
}
