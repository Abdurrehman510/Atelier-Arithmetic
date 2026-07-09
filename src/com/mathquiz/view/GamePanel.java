package com.mathquiz.view;

import com.mathquiz.model.Question;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.service.QuestionGenerator;
import com.mathquiz.service.AdaptiveDifficultyEngine;
import com.mathquiz.service.SoundService;
import com.mathquiz.config.AppTheme;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Active gameplay screen.
 *
 * Features (Phase 1):
 *   - Live per-question count-up timer (⏱ Xs)
 *   - Progress bar tracking session completion
 *   - Immediate correct/wrong feedback with the right answer shown on wrong
 *   - Records QuestionResult (answer + time) via QuizSession.recordResult()
 *   - Exposes named components for TourManager spotlight targeting
 */
public class GamePanel extends JPanel {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final Color BG_PRIMARY    = new Color(250, 249, 246);
    private static final Color BG_CARD       = Color.WHITE;
    private static final Color ACCENT_GOLD   = new Color(184, 150, 110);
    private static final Color TEXT_DARK     = new Color(28, 25, 23);
    private static final Color TEXT_MUTED    = new Color(120, 113, 108);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color ERROR_RED     = new Color(239, 68, 68);
    private static final Color BORDER_CLR    = new Color(230, 227, 220);

    // ── Tour-targetable components ────────────────────────────────────────────
    private JLabel       progressLabel;
    private JProgressBar progressBar;
    private JLabel       timerLabel;
    private JLabel       expressionLabel;
    private JTextField   answerField;
    private JLabel       feedbackLabel;
    private JButton      submitButton;

    // Themed components references
    private JPanel       playCard;
    private JLabel       eqLabel;
    private JButton      helpButton;
    private JButton      hintButton;
    private JPanel       hintPanel;
    private JLabel       hintTextLabel;



    // ── State ────────────────────────────────────────────────────────────────
    private final QuizNavigator    nav;
    private final QuestionGenerator generator;
    private final AdaptiveDifficultyEngine difficultyEngine;
    private final SoundService     sound;



    private QuizSession activeSession;
    private Question    currentQuestion;
    private long        questionStartTime;     // ms timestamp when question loaded
    private boolean     awaitingNext = false;  // true after answer submitted
    private java.util.List<Question> customQuestions = null;


    // ── Timer (counts up every second per question) ───────────────────────────
    private javax.swing.Timer questionTimer;
    private int               elapsedSeconds;

    public GamePanel(QuizNavigator nav, SoundService sound) {
        this.nav       = nav;
        this.sound     = sound;
        this.generator = new QuestionGenerator();
        this.difficultyEngine = new AdaptiveDifficultyEngine();
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
        initTimer();
        setupKeyBindings();
    }



    // ── Public accessors for TourManager ─────────────────────────────────────

    public JLabel       getExpressionLabel() { return expressionLabel; }
    public JTextField   getAnswerField()     { return answerField;     }
    public JButton      getSubmitButton()    { return submitButton;    }
    public JProgressBar getProgressBar()     { return progressBar;     }
    public JLabel       getFeedbackLabel()   { return feedbackLabel;   }
    public JButton      getHintButton()      { return hintButton;      }
    public JLabel       getTimerLabel()      { return timerLabel;      }

    // ── Session lifecycle ─────────────────────────────────────────────────────

    /** Called by QuizFrame whenever a new session begins. */
    public void startSession(QuizSession session) {
        startSession(session, null);
    }

    public void startSession(QuizSession session, java.util.List<Question> customQuestions) {
        this.activeSession = session;
        this.customQuestions = customQuestions;
        difficultyEngine.reset();
        progressBar.setMaximum(session.getTotalQuestions());
        progressBar.setValue(0);
        loadNextQuestion();
    }



    // ── UI construction ───────────────────────────────────────────────────────

