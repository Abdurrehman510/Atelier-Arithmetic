package com.mathquiz.view;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Loads the brand images (see {@code brand/README.md}): the window icon set (16–256 px, the small sizes
 * simplified so they stay legible), and Archie for headers and the exported report. All are transparent
 * PNGs, so they suit both light and dark mode. A missing image is skipped — the logo is decoration, the
 * app works without it.
 */
final class BrandAssets {

    private static final String RESOURCES = "/com/mathquiz/resources/";
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 256};

    private BrandAssets() {
    }

    /** The window/taskbar icon in every size, smallest first, for {@code JFrame.setIconImages}. */
    static List<Image> windowIcons() {
        List<Image> icons = new ArrayList<>();
        for (int size : ICON_SIZES) {
            Image icon = read(RESOURCES + "icons/app-" + size + ".png");
            if (icon != null) {
                icons.add(icon);
            }
        }
        return icons;
    }

    /** Archie the Owl scaled to {@code height} pixels (keeps his proportions), or null if missing. */
    static Image archie(int height) {
        BufferedImage source = read(RESOURCES + "archie.png");
        if (source == null) {
            return null;
        }
        int width = Math.max(1, Math.round(source.getWidth() * (float) height / source.getHeight()));
        return source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /** Archie as Base64 PNG for the exported HTML report, or "" if missing. */
    static String archieBase64() {
        try (InputStream in = BrandAssets.class.getResourceAsStream(RESOURCES + "archie.png")) {
            return in == null ? "" : Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (IOException e) {
            return "";   // optional decoration; the report is complete without it
        }
    }

    private static BufferedImage read(String resource) {
        try (InputStream in = BrandAssets.class.getResourceAsStream(resource)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("Could not load " + resource + ": " + e.getMessage());
            return null;
        }
    }
}
