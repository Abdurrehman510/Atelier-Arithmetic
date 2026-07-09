package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.view.tour.TourStep;
import com.mathquiz.view.tour.TourOverlay;
import com.mathquiz.view.QuizNavigator;


import java.awt.Component;
import java.util.*;

/**
 * Controls the interactive guided tour lifecycle.
 *
 * Responsibilities:
 *   - Owns the ordered list of TourSteps (built after panels are initialised)
 *   - Tracks current step index and whether the tour is active
 *   - Persists "tour seen" flag via AppConfig
 *   - Drives the TourOverlay forward/skip on user interaction
 *
 * Usage:
 *   QuizFrame creates TourManager, calls initialize() after all panels exist,
 *   then calls maybeAutoLaunch() — which auto-starts the tour on first run.
 */
public class TourManager {

    private final AppConfig    config;
    private final TourOverlay  overlay;
    private final QuizNavigator navigator;

    private List<TourStep> steps  = new ArrayList<>();
    private int            cursor = 0;
    private boolean        active = false;

    // Callback invoked when the tour ends (skip or finish) so QuizFrame can
    // perform any cleanup (e.g. re-enable the help button).
    private Runnable onTourEnd;

    public TourManager(AppConfig config, TourOverlay overlay, QuizNavigator navigator) {
        this.config    = config;
        this.overlay   = overlay;
        this.navigator = navigator;
    }

    // -------------------------------------------------------------------------
    // Initialisation — called by QuizFrame after ALL panels are ready
    // -------------------------------------------------------------------------

