package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.testutil.TestHome;
import com.mathquiz.view.QuizNavigator;
import com.mathquiz.view.tour.TourOverlay;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-09: skipping the guided tour must stop it from auto-starting at every launch. */
class TourManagerTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();   // writes tourSeen through AppConfig
    }

    @Test
    void skippingTheTourMarksItAsSeen() throws Exception {
        AppConfig config = AppConfig.getInstance();
        boolean seenBefore = config.isTourSeen();
        config.setTourSeen(false);
        try {
            SwingUtilities.invokeAndWait(() -> {
                QuizNavigator nav = (QuizNavigator) Proxy.newProxyInstance(QuizNavigator.class.getClassLoader(),
                        new Class<?>[]{QuizNavigator.class}, (proxy, method, args) -> null);
                TourManager tour = new TourManager(config, new TourOverlay(), nav);
                tour.initialize(new HashMap<>(), null);
                tour.skipTour();
            });
            assertTrue(config.isTourSeen(), "a skipped tour must not auto-start again");
        } finally {
            config.setTourSeen(seenBefore);
        }
    }
}
