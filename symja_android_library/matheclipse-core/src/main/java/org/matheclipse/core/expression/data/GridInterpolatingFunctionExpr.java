package org.matheclipse.core.expression.data;

import java.io.Serializable;
import java.util.Arrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * An interpolating function of <code>n</code> variables through values given on a complete grid,
 * in one variable optionally together with derivatives at the grid points.
 *
 * <p>
 * Two kinds of interpolation are supported:
 * <ul>
 * <li>local polynomials: along each axis, on the interval between two neighbouring grid points the
 * polynomial satisfying every value and derivative given at both ends, extended by whole
 * neighbouring points - the one nearer to the interval first, of two at the same distance the right
 * one - until it has at least <code>order+1</code> conditions. In more variables the function is the tensor product of
 * these pieces, the last axis innermost. The polynomial is built in the Newton form with confluent
 * divided differences, so exact data at an exact point gives an exact value.</li>
 * <li>an interpolating B-spline of one variable of the degree <code>order</code>, with a clamped
 * knot vector whose interior knots are averages of <code>order</code> consecutive abscissae and
 * control points solving the collocation system.</li>
 * </ul>
 * The value is linear in the data, so each axis contributes a vector of weights for the data at the
 * points around the argument, which also holds for vector or tensor values. A derivative of the
 * function shares the data and only records the order of differentiation along each axis.
 */
