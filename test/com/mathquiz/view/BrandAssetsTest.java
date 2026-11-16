package com.mathquiz.view;

import com.mathquiz.testutil.TestHome;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The new logo is really in the app (FEAT-12 / DEBT-13): true transparent PNGs in every size, the window
 * icon set, Archie in Help and Analytics, and the report logo. The old JPEG-in-a-.png is gone.
 */
class BrandAssetsTest {

    private static final int[] ICON_SIZES = {16, 24, 32, 48, 256};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};

    @BeforeAll
    static void requireThrowAwayHome() {
        TestHome.requireThrowAwayHome();
    }

    private static byte[] bytes(String resource) throws IOException {
        try (InputStream in = BrandAssets.class.getResourceAsStream(resource)) {
            assertNotNull(in, "missing resource " + resource);
            return in.readAllBytes();
        }
    }

    private static void assertRealPng(byte[] data, String what) {
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            assertEquals(PNG_SIGNATURE[i], data[i], what + " must be a real PNG, not renamed JPEG data");
        }
    }

    @Test
    void everyIconIsARealTransparentPngAtItsSize() throws Exception {
        for (int size : ICON_SIZES) {
            String name = "/com/mathquiz/resources/icons/app-" + size + ".png";
            byte[] data = bytes(name);
            assertRealPng(data, name);
            BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(data));
            assertEquals(size, img.getWidth(), name);
            assertEquals(size, img.getHeight(), name);
            assertTrue((img.getRGB(0, 0) >>> 24) < 40, name + ": corner should be transparent (rounded tile)");
            assertEquals(255, img.getRGB(size / 2, size / 2) >>> 24, name + ": centre should be opaque");
        }
    }

    @Test
    void archieIsATransparentPng() throws Exception {
        byte[] data = bytes("/com/mathquiz/resources/archie.png");
        assertRealPng(data, "archie.png");
        BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(data));
        assertEquals(256, img.getHeight());
        assertEquals(0, img.getRGB(0, 0) >>> 24, "background must be transparent (works on dark mode)");
        assertEquals(255, img.getRGB(img.getWidth() / 2, img.getHeight() / 2) >>> 24);
    }

    @Test
    void windowIconsCoverEverySizeSmallestFirst() {
        List<Integer> sizes = new ArrayList<>();
        for (Image icon : BrandAssets.windowIcons()) {
            assertEquals(icon.getWidth(null), icon.getHeight(null));
            sizes.add(icon.getWidth(null));
        }
        assertEquals(List.of(16, 24, 32, 48, 256), sizes);
    }

    @Test
    void archieScalesToTheRequestedHeight() {
        Image img = BrandAssets.archie(55);
        assertNotNull(img);
        assertEquals(55, img.getHeight(null));
        assertTrue(Math.abs(img.getWidth(null) - 51) <= 1, "keeps his proportions, width " + img.getWidth(null));
    }

    @Test
    void reportLogoIsTheArchiePngInBase64() throws Exception {
        String base64 = BrandAssets.archieBase64();
        assertTrue(!base64.isEmpty());
        assertRealPng(Base64.getDecoder().decode(base64), "report logo");
    }

    @Test
    void theOldJpegLogoIsGoneAndNothingPointsAtIt() throws Exception {
        assertNull(BrandAssets.class.getResource("/com/mathquiz/resources/logo.png"),
                "logo.png was JPEG data with a white box (DEBT-13)");
        try (Stream<Path> files = Files.walk(Path.of("src"))) {
            List<String> offenders = files.filter(p -> p.toString().endsWith(".java")).filter(p -> {
                try {
                    return Files.readString(p).contains("resources/logo.png");
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }).map(Path::toString).toList();
            assertTrue(offenders.isEmpty(), "still referencing the old logo: " + offenders);
        }
    }

    @Test
    void windowUsesTheNewIconSet() throws Exception {
        List<Integer> sizes = FrameHarness.inspect(820, 620, 1.25, frame -> {
            List<Integer> s = new ArrayList<>();
            for (Image icon : frame.getIconImages()) {
                s.add(icon.getWidth(null));
            }
            return s;
        });
        assertEquals(List.of(16, 24, 32, 48, 256), sizes);
    }

    private static int labelIconHeight(java.awt.Container root, int wantedHeight) {
        for (JLabel label : FrameHarness.findAll(root, JLabel.class)) {
            Icon icon = label.getIcon();
            if (icon != null && icon.getIconHeight() == wantedHeight) {
                return icon.getIconHeight();
            }
        }
        return -1;
    }

    @Test
    void helpAndAnalyticsShowArchie() throws Exception {
        List<Integer> heights = FrameHarness.inspect(820, 620, 1.25, frame -> {
            HelpPanel help = FrameHarness.find(frame.getContentPane(), HelpPanel.class);
            AnalyticsPanel analytics = FrameHarness.find(frame.getContentPane(), AnalyticsPanel.class);
            return List.of(labelIconHeight(help, 55), labelIconHeight(analytics, 32));
        });
        assertEquals(List.of(55, 32), heights);
    }
}
