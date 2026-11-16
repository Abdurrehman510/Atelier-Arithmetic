package com.mathquiz.view;

import com.mathquiz.config.AppConfig;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Builds the real main window (never shown) at a given size and text scale, with the tour and sound
 * off, and finds controls that are clipped. Settings are restored afterwards. Test-only.
 */
final class FrameHarness {

    private FrameHarness() {
    }

    /** Lays the window out at {@code width × height} and {@code scale}, then runs {@code inspect} on the EDT. */
    static <T> T inspect(int width, int height, double scale, Function<QuizFrame, T> inspect) throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "needs a display to lay out the real window");
        AppConfig config = AppConfig.getInstance();
        boolean tourSeen = config.isTourSeen();
        boolean sound = config.isSoundEnabled();
        double oldScale = config.getFontSizeScale();
        config.setTourSeen(true);
        config.setSoundEnabled(false);
        config.setFontSizeScale(scale);
        try {
            QuizFrame[] holder = new QuizFrame[1];
            SwingUtilities.invokeAndWait(() -> holder[0] = new QuizFrame());   // start-up scaling is queued
            List<T> result = new ArrayList<>();
            try {
                SwingUtilities.invokeAndWait(() -> {
                    holder[0].pack();                  // displayable, never shown
                    holder[0].setSize(width, height);
                    holder[0].validate();
                });
                // Like a real window, let follow-up layout passes queued by the first one run before looking
                SwingUtilities.invokeAndWait(() -> holder[0].validate());
                SwingUtilities.invokeAndWait(() -> {
                    holder[0].validate();
                    result.add(inspect.apply(holder[0]));
                });
            } finally {
                SwingUtilities.invokeAndWait(() -> holder[0].dispose());
            }
            return result.get(0);
        } finally {
            config.setFontSizeScale(oldScale);
            config.setSoundEnabled(sound);
            config.setTourSeen(tourSeen);
        }
    }

    static <T extends Component> T find(Container root, Class<T> type) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) {
                return type.cast(c);
            }
            if (c instanceof Container) {
                T found = find((Container) c, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    static Component findByName(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
            if (c instanceof Container) {
                Component found = findByName((Container) c, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Every visible component under {@code root} must lie inside its parent (a scrolling viewport may hold
     * a larger view), and labels/buttons must get their full width — or, for wrapping HTML labels, their
     * full height. Returns one line per problem.
     */
    static List<String> clippedComponents(Container root) {
        List<String> problems = new ArrayList<>();
        walk(root, problems);
        return problems;
    }

    private static void walk(Container parent, List<String> problems) {
        for (Component c : parent.getComponents()) {
            if (!c.isVisible()) {
                continue;
            }
            if (!(parent instanceof JViewport)
                    && (c.getX() < 0 || c.getY() < 0
                    || c.getX() + c.getWidth() > parent.getWidth()
                    || c.getY() + c.getHeight() > parent.getHeight())) {
                problems.add(describe(c) + " sticks out of its container (" + c.getBounds()
                        + " in " + parent.getWidth() + "x" + parent.getHeight() + ")");
            }
            if (c instanceof JLabel || c instanceof AbstractButton) {
                JComponent jc = (JComponent) c;
                String text = c instanceof JLabel ? ((JLabel) c).getText() : ((AbstractButton) c).getText();
                boolean html = text != null && text.startsWith("<html>");
                int needed = html ? wrappedHtmlHeight(jc) : 0;
                if (html && jc.getHeight() < Math.max(needed, jc.getPreferredSize().height)) {
                    problems.add(describe(c) + " is cut off at the bottom (" + jc.getHeight()
                            + " < " + Math.max(needed, jc.getPreferredSize().height) + ")");
                } else if (!html && jc.getWidth() < jc.getPreferredSize().width) {
                    problems.add(describe(c) + " is cut short (" + jc.getWidth()
                            + " < " + jc.getPreferredSize().width + ")");
                }
            }
            if (c instanceof Container && !(c instanceof JComboBox) && !(c instanceof JTextField)) {
                walk((Container) c, problems);
            }
        }
    }

    /** Height an HTML label's text needs when wrapped to the label's current width (its own preferred
     *  height assumes one line, so a narrowed label silently hides the wrapped lines). */
    private static int wrappedHtmlHeight(JComponent label) {
        Object view = label.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
        if (!(view instanceof javax.swing.text.View) || label.getWidth() <= 0) {
            return 0;
        }
        javax.swing.text.View v = (javax.swing.text.View) view;
        java.awt.Insets in = label.getInsets();
        int innerWidth = label.getWidth() - in.left - in.right;
        v.setSize(innerWidth, Integer.MAX_VALUE);
        int needed = (int) Math.ceil(v.getPreferredSpan(javax.swing.text.View.Y_AXIS)) + in.top + in.bottom;
        v.setSize(innerWidth, label.getHeight() - in.top - in.bottom);   // leave the view as it was laid out
        return needed;
    }

    private static String describe(Component c) {
        String text = c instanceof JLabel ? ((JLabel) c).getText()
                : c instanceof AbstractButton ? ((AbstractButton) c).getText()
                : c.getClass().getSimpleName();
        return "'" + text + "'";
    }
}
