package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.view.tour.MascotPainter;
import com.mathquiz.view.tour.TourStep.MascotExpression;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Archie the Owl ({@link MascotPainter}) wearing the current child's equipped Mascot Shop item — or, for
 * a shop preview, a fixed item — so a purchase shows wherever Archie appears (BUG-22). Transparent, so
 * he suits light and dark mode. His height follows his font, so the text-size setting scales him too
 * (rules.md R-UI-15).
 */
public final class ArchieView extends JComponent {

    /** The box, in body-size-100 units around his body's centre, that holds every shop item and every
     *  expression used in the app (the tour's raised wing for WAVING / POINTING may reach outside it). */
    static final int HALF_WIDTH = 72;
    static final int TOP = -100;
    static final int BOTTOM = 70;
    private static final double HEIGHT_PER_POINT = 4.6;     // 13 pt font → 60 px tall at 100 % text size

    private final String fixedItem;                         // null: follow the child's equipped item
    private MascotExpression expression;

    /** Archie wearing whatever the current child has equipped (read each time he is painted). */
    ArchieView(MascotExpression expression) {
        this(null, expression);
    }

    /** Archie wearing {@code itemId}, for shop previews. */
    static ArchieView wearing(String itemId) {
        return new ArchieView(itemId, MascotExpression.HAPPY);
    }

    private ArchieView(String fixedItem, MascotExpression expression) {
        this.fixedItem = fixedItem;
        this.expression = expression;
        setOpaque(false);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setToolTipText("Archie the Owl");
    }

    String itemId() {
        return fixedItem != null ? fixedItem : AppConfig.getInstance().getEquippedItem();
    }

    MascotExpression getExpression() {
        return expression;
    }

    void setExpression(MascotExpression expression) {
        if (expression != this.expression) {
            this.expression = expression;
            repaint();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        int height = (int) Math.round(getFont().getSize2D() * HEIGHT_PER_POINT);
        int width = (int) Math.round(height * (2.0 * HALF_WIDTH) / (BOTTOM - TOP));
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        try {
            double scale = Math.min(getWidth() / (2.0 * HALF_WIDTH), getHeight() / (double) (BOTTOM - TOP));
            g.translate(getWidth() / 2.0, getHeight() / 2.0);
            g.scale(scale, scale);
            // Body centre placed so the reserved box is centred in the component
            MascotPainter.draw(g, 0, -(TOP + BOTTOM) / 2, 100, expression, itemId());
        } finally {
            g.dispose();
        }
    }
}
