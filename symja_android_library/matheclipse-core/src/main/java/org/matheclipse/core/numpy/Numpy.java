package org.matheclipse.core.numpy;

public class Numpy {

  /**
   * Generate numbers spaced evenly over a specified interval.
   * 
   * @param start
   * @param stop
   * @param num
   * @param endpoint
   * @return samples, equally spaced over the interval [start, stop] (if endpoint is true) or
   *         [start, stop) (if endpoint is false).
   */
  public static double[] linspace(double start, double stop, int num, boolean endpoint) {
    double[] result = new double[num];
    double step;
    if (endpoint) {
      step = (stop - start) / (num - 1);
    } else {
      step = (stop - start) / (num);
    }
    double x = start;
    result[0] = x;
    for (int i = 1; i < result.length - 1; i++) {
      x += step;
      result[i] = x;
    }
    if (endpoint) {
      result[result.length - 1] = stop;
    } else {
      x += step;
      result[result.length - 1] = x;
    }
    return result;
  }

  /**
   * Generate numbers spaced evenly on a log scale.
   * 
   * @param start
   * @param stop
   * @param num
   * @param endpoint
   * @param base
   * @return samples, equally spaced on a log scale.
   */
  public static double[] logspace(double start, double stop, int num, boolean endpoint,
      double base) {
    double[] result = linspace(start, stop, num, endpoint);
    for (int i = 0; i < result.length; i++) {
      result[i] = Math.pow(base, result[i]);
    }
    return result;
  }
}
