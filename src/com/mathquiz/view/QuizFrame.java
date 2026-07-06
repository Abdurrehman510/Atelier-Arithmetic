package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuizSession;
import com.mathquiz.service.SessionRepository;
import com.mathquiz.service.TourManager;
import com.mathquiz.view.tour.TourOverlay;

import javax.swing.*;
import java.awt.*;

/**
 * Lightweight orchestrator frame — the entry point for the Swing UI.
 *
 * Responsibilities (Phase 1):
 *   ✅ Owns the CardLayout and all panel instances
 *   ✅ Implements QuizNavigator so panels remain loosely coupled
 *   ✅ Manages QuizSession lifecycle (create → pass to panels → save)
 *   ✅ Installs the TourOverlay as the glass pane and drives the tour
 *   ✅ Triggers the auto-tour on first run via AppConfig
 *
 * This class deliberately contains NO business logic — only wiring.
 * Total LOC is kept to a minimum; all real logic lives in panels and services.
 */
public class QuizFrame extends JFrame implements QuizNavigator {

    // ── Panels (one instance each, reused) ────────────────────────────────────
    private final WelcomePanel  welcomePanel;
    private final CategoryPanel categoryPanel;
    private final GamePanel     gamePanel;
    private final ResultsPanel  resultsPanel;
    private final ReviewPanel   reviewPanel;
    private final HelpPanel     helpPanel;

    // ── Layout ────────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);

    // ── Services ──────────────────────────────────────────────────────────────
    private final AppConfig         config     = new AppConfig();
    private final SessionRepository repository = new SessionRepository();
    private final TourOverlay       tourOverlay;
    private final TourManager       tourManager;

    // ── Session state ─────────────────────────────────────────────────────────
    private QuizSession lastSession;   // retained so Review can access it

    // ── Card name constants ───────────────────────────────────────────────────
    public static final String CARD_WELCOME    = "welcome";
    public static final String CARD_CATEGORIES = "categories";
    public static final String CARD_GAME       = "game";
    public static final String CARD_RESULTS    = "results";
    public static final String CARD_REVIEW     = "review";
    public static final String CARD_HELP       = "help";

    // ─────────────────────────────────────────────────────────────────────────

    public QuizFrame() {
        // ── Frame setup ────────────────────────────────────────────────────────
        setTitle("Atelier Arithmetic · Royal Math Quiz");
        setSize(800, 580);
        setMinimumSize(new Dimension(700, 510));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ── Create panels ──────────────────────────────────────────────────────
        welcomePanel  = new WelcomePanel(this);
        categoryPanel = new CategoryPanel(this);
        gamePanel     = new GamePanel(this);
        resultsPanel  = new ResultsPanel(this);
        reviewPanel   = new ReviewPanel(this);
        helpPanel     = new HelpPanel(this);

        // ── Register cards ─────────────────────────────────────────────────────
        mainPanel.add(welcomePanel,  CARD_WELCOME);
        mainPanel.add(categoryPanel, CARD_CATEGORIES);
        mainPanel.add(gamePanel,     CARD_GAME);
        mainPanel.add(resultsPanel,  CARD_RESULTS);
        mainPanel.add(reviewPanel,   CARD_REVIEW);
        mainPanel.add(helpPanel,     CARD_HELP);

        add(mainPanel);
        cardLayout.show(mainPanel, CARD_WELCOME);

        // ── Tour overlay (glass pane) ──────────────────────────────────────────
        tourOverlay = new TourOverlay();
        setGlassPane(tourOverlay);
        tourOverlay.setVisible(false);

        tourManager = new TourManager(config, tourOverlay);
        tourManager.initialize(
                welcomePanel.getQuestionCountField(),
                welcomePanel.getDifficultyCombo(),
                welcomePanel.getStartButton(),
                categoryPanel.getCategoryGrid(),
                gamePanel.getExpressionLabel(),
                gamePanel.getAnswerField(),
                gamePanel.getSubmitButton(),
                gamePanel.getProgressBar(),
                gamePanel.getFeedbackLabel(),
                resultsPanel.getGradeLabel(),
                resultsPanel.getRestartButton(),
                () -> {}   // onTourEnd callback — no-op for now
        );

        // ── Auto-launch tour on first run ─────────────────────────────────────
        if (!config.isTourSeen()) {
            // Small delay so the frame is fully painted before the overlay appears
            javax.swing.Timer[] timerRef = { null };
            timerRef[0] = new javax.swing.Timer(400, e -> {
                tourManager.startTourForScreen(CARD_WELCOME);
                timerRef[0].stop();
            });
            timerRef[0].setRepeats(false);
            SwingUtilities.invokeLater(() -> timerRef[0].start());
        }
    }

    // =========================================================================
    // QuizNavigator implementation
    // =========================================================================

    @Override
    public void goToWelcome() {
        cardLayout.show(mainPanel, CARD_WELCOME);
        maybeContinueTour(CARD_WELCOME);
    }

    @Override
    public void goToCategories() {
        categoryPanel.configure(
                welcomePanel.getQuestionCount(),
                welcomePanel.getDifficulty());
        cardLayout.show(mainPanel, CARD_CATEGORIES);
        maybeContinueTour(CARD_CATEGORIES);
    }

    @Override
    public void startQuiz(String category, int questionCount, String difficulty) {
        lastSession = new QuizSession(questionCount, difficulty, category);
        gamePanel.startSession(lastSession);
        cardLayout.show(mainPanel, CARD_GAME);
        maybeContinueTour(CARD_GAME);
    }

    @Override
    public void finishQuiz(QuizSession session) {
        if (session == null) {
            // Called from ReviewPanel "Back to Results" — just navigate back
            cardLayout.show(mainPanel, CARD_RESULTS);
            return;
        }
        // Save to disk
        repository.save(session);
        // Update results screen
        resultsPanel.populate(session);
        cardLayout.show(mainPanel, CARD_RESULTS);
        maybeContinueTour(CARD_RESULTS);
    }

    @Override
    public void showReview(QuizSession session) {
        if (lastSession == null) return;
        reviewPanel.populate(lastSession, CARD_RESULTS);
        cardLayout.show(mainPanel, CARD_REVIEW);
    }

    @Override
    public void showHelp(String returnScreen) {
        helpPanel.setReturnScreen(returnScreen);
        cardLayout.show(mainPanel, CARD_HELP);
    }

    @Override
    public void launchTour() {
        // Always restart from the welcome card
        cardLayout.show(mainPanel, CARD_WELCOME);
        tourManager.startTourForScreen(CARD_WELCOME);
    }

    // ── Tour helper ───────────────────────────────────────────────────────────

    /**
     * If the tour is currently active and there are steps for the new screen,
     * advance the tour to show them.
     */
    private void maybeContinueTour(String screen) {
        if (tourManager.isActive() && tourManager.hasStepsForScreen(screen)) {
            tourManager.startTourForScreen(screen);
        }
    }
}
