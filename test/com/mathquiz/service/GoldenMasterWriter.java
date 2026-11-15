package com.mathquiz.service;

import com.mathquiz.model.Question;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the golden-master fixture of seeded generator output.
 * Run once from a known-good commit; the fixture is then committed and never edited by hand.
 *
 * Usage: java GoldenMasterWriter &lt;output.tsv&gt; &lt;git short hash&gt;
 */
public final class GoldenMasterWriter {

    static final String[] CATEGORIES = {
            "Addition", "Difference", "Multiplication", "Division", "Mixed",
            "Special", "Fractions", "Patterns", "Algebra", "Measurement"};
    static final String[] DIFFICULTIES = {"Easy", "Medium", "Hard"};
    static final long MAIN_SEED_BASE = 20260101L;
    static final int SAMPLES_PER_COMBINATION = 50;
    static final long DAILY_SEED = 20261108L;

    private GoldenMasterWriter() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("usage: GoldenMasterWriter <output.tsv> <git short hash>");
        }
        QuestionGenerator generator = new QuestionGenerator();
        try (Writer out = Files.newBufferedWriter(Path.of(args[0]), StandardCharsets.UTF_8)) {
            out.write("# category\tdifficulty\tseed\tindex\texpression\tanswer — generated from "
                    + args[1] + "; do not edit by hand\n");
            for (String category : CATEGORIES) {
                for (String difficulty : DIFFICULTIES) {
                    for (int k = 0; k < SAMPLES_PER_COMBINATION; k++) {
                        writeRow(out, generator, category, difficulty, MAIN_SEED_BASE + k, k % 10);
                    }
                }
            }
            for (int index = 0; index < 10; index++) {
                writeRow(out, generator, "Mixed", "Medium", DAILY_SEED, index);
            }
        }
    }

    private static void writeRow(Writer out, QuestionGenerator generator, String category,
                                 String difficulty, long seed, int index) throws IOException {
        Question q = generator.generateSeeded(difficulty, category, seed, index);
        out.write(category + "\t" + difficulty + "\t" + seed + "\t" + index + "\t"
                + q.getExpression() + "\t" + q.getCorrectAnswer() + "\n");
    }
}
