package com.mathquiz.view;

import com.mathquiz.model.QuizSession;
import com.mathquiz.testutil.Results;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** BUG-12: the printed report covers every question, across as many pages as needed. */
class ReportPrintableTest {

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
        List<List<String>> pages = printable.pages(new PageFormat().getImageableHeight());

        assertTrue(pages.size() >= 2, "45 questions should not fit on one page");
        List<String> all = new ArrayList<>();
        pages.forEach(all::addAll);
        assertEquals(printable.lines(), all);
        assertTrue(all.stream().anyMatch(l -> l.startsWith("45.  44 + 1 = 45")), "last question missing");
    }

    @Test
    void printDrawsEachPageThenStops() throws Exception {
        ReportPrintable printable = new ReportPrintable(sessionWith(45));
        PageFormat format = new PageFormat();
        int pageCount = printable.pages(format.getImageableHeight()).size();
        BufferedImage image = new BufferedImage(612, 792, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            for (int i = 0; i < pageCount; i++) {
                assertEquals(Printable.PAGE_EXISTS, printable.print(g, format, i));
            }
            assertEquals(Printable.NO_SUCH_PAGE, printable.print(g, format, pageCount));
        } finally {
            g.dispose();
        }
    }

    @Test
    void shortReportsFitOnOnePage() {
        assertEquals(1, new ReportPrintable(sessionWith(5)).pages(new PageFormat().getImageableHeight()).size());
    }
}
