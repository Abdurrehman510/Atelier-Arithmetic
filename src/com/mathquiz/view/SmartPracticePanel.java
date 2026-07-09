package com.mathquiz.view;

import com.mathquiz.service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.mathquiz.config.AppTheme;


/**
 * Smart Practice Mode Panel — Phase 2 Intelligence Layer.
 *
 * Checks history to find the child's weakest category and difficulty level.
 * Displays a personalized recommendation card with an action button to
 * immediately launch a targeted 10-question practice session.
 *
 * If no history exists, shows a friendly empty state encouraging the user
 * to play a few regular sessions first.
 */
public class SmartPracticePanel extends JPanel {

    // ── Design tokens ─────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY   = new Color(250, 249, 246);
    private static final Color BG_CARD      = Color.WHITE;
    private static final Color ACCENT_GOLD  = new Color(184, 150, 110);
    private static final Color TEXT_DARK    = new Color(28, 25, 23);
    private static final Color TEXT_MUTED   = new Color(120, 113, 108);
    private static final Color BORDER_CLR   = new Color(230, 227, 220);

    private final QuizNavigator nav;
    private final AnalyticsService analytics;

    // Components to update dynamically
    private JLabel bannerIconLabel;
    private JLabel promptLabel;
    private JLabel targetLabel;
    private JButton actionButton;

    // Themed components
    private JLabel subtitleLabel;
    private JLabel titleLabel;
    private JButton backButton;
    private JPanel practiceCardPanel;

    // Dynamic state
    private String recommendedCategory;
    private String recommendedDifficulty;
    private boolean hasHistory;


    public SmartPracticePanel(QuizNavigator nav, AnalyticsService analytics) {
        this.nav = nav;
        this.analytics = analytics;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    public JPanel getPracticeCardPanel() { return practiceCardPanel; }
    public JButton getActionButton() { return actionButton; }

    /** Refresh recommendations based on updated history before showing. */
    public void refresh() {
        int sessionCount = analytics.getTotalSessions();
        hasHistory = (sessionCount > 0);

        if (hasHistory) {
            recommendedCategory = analytics.getWeakestCategory();
            recommendedDifficulty = analytics.getWeakestDifficulty();

            bannerIconLabel.setText("🎯");
            promptLabel.setText("Based on your history, we found where you have room to grow!");
            targetLabel.setText(recommendedCategory + " (" + recommendedDifficulty + ")");
            actionButton.setText("START SMART PRACTICE  →");
        } else {
            recommendedCategory = "Mixed";
            recommendedDifficulty = "Easy";

            bannerIconLabel.setText("🌱");
            promptLabel.setText("Play a few regular games first so Archie can analyze your strengths!");
            targetLabel.setText("No history data yet");
            actionButton.setText("PLAY A REGULAR GAME  →");
        }
    }

    private void build() {
        add(buildHeader(), BorderLayout.NORTH);
        add(buildPracticeCard(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(35, 40, 10, 40));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        subtitleLabel = new JLabel("SMART PRACTICE MODE");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitleLabel.setForeground(ACCENT_GOLD);
        left.add(subtitleLabel);

        titleLabel = new JLabel("Personalized Training");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 26));
        titleLabel.setForeground(TEXT_DARK);
        left.add(titleLabel);

        panel.add(left, BorderLayout.WEST);

        backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backButton.setBackground(BG_CARD);
        backButton.setForeground(TEXT_MUTED);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(8, 18, 8, 18)));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> nav.goToWelcome());
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }


    private JPanel buildPracticeCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        practiceCardPanel = new JPanel(new GridBagLayout());
        practiceCardPanel.setBackground(BG_CARD);
        practiceCardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(40, 60, 40, 60)));


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(12, 10, 12, 10);
        c.gridx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;

        // Big Icon
        c.gridy = 0;
        bannerIconLabel = new JLabel("🎯", SwingConstants.CENTER);
        bannerIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        practiceCardPanel.add(bannerIconLabel, c);

        // Subtext / Description
        c.gridy = 1;
        promptLabel = new JLabel("Analyzing your strengths...", SwingConstants.CENTER);
        promptLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        promptLabel.setForeground(TEXT_MUTED);
        practiceCardPanel.add(promptLabel, c);

        // Highlight Target
        c.gridy = 2;
        targetLabel = new JLabel("Category (Difficulty)", SwingConstants.CENTER);
        targetLabel.setFont(new Font("Serif", Font.BOLD, 28));
        targetLabel.setForeground(TEXT_DARK);
        practiceCardPanel.add(targetLabel, c);

        // Separator / Info
        c.gridy = 3;
        JLabel descLabel = new JLabel("This practice session generates 10 tailored questions.", SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        descLabel.setForeground(TEXT_MUTED);
        practiceCardPanel.add(descLabel, c);

        // Action Button
        c.gridy = 4;
        c.insets = new Insets(24, 10, 10, 10);
        actionButton = new JButton("START SMART PRACTICE  →");
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        actionButton.setBackground(TEXT_DARK);
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setBorder(new EmptyBorder(12, 36, 12, 36));
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.addActionListener(e -> handleAction());
        practiceCardPanel.add(actionButton, c);

        outer.add(practiceCardPanel);
        return outer;

    }

    private void handleAction() {
        if (hasHistory) {
            // Start targeted session immediately with 10 questions
            nav.startQuiz(recommendedCategory, 10, recommendedDifficulty);
        } else {
            // Guide user to regular category selector
            nav.goToCategories();
        }
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (subtitleLabel != null) subtitleLabel.setForeground(AppTheme.getAccentGold());
        if (titleLabel != null) titleLabel.setForeground(AppTheme.getTextDark());
        if (backButton != null) {
            backButton.setBackground(AppTheme.getBgCard());
            backButton.setForeground(AppTheme.getTextMuted());
            backButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(8, 18, 8, 18)));
        }
        if (practiceCardPanel != null) {
            practiceCardPanel.setBackground(AppTheme.getBgCard());
            practiceCardPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(40, 60, 40, 60)));
        }
        if (promptLabel != null) promptLabel.setForeground(AppTheme.getTextMuted());
        if (targetLabel != null) targetLabel.setForeground(AppTheme.getTextDark());
        if (actionButton != null) {
            actionButton.setBackground(AppTheme.getTextDark());
            actionButton.setForeground(AppTheme.getBgCard());
        }
    }
}

