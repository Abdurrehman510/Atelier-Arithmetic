package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuizSession;
import com.mathquiz.service.AnalyticsService;
import com.mathquiz.service.SessionRepository;
import com.mathquiz.service.TourManager;
import com.mathquiz.service.SoundService;
import com.mathquiz.service.AchievementService;
import com.mathquiz.config.AppTheme;
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
    private final AnalyticsPanel analyticsPanel;
    private final SmartPracticePanel smartPracticePanel;
    private final AchievementsPanel achievementsPanel;
    private final QuizBuilderPanel quizBuilderPanel;



    // ── Layout ────────────────────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);



    // ── Services ──────────────────────────────────────────────────────────────
    private final AppConfig         config     = AppConfig.getInstance();
    private final SessionRepository repository = new SessionRepository();
    private final AnalyticsService  analyticsService = new AnalyticsService(repository);
    private final SoundService      soundService = new SoundService(config);
    private final AchievementService achievementsService = new AchievementService(repository, analyticsService);
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
    public static final String CARD_ANALYTICS  = "analytics";
    public static final String CARD_SMART_PRACTICE = "smart_practice";
    public static final String CARD_ACHIEVEMENTS = "achievements";
    public static final String CARD_QUIZ_BUILDER = "quiz_builder";



    // ─────────────────────────────────────────────────────────────────────────

    public QuizFrame() {
        // ── Frame setup ────────────────────────────────────────────────────────
        setTitle("Atelier Arithmetic · Royal Math Quiz");
        setSize(820, 620);
        setMinimumSize(new Dimension(720, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load Application Icon
        try {
            java.net.URL iconUrl = QuizFrame.class.getResource("/com/mathquiz/resources/logo.png");
            if (iconUrl != null) {
                setIconImage(new ImageIcon(iconUrl).getImage());
            }
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        // Initialize AppTheme with saved config value
        AppTheme.setDarkMode(config.isDarkMode());

        // ── Create panels ──────────────────────────────────────────────────────
        welcomePanel  = new WelcomePanel(this);
        categoryPanel = new CategoryPanel(this);
        gamePanel     = new GamePanel(this, soundService);
        resultsPanel  = new ResultsPanel(this);
        reviewPanel   = new ReviewPanel(this);
        helpPanel     = new HelpPanel(this);
        analyticsPanel = new AnalyticsPanel(this, analyticsService);
        smartPracticePanel = new SmartPracticePanel(this, analyticsService);
        achievementsPanel = new AchievementsPanel(this, achievementsService);
        quizBuilderPanel = new QuizBuilderPanel(this);



        // ── Register cards ─────────────────────────────────────────────────────
        mainPanel.add(welcomePanel,  CARD_WELCOME);
        mainPanel.add(categoryPanel, CARD_CATEGORIES);
        mainPanel.add(gamePanel,     CARD_GAME);
        mainPanel.add(resultsPanel,  CARD_RESULTS);
        mainPanel.add(reviewPanel,   CARD_REVIEW);
        mainPanel.add(helpPanel,     CARD_HELP);
        mainPanel.add(analyticsPanel, CARD_ANALYTICS);
        mainPanel.add(smartPracticePanel, CARD_SMART_PRACTICE);
        mainPanel.add(achievementsPanel, CARD_ACHIEVEMENTS);
        mainPanel.add(quizBuilderPanel, CARD_QUIZ_BUILDER);


        add(mainPanel);
        cardLayout.show(mainPanel, CARD_WELCOME);


        // Apply dark mode styling at start
        SwingUtilities.invokeLater(this::updateAllThemes);


        // ── Tour overlay (glass pane) ──────────────────────────────────────────
        tourOverlay = new TourOverlay();
        setGlassPane(tourOverlay);
        tourOverlay.setVisible(false);

        tourManager = new TourManager(config, tourOverlay, this);
        java.util.Map<String, Component> registry = new java.util.HashMap<>();
        // Welcome Panel
        registry.put("qCountField", welcomePanel.getQuestionCountField());
        registry.put("diffCombo", welcomePanel.getDifficultyCombo());
        registry.put("startButton", welcomePanel.getStartButton());
        registry.put("profileButton", welcomePanel.getProfileButton());
        registry.put("scaleToggleBtn", welcomePanel.getScaleToggleBtn());
        registry.put("themeToggleBtn", welcomePanel.getThemeToggleBtn());
        registry.put("soundToggleBtn", welcomePanel.getSoundToggleBtn());
        registry.put("calendarStrip", welcomePanel.getCalendarStripHolder());
        registry.put("customBuilderBtn", welcomePanel.getCustomBuilderBtn());
        registry.put("customLoadBtn", welcomePanel.getCustomLoadBtn());
        registry.put("analyticsBtn", welcomePanel.getAnalyticsBtn());
        registry.put("smartPracticeBtn", welcomePanel.getSmartPracticeBtn());
        registry.put("achievementsBtn", welcomePanel.getAchievementsBtn());

        // Category Panel
        registry.put("categoryGrid", categoryPanel.getCategoryGrid());

        // Game Panel
        registry.put("expressionLabel", gamePanel.getExpressionLabel());
        registry.put("answerField", gamePanel.getAnswerField());
        registry.put("submitButton", gamePanel.getSubmitButton());
        registry.put("progressBar", gamePanel.getProgressBar());
        registry.put("feedbackLabel", gamePanel.getFeedbackLabel());
        registry.put("hintButton", gamePanel.getHintButton());
        registry.put("timerLabel", gamePanel.getTimerLabel());

        // Results Panel
        registry.put("gradeLabel", resultsPanel.getGradeLabel());
        registry.put("restartButton", resultsPanel.getRestartButton());
        registry.put("reviewBtn", resultsPanel.getReviewBtn());
        registry.put("printBtn", resultsPanel.getPrintBtn());

        // Review Panel
        registry.put("questionTable", reviewPanel.getTable());

        // Analytics Panel
        registry.put("lineChart", analyticsPanel.getLineChart());
        registry.put("radarChart", analyticsPanel.getRadarChart());

        // Achievements Panel
        registry.put("badgeListGrid", achievementsPanel.getGridPanel());

        // Smart Practice Panel
        registry.put("smartPracticeActionBtn", smartPracticePanel.getActionButton());

        // Quiz Builder Panel
        registry.put("quizBuilderSaveBtn", quizBuilderPanel.getSaveBtn());

        tourManager.initialize(registry, () -> {
            welcomePanel.applyTheme();
        });

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
        soundService.playLaunch();
    }

    // =========================================================================
    // QuizNavigator implementation
    // =========================================================================

    @Override
    public void goToWelcome() {
        soundService.playTransition();
        cardLayout.show(mainPanel, CARD_WELCOME);
        welcomePanel.focusDefaultField();
        maybeContinueTour(CARD_WELCOME);
    }

    @Override
    public void goToCategories() {
        soundService.playTransition();
        categoryPanel.configure(
                welcomePanel.getQuestionCount(),
                welcomePanel.getDifficulty());
        cardLayout.show(mainPanel, CARD_CATEGORIES);
        categoryPanel.focusFirstCard();
        maybeContinueTour(CARD_CATEGORIES);
    }

    @Override
    public void startQuiz(String category, int questionCount, String difficulty) {
        soundService.playQuizStart();
        lastSession = new QuizSession(questionCount, difficulty, category);
        gamePanel.startSession(lastSession);
        cardLayout.show(mainPanel, CARD_GAME);
        maybeContinueTour(CARD_GAME);
    }

    @Override
    public void finishQuiz(QuizSession session) {
        if (session == null) {
            // Called from ReviewPanel "Back to Results" — just navigate back
            soundService.playTransition();
            cardLayout.show(mainPanel, CARD_RESULTS);
            return;
        }
        soundService.playFanfare();
        if (session.getCategory().equalsIgnoreCase("Daily Challenge")) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
            String todayStr = sdf.format(new java.util.Date());
            config.setLastDailyChallengeDate(todayStr);
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
        soundService.playTransition();
        if (lastSession == null) return;
        reviewPanel.populate(lastSession, CARD_RESULTS);
        cardLayout.show(mainPanel, CARD_REVIEW);
    }

    @Override
    public void showHelp(String returnScreen) {
        soundService.playTransition();
        helpPanel.setReturnScreen(returnScreen);
        cardLayout.show(mainPanel, CARD_HELP);
    }

    @Override
    public void showAnalytics() {
        soundService.playTransition();
        analyticsPanel.refresh();
        cardLayout.show(mainPanel, CARD_ANALYTICS);
    }

    @Override
    public void startSmartPractice() {
        soundService.playTransition();
        smartPracticePanel.refresh();
        cardLayout.show(mainPanel, CARD_SMART_PRACTICE);
    }

    @Override
    public void showAchievements() {
        soundService.playTransition();
        achievementsPanel.refresh();
        cardLayout.show(mainPanel, CARD_ACHIEVEMENTS);
    }

    @Override
    public void startDailyChallenge() {
        soundService.playQuizStart();
        String category = "Daily Challenge";
        String difficulty = "Medium";
        int questionCount = 10;
        
        lastSession = new QuizSession(questionCount, difficulty, category);
        gamePanel.startSession(lastSession);
        cardLayout.show(mainPanel, CARD_GAME);
    }


    @Override
    public void launchTour() {
        soundService.playTransition();
        // Always restart from the welcome card
        cardLayout.show(mainPanel, CARD_WELCOME);
        tourManager.startTourForScreen(CARD_WELCOME);
    }

    @Override
    public void showQuizBuilder() {
        soundService.playTransition();
        cardLayout.show(mainPanel, CARD_QUIZ_BUILDER);
    }

    @Override
    public void startCustomQuiz(String quizName, java.util.List<com.mathquiz.model.Question> questions) {
        soundService.playQuizStart();
        lastSession = new QuizSession(questions.size(), "Medium", "Custom: " + quizName);
        gamePanel.startSession(lastSession, questions);
        cardLayout.show(mainPanel, CARD_GAME);
    }

    @Override
    public void showCardForTour(String screen) {
        cardLayout.show(mainPanel, screen);

        // Populate mock structures for screens requiring context
        if (CARD_GAME.equals(screen) && lastSession == null) {
            lastSession = new QuizSession(10, "Medium", "Mixed");
            gamePanel.startSession(lastSession);
        }
        if (CARD_RESULTS.equals(screen) && lastSession == null) {
            lastSession = new QuizSession(10, "Medium", "Mixed");
            resultsPanel.populate(lastSession);
        }
        if (CARD_REVIEW.equals(screen) && lastSession == null) {
            lastSession = new QuizSession(10, "Medium", "Mixed");
            reviewPanel.populate(lastSession, CARD_RESULTS);
        }
        if (CARD_ANALYTICS.equals(screen)) {
            analyticsPanel.refresh();
        }
        if (CARD_ACHIEVEMENTS.equals(screen)) {
            achievementsPanel.refresh();
        }
        if (CARD_SMART_PRACTICE.equals(screen)) {
            smartPracticePanel.refresh();
        }
    }


    // ── Tour helper ───────────────────────────────────────────────────────────

    public void updateAllThemes() {
        AppTheme.setDarkMode(config.isDarkMode());
        welcomePanel.applyTheme();
        categoryPanel.applyTheme();
        gamePanel.applyTheme();
        resultsPanel.applyTheme();
        reviewPanel.applyTheme();
        helpPanel.applyTheme();
        analyticsPanel.applyTheme();
        smartPracticePanel.applyTheme();
        achievementsPanel.applyTheme();
        quizBuilderPanel.applyTheme();

        // Dynamically apply scalable font size factor
        double scale = config.getFontSizeScale();
        AppTheme.scaleComponentFont(this, scale, soundService);

        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }


    /**
     * If the tour is currently active and there are steps for the new screen,
     * advance the tour to show them.
     */
    private void maybeContinueTour(String screen) {
        if (tourManager.isActive() && tourManager.hasStepsForScreen(screen)) {
            tourManager.startTourForScreen(screen);
        }
    }

    public SoundService getSoundService() {
        return soundService;
    }
}

