package org.matheclipse.core.sympy.plotting;

import java.util.SortedSet;
import java.util.function.DoubleUnaryOperator;

public class Plot {
  final static int MAXIMUM_DEPTH = 12;

  /** The number of points of the initial grid of an adaptively sampled plot. */
  final static int DEFAULT_PLOT_POINTS = 50;

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
   * @param hun the sampled function; a sample it has no value for comes back as
   *        {@code Double.NaN}
   * @param data preallocated data array (ignored and replaced when non-empty sampling is returned)
   * @param xMin minimum x value
   * @param xMax maximum x value
   * @param xScale x-axis scale ("Log", "Log10", "Log2" or empty)
   * @return a 2xN array with x coordinates in row 0 and y coordinates in row 1
   */
  public static double[][] computePlot(final DoubleUnaryOperator hun, double[][] data,
      final double xMin, final double xMax, String xScale) {
    return computePlot(hun, data, xMin, xMax, xScale, -1, -1, false);
  }

  /**
   * Sample a function over a range.
   *
   * @param plotPoints number of evenly spaced samples to take, or a value below 2 to sample
   *        adaptively
   * @param maxRecursion how deep the adaptive sampler may subdivide, or a value below 1 for the
   *        default depth
   * @param insetEnds whether the first and the last sample sit a millionth of a grid step of
   *        <code>PlotPoints</code> (by default <code>50</code>) points inside the range
   */
  public static double[][] computePlot(final DoubleUnaryOperator hun, double[][] data,
      final double xMin, final double xMax, String xScale, int plotPoints, int maxRecursion,
      boolean insetEnds) {
    // An explicit PlotPoints asks for that many samples, which is a uniform sweep rather than the
    // adaptive refinement; MaxRecursion only bounds the adaptive one.
    int depth = maxRecursion >= 1 ? Math.min(maxRecursion, 20) : MAXIMUM_DEPTH;
    Series.LineOver1DRangeSeries series = plotPoints >= 2
        ? new Series.LineOver1DRangeSeries(hun, false, xMin, xMax, plotPoints, xScale)
        : new Series.LineOver1DRangeSeries(hun, xMin, xMax, depth, true, xScale);
    if (insetEnds) {
      series.setEndInset(plotPoints >= 2 ? plotPoints - 1 : DEFAULT_PLOT_POINTS - 1);
    }
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
