package org.matheclipse.core.sympy.plotting;

import java.util.SortedSet;
import org.matheclipse.core.generic.UnaryNumerical;

public class Plot {
  final static int MAXIMUM_DEPTH = 12;

  /**
   * Simple immutable point with double coordinates used for plotting.
   */
  static class Point {
    final double x, y;

    /**
     * Create a 2D point.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Compute the plot data for a given numerical unary function over a range.
   *
   * @param hun numerical function wrapper
   * @param data preallocated data array (ignored and replaced when non-empty sampling is returned)
   * @param xMin minimum x value
   * @param xMax maximum x value
   * @param xScale x-axis scale ("Log", "Log10", "Log2" or empty)
   * @return a 2xN array with x coordinates in row 0 and y coordinates in row 1
   */
  public static double[][] computePlot(final UnaryNumerical hun, double[][] data, final double xMin,
      final double xMax, String xScale) {
    return computePlot(hun, data, xMin, xMax, xScale, -1, -1);
  }

  /**
   * Sample a function over a range.
   *
   * @param plotPoints number of evenly spaced samples to take, or a value below 2 to sample
   *        adaptively
   * @param maxRecursion how deep the adaptive sampler may subdivide, or a value below 1 for the
   *        default depth
   */
  public static double[][] computePlot(final UnaryNumerical hun, double[][] data, final double xMin,
      final double xMax, String xScale, int plotPoints, int maxRecursion) {
    // An explicit PlotPoints asks for that many samples, which is a uniform sweep rather than the
    // adaptive refinement; MaxRecursion only bounds the adaptive one.
    int depth = maxRecursion >= 1 ? Math.min(maxRecursion, 20) : MAXIMUM_DEPTH;
    Series.LineOver1DRangeSeries series = plotPoints >= 2
        ? new Series.LineOver1DRangeSeries(hun, false, xMin, xMax, plotPoints, xScale)
        : new Series.LineOver1DRangeSeries(hun, xMin, xMax, depth, true, xScale);
    SortedSet<Point> plot = series.getPoints();
    if (plot.size() > 0) {
      data = new double[2][plot.size()];
      int i = 0;
      for (Point p : plot) {
        data[0][i] = p.x;
        data[1][i] = p.y;
        i++;
      }
    }
    return data;
  }

}
