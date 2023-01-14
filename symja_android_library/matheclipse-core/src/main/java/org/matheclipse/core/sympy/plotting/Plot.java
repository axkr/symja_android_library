package org.matheclipse.core.sympy.plotting;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.DoubleUnaryOperator;
import org.hipparchus.util.MathArrays;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.numpy.Numpy;

public class Plot {
  final static int MAXIMUM_DEPTH = 12;

  private static class Point {
    final double x, y;

    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  /**
   * Representation for a line consisting of a Symja expression over a range. Ported from Sympy
   * <code>LineOver1DRangeSeries</code>
   *
   */
  private static class LineOver1DRangeSeries {

    private final DoubleUnaryOperator f;

    private boolean adaptive;

    private boolean onlyIntegers = false;

    private int maxDepth;

    private double fStart;

    private double fEnd;

    private String xScale = "";

    private int numberOfPoints = 300;

    private final SortedSet<Point> plot = new TreeSet<>((s, t) -> Double.compare(s.x, t.x));

    public LineOver1DRangeSeries(DoubleUnaryOperator f, double start, double end, int maxDepth) {
      this(f, start, end, maxDepth, true, "");
    }

    public LineOver1DRangeSeries(DoubleUnaryOperator f, double start, double end, int maxDepth,
        boolean adaptive, String xScale) {
      this.f = f;
      this.fStart = start;
      this.fEnd = end;
      this.adaptive = adaptive;
      this.maxDepth = maxDepth;
      this.xScale = xScale;
    }

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
     * Checks whether three points are almost collinear.
     * 
     * @param p
     * @param q
     * @param r
     * @return
     */
    private static boolean flat(double[] p, double[] q, double[] r) {
      double linearCombination =
          MathArrays.linearCombination(new double[] {p[0], -p[0], q[0], -q[0], r[0], -r[0]},
              new double[] {q[1], r[1], r[1], p[1], p[1], q[1]});
      return F.isZero(linearCombination, 1e-3);
    }

    /**
     * Samples recursively if three points are almost collinear. For depth < 6, points are added
     * irrespective of whether they satisfy the collinearity condition or not. The maximum depth
     * allowed is 12.
     * 
     * @param p
     * @param q
     * @param depth
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
        plot.add(new Point(q[0], q[1]));
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
        plot.add(new Point(q[0], q[1]));
      }
    }

    private SortedSet<Point> uniformSampling() {
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
        plot.add(new Point(listX[i], evalf(listX[i])));
      }
      return plot;
    }

    /**
     * <pre>
        Return lists of coordinates for plotting. Depending on the
        "adaptive" option, this function will either use an adaptive algorithm
        or it will uniformly sample the expression over the provided range.
    
        Returns
        =======
            x : list
                List of x-coordinates
    
            y : list
                List of y-coordinates
    
    
        Explanation
        ===========
    
        The adaptive sampling is done by recursively checking if three
        points are almost collinear. If they are not collinear, then more
        points are added between those points.
    
        References
        ==========
    
        .. [1] Adaptive polygonal approximation of parametric curves,
               Luiz Henrique de Figueiredo.
     * 
     * </pre>
     * 
     */
    public SortedSet<Point> getPoints() {
      if (onlyIntegers || !adaptive) {
        return uniformSampling();
      }
      double start = evalf(this.fStart);
      double end = evalf(this.fEnd);
      plot.add(new Point(this.fStart, start));
      sample(new double[] {fStart, start}, new double[] {fEnd, end}, 0);
      return plot;
    }

    /**
     * Evaluate the real value of the function. If evaluation is not possible return
     * <code>Double.NaN</code>.
     * 
     * @param x
     * @return if evaluation is not possible return <code>Double.NaN</code>.
     */
    private double evalf(double x) {
      try {
        return f.applyAsDouble(x);
      } catch (ArgumentTypeException ate) {

      }
      return Double.NaN;
    }
  }

  public static double[][] computePlot(final UnaryNumerical hun, double[][] data, final double xMin,
      final double xMax, String xScale) {
    SortedSet<Point> plot =
        new LineOver1DRangeSeries(hun, xMin, xMax, MAXIMUM_DEPTH, true, xScale).getPoints();
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
