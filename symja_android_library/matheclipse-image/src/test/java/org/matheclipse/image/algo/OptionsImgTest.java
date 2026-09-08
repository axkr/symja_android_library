package org.matheclipse.image.algo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import boofcv.alg.interpolate.InterpolationType;
import boofcv.struct.ConnectRule;
import boofcv.struct.border.BorderType;

/** The option values shared by the image built-ins map onto the BoofCV enums. */
public class OptionsImgTest {

  static {
    try {
      F.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void paddingDefaultsToRepeatingTheEdgePixel() {
    assertEquals(BorderType.EXTENDED, OptionsImg.padding(S.Automatic));
    assertEquals(BorderType.EXTENDED, OptionsImg.padding(F.stringx("Fixed")));
    assertEquals(BorderType.WRAP, OptionsImg.padding(F.stringx("Periodic")));
    assertEquals(BorderType.REFLECT, OptionsImg.padding(F.stringx("Reflected")));
    assertEquals(BorderType.SKIP, OptionsImg.padding(S.None));
    assertEquals(BorderType.ZERO, OptionsImg.padding(F.C0));
  }

  @Test
  public void cornerNeighborsDefaultsToEightConnectivity() {
    assertEquals(ConnectRule.EIGHT, OptionsImg.cornerNeighbors(S.True));
    assertEquals(ConnectRule.EIGHT, OptionsImg.cornerNeighbors(S.Automatic));
    assertEquals(ConnectRule.FOUR, OptionsImg.cornerNeighbors(S.False));
  }

  @Test
  public void interpolationTakesOrdersAndMethodNames() {
    assertEquals(InterpolationType.NEAREST_NEIGHBOR, OptionsImg.interpolation(F.C0));
    assertEquals(InterpolationType.BILINEAR, OptionsImg.interpolation(F.C1));
    assertEquals(InterpolationType.BICUBIC, OptionsImg.interpolation(F.C3));
    assertEquals(InterpolationType.NEAREST_NEIGHBOR, OptionsImg.interpolation(S.None));
    assertEquals(InterpolationType.BICUBIC, OptionsImg.interpolation(F.stringx("Cubic")));
    assertEquals(InterpolationType.BILINEAR, OptionsImg.interpolation(S.Automatic));
  }

  @Test
  public void colorSpaceIsNormalizedAndAutomaticMeansUnchanged() {
    assertEquals("Grayscale", OptionsImg.colorSpace(F.stringx("Gray")));
    assertEquals("Grayscale", OptionsImg.colorSpace(F.stringx("Grayscale")));
    assertEquals("RGB", OptionsImg.colorSpace(F.stringx("RGB")));
    assertNull(OptionsImg.colorSpace(S.Automatic));
  }
}
