package org.matheclipse.core.graphics;

import java.util.Arrays;

/**
 * The automatic range of one axis of a plot: the body of the drawn values, without the far outliers
 * a pole or a stray point produces.
 *
 * <p>
 * The body lies between the 10th and the 90th percentile of the values, widened by its own size,
 * and not wider than three interquartile ranges outside the quartiles. With weights the
 * percentiles are taken over what the weights measure, for a curve the stretch of its variable
 * each point stands for, so the points an adaptive sampling crowds next to a pole don't make the
 * pole the body of the data. The steep end of a bounded curve, as of an exponential or a narrow
 * peak, is cut the same way when it covers only a small part of the variable's range.
 */
public final class AutomaticPlotRange {

  private AutomaticPlotRange() {}

  /**
   * The range narrowed to the body of the values. It only ever shrinks, and its ends are values
   * of the data.
   *
   * @param values the finite values
   * @param weights the weight of each value, or <code>null</code> to count each value once
   * @param min the smallest value of the full range
   * @param max the largest value of the full range
   * @return <code>{min, max}</code> narrowed, or <code>null</code> if there are too few values or
   *         no spread to tell a body from
   */
  public static double[] narrow(double[] values, double[] weights, double min, double max) {
    int n = values.length;
    if (n < 10) {
      return null;
    }
    double[] sorted;
    double[] cumulative = null;
    if (weights == null) {
      sorted = values.clone();
      Arrays.sort(sorted);
    } else {
      Integer[] order = new Integer[n];
      for (int i = 0; i < n; i++) {
        order[i] = i;
      }
      Arrays.sort(order, (a, b) -> Double.compare(values[a], values[b]));
      sorted = new double[n];
      cumulative = new double[n];
      double total = 0.0;
      for (int i = 0; i < n; i++) {
        sorted[i] = values[order[i]];
        total += weights[order[i]];
        cumulative[i] = total;
      }
      if (!(total > 0.0) || !Double.isFinite(total)) {
        return narrow(values, null, min, max);
      }
      for (int i = 0; i < n; i++) {
        cumulative[i] /= total;
      }
    }
    double p10 = quantile(sorted, cumulative, 1, 10);
    double p90 = quantile(sorted, cumulative, 9, 10);
    double q1 = quantile(sorted, cumulative, 1, 4);
    double q3 = quantile(sorted, cumulative, 3, 4);
    double body = p90 - p10;
    if (body <= 1e-10) {
      if (q3 - q1 <= 1e-10) {
        return null;
      }
      p10 = q1;
      p90 = q3;
      body = q3 - q1;
    }
    double iqr = q3 - q1;
    double targetMin = Math.max(p10 - body, q1 - 3.0 * iqr);
    double targetMax = Math.min(p90 + body, q3 + 3.0 * iqr);
    double newMin = min;
    double newMax = max;
    for (int i = 0; i < n; i++) {
      if (sorted[i] >= targetMin) {
        newMin = Math.max(sorted[i], min);
        break;
      }
    }
    for (int i = n - 1; i >= 0; i--) {
      if (sorted[i] <= targetMax) {
        newMax = Math.min(sorted[i], max);
        break;
      }
    }
    return new double[] {newMin, newMax};
  }

  /**
   * The value at the share <code>numerator / denominator</code>: counted, the value at that index;
   * weighted, the first value whose cumulative share reaches it.
   */
  private static double quantile(double[] sorted, double[] cumulative, int numerator,
      int denominator) {
    int n = sorted.length;
    if (cumulative == null) {
      return sorted[numerator * n / denominator];
    }
    double p = (double) numerator / denominator;
    for (int i = 0; i < n; i++) {
      if (cumulative[i] >= p) {
        return sorted[i];
      }
    }
    return sorted[n - 1];
  }
}
