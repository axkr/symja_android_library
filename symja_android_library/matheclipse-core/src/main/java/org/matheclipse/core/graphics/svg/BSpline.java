package org.matheclipse.core.graphics.svg;

import java.util.ArrayList;
import java.util.List;

/** Evaluation of a (rational) B-spline into a polyline, by de Boor's algorithm. */
final class BSpline {

  private BSpline() {}

  private static final int STEPS_PER_SPAN = 16;

  /**
   * @param control the control points
   * @param degree the spline degree, at least 1
   * @param closed whether the curve wraps around
   * @param weights per control point weights for a rational spline, or {@code null}
   * @return the curve as a polyline, never {@code null}
   */
  static List<double[]> evaluate(List<double[]> control, int degree, boolean closed,
      double[] weights) {
    List<double[]> points = new ArrayList<>(control);
    double[] w = weights == null ? null : weights.clone();
    if (points.size() < 2) {
      return points;
    }
    int d = Math.max(1, Math.min(degree, points.size() - 1));

    if (closed) {
      int n = points.size();
      for (int i = 0; i < d; i++) {
        points.add(points.get(i % n));
      }
      if (w != null) {
        double[] extended = new double[points.size()];
        System.arraycopy(w, 0, extended, 0, Math.min(w.length, n));
        for (int i = n; i < extended.length; i++) {
          extended[i] = extended[i % n];
        }
        w = extended;
      }
    }

    int n = points.size();
    int knotCount = n + d + 1;
    double[] knots = new double[knotCount];
    if (closed) {
      for (int i = 0; i < knotCount; i++) {
        knots[i] = i;
      }
    } else {
      // clamped: the curve touches its first and last control point
      for (int i = 0; i <= d; i++) {
        knots[i] = 0.0;
      }
      for (int i = 1; i < n - d; i++) {
        knots[d + i] = i;
      }
      double last = Math.max(1, n - d);
      for (int i = n; i < knotCount; i++) {
        knots[i] = last;
      }
    }

    double tStart = knots[d];
    double tEnd = knots[n];
    if (!(tEnd > tStart)) {
      return new ArrayList<>(control);
    }
    int steps = Math.max(24, (int) ((tEnd - tStart) * STEPS_PER_SPAN));
    List<double[]> curve = new ArrayList<>(steps + 1);
    for (int i = 0; i <= steps; i++) {
      double t = tStart + (tEnd - tStart) * i / steps;
      if (t >= tEnd) {
        t = Math.nextDown(tEnd);
      }
      double[] p = deBoor(t, d, knots, points, w);
      if (p != null && Double.isFinite(p[0]) && Double.isFinite(p[1])) {
        curve.add(p);
      }
    }
    return curve;
  }

  private static double[] deBoor(double u, int degree, double[] knots, List<double[]> points,
      double[] weights) {
    int n = points.size();
    int k = -1;
    for (int i = degree; i < n; i++) {
      if (u >= knots[i] && u < knots[i + 1]) {
        k = i;
        break;
      }
    }
    if (k < 0) {
      return points.get(n - 1).clone();
    }
    int dim = weights != null ? 3 : 2;
    double[][] d = new double[degree + 1][dim];
    for (int j = 0; j <= degree; j++) {
      int index = k - degree + j;
      if (index < 0 || index >= n) {
        return points.get(n - 1).clone();
      }
      double[] p = points.get(index);
      double weight = weights != null ? weights[index] : 1.0;
      d[j][0] = p[0] * weight;
      d[j][1] = p[1] * weight;
      if (weights != null) {
        d[j][2] = weight;
      }
    }
    for (int r = 1; r <= degree; r++) {
      for (int j = degree; j >= r; j--) {
        double denom = knots[j + 1 + k - r] - knots[j + k - degree];
        double alpha = Math.abs(denom) < 1e-15 ? 0.0 : (u - knots[j + k - degree]) / denom;
        for (int z = 0; z < dim; z++) {
          d[j][z] = (1.0 - alpha) * d[j - 1][z] + alpha * d[j][z];
        }
      }
    }
    double rw = weights != null ? d[degree][2] : 1.0;
    if (Math.abs(rw) < 1e-15) {
      return null;
    }
    return new double[] {d[degree][0] / rw, d[degree][1] / rw};
  }
}
