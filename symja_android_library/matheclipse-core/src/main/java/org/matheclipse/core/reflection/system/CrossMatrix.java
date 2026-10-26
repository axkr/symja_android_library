package org.matheclipse.core.reflection.system;

/**
 * <pre>
 * CrossMatrix(r)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a matrix whose elements are 1 in a cross-shaped region that extends r index positions to
 * each side, and are 0 otherwise.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * CrossMatrix(r, w)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a w x w matrix containing a cross-shaped region of 1s.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * CrossMatrix({r1, r2, ...}, ...)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * yields an array whose elements are 1 in a cross-shaped region that extends ri index positions in
 * the i-th direction.
 * </p>
 * </blockquote>
 */
public class CrossMatrix extends AbstractShapeMatrix {

  @Override
  protected int defaultWidth(double radius) {
    // an odd width that fits the cross exactly
    return (int) (2 * Math.floor(radius) + 1);
  }

  /**
   * A multidimensional cross allows a deviation from the center along at most one axis, within the
   * radius of that axis extended by 1/2.
   */
  @Override
  protected boolean contains(int[] indices, double[] radii, double[] center) {
    int deviations = 0;
    for (int d = 0; d < indices.length; d++) {
      double diff = Math.abs(indices[d] - center[d]);
      if (diff > 1e-9) {
        deviations++;
        // the radius is extended by 0.5 to accommodate fractional inputs
        if (diff > radii[d] + 0.5 + 1e-9) {
          return false;
        }
      }
    }
    return deviations <= 1;
  }
}
