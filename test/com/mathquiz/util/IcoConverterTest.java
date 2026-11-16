package com.mathquiz.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The Windows icon is built from the logo's PNG sizes (DEBT-13): one ICO holding every size, so the
 * title bar, taskbar, Start menu and installer each get a crisp image instead of one scaled-down 256.
 */
class IcoConverterTest {

    private static File png(Path dir, int size, Color fill) throws IOException {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(fill);
        g.fillOval(0, 0, size, size);
        g.dispose();
        File f = dir.resolve("icon-" + size + ".png").toFile();
        ImageIO.write(img, "png", f);
        return f;
    }

    private static int u16(ByteBuffer b) {
        return b.getShort() & 0xFFFF;
    }

    @Test
    void holdsEverySizeSortedSmallestFirstWithTheOriginalPngBytes(@TempDir Path dir) throws Exception {
        File big = png(dir, 256, Color.ORANGE);
        File small = png(dir, 16, Color.BLUE);
        File mid = png(dir, 48, Color.GREEN);
        File ico = dir.resolve("out.ico").toFile();

        IcoConverter.writeIco(ico, List.of(big, small, mid));   // deliberately unsorted

        ByteBuffer b = ByteBuffer.wrap(Files.readAllBytes(ico.toPath())).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(0, u16(b), "reserved");
        assertEquals(1, u16(b), "type = icon");
        assertEquals(3, u16(b), "image count");

        int[] expectedSizes = {16, 48, 256};
        File[] expectedFiles = {small, mid, big};
        int[] offsets = new int[3];
        int[] lengths = new int[3];
        for (int i = 0; i < 3; i++) {
            int w = b.get() & 0xFF;
            int h = b.get() & 0xFF;
            assertEquals(expectedSizes[i] == 256 ? 0 : expectedSizes[i], w, "width byte (0 = 256)");
            assertEquals(w, h, "square");
            b.get();
            b.get();                                    // colour count, reserved
            assertEquals(1, u16(b), "planes");
            assertEquals(32, u16(b), "bits per pixel");
            lengths[i] = b.getInt();
            offsets[i] = b.getInt();
        }
        for (int i = 0; i < 3; i++) {
            byte[] expected = Files.readAllBytes(expectedFiles[i].toPath());
            assertEquals(expected.length, lengths[i], "length of image " + i);
            byte[] actual = new byte[lengths[i]];
            System.arraycopy(b.array(), offsets[i], actual, 0, lengths[i]);
            assertArrayEquals(expected, actual, "image " + i + " must be the original PNG bytes");
        }
        assertEquals(offsets[2] + lengths[2], b.array().length, "no stray bytes");
    }

    @Test
    void rejectsAnEmptyList(@TempDir Path dir) {
        assertThrows(IllegalArgumentException.class,
                () -> IcoConverter.writeIco(dir.resolve("x.ico").toFile(), List.of()));
    }

    @Test
    void rejectsImagesIcoCannotHold(@TempDir Path dir) throws Exception {
        File tooBig = png(dir, 300, Color.RED);
        assertThrows(IllegalArgumentException.class,
                () -> IcoConverter.writeIco(dir.resolve("x.ico").toFile(), List.of(tooBig)));
        BufferedImage wide = new BufferedImage(32, 16, BufferedImage.TYPE_INT_ARGB);
        File notSquare = dir.resolve("wide.png").toFile();
        ImageIO.write(wide, "png", notSquare);
        assertThrows(IllegalArgumentException.class,
                () -> IcoConverter.writeIco(dir.resolve("y.ico").toFile(), List.of(notSquare)));
    }

    @Test
    void rejectsAFileThatIsNotAnImage(@TempDir Path dir) throws Exception {
        File junk = dir.resolve("junk.png").toFile();
        Files.writeString(junk.toPath(), "not an image");
        assertThrows(IOException.class,
                () -> IcoConverter.writeIco(dir.resolve("z.ico").toFile(), List.of(junk)));
    }
}
