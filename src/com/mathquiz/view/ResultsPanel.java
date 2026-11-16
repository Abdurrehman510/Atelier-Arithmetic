package com.mathquiz.view;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.config.AppTheme;
import com.mathquiz.config.AppConfig;
import com.mathquiz.view.tour.TourStep;

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
    private ArchieView archie;   // wearing the child's shop item; celebrates a high score (BUG-22)
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
    private com.mathquiz.service.NextStepAdvisor.NextStep nextStep;   // what actionButton does now (BUG-02)

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
        double pct = session.getPercentage();
        archie.setExpression(pct >= 80 ? TourStep.MascotExpression.EXCITED
                : pct >= 50 ? TourStep.MascotExpression.HAPPY
                : TourStep.MascotExpression.THINKING);
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
        nextStep = com.mathquiz.service.NextStepAdvisor.recommend(session, nav.isDailyChallengeAvailable());
        nextActionPromptLabel.setText(nextStep.prompt());
        actionButton.setText(nextStep.buttonLabel());
    }

    private void followNextStep() {
        if (nextStep == null) {
            return;
        }
        switch (nextStep.kind()) {
            case STEP_UP:
                nav.goToWelcome(nextStep.targetDifficulty());
                break;
            case DAILY_CHALLENGE:
                nav.startDailyChallenge();
                break;
            case SMART_PRACTICE:
            default:
                nav.startSmartPractice();
                break;
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
        header.setBorder(new EmptyBorder(16, 40, 5, 40));   // 25 → 16: Archie is ~9 px taller than the titles

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

        // Archie, wearing the child's shop item, reacts to the score (the tour promises he celebrates);
        // a little smaller than elsewhere, and the top padding is trimmed, so the header (and the cards
        // below it) keep their old height
        archie = new ArchieView(TourStep.MascotExpression.HAPPY);
        archie.setFont(new Font("SansSerif", Font.PLAIN, 11));
        header.add(archie, BorderLayout.EAST);
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
        PrimaryButtonStyle.apply(actionButton);
        actionButton.setFocusPainted(false);
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(10, 16, 10, 16)));
        // One listener for the panel's lifetime; it follows the latest recommendation (BUG-02)
        actionButton.addActionListener(e -> followNextStep());

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
        PrimaryButtonStyle.apply(btn);
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
            PrimaryButtonStyle.apply(actionButton);
        }

        if (restartButton != null) {
            PrimaryButtonStyle.apply(restartButton);
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

            // Archie in the report header (optional decoration; the report is complete without it)
            String html = com.mathquiz.service.ReportBuilder.toHtml(activeSession, BrandAssets.archieBase64());

            if (!com.mathquiz.service.ReportBuilder.writeReport(dest, html)) {   // also catches invalid file names
                JOptionPane.showMessageDialog(this, "Failed to write file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // BUG-12: printing is a separate choice, never automatic
            int choice = JOptionPane.showConfirmDialog(this,
                    "Report exported successfully!\nSaved to: " + dest.getAbsolutePath()
                            + "\n\nWould you like to print it too?",
                    "Report saved 🦉", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                triggerNativePrint();
            }
        }
    }

    private void triggerNativePrint() {
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setJobName("Atelier Arithmetic Report");

        job.setPrintable(new ReportPrintable(activeSession));   // every question, paginated (BUG-12)

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
