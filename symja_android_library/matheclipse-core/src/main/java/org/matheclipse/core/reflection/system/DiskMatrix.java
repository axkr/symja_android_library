package org.matheclipse.core.reflection.system;

/**
 * <pre>
 * DiskMatrix(r)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a matrix whose elements are 1 in a circular region that extends r index positions to each
 * side, and are 0 otherwise.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * DiskMatrix(r, w)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * gives a w x w matrix containing a circular region of 1s.
 * </p>
 * </blockquote>
 * 
 * <pre>
 * DiskMatrix({r1, r2, ...}, ...)
 * </pre>
 * 
 * <blockquote>
 * <p>
 * yields an array whose elements are 1 in a generalized ellipsoidal region that extends ri index
 * positions in the i-th direction.
 * </p>
 * </blockquote>
 */
public class DiskMatrix extends AbstractShapeMatrix {

  @Override
  protected int defaultWidth(double radius) {
    // an odd width that fits the disk exactly. Math#rint() rounds half to even, like the Round()
    // function does.
    return (int) (2 * Math.rint(radius) + 1);
  }

  /**
   * Scaled Euclidean (L2) distance: an element belongs to the disk if its center lies within the
   * radius r+1/2, so that the disk extends exactly r index positions to each side.
   */
  @Override
  protected boolean contains(int[] indices, double[] radii, double[] center) {
    double sum = 0.0;
    for (int d = 0; d < indices.length; d++) {
      double diff = (indices[d] - center[d]) / (radii[d] + 0.5);
      sum += diff * diff;
    }
    // small epsilon tolerance for floating-point inaccuracies
    return sum <= 1.0 + 1e-9;
  }
}
