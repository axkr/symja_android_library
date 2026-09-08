package org.matheclipse.image.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.image.algo.Pixels.Samples;

/**
 * The two pixel layouts {@link Pixels.Samples} hides.
 *
 * <p>
 * The planar reader has no Symja surface yet - <code>Interleaving</code> is not wired into
 * <code>Image</code> until the option work reaches it - so it is exercised here rather than through
 * an evaluation. The interleaved reader is covered both ways: the behaviour visible from Symja is
 * pinned in <code>ImageCharacterizationTest</code>, and the identity that ties the two layouts
 * together is pinned here.
 */
public class PixelsTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  /** {{{r,g,b},...},...} - two pixels of a one row RGB image, red then green. */
  private static IAST interleavedRgb() {
    return F.list(F.list(F.list(F.C1, F.C0, F.C0), F.list(F.C0, F.C1, F.C0)));
  }

  /** The same two pixels written as three planes of one row each. */
  private static IAST planarRgb() {
    return F.list(//
        F.list(F.list(F.C1, F.C0)), // red plane
        F.list(F.list(F.C0, F.C1)), // green plane
        F.list(F.list(F.C0, F.C0))); // blue plane
  }

  @Test
  public void interleavedDataIsRowColumnChannel() {
    Samples samples = Pixels.samplesOf(interleavedRgb(), true);
    assertNotNull(samples);
    assertEquals(2, samples.width());
    assertEquals(1, samples.height());
    assertEquals(3, samples.channels());
    assertEquals(F.C1, samples.at(0, 0, 0));
    assertEquals(F.C0, samples.at(1, 0, 0));
    assertEquals(F.C0, samples.at(0, 1, 0));
    assertEquals(F.C1, samples.at(1, 1, 0));
  }

  @Test
  public void planarDataIsChannelRowColumn() {
    Samples samples = Pixels.samplesOf(planarRgb(), false);
    assertNotNull(samples);
    assertEquals(2, samples.width());
    assertEquals(1, samples.height());
    assertEquals(3, samples.channels());
    assertEquals(F.C1, samples.at(0, 0, 0));
    assertEquals(F.C0, samples.at(1, 0, 0));
    assertEquals(F.C0, samples.at(0, 1, 0));
    assertEquals(F.C1, samples.at(1, 1, 0));
  }

  /** The point of the abstraction: the two layouts describe the same picture, sample for sample. */
  @Test
  public void theTwoLayoutsAgreeSampleForSample() {
    Samples interleaved = Pixels.samplesOf(interleavedRgb(), true);
    Samples planar = Pixels.samplesOf(planarRgb(), false);
    assertEquals(interleaved.width(), planar.width());
    assertEquals(interleaved.height(), planar.height());
    assertEquals(interleaved.channels(), planar.channels());
    for (int c = 0; c < interleaved.channels(); c++) {
      for (int y = 0; y < interleaved.height(); y++) {
        for (int x = 0; x < interleaved.width(); x++) {
          assertEquals(interleaved.at(c, x, y), planar.at(c, x, y),
              "channel " + c + " at (" + x + "," + y + ")");
        }
      }
    }
  }

  /** ... and so build the same bitmap. */
  @Test
  public void theTwoLayoutsBuildTheSameBitmap() {
    BufferedImage fromInterleaved = Pixels.toBufferedImage(interleavedRgb(), null, true);
    BufferedImage fromPlanar = Pixels.toBufferedImage(planarRgb(), null, false);
    assertNotNull(fromInterleaved);
    assertNotNull(fromPlanar);
    assertEquals(fromInterleaved.getWidth(), fromPlanar.getWidth());
    assertEquals(fromInterleaved.getHeight(), fromPlanar.getHeight());
    for (int y = 0; y < fromInterleaved.getHeight(); y++) {
      for (int x = 0; x < fromInterleaved.getWidth(); x++) {
        assertEquals(Boof.argb(fromInterleaved, x, y), Boof.argb(fromPlanar, x, y),
            "pixel (" + x + "," + y + ")");
      }
    }
  }

  /**
   * A matrix of scalars is one greyscale plane whichever way it is read, so the layout flag makes
   * no difference to rank 2 data.
   */
  @Test
  public void aMatrixOfScalarsIsGreyscaleInEitherLayout() {
    IAST data = F.list(F.list(F.C0, F.C1), F.list(F.C1, F.C0));
    for (boolean interleaved : new boolean[] {true, false}) {
      Samples samples = Pixels.samplesOf(data, interleaved);
      assertNotNull(samples, "interleaved=" + interleaved);
      assertEquals(2, samples.width());
      assertEquals(2, samples.height());
      assertEquals(1, samples.channels());
      assertEquals(F.C1, samples.at(0, 1, 0));
    }
  }

  /** A one channel image may be written as a matrix of one element lists instead. */
  @Test
  public void oneElementListsAreReadAsSingleSamples() {
    IAST data = F.list(F.list(F.list(F.C0), F.list(F.C1)));
    Samples samples = Pixels.samplesOf(data, true);
    assertNotNull(samples);
    assertEquals(1, samples.channels());
    assertEquals(F.C0, samples.at(0, 0, 0));
    assertEquals(F.C1, samples.at(0, 1, 0));
  }

  /**
   * The channel count is read from a different axis in each layout: the last one interleaved, the
   * first one planar. Two channels - greyscale with alpha - has no <code>BufferedImage</code> type
   * and no colour space name, so it is refused rather than turned into something else.
   */
  @Test
  public void twoChannelsAreRefused() {
    // {2,1,2}: interleaved that is two rows of one pixel with two channels
    IAST data = F.list(F.list(F.list(F.C0, F.C1)), F.list(F.list(F.C1, F.C0)));
    assertNull(Pixels.samplesOf(data, true));
    // and planar it is two planes of a 1x2 image, which is the same two channels
    assertNull(Pixels.samplesOf(data, false));
  }

  /** Rank 4 is not a picture in either layout, and neither is rank 1. */
  @Test
  public void onlyRankTwoAndThreeAreRead() {
    IAST rank4 = F.list(F.list(F.list(F.list(F.C0))));
    assertNull(Pixels.samplesOf(rank4, true));
    assertNull(Pixels.samplesOf(rank4, false));
    IAST rank1 = F.list(F.C0, F.C1);
    assertNull(Pixels.samplesOf(rank1, true));
    assertNull(Pixels.samplesOf(rank1, false));
  }

  /**
   * <code>{1, 2, 2}</code> is a picture in both layouts and a different one in each: two rows of
   * two pixels read interleaved, since the trailing 1 is the channel count, and one greyscale
   * plane of a 2x2 image read planar. Nothing about the data says which; only the layout does.
   */
  @Test
  public void oneAsTheOddAxisIsAPictureEitherWay() {
    IAST data = F.list(F.list(F.list(F.C0), F.list(F.C1)), F.list(F.list(F.C1), F.list(F.C0)));
    Samples interleaved = Pixels.samplesOf(data, true);
    assertNotNull(interleaved);
    assertEquals(1, interleaved.channels());
    assertEquals(2, interleaved.width());
    assertEquals(2, interleaved.height());

    IAST plane = F.list(F.list(F.list(F.C0, F.C1), F.list(F.C1, F.C0)));
    Samples planar = Pixels.samplesOf(plane, false);
    assertNotNull(planar);
    assertEquals(1, planar.channels());
    assertEquals(2, planar.width());
    assertEquals(2, planar.height());
  }

  /**
   * Three planes of a 2x2 image is a picture read as planar, and two channels - so refused - read
   * as interleaved. The layout is what decides, not the data.
   */
  @Test
  public void theSameDataMeansDifferentThingsInTheTwoLayouts() {
    IAST data = F.list(//
        F.list(F.list(F.C0, F.C1), F.list(F.C1, F.C0)), //
        F.list(F.list(F.C1, F.C0), F.list(F.C0, F.C1)), //
        F.list(F.list(F.C0, F.C0), F.list(F.C1, F.C1)));
    Samples planar = Pixels.samplesOf(data, false);
    assertNotNull(planar);
    assertEquals(3, planar.channels());
    assertEquals(2, planar.width());
    assertEquals(2, planar.height());
    // read as interleaved the same data is 3 rows of 2 pixels with 2 channels each
    assertNull(Pixels.samplesOf(data, true));
  }

  // ------------------------------------------------------------------ image -> IAST

  private static BufferedImage twoByOneRgb() {
    return Pixels.toBufferedImage(interleavedRgb(), null, true);
  }

  @Test
  public void toDataDefaultsToInterleavedRowsTopToBottom() {
    assertEquals(Pixels.toData(twoByOneRgb(), Pixels.BYTE),
        Pixels.toData(twoByOneRgb(), Pixels.BYTE, true, false));
  }

  @Test
  public void toDataCanReportOneMatrixPerChannel() {
    IAST planar = Pixels.toData(twoByOneRgb(), Pixels.BYTE, false, false);
    // three planes of one row of two samples
    assertEquals(3, planar.argSize());
    assertEquals(F.list(F.list(F.ZZ(255), F.C0)), planar.arg1(), "red plane");
    assertEquals(F.list(F.list(F.C0, F.ZZ(255))), planar.arg2(), "green plane");
    assertEquals(F.list(F.list(F.C0, F.C0)), planar.arg3(), "blue plane");
  }

  @Test
  public void dataReversedTurnsTheRowsUpsideDown() {
    // a two row greyscale image, black over white
    IAST data = F.list(F.list(F.C0, F.C0), F.list(F.C1, F.C1));
    BufferedImage image = Pixels.toBufferedImage(data, null, true);
    assertEquals(F.list(F.list(F.C0, F.C0), F.list(F.ZZ(255), F.ZZ(255))),
        Pixels.toData(image, Pixels.BYTE, true, false));
    assertEquals(F.list(F.list(F.ZZ(255), F.ZZ(255)), F.list(F.C0, F.C0)),
        Pixels.toData(image, Pixels.BYTE, true, true));
  }

  /** Reversing the rows of each plane is the same picture upside down, plane by plane. */
  @Test
  public void dataReversedAppliesToEachPlane() {
    IAST data = F.list(F.list(F.list(F.C1, F.C0, F.C0)), F.list(F.list(F.C0, F.C1, F.C0)));
    BufferedImage image = Pixels.toBufferedImage(data, null, true);
    IAST planes = Pixels.toData(image, Pixels.BYTE, false, true);
    assertEquals(3, planes.argSize());
    // the red plane was {{255},{0}} the right way up
    assertEquals(F.list(F.list(F.C0), F.list(F.ZZ(255))), planes.arg1(), "red plane");
  }
}
