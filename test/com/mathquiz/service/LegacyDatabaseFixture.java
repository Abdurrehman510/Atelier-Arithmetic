package com.mathquiz.service;

import com.mathquiz.util.CryptoHelper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds databases exactly as app v1.0.0 wrote them (schema v1, {@code user_version} 0) and
 * offers small raw-SQL helpers for assertions. Test-only.
 */
public final class LegacyDatabaseFixture {

    private LegacyDatabaseFixture() {
    }

    /**
     * Creates a v1 database with one session ("Addition", "Easy", grade "A+") holding two questions:
     * "12 + 30" = 42 answered 42 (correct) and "50 - 8" = 42 answered 41 (wrong).
     */
    public static void createV1(Path db, String profile) throws SQLException {
        try (Connection conn = open(db); Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS sessions ("
                    + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  profile_name TEXT NOT NULL,"
                    + "  timestamp TEXT NOT NULL,"
                    + "  category TEXT NOT NULL,"
                    + "  difficulty TEXT NOT NULL,"
                    + "  total_questions INTEGER NOT NULL,"
                    + "  correct_answers INTEGER NOT NULL,"
                    + "  percentage REAL NOT NULL,"
                    + "  grade TEXT NOT NULL,"
                    + "  duration_ms INTEGER NOT NULL"
                    + ");");
            st.execute("CREATE TABLE IF NOT EXISTS session_questions ("
                    + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  session_id INTEGER NOT NULL,"
                    + "  expression TEXT NOT NULL,"
                    + "  correct_answer INTEGER NOT NULL,"
                    + "  user_answer INTEGER NOT NULL,"
                    + "  correct INTEGER NOT NULL,"
                    + "  time_ms INTEGER NOT NULL,"
                    + "  FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE"
                    + ");");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO sessions (profile_name, timestamp, category, difficulty, total_questions, "
                            + "correct_answers, percentage, grade, duration_ms) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, CryptoHelper.encrypt(profile));
                ps.setString(2, "2026-11-01T10:00:00");
                ps.setString(3, CryptoHelper.encrypt("Addition"));
                ps.setString(4, CryptoHelper.encrypt("Easy"));
                ps.setInt(5, 2);
                ps.setInt(6, 1);
                ps.setDouble(7, 50.0);
                ps.setString(8, CryptoHelper.encrypt("A+"));
                ps.setLong(9, 3000L);
                ps.executeUpdate();
            }
            insertQuestion(conn, "12 + 30", 42, 42, true, 1000L);
            insertQuestion(conn, "50 - 8", 42, 41, false, 2000L);
        }
    }

    private static void insertQuestion(Connection conn, String expression, int answer, int given,
                                       boolean correct, long ms) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO session_questions (session_id, expression, correct_answer, user_answer, correct, time_ms) "
                        + "VALUES ((SELECT MAX(id) FROM sessions), ?, ?, ?, ?, ?)")) {
            ps.setString(1, CryptoHelper.encrypt(expression));
            ps.setInt(2, answer);
            ps.setInt(3, given);
            ps.setInt(4, correct ? 1 : 0);
            ps.setLong(5, ms);
            ps.executeUpdate();
        }
    }

    public static Connection open(Path db) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + db);
    }

    public static void execute(Path db, String sql) throws SQLException {
        try (Connection conn = open(db); Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    public static int userVersion(Path db) throws SQLException {
        try (Connection conn = open(db); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA user_version")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public static List<String> columns(Path db, String table) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Connection conn = open(db); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }

    public static List<String> indexNames(Path db) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Connection conn = open(db); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type = 'index'")) {
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        }
        return names;
    }

    /** Raw column values of session_questions, in insertion order. */
    public static List<Object> questionColumn(Path db, String column) throws SQLException {
        List<Object> values = new ArrayList<>();
        try (Connection conn = open(db); Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT " + column + " FROM session_questions ORDER BY id")) {
            while (rs.next()) {
                values.add(rs.getObject(1));
            }
        }
        return values;
    }
}
