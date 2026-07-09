package com.mathquiz.view;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.mathquiz.config.AppTheme;


/**
 * Post-quiz answer review screen.
 *
 * Shows every question from the session in a scrollable table with columns:
 *   # | Question | Your Answer | Correct Answer | Result | Time
 *
 * Correct rows are tinted green; incorrect rows are tinted red — giving a
 * clear visual breakdown of where the child performed well or needs practice.
 */
public class ReviewPanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY    = new Color(250, 249, 246);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color TEXT_DARK     = new Color(28, 25, 23);
    private static final Color TEXT_MUTED    = new Color(120, 113, 108);
    private static final Color BORDER_CLR    = new Color(230, 227, 220);
    private static final Color ROW_CORRECT   = new Color(220, 252, 231);   // light green
    private static final Color ROW_WRONG     = new Color(254, 226, 226);   // light red
    private static final Color ROW_CORRECT_D = new Color(187, 247, 208);   // darker alternate
    private static final Color ROW_WRONG_D   = new Color(252, 202, 202);

    private static final String[] COLUMNS = {
            "#", "Expression", "Your Answer", "Correct Answer", "Result", "Time"
    };

    private final QuizNavigator  nav;
    private       DefaultTableModel tableModel;
    private       JTable         table;
    private       String         returnScreen = "results";


    public ReviewPanel(QuizNavigator nav) {
        this.nav = nav;
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    public JTable getTable() { return table; }

    // ── Public API ────────────────────────────────────────────────────────────


    /** Populate the table from a completed session and record where to return. */
    public void populate(QuizSession session, String returnScreen) {
        this.returnScreen = returnScreen;
        tableModel.setRowCount(0);   // clear previous

        List<QuestionResult> results = session.getResults();
        for (int i = 0; i < results.size(); i++) {
            QuestionResult r = results.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    r.getExpression() + " = ?",
                    r.getUserAnswer(),
                    r.getCorrectAnswer(),
                    r.isCorrect() ? "✅ Correct" : "❌ Wrong",
                    r.getTimeFormatted()
            });
        }
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTable(),     BorderLayout.CENTER);
        add(buildFooter(),    BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(28, 20, 8, 20));

        JLabel title = new JLabel("ANSWER REVIEW");
        title.setFont(new Font("Serif", Font.PLAIN, 28));
        title.setForeground(TEXT_DARK);
        p.add(title);

        JLabel sub = new JLabel("  — Every question, your answer, and the correct answer");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        p.add(sub);

        return p;
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        table.setRowHeight(34);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 244, 240));
        table.getTableHeader().setForeground(TEXT_MUTED);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setSelectionBackground(new Color(230, 227, 220));

        // Column widths
        int[] widths = { 30, 230, 100, 120, 90, 60 };
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Row colour renderer (green correct / red wrong)
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, selected, focus, row, col);
                if (!selected) {
                    String result = (String) t.getValueAt(row, 4);
                    boolean correct = result != null && result.startsWith("✅");
                    boolean alt = (row % 2 == 0);
                    c.setBackground(getRowBg(correct, alt));
                    c.setForeground(AppTheme.getTextDark());
                }
                return c;
            }
        };
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        // Left-align the expression column
        DefaultTableCellRenderer leftAlign = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, selected, focus, row, col);
                if (!selected) {
                    String result = (String) t.getValueAt(row, 4);
                    boolean correct = result != null && result.startsWith("✅");
                    boolean alt = (row % 2 == 0);
                    c.setBackground(getRowBg(correct, alt));
                    c.setForeground(AppTheme.getTextDark());
                }
                return c;
            }
        };
        leftAlign.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(leftAlign);


        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(6, 30, 6, 30),
                BorderFactory.createLineBorder(BORDER_CLR, 1)));
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_CARD);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 34, 10));

        // Back to results
        JButton backBtn = new JButton("← Back to Results");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        backBtn.setBackground(new Color(28, 25, 23));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new EmptyBorder(10, 28, 10, 28));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> nav.finishQuiz(null));   // null = navigate to results
        panel.add(backBtn);

        // Play again from home
        JButton homeBtn = new JButton("🏠 Play Again");
        homeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        homeBtn.setBackground(BG_PRIMARY);
        homeBtn.setForeground(TEXT_MUTED);
        homeBtn.setFocusPainted(false);
        homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> nav.goToWelcome());
        panel.add(homeBtn);

        return panel;
    }
    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        recolorTree(this);
    }

    private void recolorTree(Container parent) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                p.setBackground(AppTheme.getBgPrimary());
                recolorTree(p);
            } else if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() > 20) {
                    lbl.setForeground(AppTheme.getTextDark());
                } else {
                    lbl.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (btn.getText().contains("Back")) {
                    btn.setBackground(AppTheme.getTextDark());
                    btn.setForeground(AppTheme.getBgCard());
                } else {
                    btn.setBackground(AppTheme.getBgPrimary());
                    btn.setForeground(AppTheme.getTextMuted());
                }
            } else if (c instanceof JScrollPane) {
                JScrollPane s = (JScrollPane) c;
                s.getViewport().setBackground(AppTheme.getBgCard());
                s.setBorder(BorderFactory.createCompoundBorder(
                        new EmptyBorder(6, 30, 6, 30),
                        BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1)));
                Component view = s.getViewport().getView();
                if (view instanceof JTable) {
                    JTable t = (JTable) view;
                    t.setBackground(AppTheme.getBgCard());
                    t.setForeground(AppTheme.getTextDark());
                    t.setGridColor(AppTheme.getBorderClr());
                    t.getTableHeader().setBackground(AppTheme.getBgCard());
                    t.getTableHeader().setForeground(AppTheme.getTextDark());
                }
            }
        }
    }

    private Color getRowBg(boolean correct, boolean alt) {
        if (AppTheme.isDarkMode()) {
            if (correct) {
                return alt ? new Color(20, 50, 32) : new Color(25, 60, 38);
            } else {
                return alt ? new Color(64, 24, 24) : new Color(74, 30, 30);
            }
        } else {
            if (correct) {
                return alt ? ROW_CORRECT : ROW_CORRECT_D;
            } else {
                return alt ? ROW_WRONG : ROW_WRONG_D;
            }
        }
    }
}


