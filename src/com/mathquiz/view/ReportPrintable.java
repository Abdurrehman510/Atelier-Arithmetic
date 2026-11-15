package com.mathquiz.view;

import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plain-text printout of a session (PRD FR-EXP-02). Lists every question, wraps lines that are too
 * wide for the page (word problems), and continues onto as many pages as needed (BUG-12).
 */
final class ReportPrintable implements Printable {

    /** Vertical distance between printed lines, in points. */
    static final int LINE_HEIGHT = 20;
    /** Baseline of the first line below the top of the printable area, in points. */
    static final int TOP = 30;
    /** Space kept below the last baseline for descenders (g, p, y), in points. */
    static final int DESCENT_ROOM = 4;
    /** Left and right insets inside the printable area, in points. */
    static final int LEFT = 36;
    static final int RIGHT = 12;
    /** Continuation lines of a wrapped line are indented by this text. */
    static final String INDENT = "      ";

    static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 18);
    static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 12);
    static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 11);

    private static final String TITLE = "Atelier Arithmetic Dashboard Report";
    private static final String LIST_HEADING = "Question List Summary:";
    private static final FontRenderContext FRC = new FontRenderContext(null, true, true);

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

    /** Every logical line of the printout, in order (before wrapping). */
    List<String> lines() {
        return lines;
    }

    /** The lines as printed on a page of the given printable width: long lines wrapped at spaces. */
    List<String> printedLines(double imageableWidth) {
        double usable = imageableWidth - LEFT - RIGHT;
        List<String> printed = new ArrayList<>();
        for (String line : lines) {
            printed.addAll(wrap(line, fontFor(line), usable));
        }
        return printed;
    }

    /** The printed lines split into pages for a printable area of the given size (points). */
    List<List<String>> pages(double imageableWidth, double imageableHeight) {
        int perPage = Math.max(1, (int) ((imageableHeight - TOP - DESCENT_ROOM) / LINE_HEIGHT) + 1);
        List<String> printed = printedLines(imageableWidth);
        List<List<String>> pages = new ArrayList<>();
        for (int from = 0; from < printed.size(); from += perPage) {
            pages.add(printed.subList(from, Math.min(printed.size(), from + perPage)));
        }
        return pages;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        List<List<String>> pages = pages(pageFormat.getImageableWidth(), pageFormat.getImageableHeight());
        if (pageIndex >= pages.size()) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setColor(Color.BLACK);
        int y = TOP;
        for (String line : pages.get(pageIndex)) {
            g2d.setFont(fontFor(line));
            g2d.drawString(line, LEFT, y);
            y += LINE_HEIGHT;
        }
        return PAGE_EXISTS;
    }

    private static Font fontFor(String line) {
        if (line.equals(TITLE)) {
            return TITLE_FONT;
        }
        return line.equals(LIST_HEADING) ? HEADING_FONT : BODY_FONT;
    }

    private static double width(String text, Font font) {
        return font.getStringBounds(text, FRC).getWidth();
    }

    /** Greedy word wrap; continuation lines are indented; a single over-long word is split by characters. */
    private static List<String> wrap(String line, Font font, double usable) {
        List<String> out = new ArrayList<>();
        if (width(line, font) <= usable) {
            out.add(line);
            return out;
        }
        StringBuilder current = new StringBuilder();
        for (String word : line.split(" ")) {
            String candidate = current.length() == 0
                    ? (out.isEmpty() ? word : INDENT + word)
                    : current + " " + word;
            if (width(candidate, font) <= usable) {
                current.setLength(0);
                current.append(candidate);
                continue;
            }
            if (current.length() > 0) {
                out.add(current.toString());
                current.setLength(0);
            }
            String start = out.isEmpty() ? word : INDENT + word;
            while (width(start, font) > usable && start.length() > INDENT.length() + 1) {
                int cut = start.length() - 1;
                while (cut > INDENT.length() + 1 && width(start.substring(0, cut), font) > usable) {
                    cut--;
                }
                out.add(start.substring(0, cut));
                start = INDENT + start.substring(cut);
            }
            current.append(start);
        }
        if (current.length() > 0) {
            out.add(current.toString());
        }
        return out;
    }
}
