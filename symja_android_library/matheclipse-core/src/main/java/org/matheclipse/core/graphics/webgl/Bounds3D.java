package org.matheclipse.core.graphics.webgl;

/** The axis aligned extent of everything collected so far, in data coordinates. */
public final class Bounds3D {

  public final double[] min =
      {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
  public final double[] max =
      {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};

  public boolean isEmpty() {
    return min[0] > max[0];
  }

  public void add(double[] p) {
    for (int i = 0; i < 3; i++) {
      double v = p[i];
      if (!Double.isFinite(v)) {
        return;
      }
    }
    for (int i = 0; i < 3; i++) {
      if (p[i] < min[i]) {
        min[i] = p[i];
      }
      if (p[i] > max[i]) {
        max[i] = p[i];
      }
    }
  }

  /** Grow the box by {@code radius} in every direction around {@code center}. */
  public void addBall(double[] center, double radius) {
    double r = Math.abs(radius);
    add(new double[] {center[0] - r, center[1] - r, center[2] - r});
    add(new double[] {center[0] + r, center[1] + r, center[2] + r});
  }

  /** The length of the box diagonal, or 1 when nothing has been added. */
  public double diagonal() {
    if (isEmpty()) {
      return 1.0;
    }
    double dx = max[0] - min[0];
    double dy = max[1] - min[1];
    double dz = max[2] - min[2];
    double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
    return d > 0 ? d : 1.0;
  }

  /**
   * A range that is never degenerate: an axis on which every coordinate is the same is widened, so
   * a flat plot still gets a box and a sensible set of ticks.
   */
  public double[][] ranges() {
    double[][] out = new double[3][];
    for (int i = 0; i < 3; i++) {
      double lo = min[i];
      double hi = max[i];
      if (!Double.isFinite(lo) || !Double.isFinite(hi)) {
        lo = -1;
        hi = 1;
      } else if (hi - lo < 1e-12) {
        double pad = Math.max(Math.abs(lo) * 0.1, 0.5);
        lo -= pad;
        hi += pad;
      }
      out[i] = new double[] {lo, hi};
    }
    return out;
  }
}
