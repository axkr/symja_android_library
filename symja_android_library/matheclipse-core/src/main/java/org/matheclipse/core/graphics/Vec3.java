package org.matheclipse.core.graphics;

/**
 * Arithmetic on 3-vectors stored as <code>double[3]</code> arrays, shared by the 3D plotting and
 * region code. Every method except {@link #normalizeInPlace(double[])} leaves its arguments
 * unchanged and returns a new array.
 */
public final class Vec3 {

  /** <code>a + b</code> */
  public static double[] add(double[] a, double[] b) {
    return new double[] {a[0] + b[0], a[1] + b[1], a[2] + b[2]};
  }

  /** <code>a - b</code> */
  public static double[] subtract(double[] a, double[] b) {
    return new double[] {a[0] - b[0], a[1] - b[1], a[2] - b[2]};
  }

  /** <code>s * a</code> */
  public static double[] scale(double[] a, double s) {
    return new double[] {a[0] * s, a[1] * s, a[2] * s};
  }

  /** The dot product of <code>a</code> and <code>b</code>. */
  public static double dot(double[] a, double[] b) {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }

  /** The cross product <code>a x b</code>. */
  public static double[] cross(double[] a, double[] b) {
    return new double[] {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0]};
  }

  /** The Euclidean length of <code>a</code>. */
  public static double length(double[] a) {
    return Math.sqrt(dot(a, a));
  }

  /**
   * The unit vector in the direction of <code>a</code>.
   *
   * @return <code>a</code> itself if its length is zero
   */
  public static double[] normalize(double[] a) {
    double l = length(a);
    return l > 0 ? scale(a, 1 / l) : a;
  }

  /** Scale <code>a</code> to unit length in place; a zero vector stays unchanged. */
  public static void normalizeInPlace(double[] a) {
    double l = length(a);
    if (l > 0.0) {
      for (int i = 0; i < 3; i++) {
        a[i] /= l;
      }
    }
  }

  private Vec3() {}
}
