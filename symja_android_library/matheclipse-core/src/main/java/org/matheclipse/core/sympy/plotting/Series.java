package org.matheclipse.core.sympy.plotting;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.numpy.Numpy;

public class Series {

  /**
   * Representation for a line consisting of a numerical function over a range. This is a Java port
   * of Sympy's LineOver1DRangeSeries used for sampling a function for plotting. The sampling can be
   * either uniform or adaptive (recursively subdividing intervals until points are almost
   * collinear).
   */
  static class LineOver1DRangeSeries {

    private final DoubleUnaryOperator f;

    private boolean adaptive;

    private boolean onlyIntegers = false;

    private int maxDepth;

    private double fStart;

    private double fEnd;

    private String xScale = "";

    private int numberOfPoints = 300;

    private final SortedSet<Plot.Point> plot = new TreeSet<>((s, t) -> Double.compare(s.x, t.x));

    /**
     * Create an adaptive sampling series over [start, end].
     *
     * @param f function to sample
     * @param start start of range
     * @param end end of range
     * @param maxDepth maximum recursion depth
     */
    public LineOver1DRangeSeries(DoubleUnaryOperator f, double start, double end, int maxDepth) {
      this(f, start, end, maxDepth, true, "");
    }

    /**
     * Create a sampling series with explicit adaptive flag and x-scale.
     *
     * @param f function to sample
     * @param start start of range
     * @param end end of range
     * @param maxDepth maximum recursion depth
     * @param adaptive whether to use adaptive sampling
     * @param xScale scale for the x-axis (e.g. "Log", "Log10", "Log2")
     */
    public LineOver1DRangeSeries(DoubleUnaryOperator f, double start, double end, int maxDepth,
        boolean adaptive, String xScale) {
      this.f = f;
      this.fStart = start;
      this.fEnd = end;
      this.adaptive = adaptive;
      this.maxDepth = maxDepth;
      this.xScale = xScale;
    }

    /**
     * Create a uniform sampling series that samples either integer x values or a fixed number of
     * points.
     *
     * @param f function to sample
     * @param onlyIntegers if true sample only integer x-values in [start, end]
     * @param start start of range
     * @param end end of range
     * @param numberOfPoints number of points to sample when onlyIntegers is false
     * @param xScale x-axis scale ("Log", "Log10", "Log2" or empty for linear)
     */
    public LineOver1DRangeSeries(DoubleUnaryOperator f, boolean onlyIntegers, double start,
        double end, int numberOfPoints, String xScale) {
      this.f = f;
      this.fStart = start;
      this.fEnd = end;
      this.adaptive = false;
      this.maxDepth = 0;
      this.xScale = xScale;
      this.onlyIntegers = onlyIntegers;
      this.numberOfPoints = numberOfPoints;
    }

    /**
     * Checks whether three points are almost collinear. This is used by the adaptive sampling
     * algorithm to decide whether further subdivision is necessary.
     *
     * @param p first point as [x,y]
     * @param q middle point as [x,y]
     * @param r last point as [x,y]
     * @return true if the three points are approximately collinear
     */
    private static boolean flat(double[] p, double[] q, double[] r) {
      double linearCombination =
          MathArrays.linearCombination(new double[] {p[0], -p[0], q[0], -q[0], r[0], -r[0]},
              new double[] {q[1], r[1], r[1], p[1], p[1], q[1]});
      return F.isZero(linearCombination, 1e-3);
    }

