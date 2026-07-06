package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * Custom Java2D graphics panel that renders an expressive face (smile / neutral /
 * sad) based on the quiz grade. Extracted from the original monolithic QuizFrame.
 */
public class SmilePanel extends JPanel {

    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);

    private String grade = "D";
    private String emoji = "🚀";

    public SmilePanel() {
        setOpaque(true);
    }

    public void setGrade(String grade, String emoji) {
        this.grade = grade;
        this.emoji = emoji;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width  = getWidth();
        int height = getHeight();
        int size   = Math.min(width, height) - 70;
        int x      = (width  - size) / 2;
        int y      = (height - size) / 2 - 15;

        // Soft background
        g2.setColor(new Color(250, 249, 246));
        g2.fillRect(0, 0, width, height);

        // Face circle
        g2.setColor(ACCENT_GOLD);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(x, y, size, size);

        // Eyes
        int eyeSize  = size / 10;
        int leftEyeX = x + size / 3 - eyeSize / 2;
        int rightEyeX= x + (2 * size) / 3 - eyeSize / 2;
        int eyeY     = y + size / 3;
        g2.setColor(ACCENT_GOLD);
        g2.fillOval(leftEyeX, eyeY, eyeSize, eyeSize);
        g2.fillOval(rightEyeX, eyeY, eyeSize, eyeSize);

        // Mouth
        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int mouthWidth = size / 3;
        int mouthX     = x + size / 2 - mouthWidth / 2;
        int mouthY     = y + (3 * size) / 5;

        if (grade.startsWith("A") || grade.startsWith("B")) {
            g2.draw(new Arc2D.Double(mouthX, mouthY - 15, mouthWidth, size / 4.0, 180, 180, Arc2D.OPEN));
        } else if (grade.startsWith("C")) {
            g2.drawLine(mouthX, mouthY, mouthX + mouthWidth, mouthY);
        } else {
            g2.draw(new Arc2D.Double(mouthX, mouthY + 10, mouthWidth, size / 4.0, 0, 180, Arc2D.OPEN));
        }

        // Emoji badge centred above face
        g2.setFont(new Font("SansSerif", Font.PLAIN, 28));
        FontMetrics fm  = g2.getFontMetrics();
        int emojiW      = fm.stringWidth(emoji);
        g2.drawString(emoji, (width - emojiW) / 2, y - 12);

        // Footer label
        g2.setColor(TEXT_MUTED);
        g2.setFont(new Font("Serif", Font.PLAIN, 12));
        String msg = "THANKS FOR PLAYING!";
        fm = g2.getFontMetrics();
        g2.drawString(msg, (width - fm.stringWidth(msg)) / 2, y + size + 28);
    }
}
