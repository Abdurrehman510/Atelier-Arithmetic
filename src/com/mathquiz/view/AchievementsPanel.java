package com.mathquiz.view;

import com.mathquiz.config.AppTheme;
import com.mathquiz.service.AchievementService;
import com.mathquiz.service.AchievementService.Achievement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Achievements Badge Grid Screen — Phase 3 Gamification.
 *
 * Renders 10 milestones in a premium 2x5 visual grid.
 * Unlocked items glow with gold borders and color, while locked items
 * are slightly translucent and greyed out to highlight the goal.
 */
public class AchievementsPanel extends JPanel {

    private final QuizNavigator navigator;
    private final AchievementService achievementService;

    // UI elements to recolor on theme updates
    private JPanel gridPanel;
    private JLabel subTitle;
    private JLabel titleLabel;
    private JButton backButton;
    private JScrollPane scrollPane;

    public AchievementsPanel(QuizNavigator navigator, AchievementService achievementService) {
        this.navigator = navigator;
        this.achievementService = achievementService;
        setLayout(new BorderLayout());
        build();
        applyTheme();
    }

    public JPanel getGridPanel() { return gridPanel; }

    /** Refresh achievements state and repaint grid. */
    public void refresh() {
        gridPanel.removeAll();
        List<Achievement> achievements = achievementService.calculateAchievements();
        for (Achievement ach : achievements) {
            gridPanel.add(buildBadgeCard(ach));
        }
        applyTheme();
        revalidate();
        repaint();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(35, 40, 16, 40));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        subTitle = new JLabel("ACHIEVEMENTS");
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        left.add(subTitle);

        titleLabel = new JLabel("Milestones & Badges");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 26));
        left.add(titleLabel);
        header.add(left, BorderLayout.WEST);

        backButton = new JButton("← Back");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> navigator.goToWelcome());
        header.add(backButton, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Badge Grid
        gridPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        gridPanel.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 40, 40, 40));
        wrapper.add(gridPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildBadgeCard(Achievement ach) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Premium glassmorphism outline
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (ach.unlocked) {
                    g2.setColor(AppTheme.getAccentGold());
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
            }
        };
        card.setBackground(AppTheme.getBgCard());
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        card.setPreferredSize(new Dimension(320, 85));

        // Big Badge Icon
        JLabel badgeIcon = new JLabel(ach.emoji, SwingConstants.CENTER);
        badgeIcon.setFont(new Font("SansSerif", Font.PLAIN, 28));

        // Text Box
        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(ach.name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(AppTheme.getTextDark());

        JLabel descLabel = new JLabel("<html><body style='width: 180px;'>" + ach.description + "</body></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(AppTheme.getTextMuted());

        textBox.add(nameLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(descLabel);

        card.add(badgeIcon, BorderLayout.WEST);
        card.add(textBox, BorderLayout.CENTER);

        // Visual lock/unlock styling
        if (ach.unlocked) {
            card.setOpaque(true);
            badgeIcon.setEnabled(true);
            nameLabel.setEnabled(true);
            descLabel.setEnabled(true);
        } else {
            card.setOpaque(true);
            // Give a muted feel
            badgeIcon.setText("🔒");
            nameLabel.setForeground(AppTheme.getTextMuted());
            descLabel.setForeground(AppTheme.getTextMuted());
        }

        return card;
    }

    /** Reapply styling based on AppTheme current status. */
    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        subTitle.setForeground(AppTheme.getAccentGold());
        titleLabel.setForeground(AppTheme.getTextDark());

        backButton.setBackground(AppTheme.getBgCard());
        backButton.setForeground(AppTheme.getTextMuted());
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(8, 18, 8, 18)));

        // Recolor existing card panels recursively if they exist
        for (Component c : gridPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel card = (JPanel) c;
                card.setBackground(AppTheme.getBgCard());
                // Recolor texts within
                for (Component tc : card.getComponents()) {
                    if (tc instanceof JPanel) {
                        for (Component t : ((JPanel) tc).getComponents()) {
                            if (t instanceof JLabel) {
                                JLabel label = (JLabel) t;
                                // Restore original logic color based on current theme state
                                if (label.getText() != null && label.getText().startsWith("<html>")) {
                                    label.setForeground(AppTheme.getTextMuted());
                                } else {
                                    // Title text
                                    label.setForeground(AppTheme.getTextDark());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
