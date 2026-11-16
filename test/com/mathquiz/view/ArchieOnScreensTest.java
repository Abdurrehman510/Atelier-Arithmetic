package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.model.QuestionResult;
import com.mathquiz.model.QuizSession;
import com.mathquiz.model.Skill;
import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Where a child sees what they bought (BUG-22): Archie, wearing the equipped item, on Home, on the
 * quiz screen (reacting to answers, as the tour promises) and on the results screen (celebrating a
 * high score); every shop card previews Archie wearing that item. He is never clipped.
 */
class ArchieOnScreensTest {

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    /** Problems if {@code c} is hidden, squeezed below its preferred size or pokes out of a parent. */
    private static List<String> notFullyShown(Component c, Container root) {
        List<String> problems = new ArrayList<>();
        if (c.getWidth() < c.getPreferredSize().width || c.getHeight() < c.getPreferredSize().height) {
            problems.add("Archie squeezed to " + c.getWidth() + "x" + c.getHeight() + ", needs " + c.getPreferredSize());
        }
        for (Component p = c; p != root && p != null; p = p.getParent()) {
            Container parent = p.getParent();
            if (!p.isVisible()) {
                problems.add(p.getClass().getSimpleName() + " hidden");
            }
            if (parent != null && !(parent instanceof javax.swing.JViewport)
                    && (p.getX() < 0 || p.getY() < 0 || p.getX() + p.getWidth() > parent.getWidth()
                    || p.getY() + p.getHeight() > parent.getHeight())) {
                problems.add(p.getClass().getSimpleName() + " sticks out of " + parent.getClass().getSimpleName());
            }
        }
        return problems;
    }

    @Test
    void homeShowsArchieWearingTheChildsItem() throws Exception {
        AppConfig config = AppConfig.getInstance();
        String old = config.getEquippedItem();
        config.setEquippedItem("hat_crown");
        try {
            String seen = FrameHarness.inspect(720, 550, 1.25, frame -> {
                WelcomePanel home = FrameHarness.find(frame.getContentPane(), WelcomePanel.class);
                ArchieView archie = FrameHarness.find(home, ArchieView.class);
                assertNotNull(archie, "no Archie on Home");
                return archie.itemId() + " " + notFullyShown(archie, home);
            });
            assertEquals("hat_crown []", seen);
        } finally {
            config.setEquippedItem(old);
        }
    }

    private static int solve(String shown) {            // "12 + 7 = ?" → 19 (addition quizzes only)
        int sum = 0;
        for (String term : shown.replace("= ?", "").split("\\+")) {
            sum += Integer.parseInt(term.trim());
        }
        return sum;
    }

    @Test
    void archieReactsToAnswersDuringAQuiz() throws Exception {
        List<String> seen = FrameHarness.inspect(720, 550, 1.25, frame -> {
            frame.startQuiz("addition", 3, "Easy");
            frame.validate();
            GamePanel game = FrameHarness.find(frame.getContentPane(), GamePanel.class);
            ArchieView archie = FrameHarness.find(game, ArchieView.class);
            assertNotNull(archie, "no Archie on the quiz screen");
            List<String> s = new ArrayList<>(notFullyShown(archie, game));
            s.add(archie.getExpression().name());                                   // new question
            game.getAnswerField().setText(String.valueOf(solve(game.getExpressionLabel().getText()) + 1));
            game.getSubmitButton().doClick();
            s.add(archie.getExpression().name());                                   // wrong answer
            game.getSubmitButton().doClick();
            s.add(archie.getExpression().name());                                   // next question
            game.getAnswerField().setText(String.valueOf(solve(game.getExpressionLabel().getText())));
            game.getSubmitButton().doClick();
            s.add(archie.getExpression().name());                                   // right answer
            frame.goToWelcome();                                                    // stops the question timer
            return s;
        });
        assertEquals(List.of("HAPPY", "THINKING", "HAPPY", "EXCITED"), seen);
    }

    private static QuizSession session(int correct, int total) {
        QuizSession s = new QuizSession(total, "Easy", "addition");
        for (int i = 0; i < total; i++) {
            boolean right = i < correct;
            s.recordResult(new QuestionResult("1 + 1", 2, right ? 2 : 3, right, 1000,
                    Skill.values()[0], "Easy", 0));
        }
        return s;
    }

    @Test
    void archieCelebratesAHighScoreOnTheResultsScreen() throws Exception {
        List<String> seen = FrameHarness.inspect(720, 550, 1.25, frame -> {
            ResultsPanel results = FrameHarness.find(frame.getContentPane(), ResultsPanel.class);
            ArchieView archie = FrameHarness.find(results, ArchieView.class);
            assertNotNull(archie, "no Archie on the results screen");
            List<String> s = new ArrayList<>();
            for (int correct : new int[]{9, 6, 2}) {
                results.populate(session(correct, 10));
                s.add(archie.getExpression().name());
            }
            results.setSize(frame.getContentPane().getSize());   // lay the hidden card out as if shown
            results.validate();
            s.addAll(notFullyShown(archie, results));
            return s;
        });
        assertEquals(List.of("EXCITED", "HAPPY", "THINKING"), seen);
    }

    @Test
    void everyShopCardPreviewsArchieWearingThatItem() throws Exception {
        Set<String> previews = FrameHarness.inspect(1024, 700, 1.0, frame -> {
            frame.showMascotShop();
            frame.validate();
            MascotShopPanel shop = FrameHarness.find(frame.getContentPane(), MascotShopPanel.class);
            Set<String> ids = new TreeSet<>();
            for (ArchieView v : FrameHarness.findAll(shop, ArchieView.class)) {
                ids.add(v.itemId());
            }
            return ids;
        });
        Set<String> expected = new TreeSet<>();
        for (MascotShopPanel.ShopItem item : MascotShopPanel.ALL_ITEMS) {
            expected.add(item.id);
        }
        assertEquals(expected, previews);
    }

    private static List<String> shopProblems(int width, int height, double scale) throws Exception {
        return FrameHarness.inspect(width, height, scale, frame -> {
            frame.showMascotShop();
            frame.validate();
            frame.validate();
            MascotShopPanel shop = FrameHarness.find(frame.getContentPane(), MascotShopPanel.class);
            return FrameHarness.clippedComponents(shop);
        });
    }

    @Test
    void shopFitsTheSmallestWindowAt125Percent() throws Exception {
        List<String> problems = shopProblems(720, 550, 1.25);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void shopFitsAt150Percent() throws Exception {
        List<String> problems = shopProblems(1024, 700, 1.5);
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }
}
