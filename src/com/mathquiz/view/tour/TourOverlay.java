package com.mathquiz.view.tour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Full-screen overlay used as the JFrame glass pane during the guided tour.
 *
 * Painting strategy:
 *   1. Fill the whole pane with a dark semi-transparent mask using Area subtraction.
 *   2. Cut a rounded-rectangle "spotlight" hole around the target component.
 *   3. Draw a glowing gold border around the spotlight.
 *   4. Draw a rounded speech bubble with the step text.
 *   5. Draw Archie the Owl next to the bubble.
 *
 * All mouse events on the overlay are consumed so the user can only interact
 * with the NEXT and SKIP buttons embedded in the overlay itself.
 */
public class TourOverlay extends JPanel {

    // ── Visual constants ─────────────────────────────────────────────────────
    private static final Color   MASK_COLOR     = new Color(0, 0, 0, 175);
    private static final Color   SPOTLIGHT_GLOW = new Color(184, 150, 110, 220); // ACCENT_GOLD
    private static final Color   BUBBLE_BG      = new Color(255, 253, 248);
    private static final Color   BUBBLE_BORDER  = new Color(184, 150, 110);
    private static final Color   TEXT_DARK      = new Color(28, 25, 23);
    private static final Color   BTN_DARK       = new Color(28, 25, 23);
    private static final Color   BTN_SKIP       = new Color(120, 113, 108);
    private static final int     SPOT_PAD       = 14;   // extra pixels around spotlight
    private static final int     BUBBLE_W       = 320;
    private static final int     BUBBLE_H       = 160;
    private static final int     MASCOT_SIZE    = 80;

    // ── State ────────────────────────────────────────────────────────────────
    private TourStep currentStep;
    private int      stepIndex   = 0;
    private int      totalSteps  = 0;
    private Runnable onNext;
    private Runnable onSkip;

    // ── Swing children (positioned in paintComponent to avoid layout manager) ─
    private final JButton nextBtn;
    private final JButton skipBtn;

    // ─────────────────────────────────────────────────────────────────────────

    public TourOverlay() {
        setOpaque(false);
        setLayout(null);    // absolute positioning for buttons

        // NEXT button
        nextBtn = makeButton("NEXT  →", BTN_DARK, Color.WHITE);
        nextBtn.addActionListener(e -> { if (onNext != null) onNext.run(); });
        add(nextBtn);

        // SKIP button
        skipBtn = makeButton("SKIP TOUR", BTN_SKIP, Color.WHITE);
        skipBtn.addActionListener(e -> { if (onSkip != null) onSkip.run(); });
        add(skipBtn);

        // Consume all mouse events except those aimed at our own buttons
        addMouseListener(new MouseAdapter() {});
        addMouseMotionListener(new MouseMotionAdapter() {});
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public void showStep(TourStep step, int index, int total,
                         Runnable onNext, Runnable onSkip) {
        this.currentStep = step;
        this.stepIndex   = index;
        this.totalSteps  = total;
        this.onNext      = onNext;
        this.onSkip      = onSkip;

        // Update NEXT button label on last step
        nextBtn.setText(index == total - 1 ? "FINISH  ✓" : "NEXT  →");

        setVisible(true);
        repaint();
    }

    public void hideTour() {
        setVisible(false);
        currentStep = null;
    }

    // ── Painting ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentStep == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Compute spotlight bounds (absolute in our coordinate system)
        Rectangle spot = computeSpotlight(currentStep.target);

        // ── 1. Dark mask with spotlight hole ──────────────────────────────────
        Area mask = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
        if (spot != null) {
            RoundRectangle2D hole = new RoundRectangle2D.Double(
                    spot.x - SPOT_PAD, spot.y - SPOT_PAD,
                    spot.width + SPOT_PAD * 2, spot.height + SPOT_PAD * 2,
                    20, 20);
            mask.subtract(new Area(hole));
        }
        g2.setColor(MASK_COLOR);
        g2.fill(mask);

        // ── 2. Glowing border around spotlight ────────────────────────────────
        if (spot != null) {
            g2.setColor(SPOTLIGHT_GLOW);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(spot.x - SPOT_PAD, spot.y - SPOT_PAD,
                             spot.width + SPOT_PAD * 2, spot.height + SPOT_PAD * 2, 20, 20);
        }

        // ── 3. Compute bubble position ─────────────────────────────────────────
        Rectangle bubble = computeBubbleRect(spot, currentStep.bubblePos);

        // ── 4. Draw speech bubble ─────────────────────────────────────────────
        drawBubble(g2, bubble, spot, currentStep);

        // ── 5. Draw Archie ────────────────────────────────────────────────────
        int mascotX = bubble.x - MASCOT_SIZE - 12;
        int mascotY = bubble.y + (bubble.height - MASCOT_SIZE) / 2;
        // If owl would go off-screen, put it to the right of the bubble
        if (mascotX < 8) {
            mascotX = bubble.x + bubble.width + 12;
        }
        MascotPainter.draw(g2,
                mascotX + MASCOT_SIZE / 2,
                mascotY + MASCOT_SIZE / 2,
                MASCOT_SIZE, currentStep.expression);

        // ── 6. Position NEXT / SKIP buttons inside bubble ─────────────────────
        int btnY = bubble.y + bubble.height - 44;
        nextBtn.setBounds(bubble.x + bubble.width - 130, btnY, 120, 34);
        skipBtn.setBounds(bubble.x + 10,                btnY, 110, 34);

        // ── 7. Step counter ───────────────────────────────────────────────────
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(180, 150, 110));
        String counter = "Step " + (stepIndex + 1) + " of " + totalSteps;
        g2.drawString(counter, bubble.x + bubble.width / 2 - 25, bubble.y + 18);

