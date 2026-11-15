package com.mathquiz.view;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plain-text printout of a session (PRD FR-EXP-02). Lists every question and continues onto
 * as many pages as needed (BUG-12: the old printout stopped after 20 questions on one page).
 */
final class ReportPrintable implements Printable {

    /** Vertical distance between printed lines, in points. */
    static final int LINE_HEIGHT = 20;
    /** Baseline of the first line below the top of the printable area, in points. */
    static final int TOP = 30;
    private static final int LEFT = 36;
    private static final String TITLE = "Atelier Arithmetic Dashboard Report";
    private static final String LIST_HEADING = "Question List Summary:";

    private final List<String> lines;

    ReportPrintable(QuizSession session) {
        List<String> all = new ArrayList<>();
        all.add(TITLE);
        all.add("Category: " + session.getCategory());
        all.add("Difficulty: " + session.getDifficulty());
        all.add("Grade: " + session.getGrade());
        all.add("Success Rate: " + (int) session.getPercentage() + "%");
        all.add("Correct Answers: " + session.getCorrectAnswersCount() + " / " + session.getTotalQuestions());
        all.add("");
        all.add(LIST_HEADING);
        List<QuestionResult> results = session.getResults();
        for (int i = 0; i < results.size(); i++) {
            QuestionResult r = results.get(i);
            String mark = r.isCorrect() ? "Correct" : "Wrong";
            all.add((i + 1) + ".  " + r.getExpression() + " = " + r.getUserAnswer()
                    + "  (" + mark + " - Correct: " + r.getCorrectAnswer() + ")");
        }
        this.lines = Collections.unmodifiableList(all);
    }

    /** Every line of the printout, in order. */
    List<String> lines() {
        return lines;
    }

    /** The lines split into pages for a printable area of the given height (points). */
    List<List<String>> pages(double imageableHeight) {
        int perPage = Math.max(1, (int) ((imageableHeight - TOP) / LINE_HEIGHT) + 1);
        List<List<String>> pages = new ArrayList<>();
        for (int from = 0; from < lines.size(); from += perPage) {
            pages.add(lines.subList(from, Math.min(lines.size(), from + perPage)));
        }
        return pages;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        List<List<String>> pages = pages(pageFormat.getImageableHeight());
        if (pageIndex >= pages.size()) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setColor(Color.BLACK);
        int y = TOP;
        for (String line : pages.get(pageIndex)) {
            if (line.equals(TITLE)) {
                g2d.setFont(new Font("Serif", Font.BOLD, 18));
            } else if (line.equals(LIST_HEADING)) {
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            } else {
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            }
            g2d.drawString(line, LEFT, y);
            y += LINE_HEIGHT;
        }
        return PAGE_EXISTS;
    }
}
