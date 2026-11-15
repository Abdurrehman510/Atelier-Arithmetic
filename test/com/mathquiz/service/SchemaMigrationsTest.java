package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** DEBT-11: PRAGMA user_version migrations never lose history. */
class SchemaMigrationsTest {

    private static final String PROFILE = "Guest";

    @TempDir
    Path dir;

    private Path db() {
        return dir.resolve("atelier_arithmetic.db");
    }

    private SessionRepository open() {
        return new SessionRepository(db().toString(), () -> PROFILE);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> questionsOf(Map<String, Object> session) {
        return (List<Map<String, Object>>) session.get("questions");
    }

    private static QuizSession oneQuestionSession() {
        QuizSession session = new QuizSession(1, "Easy", "Addition");
        session.recordResult(Results.correct("20 + 22", 42));
        return session;
    }

    @Test
    void freshDatabaseIsCreatedAtCurrentVersion() throws Exception {
        open();
        assertEquals(2, LegacyDatabaseFixture.userVersion(db()));
        assertTrue(LegacyDatabaseFixture.columns(db(), "session_questions")
                .containsAll(List.of("skill_id", "difficulty", "hints_used")));
        assertTrue(LegacyDatabaseFixture.indexNames(db())
                .containsAll(List.of("idx_sessions_profile", "idx_session_questions_session")));
    }

    @Test
    void legacyDatabaseUpgradesAndKeepsHistory() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        SessionRepository repo = open();

        assertEquals(2, LegacyDatabaseFixture.userVersion(db()));
        List<Map<String, Object>> sessions = repo.loadRaw();
        assertEquals(1, sessions.size());
        assertEquals("Addition", sessions.get(0).get("category"));
        List<Map<String, Object>> questions = questionsOf(sessions.get(0));
        assertEquals("12 + 30", questions.get(0).get("expression"));
        assertEquals(true, questions.get(0).get("correct"));
        assertEquals("50 - 8", questions.get(1).get("expression"));
        assertEquals(false, questions.get(1).get("correct"));
        assertEquals(Arrays.asList(null, null), LegacyDatabaseFixture.questionColumn(db(), "skill_id"));
        assertEquals(Arrays.asList(null, null), LegacyDatabaseFixture.questionColumn(db(), "difficulty"));
        assertEquals(List.of(0, 0), LegacyDatabaseFixture.questionColumn(db(), "hints_used"));
    }

    @Test
    void openingTwiceIsHarmless() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        open();
        SessionRepository again = open();
        assertEquals(2, LegacyDatabaseFixture.userVersion(db()));
        assertEquals(1, again.loadRaw().size());
    }

    @Test
    void newerDatabaseIsLeftAlone() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        open();
        LegacyDatabaseFixture.execute(db(), "PRAGMA user_version = 3");

        SessionRepository repo = open();
        assertEquals(3, repo.getSchemaVersion());
        assertEquals(3, LegacyDatabaseFixture.userVersion(db()));
        repo.save(oneQuestionSession());
        assertEquals(2, repo.loadRaw().size());
    }

    @Test
    void failedMigrationKeepsHistoryReadable() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        LegacyDatabaseFixture.execute(db(), "ALTER TABLE session_questions ADD COLUMN skill_id TEXT");

        SessionRepository repo = open();
        assertEquals(0, repo.getSchemaVersion());
        List<Map<String, Object>> sessions = repo.loadRaw();
        assertEquals(1, sessions.size());
        assertEquals(2, questionsOf(sessions.get(0)).size());
    }
}
