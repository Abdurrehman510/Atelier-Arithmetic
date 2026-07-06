package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Welcome / Configuration screen.
 * Lets the child set the number of questions and difficulty before choosing a category.
 * Exposes named component accessors so TourManager can target them for spotlighting.
 */
public class WelcomePanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY  = new Color(250, 249, 246);
    private static final Color BG_CARD     = Color.WHITE;
    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color BORDER_CLR  = new Color(230, 227, 220);

    // ── Tour-targetable components ────────────────────────────────────────────
    private JTextField       qCountField;
    private JComboBox<String> diffCombo;
    private JButton          startButton;

    private final QuizNavigator nav;

    public WelcomePanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    // ── Public accessors for TourManager ─────────────────────────────────────

    public JTextField        getQuestionCountField() { return qCountField; }
    public JComboBox<String> getDifficultyCombo()    { return diffCombo;   }
    public JButton           getStartButton()        { return startButton; }

    /** Returns the current question count, defaulting to 10 on parse error. */
    public int getQuestionCount() {
        try {
            int v = Integer.parseInt(qCountField.getText().trim());
            return (v > 0) ? v : 10;
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    /** Returns the selected difficulty string. */
    public String getDifficulty() {
        return (String) diffCombo.getSelectedItem();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildBrandHeader(), BorderLayout.NORTH);
        add(buildConfigCard(),  BorderLayout.CENTER);
        add(buildFooter(),      BorderLayout.SOUTH);
    }

    private JPanel buildBrandHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(45, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Sub-brand line
        gbc.gridy = 0;
        JLabel subBrand = new JLabel("ATELIER ARITHMETIC");
        subBrand.setFont(new Font("Serif", Font.PLAIN, 11));
        subBrand.setForeground(ACCENT_GOLD);
        panel.add(subBrand, gbc);

        // Main title
        gbc.gridy = 1;
        JLabel title = new JLabel("Royal Mathematics Quiz");
        title.setFont(new Font("Serif", Font.PLAIN, 34));
        title.setForeground(TEXT_DARK);
        panel.add(title, gbc);

        // Tagline
        gbc.gridy = 2;
        JLabel tag = new JLabel("Test your arithmetic skills with bespoke challenges!");
        tag.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tag.setForeground(TEXT_MUTED);
        panel.add(tag, gbc);

        return panel;
    }

    private JPanel buildConfigCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        // ── Row 0: Question count ─────────────────────────────────────────────
        c.gridx = 0; c.gridy = 0;
        card.add(makeLabel("NUMBER OF QUESTIONS:"), c);

        c.gridx = 1;
        qCountField = new JTextField("10", 8);
        styleTextField(qCountField);
        card.add(qCountField, c);

        // ── Row 1: Difficulty ─────────────────────────────────────────────────
        c.gridx = 0; c.gridy = 1;
        card.add(makeLabel("DIFFICULTY LEVEL:"), c);

        c.gridx = 1;
        diffCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        diffCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(diffCombo, c);

        outer.add(card);
        return outer;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 45, 10));

        // Help button ❓
        JButton helpBtn = makeIconButton("❓ Guide");
        helpBtn.addActionListener(e -> nav.showHelp("welcome"));
        panel.add(helpBtn);

        // Tour replay button 🦉
        JButton tourBtn = makeIconButton("🦉 Tour");
        tourBtn.addActionListener(e -> nav.launchTour());
        panel.add(tourBtn);

        // Main action
        startButton = makePrimaryButton("CHOOSE CATEGORY  →");
        startButton.addActionListener(e -> handleStart());
        panel.add(startButton);

        return panel;
    }

    private void handleStart() {
        String raw = qCountField.getText().trim();
        int qty;
        try {
            qty = Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a number for how many questions you want!",
                    "Oops!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please enter at least 1 question!",
                    "Oops!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        nav.goToCategories();
    }

    // ── Widget helpers ────────────────────────────────────────────────────────

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 195, 185), 1),
                new EmptyBorder(5, 10, 5, 10)));
    }

    private JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(TEXT_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 36, 12, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeIconButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(BG_CARD);
        btn.setForeground(TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(10, 18, 10, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
