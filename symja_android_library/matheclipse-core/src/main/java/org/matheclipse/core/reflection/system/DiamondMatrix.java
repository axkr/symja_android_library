package org.matheclipse.core.reflection.system;

public class DiamondMatrix extends AbstractShapeMatrix {

  @Override
  protected int defaultWidth(double radius) {
    // an odd width that fits the diamond exactly
    return (int) (2 * Math.floor(radius) + 1);
  }

  /**
   * Scaled Manhattan (L1) distance; the radii are extended by 1/2 to map the discrete integer grid
   * to the continuous L1 radius.
   */
  @Override
  protected boolean contains(int[] indices, double[] radii, double[] center) {
    double sum = 0.0;
    for (int d = 0; d < indices.length; d++) {
      sum += Math.abs(indices[d] - center[d]) / (radii[d] + 0.5);
    }
    // small epsilon tolerance for floating-point inaccuracies
    return sum <= 1.0 + 1e-9;
  }
}
