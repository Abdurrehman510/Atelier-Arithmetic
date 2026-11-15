package com.mathquiz.service;

import com.mathquiz.model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-10: custom quizzes survive any text, name clashes are detectable, failed saves are reported. */
class CustomQuizServiceTest {

    @TempDir
    Path dir;

    @Test
    void expressionsWithQuotesBackslashesAndBracesRoundTrip() {
        CustomQuizService service = new CustomQuizService(dir.toString());
        List<Question> quiz = List.of(
                new Question("What is \"7 x 8\"?", 56),
                new Question("10 \\ 2 = ?", 5),
                new Question("{3 + 4} * 2", 14));
        assertTrue(service.saveQuiz("Tricky", quiz));

        List<Question> loaded = service.loadQuiz("Tricky");
        assertEquals(3, loaded.size());
        for (int i = 0; i < quiz.size(); i++) {
            assertEquals(quiz.get(i).getExpression(), loaded.get(i).getExpression());
            assertEquals(quiz.get(i).getCorrectAnswer(), loaded.get(i).getCorrectAnswer());
        }
    }

    @Test
    void namesThatShareAFileAreReportedAsExisting() {
        CustomQuizService service = new CustomQuizService(dir.toString());
        assertFalse(service.quizExists("Quiz 1"));
        assertTrue(service.saveQuiz("Quiz 1", List.of(new Question("1 + 1", 2))));
        assertTrue(service.quizExists("Quiz 1"));
        assertTrue(service.quizExists("Quiz_1"), "\"Quiz 1\" and \"Quiz_1\" are stored in the same file");
        assertFalse(service.quizExists("Quiz 2"));
    }

    @Test
    void aFailedWriteIsReportedAsFailure() throws Exception {
        Path notADirectory = dir.resolve("blocker");
        Files.writeString(notADirectory, "a file where the quiz folder should be");
        CustomQuizService service = new CustomQuizService(notADirectory.toString());
        assertFalse(service.saveQuiz("Anything", List.of(new Question("1 + 1", 2))));
    }
}
