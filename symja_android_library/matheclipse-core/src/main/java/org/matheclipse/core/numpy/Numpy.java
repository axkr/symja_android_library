package org.matheclipse.core.numpy;

public class Numpy {

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
      result[result.length - 1] = x += step;
    }
    return result;
  }

  public static double[] logspace(double start, double stop, int num, boolean endpoint,
      double base) {
    double[] result = linspace(start, stop, num, endpoint);
    for (int i = 0; i < result.length; i++) {
      result[i] = Math.pow(base, result[i]);
    }
    return result;
  }
}
