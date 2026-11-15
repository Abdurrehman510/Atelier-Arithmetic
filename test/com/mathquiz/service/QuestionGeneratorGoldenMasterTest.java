package com.mathquiz.service;

import com.mathquiz.model.Question;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Seeded generator output must never change (R-DOM-04, Daily Challenge fairness).
 * The fixture was written by {@link GoldenMasterWriter} before the generator refactor.
 */
class QuestionGeneratorGoldenMasterTest {

    private static final Path FIXTURE = Path.of("test/resources/golden/question-generator-v1.tsv");

    @Test
    void seededOutputMatchesTheGoldenMaster() throws IOException {
        List<String> rows = Files.readAllLines(FIXTURE, StandardCharsets.UTF_8).stream()
                .filter(line -> !line.startsWith("#"))
                .toList();
        assertEquals(1510, rows.size(), "fixture truncated");

        QuestionGenerator generator = new QuestionGenerator();
        for (String row : rows) {
            String[] f = row.split("\t");
            Question q = generator.generateSeeded(f[1], f[0], Long.parseLong(f[2]), Integer.parseInt(f[3]));
            assertEquals(f[4], q.getExpression(), row);
            assertEquals(Integer.parseInt(f[5]), q.getCorrectAnswer(), row);
        }
    }
}
