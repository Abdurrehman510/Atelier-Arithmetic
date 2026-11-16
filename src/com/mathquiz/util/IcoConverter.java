package com.mathquiz.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Build-time tool: packs square PNG files (16–256 px) into one Microsoft Icon (ICO) file, smallest first.
 * The PNG bytes are embedded unchanged, so each size keeps the crispness it was exported with (the title
 * bar, taskbar, Start menu and installer each pick the size they need). No external libraries.
 */
public class IcoConverter {

    private static final int HEADER_BYTES = 6;
    private static final int ENTRY_BYTES = 16;
    private static final int MAX_SIZE = 256;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java com.mathquiz.util.IcoConverter <output-ico> <png> [<png> ...]");
            System.exit(1);
        }
        List<File> pngs = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            pngs.add(new File(args[i]));
        }
        try {
            writeIco(new File(args[0]), pngs);
            System.out.println("Wrote " + args[0] + " with " + pngs.size() + " sizes");
        } catch (Exception e) {
            System.err.println("Error building icon: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * @throws IllegalArgumentException if the list is empty or an image is not square or exceeds 256 px
     * @throws IOException              if a file cannot be read as an image or the ICO cannot be written
     */
    public static void writeIco(File outputIco, List<File> pngs) throws IOException {
        if (pngs.isEmpty()) {
            throw new IllegalArgumentException("at least one PNG is required");
        }
        List<Entry> entries = new ArrayList<>();
        for (File png : pngs) {
            byte[] data = Files.readAllBytes(png.toPath());
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
            if (img == null) {
                throw new IOException("Could not read image: " + png.getAbsolutePath());
            }
            if (img.getWidth() != img.getHeight() || img.getWidth() > MAX_SIZE) {
                throw new IllegalArgumentException(png.getName() + " must be square and at most "
                        + MAX_SIZE + " px (was " + img.getWidth() + "x" + img.getHeight() + ")");
            }
            entries.add(new Entry(img.getWidth(), data));
        }
        entries.sort(Comparator.comparingInt(e -> e.size));

        int total = HEADER_BYTES + ENTRY_BYTES * entries.size();
        for (Entry e : entries) {
            total += e.data.length;
        }
        ByteBuffer out = ByteBuffer.allocate(total).order(ByteOrder.LITTLE_ENDIAN);
        out.putShort((short) 0);                       // reserved
        out.putShort((short) 1);                       // type 1 = icon
        out.putShort((short) entries.size());

        int offset = HEADER_BYTES + ENTRY_BYTES * entries.size();
        for (Entry e : entries) {
            byte dimension = (byte) (e.size == MAX_SIZE ? 0 : e.size);   // 0 means 256
            out.put(dimension).put(dimension);
            out.put((byte) 0).put((byte) 0);           // colour count, reserved
            out.putShort((short) 1);                   // colour planes
            out.putShort((short) 32);                  // bits per pixel
            out.putInt(e.data.length);
            out.putInt(offset);
            offset += e.data.length;
        }
        for (Entry e : entries) {
            out.put(e.data);
        }
        Files.write(outputIco.toPath(), out.array());
    }

    private record Entry(int size, byte[] data) {
    }
}
