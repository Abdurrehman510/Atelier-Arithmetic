package com.mathquiz.view.tour;

import java.awt.*;
import java.awt.geom.*;

/**
 * Draws "Archie the Owl" — the friendly app mascot — entirely in Java2D.
 * No image files required. Supports four expressions to match tour context.
 */
public class MascotPainter {

    private MascotPainter() {}

    /**
     * Draws Archie centred inside the provided bounding box.
     *
     * @param g2   Graphics2D context (caller must dispose after use)
     * @param cx   centre x of the drawing area
     * @param cy   centre y of the drawing area
     * @param size diameter of the owl body (recommended 80–120px)
     * @param expr which facial expression to draw
     */
    public static void draw(Graphics2D g2, int cx, int cy, int size,
                             TourStep.MascotExpression expr) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = size / 2;          // body radius

        // ── Body ────────────────────────────────────────────────────────────
        // Warm amber-brown gradient for the body
        GradientPaint bodyGrad = new GradientPaint(
                cx - r, cy - r, new Color(210, 150, 80),
                cx + r, cy + r, new Color(160, 100, 45));
        g2.setPaint(bodyGrad);
        g2.fillOval(cx - r, cy - r, size, size);

        // Body outline
        g2.setColor(new Color(120, 70, 20));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, size, size);

        // ── Ear Tufts ────────────────────────────────────────────────────────
        drawEarTuft(g2, cx - r / 2, cy - r + 4, -15);   // left
        drawEarTuft(g2, cx + r / 2, cy - r + 4,  15);   // right

        // ── Chest / Belly patch ──────────────────────────────────────────────
        g2.setColor(new Color(245, 220, 170));
        g2.fillOval(cx - r / 3, cy - r / 6, r * 2 / 3, r * 3 / 4);

        // ── Eyes ────────────────────────────────────────────────────────────
        int eyeR   = r / 4;
        int leftEX = cx - r / 3;
        int rightEX= cx + r / 3;
        int eyeY   = cy - r / 6;

        // White sclera
        g2.setColor(Color.WHITE);
        g2.fillOval(leftEX  - eyeR, eyeY - eyeR, eyeR * 2, eyeR * 2);
        g2.fillOval(rightEX - eyeR, eyeY - eyeR, eyeR * 2, eyeR * 2);

        // Eye rims
        g2.setColor(new Color(80, 50, 10));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(leftEX  - eyeR, eyeY - eyeR, eyeR * 2, eyeR * 2);
        g2.drawOval(rightEX - eyeR, eyeY - eyeR, eyeR * 2, eyeR * 2);

        // Pupils (shift for "excited" expression)
        int pupilR = eyeR / 2;
        int pupilOffset = (expr == TourStep.MascotExpression.EXCITED) ? -eyeR / 4 : 0;
        g2.setColor(new Color(40, 20, 5));
        g2.fillOval(leftEX  - pupilR + pupilOffset, eyeY - pupilR + pupilOffset, pupilR * 2, pupilR * 2);
        g2.fillOval(rightEX - pupilR + pupilOffset, eyeY - pupilR + pupilOffset, pupilR * 2, pupilR * 2);

        // Eye shine
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillOval(leftEX  - pupilR / 2 + pupilOffset + 2, eyeY - pupilR + pupilOffset + 2, pupilR - 2, pupilR - 2);
        g2.fillOval(rightEX - pupilR / 2 + pupilOffset + 2, eyeY - pupilR + pupilOffset + 2, pupilR - 2, pupilR - 2);

        // Expression: raised inner eyebrow for THINKING
        if (expr == TourStep.MascotExpression.THINKING) {
            g2.setColor(new Color(80, 50, 10));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(leftEX - eyeR, eyeY - eyeR - 6, leftEX + 2, eyeY - eyeR - 10);
        }

        // ── Beak ─────────────────────────────────────────────────────────────
        g2.setColor(new Color(240, 180, 60));
        int beakW = r / 4;
        int beakH = r / 5;
        int beakY = eyeY + eyeR + 2;
        int[] beakXPts = { cx - beakW / 2, cx + beakW / 2, cx };
        int[] beakYPts = { beakY, beakY, beakY + beakH };
        g2.fillPolygon(beakXPts, beakYPts, 3);
        g2.setColor(new Color(200, 140, 30));
        g2.drawPolygon(beakXPts, beakYPts, 3);

        // ── Mouth expression ─────────────────────────────────────────────────
        int mouthY = beakY + beakH + 4;
        g2.setColor(new Color(80, 50, 10));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (expr) {
            case HAPPY:
            case WAVING:
            case EXCITED:
            case POINTING:
                // Wide happy smile
                g2.draw(new Arc2D.Double(cx - r / 3, mouthY - 6, r * 2.0 / 3, r / 3.0, 200, 140, Arc2D.OPEN));
                break;
            case THINKING:
                // Slight smirk
                g2.drawLine(cx - r / 5, mouthY + 2, cx + r / 5, mouthY);
                break;
        }

        // ── Wings ────────────────────────────────────────────────────────────
        g2.setColor(new Color(160, 100, 45, 200));
        // Left wing
        g2.fillOval(cx - r - r / 3, cy, r / 2, r);
        // Right wing
        g2.fillOval(cx + r - r / 6, cy, r / 2, r);

        // ── Waving arm (right wing raised) for WAVING expression ─────────────
        if (expr == TourStep.MascotExpression.WAVING) {
            g2.setColor(new Color(160, 100, 45));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx + r, cy + r / 4, cx + r + r / 2, cy - r / 3);
            // Small hand/feather at the tip
            g2.setColor(new Color(210, 150, 80));
            g2.fillOval(cx + r + r / 2 - 5, cy - r / 3 - 5, 10, 10);
        }

        // ── Pointing finger for POINTING expression ──────────────────────────
        if (expr == TourStep.MascotExpression.POINTING) {
            g2.setColor(new Color(160, 100, 45));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx + r, cy, cx + r + r / 2, cy - r / 2);
            g2.setColor(new Color(240, 180, 60));
            g2.fillOval(cx + r + r / 2 - 5, cy - r / 2 - 5, 10, 10);
        }

        // ── Feet ─────────────────────────────────────────────────────────────
        g2.setColor(new Color(240, 180, 60));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Left foot
        g2.drawLine(cx - r / 4, cy + r - 4, cx - r / 4, cy + r + 10);
        g2.drawLine(cx - r / 4, cy + r + 10, cx - r / 4 - 8, cy + r + 14);
        g2.drawLine(cx - r / 4, cy + r + 10, cx - r / 4, cy + r + 14);
        g2.drawLine(cx - r / 4, cy + r + 10, cx - r / 4 + 8, cy + r + 14);
        // Right foot
        g2.drawLine(cx + r / 4, cy + r - 4, cx + r / 4, cy + r + 10);
        g2.drawLine(cx + r / 4, cy + r + 10, cx + r / 4 - 8, cy + r + 14);
        g2.drawLine(cx + r / 4, cy + r + 10, cx + r / 4, cy + r + 14);
        g2.drawLine(cx + r / 4, cy + r + 10, cx + r / 4 + 8, cy + r + 14);
    }

    /** Draws a small pointed ear tuft at the given position, rotated by angleDeg. */
    private static void drawEarTuft(Graphics2D g2, int tx, int ty, int angleDeg) {
        Graphics2D g = (Graphics2D) g2.create();
        g.rotate(Math.toRadians(angleDeg), tx, ty);
        g.setColor(new Color(160, 100, 45));
        int[] xs = { tx - 6, tx + 6, tx };
        int[] ys = { ty, ty, ty - 16 };
        g.fillPolygon(xs, ys, 3);
        g.setColor(new Color(120, 70, 20));
        g.setStroke(new BasicStroke(1.2f));
        g.drawPolygon(xs, ys, 3);
        g.dispose();
    }
}
