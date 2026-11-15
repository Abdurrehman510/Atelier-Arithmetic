package com.mathquiz.model;

import com.mathquiz.testutil.Results;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pins the grade and remark thresholds of PRD BR-01 (QuizSession). */
class QuizSessionGradeTest {

    private static QuizSession sessionWith(int correct, int total) {
        QuizSession session = new QuizSession(total, "Easy", "Addition");
        for (int i = 0; i < total; i++) {
            if (i < correct) {
                session.recordResult(Results.correct("1 + 1", 2));
            } else {
                session.recordResult(Results.wrong("1 + 1", 2, 3));
            }
        }
        return session;
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', value = {
            "19|20|A++", "94|100|A+", "17|20|A+", "84|100|A", "39|50|A", "77|100|B+",
            "13|20|B+", "53|100|B", "2|5|C+", "1|3|C", "33|100|D", "0|10|D"})
    void gradeFollowsThePrdThresholds(int correct, int total, String grade) {
        assertEquals(grade, sessionWith(correct, total).getGrade());
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', quoteCharacter = '"', value = {
            "19|20|Outstanding — True Mastery!", "17|20|Excellent — Impressive Skills!",
            "39|50|Very Good — Strong Performance!", "13|20|Good — Solid Foundation!",
            "53|100|Progressing — Keep Practicing!", "2|5|Developing — You're Learning!",
            "1|3|Getting Started — Stay Curious!", "33|100|Room to Grow — Try an Easier Level!"})
    void remarkStaysGrowthMinded(int correct, int total, String remark) {
        assertEquals(remark, sessionWith(correct, total).getRemarks());
    }
}
