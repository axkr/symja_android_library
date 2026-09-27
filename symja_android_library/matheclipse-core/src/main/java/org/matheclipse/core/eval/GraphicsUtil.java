package org.matheclipse.core.eval;

import java.util.Arrays;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.graphics.SVGGraphics;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class GraphicsUtil {

  /**
   * Automatic y-plot range determination based on robust percentiles (10th-90th) to handle
   * singularities and exponential growth gracefully.
   *
   * @param function the current function
   * @param values current y-values of the current function curve
   * @param yMinMax y-plot range which will be updated [min, max]
   */
  public static void automaticPlotRange2D(final IExpr function, final double[] values,
      double[] yMinMax) {
    if (values == null || values.length == 0) {
      return; // No data to analyze
    }

    if (function != null) {
      int headID = function.headID();
      switch (headID) {
        case ID.Cot:
        case ID.Csc:
        case ID.Sec:
        case ID.Tan:
          setYRange(-7.0, 7.0, yMinMax);
          return;
      }
    }

    // Filter and Sort Data
    // We need a sorted list of finite values to determine percentiles.
    double[] sorted = Arrays.stream(values).filter(Double::isFinite).sorted().toArray();
    int n = sorted.length;

    if (n == 0) {
      return; // All NaNs
    }

    // Identify Core Distribution (10th to 90th percentile)
    // This ignores the extreme tails (asymptotes or exponential explosions).
    double p10 = sorted[(int) (n * 0.10)];
    double p90 = sorted[(int) (n * 0.90)];
    double median = sorted[n / 2];

    double bodyRange = p90 - p10;

    // Handle flat functions (e.g., y=5)
    if (bodyRange < 1.0e-9) {
      double margin = Math.abs(median) * 0.1;
      if (margin < 1.0e-9)
        margin = 1.0;
      setYRange(median - margin, median + margin, yMinMax);
      return;
    }

    // Define "Reasonable" Visual Bounds
    // Expand the core body by a factor (1.5x) to include "interesting" variation
    // but cut off extreme outliers found in the top/bottom 10%.
    double expansionFactor = 1.5;
    double proposedMin = p10 - (bodyRange * expansionFactor);
    double proposedMax = p90 + (bodyRange * expansionFactor);

    // Clamp to Actual Data Limits
    // We never want to show a range *larger* than the actual data exists (empty whitespace).
    // But we *do* want to show a range *smaller* than data if data has singularities.
    double actualMin = sorted[0];
    double actualMax = sorted[n - 1];

    // If proposed bound extends beyond actual data, clamp it.
    // If proposed bound is inside actual data (cutting off singularity), keep it.
    double finalMin = Math.max(proposedMin, actualMin);
    double finalMax = Math.min(proposedMax, actualMax);

    if (actualMin >= 0 && finalMin < 0) {
      finalMin = actualMin;
    }

    setYRange(finalMin, finalMax, yMinMax);
  }

  /**
   * Compute the visible plot range of a sorted list of values.
   *
   * @param values unsorted values
   * @return the min and max value (possibly clipped to exclude outliers)
   */
  public static double[] automaticPlotRange3D(double[] values) {
    if (values == null || values.length == 0) {
      return new double[] {0.0, 1.0};
    }
    Arrays.sort(values);
    int size = values.length;
    double min = values[0];
    double max = values[size - 1];
    if (size > 10) {
      double q1 = values[size / 4];
      double q3 = values[(size * 3) / 4];
      double iqr = q3 - q1;
      if (iqr > 10.0 * Math.ulp(q3)) {
        // Upper fence. 2.0 * IQR is a heuristic to include most data but cut massive poles
        double upperFence = q3 + 2.0 * iqr;
        if (max > upperFence) {
          int idx = Arrays.binarySearch(values, upperFence);
          if (idx < 0) {
            idx = -idx - 1;
          }
          if (idx < size) {
            max = values[idx];
            // Ensure we don't accidentally clamp below the fence if binary search lands oddly
            if (max < upperFence) {
              max = upperFence;
            }
          }
        }
      }
    }
    return new double[] {min, max};
  }

  public static boolean renderGraphics2DSVG(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      boolean withSVGTag, EvalEngine engine) {
    SVGGraphics svg = new SVGGraphics(600, 400);
    String svgString = svg.toSVG(graphics2DAST, withSVGTag);
    graphics2DBuffer.append(svgString);
    return true;
  }

  /**
   * Render to a complete SVG document, including the {@code <svg>} root element. The root carries
   * the size the {@code ImageSize} option asked for, so callers must not wrap the result in a
   * second root of their own.
   */
  public static boolean renderGraphics2DSVG(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      EvalEngine engine) {
    return renderGraphics2DSVG(graphics2DBuffer, graphics2DAST, true, engine);
  }

  public static void setYRange(double vmin, double vmax, double[] yMinMax) {
    if (vmin < yMinMax[0]) {
      yMinMax[0] = vmin;
    }
    if (vmax > yMinMax[1]) {
      yMinMax[1] = vmax;
    }
  }

  private GraphicsUtil() {
    // private constructor to avoid instantiation
  }

}
