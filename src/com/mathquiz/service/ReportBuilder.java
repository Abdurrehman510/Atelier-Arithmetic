package com.mathquiz.service;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import java.util.List;

/**
 * Builds the self-contained HTML session report saved by "Export Report" (PRD FR-EXP).
 * Every dynamic value is HTML-escaped (rules.md R-SEC-04, BUG-11): custom quiz names and
 * expressions are typed by a parent and may contain {@code < > & " '}.
 */
public final class ReportBuilder {

    private ReportBuilder() {
    }

    /**
     * Writes the report as UTF-8. Returns false instead of throwing when the file cannot be written,
     * including file names the operating system rejects (e.g. {@code < > | "} on Windows).
     */
    public static boolean writeReport(java.io.File dest, String html) {
        try {
            java.nio.file.Files.writeString(dest.toPath(), html, java.nio.charset.StandardCharsets.UTF_8);
            return true;
        } catch (java.io.IOException | java.nio.file.InvalidPathException e) {
            System.err.println("Failed to write report: " + e.getClass().getSimpleName());
            return false;
        }
    }

    /** Escapes text for HTML element content and quoted attributes; {@code null} becomes "". */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * @param base64Logo the logo as Base64 (embedded as a data URI), or "" to omit it
     */
    public static String toHtml(QuizSession session, String base64Logo) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Atelier Arithmetic Dashboard Report</title>");
        html.append("<style>");
        html.append("body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #FAF9F6; color: #1C1917; padding: 40px; }");
        html.append(".card { background: white; border: 1px solid #E6E2DC; border-radius: 12px; padding: 30px; max-width: 700px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.02); }");
        html.append("h1 { font-family: Georgia, serif; color: #B8966E; margin-bottom: 5px; }");
        html.append(".subtitle { color: #78716C; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 25px; }");
        html.append(".badge { font-size: 48px; margin-right: 15px; }");
        html.append(".grade-box { display: flex; align-items: center; background: #FAF9F6; border: 1px solid #E6E2DC; border-radius: 8px; padding: 20px; margin-bottom: 25px; }");
        html.append(".grade-text { font-size: 24px; font-weight: bold; color: #1C1917; }");
        html.append(".grade-sub { font-size: 14px; color: #78716C; margin-top: 4px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
        html.append("th, td { border-bottom: 1px solid #E6E2DC; padding: 12px; text-align: left; font-size: 13px; }");
        html.append("th { background-color: #FAF9F6; color: #78716C; font-weight: bold; }");
        html.append(".correct { background-color: #ECFDF5; color: #065F46; }");
        html.append(".incorrect { background-color: #FEF2F2; color: #991B1B; }");
        html.append("</style></head><body>");

        html.append("<div class='card'>");
        if (base64Logo != null && !base64Logo.isEmpty()) {
            html.append("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;'>");
            html.append("<div>");
            html.append("<h1>Atelier Arithmetic Dashboard</h1>");
            html.append("<div class='subtitle'>Bespoke Performance Report</div>");
            html.append("</div>");
            html.append("<img src='data:image/png;base64,").append(escape(base64Logo))
                    .append("' style='height: 60px; width: 60px; object-fit: contain; border-radius: 8px;' />");
            html.append("</div>");
        } else {
            html.append("<h1>Atelier Arithmetic Dashboard</h1>");
            html.append("<div class='subtitle'>Bespoke Performance Report</div>");
        }

        html.append("<div class='grade-box'>");
        html.append("<span class='badge'>").append(escape(session.getGradeEmoji())).append("</span>");
        html.append("<div>");
        html.append("<div class='grade-text'>Grade ").append(escape(session.getGrade()))
                .append(" (").append((int) session.getPercentage()).append("%)</div>");
        html.append("<div class='grade-sub'>Remarks: ").append(escape(session.getRemarks())).append("</div>");
        html.append("</div></div>");

        html.append("<p><b>Category:</b> ").append(escape(session.getCategory())).append("</p>");
        html.append("<p><b>Difficulty:</b> ").append(escape(session.getDifficulty())).append("</p>");
        html.append("<p><b>Total Questions:</b> ").append(session.getTotalQuestions()).append("</p>");
        html.append("<p><b>Correct Answers:</b> ").append(session.getCorrectAnswersCount()).append("</p>");
        html.append("<p><b>Total Duration:</b> ").append(session.getDurationMs() / 1000).append(" seconds</p>");

        html.append("<h2>Session Details</h2>");
        html.append("<table><thead><tr><th>#</th><th>Expression</th><th>Your Answer</th><th>Correct Answer</th><th>Time</th></tr></thead><tbody>");
        List<QuestionResult> results = session.getResults();
        for (int i = 0; i < results.size(); i++) {
            QuestionResult r = results.get(i);
            html.append("<tr class='").append(r.isCorrect() ? "correct" : "incorrect").append("'>");
            html.append("<td>").append(i + 1).append("</td>");
            html.append("<td>").append(escape(r.getExpression())).append("</td>");
            html.append("<td>").append(r.getUserAnswer()).append("</td>");
            html.append("<td>").append(r.getCorrectAnswer()).append("</td>");
            html.append("<td>").append(r.getTimeSpentMs() / 1000).append("s</td>");
            html.append("</tr>");
        }
        html.append("</tbody></table>");
        html.append("</div></body></html>");
        return html.toString();
    }
}
