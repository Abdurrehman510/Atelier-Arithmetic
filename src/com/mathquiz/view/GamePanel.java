package com.mathquiz.view;

import com.mathquiz.model.Question;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.service.QuestionGenerator;

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

    // ── State ────────────────────────────────────────────────────────────────
    private final QuizNavigator    nav;
    private final QuestionGenerator generator;

    private QuizSession activeSession;
    private Question    currentQuestion;
    private long        questionStartTime;     // ms timestamp when question loaded
    private boolean     awaitingNext = false;  // true after answer submitted

    // ── Timer (counts up every second per question) ───────────────────────────
    private javax.swing.Timer questionTimer;
    private int               elapsedSeconds;

    public GamePanel(QuizNavigator nav) {
        this.nav       = nav;
        this.generator = new QuestionGenerator();
        setBackground(BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
        initTimer();
    }

    // ── Public accessors for TourManager ─────────────────────────────────────

    public JLabel       getExpressionLabel() { return expressionLabel; }
    public JTextField   getAnswerField()     { return answerField;     }
    public JButton      getSubmitButton()    { return submitButton;    }
    public JProgressBar getProgressBar()     { return progressBar;     }
    public JLabel       getFeedbackLabel()   { return feedbackLabel;   }

    // ── Session lifecycle ─────────────────────────────────────────────────────

    /** Called by QuizFrame whenever a new session begins. */
    public void startSession(QuizSession session) {
        this.activeSession = session;
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

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
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
        card.add(expressionLabel, c);

        // ── "Your Answer" label ───────────────────────────────────────────────
        c.gridy     = 1;
        c.gridwidth = 1;
        c.fill      = GridBagConstraints.NONE;
        c.anchor    = GridBagConstraints.EAST;
        JLabel eqLabel = new JLabel("YOUR ANSWER: ");
        eqLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        eqLabel.setForeground(TEXT_MUTED);
        card.add(eqLabel, c);

        // ── Answer text field ─────────────────────────────────────────────────
        c.gridx  = 1;
        c.anchor = GridBagConstraints.WEST;
        answerField = new JTextField(8);
        answerField.setFont(new Font("SansSerif", Font.BOLD, 18));
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 195, 185), 1),
                new EmptyBorder(6, 10, 6, 10)));
        answerField.addActionListener(e -> {
            if (submitButton.isEnabled()) submitButton.doClick();
        });
        card.add(answerField, c);

        // ── Feedback label ────────────────────────────────────────────────────
        c.gridx     = 0;
        c.gridy     = 2;
        c.gridwidth = 2;
        c.anchor    = GridBagConstraints.CENTER;
        c.fill      = GridBagConstraints.HORIZONTAL;
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        feedbackLabel.setForeground(TEXT_MUTED);
        card.add(feedbackLabel, c);

        outer.add(card);
        return outer;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 38, 10));

        JButton helpBtn = makeSecondaryButton("❓ Guide");
        helpBtn.addActionListener(e -> nav.showHelp("game"));
        panel.add(helpBtn);

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
        currentQuestion = generator.generate(
                activeSession.getDifficulty(),
                activeSession.getCategory());

        // Reset UI
        expressionLabel.setText(currentQuestion.getExpression() + " = ?");
        answerField.setText("");
        answerField.setEnabled(true);
        answerField.requestFocusInWindow();
        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(TEXT_MUTED);

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
        } else {
            feedbackLabel.setText("❌ Wrong. The correct answer was: "
                    + currentQuestion.getCorrectAnswer());
            feedbackLabel.setForeground(ERROR_RED);
        }

        submitButton.setText("NEXT QUESTION  →");
        awaitingNext = true;
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
