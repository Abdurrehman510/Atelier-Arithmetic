package com.mathquiz.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The once-per-day rule for the Daily Challenge (PRD FR-DLY-03, decision Q4, BUG-04).
 * The completion day is stored per profile as {@code yyyyMMdd} ({@code AppConfig.getLastDailyChallengeDate}).
 */
public final class DailyChallenge {

    private static final DateTimeFormatter DAY_KEY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private DailyChallenge() {
    }

    /** The stored form of a calendar day, e.g. {@code 20261115}. */
    public static String dayKey(LocalDate day) {
        return day.format(DAY_KEY);
    }

    /** True unless the challenge was already completed on {@code today}. */
    public static boolean isAvailable(String lastCompletedDay, LocalDate today) {
        return !dayKey(today).equals(lastCompletedDay);
    }
}
