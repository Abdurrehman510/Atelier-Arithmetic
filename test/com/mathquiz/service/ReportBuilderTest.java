package com.mathquiz.service;

import com.mathquiz.model.QuizSession;
import com.mathquiz.model.Skill;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-11: every dynamic value in the exported HTML report is escaped. */
class ReportBuilderTest {

    @Test
    void customQuizTextIsEscaped() {
        QuizSession session = new QuizSession(1, "Medium", "Custom: <i>Tables</i>");
        session.recordResult(Results.of("<b>x</b> & \"y\"", 7, 7, Skill.CUSTOM, "Medium", 0));

        String html = ReportBuilder.toHtml(session, "");

        assertTrue(html.contains("&lt;b&gt;x&lt;/b&gt; &amp; &quot;y&quot;"), "expression not escaped");
        assertTrue(html.contains("Custom: &lt;i&gt;Tables&lt;/i&gt;"), "category not escaped");
        assertFalse(html.contains("<b>x</b>"));
        assertFalse(html.contains("<i>Tables</i>"));
    }

    @Test
    void escapeHandlesTheFiveSpecialCharacters() {
        assertEquals("&lt;&gt;&amp;&quot;&#39;", ReportBuilder.escape("<>&\"'"));
        assertEquals("", ReportBuilder.escape(null));
    }

    @Test
    void writingToAnInvalidFileNameReportsFailureInsteadOfThrowing(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) {
        java.io.File invalid = new java.io.File(dir.toFile(), "bad\0name.html");   // NUL is invalid on every OS
        assertFalse(ReportBuilder.writeReport(invalid, "<html></html>"));
    }

    @Test
    void writingAReportSavesItAsUtf8(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) throws Exception {
        java.io.File file = new java.io.File(dir.toFile(), "report.html");
        assertTrue(ReportBuilder.writeReport(file, "<p>🦉 ★</p>"));
        assertEquals("<p>🦉 ★</p>", java.nio.file.Files.readString(file.toPath(), java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    void reportListsEveryQuestion() {
        QuizSession session = new QuizSession(25, "Easy", "Addition");
        for (int i = 0; i < 25; i++) {
            session.recordResult(Results.correct(i + " + 1", i + 1));
        }
        String html = ReportBuilder.toHtml(session, "");
        assertTrue(html.contains("<td>24 + 1</td>"));
        assertEquals(25, html.split("<tr class=").length - 1);
    }
}
