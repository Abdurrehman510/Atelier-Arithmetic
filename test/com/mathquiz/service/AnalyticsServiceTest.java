package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalyticsServiceTest {

    @TempDir
    Path dir;

    /** Saves sessions oldest-first; session k scores 4k % (k of 25 correct). */
    private AnalyticsService serviceWithSessions(int count) {
        SessionRepository repo = new SessionRepository(dir.resolve("a.db").toString(), () -> "Guest");
        for (int k = 1; k <= count; k++) {
            QuizSession session = new QuizSession(25, "Easy", "Addition");
            for (int q = 0; q < 25; q++) {
                session.recordResult(q < k ? Results.correct("1 + 1", 2) : Results.wrong("1 + 1", 2, 3));
            }
            repo.save(session);
        }
        return new AnalyticsService(repo);
    }

    private static List<Double> percentagesOfSessions(int first, int last) {
        List<Double> expected = new ArrayList<>();
        for (int k = first; k <= last; k++) {
            expected.add(4.0 * k);
        }
        return expected;
    }

    /** BUG-01: with more than 20 sessions the chart must show the newest 20, oldest → newest. */
    @Test
    void trendShowsTheNewestSessionsOldestFirst() {
        assertEquals(percentagesOfSessions(6, 25), serviceWithSessions(25).getAccuracyTrend(20));
    }

    @Test
    void trendWithFewSessionsShowsAllOfThemOldestFirst() {
        assertEquals(percentagesOfSessions(1, 3), serviceWithSessions(3).getAccuracyTrend(20));
    }
}
