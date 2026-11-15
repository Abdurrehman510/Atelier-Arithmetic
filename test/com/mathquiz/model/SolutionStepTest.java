package com.mathquiz.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolutionStepTest {

    @Test
    void ofComputesExactResults() {
        assertEquals(85, SolutionStep.of('+', 47, 38).result());
        assertEquals(25, SolutionStep.of('-', 43, 18).result());
        assertEquals(-2, SolutionStep.of('-', 3, 5).result());
        assertEquals(84, SolutionStep.of('*', 12, 7).result());
        assertEquals(12, SolutionStep.of('/', 84, 7).result());
    }

    @Test
    void rejectsInexactOrInvalidSteps() {
        assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('/', 10, 4));
        assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('/', 5, 0));
        assertThrows(IllegalArgumentException.class, () -> SolutionStep.of('%', 1, 2));
        assertThrows(ArithmeticException.class, () -> SolutionStep.of('*', Integer.MAX_VALUE, 2));
        assertThrows(IllegalArgumentException.class, () -> new SolutionStep('+', 2, 2, 5));
    }
}
