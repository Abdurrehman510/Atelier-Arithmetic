package com.mathquiz.view;

import com.mathquiz.model.QuizSession;

/**
 * Navigation contract implemented by QuizFrame.
 * Panels receive this interface to trigger screen transitions without holding
 * a direct reference to the concrete QuizFrame class (keeps them loosely coupled).
 */
public interface QuizNavigator {

    /** Go back to the Welcome / setup screen. */
    void goToWelcome();

    /** Advance from Welcome to the Category selection screen. */
    void goToCategories();

    /**
     * Start a new quiz session with the given parameters and navigate to the
     * Game screen.
     *
     * @param category       arithmetic category key (e.g. "Addition")
     * @param questionCount  total questions for this session
     * @param difficulty     "Easy" | "Medium" | "Hard"
     */
    void startQuiz(String category, int questionCount, String difficulty);

    /**
     * Called by GamePanel when all questions are answered.
     * Saves the session and navigates to the Results screen.
     */
    void finishQuiz(QuizSession session);

    /**
     * Show the post-quiz review panel for a completed session.
     *
     * @param session the session whose questions should be reviewed
     */
    void showReview(QuizSession session);

    /**
     * Show the Help & Guide panel.
     *
     * @param returnScreen the card name to navigate back to when the user
     *                     presses "← Back" inside HelpPanel
     */
    void showHelp(String returnScreen);

    /** (Re-)launch the interactive guided tour from the beginning. */
    void launchTour();
}
