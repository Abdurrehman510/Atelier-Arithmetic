package com.mathquiz.view;

import com.mathquiz.config.AppTheme;
import com.mathquiz.model.Question;
import com.mathquiz.service.CustomQuizService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen that allows parents/teachers to build custom quiz question sets.
 * Features a table view of current questions and simple add/remove controls.
 */
public class QuizBuilderPanel extends JPanel {

    private final QuizNavigator navigator;
    private final CustomQuizService quizService;
    private final List<Question> tempQuestions = new ArrayList<>();

    // UI elements
    private JTextField nameField;
    private JTextField exprField;
    private JTextField answerField;
    private JTable questionTable;
    private DefaultTableModel tableModel;

    // Themed components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel nameLabel;
    private JLabel exprLabel;
    private JLabel answerLabel;
    private JButton addBtn;
    private JButton removeBtn;
    private JButton saveBtn;
    private JButton backBtn;
    private JScrollPane scrollPane;
    private JPanel formCard;

    public QuizBuilderPanel(QuizNavigator navigator) {
        this.navigator = navigator;
        this.quizService = new CustomQuizService();
        setLayout(new BorderLayout());
        build();
        applyTheme();
    }

    public JPanel getFormCard() { return formCard; }
    public JButton getSaveBtn() { return saveBtn; }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(35, 40, 10, 40));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        subtitleLabel = new JLabel("TEACHER & PARENT ZONE");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitleLabel.setForeground(AppTheme.getAccentGold());
        left.add(subtitleLabel);

        titleLabel = new JLabel("Custom Quiz Builder");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 26));
        titleLabel.setForeground(AppTheme.getTextDark());
        left.add(titleLabel);
        header.add(left, BorderLayout.WEST);

        backBtn = new JButton("← Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> navigator.goToWelcome());
        header.add(backBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center Content Area: Split pane or GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 16);

        // Left Side: Form Controls
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.45;
        formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(AppTheme.getBgCard());
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(20, 25, 20, 25)));

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets = new Insets(6, 0, 6, 0);

        // Quiz Name
        fc.gridy = 0;
        nameLabel = new JLabel("QUIZ NAME:");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        nameLabel.setForeground(AppTheme.getTextMuted());
        formCard.add(nameLabel, fc);

        fc.gridy = 1;
        nameField = new JTextField("My Custom Quiz");
        styleTextField(nameField);
        formCard.add(nameField, fc);

        // Separator
        fc.gridy = 2;
        formCard.add(Box.createVerticalStrut(10), fc);

        // Expression
        fc.gridy = 3;
        exprLabel = new JLabel("QUESTION EXPRESSION (e.g. 5 * 12 + 6):");
        exprLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        exprLabel.setForeground(AppTheme.getTextMuted());
        formCard.add(exprLabel, fc);

        fc.gridy = 4;
        exprField = new JTextField();
        styleTextField(exprField);
        formCard.add(exprField, fc);

        // Correct Answer
        fc.gridy = 5;
        answerLabel = new JLabel("CORRECT INTEGER ANSWER:");
        answerLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        answerLabel.setForeground(AppTheme.getTextMuted());
        formCard.add(answerLabel, fc);

        fc.gridy = 6;
        answerField = new JTextField();
        styleTextField(answerField);
        formCard.add(answerField, fc);

        // Add Button
        fc.gridy = 7;
        fc.insets = new Insets(14, 0, 0, 0);
        addBtn = new JButton("➕  ADD QUESTION");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.setBackground(AppTheme.getTextDark());
        addBtn.setForeground(AppTheme.getBgCard());
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> handleAddQuestion());
        formCard.add(addBtn, fc);

        centerPanel.add(formCard, c);

        // Right Side: Questions Table & Actions
        c.gridx = 1;
        c.weightx = 0.55;
        c.insets = new Insets(0, 0, 0, 0);

        JPanel rightPane = new JPanel(new BorderLayout(0, 10));
        rightPane.setOpaque(false);

        String[] cols = {"#", "Expression", "Answer"};
        tableModel = new DefaultTableModel(cols, 0);
        questionTable = new JTable(tableModel);
        questionTable.setRowHeight(24);
        questionTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        questionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        scrollPane = new JScrollPane(questionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1));
        scrollPane.getViewport().setBackground(AppTheme.getBgCard());
        rightPane.add(scrollPane, BorderLayout.CENTER);

        // Actions panel
        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);

        removeBtn = new JButton("➖ Remove Selected");
        removeBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> handleRemoveSelected());
        actions.add(removeBtn);

        saveBtn = new JButton("💾 Save Custom Quiz");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSaveQuiz());
        actions.add(saveBtn);

        rightPane.add(actions, BorderLayout.SOUTH);

        centerPanel.add(rightPane, c);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                new EmptyBorder(6, 8, 6, 8)));
    }

    private void handleAddQuestion() {
        String expr = exprField.getText().trim();
        String ansStr = answerField.getText().trim();
        if (expr.isEmpty() || ansStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both the expression and answer!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int answer;
        try {
            answer = Integer.parseInt(ansStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The correct answer must be a valid integer!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Question q = new Question(expr, answer);
        tempQuestions.add(q);
        tableModel.addRow(new Object[]{tempQuestions.size(), q.getExpression(), q.getCorrectAnswer()});

        // Reset inputs
        exprField.setText("");
        answerField.setText("");
        exprField.requestFocusInWindow();
    }

    private void handleRemoveSelected() {
        int row = questionTable.getSelectedRow();
        if (row >= 0 && row < tempQuestions.size()) {
            tempQuestions.remove(row);
            tableModel.removeRow(row);
            // Re-index rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(i + 1, i, 0);
            }
        }
    }

    private void handleSaveQuiz() {
        String quizName = nameField.getText().trim();
        if (quizName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quiz name!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tempQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one question!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = quizService.saveQuiz(quizName, tempQuestions);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Quiz '" + quizName + "' saved successfully!\nSaved to custom_quizzes/",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            tempQuestions.clear();
            tableModel.setRowCount(0);
            exprField.setText("");
            answerField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save quiz. Verify input parameters.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (titleLabel != null) titleLabel.setForeground(AppTheme.getTextDark());
        if (subtitleLabel != null) subtitleLabel.setForeground(AppTheme.getAccentGold());
        if (nameLabel != null) nameLabel.setForeground(AppTheme.getTextMuted());
        if (exprLabel != null) exprLabel.setForeground(AppTheme.getTextMuted());
        if (answerLabel != null) answerLabel.setForeground(AppTheme.getTextMuted());

        if (formCard != null) {
            formCard.setBackground(AppTheme.getBgCard());
            formCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(20, 25, 20, 25)));
        }

        if (nameField != null) {
            nameField.setBackground(AppTheme.getBgCard());
            nameField.setForeground(AppTheme.getTextDark());
            nameField.setCaretColor(AppTheme.getTextDark());
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 8, 6, 8)));
        }
        if (exprField != null) {
            exprField.setBackground(AppTheme.getBgCard());
            exprField.setForeground(AppTheme.getTextDark());
            exprField.setCaretColor(AppTheme.getTextDark());
            exprField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 8, 6, 8)));
        }
        if (answerField != null) {
            answerField.setBackground(AppTheme.getBgCard());
            answerField.setForeground(AppTheme.getTextDark());
            answerField.setCaretColor(AppTheme.getTextDark());
            answerField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 8, 6, 8)));
        }

        if (questionTable != null) {
            questionTable.setBackground(AppTheme.getBgCard());
            questionTable.setForeground(AppTheme.getTextDark());
            questionTable.setGridColor(AppTheme.getBorderClr());
        }
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1));
            scrollPane.getViewport().setBackground(AppTheme.getBgCard());
        }

        if (addBtn != null) {
            addBtn.setBackground(AppTheme.getTextDark());
            addBtn.setForeground(AppTheme.getBgCard());
        }
        if (removeBtn != null) {
            removeBtn.setBackground(AppTheme.getBgCard());
            removeBtn.setForeground(AppTheme.getTextMuted());
            removeBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(10, 18, 10, 18)));
        }
        if (saveBtn != null) {
            saveBtn.setBackground(AppTheme.getTextDark());
            saveBtn.setForeground(AppTheme.getBgCard());
            saveBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(10, 18, 10, 18)));
        }
        if (backBtn != null) {
            backBtn.setBackground(AppTheme.getBgPrimary());
            backBtn.setForeground(AppTheme.getTextMuted());
            backBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 12, 6, 12)));
        }
    }
}