public class GridInterpolatingFunctionExpr
    extends InterpolatingFunctionExpr<GridInterpolatingFunctionExpr.Model> {

  private static final long serialVersionUID = -2747106513338346236L;

  /** The data of an interpolating function, shared by all its derivatives. */
  public static final class Model implements Serializable {
    private static final long serialVersionUID = 6006262245106758893L;

    /** The increasing coordinates along each axis. */
    final IExpr[][] coordinates;

    /** The machine values of {@link #coordinates}, to locate an argument. */
    final double[][] coordinatesN;

    /** The interpolation order along each axis. */
    final int[] orders;

    /**
     * For each grid point, in the order of its multi-index with the last axis innermost, the value
     * followed by the derivatives given there (only one variable can have derivatives).
     */
    final IExpr[][] data;

    /** The B-spline knots of one variable, or <code>null</code> for local polynomials. */
    final IExpr[] knots;

    /** The B-spline control points belonging to {@link #knots}. */
    final IExpr[] controls;

    Model(IExpr[][] coordinates, int[] orders, IExpr[][] data, IExpr[] knots, IExpr[] controls) {
      this.coordinates = coordinates;
      this.orders = orders;
      this.data = data;
      this.knots = knots;
      this.controls = controls;
      this.coordinatesN = new double[coordinates.length][];
      for (int i = 0; i < coordinates.length; i++) {
        coordinatesN[i] = new double[coordinates[i].length];
        for (int j = 0; j < coordinates[i].length; j++) {
          coordinatesN[i][j] = coordinates[i][j].evalf();
        }
      }
    }

    int dimension() {
      return coordinates.length;
    }
  }

  /** The order of differentiation along each axis. */
  private final int[] derivativeOrders;

  /** The spline of the derivative of the order <code>derivativeOrders[0]</code>, if any. */
  private final IExpr[] splineKnots;
  private final IExpr[] splineControls;
  private final int splineDegree;

  private GridInterpolatingFunctionExpr(Model model, int[] derivativeOrders, IExpr[] splineKnots,
      IExpr[] splineControls, int splineDegree) {
    super(model, model.coordinatesN[0][0],
        model.coordinatesN[0][model.coordinatesN[0].length - 1]);
    this.derivativeOrders = derivativeOrders;
    this.splineKnots = splineKnots;
    this.splineControls = splineControls;
    this.splineDegree = splineDegree;
  }

  private GridInterpolatingFunctionExpr(Model model) {
    this(model, new int[model.dimension()], model.knots, model.controls,
        model.knots == null ? 0 : model.orders[0]);
  }

  /**
   * Local polynomial interpolation through the data at the points of a complete grid.
   *
   * @param coordinates the increasing coordinates along each axis
   * @param data for each grid point, last axis innermost, the value followed by the derivatives
   *        there; a function of more than one variable takes values only
   * @param orders the requested interpolation order along each axis; an order higher than the data
   *        of an axis allows is reduced with a message
   * @param engine the evaluation engine for messages
   */
  public static GridInterpolatingFunctionExpr newLocal(IExpr[][] coordinates, IExpr[][] data,
      int[] orders, EvalEngine engine) {
    int[] reduced = orders.clone();
    boolean isReduced = false;
    for (int axis = 0; axis < coordinates.length; axis++) {
      // the number of conditions along the axis: the points, and in one variable their derivatives
      int conditions = coordinates[axis].length;
      if (coordinates.length == 1) {
        conditions = 0;
        for (IExpr[] point : data) {
          conditions += point.length;
        }
      }
      if (reduced[axis] + 1 > conditions) {
        reduced[axis] = conditions - 1;
        isReduced = true;
      }
    }
    if (isReduced) {
      // Requested order is too high; order has been reduced to `1`.
      Errors.printMessage(S.Interpolation, "inhr", F.list(toList(reduced)), engine);
    }
    return new GridInterpolatingFunctionExpr(new Model(coordinates, reduced, data, null, null));
  }

  /**
   * The interpolating B-spline of the degree <code>degree</code> through the points
   * <code>(xs[i], ys[i])</code>.
   *
   * @param xs the strictly increasing abscissae
   * @param ys the values, numbers or tensors of one shape
   * @param degree the requested degree; one higher than the number of points allows is reduced
   *        with a message
   * @param engine the evaluation engine for messages
   * @return <code>null</code> if the collocation system has no solution
   */
  public static GridInterpolatingFunctionExpr newSpline(IExpr[] xs, IExpr[] ys, int degree,
      EvalEngine engine) {
    final int n = xs.length;
    int p = degree;
    if (p > n - 1) {
      p = n - 1;
      // Requested order is too high; order has been reduced to `1`.
      Errors.printMessage(S.Interpolation, "inhr", F.list(F.list(F.ZZ(p))), engine);
    }
    // clamped knots with the interior ones averaging p consecutive abscissae
    IExpr[] knots = new IExpr[n + p + 1];
    for (int i = 0; i <= p; i++) {
      knots[i] = xs[0];
      knots[n + i] = xs[n - 1];
    }
    for (int j = 1; j <= n - p - 1; j++) {
      IExpr sum = F.C0;
      for (int i = j; i < j + p; i++) {
        sum = sum.plus(xs[i]);
      }
      knots[j + p] = sum.divide(F.ZZ(p));
    }
    // the collocation matrix is banded: row i has its non-zero entries in the columns
    // span-p..span of the knot interval containing xs[i]
    final int width = p;
    IExpr[][] band = new IExpr[n][2 * width + 1];
    IExpr[] rhs = ys.clone();
    for (int i = 0; i < n; i++) {
      Arrays.fill(band[i], F.C0);
      int span = findSpan(knots, n, p, xs[i].evalf());
      IExpr[] basis = basisFunctions(knots, span, p, xs[i]);
      for (int k = 0; k <= p; k++) {
        int column = span - p + k;
        int offset = column - i + width;
        if (offset < 0 || offset > 2 * width) {
          if (!basis[k].isZero()) {
            return null;
          }
          continue;
        }
        band[i][offset] = basis[k];
      }
    }
    // Gaussian elimination without pivoting keeps the band; the matrix of an interpolating
    // spline is totally positive, so no pivoting is needed
    for (int k = 0; k < n; k++) {
      IExpr pivot = band[k][width];
      if (pivot.isZero()) {
        return null;
      }
      for (int i = k + 1; i <= Math.min(n - 1, k + width); i++) {
        IExpr factor = band[i][k - i + width];
        if (factor.isZero()) {
          continue;
        }
        factor = factor.divide(pivot);
        for (int j = k; j <= Math.min(n - 1, k + width); j++) {
          band[i][j - i + width] =
              band[i][j - i + width].subtract(factor.times(band[k][j - k + width]));
        }
        rhs[i] = addScaled(rhs[i], factor.negate(), rhs[k]);
      }
    }
    IExpr[] controls = new IExpr[n];
    for (int i = n - 1; i >= 0; i--) {
      IExpr sum = rhs[i];
      for (int j = i + 1; j <= Math.min(n - 1, i + width); j++) {
        sum = addScaled(sum, band[i][j - i + width].negate(), controls[j]);
      }
      controls[i] = scale(F.C1.divide(band[i][width]), sum);
    }
    IExpr[][] data = new IExpr[n][];
    for (int i = 0; i < n; i++) {
      data[i] = new IExpr[] {ys[i]};
    }
    return new GridInterpolatingFunctionExpr(
        new Model(new IExpr[][] {xs}, new int[] {p}, data, knots, controls));
  }

  @Override
  public IExpr copy() {
    return new GridInterpolatingFunctionExpr(fData, derivativeOrders, splineKnots, splineControls,
        splineDegree);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    GridInterpolatingFunctionExpr other = (GridInterpolatingFunctionExpr) obj;
    return fData == other.fData && Arrays.equals(derivativeOrders, other.derivativeOrders);
  }

  @Override
  public int hashCode() {
    return 31 * System.identityHashCode(fData) + Arrays.hashCode(derivativeOrders);
  }

  /**
   * The derivative of the orders <code>orders</code> along the axes, which shares the data.
   */
  @Override
  public IExpr derivative(int[] orders) {
    Model model = fData;
    if (orders.length != model.dimension()) {
      return F.NIL;
    }
    int[] total = new int[orders.length];
    for (int i = 0; i < orders.length; i++) {
      if (orders[i] < 0) {
        return F.NIL;
      }
      total[i] = derivativeOrders[i] + orders[i];
    }
    if (model.knots == null) {
      return new GridInterpolatingFunctionExpr(model, total, null, null, 0);
    }
    // the derivative of a B-spline is a B-spline of one degree less on the inner knots
    IExpr[] knots = splineKnots;
    IExpr[] controls = splineControls;
    int degree = splineDegree;
    for (int r = 0; r < orders[0]; r++) {
      if (degree == 0) {
        controls = new IExpr[] {zero(controls[0])};
        knots = new IExpr[] {knots[0], knots[knots.length - 1]};
        break;
      }
      IExpr[] derived = new IExpr[controls.length - 1];
      for (int i = 0; i < derived.length; i++) {
        IExpr factor = F.ZZ(degree).divide(knots[i + degree + 1].subtract(knots[i + 1]));
        derived[i] = scale(factor, addScaled(controls[i + 1], F.CN1, controls[i]));
      }
      controls = derived;
      knots = Arrays.copyOfRange(knots, 1, knots.length - 1);
      degree--;
    }
    return new GridInterpolatingFunctionExpr(model, total, knots, controls, degree);
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    Model model = fData;
    final int dimension = model.dimension();
    if (ast.isAST1() && ast.arg1().isString()) {
      return property(ast.arg1().toString());
    }
    if (dimension == 1 && ast.isAST1() && ast.arg1().isList()) {
      // the values at a list of points
      return ((IAST) ast.arg1()).map(x -> engine.evaluate(F.unaryAST1(this, x)));
    }
    if (ast.argSize() != dimension) {
      return F.NIL;
    }
    IExpr[] x = new IExpr[dimension];
    double[] xN = new double[dimension];
    boolean inexact = false;
    boolean outside = false;
    for (int i = 0; i < dimension; i++) {
      IExpr arg = ast.get(i + 1);
      if (arg.isReal()) {
        x[i] = arg;
        xN[i] = arg.evalf();
        inexact |= arg.isInexactNumber();
      } else {
        double value = arg.isComplexNumeric() || arg.isComplex() ? Double.NaN : arg.evalfNaN();
        if (Double.isNaN(value)) {
          // a symbolic argument stays unevaluated
          return F.NIL;
        }
        x[i] = F.num(value);
        xN[i] = value;
        inexact = true;
      }
      double[] axis = model.coordinatesN[i];
      if (xN[i] < axis[0] || xN[i] > axis[axis.length - 1]) {
        outside = true;
      }
    }
    if (outside) {
      // Input value `1` lies outside the range of data in the interpolating function.
      // Extrapolation will be used.
      Errors.printMessage(S.InterpolatingFunction, "dmval", F.list(F.List(x)), engine);
    }
    IExpr result =
        model.knots != null ? splineValue(x[0], xN[0]) : localValue(model, x, xN);
    if (inexact) {
      result = engine.evalN(result);
    }
    return result;
  }

  /** The value of the local polynomial interpolation, or of a derivative of it, at the point. */
  private IExpr localValue(Model model, IExpr[] x, double[] xN) {
    final int dimension = model.dimension();
    int[][] points = new int[dimension][];
    int[][] derivatives = new int[dimension][];
    IExpr[][] weights = new IExpr[dimension][];
    for (int axis = 0; axis < dimension; axis++) {
      IExpr[] coordinates = model.coordinates[axis];
      int[] multiplicities = new int[coordinates.length];
      for (int i = 0; i < coordinates.length; i++) {
        multiplicities[i] = dimension == 1 ? model.data[i].length : 1;
      }
      int interval = findInterval(model.coordinatesN[axis], xN[axis]);
      // the points of the piece: both ends, then whole neighbours, the one nearer to the interval
      // first and the right one of two at the same distance - on an evenly spaced axis
      // alternately the right and the left one
      double[] axisN = model.coordinatesN[axis];
      int count = multiplicities[interval];
      int left = interval - 1;
      int right = interval;
      if (coordinates.length > 1) {
        count += multiplicities[interval + 1];
        right = interval + 1;
      }
      while (count < model.orders[axis] + 1 && (left >= 0 || right < coordinates.length - 1)) {
        boolean takeRight;
        if (left < 0) {
          takeRight = true;
        } else if (right >= coordinates.length - 1) {
          takeRight = false;
        } else {
          double leftDistance = axisN[interval] - axisN[left];
          double rightDistance = axisN[right + 1] - axisN[interval + 1];
          takeRight = rightDistance <= leftDistance
              + 1.0e-12 * Math.max(Math.abs(leftDistance), Math.abs(rightDistance));
        }
        if (takeRight) {
          right++;
          count += multiplicities[right];
        } else {
          count += multiplicities[left];
          left--;
        }
      }
      // the conditions of the piece, the points in increasing order
      int[] point = new int[count];
      int[] derivative = new int[count];
      IExpr[] nodes = new IExpr[count];
      int c = 0;
      for (int i = left + 1; i <= right; i++) {
        for (int k = 0; k < multiplicities[i]; k++) {
          point[c] = i;
          derivative[c] = k;
          nodes[c] = coordinates[i];
          c++;
        }
      }
      points[axis] = point;
      derivatives[axis] = derivative;
      weights[axis] = newtonWeights(nodes, point, derivative, x[axis], derivativeOrders[axis]);
    }
    IExpr[] sum = new IExpr[1];
    combine(model, points, derivatives, weights, 0, 0, 0, F.C1, sum);
    return sum[0] == null ? zero(model.data[0][0]) : sum[0];
  }

  /**
   * Adds to <code>sum[0]</code> the data at the grid points of the pieces of the axes from
   * <code>axis</code> on, multiplied by their weights.
   *
   * @param index the multi-index of the grid point chosen along the axes before <code>axis</code>
   * @param derivative the order of the derivative in the data, only in a function of one variable
   * @param weight the product of the weights chosen along the axes before <code>axis</code>
   */
  private static void combine(Model model, int[][] points, int[][] derivatives,
      IExpr[][] weights, int axis, int index, int derivative, IExpr weight, IExpr[] sum) {
    if (axis == model.dimension()) {
      IExpr value = model.data[index][derivative];
      sum[0] = sum[0] == null ? scale(weight, value) : addScaled(sum[0], weight, value);
      return;
    }
    int stride = model.coordinates[axis].length;
    for (int c = 0; c < points[axis].length; c++) {
      IExpr w = weights[axis][c];
      if (w.isZero()) {
        continue;
      }
      combine(model, points, derivatives, weights, axis + 1, index * stride + points[axis][c],
          axis == 0 ? derivatives[0][c] : derivative, weight.times(w), sum);
    }
  }

  /**
   * The weights of the conditions in the value of the <code>order</code>-th derivative at
   * <code>x</code> of the polynomial satisfying them, from the Newton form with confluent divided
   * differences.
   *
   * @param nodes the abscissa of each condition, equal ones adjacent
   * @param point the grid point of each condition, equal ones adjacent
   * @param derivative the order of the derivative each condition gives, increasing from
   *        <code>0</code> at each point
   * @param x the argument
   * @param order the order of the derivative to evaluate
   */
  private static IExpr[] newtonWeights(IExpr[] nodes, int[] point, int[] derivative, IExpr x,
      int order) {
    final int m = nodes.length;
    // first[j] is the condition giving the value at the point of condition j
    int[] first = new int[m];
    IExpr[][] table = new IExpr[m][];
    for (int j = 0; j < m; j++) {
      first[j] = j - derivative[j];
      table[j] = unit(m, first[j]);
    }
    IExpr[][] coefficients = new IExpr[m][];
    coefficients[0] = table[0];
    for (int level = 1; level < m; level++) {
      for (int j = 0; j < m - level; j++) {
        if (point[j] == point[j + level]) {
          // level+1 equal nodes: the derivative of the order level, divided by level!
          IExpr[] w = unit(m, -1);
          w[first[j] + level] = F.C1.divide(F.ZZ(level).factorial());
          table[j] = w;
        } else {
          IExpr denominator = nodes[j + level].subtract(nodes[j]);
          IExpr[] w = new IExpr[m];
          for (int c = 0; c < m; c++) {
            w[c] = table[j + 1][c].subtract(table[j][c]).divide(denominator);
          }
          table[j] = w;
        }
      }
      coefficients[level] = table[0];
    }
    // Horner's scheme for the polynomial and its derivatives up to the order asked for
    IExpr[][] p = new IExpr[order + 1][];
    p[0] = coefficients[m - 1].clone();
    for (int r = 1; r <= order; r++) {
      p[r] = unit(m, -1);
    }
    for (int j = m - 2; j >= 0; j--) {
      IExpr factor = x.subtract(nodes[j]);
      for (int r = order; r >= 1; r--) {
        IExpr rr = F.ZZ(r);
        for (int c = 0; c < m; c++) {
          p[r][c] = factor.times(p[r][c]).plus(rr.times(p[r - 1][c]));
        }
      }
      for (int c = 0; c < m; c++) {
        p[0][c] = factor.times(p[0][c]).plus(coefficients[j][c]);
      }
    }
    return p[order];
  }

  /** The vector of length <code>m</code> with <code>1</code> at <code>index</code>. */
  private static IExpr[] unit(int m, int index) {
    IExpr[] result = new IExpr[m];
    Arrays.fill(result, F.C0);
    if (index >= 0) {
      result[index] = F.C1;
    }
    return result;
  }

  /**
   * The index <code>i</code> of the interval <code>[axis[i], axis[i+1]]</code> containing
   * <code>x</code>, the first or the last one for a point outside, <code>0</code> for an axis of a
   * single point.
   */
  private static int findInterval(double[] axis, double x) {
    if (axis.length < 2) {
      return 0;
    }
    int low = 0;
    int high = axis.length - 2;
    while (low < high) {
      int middle = (low + high + 1) >>> 1;
      if (axis[middle] <= x) {
        low = middle;
      } else {
        high = middle - 1;
      }
    }
    return low;
  }

  /**
   * The knot span <code>k</code> with <code>knots[k] <= x < knots[k+1]</code> of a clamped knot
   * vector for <code>n</code> control points, the last non-empty one at its right end.
   */
  private static int findSpan(IExpr[] knots, int n, int p, double x) {
    int low = p;
    int high = n - 1;
    while (low < high) {
      int middle = (low + high + 1) >>> 1;
      if (knots[middle].evalf() <= x) {
        low = middle;
      } else {
        high = middle - 1;
      }
    }
    return low;
  }

  /**
   * The values at <code>x</code> of the <code>p+1</code> B-splines of the degree <code>p</code>
   * which don't vanish in the knot span <code>span</code>.
   */
  private static IExpr[] basisFunctions(IExpr[] knots, int span, int p, IExpr x) {
    IExpr[] basis = new IExpr[p + 1];
    IExpr[] left = new IExpr[p + 1];
    IExpr[] right = new IExpr[p + 1];
    basis[0] = F.C1;
    for (int j = 1; j <= p; j++) {
      left[j] = x.subtract(knots[span + 1 - j]);
      right[j] = knots[span + j].subtract(x);
      IExpr saved = F.C0;
      for (int r = 0; r < j; r++) {
        IExpr temp = basis[r].divide(right[r + 1].plus(left[j - r]));
        basis[r] = saved.plus(right[r + 1].times(temp));
        saved = left[j - r].times(temp);
      }
      basis[j] = saved;
    }
    return basis;
  }

  /** The value of the spline, or of the derivative this function stands for, by de Boor. */
  private IExpr splineValue(IExpr x, double xN) {
    final int p = splineDegree;
    final int n = splineControls.length;
    int span = findSpan(splineKnots, n, p, xN);
    IExpr[] d = new IExpr[p + 1];
    for (int j = 0; j <= p; j++) {
      d[j] = splineControls[j + span - p];
    }
    for (int r = 1; r <= p; r++) {
      for (int j = p; j >= r; j--) {
        IExpr low = splineKnots[j + span - p];
        IExpr alpha = x.subtract(low).divide(splineKnots[j + 1 + span - r].subtract(low));
        d[j] = addScaled(scale(F.C1.subtract(alpha), d[j - 1]), alpha, d[j]);
      }
    }
    return d[p];
  }

  /** The properties <code>f("Domain")</code>, <code>f("Grid")</code>, ... */
  private IExpr property(String property) {
    Model model = fData;
    final int dimension = model.dimension();
    switch (property) {
      case "Domain": {
        IASTAppendable domain = F.ListAlloc(dimension);
        for (IExpr[] axis : model.coordinates) {
          domain.append(F.list(axis[0], axis[axis.length - 1]));
        }
        return domain;
      }
      case "InterpolationOrder":
        return toList(model.orders);
      case "Coordinates": {
        IASTAppendable coordinates = F.ListAlloc(dimension);
        for (IExpr[] axis : model.coordinates) {
          coordinates.append(F.List(axis));
        }
        return coordinates;
      }
      case "Grid":
        return gridArray(model, 0, 0, new IExpr[dimension], true);
      case "ValuesOnGrid":
        return gridArray(model, 0, 0, new IExpr[dimension], false);
      default:
        return F.NIL;
    }
  }

  /**
   * The array of the coordinate tuples (<code>grid == true</code>) or of the values at the grid
   * points along the axes from <code>axis</code> on.
   */
  private static IExpr gridArray(Model model, int axis, int index, IExpr[] tuple, boolean grid) {
    if (axis == model.dimension()) {
      return grid ? F.List(tuple.clone()) : model.data[index][0];
    }
    IExpr[] coordinates = model.coordinates[axis];
    IASTAppendable result = F.ListAlloc(coordinates.length);
    for (int i = 0; i < coordinates.length; i++) {
      tuple[axis] = coordinates[i];
      result.append(gridArray(model, axis + 1, index * coordinates.length + i, tuple, grid));
    }
    return result;
  }

  /** <code>acc + w * value</code>, componentwise for tensors. */
  private static IExpr addScaled(IExpr acc, IExpr w, IExpr value) {
    if (value.isList()) {
      IAST list = (IAST) value;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (int i = 1; i < list.size(); i++) {
        result.append(addScaled(((IAST) acc).get(i), w, list.get(i)));
      }
      return result;
    }
    return acc.plus(w.times(value));
  }

  /** <code>w * value</code>, componentwise for tensors. */
  private static IExpr scale(IExpr w, IExpr value) {
    if (value.isList()) {
      return ((IAST) value).map(v -> scale(w, v));
    }
    return w.times(value);
  }

  /** The zero of the shape of <code>value</code>. */
  private static IExpr zero(IExpr value) {
    if (value.isList()) {
      return ((IAST) value).map(v -> zero(v));
    }
    return F.C0;
  }

  private static IAST toList(int[] values) {
    IASTAppendable list = F.ListAlloc(values.length);
    for (int v : values) {
      list.append(F.ZZ(v));
    }
    return list;
  }

  /** The data is shown as the domain it is defined on, followed by a placeholder. */
  @Override
  public String toString() {
    return "InterpolatingFunction(" + property("Domain").toString() + ",<>)";
  }

  @Override
  public String fullFormString() {
    return toString();
  }

  @Override
  public String toHTML() {
    return toString();
  }
}