    private void build() {
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildPlayCard(),BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 40, 8, 40));

        // Left: progress text
        progressLabel = new JLabel("Question 1 of 10");
        progressLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        progressLabel.setForeground(TEXT_DARK);
        panel.add(progressLabel, BorderLayout.WEST);

        // Centre: progress bar
        progressBar = new JProgressBar(0, 10);
        progressBar.setPreferredSize(new Dimension(180, 8));
        progressBar.setForeground(ACCENT_GOLD);
        progressBar.setBackground(new Color(230, 227, 220));
        progressBar.setBorderPainted(false);
        panel.add(progressBar, BorderLayout.CENTER);

        // Right: timer
        timerLabel = new JLabel("⏱ 0s");
        timerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        timerLabel.setForeground(TEXT_MUTED);
        panel.add(timerLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildPlayCard() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        playCard = new JPanel(new GridBagLayout());
        playCard.setBackground(BG_CARD);
        playCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(40, 55, 40, 55)));


        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(14, 10, 14, 10);
        c.gridx   = 0;
        c.gridwidth = 2;

        // ── Expression ────────────────────────────────────────────────────────
        c.gridy = 0;
        expressionLabel = new JLabel("Loading…", SwingConstants.CENTER);
        expressionLabel.setFont(new Font("Serif", Font.PLAIN, 32));
        expressionLabel.setForeground(TEXT_DARK);
        playCard.add(expressionLabel, c);


        // ── "Your Answer" label ───────────────────────────────────────────────
        c.gridy     = 1;
        c.gridwidth = 1;
        c.fill      = GridBagConstraints.NONE;
        c.anchor    = GridBagConstraints.EAST;
        eqLabel = new JLabel("YOUR ANSWER: ");
        eqLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        eqLabel.setForeground(TEXT_MUTED);
        playCard.add(eqLabel, c);


        // ── Answer text field with Hint Button ────────────────────────────────
        c.gridx  = 1;
        c.anchor = GridBagConstraints.WEST;
        JPanel fieldRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fieldRow.setOpaque(false);

        answerField = new JTextField(8);
        answerField.setFont(new Font("SansSerif", Font.BOLD, 18));
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 195, 185), 1),
                new EmptyBorder(6, 10, 6, 10)));
        answerField.addActionListener(e -> {
            if (submitButton.isEnabled()) submitButton.doClick();
        });
        fieldRow.add(answerField);

        hintButton = new JButton("💡 Hint");
        hintButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hintButton.setFocusPainted(false);
        hintButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        hintButton.setBackground(BG_CARD);
        hintButton.setForeground(TEXT_MUTED);
        hintButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1),
                new EmptyBorder(6, 12, 6, 12)));
        hintButton.addActionListener(e -> {
            if (currentQuestion != null) {
                String hint = com.mathquiz.service.HintService.generateHint(currentQuestion.getExpression());
                hintTextLabel.setText(hint);
                hintPanel.setVisible(true);
                playCard.revalidate();
                playCard.repaint();
            }
        });
        fieldRow.add(hintButton);

        playCard.add(fieldRow, c);



        // ── Feedback label ────────────────────────────────────────────────────
        c.gridx     = 0;
        c.gridy     = 2;
        c.gridwidth = 2;
        c.anchor    = GridBagConstraints.CENTER;
        c.fill      = GridBagConstraints.HORIZONTAL;
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        feedbackLabel.setForeground(TEXT_MUTED);
        playCard.add(feedbackLabel, c);

        // ── Hint Box Panel ───────────────────────────────────────────────────
        c.gridy     = 3;
        hintPanel = new JPanel(new BorderLayout(10, 0));
        hintPanel.setOpaque(true);
        hintPanel.setVisible(false);

        JLabel hintIcon = new JLabel("💡");
        hintIcon.setFont(new Font("SansSerif", Font.PLAIN, 15));
        hintPanel.add(hintIcon, BorderLayout.WEST);

        hintTextLabel = new JLabel("");
        hintTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hintPanel.add(hintTextLabel, BorderLayout.CENTER);

        playCard.add(hintPanel, c);

        outer.add(playCard);
        return outer;


    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 38, 10));

        helpButton = makeSecondaryButton("❓ Guide");
        helpButton.addActionListener(e -> nav.showHelp("game"));
        panel.add(helpButton);


        submitButton = new JButton("SUBMIT ANSWER");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        submitButton.setBackground(TEXT_DARK);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(new EmptyBorder(11, 32, 11, 32));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> handleSubmit());
        panel.add(submitButton);

        return panel;
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private void initTimer() {
        questionTimer = new javax.swing.Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText("⏱ " + elapsedSeconds + "s");
        });
    }

    private void resetTimer() {
        questionTimer.stop();
        elapsedSeconds     = 0;
        questionStartTime  = System.currentTimeMillis();
        timerLabel.setText("⏱ 0s");
        questionTimer.start();
    }

    private void stopTimer() {
        questionTimer.stop();
    }

    // ── Question flow ─────────────────────────────────────────────────────────

    private void loadNextQuestion() {
        if (activeSession.getCurrentQuestionIndex() >= activeSession.getTotalQuestions()) {
            stopTimer();
            nav.finishQuiz(activeSession);
            return;
        }

        // Generate question
        if (customQuestions != null) {
            currentQuestion = customQuestions.get(activeSession.getCurrentQuestionIndex());
        } else if (activeSession.getCategory().equalsIgnoreCase("Daily Challenge")) {
            long seed = getTodaySeed();
            currentQuestion = generator.generateSeeded(
                    activeSession.getDifficulty(),
                    "Mixed",
                    seed,
                    activeSession.getCurrentQuestionIndex()
            );
        } else {
            currentQuestion = generator.generate(
                    activeSession.getDifficulty(),
                    activeSession.getCategory()
            );
        }



        // Reset UI
        expressionLabel.setText(currentQuestion.getExpression() + " = ?");
        answerField.setText("");
        answerField.setEnabled(true);
        answerField.requestFocusInWindow();
        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(TEXT_MUTED);
        if (hintPanel != null) {
            hintPanel.setVisible(false);
            hintTextLabel.setText("");
        }


        int idx   = activeSession.getCurrentQuestionIndex();
        int total = activeSession.getTotalQuestions();
        progressLabel.setText("Question " + (idx + 1) + " of " + total);
        progressBar.setValue(idx);

        submitButton.setText("SUBMIT ANSWER");
        awaitingNext = false;

        resetTimer();
    }

    private void handleSubmit() {
        if (awaitingNext) {
            awaitingNext = false;
            loadNextQuestion();
            return;
        }

        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            feedbackLabel.setText("Type your answer first!");
            feedbackLabel.setForeground(ERROR_RED);
            return;
        }

        int val;
        try {
            val = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Only whole numbers, please!");
            feedbackLabel.setForeground(ERROR_RED);
            return;
        }

        stopTimer();
        answerField.setEnabled(false);

        long timeSpentMs = System.currentTimeMillis() - questionStartTime;
        boolean correct  = (val == currentQuestion.getCorrectAnswer());

        // Record result in session
        activeSession.recordResult(new QuestionResult(
                currentQuestion.getExpression(),
                currentQuestion.getCorrectAnswer(),
                val, correct, timeSpentMs));

        if (correct) {
            feedbackLabel.setText("✅ Correct! Great job!");
            feedbackLabel.setForeground(SUCCESS_GREEN);
            sound.playCorrect();
        } else {
            feedbackLabel.setText("❌ Wrong. The correct answer was: "
                    + currentQuestion.getCorrectAnswer());
            feedbackLabel.setForeground(ERROR_RED);
            sound.playIncorrect();
        }


        submitButton.setText("NEXT QUESTION  →");
        awaitingNext = true;

        // Evaluate adaptive difficulty suggestion
        AdaptiveDifficultyEngine.Recommendation recommendation =
                difficultyEngine.evaluate(activeSession.getResults(), activeSession.getDifficulty());
        if (recommendation == AdaptiveDifficultyEngine.Recommendation.UPGRADE) {
            SwingUtilities.invokeLater(this::proposeUpgrade);
        } else if (recommendation == AdaptiveDifficultyEngine.Recommendation.DOWNGRADE) {
            SwingUtilities.invokeLater(this::proposeDowngrade);
        }
    }

    private long getTodaySeed() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String formatted = sdf.format(new java.util.Date());
        return Long.parseLong(formatted);
    }

    public void applyTheme() {
        setBackground(AppTheme.getBgPrimary());
        if (progressLabel != null) progressLabel.setForeground(AppTheme.getTextDark());
        if (timerLabel != null) timerLabel.setForeground(AppTheme.getTextMuted());
        if (expressionLabel != null) expressionLabel.setForeground(AppTheme.getTextDark());
        if (eqLabel != null) eqLabel.setForeground(AppTheme.getTextMuted());
        if (feedbackLabel != null && awaitingNext) {
            // If showing wrong, keep red, else success or dark/muted
            if (!feedbackLabel.getForeground().equals(SUCCESS_GREEN) && !feedbackLabel.getForeground().equals(ERROR_RED)) {
                feedbackLabel.setForeground(AppTheme.getTextMuted());
            }
        }
        if (playCard != null) {
            playCard.setBackground(AppTheme.getBgCard());
            playCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(40, 55, 40, 55)));
        }
        if (answerField != null) {
            answerField.setBackground(AppTheme.getBgCard());
            answerField.setForeground(AppTheme.getTextDark());
            answerField.setCaretColor(AppTheme.getTextDark());
            answerField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 10, 6, 10)));
        }
        if (submitButton != null) {
            submitButton.setBackground(AppTheme.getTextDark());
            submitButton.setForeground(AppTheme.getBgCard());
        }
        if (helpButton != null) {
            helpButton.setBackground(AppTheme.getBgPrimary());
            helpButton.setForeground(AppTheme.getTextMuted());
        }
        if (hintButton != null) {
            hintButton.setBackground(AppTheme.getBgCard());
            hintButton.setForeground(AppTheme.getTextMuted());
            hintButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getBorderClr(), 1),
                    new EmptyBorder(6, 12, 6, 12)));
        }
        if (hintPanel != null) {
            hintPanel.setBackground(AppTheme.isDarkMode() ? new Color(40, 38, 35) : new Color(254, 252, 246));
            hintPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.getAccentGold(), 1),
                    new EmptyBorder(8, 12, 8, 12)));
        }
        if (hintTextLabel != null) {
            hintTextLabel.setForeground(AppTheme.getTextDark());
        }
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escapeKey");
        am.put("escapeKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                        GamePanel.this,
                        "Do you want to exit the current quiz and return to categories? 🦉",
                        "Exit Quiz?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    nav.goToCategories();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "hintKey");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "hintKey");
        am.put("hintKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hintButton != null && hintButton.isEnabled()) {
                    hintButton.doClick();
                }
            }
        });
    }



    private void proposeUpgrade() {
        String current = activeSession.getDifficulty();
        String next = AdaptiveDifficultyEngine.upgrade(current);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Archie notices you're doing amazing! 🦉\nWould you like to try the next difficulty level: " + next + "?",
                "Level Up?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            activeSession.setDifficulty(next);
            feedbackLabel.setText("✨ Difficulty upgraded to " + next + "! ✨");
            feedbackLabel.setForeground(SUCCESS_GREEN);
        }
    }

    private void proposeDowngrade() {
        String current = activeSession.getDifficulty();
        String prev = AdaptiveDifficultyEngine.downgrade(current);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "No worries! Archie suggests taking it a bit easier. 🦉\nWould you like to change the difficulty to: " + prev + "?",
                "Adjust Level?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            activeSession.setDifficulty(prev);
            feedbackLabel.setText("✨ Difficulty adjusted to " + prev + "! ✨");
            feedbackLabel.setForeground(ACCENT_GOLD);
        }
    }


    // ── Widget helper ─────────────────────────────────────────────────────────

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
