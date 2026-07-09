package com.mathquiz.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import com.mathquiz.config.AppTheme;



/**
 * Category selection screen.
 * Shows six discipline cards; clicking one starts the quiz.
 * The entire grid panel is exposed so TourManager can spotlight it.
 */
public class CategoryPanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY  = new Color(250, 249, 246);
    private static final Color BG_CARD     = Color.WHITE;
    private static final Color BG_HOVER    = new Color(253, 251, 245);
    private static final Color ACCENT_GOLD = new Color(184, 150, 110);
    private static final Color TEXT_DARK   = new Color(28, 25, 23);
    private static final Color TEXT_MUTED  = new Color(120, 113, 108);
    private static final Color BORDER_CLR  = new Color(230, 227, 220);
    private static final Color BORDER_HOV  = new Color(184, 150, 110);

    private static final String[] CATEGORIES = {
            "Addition", "Difference", "Multiplication", "Division", "Mixed", "Special",
            "Fractions", "Patterns", "Algebra", "Measurement"
    };
    private static final String[] DESCRIPTIONS = {
            "Add numbers together and find the sum!",
            "Subtract and find the difference!",
            "Multiply numbers to get the product!",
            "Divide numbers perfectly — no remainders!",
            "A mix of all operations — ultimate challenge!",
            "Tricky compound expressions for math champs!",
            "Solve fractional proportions & percents!",
            "Find the missing numbers in sequences!",
            "Solve algebraic equations for X!",
            "Convert metric units & estimate areas!"
    };
    private static final String[] ICONS = { "➕", "➖", "✖️", "➗", "🔀", "⭐", "📊", "📈", "⚖️", "📏" };


    // ── Tour-targetable ───────────────────────────────────────────────────────
    private JPanel categoryGrid;

    private final QuizNavigator nav;
    private       int           questionCount;
    private       String        difficulty;

    public CategoryPanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
        setupKeyBindings();
    }


    // ── Public API ────────────────────────────────────────────────────────────

    /** Called by QuizFrame before showing this panel so session params are current. */
    public void configure(int questionCount, String difficulty) {
        this.questionCount = questionCount;
        this.difficulty    = difficulty;
    }

    public JPanel getCategoryGrid() { return categoryGrid; }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildGrid(),    BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(35, 20, 15, 20));

        JLabel title = new JLabel("Choose Your Arithmetic Discipline");
        title.setFont(new Font("Serif", Font.PLAIN, 27));
        title.setForeground(TEXT_DARK);
        p.add(title);
        return p;
    }

    private JPanel buildGrid() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        categoryGrid = new JPanel(new GridLayout(2, 5, 12, 12));
        categoryGrid.setOpaque(false);
        categoryGrid.setBorder(new EmptyBorder(10, 40, 30, 40));

        for (int i = 0; i < CATEGORIES.length; i++) {
            categoryGrid.add(buildCategoryCard(i));
        }

        outer.add(categoryGrid);
        return outer;
    }

    private JPanel buildCategoryCard(int idx) {
        final String cat = CATEGORIES[idx];

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(18, 16, 18, 16)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setFocusable(true);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        // Icon
        gbc.gridy = 0;
        JLabel iconLbl = new JLabel(ICONS[idx]);
        iconLbl.setFont(new Font("SansSerif", Font.PLAIN, 26));
        card.add(iconLbl, gbc);

        // Name
        gbc.gridy = 1;
        JLabel nameLbl = new JLabel(cat.toUpperCase());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLbl.setForeground(ACCENT_GOLD);
        card.add(nameLbl, gbc);

        // Description
        gbc.gridy = 2;
        JLabel descLbl = new JLabel(
                "<html><center>" + DESCRIPTIONS[idx] + "</center></html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_MUTED);
        card.add(descLbl, gbc);

        // Select button
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 0, 0);
        JButton btn = new JButton("START");
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(TEXT_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setBorder(new EmptyBorder(6, 22, 6, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> nav.startQuiz(cat, questionCount, difficulty));
        card.add(btn, gbc);


        // Hover highlight on the card border
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getAccentGold(), 2),
                        new EmptyBorder(17, 15, 17, 15)));
                card.setBackground(AppTheme.isDarkMode() ? new Color(48, 48, 54) : new Color(253, 251, 245));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                        new EmptyBorder(18, 16, 18, 16)));
                card.setBackground(AppTheme.getBgCard());
            }
        });

        // Focus highlights and keyboard navigation
        card.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getAccentGold(), 2),
                        new EmptyBorder(17, 15, 17, 15)));
                card.setBackground(AppTheme.isDarkMode() ? new Color(48, 48, 54) : new Color(253, 251, 245));
            }

            @Override
            public void focusLost(FocusEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                        new EmptyBorder(18, 16, 18, 16)));
                card.setBackground(AppTheme.getBgCard());
            }
        });

        card.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                int nextIdx = idx;
                int numCats = CATEGORIES.length;
                if (code == KeyEvent.VK_LEFT) {
                    nextIdx = (idx - 1 + numCats) % numCats;
                } else if (code == KeyEvent.VK_RIGHT) {
                    nextIdx = (idx + 1) % numCats;
                } else if (code == KeyEvent.VK_UP) {
                    nextIdx = (idx - 5 + numCats) % numCats;
                } else if (code == KeyEvent.VK_DOWN) {
                    nextIdx = (idx + 5) % numCats;
                } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
                    nav.startQuiz(cat, questionCount, difficulty);
                    return;
                } else if (code == KeyEvent.VK_ESCAPE) {
                    nav.goToWelcome();
                    return;
                }

                if (nextIdx != idx) {
                    Component[] cards = categoryGrid.getComponents();
                    if (nextIdx >= 0 && nextIdx < cards.length) {
                        cards[nextIdx].requestFocusInWindow();
                    }
                }
            }
        });



        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 10, 28, 10));

        JButton helpBtn = makeSecondaryButton("❓ Guide");
        helpBtn.addActionListener(e -> nav.showHelp("categories"));
        panel.add(helpBtn);

        JButton backBtn = makeSecondaryButton("← Back");
        backBtn.addActionListener(e -> nav.goToWelcome());
        panel.add(backBtn);

        return panel;
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
    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (categoryGrid != null) {
            for (Component c : categoryGrid.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel card = (JPanel) c;
                    card.setBackground(AppTheme.getBgCard());
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                            new EmptyBorder(18, 16, 18, 16)));

                    for (Component inner : card.getComponents()) {
                        if (inner instanceof JLabel) {
                            JLabel lbl = (JLabel) inner;
                            if (lbl.getFont().getSize() > 14) {
                                lbl.setForeground(AppTheme.getTextDark());
                            } else {
                                lbl.setForeground(AppTheme.getTextMuted());
                            }
                        } else if (inner instanceof JButton) {
                            JButton btn = (JButton) inner;
                            btn.setBackground(AppTheme.getTextDark());
                            btn.setForeground(AppTheme.getBgCard());
                        }
                    }
                }
            }
        }
        recolorTree(this);
    }

    private void recolorTree(Container parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel && c != categoryGrid && c != this && !(c.getParent() == categoryGrid)) {
                c.setBackground(AppTheme.getBgPrimary());
                recolorTree((Container) c);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getParent() != categoryGrid && !(lbl.getParent().getParent() == categoryGrid)) {
                    lbl.setForeground(AppTheme.getTextDark());
                }
            } else if (c instanceof JButton && !(c.getParent().getParent() == categoryGrid)) {
                JButton btn = (JButton) c;
                btn.setBackground(AppTheme.getBgPrimary());
                btn.setForeground(AppTheme.getTextMuted());
            }
        }
    }

    public void focusFirstCard() {
        SwingUtilities.invokeLater(() -> {
            if (categoryGrid != null && categoryGrid.getComponentCount() > 0) {
                categoryGrid.getComponent(0).requestFocusInWindow();
            }
        });
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapeKey");
        am.put("escapeKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nav.goToWelcome();
            }
        });
    }
}


