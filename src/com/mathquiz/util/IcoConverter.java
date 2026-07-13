package com.mathquiz.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.imageio.ImageIO;

/**
 * Automates the translation of standard PNG logo formats into Microsoft Icon (ICO) format
 * containing a high-resolution 256x256 image with zero external library dependencies.
 */
public class IcoConverter {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java com.mathquiz.util.IcoConverter <input-png> <output-ico>");
            System.exit(1);
        }

        File input = new File(args[0]);
        File output = new File(args[1]);

        try {
            convertPngToIco(input, output);
            System.out.println("Successfully converted " + input.getName() + " to " + output.getName());
        } catch (Exception e) {
            System.err.println("Error converting image: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void convertPngToIco(File inputPng, File outputIco) throws IOException {
        BufferedImage src = ImageIO.read(inputPng);
        if (src == null) {
            throw new IOException("Could not read input PNG file: " + inputPng.getAbsolutePath());
        }

        // Scale to 256x256
        int size = 256;
        Image scaled = src.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dest.createGraphics();
        try {
            g2d.drawImage(scaled, 0, 0, null);
        } finally {
            g2d.dispose();
        }

        // Write scaled image to PNG byte array
        ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
        ImageIO.write(dest, "png", pngBaos);
        byte[] pngData = pngBaos.toByteArray();

        // Write ICO file structure
        try (FileOutputStream fos = new FileOutputStream(outputIco)) {
            // ICO Header: 6 bytes
            fos.write(new byte[]{0, 0}); // Reserved
            fos.write(new byte[]{1, 0}); // Image type (1 = icon)
            fos.write(new byte[]{1, 0}); // Number of images

            // ICO Directory Entry: 16 bytes
            fos.write(0); // Width (0 means 256)
            fos.write(0); // Height (0 means 256)
            fos.write(0); // Color count (0)
            fos.write(0); // Reserved

            // Color planes (1)
            ByteBuffer planes = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            planes.putShort((short) 1);
            fos.write(planes.array());

            // Bits per pixel (32)
            ByteBuffer bpp = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            bpp.putShort((short) 32);
            fos.write(bpp.array());

            // Size of image data
            ByteBuffer dataSize = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            dataSize.putInt(pngData.length);
            fos.write(dataSize.array());

            // Offset of image data (header 6 bytes + directory 16 bytes = 22)
            ByteBuffer offset = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            offset.putInt(22);
            fos.write(offset.array());

            // Write actual PNG image data bytes
            fos.write(pngData);
        }
    }
}
