package org.matheclipse.image.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;

/** The {@link Boof} bridge has to survive a round trip without moving any sample. */
public class BoofTest {

  @Test
  public void grayscaleRoundTrips() {
    GrayF32 original = new GrayF32(4, 3);
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 4; x++) {
        original.unsafe_set(x, y, 17 * (y * 4 + x));
      }
    }

    GrayF32 restored = Boof.grayF32(Boof.toBufferedImage(original));

    assertEquals(original.getWidth(), restored.getWidth());
    assertEquals(original.getHeight(), restored.getHeight());
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 4; x++) {
        assertEquals(original.unsafe_get(x, y), restored.unsafe_get(x, y), 0.0f,
            "sample at " + x + "," + y);
      }
    }
  }

  /**
   * The reason {@link Boof#argb(BufferedImage, int, int)} exists. A
   * {@link BufferedImage#TYPE_BYTE_GRAY} raster is linear grey, so <code>getRGB</code> gamma
   * converts it to sRGB and reports a different number than the one stored - here 128 comes back as
   * 186. Reading the raster sample is what makes the round trip above exact.
   */
  @Test
  public void argbReadsTheRasterOfAGrayImageRatherThanGammaCorrectingIt() {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    image.getRaster().setSample(0, 0, 0, 128);

    assertEquals(128, Boof.argb(image, 0, 0) & 0xFF);
    assertNotEquals(128, image.getRGB(0, 0) & 0xFF, "the JDK conversion this method works around");
  }

  @Test
  public void colorRoundTripsWithoutAlpha() {
    Planar<GrayF32> original = new Planar<GrayF32>(GrayF32.class, 2, 2, 3);
    original.getBand(0).unsafe_set(0, 0, 200);
    original.getBand(1).unsafe_set(0, 0, 100);
    original.getBand(2).unsafe_set(0, 0, 50);

    BufferedImage buffered = Boof.toBufferedImage(original);
    assertEquals(3, Boof.channels(buffered));
    assertTrue(Boof.isColor(buffered));

    Planar<GrayF32> restored = Boof.planarF32(buffered);
    assertEquals(3, restored.getNumBands());
    assertEquals(200.0f, restored.getBand(0).unsafe_get(0, 0), 0.0f);
    assertEquals(100.0f, restored.getBand(1).unsafe_get(0, 0), 0.0f);
    assertEquals(50.0f, restored.getBand(2).unsafe_get(0, 0), 0.0f);
  }

  @Test
  public void colorRoundTripsWithAlpha() {
    Planar<GrayF32> original = new Planar<GrayF32>(GrayF32.class, 2, 2, 4);
    original.getBand(0).unsafe_set(1, 1, 10);
    original.getBand(1).unsafe_set(1, 1, 20);
    original.getBand(2).unsafe_set(1, 1, 30);
    original.getBand(3).unsafe_set(1, 1, 40);

    BufferedImage buffered = Boof.toBufferedImage(original);
    assertEquals(4, Boof.channels(buffered));
    assertTrue(Boof.hasAlpha(buffered));

    Planar<GrayF32> restored = Boof.planarF32(buffered);
    assertEquals(4, restored.getNumBands());
    assertEquals(10.0f, restored.getBand(0).unsafe_get(1, 1), 0.0f);
    assertEquals(20.0f, restored.getBand(1).unsafe_get(1, 1), 0.0f);
    assertEquals(30.0f, restored.getBand(2).unsafe_get(1, 1), 0.0f);
    assertEquals(40.0f, restored.getBand(3).unsafe_get(1, 1), 0.0f);
  }

  @Test
  public void samplesOutsideTheRangeAreClamped() {
    GrayF32 original = new GrayF32(2, 1);
    original.unsafe_set(0, 0, -40.0f);
    original.unsafe_set(1, 0, 900.0f);

    GrayF32 restored = Boof.grayF32(Boof.toBufferedImage(original));

    assertEquals(0.0f, restored.unsafe_get(0, 0), 0.0f);
    assertEquals(255.0f, restored.unsafe_get(1, 0), 0.0f);
  }

  @Test
  public void intensityOfAColorPixelUsesLumaWeights() {
    Planar<GrayF32> original = new Planar<GrayF32>(GrayF32.class, 1, 1, 3);
    original.getBand(0).unsafe_set(0, 0, 255);
    original.getBand(1).unsafe_set(0, 0, 0);
    original.getBand(2).unsafe_set(0, 0, 0);

    assertEquals(0.299f * 255, Boof.intensity(Boof.toBufferedImage(original), 0, 0), 1e-3f);
  }

  /**
   * A result never inherits the pixel matrix of its input - see
   * {@link Boof#toImageExpr(boofcv.struct.image.ImageBase)}.
   */
  @Test
  public void resultsCarryNoSourceMatrix() {
    assertEquals(null, Boof.toImageExpr(new GrayF32(2, 2)).getMatrix());
  }
}