    /**
     * Recursively sample between two points if the curve is not sufficiently flat. For small depths
     * points are always sampled to avoid aliasing. The sampling respects the configured xScale for
     * log and linear spacing.
     *
     * @param p left endpoint as [x,y]
     * @param q right endpoint as [x,y]
     * @param depth current recursion depth
     */
    private void sample(double[] p, double[] q, int depth) {
      double random = 0.45 + Math.random() * 0.1;
      double xnew;
      if (xScale.equals("Log") || xScale.equals("Log10")) {
        xnew = Math.pow(10.0, (Math.log10(p[0]) + random * (Math.log10(q[0]) - Math.log10(p[0]))));
      } else if (xScale.equals("Log2")) {
        double log2 = Math.log(2.0);
        double p0DLog2 = Math.log(p[0] / log2);
        xnew = Math.pow(2.0, (p0DLog2 + random * (Math.log(q[0] / log2) - p0DLog2)));
      } else {
        xnew = p[0] + random * (q[0] - p[0]);
      }
      double ynew = evalf(xnew);
      double[] newPoint = new double[] {xnew, ynew};

      // Maximum depth
      if (depth > this.maxDepth) {
        plot.add(new Plot.Point(q[0], q[1]));
      } else if (depth < this.maxDepth / 2) {
        // Sample irrespective of whether the line is flat till the
        // depth of 6. We are not using linspace to avoid aliasing.
        sample(p, newPoint, depth + 1);
        sample(newPoint, q, depth + 1);
      } else if (Double.isNaN(p[1]) && Double.isNaN(q[1])) {
        // Sample ten points if complex values are encountered
        // at both ends. If there is a real value in between, then
        // sample those points further.

        double[] xarray;
        if (xScale.equals("Log") || xScale.equals("Log10")) {
          xarray = Numpy.logspace(p[0], q[0], 10, true, 10.0);
        } else if (xScale.equals("Log2")) {
          xarray = Numpy.logspace(p[0], q[0], 10, true, 2.0);
        } else {
          xarray = Numpy.linspace(p[0], q[0], 10, true);
        }

        double[] yarray = new double[xarray.length];
        for (int i = 0; i < xarray.length; i++) {
          yarray[i] = evalf(xarray[i]);
        }
        for (int i = 0; i < yarray.length - 1; i++) {
          if (!(Double.isNaN(yarray[i]) && Double.isNaN(yarray[i + 1]))) {
            sample(new double[] {xarray[i], yarray[i]}, new double[] {xarray[i + 1], yarray[i + 1]},
                depth + 1);
          }
        }
      } else if (Double.isNaN(p[1]) || Double.isNaN(q[1]) || Double.isNaN(newPoint[1])
          || !flat(p, newPoint, q)) {
        // Sample further if one of the end points is NaN (i.e. a
        // complex value) or the three points are not almost collinear.
        sample(p, newPoint, depth + 1);
        sample(newPoint, q, depth + 1);
      } else {
        plot.add(new Plot.Point(q[0], q[1]));
      }
    }

    /**
     * Uniformly sample the function over the configured range. If onlyIntegers is true, sample
     * integer x-values; otherwise sample numberOfPoints points.
     *
     * @return a sorted set of Points representing sampled coordinates
     */
    private SortedSet<Plot.Point> uniformSampling() {
      double[] listX;
      if (onlyIntegers) {
        if (xScale.equals("Log") || xScale.equals("Log10")) {
          listX = Numpy.logspace(fStart, fEnd, ((int) fEnd) - ((int) fStart) + 1, true, 10.0);
        } else if (xScale.equals("Log2")) {
          listX = Numpy.logspace(fStart, fEnd, ((int) fEnd) - ((int) fStart) + 1, true, 2.0);
        } else {
          listX = Numpy.linspace(fStart, fEnd, ((int) fEnd) - ((int) fStart) + 1, true);
        }
      } else {
        if (xScale.equals("Log") || xScale.equals("Log10")) {
          listX = Numpy.logspace(fStart, fEnd, numberOfPoints, true, 10.0);
        } else if (xScale.equals("Log2")) {
          listX = Numpy.logspace(fStart, fEnd, numberOfPoints, true, 2.0);
        } else {
          listX = Numpy.linspace(fStart, fEnd, numberOfPoints, true);
        }
      }
      for (int i = 0; i < listX.length; i++) {
        plot.add(new Plot.Point(listX[i], evalf(listX[i])));
      }
      return plot;
    }

    /**
     * Return lists of coordinates for plotting. Depending on the "adaptive" option, this function
     * will either use an adaptive algorithm or it will uniformly sample the expression over the
     * provided range.
     *
     * @return sorted set of Points containing sampled coordinates
     */
    public SortedSet<Plot.Point> getPoints() {
      if (onlyIntegers || !adaptive) {
        return uniformSampling();
      }
      double start = evalf(this.fStart);
      double end = evalf(this.fEnd);
      plot.add(new Plot.Point(this.fStart, start));
      sample(new double[] {fStart, start}, new double[] {fEnd, end}, 0);
      return plot;
    }

    /**
     * Evaluate the real value of the function. If evaluation is not possible return Double.NaN.
     *
     * @param x input x value
     * @return function value or Double.NaN when not evaluable
     */
    private double evalf(double x) {
      try {
        return f.applyAsDouble(x);
      } catch (ArgumentTypeException ate) {
        // ate.printStackTrace();
      }
      return Double.NaN;
    }
  }

}