        g2.dispose();
    }

    // ── Helper: compute spotlight rect relative to overlay ───────────────────

    private Rectangle computeSpotlight(Component target) {
        if (target == null || !target.isShowing()) return null;
        try {
            Point p = SwingUtilities.convertPoint(target, 0, 0, this);
            return new Rectangle(p.x, p.y, target.getWidth(), target.getHeight());
        } catch (Exception e) {
            return null;
        }
    }

    // ── Helper: decide where to draw the bubble ───────────────────────────────

    private Rectangle computeBubbleRect(Rectangle spot, TourStep.BubblePosition pos) {
        int w = BUBBLE_W;
        int h = BUBBLE_H;
        int cx = getWidth()  / 2;
        int cy = getHeight() / 2;

        if (spot == null || pos == TourStep.BubblePosition.SCREEN_CENTER) {
            return new Rectangle(cx - w / 2, cy - h / 2, w, h);
        }

        switch (pos) {
            case BOTTOM_CENTER:
                return clamp(new Rectangle(spot.x + spot.width / 2 - w / 2,
                                           spot.y + spot.height + SPOT_PAD + 20, w, h));
            case TOP_CENTER:
                return clamp(new Rectangle(spot.x + spot.width / 2 - w / 2,
                                           spot.y - SPOT_PAD - 20 - h, w, h));
            case LEFT_CENTER:
                return clamp(new Rectangle(spot.x - SPOT_PAD - 20 - w,
                                           spot.y + spot.height / 2 - h / 2, w, h));
            case RIGHT_CENTER:
                return clamp(new Rectangle(spot.x + spot.width + SPOT_PAD + 20,
                                           spot.y + spot.height / 2 - h / 2, w, h));
            default:
                return new Rectangle(cx - w / 2, cy - h / 2, w, h);
        }
    }

    private Rectangle clamp(Rectangle r) {
        int margin = 10;
        r.x = Math.max(margin + MASCOT_SIZE + 20, Math.min(r.x, getWidth()  - r.width  - margin));
        r.y = Math.max(margin,                    Math.min(r.y, getHeight() - r.height - margin));
        return r;
    }

    // ── Helper: draw rounded speech bubble ────────────────────────────────────

    private void drawBubble(Graphics2D g2, Rectangle bubble, Rectangle spot, TourStep step) {
        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(bubble.x + 4, bubble.y + 4, bubble.width, bubble.height, 20, 20);

        // Background
        g2.setColor(BUBBLE_BG);
        g2.fillRoundRect(bubble.x, bubble.y, bubble.width, bubble.height, 20, 20);

        // Border
        g2.setColor(BUBBLE_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(bubble.x, bubble.y, bubble.width, bubble.height, 20, 20);

        // Message text (word-wrapped)
        g2.setColor(TEXT_DARK);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        drawWrappedText(g2, step.message,
                bubble.x + 14, bubble.y + 30,
                bubble.width - 28, bubble.height - 60);
    }

    /** Simple word-wrap text renderer. */
    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxW, int maxH) {
        FontMetrics fm   = g2.getFontMetrics();
        int         lineH = fm.getHeight();
        String[]    words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y + fm.getAscent();

        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxW && line.length() > 0) {
                if (curY + lineH <= y + maxH) {
                    g2.drawString(line.toString(), x, curY);
                }
                curY += lineH;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(test);
            }
        }
        if (line.length() > 0 && curY + lineH <= y + maxH) {
            g2.drawString(line.toString(), x, curY);
        }
    }

    // ── Button factory ────────────────────────────────────────────────────────

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
