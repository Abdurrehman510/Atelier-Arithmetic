package com.mathquiz.view;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.config.AppTheme;
import com.mathquiz.config.AppConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Premium Performance Dashboard replacing the old Results Panel.
 *
 * Renders a Bespoke 4-Card Educational Analytics layout:
 *   1. KPI Metrics Card (Score, Success %, Total Time, Avg Speed)
 *   2. Session Mastery & Speed highlights (Fastest Answer, Hardest Question)
 *   3. Archie's Mascot Advice Panel (Encouraging feedback, strengths analysis)
 *   4. Next Practice Steps (Interactive recommendations & quick launcher button)
 *
 * Exposes gradeLabel and restartButton for TourManager spotlight targeting.
 */
public class ResultsPanel extends JPanel {

    private final QuizNavigator nav;
    private QuizSession activeSession;

    // UI elements to update dynamically
    private JLabel dashboardTitleLabel;
    private JLabel scoreLabel;
    private JLabel percentLabel;
    private JLabel gradeLabel;
    private JLabel durationLabel;
    private JLabel speedLabel;

    private JLabel fastestQuestionLabel;
    private JLabel hardestQuestionLabel;

    private JLabel adviceLabel;
    private JLabel mascotIcon;

    private JLabel nextActionPromptLabel;
    private JButton actionButton;

    // Footer actions
    private JButton restartButton;
    private JButton reviewBtn;
    private JButton printBtn;
    private JButton guideBtn;

    // Cards panels
    private JPanel kpiCard;
    private JPanel highlightsCard;
    private JPanel adviceCard;
    private JPanel nextStepsCard;

    // Stars earned display
    private JLabel starsEarnedLabel;

