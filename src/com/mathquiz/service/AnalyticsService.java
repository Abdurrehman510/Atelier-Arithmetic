package com.mathquiz.service;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import java.util.*;

/**
 * Computes aggregate performance statistics from the persisted session history.
 *
 * Phase 2 — Intelligence Layer.
 *
 * Used by:
 *   - AnalyticsPanel   (dashboard charts and summary cards)
 *   - SmartPracticePanel (weakest-category targeting)
 *   - AdaptiveDifficultyEngine (baseline accuracy per category)
 */
public class AnalyticsService {

    /** The canonical categories. */
    public static final String[] ALL_CATEGORIES = {
        "Addition", "Difference", "Multiplication", "Division", "Mixed", "Special",
        "Fractions", "Patterns", "Algebra", "Measurement"
    };

    private final SessionRepository repository;

    public AnalyticsService(SessionRepository repository) {
        this.repository = repository;
    }

    // =========================================================================
    // Accuracy trend (for line chart)
    // =========================================================================

    /**
     * Returns accuracy percentages for the last {@code maxSessions} sessions,
     * ordered oldest-first (left to right on the chart).
     *
     * Each element is a value 0–100.
     */
    public List<Double> getAccuracyTrend(int maxSessions) {
        List<Map<String, Object>> raw = repository.loadRaw();

        // raw is newest-first; take up to maxSessions then reverse
        int from = Math.max(0, raw.size() - maxSessions);
        List<Map<String, Object>> slice = raw.subList(from, raw.size());

        List<Double> result = new ArrayList<>();
        for (int i = slice.size() - 1; i >= 0; i--) {
            Object pct = slice.get(i).get("percentage");
            if (pct instanceof Number) {
                result.add(((Number) pct).doubleValue());
            } else {
                result.add(0.0);
            }
        }
        return result;
    }

    // =========================================================================
    // Per-category accuracy (for radar chart)
    // =========================================================================

    /**
     * Returns a map from category name → average accuracy (0–100) across all
     * sessions that used that category. Categories with no sessions map to 0.
     */
    public Map<String, Double> getCategoryAccuracy() {
        Map<String, Double> sumMap   = new LinkedHashMap<>();
        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (String cat : ALL_CATEGORIES) {
            sumMap.put(cat, 0.0);
            countMap.put(cat, 0);
        }

        for (Map<String, Object> s : repository.loadRaw()) {
            String cat = (String) s.get("category");
            if (cat == null || cat.isEmpty()) continue;

            // Normalise to the canonical display name
            String canonical = canonicalise(cat);
            if (!sumMap.containsKey(canonical)) continue;

            Object pct = s.get("percentage");
            double val = (pct instanceof Number) ? ((Number) pct).doubleValue() : 0.0;

            sumMap.put(canonical, sumMap.get(canonical) + val);
            countMap.put(canonical, countMap.get(canonical) + 1);
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (String cat : ALL_CATEGORIES) {
            int n = countMap.get(cat);
            result.put(cat, n == 0 ? 0.0 : sumMap.get(cat) / n);
        }
        return result;
    }

    // =========================================================================
    // Summary statistics (for stat cards)
    // =========================================================================

    /** Total number of completed sessions stored. */
    public int getTotalSessions() {
        return repository.loadRaw().size();
    }

    /** Highest percentage score ever achieved, or 0 if no sessions. */
    public double getBestScore() {
        double best = 0;
        for (Map<String, Object> s : repository.loadRaw()) {
            Object pct = s.get("percentage");
            if (pct instanceof Number) {
                double v = ((Number) pct).doubleValue();
                if (v > best) best = v;
            }
        }
        return best;
    }

    /** Overall average accuracy across all sessions, or 0 if no sessions. */
    public double getAverageScore() {
        List<Map<String, Object>> raw = repository.loadRaw();
        if (raw.isEmpty()) return 0;
        double sum = 0;
        for (Map<String, Object> s : raw) {
            Object pct = s.get("percentage");
            if (pct instanceof Number) sum += ((Number) pct).doubleValue();
        }
        return sum / raw.size();
    }

    /**
     * Longest streak of consecutive days on which at least one session was
     * completed. Returns 0 if no history.
     *
     * Streaks are computed from the "timestamp" field (yyyy-MM-ddTHH:mm:ss).
     */
    public int getLongestStreak() {
        Set<String> days = new TreeSet<>();
        for (Map<String, Object> s : repository.loadRaw()) {
            String ts = (String) s.get("timestamp");
            if (ts != null && ts.length() >= 10) {
                days.add(ts.substring(0, 10)); // "yyyy-MM-dd"
            }
        }
        if (days.isEmpty()) return 0;

        List<String> sorted = new ArrayList<>(days);
        Collections.sort(sorted);

        int best    = 1;
        int current = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (dayDiff(sorted.get(i - 1), sorted.get(i)) == 1) {
                current++;
                if (current > best) best = current;
            } else {
                current = 1;
            }
        }
        return best;
    }

