package com.mathquiz.view;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Home dashboard columns: side by side when both fit, otherwise the right column moves below. */
class TwoColumnLayoutTest {

    private static JPanel column(int minWidth, int prefWidth, int prefHeight) {
        JPanel p = new JPanel();
        p.setMinimumSize(new Dimension(minWidth, 50));
        p.setPreferredSize(new Dimension(prefWidth, prefHeight));
        return p;
    }

    private static JPanel container(int width, int height, JPanel left, JPanel right) {
        JPanel c = new JPanel(new TwoColumnLayout(0.58, 16));
        c.add(left);
        c.add(right);
        c.setSize(width, height);
        c.doLayout();
        return c;
    }

    @Test
    void sideBySideWhenBothColumnsFit() {
        JPanel left = column(300, 600, 400);
        JPanel right = column(380, 380, 500);
        container(800, 600, left, right);
        assertEquals(new Rectangle(0, 0, 404, 600), left.getBounds(), "left gives way so right keeps its minimum");
        assertEquals(new Rectangle(420, 0, 380, 600), right.getBounds());
    }

    @Test
    void wideWindowsSplitByShare() {
        JPanel left = column(300, 600, 400);
        JPanel right = column(380, 380, 500);
        container(1316, 600, left, right);
        assertEquals(754, left.getWidth(), "58 % of 1300");
        assertEquals(546, right.getWidth());
    }

    @Test
    void stackedWhenTheColumnsCannotBothFit() {
        JPanel left = column(300, 600, 400);
        JPanel right = column(380, 380, 500);
        JPanel c = container(600, 300, left, right);
        assertEquals(new Rectangle(0, 0, 600, 400), left.getBounds());
        assertEquals(new Rectangle(0, 416, 600, 500), right.getBounds());
        Dimension pref = c.getLayout().preferredLayoutSize(c);
        assertEquals(916, pref.height, "stacked height = left + gap + right, so a scroll pane can scroll it");
        assertTrue(c.getLayout().minimumLayoutSize(c).width <= 380, "stacking needs only the wider minimum");
    }
}
