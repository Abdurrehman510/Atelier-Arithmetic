package com.mathquiz.view;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-12: the printed report covers every question in full, across as many pages as needed. */
class ReportPrintableTest {

    private static final PageFormat LETTER = new PageFormat();   // 612 × 792 pt, 72 pt margins
    private static final double W = LETTER.getImageableWidth();
    private static final double H = LETTER.getImageableHeight();

    private static QuizSession sessionWith(int questions) {
        QuizSession s = new QuizSession(questions, "Easy", "Addition");
        for (int i = 0; i < questions; i++) {
            s.recordResult(Results.correct(i + " + 1", i + 1));
        }
        return s;
    }

    @Test
    void longReportsSpillOntoMorePagesAndKeepEveryQuestion() {
        ReportPrintable printable = new ReportPrintable(sessionWith(45));
        List<List<String>> pages = printable.pages(W, H);

        assertTrue(pages.size() >= 2, "45 questions should not fit on one page");
        List<String> all = new ArrayList<>();
        pages.forEach(all::addAll);
        assertEquals(printable.printedLines(W), all);
        assertTrue(all.stream().anyMatch(l -> l.startsWith("45.  44 + 1 = 45")), "last question missing");
    }

    @Test
    void printDrawsEachPageThenStops() throws Exception {
        ReportPrintable printable = new ReportPrintable(sessionWith(45));
        int pageCount = printable.pages(W, H).size();
        BufferedImage image = new BufferedImage(612, 792, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            for (int i = 0; i < pageCount; i++) {
                assertEquals(Printable.PAGE_EXISTS, printable.print(g, LETTER, i));
            }
            assertEquals(Printable.NO_SUCH_PAGE, printable.print(g, LETTER, pageCount));
        } finally {
            g.dispose();
        }
    }

    @Test
    void shortReportsFitOnOnePage() {
        assertEquals(1, new ReportPrintable(sessionWith(5)).pages(W, H).size());
    }

    /** Review finding: word problems ran past the right margin, cutting off the answer and mark. */
    @Test
    void longWordProblemLinesWrapInsteadOfBeingCutOff() {
        String problem = "A rectangular field has an area of 120 square meters and a length of 15 meters. "
                + "What is its perimeter in meters?";
        QuizSession s = new QuizSession(1, "Hard", "Measurement");
        s.recordResult(Results.correct(problem, 46));
        ReportPrintable printable = new ReportPrintable(s);

        FontRenderContext frc = new FontRenderContext(null, true, true);
        double usable = W - ReportPrintable.LEFT - ReportPrintable.RIGHT;
        List<String> printed = printable.printedLines(W);
        for (String line : printed) {
            double width = ReportPrintable.BODY_FONT.getStringBounds(line, frc).getWidth();
            assertTrue(width <= usable, () -> "line too wide (" + width + " > " + usable + "): " + line);
        }
        String joined = String.join(" ", printed).replaceAll("\\s+", " ");
        assertTrue(joined.contains("(Correct - Correct: 46)"), "answer and mark must still be printed: " + joined);
    }

    /** Review finding: a baseline exactly at the bottom edge clips letters such as g, p, y. */
    @Test
    void lastLineOnAPageLeavesRoomForDescenders() {
        double height = 630;   // (height - TOP) is an exact multiple of LINE_HEIGHT
        int perPage = new ReportPrintable(sessionWith(45)).pages(W, height).get(0).size();
        double lastBaseline = ReportPrintable.TOP + (perPage - 1) * ReportPrintable.LINE_HEIGHT;
        assertTrue(lastBaseline + ReportPrintable.DESCENT_ROOM <= height, "last baseline at " + lastBaseline);
    }
}
