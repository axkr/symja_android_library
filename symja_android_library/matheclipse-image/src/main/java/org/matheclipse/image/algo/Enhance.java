package org.matheclipse.image.algo;

import java.awt.image.BufferedImage;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.struct.ConfigLength;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;

/**
 * The BoofCV thresholding and histogram operations, behind an interface of plain Java types.
 *
 * <p>
 * The built-ins are kept out of BoofCV's own types on purpose - see the class comment of
 * {@link Boof} and the <code>BoofCvDependencyTest</code> in <code>matheclipse-io</code> that
 * enforces it - so everything BoofCV computes for <code>FindThreshold</code>,
 * <code>Binarize</code>, <code>LocalAdaptiveBinarize</code> and <code>HistogramTransform</code>
 * passes through here.
 */
public final class Enhance {

  private Enhance() {}

  /**
   * The threshold that minimizes the cross entropy of the two groups it makes, of the intensities
   * <code>values</code>, row by row from the top, which lie in <code>min ... max</code>.
   */
  public static double entropy(float[] values, int width, int height, double min, double max) {
    GrayF32 gray = new GrayF32(width, height);
    System.arraycopy(values, 0, gray.data, 0, values.length);
    return GThresholdImageOps.computeEntropy(gray, min, max);
  }

  /**
   * A binary mask thresholded against the neighbourhood of each pixel rather than the whole image.
   *
   * @param method one of <code>"Mean"</code>, <code>"Otsu"</code>, <code>"Niblack"</code>,
   *        <code>"Sauvola"</code>
   * @return <code>true</code> where the pixel is above its local threshold, or <code>null</code> for
   *         an unknown method
   */
  public static boolean[][] localBinary(BufferedImage image, int radius, String method) {
    GrayF32 gray = Boof.grayF32(image);
    ConfigLength width = ConfigLength.fixed(2 * radius + 1);
    GrayU8 binary;
    switch (method) {
      case "Mean":
        binary = GThresholdImageOps.localMean(gray, null, width, 1.0, true, null, null, null);
        break;
      case "Otsu":
        binary = GThresholdImageOps.localOtsu(gray, null, true, width, 0.0, 1.0, true);
        break;
      case "Niblack":
        binary = GThresholdImageOps.localNiblack(gray, null, width, 0.3f, true);
        break;
      case "Sauvola":
        binary = GThresholdImageOps.localSauvola(gray, null, width, 0.3f, true);
        break;
      default:
        return null;
    }
    boolean[][] mask = new boolean[image.getHeight()][image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        mask[y][x] = binary.unsafe_get(x, y) != 0;
      }
    }
    return mask;
  }

}