    /**
     * Builds the tour step list using references to actual Swing components.
     * Maps named targets across all cards to explain every single feature.
     */
    public void initialize(Map<String, Component> registry, Runnable onTourEnd) {
        this.onTourEnd = onTourEnd;
        steps.clear();

        // ── WELCOME CARD STEPS ──
        steps.add(new TourStep("welcome", null,
                "Welcome to Atelier Arithmetic! I'm Archie, your math guide. " +
                "I'll show you every single detail of our app so you can master math!",
                TourStep.BubblePosition.SCREEN_CENTER,
                TourStep.MascotExpression.WAVING));

        steps.add(new TourStep("welcome", registry.get("profileButton"),
                "This is the Profile dropdown! Create separate profiles for each " +
                "child to track their individual history, streaks, and badges separately.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("welcome", registry.get("scaleToggleBtn"),
                "Tired of tiny text? Click here to scale the font size up to 150%! " +
                "Perfect for high-resolution screens and better readability.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("welcome", registry.get("themeToggleBtn"),
                "Love dark screens? Press this Moon icon to instantly switch to our " +
                "professional Zinc Dark Mode to reduce eye strain in the evening.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.HAPPY));

        steps.add(new TourStep("welcome", registry.get("calendarStrip"),
                "Practice every day! Completing a quiz fills this weekly calendar tracker " +
                "and increments your Practice Streak Flame. Stay consistent!",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("welcome", registry.get("customBuilderBtn"),
                "Parents & Teachers: use the Quiz Builder editor to write and compile " +
                "custom questions with tailored mathematical expressions.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("welcome", registry.get("customLoadBtn"),
                "Click this button to see and run any custom quizzes you've authored! " +
                "Great for targeted homework or special class challenges.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("welcome", registry.get("qCountField"),
                "Configure regular sessions: type how many questions you want " +
                "to answer in this block (e.g. 5, 10, or 20 questions).",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("welcome", registry.get("diffCombo"),
                "Pick your challenge level: Easy, Medium, or Hard. " +
                "The engine also adapts difficulty live as you play!",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("welcome", registry.get("analyticsBtn"),
                "Click this to view your analytics dashboard! Renders interactive " +
                "Bezier trend charts and a custom radar grid of topic accuracies.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("welcome", registry.get("achievementsBtn"),
                "Tap here to see your unlocked Milestones. Can you unlock all 10 " +
                "glowing gold badges and achievements?",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("welcome", registry.get("smartPracticeBtn"),
                "Archie's Smart Practice: automatically analyzes your data and launch " +
                "targeted review quizzes addressing your weakest category/difficulty combination.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.HAPPY));

        steps.add(new TourStep("welcome", registry.get("startButton"),
                "When your settings are set, click CHOOSE CATEGORY to advance and pick " +
                "the arithmetic discipline you wish to practice today.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.EXCITED));

        // ── CATEGORY CARD STEPS ──
        steps.add(new TourStep("categories", registry.get("categoryGrid"),
                "Pick one of the 6 disciplines! Use arrow keys to navigate and " +
                "press Space or Enter to select cards without the mouse.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        // ── GAME PLAY STEPS ──
        steps.add(new TourStep("game", registry.get("expressionLabel"),
                "This is the question block. Read it carefully and formulate the calculation.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("game", registry.get("answerField"),
                "Input your digits in this entry box. No spaces or letters allowed.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("game", registry.get("hintButton"),
                "Stuck? Click this lightbulb (or press F1 / Ctrl+H) to see a step-by-step " +
                "mathematical hint partitioning calculations or order of operations.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("game", registry.get("timerLabel"),
                "This counts up time spent on the current question. We collect " +
                "timing speed metrics to graph in analytics charts.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("game", registry.get("submitButton"),
                "Press ENTER or tap here to submit. You get instant correct/wrong " +
                "audio and visual mascot feedback immediately.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.HAPPY));

        // ── RESULTS STEPS ──
        steps.add(new TourStep("results", registry.get("gradeLabel"),
                "View your grade, remarks, and visual grade badge! High-achieving results " +
                "will animate the owl mascot, celebrating success.",
                TourStep.BubblePosition.RIGHT_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("results", registry.get("printBtn"),
                "Click Export Report to write a beautifully laid out HTML parent sheet " +
                "or trigger your system printing dialog to print physical sheets.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("results", registry.get("reviewBtn"),
                "Click Review Answers to open the question review table.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        // ── REVIEW STEPS ──
        steps.add(new TourStep("review", registry.get("questionTable"),
                "Review each question. Rows are color-tinted: light green for correct answers, " +
                "and light red for wrong inputs showing correct values.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.HAPPY));

        // ── OUTRO STEP ──
        steps.add(new TourStep("welcome", null,
                "You're fully trained now! Practice daily, earn gold badges, and " +
                "become an Arithmetic Master. Keep learning!",
                TourStep.BubblePosition.SCREEN_CENTER,
                TourStep.MascotExpression.WAVING));
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Starts the tour from the beginning on the given screen.
     * Only shows steps that belong to the current screen.
     */
    public void startTourForScreen(String screen) {
        // Find first step for this screen
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).screen.equals(screen)) {
                cursor = i;
                active = true;
                showCurrentStep();
                return;
            }
        }
    }

    /** Advances to the next step; if on the last step, ends the tour. */
    public void nextStep() {
        if (!active) return;
        cursor++;
        if (cursor >= steps.size()) {
            endTour(true);
        } else {
            showCurrentStep();
        }
    }

    /** Skips the tour entirely. */
    public void skipTour() {
        endTour(false);
    }

    /** Returns true if there are pending steps for the given screen name. */
    public boolean hasStepsForScreen(String screen) {
        for (TourStep step : steps) {
            if (step.screen.equals(screen)) return true;
        }
        return false;
    }

    /** Whether the tour has already been completed/seen. */
    public boolean isTourAlreadySeen() {
        return config.isTourSeen();
    }

    /** Whether the tour is currently running. */
    public boolean isActive() { return active; }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void showCurrentStep() {
        if (cursor >= steps.size()) {
            endTour(true);
            return;
        }
        TourStep step = steps.get(cursor);

        // Auto-navigate user to correct screen so target components are rendered and painted!
        if (navigator != null) {
            navigator.showCardForTour(step.screen);
        }

        overlay.showStep(step, cursor, steps.size(), this::nextStep, this::skipTour);
    }


    private void endTour(boolean completed) {
        active = false;
        overlay.hideTour();
        if (completed) {
            config.setTourSeen(true);
        }
        if (onTourEnd != null) onTourEnd.run();
    }
}
