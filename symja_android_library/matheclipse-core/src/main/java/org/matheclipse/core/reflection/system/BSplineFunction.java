package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BSplineFunction(points)[u]</code> - the point of the B-spline curve with the given control
 * points at the parameter <code>u</code> in <code>0..1</code>;
 * <code>BSplineFunction(net)[u, v]</code> - the point of the B-spline surface with a rectangular
 * net of control points.
 *
 * <p>
 * The spline is clamped with uniform knots and has the degree 3, or one less than the number of
 * control points where there are fewer than four; <code>SplineDegree -> d</code> or
 * <code>{du, dv}</code> sets it. <code>SplineClosed -> True</code> or <code>{cu, cv}</code> closes
 * the spline in a direction: its control points are repeated periodically, with uniform knots, and
 * the parameter <code>0..1</code> runs once around them. <code>BSplineFunction(array, n)</code>
 * reads the first <code>n</code> levels of <code>array</code> as the net, so a matrix of numbers
 * with <code>n = 2</code> is a surface of numbers. The function itself stays as it is written, and
 * so does a call with a parameter which is no number in <code>0..1</code>, unless
 * <code>Method -> {"Extrapolation" -> "Clamp"}</code> moves such a parameter to the nearest end.
 */
public class BSplineFunction extends AbstractEvaluator {
  public BSplineFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ast.head().isAST(S.BSplineFunction) || ast.head().argSize() < 1) {
      return F.NIL;
    }
    IAST function = (IAST) ast.head();
    int directions = ast.argSize();
    if (directions < 1 || directions > 2) {
      return F.NIL;
    }
    int[] degrees = {3, 3};
    boolean[] closed = {false, false};
    boolean clamp = false;
    int firstOption = 2;
    if (function.size() > 2 && function.arg2().isInteger()) {
      // BSplineFunction(array, n): the net has n directions
      if (function.arg2().toIntDefault() != directions) {
        return F.NIL;
      }
      firstOption = 3;
    }
    for (int i = firstOption; i < function.size(); i++) {
      IExpr option = function.get(i);
      if (!option.isRuleAST()) {
        return F.NIL;
      }
      IExpr value = engine.evaluate(option.second());
      if (option.first() == S.SplineClosed) {
        IExpr u = value.isList() && value.argSize() >= 1 ? value.first() : value;
        IExpr v = value.isList() && value.argSize() >= 2 ? value.second() : u;
        if (!(u.isTrue() || u.isFalse()) || !(v.isTrue() || v.isFalse())) {
          return F.NIL;
        }
        closed[0] = u.isTrue();
        closed[1] = v.isTrue();
      } else if (option.first() == S.Method) {
        IAST settings = value.isList() ? (IAST) value : F.List(value);
        for (IExpr setting : settings) {
          if (setting.isRuleAST() && setting.first().isString("Extrapolation")) {
            clamp = setting.second().isString("Clamp");
          }
        }
      } else if (option.first() == S.SplineDegree) {
        IExpr u = value.isList() && value.argSize() >= 1 ? value.first() : value;
        IExpr v = value.isList() && value.argSize() >= 2 ? value.second() : u;
        degrees[0] = u.toIntDefault(-1);
        degrees[1] = v.toIntDefault(-1);
        if (degrees[0] < 1 || degrees[1] < 1) {
          return F.NIL;
        }
      } else if (!(value == S.Automatic || value.isFalse())) {
        // SplineKnots and SplineWeights are not read
        return F.NIL;
      }
    }

    double[] parameters = new double[directions];
    for (int i = 0; i < directions; i++) {
      IExpr parameter = ast.get(i + 1);
      if (!parameter.isReal()) {
        return F.NIL;
      }
      parameters[i] = parameter.evalf();
      if (clamp) {
        parameters[i] = Math.max(0.0, Math.min(1.0, parameters[i]));
      }
      if (!(parameters[i] >= 0.0 && parameters[i] <= 1.0)) {
        return F.NIL;
      }
    }

    double[][][] net = controlNet(function.arg1(), directions);
    if (net == null) {
      return F.NIL;
    }
    double[] basisU = closed[0] ? periodicBasis(net.length, degrees[0], parameters[0])
        : basis(net.length, degrees[0], parameters[0]);
    double[] basisV = directions == 2
        ? (closed[1] ? periodicBasis(net[0].length, degrees[1], parameters[1])
            : basis(net[0].length, degrees[1], parameters[1]))
        : new double[] {1.0};
    int dimension = net[0][0].length;
    double[] point = new double[dimension];
    for (int i = 0; i < basisU.length; i++) {
      for (int j = 0; j < basisV.length; j++) {
        double weight = basisU[i] * basisV[j];
        if (weight != 0.0) {
          for (int d = 0; d < dimension; d++) {
            point[d] += weight * net[i][j][d];
          }
        }
      }
    }
    IExpr corner = directions == 2 ? function.arg1().first().first() : function.arg1().first();
    if (dimension == 1 && !corner.isList()) {
      // control points which are numbers give a number
      return F.num(point[0]);
    }
    IASTAppendable result = F.ListAlloc(dimension);
    for (double coordinate : point) {
      result.append(F.num(coordinate));
    }
    return result;
  }

  /**
   * The control points as <code>net[i][j]</code>; a curve is a net with one column.
   *
   * @return <code>null</code> if the points are not a rectangular array of real numbers
   */
  private static double[][][] controlNet(IExpr points, int directions) {
    if (!points.isList() || points.argSize() < 2) {
      return null;
    }
    IAST rows = (IAST) points;
    double[][][] net = new double[rows.argSize()][][];
    int dimension = -1;
    for (int i = 1; i <= rows.argSize(); i++) {
      IExpr row = rows.get(i);
      IAST columns = directions == 2 ? (row.isList() ? (IAST) row : null) : F.list(row);
      if (columns == null || columns.argSize() < (directions == 2 ? 2 : 1)
          || (i > 1 && columns.argSize() != net[0].length)) {
        return null;
      }
      net[i - 1] = new double[columns.argSize()][];
      for (int j = 1; j <= columns.argSize(); j++) {
        IExpr point = columns.get(j);
        double[] coordinates = point.isList() ? ((IAST) point).toDoubleVector()
            : point.isReal() ? new double[] {point.evalf()} : null;
        if (coordinates == null || coordinates.length == 0
            || (dimension >= 0 && coordinates.length != dimension)) {
          return null;
        }
        dimension = coordinates.length;
        net[i - 1][j - 1] = coordinates;
      }
    }
    return net;
  }

  /**
   * The values at <code>u</code> of the <code>n</code> basis functions of a clamped B-spline with
   * uniform knots (Cox - de Boor recursion).
   */
  private static double[] basis(int n, int degree, double u) {
    int d = Math.min(degree, n - 1);
    // d + 1 knots at 0, the inner knots, d + 1 knots at 1
    double[] knots = new double[n + d + 1];
    int spans = n - d;
    for (int i = 0; i < knots.length; i++) {
      knots[i] = i <= d ? 0.0 : i >= n ? 1.0 : (double) (i - d) / spans;
    }
    // the span which holds u; u = 1 belongs to the last one
    int span = d;
    while (span < n - 1 && u >= knots[span + 1]) {
      span++;
    }
    double[] values = new double[n];
    values[span] = 1.0;
    for (int k = 1; k <= d; k++) {
      for (int i = span - k; i <= span; i++) {
        if (i < 0) {
          continue;
        }
        double left = 0.0;
        double denominator = knots[i + k] - knots[i];
        if (denominator > 0.0) {
          left = (u - knots[i]) / denominator * values[i];
        }
        double right = 0.0;
        if (i + 1 < n) {
          denominator = knots[i + k + 1] - knots[i + 1];
          if (denominator > 0.0) {
            right = (knots[i + k + 1] - u) / denominator * values[i + 1];
          }
        }
        values[i] = left + right;
      }
    }
    return values;
  }

  /**
   * The values at <code>u</code> of the <code>n</code> basis functions of a closed B-spline: the
   * control points repeat with the period <code>n</code> on uniform knots, and <code>u = k / n</code>
   * is the centre of the basis function of the <code>k</code>-th point, counted from 0.
   */
  private static double[] periodicBasis(int n, int degree, double u) {
    double[] values = new double[n];
    double t = u * n;
    // the basis function reaches (degree + 1) / 2 to both sides of its centre
    int wraps = (degree + 1) / (2 * n) + 2;
    for (int k = 0; k < n; k++) {
      double sum = 0.0;
      for (int m = -wraps; m <= wraps; m++) {
        sum += centredBSpline(degree, t - k - (double) m * n);
      }
      values[k] = sum;
    }
    return values;
  }

  /** The cardinal B-spline of the given degree on uniform knots, centred at 0. */
  private static double centredBSpline(int degree, double x) {
    double y = x + (degree + 1) / 2.0;
    if (y <= 0.0 || y >= degree + 1) {
      return 0.0;
    }
    // (1 / d!) sum_j (-1)^j binomial(d + 1, j) (y - j)_+^d
    double sum = 0.0;
    double binomial = 1.0;
    for (int j = 0; j <= degree + 1; j++) {
      if (y > j) {
        double term = binomial * Math.pow(y - j, degree);
        sum += (j % 2 == 0) ? term : -term;
      }
      binomial = binomial * (degree + 1 - j) / (j + 1);
    }
    double factorial = 1.0;
    for (int j = 2; j <= degree; j++) {
      factorial *= j;
    }
    return sum / factorial;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // the third element lets the call with the function as its head, f[points][u], through
    return ARGS_1_INFINITY_0;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
