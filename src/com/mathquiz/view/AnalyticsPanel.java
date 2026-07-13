package com.mathquiz.view;

import com.mathquiz.service.AnalyticsService;
import com.mathquiz.service.SessionRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.mathquiz.config.AppTheme;

/**
 * Redesigned Premium Analytics Dashboard.
 *
 * Replaces the basic statistics grid with a comprehensive learning metrics dashboard:
 *   1. Stat cards with detailed KPIs (Overall Accuracy, Solved counts, Speed, Streaks)
 *   2. Smooth bezier Accuracy Trend Chart
 *   3. 10-Spoke Category Radar Chart
 *   4. Practice Heatmap (Activity Grid of last 28 days)
 *   5. Smart Archie speech bubble recommendations
 *   6. Strengths vs Weaknesses breakdown (highest vs lowest topics)
 *   7. Recent quiz sessions history log
 *   8. Interactive goals checklist and quick actions panel
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
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color ERROR_RED     = new Color(239, 68, 68);

    private static final String[] CATEGORY_LABELS = {
            "Addition", "Difference", "Multiplication", "Division", "Mixed", "Special",
            "Fractions", "Patterns", "Algebra", "Measurement"
    };
    private static final Color[] CATEGORY_COLORS  = {
        new Color(99, 179, 237),
        new Color(154, 205, 50),
        new Color(255, 153, 51),
        new Color(218, 112, 214),
        new Color(64, 224, 208),
        new Color(255, 99, 132),
        new Color(147, 112, 219),
        new Color(255, 215, 0),
        new Color(72, 209, 204),
        new Color(255, 127, 80)
    };

    // ── State ─────────────────────────────────────────────────────────────────
    private final QuizNavigator    nav;
    private final AnalyticsService analytics;

    // Cached data
    private List<Double>         trendData;
    private Map<String, Double>  categoryAccuracy;
    private int     totalSessions;
    private double  bestScore;
    private double  avgScore;
    private int     longestStreak;

    // ── Visual components ─────────────────────────────────────────────────────
    private LineChartPanel  lineChart;
    private RadarChartPanel radarChart;
    private JPanel statCardsHolder;
    private JPanel heatmapPanel;
    private JLabel recommendationLabel;
    private JPanel strengthsPanel;
    private JPanel recentSessionsPanel;
    private JPanel goalsPanel;

    public AnalyticsPanel(QuizNavigator nav, AnalyticsService analytics) {
        this.nav       = nav;
        this.analytics = analytics;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    public LineChartPanel getLineChart() { return lineChart; }
    public RadarChartPanel getRadarChart() { return radarChart; }

    /** Refresh data and repaint all components. */
    public void refresh() {
        trendData        = analytics.getAccuracyTrend(20);
        categoryAccuracy = analytics.getCategoryAccuracy();
        totalSessions    = analytics.getTotalSessions();
        bestScore        = analytics.getBestScore();
        avgScore         = analytics.getAverageScore();
        longestStreak    = analytics.getLongestStreak();

        lineChart.setData(trendData);
        radarChart.setData(categoryAccuracy);

        populateDashboard();
        revalidate();
        repaint();
    }

    // ── UI construction ───────────────────────────────────────────────────────
    private void build() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(0, 30, 30, 30));

        GridBagConstraints rowC = new GridBagConstraints();
        rowC.fill = GridBagConstraints.HORIZONTAL;
        rowC.weightx = 1.0;
        rowC.gridx = 0;

        // 1. Stat cards row
        rowC.gridy = 0;
        rowC.insets = new Insets(0, 0, 18, 0);
        statCardsHolder = new JPanel(new GridLayout(1, 4, 14, 0));
        statCardsHolder.setOpaque(false);
        statCardsHolder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));
        body.add(statCardsHolder, rowC);

        // 2. Chart row (Enlarged for better charts readability)
        rowC.gridy = 1;
        rowC.insets = new Insets(0, 0, 18, 0);
        JPanel chartRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartRow.setOpaque(false);
        chartRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        lineChart  = new LineChartPanel();
        radarChart = new RadarChartPanel();

        chartRow.add(wrapCard(lineChart,  "Accuracy Trend  (last 20 sessions)"));
        chartRow.add(wrapCard(radarChart, "Category Radar  (average accuracy)"));
        body.add(chartRow, rowC);

        // 3. Learning Insights & Heatmap row
        rowC.gridy = 2;
        rowC.insets = new Insets(0, 0, 18, 0);
        JPanel insightsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        insightsRow.setOpaque(false);
        insightsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Insights Left Card: Heatmap & Recommendations
        JPanel insightsLeft = new JPanel(new GridBagLayout());
        insightsLeft.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridx = 0;

        c.gridy = 0; c.weighty = 0.5;
        c.insets = new Insets(0, 0, 10, 0);
        heatmapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        heatmapPanel.setOpaque(false);
        insightsLeft.add(heatmapPanel, c);

        c.gridy = 1; c.weighty = 0.5;
        c.insets = new Insets(0, 0, 0, 0);
        JPanel recCard = new JPanel(new BorderLayout(10, 0));
        recCard.setOpaque(false);

        // Mascot Icon
        try {
            java.net.URL logoUrl = AnalyticsPanel.class.getResource("/com/mathquiz/resources/logo.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                recCard.add(new JLabel(new ImageIcon(img)), BorderLayout.WEST);
            }
        } catch (Exception e) {}

        recommendationLabel = new JLabel("<html><body>Archie says: loading recommendations...</body></html>");
        recommendationLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        recommendationLabel.setForeground(TEXT_MUTED);
        recCard.add(recommendationLabel, BorderLayout.CENTER);
        insightsLeft.add(recCard, c);

        insightsRow.add(wrapCard(insightsLeft, "📅 Practice Activity & Recommendations"));

        // Insights Right Card: Strengths vs Weaknesses
        strengthsPanel = new JPanel();
        strengthsPanel.setOpaque(false);
        strengthsPanel.setLayout(new BoxLayout(strengthsPanel, BoxLayout.Y_AXIS));
        insightsRow.add(wrapCard(strengthsPanel, "⚖️ Topic Mastery: Strengths & Weaknesses"));
        body.add(insightsRow, rowC);

        // 4. Recent history & Actions Row
        rowC.gridy = 3;
        rowC.insets = new Insets(0, 0, 0, 0);
        JPanel historyRow = new JPanel(new GridLayout(1, 2, 20, 0));
        historyRow.setOpaque(false);
        historyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        recentSessionsPanel = new JPanel();
        recentSessionsPanel.setOpaque(false);
        recentSessionsPanel.setLayout(new BoxLayout(recentSessionsPanel, BoxLayout.Y_AXIS));
        historyRow.add(wrapCard(recentSessionsPanel, " Recent Quiz History"));

        goalsPanel = new JPanel();
        goalsPanel.setOpaque(false);
        goalsPanel.setLayout(new BoxLayout(goalsPanel, BoxLayout.Y_AXIS));
        historyRow.add(wrapCard(goalsPanel, "🚀 Learning Goals & Quick Actions"));
        body.add(historyRow, rowC);

        // Scroll wrap (Forced vertical-only scrolling to prevent horizontal bar)
        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(22, 32, 14, 32));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel sub = new JLabel("ANALYTICS");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        sub.setForeground(ACCENT_GOLD);
        left.add(sub);

        JLabel title = new JLabel("Performance Dashboard");
        title.setFont(new Font("Serif", Font.PLAIN, 24));
        title.setForeground(TEXT_DARK);
        left.add(title);

        panel.add(left, BorderLayout.WEST);

        // Buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton reset = new JButton("🗑 Reset History");
        reset.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reset.setBackground(BG_CARD);
        reset.setForeground(ERROR_RED);
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
        if (!com.mathquiz.service.ParentalGate.verifyParent(this)) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all your math quiz history?\nThis cannot be undone! 🦉",
                "Reset History?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            nav.resetAllData();
        }
    }

    private JPanel wrapCard(JPanel content, String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        card.add(lbl, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Dynamic content populator
    // =========================================================================
    private void populateDashboard() {
        // 1. Stat Cards
        statCardsHolder.removeAll();
        
        int totalQuestions = analytics.getTotalQuestionsSolved();
        double totalSecs = analytics.getTotalPracticeTimeSec();
        double avgSpeed = analytics.getAverageResponseTimeSec();
        double fastestSpeed = analytics.getFastestAnswerSec();
        int currentStreak = analytics.getCurrentStreak();

        String accuracySubtitle = "Best score: " + fmt(bestScore) + "%";
        statCardsHolder.add(makeStatCard("Overall Accuracy", fmt(avgScore) + "%", accuracySubtitle, "📈"));

        String countSubtitle = "Practice: " + formatTime(totalSecs);
        statCardsHolder.add(makeStatCard("Questions Solved", String.valueOf(totalQuestions), countSubtitle, "✏"));

        String speedSubtitle = "Fastest correct: " + (fastestSpeed == 0.0 ? "N/A" : fmt(fastestSpeed) + "s");
        statCardsHolder.add(makeStatCard("Average Speed", (avgSpeed == 0.0 ? "N/A" : fmt(avgSpeed) + "s"), speedSubtitle, "⏱"));

        String streakSubtitle = "Longest: " + longestStreak + "d";
        statCardsHolder.add(makeStatCard("Practice Streak", currentStreak + "d", streakSubtitle, "🔥"));

        // 2. Heatmap
        heatmapPanel.removeAll();
        boolean[] heatmap = analytics.getPracticeHeatmap28Days();
        
        JLabel titleLbl = new JLabel("Heatmap (Last 28 Days): ");
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        titleLbl.setForeground(TEXT_MUTED);
        heatmapPanel.add(titleLbl);

        JPanel grid = new JPanel(new GridLayout(4, 7, 5, 5));
        grid.setOpaque(false);
        // Show oldest first (left-to-right)
        for (int i = 27; i >= 0; i--) {
            JPanel dot = new JPanel();
            dot.setPreferredSize(new Dimension(12, 12));
            dot.setBackground(heatmap[i] ? ACCENT_GOLD : BORDER_CLR);
            dot.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 10), 1));
            grid.add(dot);
        }
        heatmapPanel.add(grid);

        // 3. Recommendation
        if (totalSessions == 0) {
            recommendationLabel.setText("<html><body><font face='Segoe UI Emoji'>\ud83e\udd89</font> <b>Archie says:</b> \"Welcome! Complete a math quiz to see your learning recommendations!\"</body></html>");
        } else {
            String weakCat = analytics.getWeakestCategory();
            String weakDiff = analytics.getWeakestDifficulty();
            recommendationLabel.setText("<html><body><font face='Segoe UI Emoji'>\ud83e\udd89</font> <b>Archie says:</b> \"To boost your math skills, try a Practice run focusing on <b>" + weakCat + "</b> on <b>" + weakDiff + "</b>!\"</body></html>");
        }

        // 4. Strengths & Weaknesses
        strengthsPanel.removeAll();
        Map<String, Double> acc = analytics.getCategoryAccuracy();
        java.util.List<Map.Entry<String, Double>> sortedAcc = new ArrayList<>(acc.entrySet());
        // Sort highest accuracy first
        Collections.sort(sortedAcc, (a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Filter played categories vs unplayed
        List<Map.Entry<String, Double>> played = new ArrayList<>();
        for (Map.Entry<String, Double> e : sortedAcc) {
            if (e.getValue() > 0) played.add(e);
        }

        if (played.isEmpty()) {
            JLabel lbl = new JLabel("Play sessions to view topic strengths!");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(TEXT_MUTED);
            strengthsPanel.add(lbl);
        } else {
            // Strengths: top 2
            int shownStr = 0;
            List<String> strengthsList = new ArrayList<>();
            for (int i = 0; i < played.size() && shownStr < 2; i++) {
                Map.Entry<String, Double> e = played.get(i);
                if (e.getValue() >= 70.0) { // Baseline for strength
                    JLabel item = new JLabel(" ✅  " + e.getKey() + ": " + fmt(e.getValue()) + "% Accuracy");
                    item.setFont(new Font("SansSerif", Font.BOLD, 12));
                    item.setForeground(SUCCESS_GREEN);
                    strengthsPanel.add(item);
                    strengthsList.add(e.getKey());
                    shownStr++;
                }
            }
            if (shownStr == 0) {
                JLabel item = new JLabel(" 🔍 Keep practicing to build strengths!");
                item.setFont(new Font("SansSerif", Font.PLAIN, 12));
                item.setForeground(TEXT_MUTED);
                strengthsPanel.add(item);
            }
            
            strengthsPanel.add(Box.createVerticalStrut(10));
            
            // Weaknesses: bottom 2
            int shownWeak = 0;
            for (int i = played.size() - 1; i >= 0 && shownWeak < 2; i--) {
                Map.Entry<String, Double> e = played.get(i);
                if (strengthsList.contains(e.getKey())) continue; // prevent overlap
                if (e.getValue() < 70.0) { // Baseline for weakness
                    JLabel item = new JLabel(" ⚠️  " + e.getKey() + ": " + fmt(e.getValue()) + "% accuracy");
                    item.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    item.setForeground(TEXT_DARK);
                    strengthsPanel.add(item);
                    shownWeak++;
                }
            }
            if (shownWeak == 0 && shownStr > 0) {
                JLabel item = new JLabel(" 🎉 Outstanding! No weak areas detected!");
                item.setFont(new Font("SansSerif", Font.BOLD, 12));
                item.setForeground(ACCENT_GOLD);
                strengthsPanel.add(item);
            }
        }

        // 5. Recent Sessions
        recentSessionsPanel.removeAll();
        SessionRepository repo = new SessionRepository();
        List<Map<String, Object>> recentRaw = repo.loadRaw();
        if (recentRaw.isEmpty()) {
            JLabel lbl = new JLabel("No sessions played yet!");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(TEXT_MUTED);
            recentSessionsPanel.add(lbl);
        } else {
            int displayedCount = 0;
            for (int i = 0; i < recentRaw.size() && displayedCount < 4; i++) {
                Map<String, Object> sess = recentRaw.get(i);
                String cat = (String) sess.get("category");
                String diff = (String) sess.get("difficulty");
                double pct = (double) sess.get("percentage");
                long durMs = (long) sess.get("durationMs");
                int questions = (int) sess.get("totalQuestions");
                double speed = (durMs / 1000.0) / questions;
                
                String date = (String) sess.get("timestamp");
                if (date != null && date.length() >= 10) {
                    date = date.substring(5, 10).replace("-", "/"); // "MM/DD"
                } else {
                    date = "";
                }

                JLabel line = new JLabel(String.format(" 📄 %s (%s)  ·  %d%% Score  ·  %s/q  ·  %s", cat, diff, (int)pct, fmt(speed) + "s", date));
                line.setFont(new Font("SansSerif", Font.PLAIN, 11));
                line.setForeground(TEXT_DARK);
                recentSessionsPanel.add(line);
                recentSessionsPanel.add(Box.createVerticalStrut(4));
                displayedCount++;
            }
        }

        // 6. Goals & Quick Actions
        goalsPanel.removeAll();
        
        // Goals Checklist
        java.text.SimpleDateFormat daySdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String todayStr = daySdf.format(new java.util.Date());
        boolean dailyChallengeDone = com.mathquiz.config.AppConfig.getInstance().getLastDailyChallengeDate().equals(todayStr);
        boolean streakMaintained = currentStreak > 0 && heatmap[0];

        JCheckBox dailyBox = new JCheckBox("Complete Daily Quest today", dailyChallengeDone);
        dailyBox.setEnabled(false);
        dailyBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dailyBox.setOpaque(false);
        dailyBox.setForeground(TEXT_DARK);
        goalsPanel.add(dailyBox);

        JCheckBox streakBox = new JCheckBox("Keep practice streak alive", streakMaintained);
        streakBox.setEnabled(false);
        streakBox.setFont(new Font("SansSerif", Font.PLAIN, 12));
        streakBox.setOpaque(false);
        streakBox.setForeground(TEXT_DARK);
        goalsPanel.add(streakBox);

        goalsPanel.add(Box.createVerticalStrut(12));

        // Quick Actions Row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        JButton practiceBtn = new JButton("🎯 Smart Practice");
        practiceBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        practiceBtn.setFocusPainted(false);
        practiceBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        practiceBtn.addActionListener(e -> nav.startSmartPractice());
        btnRow.add(practiceBtn);

        JButton dailyBtn = new JButton("📅 Daily Challenge");
        dailyBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dailyBtn.setFocusPainted(false);
        dailyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dailyBtn.addActionListener(e -> nav.startDailyChallenge());
        btnRow.add(dailyBtn);

        goalsPanel.add(btnRow);
    }

    private JPanel makeStatCard(String label, String value, String subtitle, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setOpaque(true);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 26));
        iconLbl.setBorder(new EmptyBorder(2, 4, 2, 12));
        iconLbl.setForeground(ACCENT_GOLD);
        card.add(iconLbl, BorderLayout.WEST);

        JPanel txtPanel = new JPanel();
        txtPanel.setOpaque(false);
        txtPanel.setLayout(new BoxLayout(txtPanel, BoxLayout.Y_AXIS));

        JLabel lblLbl = new JLabel(label.toUpperCase());
        lblLbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        lblLbl.setForeground(TEXT_MUTED);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Serif", Font.BOLD, 22));
        valLbl.setForeground(TEXT_DARK);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subLbl.setForeground(TEXT_MUTED);

        txtPanel.add(lblLbl);
        txtPanel.add(Box.createVerticalStrut(2));
        txtPanel.add(valLbl);
        txtPanel.add(Box.createVerticalStrut(2));
        txtPanel.add(subLbl);

        card.add(txtPanel, BorderLayout.CENTER);
        return card;
    }

    private static String fmt(double v) {
        return String.format("%.1f", v);
    }

    private String formatTime(double totalSeconds) {
        if (totalSeconds < 60) {
            return (int) totalSeconds + "s";
        }
        double mins = totalSeconds / 60.0;
        return String.format("%.1f", mins) + " min";
    }

    // =========================================================================
    // Inner panel — Accuracy Line Chart
    // =========================================================================
    private class LineChartPanel extends JPanel {
        private List<Double> data;

        LineChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 200));
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

            // Grid lines
            g2.setStroke(new BasicStroke(1f));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int pct : new int[]{0, 25, 50, 75, 100}) {
                int y = padT + chartH - (int)(chartH * pct / 100.0);
                g2.setColor(AppTheme.getBorderClr());
                g2.drawLine(padL, y, padL + chartW, y);
                g2.setColor(AppTheme.getTextMuted());
                g2.drawString(pct + "%", 2, y + 4);
            }

            int n = data.size();
            float[] xs = new float[n];
            float[] ys = new float[n];
            for (int i = 0; i < n; i++) {
                xs[i] = padL + (float) i / (n - 1) * chartW;
                ys[i] = padT + chartH - (float)(data.get(i) / 100.0 * chartH);
            }

            // Fill
            GeneralPath fillPath = buildBezierPath(xs, ys, n);
            fillPath.lineTo(xs[n - 1], padT + chartH);
            fillPath.lineTo(xs[0],     padT + chartH);
            fillPath.closePath();
            Color accentColor = AppTheme.getAccentGold();
            GradientPaint grad = new GradientPaint(0, padT, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80),
                                                   0, padT + chartH, new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 0));
            g2.setPaint(grad);
            g2.fill(fillPath);

            // Curve stroke
            g2.setColor(AppTheme.getAccentGold());
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(buildBezierPath(xs, ys, n));

            // Dots
            for (int i = 0; i < n; i++) {
                g2.setColor(AppTheme.getBgCard());
                g2.fillOval((int) xs[i] - 4, (int) ys[i] - 4, 8, 8);
                g2.setColor(AppTheme.getAccentGold());
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval((int) xs[i] - 4, (int) ys[i] - 4, 8, 8);
            }

            // X-axis labels
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
            setPreferredSize(new Dimension(340, 270));
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
            float maxR = Math.min(cx - 40, cy - 14);

            int n = CATEGORY_LABELS.length;

            // Empty state check first
            if (data == null || data.values().stream().allMatch(v -> v == 0.0)) {
                g2.setColor(AppTheme.getBorderClr());
                g2.setStroke(new BasicStroke(1f));
                Polygon hex = makeHexPoly(cx, cy, maxR, n, -Math.PI / 2);
                g2.draw(hex);

                g2.setColor(AppTheme.getTextMuted());
                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                FontMetrics fm = g2.getFontMetrics();
                String msg = "No category data yet";
                g2.drawString(msg, cx - fm.stringWidth(msg) / 2f, cy + 4);
                g2.dispose();
                return;
            }

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

            // Spokes
            g2.setColor(AppTheme.getBorderClr());
            g2.setStroke(new BasicStroke(1f));
            for (int i = 0; i < n; i++) {
                double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
                g2.drawLine((int) cx, (int) cy,
                            (int)(cx + maxR * Math.cos(angle)),
                            (int)(cy + maxR * Math.sin(angle)));
            }

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

            Polygon dataPoly = new Polygon();
            for (int i = 0; i < n; i++) dataPoly.addPoint((int) dataX[i], (int) dataY[i]);
            Color accentColor = AppTheme.getAccentGold();
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 55));
            g2.fillPolygon(dataPoly);

            g2.setColor(AppTheme.getAccentGold());
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawPolygon(dataPoly);

            // Vertices
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            for (int i = 0; i < n; i++) {
                g2.setColor(CATEGORY_COLORS[i]);
                g2.fillOval((int) dataX[i] - 4, (int) dataY[i] - 4, 8, 8);

                double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
                float lx = (float)(cx + (maxR + 12) * Math.cos(angle));
                float ly = (float)(cy + (maxR + 12) * Math.sin(angle));
                String label = CATEGORY_LABELS[i];
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(AppTheme.getTextMuted());
                g2.drawString(label, lx - fm.stringWidth(label) / 2f, ly + 4);
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
                    btn.setForeground(ERROR_RED);
                } else {
                    btn.setForeground(AppTheme.getTextMuted());
                }
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                        new EmptyBorder(8, 18, 8, 18)));
            } else if (c instanceof JCheckBox) {
                JCheckBox cb = (JCheckBox) c;
                cb.setForeground(AppTheme.getTextDark());
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
