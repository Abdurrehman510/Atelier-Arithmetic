package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Reads and writes quiz session history to a unified SQLite database
 * stored at ~/.atelier-arithmetic/atelier_arithmetic.db.
 */
public class SessionRepository {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    static {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
        }
    }

    public SessionRepository() {
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        String dbPath = AppConfig.getAppDir() + File.separator + "atelier_arithmetic.db";
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void initDatabase() {
        // Ensure directories exist
        File appDir = new File(AppConfig.getAppDir());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String createSessionsTable = 
            "CREATE TABLE IF NOT EXISTS sessions (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  profile_name TEXT NOT NULL," +
            "  timestamp TEXT NOT NULL," +
            "  category TEXT NOT NULL," +
            "  difficulty TEXT NOT NULL," +
            "  total_questions INTEGER NOT NULL," +
            "  correct_answers INTEGER NOT NULL," +
            "  percentage REAL NOT NULL," +
            "  grade TEXT NOT NULL," +
            "  duration_ms INTEGER NOT NULL" +
            ");";

        String createQuestionsTable =
            "CREATE TABLE IF NOT EXISTS session_questions (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  session_id INTEGER NOT NULL," +
            "  expression TEXT NOT NULL," +
            "  correct_answer INTEGER NOT NULL," +
            "  user_answer INTEGER NOT NULL," +
            "  correct INTEGER NOT NULL," +
            "  time_ms INTEGER NOT NULL," +
            "  FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE" +
            ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createSessionsTable);
            stmt.execute(createQuestionsTable);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Appends the completed session to the database.
     */
    public void save(QuizSession session) {
        String profile = AppConfig.getInstance().getCurrentProfile();
        String timestamp = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(session.getSessionStartTime()),
                ZoneId.systemDefault()).format(TIMESTAMP_FMT);

        String insertSessionSql = 
            "INSERT INTO sessions (profile_name, timestamp, category, difficulty, total_questions, correct_answers, percentage, grade, duration_ms) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Begin transaction
            try {
                // Insert session
                long sessionId;
                try (PreparedStatement pstmt = conn.prepareStatement(insertSessionSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, profile);
                    pstmt.setString(2, timestamp);
                    pstmt.setString(3, session.getCategory());
                    pstmt.setString(4, session.getDifficulty());
                    pstmt.setInt(5, session.getTotalQuestions());
                    pstmt.setInt(6, session.getCorrectAnswersCount());
                    pstmt.setDouble(7, Math.round(session.getPercentage() * 10.0) / 10.0);
                    pstmt.setString(8, session.getGrade());
                    pstmt.setLong(9, session.getDurationMs());
                    pstmt.executeUpdate();

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            sessionId = generatedKeys.getLong(1);
                        } else {
                            throw new SQLException("Creating session failed, no ID obtained.");
                        }
                    }
                }

                // Insert questions
                String insertQ = "INSERT INTO session_questions (session_id, expression, correct_answer, user_answer, correct, time_ms) " +
                                 "VALUES (?, ?, ?, ?, ?, ?);";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQ)) {
                    for (QuestionResult r : session.getResults()) {
                        pstmt.setLong(1, sessionId);
                        pstmt.setString(2, r.getExpression());
                        pstmt.setInt(3, r.getCorrectAnswer());
                        pstmt.setInt(4, r.getUserAnswer());
                        pstmt.setInt(5, r.isCorrect() ? 1 : 0);
                        pstmt.setLong(6, r.getTimeSpentMs());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }

                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback transaction on failure
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Failed to save session history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Returns the raw history structure for all saved sessions of the active profile (newest first). */
    public List<Map<String, Object>> loadRaw() {
        String profile = AppConfig.getInstance().getCurrentProfile();
        List<Map<String, Object>> result = new ArrayList<>();

        String querySessions = 
            "SELECT id, timestamp, category, difficulty, total_questions, correct_answers, percentage, grade, duration_ms " +
            "FROM sessions " +
            "WHERE profile_name = ? " +
            "ORDER BY id DESC;";

        String queryQuestions = 
            "SELECT expression, correct_answer, user_answer, correct, time_ms " +
            "FROM session_questions " +
            "WHERE session_id = ?;";

        try (Connection conn = getConnection();
             PreparedStatement pstmtSessions = conn.prepareStatement(querySessions);
             PreparedStatement pstmtQuestions = conn.prepareStatement(queryQuestions)) {

            pstmtSessions.setString(1, profile);
            try (ResultSet rsSessions = pstmtSessions.executeQuery()) {
                while (rsSessions.next()) {
                    long sessionId = rsSessions.getLong("id");
                    Map<String, Object> sessionMap = new LinkedHashMap<>();
                    sessionMap.put("timestamp", rsSessions.getString("timestamp"));
                    sessionMap.put("category", rsSessions.getString("category"));
                    sessionMap.put("difficulty", rsSessions.getString("difficulty"));
                    sessionMap.put("totalQuestions", rsSessions.getInt("total_questions"));
                    sessionMap.put("correctAnswers", rsSessions.getInt("correct_answers"));
                    sessionMap.put("percentage", rsSessions.getDouble("percentage"));
                    sessionMap.put("grade", rsSessions.getString("grade"));
                    sessionMap.put("durationMs", rsSessions.getLong("duration_ms"));

                    // Fetch questions for this session
                    List<Map<String, Object>> questionsList = new ArrayList<>();
                    pstmtQuestions.setLong(1, sessionId);
                    try (ResultSet rsQuestions = pstmtQuestions.executeQuery()) {
                        while (rsQuestions.next()) {
                            Map<String, Object> qMap = new LinkedHashMap<>();
                            qMap.put("expression", rsQuestions.getString("expression"));
                            qMap.put("correctAnswer", rsQuestions.getInt("correct_answer"));
                            qMap.put("userAnswer", rsQuestions.getInt("user_answer"));
                            qMap.put("correct", rsQuestions.getInt("correct") == 1);
                            qMap.put("timeMs", rsQuestions.getLong("time_ms"));
                            questionsList.add(qMap);
                        }
                    }
                    sessionMap.put("questions", questionsList);
                    result.add(sessionMap);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load session history: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    /** Returns the number of saved sessions for the current profile. */
    public int getSessionCount() {
        String profile = AppConfig.getInstance().getCurrentProfile();
        String query = "SELECT COUNT(*) FROM sessions WHERE profile_name = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, profile);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to count sessions: " + e.getMessage());
        }
        return 0;
    }

    /** Returns true if history exists for the current profile. */
    public boolean hasHistory() {
        return getSessionCount() > 0;
    }

    /** Clears all saved sessions for the active profile name. */
    public void clear() {
        String profile = AppConfig.getInstance().getCurrentProfile();
        String deleteSql = "DELETE FROM sessions WHERE profile_name = ?;";
        try (Connection conn = getConnection()) {
            // Enable foreign key cascading delete to purge session_questions automatically
            try (Statement pragma = conn.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, profile);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to clear session history: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
