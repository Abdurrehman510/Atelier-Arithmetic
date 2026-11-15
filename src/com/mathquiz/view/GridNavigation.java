package com.mathquiz.view;

/**
 * Arrow-key movement in a grid of cards laid out row by row (BUG-16).
 * Up/Down keep the column and stop at the top/bottom (a short last row is respected);
 * Left/Right walk the cards in reading order and wrap around.
 */
final class GridNavigation {

    enum Direction { LEFT, RIGHT, UP, DOWN }

    private GridNavigation() {
    }

    /**
     * @param index   the focused card (0-based)
     * @param columns cards per row
     * @param count   number of cards
     * @return the card to focus next (unchanged when there is nowhere to go)
     */
    static int move(int index, Direction direction, int columns, int count) {
        switch (direction) {
            case LEFT:
                return (index - 1 + count) % count;
            case RIGHT:
                return (index + 1) % count;
            case UP:
                return index - columns >= 0 ? index - columns : index;
            case DOWN:
                return index + columns < count ? index + columns : index;
            default:
                return index;
        }
    }
}
