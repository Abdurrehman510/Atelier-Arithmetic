package com.mathquiz.view;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** BUG-02: the Results "next step" button must fire exactly one, current recommendation. */
class ResultsPanelNextStepTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();   // panels read AppConfig, which creates the data folder
    }

    private final List<String> calls = new ArrayList<>();

    private QuizNavigator recordingNavigator() {
        return (QuizNavigator) Proxy.newProxyInstance(QuizNavigator.class.getClassLoader(),
                new Class<?>[]{QuizNavigator.class}, (proxy, method, args) -> {
                    calls.add(method.getName() + (args == null ? "" : Arrays.toString(args)));
                    return null;
                });
    }

    private static QuizSession session(String difficulty, int correct, int total) {
        QuizSession s = new QuizSession(total, difficulty, "Addition");
        for (int i = 0; i < total; i++) {
            s.recordResult(i < correct ? Results.correct("1 + 1", 2) : Results.wrong("1 + 1", 2, 3));
        }
        return s;
    }

    static AbstractButton findButton(Container root, String textPrefix) {
        for (Component c : root.getComponents()) {
            if (c instanceof AbstractButton && ((AbstractButton) c).getText() != null
                    && ((AbstractButton) c).getText().startsWith(textPrefix)) {
                return (AbstractButton) c;
            }
            if (c instanceof Container) {
                AbstractButton found = findButton((Container) c, textPrefix);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Test
    void oneClickNavigatesOnceAfterSeveralQuizzes() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ResultsPanel panel = new ResultsPanel(recordingNavigator());
            for (int i = 0; i < 3; i++) {
                panel.populate(session("Easy", 5, 10));      // 50 % → Smart Practice
            }
            calls.clear();
            AbstractButton button = findButton(panel, "START SMART PRACTICE");
            assertNotNull(button, "Smart Practice button not found");
            button.doClick();
        });
        assertEquals(List.of("startSmartPractice"), calls);
    }

    @Test
    void stepUpPreselectsTheNextDifficulty() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            ResultsPanel panel = new ResultsPanel(recordingNavigator());
            panel.populate(session("Easy", 3, 10));          // Smart Practice first
            panel.populate(session("Easy", 9, 10));          // then 90 % → step up
            calls.clear();
            AbstractButton button = findButton(panel, "STEP UP TO MEDIUM");
            assertNotNull(button, "step-up button not found");
            button.doClick();
        });
        assertEquals(List.of("goToWelcome[Medium]"), calls);
    }
}