    public ResultsPanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(AppTheme.getBgPrimary());
        setLayout(new BorderLayout());
        build();
    }

    // ── Public accessors for TourManager ─────────────────────────────────────
    public JLabel  getGradeLabel()    { return gradeLabel;    }
    public JButton getRestartButton() { return restartButton; }
    public JButton getReviewBtn()     { return reviewBtn;     }
    public JButton getPrintBtn()      { return printBtn;      }

    // ── Public update ─────────────────────────────────────────────────────────

    /** Populate dashboard cards and metrics from the completed quiz session. */
    public void populate(QuizSession session) {
        this.activeSession = session;

        // 1. KPI Card
        scoreLabel.setText("🎯  Score: " + session.getCorrectAnswersCount() + " / " + session.getTotalQuestions());
        percentLabel.setText("📈  Success Rate: " + String.format("%.1f%%", session.getPercentage()));
        gradeLabel.setText(session.getGradeEmoji() + "  Grade: " + session.getGrade());
        durationLabel.setText("⏱  Total Time: " + formatDuration(session.getDurationMs()));

        double avgSpeed = 0.0;
        if (session.getTotalQuestions() > 0) {
            long totalQuestionTime = session.getResults().stream().mapToLong(QuestionResult::getTimeSpentMs).sum();
            avgSpeed = (totalQuestionTime / 1000.0) / session.getTotalQuestions();
        }
        speedLabel.setText("⚡  Avg Speed: " + String.format("%.1fs / question", avgSpeed));

        // 2. Highlights Card
        calculateMasteryHighlights(session.getResults());

        // 3. Archie's Advice Card
        generateArchiesAdvice(session);

        // 4. Actionable Next Steps Card
        generateNextSteps(session);

        // Update dashboard title with profile context
        dashboardTitleLabel.setText("Performance Dashboard: " + AppConfig.getInstance().getCurrentProfile());

        // Hide stars label when called without reward (tour, review back, etc.)
        if (starsEarnedLabel != null) starsEarnedLabel.setVisible(false);

        applyTheme();
        revalidate();
        repaint();
    }

    /**
     * Populate with reward information displayed in the KPI card.
     * Called by QuizFrame after a real completed quiz session.
     */
    public void populate(QuizSession session, com.mathquiz.service.RewardService.RewardResult reward) {
        populate(session);
        if (reward != null && starsEarnedLabel != null) {
            starsEarnedLabel.setText("\u2605 +" + reward.total + " Stars Earned!");
            starsEarnedLabel.setToolTipText("<html><pre>" + reward.toSummary().replace("\n", "<br>") + "</pre></html>");
            starsEarnedLabel.setVisible(true);
        }
    }

    // ── Calculations & Content Generators ────────────────────────────────────

    private String formatDuration(long ms) {
        long totalSecs = ms / 1000;
        long mins = totalSecs / 60;
        long secs = totalSecs % 60;
        if (mins > 0) {
            return mins + "m " + secs + "s";
        }
        return secs + "s";
    }

    private void calculateMasteryHighlights(List<QuestionResult> results) {
        QuestionResult fastest = null;
        QuestionResult hardest = null;

        // Find fastest CORRECTLY solved question
        for (QuestionResult r : results) {
            if (r.isCorrect()) {
                if (fastest == null || r.getTimeSpentMs() < fastest.getTimeSpentMs()) {
                    fastest = r;
                }
            }
        }

        // Find challenge area: slowest incorrect question, or slowest correct question if all correct
        for (QuestionResult r : results) {
            if (!r.isCorrect()) {
                if (hardest == null || hardest.isCorrect() || r.getTimeSpentMs() > hardest.getTimeSpentMs()) {
                    hardest = r;
                }
            } else {
                if (hardest == null || (hardest.isCorrect() && r.getTimeSpentMs() > hardest.getTimeSpentMs())) {
                    hardest = r;
                }
            }
        }

        if (fastest != null) {
            fastestQuestionLabel.setText("🚀  Fastest solved: " + fastest.getExpression() + " = " + fastest.getUserAnswer() + " in " + fastest.getTimeFormatted());
        } else {
            fastestQuestionLabel.setText("🚀  Fastest solved: None");
        }

        if (hardest != null) {
            String status = hardest.isCorrect() ? " (Correct)" : " (Incorrect)";
            hardestQuestionLabel.setText("🧠  Challenge area: " + hardest.getExpression() + " took " + hardest.getTimeFormatted() + status);
        }
    }

    private void generateArchiesAdvice(QuizSession session) {
        double pct = session.getPercentage();
        String advice;
        String emoji;

        if (pct >= 95.0) {
            advice = "True Math Mastery! You calculated everything flawlessly with incredible accuracy. Ready for a step up?";
            emoji = "🦉🌟";
        } else if (pct >= 80.0) {
            advice = "Excellent work! You have strong core math skills. Focus on answering calculations under 3s to grow your speed arpeggios.";
            emoji = "🦉💪";
        } else if (pct >= 50.0) {
            advice = "Solid performance! You solved many correctly. Archie suggests reviewing incorrect items in the review table to iron out tricks.";
            emoji = "🦉👍";
        } else {
            advice = "Great effort! Practice makes champions. Archie recommends starting an Easy category session to build calculation confidence!";
            emoji = "🦉🌱";
        }

        adviceLabel.setText("<html><body style='width: 250px; text-align: left;'>" + advice + "</body></html>");
        mascotIcon.setText(emoji);
    }

    private void generateNextSteps(QuizSession session) {
        double pct = session.getPercentage();
        if (pct >= 85.0) {
            if ("Easy".equalsIgnoreCase(session.getDifficulty())) {
                nextActionPromptLabel.setText("You are ready for the next challenge!");
                actionButton.setText("STEP UP TO MEDIUM DIFFICULTY");
                actionButton.addActionListener(e -> {
                    nav.goToWelcome();
                });
            } else if ("Medium".equalsIgnoreCase(session.getDifficulty())) {
                nextActionPromptLabel.setText("Incredible skills! Level up to master.");
                actionButton.setText("STEP UP TO HARD DIFFICULTY");
                actionButton.addActionListener(e -> {
                    nav.goToWelcome();
                });
            } else {
                nextActionPromptLabel.setText("Master class quizzer! Try the seeded daily run.");
                actionButton.setText("PLAY TODAY'S DAILY CHALLENGE");
                actionButton.addActionListener(e -> {
                    nav.startDailyChallenge();
                });
            }
        } else {
            nextActionPromptLabel.setText("Review weaknesses to improve mastery!");
            actionButton.setText("START SMART PRACTICE MODE");
            actionButton.addActionListener(e -> {
                nav.startSmartPractice();
            });
        }
    }

    // ── UI Construction ──────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildDashboardGrid(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(25, 40, 5, 40));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel categoryLabel = new JLabel("BESPOKE LEARNING METRICS");
        categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        categoryLabel.setForeground(AppTheme.getAccentGold());
        left.add(categoryLabel);

        dashboardTitleLabel = new JLabel("Performance Dashboard");
        dashboardTitleLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        dashboardTitleLabel.setForeground(AppTheme.getTextDark());
        left.add(dashboardTitleLabel);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JPanel buildDashboardGrid() {
        JPanel container = new JPanel(new GridLayout(2, 2, 16, 16));
        container.setOpaque(false);
        container.setBorder(new EmptyBorder(12, 40, 18, 40));

        // Card 1: Key Performance Metrics
        JPanel kpiContent = new JPanel();
        kpiContent.setLayout(new BoxLayout(kpiContent, BoxLayout.Y_AXIS));
        scoreLabel = createMetricsLabel("");
        percentLabel = createMetricsLabel("");
        gradeLabel = new JLabel("");
        gradeLabel.setFont(new Font("Serif", Font.BOLD, 18));
        gradeLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        durationLabel = createMetricsLabel("");
        speedLabel = createMetricsLabel("");

        kpiContent.add(scoreLabel);
        kpiContent.add(percentLabel);
        kpiContent.add(gradeLabel);
        kpiContent.add(durationLabel);
        kpiContent.add(speedLabel);

        // Stars reward display (hidden initially; shown after real quiz)
        starsEarnedLabel = new JLabel("");
        starsEarnedLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        starsEarnedLabel.setForeground(new Color(184, 150, 110)); // gold
        starsEarnedLabel.setBorder(new EmptyBorder(8, 0, 0, 0));
        starsEarnedLabel.setVisible(false);
        kpiContent.add(starsEarnedLabel);

        kpiCard = createCardPanel("📊 Key Metrics", kpiContent);
        container.add(kpiCard);

        // Card 2: Highlights & Challenges
        JPanel highlightsContent = new JPanel();
        highlightsContent.setLayout(new BoxLayout(highlightsContent, BoxLayout.Y_AXIS));
        fastestQuestionLabel = createMetricsLabel("");
        hardestQuestionLabel = createMetricsLabel("");
        JLabel speedTip = new JLabel("💡 Fast answers unlock chime arpeggios.");
        speedTip.setFont(new Font("SansSerif", Font.ITALIC, 11));
        speedTip.setForeground(AppTheme.getTextMuted());
        speedTip.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        highlightsContent.add(fastestQuestionLabel);
        highlightsContent.add(Box.createVerticalStrut(10));
        highlightsContent.add(hardestQuestionLabel);
        highlightsContent.add(Box.createVerticalStrut(14));
        highlightsContent.add(speedTip);

        highlightsCard = createCardPanel("🏆 Session Mastery", highlightsContent);
        container.add(highlightsCard);

        // Card 3: Mascot Insights & Remarks
        JPanel adviceContent = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 14);
        gc.gridx = 0; gc.gridy = 0;

        mascotIcon = new JLabel("🦉");
        mascotIcon.setFont(new Font("SansSerif", Font.PLAIN, 42));
        adviceContent.add(mascotIcon, gc);

        gc.gridx = 1; gc.weightx = 1.0; gc.fill = GridBagConstraints.HORIZONTAL;
        adviceLabel = new JLabel("");
        adviceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        adviceContent.add(adviceLabel, gc);

        adviceCard = createCardPanel("🦉 Archie's Insights", adviceContent);
        container.add(adviceCard);

        // Card 4: Actionable Next Steps
        JPanel nextStepsContent = new JPanel();
        nextStepsContent.setLayout(new BoxLayout(nextStepsContent, BoxLayout.Y_AXIS));
        nextActionPromptLabel = createMetricsLabel("");
        nextActionPromptLabel.setBorder(new EmptyBorder(4, 0, 10, 0));

        actionButton = new JButton("START RECOMMENDATION");
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        actionButton.setBackground(AppTheme.getTextDark());
        actionButton.setForeground(AppTheme.getBgCard());
        actionButton.setFocusPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(10, 16, 10, 16)));

        nextStepsContent.add(nextActionPromptLabel);
        nextStepsContent.add(actionButton);

        nextStepsCard = createCardPanel("🎯 Suggested Path", nextStepsContent);
        container.add(nextStepsCard);

        return container;
    }

    private JPanel createCardPanel(String title, JPanel content) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AppTheme.getBorderClr());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(AppTheme.getBgCard());
        card.setLayout(new BorderLayout(0, 10)); // 10px vertical gap
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(AppTheme.getAccentGold());
        card.add(titleLabel, BorderLayout.NORTH);

        content.setOpaque(false);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JLabel createMetricsLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(AppTheme.getTextDark());
        lbl.setBorder(new EmptyBorder(3, 0, 3, 0));
        return lbl;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 25, 10));

        guideBtn = makeSecondaryButton("❓ Guide");
        guideBtn.addActionListener(e -> nav.showHelp("results"));
        panel.add(guideBtn);

        reviewBtn = makeSecondaryButton("📋 Review Answers");
        reviewBtn.addActionListener(e -> nav.showReview(null));
        panel.add(reviewBtn);

        printBtn = makeSecondaryButton("📄 Export Report");
        printBtn.addActionListener(e -> handleExportReport());
        panel.add(printBtn);

        restartButton = makePrimaryButton("🔄  PLAY AGAIN");
        restartButton.addActionListener(e -> nav.goToWelcome());
        panel.add(restartButton);

        return panel;
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(AppTheme.getTextDark());
        btn.setForeground(AppTheme.getBgCard());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(10, 26, 10, 26)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(AppTheme.getBgCard());
        btn.setForeground(AppTheme.getTextMuted());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(10, 18, 10, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (dashboardTitleLabel != null) dashboardTitleLabel.setForeground(AppTheme.getTextDark());

        if (kpiCard != null) kpiCard.setBackground(AppTheme.getBgCard());
        if (highlightsCard != null) highlightsCard.setBackground(AppTheme.getBgCard());
        if (adviceCard != null) adviceCard.setBackground(AppTheme.getBgCard());
        if (nextStepsCard != null) nextStepsCard.setBackground(AppTheme.getBgCard());

        if (scoreLabel != null) scoreLabel.setForeground(AppTheme.getTextDark());
        if (percentLabel != null) percentLabel.setForeground(AppTheme.getTextDark());
        if (gradeLabel != null) gradeLabel.setForeground(AppTheme.getAccentGold());
        if (durationLabel != null) durationLabel.setForeground(AppTheme.getTextDark());
        if (speedLabel != null) speedLabel.setForeground(AppTheme.getTextDark());

        if (fastestQuestionLabel != null) fastestQuestionLabel.setForeground(AppTheme.getTextDark());
        if (hardestQuestionLabel != null) hardestQuestionLabel.setForeground(AppTheme.getTextDark());
        if (adviceLabel != null) adviceLabel.setForeground(AppTheme.getTextDark());
        if (nextActionPromptLabel != null) nextActionPromptLabel.setForeground(AppTheme.getTextDark());

        if (actionButton != null) {
            actionButton.setBackground(AppTheme.getTextDark());
            actionButton.setForeground(AppTheme.getBgCard());
        }

        if (restartButton != null) {
            restartButton.setBackground(AppTheme.getTextDark());
            restartButton.setForeground(AppTheme.getBgCard());
        }
        if (reviewBtn != null) {
            reviewBtn.setBackground(AppTheme.getBgCard());
            reviewBtn.setForeground(AppTheme.getTextMuted());
            reviewBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(10, 18, 10, 18)));
        }
        if (printBtn != null) {
            printBtn.setBackground(AppTheme.getBgCard());
            printBtn.setForeground(AppTheme.getTextMuted());
            printBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(10, 18, 10, 18)));
        }
        if (guideBtn != null) {
            guideBtn.setBackground(AppTheme.getBgCard());
            guideBtn.setForeground(AppTheme.getTextMuted());
            guideBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(10, 18, 10, 18)));
        }
    }

    private void handleExportReport() {
        if (activeSession == null) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(AppConfig.getAppDir() + java.io.File.separator + "math_report.html"));
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File dest = chooser.getSelectedFile();

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Atelier Arithmetic Dashboard Report</title>");
            html.append("<style>");
            html.append("body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #FAF9F6; color: #1C1917; padding: 40px; }");
            html.append(".card { background: white; border: 1px solid #E6E2DC; border-radius: 12px; padding: 30px; max-width: 700px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.02); }");
            html.append("h1 { font-family: Georgia, serif; color: #B8966E; margin-bottom: 5px; }");
            html.append(".subtitle { color: #78716C; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 25px; }");
            html.append(".badge { font-size: 48px; margin-right: 15px; }");
            html.append(".grade-box { display: flex; align-items: center; background: #FAF9F6; border: 1px solid #E6E2DC; border-radius: 8px; padding: 20px; margin-bottom: 25px; }");
            html.append(".grade-text { font-size: 24px; font-weight: bold; color: #1C1917; }");
            html.append(".grade-sub { font-size: 14px; color: #78716C; margin-top: 4px; }");
            html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            html.append("th, td { border-bottom: 1px solid #E6E2DC; padding: 12px; text-align: left; font-size: 13px; }");
            html.append("th { background-color: #FAF9F6; color: #78716C; font-weight: bold; }");
            html.append(".correct { background-color: #ECFDF5; color: #065F46; }");
            html.append(".incorrect { background-color: #FEF2F2; color: #991B1B; }");
            html.append("</style></head><body>");

            // Load Base64 logo
            String base64Logo = "";
            try (java.io.InputStream is = ResultsPanel.class.getResourceAsStream("/com/mathquiz/resources/logo.png")) {
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    base64Logo = java.util.Base64.getEncoder().encodeToString(bytes);
                }
            } catch (Exception ignored) {}

            html.append("<div class='card'>");
            if (!base64Logo.isEmpty()) {
                html.append("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>");
                html.append("<div>");
                html.append("<h1>Atelier Arithmetic Dashboard</h1>");
                html.append("<div class='subtitle'>Bespoke Performance Report</div>");
                html.append("</div>");
                html.append("<img src='data:image/png;base64,").append(base64Logo).append("' style='height: 60px; width: 60px; object-fit: contain; border-radius: 8px;' />");
                html.append("</div>");
            } else {
                html.append("<h1>Atelier Arithmetic Dashboard</h1>");
                html.append("<div class='subtitle'>Bespoke Performance Report</div>");
            }

            html.append("<div class='grade-box'>");
            html.append("<span class='badge'>").append(activeSession.getGradeEmoji()).append("</span>");
            html.append("<div>");
            html.append("<div class='grade-text'>Grade ").append(activeSession.getGrade()).append(" (").append((int)activeSession.getPercentage()).append("%)</div>");
            html.append("<div class='grade-sub'>Remarks: ").append(activeSession.getRemarks()).append("</div>");
            html.append("</div></div>");

            html.append("<p><b>Category:</b> ").append(activeSession.getCategory()).append("</p>");
            html.append("<p><b>Difficulty:</b> ").append(activeSession.getDifficulty()).append("</p>");
            html.append("<p><b>Total Questions:</b> ").append(activeSession.getTotalQuestions()).append("</p>");
            html.append("<p><b>Correct Answers:</b> ").append(activeSession.getCorrectAnswersCount()).append("</p>");
            html.append("<p><b>Total Duration:</b> ").append(activeSession.getDurationMs() / 1000).append(" seconds</p>");

            html.append("<h2>Session Details</h2>");
            html.append("<table><thead><tr><th>#</th><th>Expression</th><th>Your Answer</th><th>Correct Answer</th><th>Time</th></tr></thead><tbody>");

            java.util.List<QuestionResult> results = activeSession.getResults();
            for (int i = 0; i < results.size(); i++) {
                QuestionResult r = results.get(i);
                String clrClass = r.isCorrect() ? "correct" : "incorrect";
                html.append("<tr class='").append(clrClass).append("'>");
                html.append("<td>").append(i + 1).append("</td>");
                html.append("<td>").append(r.getExpression()).append("</td>");
                html.append("<td>").append(r.getUserAnswer()).append("</td>");
                html.append("<td>").append(r.getCorrectAnswer()).append("</td>");
                html.append("<td>").append(r.getTimeSpentMs() / 1000).append("s</td>");
                html.append("</tr>");
            }
            html.append("</tbody></table>");
            html.append("</div></body></html>");

            try (java.io.FileWriter writer = new java.io.FileWriter(dest)) {
                writer.write(html.toString());
                JOptionPane.showMessageDialog(this,
                        "Report exported successfully!\nSaved to: " + dest.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to write file!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            triggerNativePrint();
        }
    }

    private void triggerNativePrint() {
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setJobName("Atelier Arithmetic Report");

        job.setPrintable((g, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Serif", Font.BOLD, 22));
            g2d.drawString("Atelier Arithmetic Dashboard Report", 100, 100);

            g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2d.drawString("Category: " + activeSession.getCategory(), 100, 130);
            g2d.drawString("Difficulty: " + activeSession.getDifficulty(), 100, 150);
            g2d.drawString("Grade: " + activeSession.getGrade(), 100, 170);
            g2d.drawString("Success Rate: " + (int)activeSession.getPercentage() + "%", 100, 190);
            g2d.drawString("Correct Answers: " + activeSession.getCorrectAnswersCount() + " / " + activeSession.getTotalQuestions(), 100, 210);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("Question List Summary:", 100, 250);

            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            int y = 280;
            java.util.List<QuestionResult> results = activeSession.getResults();
            for (int i = 0; i < Math.min(results.size(), 20); i++) {
                QuestionResult r = results.get(i);
                String mark = r.isCorrect() ? "Correct" : "Wrong";
                g2d.drawString((i + 1) + ".  " + r.getExpression() + " = " + r.getUserAnswer() + "  (" + mark + " - Correct: " + r.getCorrectAnswer() + ")", 100, y);
                y += 20;
            }

            return java.awt.print.Printable.PAGE_EXISTS;
        });

        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
