package com.mathquiz.view;

import com.mathquiz.view.tour.MascotPainter;
import com.mathquiz.view.tour.TourStep.MascotExpression;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Archie really wears what a child buys in the shop (BUG-22): every item visibly changes his drawing,
 * no two items look alike, and nothing pokes outside the box {@link ArchieView} reserves for him.
 * Pure Java2D, so no window is needed.
 */
class ArchieLookTest {

    private static final int W = 320;
    private static final int H = 320;
    private static final int CX = 160;
    private static final int CY = 180;
    private static final MascotExpression[] IN_APP = {
            MascotExpression.HAPPY, MascotExpression.EXCITED, MascotExpression.THINKING};

    private static BufferedImage draw(String item, MascotExpression expression) {
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        MascotPainter.draw(g, CX, CY, 100, expression, item);
        g.dispose();
        return img;
    }

    private static BufferedImage draw(String item) {
        return draw(item, MascotExpression.HAPPY);
    }

    private static int differingPixels(BufferedImage a, BufferedImage b) {
        int n = 0;
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                if (a.getRGB(x, y) != b.getRGB(x, y)) {
                    n++;
                }
            }
        }
        return n;
    }

    @Test
    void everyShopItemVisiblyChangesArchie() {
        BufferedImage plain = draw("none");
        List<String> notWorn = new ArrayList<>();
        for (MascotShopPanel.ShopItem item : MascotShopPanel.ALL_ITEMS) {
            int changed = differingPixels(plain, draw(item.id));
            if (changed < 120) {
                notWorn.add(item.id + " (" + changed + " px)");
            }
        }
        assertTrue(notWorn.isEmpty(), "items Archie does not visibly wear: " + notWorn);
    }

    @Test
    void noTwoItemsLookTheSame() {
        List<MascotShopPanel.ShopItem> items = MascotShopPanel.ALL_ITEMS;
        List<BufferedImage> looks = new ArrayList<>();
        for (MascotShopPanel.ShopItem item : items) {
            looks.add(draw(item.id));
        }
        List<String> twins = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                if (differingPixels(looks.get(i), looks.get(j)) < 60) {
                    twins.add(items.get(i).id + " = " + items.get(j).id);
                }
            }
        }
        assertTrue(twins.isEmpty(), "items that look alike: " + twins);
    }

    @Test
    void unknownOrMissingItemsDrawPlainArchie() {
        BufferedImage plain = draw("none");
        assertEquals(0, differingPixels(plain, draw("no_such_item")));
        assertEquals(0, differingPixels(plain, draw(null)));
    }

    @Test
    void goldenArchieTurnsGold() {
        Color plain = new Color(draw("none").getRGB(CX - 30, CY - 30), true);    // upper-left of the body
        Color gold = new Color(draw("color_gold").getRGB(CX - 30, CY - 30), true);
        assertTrue(gold.getGreen() > plain.getGreen() + 30 && gold.getRed() >= plain.getRed(),
                "body should turn gold: plain " + plain + ", golden " + gold);
    }

    @Test
    void rainbowGlowShinesAroundArchie() {
        int above = CY - 58;                                                     // just above his head
        assertEquals(0, new Color(draw("none").getRGB(CX, above), true).getAlpha(), "plain Archie has no glow");
        assertTrue(new Color(draw("color_rainbow").getRGB(CX, above), true).getAlpha() > 0, "no rainbow glow");
    }

    @Test
    void everyLookFitsTheBoxArchieViewReserves() {
        List<String> outside = new ArrayList<>();
        List<String> items = new ArrayList<>();
        items.add("none");
        for (MascotShopPanel.ShopItem item : MascotShopPanel.ALL_ITEMS) {
            items.add(item.id);
        }
        for (String item : items) {
            for (MascotExpression expression : IN_APP) {
                BufferedImage img = draw(item, expression);
                int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
                for (int y = 0; y < H; y++) {
                    for (int x = 0; x < W; x++) {
                        if ((img.getRGB(x, y) >>> 24) != 0) {
                            minX = Math.min(minX, x - CX);
                            maxX = Math.max(maxX, x - CX);
                            minY = Math.min(minY, y - CY);
                            maxY = Math.max(maxY, y - CY);
                        }
                    }
                }
                if (minX < -ArchieView.HALF_WIDTH || maxX >= ArchieView.HALF_WIDTH
                        || minY < ArchieView.TOP || maxY >= ArchieView.BOTTOM) {
                    outside.add(item + "/" + expression + " spans x " + minX + ".." + maxX + ", y " + minY + ".." + maxY);
                }
            }
        }
        assertTrue(outside.isEmpty(), "drawn outside x ±" + ArchieView.HALF_WIDTH + ", y " + ArchieView.TOP
                + ".." + ArchieView.BOTTOM + ":\n" + String.join("\n", outside));
    }
}
