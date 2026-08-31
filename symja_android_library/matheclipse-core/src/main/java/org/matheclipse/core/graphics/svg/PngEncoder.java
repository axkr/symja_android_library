package org.matheclipse.core.graphics.svg;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * Writes a block of pixels as a PNG {@code data:} URI, for the rasters that are too large to draw
 * as one rectangle per cell.
 *
 * <p>
 * The encoder is written out rather than taken from {@code javax.imageio}, which does not exist on
 * Android, so the whole SVG converter keeps working there. Everything it needs beyond the language
 * itself is {@link Deflater} and {@link CRC32}.
 */
final class PngEncoder {

  private static final byte[] SIGNATURE =
      {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n'};

  /** Bytes per pixel of the colour type this writes: red, green, blue, alpha. */
  private static final int CHANNELS = 4;

  private PngEncoder() {}

  /**
   * Encode pixels as a PNG and wrap it in the URI form an <code>&lt;image&gt;</code> element takes.
   *
   * @param argb one pixel per entry, row by row from the top, alpha in the high byte
   * @param width pixels per row
   * @param height number of rows
   */
  static String dataUri(int[] argb, int width, int height) {
    return "data:image/png;base64,"
        + Base64.getEncoder().encodeToString(encode(argb, width, height));
  }

  private static byte[] encode(int[] argb, int width, int height) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    out.write(SIGNATURE, 0, SIGNATURE.length);

    ByteArrayOutputStream header = new ByteArrayOutputStream();
    writeInt(header, width);
    writeInt(header, height);
    header.write(8); // bits per channel
    header.write(6); // colour type: truecolour with alpha
    header.write(0); // compression method: deflate, the only one defined
    header.write(0); // filter method: the adaptive one, the only one defined
    header.write(0); // not interlaced
    writeChunk(out, "IHDR", header.toByteArray());

    writeChunk(out, "IDAT", deflate(filter(argb, width, height)));
    writeChunk(out, "IEND", new byte[0]);
    return out.toByteArray();
  }

  /**
   * Lay the pixels out as PNG scanlines.
   *
   * <p>
   * Each line carries the filter it was written with, and the filter is chosen per line by the
   * heuristic the specification recommends: the one whose output has the smallest total magnitude
   * compresses best in practice. It matters here because a density plot is a gradient, where
   * subtracting the neighbouring pixel turns a slowly changing image into a nearly constant one.
   */
  private static byte[] filter(int[] argb, int width, int height) {
    int stride = width * CHANNELS;
    byte[] out = new byte[height * (stride + 1)];
    byte[] raw = new byte[stride];
    byte[] prior = new byte[stride];
    byte[] candidate = new byte[stride];
    byte[] best = new byte[stride];

    for (int y = 0; y < height; y++) {
      int rowStart = y * width;
      for (int x = 0; x < width; x++) {
        int pixel = argb[rowStart + x];
        int at = x * CHANNELS;
        raw[at] = (byte) (pixel >> 16);
        raw[at + 1] = (byte) (pixel >> 8);
        raw[at + 2] = (byte) pixel;
        raw[at + 3] = (byte) (pixel >>> 24);
      }

      int bestType = 0;
      long bestScore = score(raw, stride);
      System.arraycopy(raw, 0, best, 0, stride);

      for (int type = 1; type <= 2; type++) {
        applyFilter(type, raw, prior, candidate, stride);
        long candidateScore = score(candidate, stride);
        if (candidateScore < bestScore) {
          bestScore = candidateScore;
          bestType = type;
          System.arraycopy(candidate, 0, best, 0, stride);
        }
      }

      int destination = y * (stride + 1);
      out[destination] = (byte) bestType;
      System.arraycopy(best, 0, out, destination + 1, stride);
      System.arraycopy(raw, 0, prior, 0, stride);
    }
    return out;
  }

  /** Filter 1 subtracts the pixel to the left, filter 2 the pixel above. */
  private static void applyFilter(int type, byte[] raw, byte[] prior, byte[] out, int stride) {
    if (type == 1) {
      for (int i = 0; i < stride; i++) {
        int left = i >= CHANNELS ? (raw[i - CHANNELS] & 0xff) : 0;
        out[i] = (byte) ((raw[i] & 0xff) - left);
      }
    } else {
      for (int i = 0; i < stride; i++) {
        out[i] = (byte) ((raw[i] & 0xff) - (prior[i] & 0xff));
      }
    }
  }

  /** The sum of the filtered bytes read as signed values, which is what the heuristic compares. */
  private static long score(byte[] line, int stride) {
    long sum = 0;
    for (int i = 0; i < stride; i++) {
      sum += Math.abs((int) line[i]);
    }
    return sum;
  }

  private static byte[] deflate(byte[] data) {
    Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
    try {
      deflater.setInput(data);
      deflater.finish();
      ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(64, data.length / 4));
      byte[] buffer = new byte[8192];
      while (!deflater.finished()) {
        int count = deflater.deflate(buffer);
        if (count == 0) {
          break;
        }
        out.write(buffer, 0, count);
      }
      return out.toByteArray();
    } finally {
      deflater.end();
    }
  }

  private static void writeChunk(ByteArrayOutputStream out, String type, byte[] data) {
    writeInt(out, data.length);
    byte[] name = type.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    out.write(name, 0, name.length);
    out.write(data, 0, data.length);

    CRC32 crc = new CRC32();
    crc.update(name, 0, name.length);
    crc.update(data, 0, data.length);
    writeInt(out, (int) crc.getValue());
  }

  private static void writeInt(ByteArrayOutputStream out, int value) {
    out.write(value >>> 24);
    out.write(value >>> 16);
    out.write(value >>> 8);
    out.write(value);
  }
}
