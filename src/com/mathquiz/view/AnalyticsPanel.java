package com.mathquiz.view;

import com.mathquiz.service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.Map;
import com.mathquiz.config.AppTheme;


/**
 * Analytics Dashboard — Phase 2 Intelligence Layer.
 *
 * A full-screen panel containing three custom Java2D visualisations:
 *
 *   1. Accuracy Line Chart  — bezier-smoothed trend of last 20 sessions (gold fill)
 *   2. Category Radar Chart — hexagonal radar of per-category average accuracy
 *   3. Summary Stat Cards   — Total Sessions · Best Score · Avg Score · Longest Streak
 *
 * Navigation: reached via the "📊 Analytics" button on WelcomePanel.
 *             "← Back" returns to the welcome screen via QuizNavigator.
 */
public class AnalyticsPanel extends JPanel {

    // ── Design tokens ─────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY   = new Color(250, 249, 246);
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color ACCENT_GOLD  = new Color(184, 150, 110);
    private static final Color GOLD_LIGHT   = new Color(245, 235, 215);
    private static final Color TEXT_DARK    = new Color(28, 25, 23);
    private static final Color TEXT_MUTED   = new Color(120, 113, 108);
    private static final Color BORDER_CLR   = new Color(230, 227, 220);
    private static final Color GRID_COLOR   = new Color(235, 232, 225);

    private static final String[] CATEGORY_LABELS =
            {"Addition", "Difference", "Multiply", "Division", "Mixed", "Special"};
    private static final Color[] CATEGORY_COLORS  = {
        new Color(99, 179, 237),   // blue
        new Color(154, 205, 50),   // yellow-green
        new Color(255, 153, 51),   // orange
        new Color(218, 112, 214),  // orchid
        new Color(64, 224, 208),   // turquoise
        new Color(255, 99, 132),   // pink-red
    };

    // ── State ─────────────────────────────────────────────────────────────────
    private final QuizNavigator    nav;
    private final AnalyticsService analytics;

    // Cached data (populated on showPanel())
    private List<Double>         trendData;
    private Map<String, Double>  categoryAccuracy;
    private int     totalSessions;
    private double  bestScore;
    private double  avgScore;
    private int     longestStreak;

    // ── Sub-panels (inner classes) ────────────────────────────────────────────
    private LineChartPanel  lineChart;
    private RadarChartPanel radarChart;

