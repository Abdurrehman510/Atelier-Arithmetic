package com.mathquiz.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Versioned SQLite schema (rules.md R-DAT-03, task DEBT-11), tracked in {@code PRAGMA user_version}.
 *
 * <ul>
 *   <li>v1 — the original two tables (app 1.0.0 never set user_version, so it reads 0).</li>
 *   <li>v2 — {@code session_questions} gains {@code skill_id}, {@code difficulty} (both encrypted)
 *       and {@code hints_used}; indexes on the profile and session lookups.</li>
 * </ul>
 *
 * Migrations only add; they never drop or rewrite user data.
 */
final class SchemaMigrations {

    static final int CURRENT_VERSION = 2;

    private static final String[] TO_V1 = {
            "CREATE TABLE IF NOT EXISTS sessions ("
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
                    + ");",
            "CREATE TABLE IF NOT EXISTS session_questions ("
                    + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "  session_id INTEGER NOT NULL,"
                    + "  expression TEXT NOT NULL,"
                    + "  correct_answer INTEGER NOT NULL,"
                    + "  user_answer INTEGER NOT NULL,"
                    + "  correct INTEGER NOT NULL,"
                    + "  time_ms INTEGER NOT NULL,"
                    + "  FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE"
                    + ");",
            "PRAGMA user_version = 1;"
    };

    private static final String[] TO_V2 = {
            "ALTER TABLE session_questions ADD COLUMN skill_id TEXT;",
            "ALTER TABLE session_questions ADD COLUMN difficulty TEXT;",
            "ALTER TABLE session_questions ADD COLUMN hints_used INTEGER NOT NULL DEFAULT 0;",
            "CREATE INDEX IF NOT EXISTS idx_sessions_profile ON sessions(profile_name);",
            "CREATE INDEX IF NOT EXISTS idx_session_questions_session ON session_questions(session_id);",
            "PRAGMA user_version = 2;"
    };

    private SchemaMigrations() {
    }

    /**
     * Upgrades the database to {@link #CURRENT_VERSION} in one transaction and returns the version
     * it is at afterwards. A database newer than this app is left untouched. If any step fails, the
     * whole upgrade is rolled back, one line is logged (no personal data) and the old version is returned.
     *
     * @throws SQLException only if the current version cannot be read
     */
    static int migrate(Connection conn) throws SQLException {
        int version = userVersion(conn);
        if (version >= CURRENT_VERSION) {
            return version;
        }
        boolean autoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            if (version < 1) {
                runAll(st, TO_V1);
            }
            runAll(st, TO_V2);
            conn.commit();
            return CURRENT_VERSION;
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Database upgrade failed; keeping schema version " + version + ": " + e.getMessage());
            return version;
        } finally {
            conn.setAutoCommit(autoCommit);
        }
    }

    static int userVersion(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA user_version;")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void runAll(Statement st, String[] statements) throws SQLException {
        for (String sql : statements) {
            st.execute(sql);
        }
    }
}
