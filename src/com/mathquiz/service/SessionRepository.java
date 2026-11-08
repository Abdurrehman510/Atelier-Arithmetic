package com.mathquiz.service;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.util.CryptoHelper;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

/**
 * Reads and writes quiz session history to a unified SQLite database
 * stored at ~/.atelier-arithmetic/atelier_arithmetic.db.
 * Applies secure AES-128 column level encryption to all user profiles,
 * categories, difficulty parameters, accuracy ratings, and expressions.
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

    private final String dbPath;
    private final Supplier<String> currentProfile;
    private int schemaVersion;

    /** The app's database under {@link AppConfig#getAppDir()}, for the active profile. */
    public SessionRepository() {
        this(AppConfig.getAppDir() + File.separator + "atelier_arithmetic.db",
                () -> AppConfig.getInstance().getCurrentProfile());
    }

    /**
     * A repository over an explicit database file (tests, tools). Creates parent folders and
     * brings the schema up to date ({@link SchemaMigrations}).
     */
    public SessionRepository(String dbPath, Supplier<String> currentProfile) {
        this.dbPath = dbPath;
        this.currentProfile = currentProfile;
        initDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void initDatabase() {
        File parent = new File(dbPath).getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (Connection conn = getConnection()) {
            schemaVersion = SchemaMigrations.migrate(conn);
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Schema version after start-up migration; below 2 means legacy mode (old column set). */
    int getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Appends the completed session to the database, encrypting all PII and sensitive parameters.
     */
    public void save(QuizSession session) {
        String profile = currentProfile.get();
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
                    pstmt.setString(1, CryptoHelper.encrypt(profile));
                    pstmt.setString(2, timestamp);
                    pstmt.setString(3, CryptoHelper.encrypt(session.getCategory()));
                    pstmt.setString(4, CryptoHelper.encrypt(session.getDifficulty()));
                    pstmt.setInt(5, session.getTotalQuestions());
                    pstmt.setInt(6, session.getCorrectAnswersCount());
                    pstmt.setDouble(7, Math.round(session.getPercentage() * 10.0) / 10.0);
                    pstmt.setString(8, CryptoHelper.encrypt(session.getGrade()));
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
                boolean v2 = schemaVersion >= 2;
                String insertQ = v2
                        ? "INSERT INTO session_questions (session_id, expression, correct_answer, user_answer, correct, time_ms, "
                          + "skill_id, difficulty, hints_used) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
                        : "INSERT INTO session_questions (session_id, expression, correct_answer, user_answer, correct, time_ms) "
                          + "VALUES (?, ?, ?, ?, ?, ?);";
                try (PreparedStatement pstmt = conn.prepareStatement(insertQ)) {
                    for (QuestionResult r : session.getResults()) {
                        pstmt.setLong(1, sessionId);
                        pstmt.setString(2, CryptoHelper.encrypt(r.getExpression()));
                        pstmt.setInt(3, r.getCorrectAnswer());
                        pstmt.setInt(4, r.getUserAnswer());
                        pstmt.setInt(5, r.isCorrect() ? 1 : 0);
                        pstmt.setLong(6, r.getTimeSpentMs());
                        if (v2) {
                            pstmt.setString(7, CryptoHelper.encrypt(r.getSkill().id()));
                            pstmt.setString(8, CryptoHelper.encrypt(r.getDifficulty()));
                            pstmt.setInt(9, r.getHintsUsed());
                        }
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
        String profile = currentProfile.get();
        List<Map<String, Object>> result = new ArrayList<>();

        String querySessions = 
            "SELECT id, timestamp, category, difficulty, total_questions, correct_answers, percentage, grade, duration_ms " +
            "FROM sessions " +
            "WHERE profile_name = ? " +
            "ORDER BY id DESC;";

        boolean v2 = schemaVersion >= 2;
        String queryQuestions =
            "SELECT expression, correct_answer, user_answer, correct, time_ms" +
            (v2 ? ", skill_id, difficulty, hints_used " : " ") +
            "FROM session_questions " +
            "WHERE session_id = ? ORDER BY id;";

        try (Connection conn = getConnection();
             PreparedStatement pstmtSessions = conn.prepareStatement(querySessions);
             PreparedStatement pstmtQuestions = conn.prepareStatement(queryQuestions)) {

            pstmtSessions.setString(1, CryptoHelper.encrypt(profile));
            try (ResultSet rsSessions = pstmtSessions.executeQuery()) {
                while (rsSessions.next()) {
                    long sessionId = rsSessions.getLong("id");
                    Map<String, Object> sessionMap = new LinkedHashMap<>();
                    sessionMap.put("timestamp", rsSessions.getString("timestamp"));
                    sessionMap.put("category", CryptoHelper.decrypt(rsSessions.getString("category")));
                    sessionMap.put("difficulty", CryptoHelper.decrypt(rsSessions.getString("difficulty")));
                    sessionMap.put("totalQuestions", rsSessions.getInt("total_questions"));
                    sessionMap.put("correctAnswers", rsSessions.getInt("correct_answers"));
                    sessionMap.put("percentage", rsSessions.getDouble("percentage"));
                    sessionMap.put("grade", CryptoHelper.decrypt(rsSessions.getString("grade")));
                    sessionMap.put("durationMs", rsSessions.getLong("duration_ms"));

                    // Fetch questions for this session
                    List<Map<String, Object>> questionsList = new ArrayList<>();
                    pstmtQuestions.setLong(1, sessionId);
                    try (ResultSet rsQuestions = pstmtQuestions.executeQuery()) {
                        while (rsQuestions.next()) {
                            Map<String, Object> qMap = new LinkedHashMap<>();
                            qMap.put("expression", CryptoHelper.decrypt(rsQuestions.getString("expression")));
                            qMap.put("correctAnswer", rsQuestions.getInt("correct_answer"));
                            qMap.put("userAnswer", rsQuestions.getInt("user_answer"));
                            qMap.put("correct", rsQuestions.getInt("correct") == 1);
                            qMap.put("timeMs", rsQuestions.getLong("time_ms"));
                            // Schema v2 fields; null / 0 for legacy rows and in legacy mode
                            qMap.put("skillId", v2 ? CryptoHelper.decrypt(rsQuestions.getString("skill_id")) : null);
                            qMap.put("difficulty", v2 ? CryptoHelper.decrypt(rsQuestions.getString("difficulty")) : null);
                            qMap.put("hintsUsed", v2 ? rsQuestions.getInt("hints_used") : 0);
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
        String profile = currentProfile.get();
        String query = "SELECT COUNT(*) FROM sessions WHERE profile_name = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, CryptoHelper.encrypt(profile));
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
        String profile = currentProfile.get();
        String deleteSql = "DELETE FROM sessions WHERE profile_name = ?;";
        try (Connection conn = getConnection()) {
            // Enable foreign key cascading delete to purge session_questions automatically
            try (Statement pragma = conn.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
            }
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setString(1, CryptoHelper.encrypt(profile));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to clear session history: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
