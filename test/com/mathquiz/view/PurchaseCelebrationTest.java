package com.mathquiz.view;

import com.mathquiz.config.AppConfig;
import com.mathquiz.testutil.TestHome;
import com.mathquiz.view.tour.TourStep.MascotExpression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.JDialog;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Buying something shows a "ta-da" moment (FEAT-13): a big Archie wearing exactly what was just bought,
 * like the tour's Archie, instead of a plain text pop-up that only mentioned a tiny emoji.
 */
class PurchaseCelebrationTest {

    private static final MascotShopPanel.ShopItem WIZARD = MascotShopPanel.ALL_ITEMS.stream()
            .filter(i -> i.id.equals("hat_wizard")).findFirst().orElseThrow();

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    /** Builds the dialog (never shown) inside a laid-out frame and reports what a child would see. */
    private record Seen(String title, String itemId, MascotExpression expression, int archieHeight,
                        String text, boolean hasDefaultButton, List<String> problems) {
    }

    private static Seen look(javax.swing.JFrame frame, MascotShopPanel.ShopItem item) {
        JDialog dialog = PurchaseCelebration.create(frame, item);
        try {
            dialog.pack();
            dialog.validate();
            ArchieView archie = FrameHarness.find(dialog.getContentPane(), ArchieView.class);
            assertNotNull(archie, "no Archie in the celebration");
            String text = FrameHarness.findAll(dialog.getContentPane(), JLabel.class).stream()
                    .map(JLabel::getText).filter(t -> t != null).collect(Collectors.joining(" | "));
            List<String> problems = new ArrayList<>(FrameHarness.clippedComponents(dialog.getContentPane()));
            // leave room for the title bar and a little air inside the smallest supported window
            if (dialog.getWidth() > frame.getWidth() - 40 || dialog.getHeight() > frame.getHeight() - 70) {
                problems.add("dialog " + dialog.getSize() + " is too big for the window " + frame.getSize());
            }
            return new Seen(dialog.getTitle(), archie.itemId(), archie.getExpression(),
                    archie.getPreferredSize().height, text,
                    dialog.getRootPane().getDefaultButton() != null, problems);
        } finally {
            dialog.dispose();
        }
    }

    private static Seen look(MascotShopPanel.ShopItem item, int w, int h, double scale) throws Exception {
        return FrameHarness.inspect(w, h, scale, frame -> look(frame, item));
    }

    @Test
    void showsAnExcitedArchieWearingTheNewItem() throws Exception {
        Seen s = look(WIZARD, 820, 620, 1.0);
        assertEquals("Item Unlocked!", s.title());
        assertEquals("hat_wizard", s.itemId());
        assertEquals(MascotExpression.EXCITED, s.expression());
    }

    @Test
    void archieIsBigLikeInTheTour() throws Exception {
        Seen s = look(WIZARD, 820, 620, 1.0);
        assertTrue(s.archieHeight() >= 140, "Archie should be clearly visible, was " + s.archieHeight() + " px tall");
    }

    @Test
    void namesTheItemAndSaysWhereChildSeesIt() throws Exception {
        Seen s = look(WIZARD, 820, 620, 1.0);
        assertTrue(s.text().contains("Wizard Hat"), s.text());
        assertTrue(s.text().contains("Archie says"), s.text());
        assertTrue(s.text().contains("Home") && s.text().contains("quizzes") && s.text().contains("results"), s.text());
    }

    @Test
    void enterClosesItViaADefaultButton() throws Exception {
        assertTrue(look(WIZARD, 820, 620, 1.0).hasDefaultButton());
    }

    @Test
    void everyShopItemFitsTheSmallestWindowAt150Percent() throws Exception {
        List<String> problems = FrameHarness.inspect(720, 550, 1.5, frame -> {
            List<String> found = new ArrayList<>();
            for (MascotShopPanel.ShopItem item : MascotShopPanel.ALL_ITEMS) {
                Seen s = look(frame, item);
                assertEquals(item.id, s.itemId());
                for (String p : s.problems()) {
                    found.add(item.id + ": " + p);
                }
            }
            return found;
        });
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void fitsAt125PercentToo() throws Exception {
        List<String> problems = look(MascotShopPanel.ALL_ITEMS.get(4), 720, 550, 1.25).problems();
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    // ── The buying flow triggers it ──────────────────────────────────────────

    private record Bought(boolean paid, int balance, String equipped, boolean unlocked, List<String> celebrated) {
    }

    private static Bought buy(int startBalance, MascotShopPanel.ShopItem item) throws Exception {
        AppConfig config = AppConfig.getInstance();
        int oldBalance = config.getStarBalance();
        String oldEquipped = config.getEquippedItem();
        String profile = config.getCurrentProfile();
        try {
            return FrameHarness.inspect(820, 620, 1.25, frame -> {
                MascotShopPanel shop = FrameHarness.find(frame.getContentPane(), MascotShopPanel.class);
                List<String> celebrated = new ArrayList<>();
                shop.setCelebration(i -> celebrated.add(i.id));
                config.setStarBalance(startBalance);
                boolean paid = shop.completePurchase(item);
                return new Bought(paid, config.getStarBalance(), config.getEquippedItem(),
                        config.getUnlockedItems().contains(item.id), celebrated);
            });
        } finally {
            config.setStarBalance(oldBalance);
            config.setEquippedItem(oldEquipped);
            config.resetProfileCustomizations(profile);
            config.setEquippedItem(oldEquipped);
        }
    }

    @Test
    void aSuccessfulPurchaseSpendsUnlocksEquipsAndCelebrates() throws Exception {
        Bought b = buy(100, WIZARD);
        assertTrue(b.paid());
        assertEquals(60, b.balance(), "40 stars spent");
        assertEquals("hat_wizard", b.equipped());
        assertTrue(b.unlocked());
        assertEquals(List.of("hat_wizard"), b.celebrated());
    }

    @Test
    void notEnoughStarsMeansNoPurchaseAndNoCelebration() throws Exception {
        Bought b = buy(10, WIZARD);
        assertFalse(b.paid());
        assertEquals(10, b.balance());
        assertFalse(b.unlocked());
        assertTrue(b.celebrated().isEmpty());
    }
}
