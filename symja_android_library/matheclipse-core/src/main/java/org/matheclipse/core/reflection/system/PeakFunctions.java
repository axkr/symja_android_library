package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The peaks of a list of real numbers at the unsmoothed scale <code>0</code>.
 *
 * <p>
 * A peak is a maximal run of equal values which is above its neighbours. A run strictly inside the
 * list is at the mean of its positions and has the sharpness <code>(dl + dr)/L</code>, where
 * <code>L</code> is its length and <code>dl</code>, <code>dr</code> are its heights above the
 * neighbours. A run which touches one end of the list is at that end, counts only if it is at most
 * two elements long and has the sharpness <code>2*d/L^2</code>; a run over the whole list is no
 * peak. The option <code>Padding -&gt; p</code> extends the list by <code>p</code> on both sides.
 */
public class PeakFunctions {

  private PeakFunctions() {}

  /**
   * <code>FindPeaks(list)</code>, <code>FindPeaks(list, 0, sharpness, threshold)</code> - the
   * peaks <code>{position, value}</code> whose sharpness and value reach the cut-offs. With the
   * option <code>InterpolationOrder -&gt; 2</code> or <code>3</code> the maxima of the quadratic
   * or the clamped cubic spline through the points.
   */
  public static final class FindPeaks extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Arguments arguments = Arguments.of(ast, true);
      if (arguments == null) {
        return F.NIL;
      }
      IAST list = (IAST) ast.arg1();
      if (arguments.interpolationOrder >= 2) {
        if (arguments.sharpness != 0.0 || arguments.padding != null || list.argSize() < 2) {
          return F.NIL;
        }
        List<double[]> peaks = arguments.interpolationOrder == 2
            ? quadraticSplinePeaks(arguments.values) : cubicSplinePeaks(arguments.values);
        IASTAppendable result = F.ListAlloc(peaks.size());
        for (double[] peak : peaks) {
          if (peak[1] >= arguments.threshold) {
            result.append(F.list(F.num(peak[0]), F.num(peak[1])));
          }
        }
        return result;
      }
      List<int[]> runs = peakRuns(arguments.values, arguments.sharpness, arguments.threshold,
          arguments.padding);
      IASTAppendable result = F.ListAlloc(runs.size());
      for (int[] run : runs) {
        // run[2] is twice the position
        IExpr position = F.QQ(run[2], 2);
        result.append(F.list(position, list.get(run[0] + 1)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * <code>PeakDetect(list, 0, sharpness, threshold)</code> - <code>1</code> at every element of a
   * peak run and <code>0</code> elsewhere.
   */
  public static final class PeakDetect extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Arguments arguments = Arguments.of(ast, false);
      if (arguments == null || arguments.interpolationOrder >= 0) {
        return F.NIL;
      }
      int[] marks = new int[arguments.values.length];
      for (int[] run : peakRuns(arguments.values, arguments.sharpness, arguments.threshold,
          arguments.padding)) {
        for (int i = run[0]; i <= run[1]; i++) {
          marks[i] = 1;
        }
      }
      return F.mapRange(0, marks.length, i -> F.ZZ(marks[i]));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /** The parsed arguments of both functions. */
  private static final class Arguments {
    double[] values;
    double sharpness = 0.0;
    double threshold = Double.NEGATIVE_INFINITY;
    Double padding = null;
    int interpolationOrder = -1;

    /**
     * @param scaleOptional whether the scale argument may be left out
     * @return <code>null</code> for an unsupported form, e.g. a scale other than <code>0</code>
     */
    static Arguments of(IAST ast, boolean scaleOptional) {
      Arguments arguments = new Arguments();
      int positional = 1;
      while (positional < ast.argSize() && !ast.get(positional + 1).isRuleAST()) {
        positional++;
      }
      for (int i = positional + 1; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (!option.isRuleAST()) {
          return null;
        }
        if (option.first() == S.Padding) {
          double p = option.second().evalfNaN();
          if (!Double.isFinite(p)) {
            return null;
          }
          arguments.padding = p;
        } else if (option.first() == S.InterpolationOrder) {
          int order = option.second().toIntDefault();
          if (order < 0 || order > 3) {
            return null;
          }
          arguments.interpolationOrder = order >= 2 ? order : -1;
        } else {
          return null;
        }
      }
      if (positional > 4 || (positional < 2 && !scaleOptional)) {
        return null;
      }
      if (positional >= 2 && !isZeroScale(ast.arg2())) {
        return null;
      }
      if (positional >= 3) {
        Double s = unsmoothed(ast.arg3());
        if (s == null || s < 0.0) {
          return null;
        }
        arguments.sharpness = s;
      }
      if (positional >= 4) {
        Double t = unsmoothed(ast.arg4());
        if (t == null) {
          return null;
        }
        arguments.threshold = t;
      }
      if (!ast.arg1().isList()) {
        return null;
      }
      IAST list = (IAST) ast.arg1();
      arguments.values = new double[list.argSize()];
      for (int i = 0; i < arguments.values.length; i++) {
        IExpr x = list.get(i + 1);
        if (!x.isReal() && !(x.isNumericFunction() && x.isRealResult())) {
          return null;
        }
        arguments.values[i] = x.evalfNaN();
        if (!Double.isFinite(arguments.values[i])) {
          return null;
        }
      }
      return arguments;
    }

    private static boolean isZeroScale(IExpr scale) {
      return scale.isNumber() && scale.isZero();
    }

    /** A cut-off given as a number or as <code>{value, 0}</code>. */
    private static Double unsmoothed(IExpr expr) {
      if (expr.isList2()) {
        if (!isZeroScale(expr.second())) {
          return null;
        }
        expr = expr.first();
      }
      double value = expr.evalfNaN();
      return Double.isNaN(value) ? null : value;
    }
  }

  /**
   * The peak runs <code>{start, end, twicePosition}</code> (0-based start and end) of
   * <code>values</code>.
   */
  private static List<int[]> peakRuns(double[] values, double sharpness, double threshold,
      Double padding) {
    final int n = values.length;
    List<int[]> peaks = new ArrayList<int[]>();
    int i = 0;
    while (i < n) {
      int j = i;
      while (j + 1 < n && values[j + 1] == values[i]) {
        j++;
      }
      double v = values[i];
      double length = j - i + 1;
      Double left = i > 0 ? Double.valueOf(values[i - 1]) : padding;
      Double right = j + 1 < n ? Double.valueOf(values[j + 1]) : padding;
      boolean leftOk = left == null || left < v;
      boolean rightOk = right == null || right < v;
      int twicePosition = -1;
      double sharp = 0.0;
      if (left == null && right != null && j <= 1) {
        twicePosition = 2;
        sharp = 2.0 * (v - right) / (length * length);
      } else if (left != null && right == null && i + 2 >= n) {
        twicePosition = 2 * n;
        sharp = 2.0 * (v - left) / (length * length);
      } else if (left != null && right != null) {
        twicePosition = i + 1 + j + 1;
        sharp = ((v - left) + (v - right)) / length;
      }
      if (twicePosition > 0 && leftOk && rightOk && sharp >= sharpness && v >= threshold) {
        peaks.add(new int[] {i, j, twicePosition});
      }
      i = j + 1;
    }
    return peaks;
  }

  /**
   * The maxima <code>{x, y}</code> of the cubic spline through the points <code>(i, y_i)</code>
   * with the slope <code>0</code> at both ends.
   */
  private static List<double[]> cubicSplinePeaks(double[] y) {
    final int n = y.length;
    // the second derivatives m of the clamped spline with unit spacing
    double[] diag = new double[n];
    double[] rhs = new double[n];
    for (int i = 0; i < n; i++) {
      diag[i] = (i == 0 || i == n - 1) ? 2.0 : 4.0;
      if (i == 0) {
        rhs[i] = 6.0 * (y[1] - y[0]);
      } else if (i == n - 1) {
        rhs[i] = -6.0 * (y[n - 1] - y[n - 2]);
      } else {
        rhs[i] = 6.0 * (y[i + 1] - 2.0 * y[i] + y[i - 1]);
      }
    }
    for (int i = 1; i < n; i++) {
      double w = 1.0 / diag[i - 1];
      diag[i] -= w;
      rhs[i] -= w * rhs[i - 1];
    }
    double[] m = new double[n];
    m[n - 1] = rhs[n - 1] / diag[n - 1];
    for (int i = n - 2; i >= 0; i--) {
      m[i] = (rhs[i] - m[i + 1]) / diag[i];
    }
    List<double[]> peaks = new ArrayList<double[]>();
    if (m[0] < 0.0) {
      peaks.add(new double[] {1.0, y[0]});
    }
    for (int i = 0; i < n - 1; i++) {
      // y_i + b*t + c*t^2 + d*t^3 on [i, i + 1]
      double b = y[i + 1] - y[i] - (2.0 * m[i] + m[i + 1]) / 6.0;
      double c = m[i] / 2.0;
      double d = (m[i + 1] - m[i]) / 6.0;
      List<Double> roots = new ArrayList<Double>();
      if (Math.abs(d) < 1.0e-300) {
        if (c != 0.0) {
          roots.add(-b / (2.0 * c));
        }
      } else {
        double discriminant = 4.0 * c * c - 12.0 * d * b;
        if (discriminant >= 0.0) {
          double sqrt = Math.sqrt(discriminant);
          roots.add((-2.0 * c + sqrt) / (6.0 * d));
          roots.add((-2.0 * c - sqrt) / (6.0 * d));
        }
      }
      roots.sort(Double::compare);
      for (double t : roots) {
        if (t > 0.0 && t < 1.0 && 2.0 * c + 6.0 * d * t < 0.0) {
          peaks.add(new double[] {i + 1.0 + t, y[i] + t * (b + t * (c + t * d))});
        }
      }
      if (i + 1 < n - 1 && b + 2.0 * c + 3.0 * d == 0.0 && m[i + 1] < 0.0) {
        // a maximum exactly at an interior knot
        peaks.add(new double[] {i + 2.0, y[i + 1]});
      }
    }
    if (m[n - 1] < 0.0) {
      peaks.add(new double[] {n, y[n - 1]});
    }
    return withoutDuplicates(peaks);
  }

  /**
   * The maxima <code>{x, y}</code> of the quadratic spline through the points
   * <code>(i, y_i)</code> with its knots halfway between the points and the slope <code>0</code>
   * at both ends. Around the point <code>i</code> it is <code>y_i + d_i*u + c_i*u^2</code> for
   * <code>|u| &lt;= 1/2</code>.
   */
  private static List<double[]> quadraticSplinePeaks(double[] y) {
    final int n = y.length;
    double[] d = new double[n];
    if (n > 2) {
      // d_(i-1) + 6*d_i + d_(i+1) == 4*(y_(i+1) - y_(i-1)) with d_1 == d_n == 0
      int m = n - 2;
      double[] diag = new double[m];
      double[] rhs = new double[m];
      for (int k = 0; k < m; k++) {
        diag[k] = 6.0;
        rhs[k] = 4.0 * (y[k + 2] - y[k]);
      }
      for (int k = 1; k < m; k++) {
        double w = 1.0 / diag[k - 1];
        diag[k] -= w;
        rhs[k] -= w * rhs[k - 1];
      }
      d[m] = rhs[m - 1] / diag[m - 1];
      for (int k = m - 2; k >= 0; k--) {
        d[k + 1] = (rhs[k] - d[k + 2]) / diag[k];
      }
    }
    List<double[]> peaks = new ArrayList<double[]>();
    for (int i = 0; i < n; i++) {
      double c = i + 1 < n ? (4.0 * (y[i + 1] - y[i]) - 3.0 * d[i] - d[i + 1]) / 2.0
          : (3.0 * d[i] + d[i - 1] - 4.0 * (y[i] - y[i - 1])) / 2.0;
      if (c >= 0.0) {
        continue;
      }
      double u = -d[i] / (2.0 * c);
      double lo = i == 0 ? 0.0 : -0.5;
      double hi = i == n - 1 ? 0.0 : 0.5;
      if ((u >= lo && u < hi) || (u == hi && i == n - 1)) {
        peaks.add(new double[] {i + 1.0 + u, y[i] + d[i] * u + c * u * u});
      }
    }
    return withoutDuplicates(peaks);
  }

  /** Drop a peak which is found again from the next piece. */
  private static List<double[]> withoutDuplicates(List<double[]> peaks) {
    List<double[]> result = new ArrayList<double[]>();
    for (double[] peak : peaks) {
      if (result.isEmpty() || Math.abs(result.get(result.size() - 1)[0] - peak[0]) >= 1.0e-9) {
        result.add(peak);
      }
    }
    return result;
  }
}
