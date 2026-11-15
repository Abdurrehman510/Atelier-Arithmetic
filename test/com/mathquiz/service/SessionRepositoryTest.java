package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import com.mathquiz.model.Skill;
import com.mathquiz.testutil.Results;
import com.mathquiz.util.CryptoHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** FEAT-02 storage: skill, per-question difficulty and hint presses survive save → load. */
class SessionRepositoryTest {

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

    @Test
    void savedQuestionsRoundTripSkillDifficultyAndHints() {
        SessionRepository repo = open();
        QuizSession session = new QuizSession(1, "Easy", "Addition");
        session.recordResult(Results.of("12 + 30", 42, 42, Skill.ADD_2DIGIT_2TERMS, "Easy", 2));
        repo.save(session);

        Map<String, Object> q = questionsOf(repo.loadRaw().get(0)).get(0);
        assertEquals("add.2digit.2terms", q.get("skillId"));
        assertEquals("Easy", q.get("difficulty"));
        assertEquals(2, q.get("hintsUsed"));
    }

    @Test
    void customQuizSessionSavesWithCustomSkill() {
        SessionRepository repo = open();
        QuizSession session = new QuizSession(1, "Medium", "Custom: Times");
        session.recordResult(Results.of("7 * 8", 56, 56, Skill.CUSTOM, "Medium", 0));
        repo.save(session);

        Map<String, Object> saved = repo.loadRaw().get(0);
        assertEquals("Custom: Times", saved.get("category"));
        assertEquals("custom", questionsOf(saved).get(0).get("skillId"));
    }

    @Test
    void skillAndDifficultyAreNotStoredInPlainText() throws Exception {
        SessionRepository repo = open();
        QuizSession session = new QuizSession(1, "Easy", "Addition");
        session.recordResult(Results.of("12 + 30", 42, 42, Skill.ADD_2DIGIT_2TERMS, "Easy", 0));
        repo.save(session);

        String rawSkill = (String) LegacyDatabaseFixture.questionColumn(db(), "skill_id").get(0);
        String rawDifficulty = (String) LegacyDatabaseFixture.questionColumn(db(), "difficulty").get(0);
        assertNotEquals("add.2digit.2terms", rawSkill);
        assertEquals("add.2digit.2terms", CryptoHelper.decrypt(rawSkill));
        assertNotEquals("Easy", rawDifficulty);
        assertEquals("Easy", CryptoHelper.decrypt(rawDifficulty));
    }

    @Test
    void legacyRowsLoadWithNullSkillAndZeroHints() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        List<Map<String, Object>> questions = questionsOf(open().loadRaw().get(0));
        for (Map<String, Object> q : questions) {
            assertNull(q.get("skillId"));
            assertNull(q.get("difficulty"));
            assertEquals(0, q.get("hintsUsed"));
        }
    }

    @Test
    void legacyModeStillSavesAndLoads() throws Exception {
        LegacyDatabaseFixture.createV1(db(), PROFILE);
        LegacyDatabaseFixture.execute(db(), "ALTER TABLE session_questions ADD COLUMN skill_id TEXT");
        SessionRepository repo = open();
        QuizSession session = new QuizSession(1, "Easy", "Addition");
        session.recordResult(Results.correct("20 + 22", 42));
        repo.save(session);

        List<Map<String, Object>> sessions = repo.loadRaw();
        assertEquals(2, sessions.size());
        assertNull(questionsOf(sessions.get(0)).get(0).get("skillId"));
    }
}