    public AnalyticsPanel(QuizNavigator nav, AnalyticsService analytics) {
        this.nav       = nav;
        this.analytics = analytics;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    public LineChartPanel getLineChart() { return lineChart; }
    public RadarChartPanel getRadarChart() { return radarChart; }

    // ── Called by QuizFrame before showing this card ──────────────────────────

    /** Refresh data and repaint all charts. */
    public void refresh() {
        trendData        = analytics.getAccuracyTrend(20);
        categoryAccuracy = analytics.getCategoryAccuracy();
        totalSessions    = analytics.getTotalSessions();
        bestScore        = analytics.getBestScore();
        avgScore         = analytics.getAverageScore();
        longestStreak    = analytics.getLongestStreak();

        lineChart.setData(trendData);
        radarChart.setData(categoryAccuracy);
        rebuildStatCards();
        revalidate();
        repaint();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private JPanel statCardsHolder;

    private void build() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(0, 30, 30, 30));

        // Stat cards row
        statCardsHolder = new JPanel(new GridLayout(1, 4, 14, 0));
        statCardsHolder.setOpaque(false);
        statCardsHolder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        body.add(statCardsHolder);
        body.add(Box.createVerticalStrut(20));

        // Chart row
        JPanel chartRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartRow.setOpaque(false);
        chartRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));

        lineChart  = new LineChartPanel();
        radarChart = new RadarChartPanel();

        chartRow.add(wrapCard(lineChart,  "Accuracy Trend  (last 20 sessions)"));
        chartRow.add(wrapCard(radarChart, "Category Radar  (average accuracy)"));

        body.add(chartRow);

        // Scrollable in case window is small
        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(28, 32, 16, 32));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel sub = new JLabel("ANALYTICS");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub.setForeground(ACCENT_GOLD);
        left.add(sub);

        JLabel title = new JLabel("Performance Dashboard");
        title.setFont(new Font("Serif", Font.PLAIN, 26));
        title.setForeground(TEXT_DARK);
        left.add(title);

        panel.add(left, BorderLayout.WEST);

        // Right panel for buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton reset = new JButton("🗑 Reset History");
        reset.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reset.setBackground(BG_CARD);
        reset.setForeground(new Color(239, 68, 68)); // red color for delete
        reset.setFocusPainted(false);
        reset.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(8, 18, 8, 18)));
        reset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reset.addActionListener(e -> handleResetHistory());
        right.add(reset);

        JButton back = new JButton("← Back");
        back.setFont(new Font("SansSerif", Font.PLAIN, 12));
        back.setBackground(BG_CARD);
        back.setForeground(TEXT_MUTED);
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(8, 18, 8, 18)));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> nav.goToWelcome());
        right.add(back);

        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    private void handleResetHistory() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all your math quiz history?\nThis cannot be undone! 🦉",
                "Reset History?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            analytics.clearHistory();
            refresh();
        }
    }


    private JPanel wrapCard(JPanel content, String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        card.add(lbl, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void rebuildStatCards() {
        statCardsHolder.removeAll();
        statCardsHolder.add(makeStatCard("Total Sessions", String.valueOf(totalSessions), "📚"));
        statCardsHolder.add(makeStatCard("Best Score",     fmt(bestScore) + "%",          "🏆"));
        statCardsHolder.add(makeStatCard("Average Score",  fmt(avgScore) + "%",            "📈"));
        statCardsHolder.add(makeStatCard("Longest Streak", longestStreak + " day" + (longestStreak == 1 ? "" : "s"), "🔥"));
    }

    private JPanel makeStatCard(String label, String value, String icon) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(14, 16, 14, 16)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 22));
        card.add(iconLbl, c);

        c.gridy = 1;
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Serif", Font.BOLD, 20));
        valLbl.setForeground(TEXT_DARK);
        card.add(valLbl, c);

        c.gridy = 2;
        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblLbl.setForeground(TEXT_MUTED);
        card.add(lblLbl, c);

        return card;
    }

    private static String fmt(double v) {
        return String.format("%.1f", v);
    }

    // =========================================================================
    // Inner panel — Accuracy Line Chart
    // =========================================================================

    private class LineChartPanel extends JPanel {

        private List<Double> data;

        LineChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 220));
        }

        void setData(List<Double> d) { this.data = d; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int padL = 40, padR = 16, padT = 12, padB = 32;
            int chartW = w - padL - padR;
            int chartH = h - padT - padB;

            // Empty state
            if (data == null || data.size() < 2) {
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.setColor(TEXT_MUTED);
                String msg = "Complete a few sessions";
                String msg2 = "to see your trend!";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg,  (w - fm.stringWidth(msg))  / 2, h / 2 - 8);
                g2.drawString(msg2, (w - fm.stringWidth(msg2)) / 2, h / 2 + 12);
                g2.dispose();
                return;
            }

            // Draw grid lines at 0, 25, 50, 75, 100
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int pct : new int[]{0, 25, 50, 75, 100}) {
                int y = padT + chartH - (int)(chartH * pct / 100.0);
                g2.setColor(AppTheme.getBorderClr());
                g2.drawLine(padL, y, padL + chartW, y);
                g2.setColor(AppTheme.getTextMuted());
                g2.drawString(pct + "%", 2, y + 4);
            }

            // Build X / Y coordinates for each data point
            int n = data.size();
            float[] xs = new float[n];
            float[] ys = new float[n];
            for (int i = 0; i < n; i++) {
                xs[i] = padL + (float) i / (n - 1) * chartW;
                ys[i] = padT + chartH - (float)(data.get(i) / 100.0 * chartH);
            }

            // Gold gradient fill under the curve
            GeneralPath fillPath = buildBezierPath(xs, ys, n);
            fillPath.lineTo(xs[n - 1], padT + chartH);
            fillPath.lineTo(xs[0],     padT + chartH);
            fillPath.closePath();
            Color accentColor = AppTheme.getAccentGold();
            GradientPaint grad = new GradientPaint(0, padT, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80),
                                                   0, padT + chartH, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0));
            g2.setPaint(grad);
            g2.fill(fillPath);

            // Gold line
            g2.setColor(AppTheme.getAccentGold());
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(buildBezierPath(xs, ys, n));

            // Data-point dots
            for (int i = 0; i < n; i++) {
                g2.setColor(AppTheme.getBgCard());
                g2.fillOval((int) xs[i] - 4, (int) ys[i] - 4, 8, 8);
                g2.setColor(AppTheme.getAccentGold());
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval((int) xs[i] - 4, (int) ys[i] - 4, 8, 8);
            }

            // X axis labels: first, last, maybe middle
            g2.setColor(AppTheme.getTextMuted());
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            FontMetrics fm = g2.getFontMetrics();
            drawXLabel(g2, fm, "1", xs[0], padT + chartH + 14);
            if (n > 1) drawXLabel(g2, fm, String.valueOf(n), xs[n - 1], padT + chartH + 14);
            if (n >= 5) {
                int mid = n / 2;
                drawXLabel(g2, fm, String.valueOf(mid + 1), xs[mid], padT + chartH + 14);
            }

            g2.dispose();

        }

        private GeneralPath buildBezierPath(float[] xs, float[] ys, int n) {
            GeneralPath path = new GeneralPath();
            path.moveTo(xs[0], ys[0]);
            for (int i = 0; i < n - 1; i++) {
                float cx1 = xs[i]     + (xs[i + 1] - xs[i]) * 0.5f;
                float cy1 = ys[i];
                float cx2 = xs[i]     + (xs[i + 1] - xs[i]) * 0.5f;
                float cy2 = ys[i + 1];
                path.curveTo(cx1, cy1, cx2, cy2, xs[i + 1], ys[i + 1]);
            }
            return path;
        }

        private void drawXLabel(Graphics2D g2, FontMetrics fm, String text, float x, float y) {
            g2.drawString(text, x - fm.stringWidth(text) / 2f, y);
        }
    }

    // =========================================================================
    // Inner panel — Category Radar Chart
    // =========================================================================

    private class RadarChartPanel extends JPanel {

        private Map<String, Double> data;

        RadarChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 220));
        }

        void setData(Map<String, Double> d) { this.data = d; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            float cx = w / 2f;
            float cy = h / 2f - 8;
            float maxR = Math.min(cx, cy) - 28;

            int n = CATEGORY_LABELS.length;

            // Draw background concentric hexagons at 25, 50, 75, 100%
            g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
            for (int ring : new int[]{25, 50, 75, 100}) {
                float r = maxR * ring / 100f;
                g2.setColor(AppTheme.getBorderClr());
                g2.setStroke(new BasicStroke(1f));
                Polygon hex = makeHexPoly(cx, cy, r, n, -Math.PI / 2);
                g2.draw(hex);
                if (ring < 100) {
                    g2.setColor(AppTheme.getTextMuted());
                    g2.drawString(ring + "%", cx + r + 2, cy + 4);
                }
            }

            // Axis spokes
            g2.setColor(AppTheme.getBorderClr());
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
                g2.drawLine((int) cx, (int) cy,
                            (int)(cx + maxR * Math.cos(angle)),
                            (int)(cy + maxR * Math.sin(angle)));
            }

            // No data? draw empty state
            if (data == null || data.values().stream().allMatch(v -> v == 0.0)) {
                g2.setColor(AppTheme.getTextMuted());
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                String msg = "No category data yet";
                g2.drawString(msg, cx - fm.stringWidth(msg) / 2f, cy + 4);
                g2.dispose();
                return;
            }


            // Data polygon
            float[] dataX = new float[n];
            float[] dataY = new float[n];
            String[] cats = AnalyticsService.ALL_CATEGORIES;

            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
                String catKey = cats[i];
                double pct = data.getOrDefault(catKey, 0.0);
                float r = maxR * (float)(pct / 100.0);
                dataX[i] = (float)(cx + r * Math.cos(angle));
                dataY[i] = (float)(cy + r * Math.sin(angle));
            }

            // Fill
            Polygon dataPoly = new Polygon();
            for (int i = 0; i < n; i++) dataPoly.addPoint((int) dataX[i], (int) dataY[i]);
            Color accentColor = AppTheme.getAccentGold();
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 55));
            g2.fillPolygon(dataPoly);

            // Stroke
            g2.setColor(AppTheme.getAccentGold());
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolygon(dataPoly);


            // Vertex dots and labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int i = 0; i < n; i++) {
                // Dot at vertex
                g2.setColor(CATEGORY_COLORS[i]);
                g2.fillOval((int) dataX[i] - 4, (int) dataY[i] - 4, 8, 8);

                // Label at spoke tip
                double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
                float lx = (float)(cx + (maxR + 14) * Math.cos(angle));
                float ly = (float)(cy + (maxR + 14) * Math.sin(angle));
                String label = CATEGORY_LABELS[i];
                FontMetrics fm = g2.getFontMetrics();
                float textX = lx - fm.stringWidth(label) / 2f;
                float textY = ly + 4;
                g2.setColor(AppTheme.getTextMuted());
                g2.drawString(label, textX, textY);
            }

            g2.dispose();
        }

        private Polygon makeHexPoly(float cx, float cy, float r, int sides, double startAngle) {
            Polygon p = new Polygon();
            for (int i = 0; i < sides; i++) {
                double a = startAngle + 2 * Math.PI * i / sides;
                p.addPoint((int)(cx + r * Math.cos(a)), (int)(cy + r * Math.sin(a)));
            }
            return p;
        }
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        recolorTree(this);
        if (lineChart != null) lineChart.repaint();
        if (radarChart != null) radarChart.repaint();
    }

    private void recolorTree(Container parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                if (p.isOpaque() && p != this && p != lineChart && p != radarChart) {
                    p.setBackground(AppTheme.getBgCard());
                    p.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                            p.getBorder() != null && p.getBorder() instanceof javax.swing.border.CompoundBorder
                                    ? ((javax.swing.border.CompoundBorder)p.getBorder()).getInsideBorder()
                                    : new EmptyBorder(16, 16, 16, 16)));
                } else {
                    p.setBackground(AppTheme.getBgPrimary());
                }
                recolorTree(p);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() > 20) {
                    lbl.setForeground(AppTheme.getTextDark());
                } else if (lbl.getFont().getSize() > 10) {
                    lbl.setForeground(AppTheme.getTextDark());
                } else {
                    lbl.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setBackground(AppTheme.getBgCard());
                if (btn.getText().contains("Reset")) {
                    btn.setForeground(new Color(239, 68, 68));
                } else {
                    btn.setForeground(AppTheme.getTextMuted());
                }
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                        new EmptyBorder(8, 18, 8, 18)));
            } else if (c instanceof JScrollPane) {
                JScrollPane s = (JScrollPane) c;
                s.getViewport().setBackground(AppTheme.getBgPrimary());
                Component view = s.getViewport().getView();
                if (view instanceof Container) {
                    recolorTree((Container) view);
                }
            }
        }
    }
}

