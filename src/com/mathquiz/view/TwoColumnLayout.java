package com.mathquiz.view;

import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Lays out exactly two columns (component 0 = left, 1 = right). When both fit at their minimum widths
 * they sit side by side, split by {@code leftShare} and filling the height; otherwise the right column
 * moves below the left one at full width, so nothing is clipped and a scroll pane can scroll the result
 * (Home dashboard at small windows / large text, rules.md R-UI-07).
 */
final class TwoColumnLayout implements LayoutManager {

    private final double leftShare;
    private final int gap;

    TwoColumnLayout(double leftShare, int gap) {
        this.leftShare = leftShare;
        this.gap = gap;
    }

    private boolean sideBySide(Container parent, int innerWidth) {
        return innerWidth >= parent.getComponent(0).getMinimumSize().width + gap
                + parent.getComponent(1).getMinimumSize().width;
    }

    /** Width available for the columns: the viewport's when scrolling (the view tracks it), else our own. */
    private static int innerWidth(Container parent) {
        Insets in = parent.getInsets();
        int width = parent.getParent() instanceof JViewport ? parent.getParent().getWidth() : parent.getWidth();
        return width - in.left - in.right;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Insets in = parent.getInsets();
        Dimension left = parent.getComponent(0).getPreferredSize();
        Dimension right = parent.getComponent(1).getPreferredSize();
        int width = innerWidth(parent);
        if (width <= 0 || sideBySide(parent, width)) {
            return new Dimension(left.width + gap + right.width + in.left + in.right,
                    Math.max(left.height, right.height) + in.top + in.bottom);
        }
        return new Dimension(Math.max(left.width, right.width) + in.left + in.right,
                left.height + gap + right.height + in.top + in.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        Insets in = parent.getInsets();
        Dimension left = parent.getComponent(0).getMinimumSize();
        Dimension right = parent.getComponent(1).getMinimumSize();
        return new Dimension(Math.max(left.width, right.width) + in.left + in.right,
                Math.max(left.height, right.height) + in.top + in.bottom);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets in = parent.getInsets();
        int width = parent.getWidth() - in.left - in.right;
        int height = parent.getHeight() - in.top - in.bottom;
        Component left = parent.getComponent(0);
        Component right = parent.getComponent(1);
        if (sideBySide(parent, width)) {
            int leftWidth = (int) Math.round((width - gap) * leftShare);
            leftWidth = Math.max(left.getMinimumSize().width,
                    Math.min(leftWidth, width - gap - right.getMinimumSize().width));
            left.setBounds(in.left, in.top, leftWidth, height);
            right.setBounds(in.left + leftWidth + gap, in.top, width - gap - leftWidth, height);
        } else {
            int leftHeight = left.getPreferredSize().height;
            left.setBounds(in.left, in.top, width, leftHeight);
            right.setBounds(in.left, in.top + leftHeight + gap, width, right.getPreferredSize().height);
        }
        // Inside a scroll pane our preferred size is asked for before the viewport has its new width, so the
        // first pass after a resize can reserve the wrong height. If the content now needs more room than it
        // was given, ask for one more pass (it converges: the next preferred size sees the right width).
        if (parent.getParent() instanceof JViewport
                && preferredLayoutSize(parent).height > parent.getHeight()) {
            SwingUtilities.invokeLater(parent::revalidate);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }
}
