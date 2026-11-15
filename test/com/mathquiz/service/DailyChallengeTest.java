package com.mathquiz.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-04 / decision Q4: the Daily Challenge is playable once per calendar day per child. */
class DailyChallengeTest {

    private static final LocalDate NOV_15 = LocalDate.of(2026, 11, 15);

    @Test
    void dayKeyIsYearMonthDay() {
        assertEquals("20261115", DailyChallenge.dayKey(NOV_15));
        assertEquals("20260105", DailyChallenge.dayKey(LocalDate.of(2026, 1, 5)));
    }

    @Test
    void availableUntilCompletedToday() {
        assertTrue(DailyChallenge.isAvailable("", NOV_15), "never played");
        assertTrue(DailyChallenge.isAvailable("20261114", NOV_15), "played yesterday");
        assertFalse(DailyChallenge.isAvailable("20261115", NOV_15), "already played today");
        assertTrue(DailyChallenge.isAvailable("20261115", NOV_15.plusDays(1)), "new day, new challenge");
    }
}
