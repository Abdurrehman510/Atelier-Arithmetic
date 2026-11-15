package com.mathquiz.view;

import org.junit.jupiter.api.Test;

import static com.mathquiz.view.GridNavigation.Direction.DOWN;
import static com.mathquiz.view.GridNavigation.Direction.LEFT;
import static com.mathquiz.view.GridNavigation.Direction.RIGHT;
import static com.mathquiz.view.GridNavigation.Direction.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * BUG-16: arrow keys on the 3-column category grid (10 cards: rows 0-2, 3-5, 6-8, 9).
 * Up/Down keep the column and stop at the edge; Left/Right walk the cards in order and wrap.
 */
class GridNavigationTest {

    private static int move(int index, GridNavigation.Direction d) {
        return GridNavigation.move(index, d, 3, 10);
    }

    @Test
    void downKeepsTheColumnAndStopsAtTheBottom() {
        assertEquals(4, move(1, DOWN));
        assertEquals(7, move(4, DOWN));
        assertEquals(7, move(7, DOWN), "no card below the 8th card: stay");
        assertEquals(8, move(8, DOWN), "used to wrap to the 2nd card");
        assertEquals(9, move(6, DOWN));
        assertEquals(9, move(9, DOWN));
    }

    @Test
    void upKeepsTheColumnAndStopsAtTheTop() {
        assertEquals(6, move(9, UP));
        assertEquals(2, move(5, UP));
        assertEquals(0, move(0, UP));
        assertEquals(2, move(2, UP), "used to wrap to the 10th card");
    }

    @Test
    void leftAndRightWalkInOrderAndWrap() {
        assertEquals(5, move(4, RIGHT));
        assertEquals(0, move(9, RIGHT));
        assertEquals(9, move(0, LEFT));
    }
}
