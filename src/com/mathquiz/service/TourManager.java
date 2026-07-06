package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.view.tour.TourStep;
import com.mathquiz.view.tour.TourOverlay;

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

    private List<TourStep> steps  = new ArrayList<>();
    private int            cursor = 0;
    private boolean        active = false;

    // Callback invoked when the tour ends (skip or finish) so QuizFrame can
    // perform any cleanup (e.g. re-enable the help button).
    private Runnable onTourEnd;

    public TourManager(AppConfig config, TourOverlay overlay) {
        this.config  = config;
        this.overlay = overlay;
    }

    // -------------------------------------------------------------------------
    // Initialisation — called by QuizFrame after ALL panels are ready
    // -------------------------------------------------------------------------

    /**
     * Builds the tour step list using references to actual Swing components.
     * Parameters correspond to named targets across Welcome, Category, Game,
     * and Results panels.
     */
    public void initialize(
            Component qCountField,
            Component diffCombo,
            Component startButton,
            Component categoryGrid,
            Component expressionLabel,
            Component answerField,
            Component submitButton,
            Component progressBar,
            Component feedbackLabel,
            Component gradeLabel,
            Component restartButton,
            Runnable  onTourEnd) {

        this.onTourEnd = onTourEnd;
        steps.clear();

        steps.add(new TourStep("welcome", null,
                "Hi! I'm Archie the Owl — your math buddy! " +
                "I'll show you everything in this app so you can become a Math Champion! " +
                "Ready? Let's go!",
                TourStep.BubblePosition.SCREEN_CENTER,
                TourStep.MascotExpression.WAVING));

        steps.add(new TourStep("welcome", qCountField,
                "This is where you pick HOW MANY questions you want to answer! " +
                "Try starting with 5 or 10 — you can always change it!",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("welcome", diffCombo,
                "Choose how HARD your questions should be! " +
                "\"Easy\" is great for beginners. \"Hard\" is for real Math Champions!",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("welcome", startButton,
                "When you're ready, click this button to choose your math type. " +
                "We have Addition, Subtraction, Multiplication, Division and more!",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("categories", categoryGrid,
                "Here are the 6 math categories! " +
                "Click any card to start a quiz in that topic. " +
                "\"Mixed\" uses all types together — that's the ultimate challenge!",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("game", expressionLabel,
                "This is your MATH PUZZLE! Read it carefully — " +
                "then work it out in your head or on paper before typing your answer.",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.THINKING));

        steps.add(new TourStep("game", answerField,
                "Type your answer RIGHT HERE! " +
                "Only numbers please — no spaces, no symbols.",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.POINTING));

        steps.add(new TourStep("game", submitButton,
                "Click this button OR press ENTER on your keyboard to check your answer. " +
                "ENTER is the fastest way — try it!",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.HAPPY));

        steps.add(new TourStep("game", progressBar,
                "This bar shows how far you've come in the quiz. " +
                "Watch it fill up as you answer more questions — you're doing great!",
                TourStep.BubblePosition.BOTTOM_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("game", feedbackLabel,
                "After you answer, I'll tell you if you were RIGHT ✅ or WRONG ❌. " +
                "If you got it wrong, I'll show you the correct answer so you can learn!",
                TourStep.BubblePosition.TOP_CENTER,
                TourStep.MascotExpression.HAPPY));

        steps.add(new TourStep("results", gradeLabel,
                "After all questions, here's your GRADE and SCORE! " +
                "\"A++\" means you're a genius! Even a \"D\" means you tried, " +
                "and that's what matters. Practice makes perfect!",
                TourStep.BubblePosition.RIGHT_CENTER,
                TourStep.MascotExpression.EXCITED));

        steps.add(new TourStep("results", restartButton,
                "Click here to play again and beat your own score! " +
                "You can also tap the ❓ button anytime to see this guide again. " +
                "Now go and become a Math Champion! 🏆",
                TourStep.BubblePosition.TOP_CENTER,
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