    // =========================================================================
    // Smart Practice targeting
    // =========================================================================

    /**
     * Returns the canonical category name with the lowest average accuracy.
     * Only considers categories that have at least one session.
     * Returns "Mixed" as a safe fallback if no history exists.
     */
    public String getWeakestCategory() {
        Map<String, Double> acc = getCategoryAccuracy();
        String weakest = null;
        double lowestAcc = Double.MAX_VALUE;

        // Skip categories with 0 sessions (their avg is 0 by default — unfair baseline)
        Map<String, Integer> counts = getCategoryCounts();

        for (Map.Entry<String, Double> e : acc.entrySet()) {
            int n = counts.getOrDefault(e.getKey(), 0);
            if (n > 0 && e.getValue() < lowestAcc) {
                lowestAcc = e.getValue();
                weakest = e.getKey();
            }
        }
        return weakest != null ? weakest : "Mixed";
    }

    /**
     * Returns the difficulty level that has the lowest average accuracy across
     * all sessions, regardless of category. Falls back to "Easy".
     */
    public String getWeakestDifficulty() {
        Map<String, Double> sumMap   = new LinkedHashMap<>();
        Map<String, Integer> countMap = new LinkedHashMap<>();
        String[] levels = {"Easy", "Medium", "Hard"};
        for (String l : levels) { sumMap.put(l, 0.0); countMap.put(l, 0); }

        for (Map<String, Object> s : repository.loadRaw()) {
            String diff = (String) s.get("difficulty");
            if (diff == null || !sumMap.containsKey(diff)) continue;
            Object pct = s.get("percentage");
            double val = (pct instanceof Number) ? ((Number) pct).doubleValue() : 0.0;
            sumMap.put(diff, sumMap.get(diff) + val);
            countMap.put(diff, countMap.get(diff) + 1);
        }

        String weakest    = "Easy";
        double lowestAvg  = Double.MAX_VALUE;
        for (String l : levels) {
            int n = countMap.get(l);
            if (n > 0) {
                double avg = sumMap.get(l) / n;
                if (avg < lowestAvg) { lowestAvg = avg; weakest = l; }
            }
        }
        return weakest;
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private Map<String, Integer> getCategoryCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String cat : ALL_CATEGORIES) counts.put(cat, 0);
        for (Map<String, Object> s : repository.loadRaw()) {
            String cat = canonicalise((String) s.get("category"));
            if (counts.containsKey(cat)) counts.put(cat, counts.get(cat) + 1);
        }
        return counts;
    }

    /**
     * Normalises a stored category string to the canonical display form.
     * e.g. "addition" → "Addition", "Special" → "Special"
     */
    private static String canonicalise(String raw) {
        if (raw == null || raw.isEmpty()) return "";
        for (String cat : ALL_CATEGORIES) {
            if (cat.equalsIgnoreCase(raw.trim())) return cat;
        }
        return raw.trim();
    }

    /**
     * Returns the number of calendar days between two ISO date strings
     * ("yyyy-MM-dd"). Returns a large number if parsing fails.
     */
    private static long dayDiff(String d1, String d2) {
        try {
            long ms1 = parseDate(d1);
            long ms2 = parseDate(d2);
            return (ms2 - ms1) / 86_400_000L;
        } catch (Exception e) {
            return 999;
        }
    }

    /** Very lightweight yyyy-MM-dd → epoch-day parser (no java.time dependency). */
    @SuppressWarnings("deprecation")
    private static long parseDate(String d) {
        int year  = Integer.parseInt(d.substring(0, 4));
        int month = Integer.parseInt(d.substring(5, 7));
        int day   = Integer.parseInt(d.substring(8, 10));
        // Use Date for compatibility with JDK 8
        return new java.util.Date(year - 1900, month - 1, day).getTime();
    }

    /** Total questions solved across all history. */
    public int getTotalQuestionsSolved() {
        int total = 0;
        for (Map<String, Object> s : repository.loadRaw()) {
            Object tq = s.get("totalQuestions");
            if (tq instanceof Number) {
                total += ((Number) tq).intValue();
            }
        }
        return total;
    }

    /** Total practice time in seconds. */
    public double getTotalPracticeTimeSec() {
        long totalMs = 0;
        for (Map<String, Object> s : repository.loadRaw()) {
            Object dur = s.get("durationMs");
            if (dur instanceof Number) {
                totalMs += ((Number) dur).longValue();
            }
        }
        return totalMs / 1000.0;
    }

    /** Average response time in seconds per question. */
    public double getAverageResponseTimeSec() {
        long totalMs = 0;
        int count = 0;
        for (Map<String, Object> s : repository.loadRaw()) {
            Object qListObj = s.get("questions");
            if (qListObj instanceof List) {
                List<?> qList = (List<?>) qListObj;
                for (Object qObj : qList) {
                    if (qObj instanceof Map) {
                        Map<?, ?> q = (Map<?, ?>) qObj;
                        Object timeVal = q.get("timeMs");
                        if (timeVal instanceof Number) {
                            totalMs += ((Number) timeVal).longValue();
                            count++;
                        }
                    }
                }
            }
        }
        if (count == 0) return 0.0;
        return (totalMs / 1000.0) / count;
    }

    /** Fastest correct answer in seconds. */
    public double getFastestAnswerSec() {
        long fastestMs = Long.MAX_VALUE;
        for (Map<String, Object> s : repository.loadRaw()) {
            Object qListObj = s.get("questions");
            if (qListObj instanceof List) {
                List<?> qList = (List<?>) qListObj;
                for (Object qObj : qList) {
                    if (qObj instanceof Map) {
                        Map<?, ?> q = (Map<?, ?>) qObj;
                        Object correctVal = q.get("correct");
                        Object timeVal = q.get("timeMs");
                        if (Boolean.TRUE.equals(correctVal) && timeVal instanceof Number) {
                            long ms = ((Number) timeVal).longValue();
                            if (ms > 100 && ms < fastestMs) {
                                fastestMs = ms;
                            }
                        }
                    }
                }
            }
        }
        if (fastestMs == Long.MAX_VALUE) return 0.0;
        return fastestMs / 1000.0;
    }

    /** Returns current streak in days. */
    public int getCurrentStreak() {
        Set<String> days = new TreeSet<>();
        for (Map<String, Object> s : repository.loadRaw()) {
            String ts = (String) s.get("timestamp");
            if (ts != null && ts.length() >= 10) {
                days.add(ts.substring(0, 10));
            }
        }
        if (days.isEmpty()) return 0;

        List<String> sorted = new ArrayList<>(days);
        Collections.sort(sorted);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new java.util.Date());
        int lastIndex = sorted.size() - 1;
        String lastPlayed = sorted.get(lastIndex);

        long diffFromToday = dayDiff(lastPlayed, today);
        if (diffFromToday > 1) return 0;

        int streak = 1;
        for (int i = lastIndex; i > 0; i--) {
            if (dayDiff(sorted.get(i - 1), sorted.get(i)) == 1) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    /** Returns a list of mastered categories (average accuracy >= 90%). */
    public List<String> getMasteredCategories() {
        List<String> mastered = new ArrayList<>();
        Map<String, Double> categoryAccuracy = getCategoryAccuracy();
        Map<String, Integer> counts = getCategoryCounts();
        for (Map.Entry<String, Double> e : categoryAccuracy.entrySet()) {
            if (counts.getOrDefault(e.getKey(), 0) > 0 && e.getValue() >= 90.0) {
                mastered.add(e.getKey());
            }
        }
        return mastered;
    }

    /** Returns the practice heatmap as a boolean array representing activity over the last 28 days. */
    public boolean[] getPracticeHeatmap28Days() {
        boolean[] heatmap = new boolean[28];
        Set<String> playedDays = new HashSet<>();
        for (Map<String, Object> s : repository.loadRaw()) {
            String ts = (String) s.get("timestamp");
            if (ts != null && ts.length() >= 10) {
                playedDays.add(ts.substring(0, 10));
            }
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        for (int i = 0; i < 28; i++) {
            String dateStr = sdf.format(cal.getTime());
            if (playedDays.contains(dateStr)) {
                heatmap[i] = true;
            }
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }
        return heatmap;
    }

    /** Returns count of completed daily challenges. */
    public int getDailyChallengesCompleted() {
        int count = 0;
        for (Map<String, Object> s : repository.loadRaw()) {
            String cat = (String) s.get("category");
            if (cat != null && cat.equalsIgnoreCase("Daily Challenge")) {
                count++;
            }
        }
        return count;
    }

    /** Clears the session history in the repository. */
    public void clearHistory() {
        repository.clear();
    }
}

