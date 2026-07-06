package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.util.JsonHelper;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reads and writes quiz session history to
 * ~/.atelier-arithmetic/history.json
 *
 * Each saved session record contains session metadata plus a per-question
 * breakdown, enabling future analytics (Phase 2).
 */
public class SessionRepository {

    private static final String HISTORY_FILE =
            AppConfig.getAppDir() + File.separator + "history.json";

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // -------------------------------------------------------------------------
    // Write
    // -------------------------------------------------------------------------

    /**
     * Appends the completed session to the history file.
     * Loads existing records, prepends the new one (newest-first), and writes back.
     */
    public void save(QuizSession session) {
        List<Map<String, Object>> all = loadRaw();

        // Build session map
        Map<String, Object> s = new LinkedHashMap<>();
        String timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(session.getSessionStartTime()),
                ZoneId.systemDefault()).format(TIMESTAMP_FMT);
        s.put("timestamp",      timestamp);
        s.put("category",       session.getCategory());
        s.put("difficulty",     session.getDifficulty());
        s.put("totalQuestions", session.getTotalQuestions());
        s.put("correctAnswers", session.getCorrectAnswersCount());
        s.put("percentage",     Math.round(session.getPercentage() * 10.0) / 10.0);
        s.put("grade",          session.getGrade());
        s.put("durationMs",     session.getDurationMs());

        // Per-question breakdown
        List<Map<String, Object>> questions = new ArrayList<>();
        for (QuestionResult r : session.getResults()) {
            Map<String, Object> q = new LinkedHashMap<>();
            q.put("expression",     r.getExpression());
            q.put("correctAnswer",  r.getCorrectAnswer());
            q.put("userAnswer",     r.getUserAnswer());
            q.put("correct",        r.isCorrect());
            q.put("timeMs",         r.getTimeSpentMs());
            questions.add(q);
        }
        s.put("questions", questions);

        // Prepend (newest first)
        all.add(0, s);

        JsonHelper.writeFile(HISTORY_FILE, JsonHelper.buildSessionArray(all));
    }

    // -------------------------------------------------------------------------
    // Read helpers (used by Phase 2 AnalyticsService)
    // -------------------------------------------------------------------------

    /** Returns the raw JSON object maps for all saved sessions. */
    public List<Map<String, Object>> loadRaw() {
        String json = JsonHelper.readFile(HISTORY_FILE);
        List<String> sessionBlocks = JsonHelper.splitArray(json);

        List<Map<String, Object>> result = new ArrayList<>();
        for (String block : sessionBlocks) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("timestamp",      JsonHelper.extractString(block, "timestamp"));
            m.put("category",       JsonHelper.extractString(block, "category"));
            m.put("difficulty",     JsonHelper.extractString(block, "difficulty"));
            m.put("totalQuestions", JsonHelper.extractInt(block,    "totalQuestions"));
            m.put("correctAnswers", JsonHelper.extractInt(block,    "correctAnswers"));
            m.put("percentage",     JsonHelper.extractDouble(block,  "percentage"));
            m.put("grade",          JsonHelper.extractString(block, "grade"));
            m.put("durationMs",     JsonHelper.extractLong(block,   "durationMs"));
            result.add(m);
        }
        return result;
    }

    /** Returns the number of saved sessions. */
    public int getSessionCount() {
        return loadRaw().size();
    }

    /** Returns true if the history file exists and is non-empty. */
    public boolean hasHistory() {
        File f = new File(HISTORY_FILE);
        return f.exists() && f.length() > 2;
    }

    /** Returns the path to the history file (for informational display). */
    public static String getHistoryFilePath() {
        return HISTORY_FILE;
    }
}
