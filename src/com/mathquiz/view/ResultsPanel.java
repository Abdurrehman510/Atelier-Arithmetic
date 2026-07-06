package com.mathquiz.view;

import com.mathquiz.model.QuizSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Post-quiz results screen.
 * Displays score, percentage, grade, and the custom SmilePanel graphic.
 * Exposes grade label and restart button for TourManager spotlight targeting.
 */
public class ResultsPanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY  = new Color(250, 249, 246);
    private static final Color BG_CARD     = Color.WHITE;
    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color BORDER_CLR  = new Color(230, 227, 220);

    // ── Dynamic labels ────────────────────────────────────────────────────────
    private JLabel     scoreLabel;
    private JLabel     percentLabel;
    private JLabel     gradeLabel;
    private JLabel     remarksLabel;
    private SmilePanel smilePanel;

    // ── Tour-targetable ───────────────────────────────────────────────────────
    private JButton restartButton;

    private final QuizNavigator nav;

    public ResultsPanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    // ── Public accessors for TourManager ─────────────────────────────────────

    public JLabel  getGradeLabel()    { return gradeLabel;    }
    public JButton getRestartButton() { return restartButton; }

    // ── Public update ─────────────────────────────────────────────────────────

    /** Populate all labels and the smile graphic from the completed session. */
    public void populate(QuizSession session) {
        scoreLabel.setText("Correct Answers: "
                + session.getCorrectAnswersCount() + " / " + session.getTotalQuestions());
        percentLabel.setText(String.format("Success Rate: %.1f%%", session.getPercentage()));
        gradeLabel.setText(session.getGradeEmoji() + "  Grade: " + session.getGrade());
        remarksLabel.setText(session.getRemarks());
        smilePanel.setGrade(session.getGrade(), session.getGradeEmoji());
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(28, 20, 8, 20));
        JLabel title = new JLabel("PERFORMANCE REPORT");
        title.setFont(new Font("Serif", Font.PLAIN, 28));
        title.setForeground(TEXT_DARK);
        p.add(title);
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(10, 40, 20, 40));

        // ── Left: stats card ──────────────────────────────────────────────────
        JPanel infoCard = new JPanel(new GridBagLayout());
        infoCard.setBackground(BG_CARD);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(30, 28, 30, 28)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(8, 4, 8, 4);
        c.gridx  = 0;

        scoreLabel = new JLabel("Correct Answers: — / —");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        scoreLabel.setForeground(TEXT_DARK);
        c.gridy = 0; infoCard.add(scoreLabel, c);

        percentLabel = new JLabel("Success Rate: —");
        percentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        percentLabel.setForeground(TEXT_MUTED);
        c.gridy = 1; infoCard.add(percentLabel, c);

        gradeLabel = new JLabel("— Grade: —");
        gradeLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        gradeLabel.setForeground(ACCENT_GOLD);
        c.gridy = 2; infoCard.add(gradeLabel, c);

        remarksLabel = new JLabel("—");
        remarksLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        remarksLabel.setForeground(TEXT_DARK);
        c.gridy = 3; infoCard.add(remarksLabel, c);

        body.add(infoCard);

        // ── Right: custom graphic ─────────────────────────────────────────────
        smilePanel = new SmilePanel();
        smilePanel.setBackground(BG_CARD);
        smilePanel.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        body.add(smilePanel);

        return body;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 36, 10));

        JButton helpBtn = makeSecondaryButton("❓ Guide");
        helpBtn.addActionListener(e -> nav.showHelp("results"));
        panel.add(helpBtn);

        JButton reviewBtn = makeSecondaryButton("📋 Review Answers");
        reviewBtn.addActionListener(e -> {
            // QuizFrame will supply the session reference via showReview()
            // We fire a dedicated action; QuizFrame stores the last session
            nav.showReview(null);  // null signals "current session" to QuizFrame
        });
        panel.add(reviewBtn);

        restartButton = makePrimaryButton("🔄  PLAY AGAIN");
        restartButton.addActionListener(e -> nav.goToWelcome());
        panel.add(restartButton);

        return panel;
    }

    // ── Widget helpers ────────────────────────────────────────────────────────

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(TEXT_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(11, 32, 11, 32));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(BG_PRIMARY);
        btn.setForeground(TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
