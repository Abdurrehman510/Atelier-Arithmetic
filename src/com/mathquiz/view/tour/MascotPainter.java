package com.mathquiz.view.tour;

import java.awt.*;
import java.awt.geom.*;

/**
 * Draws "Archie the Owl" — the friendly app mascot — entirely in Java2D.
 * No image files required. Supports four expressions to match tour context, and draws the Mascot Shop
 * item a child has equipped (hats, glasses, colours and badges), so a purchase is visible wherever
 * Archie appears (BUG-22).
 */
public class MascotPainter {

    private MascotPainter() {}

    /** Archie's colours: his usual amber-brown, or Golden Archie's. */
    private static final class Palette {
        final Color bodyLight, bodyDark, outline, wing, belly;

        Palette(Color bodyLight, Color bodyDark, Color outline, Color wing, Color belly) {
            this.bodyLight = bodyLight;
            this.bodyDark = bodyDark;
            this.outline = outline;
            this.wing = wing;
            this.belly = belly;
        }
    }

    private static final Palette AMBER = new Palette(new Color(210, 150, 80), new Color(160, 100, 45),
            new Color(120, 70, 20), new Color(160, 100, 45, 200), new Color(245, 220, 170));
    private static final Palette GOLD = new Palette(new Color(255, 221, 90), new Color(214, 160, 20),
            new Color(150, 105, 0), new Color(214, 160, 20, 200), new Color(255, 244, 200));

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
        draw(g2, cx, cy, size, expr, null);
    }

    /**
     * Draws Archie wearing a Mascot Shop item ({@code MascotShopPanel} catalogue IDs such as
     * {@code "hat_crown"}); {@code null}, {@code "none"} or an unknown ID draws him plain.
     */
    public static void draw(Graphics2D g2, int cx, int cy, int size,
                             TourStep.MascotExpression expr, String itemId) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String item = itemId == null ? "" : itemId;
        Palette p = "color_gold".equals(item) ? GOLD : AMBER;

        if ("color_rainbow".equals(item)) {
            inBodySpace(g2, cx, cy, size, MascotPainter::rainbowGlow);   // behind him
        }

        int r = size / 2;          // body radius

        // ── Body ────────────────────────────────────────────────────────────
        // Warm amber-brown gradient for the body
        GradientPaint bodyGrad = new GradientPaint(
                cx - r, cy - r, p.bodyLight,
                cx + r, cy + r, p.bodyDark);
        g2.setPaint(bodyGrad);
        g2.fillOval(cx - r, cy - r, size, size);

        // Body outline
        g2.setColor(p.outline);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(cx - r, cy - r, size, size);

        // ── Ear Tufts ────────────────────────────────────────────────────────
        drawEarTuft(g2, cx - r / 2, cy - r + 4, -15, p);   // left
        drawEarTuft(g2, cx + r / 2, cy - r + 4,  15, p);   // right

        // ── Chest / Belly patch ──────────────────────────────────────────────
        g2.setColor(p.belly);
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
        g2.setColor(p.wing);
        // Left wing
        g2.fillOval(cx - r - r / 3, cy, r / 2, r);
        // Right wing
        g2.fillOval(cx + r - r / 6, cy, r / 2, r);

        // ── Waving arm (right wing raised) for WAVING expression ─────────────
        if (expr == TourStep.MascotExpression.WAVING) {
            g2.setColor(p.bodyDark);
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(cx + r, cy + r / 4, cx + r + r / 2, cy - r / 3);
            // Small hand/feather at the tip
            g2.setColor(p.bodyLight);
            g2.fillOval(cx + r + r / 2 - 5, cy - r / 3 - 5, 10, 10);
        }

        // ── Pointing finger for POINTING expression ──────────────────────────
        if (expr == TourStep.MascotExpression.POINTING) {
            g2.setColor(p.bodyDark);
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

        // ── What he is wearing ───────────────────────────────────────────────
        Accessory accessory = accessoryFor(item);
        if (accessory != null) {
            inBodySpace(g2, cx, cy, size, accessory);
        }
    }

    /** Draws a small pointed ear tuft at the given position, rotated by angleDeg. */
    private static void drawEarTuft(Graphics2D g2, int tx, int ty, int angleDeg, Palette p) {
        Graphics2D g = (Graphics2D) g2.create();
        g.rotate(Math.toRadians(angleDeg), tx, ty);
        g.setColor(p.bodyDark);
        int[] xs = { tx - 6, tx + 6, tx };
        int[] ys = { ty, ty, ty - 16 };
        g.fillPolygon(xs, ys, 3);
        g.setColor(p.outline);
        g.setStroke(new BasicStroke(1.2f));
        g.drawPolygon(xs, ys, 3);
        g.dispose();
    }

    // ── Shop items ───────────────────────────────────────────────────────────
    // Drawn in "body space": origin at the body's centre, body diameter 100 (head top y = -50,
    // eyes at (±16, -8) with radius 12, chest medal spot at (27, 27)).

    @FunctionalInterface
    private interface Accessory {
        void paint(Graphics2D g);
    }

    private static Accessory accessoryFor(String itemId) {
        switch (itemId) {
            case "hat_top":       return MascotPainter::topHat;
            case "hat_crown":     return MascotPainter::crown;
            case "hat_wizard":    return MascotPainter::wizardHat;
            case "hat_party":     return MascotPainter::partyHat;
            case "glasses_nerd":  return MascotPainter::nerdGlasses;
            case "glasses_sun":   return MascotPainter::coolShades;
            case "glasses_star":  return MascotPainter::starFrames;
            case "color_gold":    return MascotPainter::sparkles;     // the gold body is the palette
            case "badge_scholar": return MascotPainter::scholarBadge;
            case "badge_star":    return MascotPainter::starBadge;
            case "badge_heart":   return MascotPainter::heartBadge;
            default:              return null;                        // "none", rainbow (drawn behind), unknown
        }
    }

    private static void inBodySpace(Graphics2D g2, int cx, int cy, int size, Accessory accessory) {
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(cx, cy);
            g.scale(size / 100.0, size / 100.0);
            accessory.paint(g);
        } finally {
            g.dispose();
        }
    }

    private static Stroke stroke(float width) {
        return new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    /** A five-pointed star centred on (x, y), one point up. */
    private static Shape star(double x, double y, double outer, double inner) {
        Path2D s = new Path2D.Double();
        for (int i = 0; i < 10; i++) {
            double radius = i % 2 == 0 ? outer : inner;
            double angle = Math.toRadians(-90 + i * 36);
            double px = x + radius * Math.cos(angle);
            double py = y + radius * Math.sin(angle);
            if (i == 0) {
                s.moveTo(px, py);
            } else {
                s.lineTo(px, py);
            }
        }
        s.closePath();
        return s;
    }

    private static void fillAndOutline(Graphics2D g, Shape shape, Paint fill, Color outline, float width) {
        g.setPaint(fill);
        g.fill(shape);
        g.setColor(outline);
        g.setStroke(stroke(width));
        g.draw(shape);
    }

    // Hats ─────────────────────────────────────────────────────────────────────

    private static void topHat(Graphics2D g) {
        g.rotate(Math.toRadians(-8), 0, -50);                     // a jaunty tilt
        Color black = new Color(32, 32, 38);
        Color edge = new Color(8, 8, 10);
        Shape brim = new Ellipse2D.Double(-31, -55, 62, 11);
        fillAndOutline(g, brim, black, edge, 1.5f);
        Shape crown = new RoundRectangle2D.Double(-20, -93, 40, 43, 6, 6);
        fillAndOutline(g, crown, new GradientPaint(-20, 0, new Color(72, 72, 84), 20, 0, black), edge, 1.5f);
        g.setColor(new Color(196, 40, 56));                       // red band
        g.fill(new Rectangle2D.Double(-19.2, -61, 38.4, 8));
        Shape brimFront = new Arc2D.Double(-31, -55, 62, 11, 180, 180, Arc2D.CHORD);
        fillAndOutline(g, brimFront, black, edge, 1.5f);
    }

    private static void crown(Graphics2D g) {
        Path2D crown = new Path2D.Double();
        crown.moveTo(-24, -47);
        crown.lineTo(-26, -82);
        crown.lineTo(-13, -66);
        crown.lineTo(0, -88);
        crown.lineTo(13, -66);
        crown.lineTo(26, -82);
        crown.lineTo(24, -47);
        crown.closePath();
        Color rim = new Color(150, 100, 10);
        fillAndOutline(g, crown, new GradientPaint(0, -88, new Color(255, 225, 90), 0, -47, new Color(214, 158, 20)),
                rim, 1.6f);
        fillAndOutline(g, new Rectangle2D.Double(-24.5, -57, 49, 9), new Color(232, 180, 30), rim, 1.4f);
        Color pearl = new Color(255, 250, 235);
        fillAndOutline(g, new Ellipse2D.Double(-29.2, -87.2, 6.4, 6.4), pearl, rim, 1f);
        fillAndOutline(g, new Ellipse2D.Double(-3.6, -94, 7.2, 7.2), pearl, rim, 1f);
        fillAndOutline(g, new Ellipse2D.Double(22.8, -87.2, 6.4, 6.4), pearl, rim, 1f);
        Color ruby = new Color(220, 30, 60);
        Color sapphire = new Color(40, 90, 220);
        fillAndOutline(g, new Ellipse2D.Double(-16, -55.5, 6, 6), ruby, rim, 0.8f);
        fillAndOutline(g, new Ellipse2D.Double(-3, -55.5, 6, 6), sapphire, rim, 0.8f);
        fillAndOutline(g, new Ellipse2D.Double(10, -55.5, 6, 6), ruby, rim, 0.8f);
    }

    private static void wizardHat(Graphics2D g) {
        Color dark = new Color(72, 38, 140);
        Color light = new Color(126, 80, 206);
        Color edge = new Color(45, 20, 95);
        fillAndOutline(g, new Ellipse2D.Double(-34, -56, 68, 12), dark, edge, 1.5f);
        Path2D cone = new Path2D.Double();
        cone.moveTo(-22, -51);
        cone.quadTo(-10, -76, 6, -89);
        cone.quadTo(14, -96, 22, -92);                            // the tip flops to the right
        cone.quadTo(12, -86, 10, -74);
        cone.quadTo(14, -60, 22, -51);
        cone.closePath();
        fillAndOutline(g, cone, new GradientPaint(-20, 0, light, 22, 0, dark), edge, 1.5f);
        Color gold = new Color(255, 214, 64);
        g.setColor(gold);
        g.fill(star(-5, -63, 5.5, 2.4));
        g.fill(star(5, -76, 3.8, 1.6));
        g.fill(star(12, -58, 3, 1.3));
        g.fill(new Ellipse2D.Double(20, -94.5, 5, 5));            // a bobble on the tip
    }

    private static void partyHat(Graphics2D g) {
        g.rotate(Math.toRadians(12), 0, -50);
        Path2D cone = new Path2D.Double();
        cone.moveTo(-19, -48);
        cone.lineTo(19, -48);
        cone.lineTo(0, -89);
        cone.closePath();
        Shape oldClip = g.getClip();
        g.clip(cone);
        g.setColor(new Color(255, 92, 170));
        g.fill(cone.getBounds2D());
        g.setColor(new Color(255, 214, 64));
        for (double x = -60; x < 30; x += 13) {                   // diagonal stripes
            Path2D stripe = new Path2D.Double();
            stripe.moveTo(x, -46);
            stripe.lineTo(x + 6, -46);
            stripe.lineTo(x + 46, -92);
            stripe.lineTo(x + 40, -92);
            stripe.closePath();
            g.fill(stripe);
        }
        g.setClip(oldClip);
        g.setColor(new Color(190, 40, 120));
        g.setStroke(stroke(1.5f));
        g.draw(cone);
        fillAndOutline(g, new Ellipse2D.Double(-6.5, -95.5, 13, 13), new Color(120, 200, 255), new Color(40, 120, 200), 1.2f);
    }

    // Glasses ──────────────────────────────────────────────────────────────────

    private static void nerdGlasses(Graphics2D g) {
        Color frame = new Color(28, 28, 32);
        Shape left = new Ellipse2D.Double(-30, -22, 28, 28);
        Shape right = new Ellipse2D.Double(2, -22, 28, 28);
        g.setColor(new Color(255, 255, 255, 60));
        g.fill(left);
        g.fill(right);
        g.setColor(frame);
        g.setStroke(stroke(3.4f));
        g.draw(left);
        g.draw(right);
        g.draw(new Line2D.Double(-2, -10, 2, -10));               // bridge
        g.setStroke(stroke(2.6f));
        g.draw(new Line2D.Double(-30, -10, -44, -14));            // arms
        g.draw(new Line2D.Double(30, -10, 44, -14));
        fillAndOutline(g, new Rectangle2D.Double(-2.6, -13, 5.2, 6), Color.WHITE, new Color(200, 200, 200), 0.8f);  // tape
    }

    private static void coolShades(Graphics2D g) {
        Shape left = new RoundRectangle2D.Double(-31, -19, 28, 21, 10, 12);
        Shape right = new RoundRectangle2D.Double(3, -19, 28, 21, 10, 12);
        Paint lens = new GradientPaint(0, -19, new Color(20, 22, 30), 0, 2, new Color(70, 76, 100));
        Color frame = new Color(10, 10, 12);
        fillAndOutline(g, left, lens, frame, 2.4f);
        fillAndOutline(g, right, lens, frame, 2.4f);
        g.setStroke(stroke(3f));
        g.draw(new Line2D.Double(-3, -15, 3, -15));               // bridge
        g.setStroke(stroke(2.6f));
        g.draw(new Line2D.Double(-31, -14, -44, -16));            // arms
        g.draw(new Line2D.Double(31, -14, 44, -16));
        g.setColor(new Color(255, 255, 255, 170));                // glints
        g.setStroke(stroke(2f));
        g.draw(new Line2D.Double(-26, -12, -20, -16));
        g.draw(new Line2D.Double(8, -12, 14, -16));
    }

    private static void starFrames(Graphics2D g) {
        Shape left = star(-17, -8, 17, 9.5);
        Shape right = star(17, -8, 17, 9.5);
        g.setColor(new Color(255, 120, 190, 70));
        g.fill(left);
        g.fill(right);
        g.setColor(new Color(240, 170, 0));
        g.setStroke(stroke(3f));
        g.draw(left);
        g.draw(right);
        g.setStroke(stroke(2.4f));
        g.draw(new Line2D.Double(-33, -10, -44, -14));            // arms
        g.draw(new Line2D.Double(33, -10, 44, -14));
    }

    // Colours ──────────────────────────────────────────────────────────────────

    /** Rainbow Glow: rainbow rings shining around Archie (drawn before his body). */
    private static void rainbowGlow(Graphics2D g) {
        Color[] bands = {
                new Color(235, 64, 52, 210), new Color(255, 150, 40, 210), new Color(255, 214, 50, 210),
                new Color(80, 190, 90, 210), new Color(60, 140, 230, 210), new Color(150, 90, 210, 210)};
        g.setStroke(new BasicStroke(2.4f));
        for (int i = 0; i < bands.length; i++) {
            double radius = 64 - i * 2;
            g.setColor(bands[i]);
            g.draw(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
        }
    }

    /** Golden Archie: sparkles around his golden body. */
    private static void sparkles(Graphics2D g) {
        double[][] spots = {{-50, -52, 8}, {54, -34, 6}, {60, 50, 6}, {-56, 26, 5}};
        for (double[] s : spots) {
            double x = s[0], y = s[1], k = s[2];
            Path2D sparkle = new Path2D.Double();
            sparkle.moveTo(x, y - k);
            sparkle.quadTo(x, y, x + k, y);
            sparkle.quadTo(x, y, x, y + k);
            sparkle.quadTo(x, y, x - k, y);
            sparkle.quadTo(x, y, x, y - k);
            sparkle.closePath();
            fillAndOutline(g, sparkle, new Color(255, 236, 140), new Color(225, 165, 0), 1f);
        }
    }

    // Badges (a medal pinned to his chest) ─────────────────────────────────────

    private static void medal(Graphics2D g, Color ribbonA, Color ribbonB, Color disc, Color rim) {
        Path2D left = new Path2D.Double();
        left.moveTo(20, 5);
        left.lineTo(26, 5);
        left.lineTo(29, 19);
        left.lineTo(24, 19);
        left.closePath();
        Path2D right = new Path2D.Double();
        right.moveTo(34, 5);
        right.lineTo(28, 5);
        right.lineTo(25, 19);
        right.lineTo(30, 19);
        right.closePath();
        g.setColor(ribbonA);
        g.fill(left);
        g.setColor(ribbonB);
        g.fill(right);
        fillAndOutline(g, new Ellipse2D.Double(18, 18, 18, 18), disc, rim, 1.8f);
    }

    private static void scholarBadge(Graphics2D g) {
        medal(g, new Color(60, 110, 220), new Color(40, 80, 180), new Color(35, 70, 160), new Color(230, 180, 40));
        Color cap = new Color(250, 250, 250);
        g.setColor(cap);
        g.fill(new Rectangle2D.Double(24, 26, 6, 4));             // mortarboard
        Path2D board = new Path2D.Double();
        board.moveTo(21.5, 25);
        board.lineTo(27, 22);
        board.lineTo(32.5, 25);
        board.lineTo(27, 28);
        board.closePath();
        g.fill(board);
        g.setColor(new Color(255, 205, 60));                      // tassel
        g.setStroke(stroke(1.2f));
        g.draw(new Line2D.Double(32, 25, 33, 30));
    }

    private static void starBadge(Graphics2D g) {
        medal(g, new Color(220, 50, 60), new Color(180, 30, 45), new Color(255, 200, 40), new Color(190, 135, 0));
        fillAndOutline(g, star(27, 27.5, 6.5, 2.8), new Color(255, 250, 230), new Color(190, 135, 0), 0.9f);
    }

    private static void heartBadge(Graphics2D g) {
        medal(g, new Color(255, 140, 190), new Color(235, 100, 160), new Color(255, 250, 252), new Color(230, 90, 150));
        double x = 27, y = 27.5;
        Path2D heart = new Path2D.Double();
        heart.moveTo(x, y + 4.5);
        heart.curveTo(x - 7, y - 0.5, x - 3.5, y - 6.5, x, y - 2.5);
        heart.curveTo(x + 3.5, y - 6.5, x + 7, y - 0.5, x, y + 4.5);
        heart.closePath();
        g.setColor(new Color(240, 70, 140));
        g.fill(heart);
    }
}
