package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RandomPoint(region)</code> - a pseudo random point which is uniformly distributed in the
 * region.
 *
 * <p>
 * <code>RandomPoint(region, n)</code> gives a list of <code>n</code> points and
 * <code>RandomPoint(region, {n1, n2, ...})</code> an <code>n1 x n2 x ...</code> array of points.
 *
 * <p>
 * The points are machine numbers drawn from the engine's random generator, so
 * <code>SeedRandom</code> makes them reproducible. They are uniform with respect to the measure of
 * the region: a curve by its length, a surface by its area, a solid by its volume. Primitives are
 * sampled directly; Boolean combinations and other full dimensional regions are sampled by
 * rejection in their bounding box, and a <code>ParametricRegion</code> by rejection in its
 * parameter box, weighted by the area element of the parametrization.
 */
public class RandomPoint extends AbstractFunctionEvaluator {

  /** The number of candidates which are tried for one point of a rejection sampler. */
  private static final int MAX_ATTEMPTS = 10000;

  /** The number of parameter values which estimate the largest area element. */
  private static final int WEIGHT_PRESAMPLES = 256;

  /** Draws one point of a region. */
  @FunctionalInterface
  private interface Sampler {
    /**
     * @return the coordinates of a random point or <code>null</code> if no point could be found
     */
    double[] sample(Random random);
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int[] dimensions = new int[0];
    if (ast.argSize() == 2) {
      dimensions = countSpecification(ast.arg2());
      if (dimensions == null) {
        return F.NIL;
      }
    }
    IExpr region = ast.arg1();
    if (region.isAST(S.Region, 2)) {
      region = region.first();
    }
    region = MeshFunctions.normalizeRegion(region);
    if (!region.isAST()) {
      return F.NIL;
    }
    Sampler sampler = sampler((IAST) region, engine);
    if (sampler == null) {
      return F.NIL;
    }
    Random random = engine.getRandom();
    int total = 1;
    for (int d : dimensions) {
      total *= d;
    }
    List<double[]> points = new ArrayList<double[]>(total);
    for (int i = 0; i < total; i++) {
      double[] point = sampler.sample(random);
      if (point == null) {
        return F.NIL;
      }
      points.add(point);
    }
    if (dimensions.length == 0) {
      return vector(points.get(0));
    }
    return array(points, dimensions, 0, new int[] {0});
  }

  /**
   * The dimensions of the requested array of points.
   *
   * @return <code>null</code> if <code>spec</code> is neither a non-negative integer nor a list of
   *         them
   */
  private static int[] countSpecification(IExpr spec) {
    if (spec.isInteger() || spec.isReal()) {
      // a whole-valued real like 3. counts as well
      int n = spec.toIntDefault();
      return n >= 0 ? new int[] {n} : null;
    }
    if (spec.isList() && spec.argSize() > 0) {
      int[] dimensions = new int[spec.argSize()];
      for (int i = 1; i <= spec.argSize(); i++) {
        IExpr n = ((IAST) spec).get(i);
        dimensions[i - 1] = n.isInteger() || n.isReal() ? n.toIntDefault() : -1;
        if (dimensions[i - 1] < 0) {
          return null;
        }
      }
      long total = 1;
      for (int d : dimensions) {
        total *= d;
        if (total > Integer.MAX_VALUE) {
          return null;
        }
      }
      return dimensions;
    }
    return null;
  }

  private static IAST array(List<double[]> points, int[] dimensions, int level, int[] next) {
    IASTAppendable list = F.ListAlloc(dimensions[level]);
    for (int i = 0; i < dimensions[level]; i++) {
      if (level == dimensions.length - 1) {
        list.append(vector(points.get(next[0]++)));
      } else {
        list.append(array(points, dimensions, level + 1, next));
      }
    }
    return list;
  }

  private static IAST vector(double[] point) {
    IASTAppendable list = F.ListAlloc(point.length);
    for (double x : point) {
      list.append(F.num(x));
    }
    return list;
  }

  /**
   * The sampler of a region.
   *
   * @return <code>null</code> if points of the region cannot be drawn
   */
  private static Sampler sampler(IAST region, EvalEngine engine) {
    switch (region.headID()) {
      case ID.Point:
        return pointSampler(region, engine);
      case ID.Line:
        return lineSampler(region, engine);
      case ID.Triangle:
      case ID.Simplex:
        return simplexSampler(region, engine);
      case ID.Polygon:
        return polygonSampler(region, engine);
      case ID.Rectangle:
      case ID.Cuboid:
        return boxSampler(region, engine);
      case ID.Disk:
      case ID.Ball:
        return ballSampler(region, engine, false);
      case ID.Ellipsoid:
        // an axis aligned ellipsoid is a ball with one radius per axis
        return region.argSize() == 2 && region.arg2().isList()
            ? ballSampler(region, engine, false)
            : null;
      case ID.Circle:
      case ID.Sphere:
        return ballSampler(region, engine, true);
      case ID.Interval:
        return intervalSampler(region, engine);
      case ID.ParametricRegion:
        return parametricSampler(region, engine);
      case ID.EmptyRegion:
        return null;
      default:
        break;
    }
    return rejectionSampler(region, engine);
  }

  // ---------------------------------------------------------------- numeric conversions

  /** A finite real number or <code>NaN</code>. */
  private static double real(IExpr expr, EvalEngine engine) {
    double value = engine.evalDoubleNaN(expr);
    return Double.isInfinite(value) ? Double.NaN : value;
  }

  /** A vector of finite real numbers or <code>null</code>. */
  private static double[] realVector(IExpr expr, EvalEngine engine) {
    if (!expr.isList() || expr.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) expr;
    double[] result = new double[list.argSize()];
    for (int i = 1; i <= list.argSize(); i++) {
      if (list.get(i).isList()) {
        return null;
      }
      result[i - 1] = real(list.get(i), engine);
      if (Double.isNaN(result[i - 1])) {
        return null;
      }
    }
    return result;
  }

  /** A list of real vectors of the same length or <code>null</code>. */
  private static double[][] realMatrix(IExpr expr, EvalEngine engine) {
    if (!expr.isList() || expr.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) expr;
    double[][] result = new double[list.argSize()][];
    for (int i = 1; i <= list.argSize(); i++) {
      result[i - 1] = realVector(list.get(i), engine);
      if (result[i - 1] == null || result[i - 1].length != result[0].length) {
        return null;
      }
    }
    return result;
  }

  /**
   * The point lists of a primitive which takes either one list of points or a list of such lists.
   */
  private static List<double[][]> realMatrices(IExpr expr, EvalEngine engine) {
    List<double[][]> result = new ArrayList<double[][]>();
    if (expr.isList() && expr.argSize() > 0 && expr.first().isList()
        && expr.first().argSize() > 0 && expr.first().first().isList()) {
      IAST list = (IAST) expr;
      for (int i = 1; i <= list.argSize(); i++) {
        double[][] matrix = realMatrix(list.get(i), engine);
        if (matrix == null || matrix[0].length != firstLength(result, matrix)) {
          return null;
        }
        result.add(matrix);
      }
      return result;
    }
    double[][] matrix = realMatrix(expr, engine);
    if (matrix == null) {
      return null;
    }
    result.add(matrix);
    return result;
  }

  /** The vector length of the first matrix, or of <code>matrix</code> if there is none yet. */
  private static int firstLength(List<double[][]> matrices, double[][] matrix) {
    return matrices.isEmpty() ? matrix[0].length : matrices.get(0)[0].length;
  }

  // ---------------------------------------------------------------- primitives

  /** One of the points, each with the same probability. */
  private static Sampler pointSampler(IAST region, EvalEngine engine) {
    if (region.argSize() != 1) {
      return null;
    }
    double[] single = realVector(region.arg1(), engine);
    if (single != null) {
      return random -> single.clone();
    }
    double[][] points = realMatrix(region.arg1(), engine);
    if (points == null) {
      return null;
    }
    return random -> points[random.nextInt(points.length)].clone();
  }

  /** A point of a polyline - or of several - chosen by arc length. */
  private static Sampler lineSampler(IAST region, EvalEngine engine) {
    if (region.argSize() != 1) {
      return null;
    }
    List<double[][]> lines = realMatrices(region.arg1(), engine);
    if (lines == null) {
      return null;
    }
    List<double[]> starts = new ArrayList<double[]>();
    List<double[]> ends = new ArrayList<double[]>();
    for (double[][] line : lines) {
      if (line.length == 1) {
        // a line of a single point is that point
        starts.add(line[0]);
        ends.add(line[0]);
      }
      for (int i = 0; i + 1 < line.length; i++) {
        starts.add(line[i]);
        ends.add(line[i + 1]);
      }
    }
    double[] weights = new double[starts.size()];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = distance(starts.get(i), ends.get(i));
    }
    WeightedChoice choice = new WeightedChoice(weights);
    return random -> {
      int i = choice.next(random);
      return interpolate(starts.get(i), ends.get(i), random.nextDouble());
    };
  }

  /**
   * A point of a simplex: the barycentric coordinates are a uniform point of the standard simplex,
   * which normalized exponential variates give.
   */
  private static Sampler simplexSampler(IAST region, EvalEngine engine) {
    IAST vertexList = RegionPrimitives.verticesOfSimplex(region);
    if (vertexList.isNIL()) {
      return null;
    }
    double[][] vertices = realMatrix(vertexList, engine);
    if (vertices == null) {
      return null;
    }
    return random -> {
      double[] weights = new double[vertices.length];
      double sum = 0.0;
      for (int i = 0; i < weights.length; i++) {
        weights[i] = -Math.log(1.0 - random.nextDouble());
        sum += weights[i];
      }
      double[] point = new double[vertices[0].length];
      for (int i = 0; i < weights.length; i++) {
        for (int j = 0; j < point.length; j++) {
          point[j] += weights[i] / sum * vertices[i][j];
        }
      }
      return point;
    };
  }

  /**
   * A point of a polygon - or of several, the even-odd rule decides which points are inside - by
   * rejection in the bounding box. A planar polygon in space is sampled in its projection onto
   * the coordinate plane in which it has the largest area and lifted back onto its plane.
   */
  private static Sampler polygonSampler(IAST region, EvalEngine engine) {
    if (region.argSize() != 1) {
      return null;
    }
    List<double[][]> rings = realMatrices(region.arg1(), engine);
    if (rings == null || rings.get(0).length < 3) {
      return null;
    }
    int dimension = rings.get(0)[0].length;
    if (dimension == 2) {
      return planarPolygonSampler(rings, null);
    }
    if (dimension != 3) {
      return null;
    }
    // the normal of the plane by Newell's method
    double[][] ring = rings.get(0);
    double[] normal = new double[3];
    for (int i = 0; i < ring.length; i++) {
      double[] p = ring[i];
      double[] q = ring[(i + 1) % ring.length];
      normal[0] += (p[1] - q[1]) * (p[2] + q[2]);
      normal[1] += (p[2] - q[2]) * (p[0] + q[0]);
      normal[2] += (p[0] - q[0]) * (p[1] + q[1]);
    }
    int dropped = 0;
    for (int i = 1; i < 3; i++) {
      if (Math.abs(normal[i]) > Math.abs(normal[dropped])) {
        dropped = i;
      }
    }
    if (normal[dropped] == 0.0) {
      return null;
    }
    final int axis = dropped;
    final int[] kept = axis == 0 ? new int[] {1, 2} : axis == 1 ? new int[] {0, 2} : new int[] {0, 1};
    List<double[][]> projected = new ArrayList<double[][]>();
    for (double[][] r : rings) {
      double[][] p = new double[r.length][];
      for (int i = 0; i < r.length; i++) {
        p[i] = new double[] {r[i][kept[0]], r[i][kept[1]]};
      }
      projected.add(p);
    }
    double[] origin = ring[0];
    return planarPolygonSampler(projected, xy -> {
      double[] point = new double[3];
      point[kept[0]] = xy[0];
      point[kept[1]] = xy[1];
      point[axis] = origin[axis] - (normal[kept[0]] * (xy[0] - origin[kept[0]])
          + normal[kept[1]] * (xy[1] - origin[kept[1]])) / normal[axis];
      return point;
    });
  }

  @FunctionalInterface
  private interface Lift {
    double[] apply(double[] xy);
  }

  private static Sampler planarPolygonSampler(List<double[][]> rings, Lift lift) {
    double[] lower = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
    double[] upper = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
    for (double[][] ring : rings) {
      for (double[] p : ring) {
        for (int j = 0; j < 2; j++) {
          lower[j] = Math.min(lower[j], p[j]);
          upper[j] = Math.max(upper[j], p[j]);
        }
      }
    }
    return random -> {
      for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
        double[] xy = uniformInBox(lower, upper, random);
        if (insidePolygon(rings, xy[0], xy[1])) {
          return lift == null ? xy : lift.apply(xy);
        }
      }
      return null;
    };
  }

  /** The even-odd rule: a ray from the point crosses the edges an odd number of times. */
  private static boolean insidePolygon(List<double[][]> rings, double x, double y) {
    boolean inside = false;
    for (double[][] ring : rings) {
      for (int i = 0, j = ring.length - 1; i < ring.length; j = i++) {
        double xi = ring[i][0];
        double yi = ring[i][1];
        double xj = ring[j][0];
        double yj = ring[j][1];
        if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
          inside = !inside;
        }
      }
    }
    return inside;
  }

  /** A point of an axis aligned box. */
  private static Sampler boxSampler(IAST region, EvalEngine engine) {
    IAST corners = RegionPrimitives.boxCorners(region);
    if (corners.isNIL()) {
      return null;
    }
    double[] a = realVector(corners.arg1(), engine);
    double[] b = realVector(corners.arg2(), engine);
    if (a == null || b == null || a.length != b.length) {
      return null;
    }
    double[] lower = new double[a.length];
    double[] upper = new double[a.length];
    for (int i = 0; i < a.length; i++) {
      lower[i] = Math.min(a[i], b[i]);
      upper[i] = Math.max(a[i], b[i]);
    }
    return random -> uniformInBox(lower, upper, random);
  }

  /**
   * A point of a ball (<code>Disk</code>, <code>Ball</code>) or of its boundary
   * (<code>Circle</code>, <code>Sphere</code>) in any dimension. One radius per axis stretches the
   * ball into an ellipsoid; a plane sector <code>{t1, t2}</code> restricts the polar angle.
   */
  private static Sampler ballSampler(IAST region, EvalEngine engine, boolean boundary) {
    int dimension = region.isAST(S.Disk) || region.isAST(S.Circle) ? 2 : 3;
    double[] center = new double[dimension];
    double[] radii = null;
    if (region.argSize() >= 1) {
      center = realVector(region.arg1(), engine);
      if (center == null) {
        return null;
      }
      dimension = center.length;
    }
    radii = new double[dimension];
    Arrays.fill(radii, 1.0);
    if (region.argSize() >= 2) {
      IExpr radius = region.arg2();
      if (radius.isList()) {
        radii = realVector(radius, engine);
        if (radii == null || radii.length != dimension) {
          return null;
        }
      } else {
        double r = real(radius, engine);
        if (Double.isNaN(r)) {
          return null;
        }
        Arrays.fill(radii, r);
      }
    }
    final int n = dimension;
    final double[] c = center;
    final double[] r = radii;
    if (region.argSize() == 3) {
      if (n != 2) {
        return null;
      }
      double[] angles = realVector(region.arg3(), engine);
      if (angles == null || angles.length != 2 || angles[1] < angles[0]) {
        return null;
      }
      return planeSampler(c, r, angles[0], Math.min(angles[1] - angles[0], 2.0 * Math.PI),
          boundary);
    } else if (region.argSize() > 3) {
      return null;
    }
    if (n == 2) {
      return planeSampler(c, r, 0.0, 2.0 * Math.PI, boundary);
    }
    boolean round = true;
    for (int i = 1; i < n; i++) {
      round &= r[i] == r[0];
    }
    if (boundary && !round) {
      // the surface of an ellipsoid is not the stretched surface of a sphere
      return null;
    }
    return random -> {
      double[] direction = gaussianDirection(n, random);
      double scale = boundary ? 1.0 : Math.pow(random.nextDouble(), 1.0 / n);
      double[] point = new double[n];
      for (int i = 0; i < n; i++) {
        point[i] = c[i] + r[i] * scale * direction[i];
      }
      return point;
    };
  }

  /**
   * A point of the sector <code>{c + rho*{rx*Cos(t), ry*Sin(t)}}</code> with <code>t</code> in
   * <code>[start, start+extent]</code> and <code>rho</code> in <code>[0, 1]</code>, or of its arc
   * with <code>rho == 1</code>. On the arc of an ellipse the angle is accepted in proportion to the
   * speed of the parametrization, which makes the points uniform by arc length.
   */
  private static Sampler planeSampler(double[] c, double[] r, double start, double extent,
      boolean boundary) {
    double maxSpeed = Math.max(Math.abs(r[0]), Math.abs(r[1]));
    return random -> {
      for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
        double t = start + extent * random.nextDouble();
        double cos = Math.cos(t);
        double sin = Math.sin(t);
        if (boundary) {
          if (r[0] != r[1]) {
            double speed = Math.hypot(r[0] * sin, r[1] * cos);
            if (random.nextDouble() * maxSpeed > speed) {
              continue;
            }
          }
          return new double[] {c[0] + r[0] * cos, c[1] + r[1] * sin};
        }
        // the area of the disk of radius rho grows with rho^2
        double rho = Math.sqrt(random.nextDouble());
        return new double[] {c[0] + r[0] * rho * cos, c[1] + r[1] * rho * sin};
      }
      return null;
    };
  }

  /** A point of the union of the intervals, chosen by length, as a one coordinate vector. */
  private static Sampler intervalSampler(IAST region, EvalEngine engine) {
    if (region.argSize() == 0) {
      return null;
    }
    double[][] intervals = new double[region.argSize()][];
    for (int i = 0; i < intervals.length; i++) {
      intervals[i] = realVector(region.get(i + 1), engine);
      if (intervals[i] == null || intervals[i].length != 2) {
        return null;
      }
    }
    double[] weights = new double[intervals.length];
    for (int i = 0; i < intervals.length; i++) {
      weights[i] = Math.abs(intervals[i][1] - intervals[i][0]);
    }
    WeightedChoice choice = new WeightedChoice(weights);
    return random -> {
      double[] interval = intervals[choice.next(random)];
      return new double[] {interval[0] + (interval[1] - interval[0]) * random.nextDouble()};
    };
  }

  // ---------------------------------------------------------------- ParametricRegion

  /**
   * A point of a <code>ParametricRegion({{f1, ..., fn}, cond}, {u1, ..., uk})</code>. The
   * parameters are drawn uniformly from the box which <code>cond</code> bounds them by; a value is
   * kept if it satisfies <code>cond</code> and with a probability proportional to the
   * <code>k</code>-dimensional area element <code>Sqrt(Det(Transpose(J).J))</code> of the
   * parametrization, so that the image points are uniform in the region.
   */
  private static Sampler parametricSampler(IAST region, EvalEngine engine) {
    IAST functions = ParametricRegion.coordinateFunctions(region);
    if (functions.isNIL() || region.argSize() != 2 || !region.arg2().isList()) {
      return null;
    }
    IAST variables = (IAST) region.arg2();
    int k = variables.argSize();
    int n = functions.argSize();
    if (k == 0 || k > n || !variables.forAll(x -> x.isVariable())) {
      return null;
    }
    IExpr condition = ParametricRegion.parameterCondition(region);
    double[] lower = new double[k];
    double[] upper = new double[k];
    if (!parameterBox(condition, variables, lower, upper, engine)) {
      return null;
    }
    IExpr[] jacobian = new IExpr[k];
    for (int j = 0; j < k; j++) {
      jacobian[j] = engine.evaluate(F.D(functions, variables.get(j + 1)));
      if (!jacobian[j].isList() || jacobian[j].argSize() != n) {
        return null;
      }
    }
    ParametricPoint parametric =
        new ParametricPoint(functions, condition, jacobian, variables, lower, upper, engine);
    return random -> parametric.sample(random);
  }

  /** The state of the rejection sampler of a parametrization. */
  private static final class ParametricPoint {
    final IAST functions;
    final IExpr condition;
    final IExpr[] jacobian;
    final IAST variables;
    final double[] lower;
    final double[] upper;
    final EvalEngine engine;
    /** The estimate of the largest area element; <code>NaN</code> before the first point. */
    double maxWeight = Double.NaN;

    ParametricPoint(IAST functions, IExpr condition, IExpr[] jacobian, IAST variables,
        double[] lower, double[] upper, EvalEngine engine) {
      this.functions = functions;
      this.condition = condition;
      this.jacobian = jacobian;
      this.variables = variables;
      this.lower = lower;
      this.upper = upper;
      this.engine = engine;
    }

    double[] sample(Random random) {
      if (Double.isNaN(maxWeight)) {
        double max = 0.0;
        for (int i = 0; i < WEIGHT_PRESAMPLES; i++) {
          double[] u = uniformInBox(lower, upper, random);
          if (admissible(u)) {
            max = Math.max(max, weight(u));
          }
        }
        // some head room above the largest sampled value
        maxWeight = 1.25 * max;
      }
      for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
        double[] u = uniformInBox(lower, upper, random);
        if (!admissible(u)) {
          continue;
        }
        double w = weight(u);
        if (Double.isNaN(w)) {
          continue;
        }
        if (w > maxWeight) {
          maxWeight = w;
        }
        if (maxWeight > 0.0 && random.nextDouble() * maxWeight <= w) {
          double[] point = new double[functions.argSize()];
          for (int i = 0; i < point.length; i++) {
            point[i] = real(substitute(functions.get(i + 1), u), engine);
            if (Double.isNaN(point[i])) {
              return null;
            }
          }
          return point;
        }
      }
      return null;
    }

    private IExpr substitute(IExpr expr, double[] u) {
      return F.subst(expr, x -> {
        int index = variables.indexOf(x);
        return index > 0 ? F.num(u[index - 1]) : F.NIL;
      });
    }

    private boolean admissible(double[] u) {
      return condition.isTrue() || engine.evaluate(substitute(condition, u)).isTrue();
    }

    /** <code>Sqrt(Det(Transpose(J).J))</code> at the parameter values <code>u</code>. */
    private double weight(double[] u) {
      int k = jacobian.length;
      int n = functions.argSize();
      double[][] columns = new double[k][n];
      for (int j = 0; j < k; j++) {
        IAST column = (IAST) jacobian[j];
        for (int i = 0; i < n; i++) {
          columns[j][i] = real(substitute(column.get(i + 1), u), engine);
          if (Double.isNaN(columns[j][i])) {
            return Double.NaN;
          }
        }
      }
      double[][] gram = new double[k][k];
      for (int a = 0; a < k; a++) {
        for (int b = 0; b < k; b++) {
          double sum = 0.0;
          for (int i = 0; i < n; i++) {
            sum += columns[a][i] * columns[b][i];
          }
          gram[a][b] = sum;
        }
      }
      return Math.sqrt(Math.max(0.0, determinant(gram)));
    }
  }

  /** The determinant by Gaussian elimination with partial pivoting. */
  private static double determinant(double[][] matrix) {
    int k = matrix.length;
    double[][] m = new double[k][];
    for (int i = 0; i < k; i++) {
      m[i] = matrix[i].clone();
    }
    double det = 1.0;
    for (int col = 0; col < k; col++) {
      int pivot = col;
      for (int row = col + 1; row < k; row++) {
        if (Math.abs(m[row][col]) > Math.abs(m[pivot][col])) {
          pivot = row;
        }
      }
      if (m[pivot][col] == 0.0) {
        return 0.0;
      }
      if (pivot != col) {
        double[] swap = m[pivot];
        m[pivot] = m[col];
        m[col] = swap;
        det = -det;
      }
      det *= m[col][col];
      for (int row = col + 1; row < k; row++) {
        double factor = m[row][col] / m[col][col];
        for (int j = col; j < k; j++) {
          m[row][j] -= factor * m[col][j];
        }
      }
    }
    return det;
  }

  /**
   * Bound every parameter by the numeric bounds which the comparisons of the condition give. A
   * chain <code>a &lt;= s &lt;= t &lt;= b</code> bounds <code>s</code> by <code>a</code> and
   * <code>b</code> directly; a bound which only holds through another parameter is passed on.
   *
   * @return <code>false</code> if a parameter is not bounded on both sides
   */
  private static boolean parameterBox(IExpr condition, IAST variables, double[] lower,
      double[] upper, EvalEngine engine) {
    Arrays.fill(lower, Double.NEGATIVE_INFINITY);
    Arrays.fill(upper, Double.POSITIVE_INFINITY);
    // pairs {i, j}: parameter i is not greater than parameter j
    List<int[]> order = new ArrayList<int[]>();
    collectBounds(condition, variables, lower, upper, order, engine);
    for (int pass = 0; pass < variables.argSize(); pass++) {
      for (int[] pair : order) {
        lower[pair[1]] = Math.max(lower[pair[1]], lower[pair[0]]);
        upper[pair[0]] = Math.min(upper[pair[0]], upper[pair[1]]);
      }
    }
    for (int i = 0; i < lower.length; i++) {
      if (Double.isInfinite(lower[i]) || Double.isInfinite(upper[i]) || lower[i] > upper[i]) {
        return false;
      }
    }
    return true;
  }

  private static void collectBounds(IExpr condition, IAST variables, double[] lower,
      double[] upper, List<int[]> order, EvalEngine engine) {
    if (!condition.isAST()) {
      return;
    }
    IAST ast = (IAST) condition;
    switch (ast.headID()) {
      case ID.And:
        for (IExpr part : ast) {
          collectBounds(part, variables, lower, upper, order, engine);
        }
        return;
      case ID.Less:
      case ID.LessEqual:
      case ID.Greater:
      case ID.GreaterEqual: {
        List<IExpr> chain = new ArrayList<IExpr>(ast.argSize());
        for (IExpr operand : ast) {
          chain.add(operand);
        }
        if (ast.isAST(S.Greater) || ast.isAST(S.GreaterEqual)) {
          Collections.reverse(chain);
        }
        chainBounds(chain, variables, lower, upper, order, engine);
        return;
      }
      case ID.Inequality: {
        if (ast.argSize() < 3 || ast.argSize() % 2 == 0) {
          return;
        }
        boolean increasing = true;
        boolean decreasing = true;
        for (int i = 2; i < ast.size(); i += 2) {
          IExpr op = ast.get(i);
          increasing &= op == S.Less || op == S.LessEqual;
          decreasing &= op == S.Greater || op == S.GreaterEqual;
        }
        List<IExpr> operands = new ArrayList<IExpr>();
        for (int i = 1; i < ast.size(); i += 2) {
          operands.add(ast.get(i));
        }
        if (increasing || decreasing) {
          if (decreasing) {
            Collections.reverse(operands);
          }
          chainBounds(operands, variables, lower, upper, order, engine);
          return;
        }
        // mixed directions: every comparison on its own
        for (int i = 2; i < ast.size(); i += 2) {
          collectBounds(F.binaryAST2(ast.get(i), ast.get(i - 1), ast.get(i + 1)), variables,
              lower, upper, order, engine);
        }
        return;
      }
      default:
        return;
    }
  }

  /** The bounds of an increasing chain <code>e1 &lt;= e2 &lt;= ...</code>. */
  private static void chainBounds(List<IExpr> chain, IAST variables, double[] lower,
      double[] upper, List<int[]> order, EvalEngine engine) {
    int size = chain.size();
    int[] variableIndex = new int[size];
    double[] values = new double[size];
    for (int p = 0; p < size; p++) {
      variableIndex[p] = variables.indexOf(chain.get(p)) - 1;
      values[p] = chain.get(p).isFree(x -> variables.indexOf(x) > 0, true)
          ? engine.evalDoubleNaN(chain.get(p))
          : Double.NaN;
    }
    for (int p = 0; p < size; p++) {
      int i = variableIndex[p];
      if (i < 0) {
        continue;
      }
      for (int q = 0; q < size; q++) {
        if (q < p && !Double.isNaN(values[q])) {
          lower[i] = Math.max(lower[i], values[q]);
        } else if (q > p && !Double.isNaN(values[q])) {
          upper[i] = Math.min(upper[i], values[q]);
        } else if (q > p && variableIndex[q] >= 0) {
          order.add(new int[] {i, variableIndex[q]});
        }
      }
    }
  }

  // ---------------------------------------------------------------- rejection in a bounding box

  /**
   * A point of a full dimensional region by rejection in its bounding box, with
   * <code>RegionMember</code> deciding which candidates belong to the region.
   */
  private static Sampler rejectionSampler(IAST region, EvalEngine engine) {
    int embeddingDimension = RegionEmbeddingDimension.getEmbeddingDimension(region);
    if (embeddingDimension < 1) {
      return null;
    }
    double[][] box = boundingBox(region, engine);
    if (box == null || box[0].length != embeddingDimension) {
      return null;
    }
    double[] lower = box[0];
    double[] upper = box[1];
    return random -> {
      for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
        double[] point = uniformInBox(lower, upper, random);
        if (engine.evaluate(F.RegionMember(region, vector(point))).isTrue()) {
          return point;
        }
      }
      return null;
    };
  }

  /**
   * A box <code>{lower, upper}</code> which contains the region.
   *
   * @return <code>null</code> if no finite box is known
   */
  private static double[][] boundingBox(IExpr region, EvalEngine engine) {
    if (!region.isAST()) {
      return null;
    }
    IAST ast = (IAST) region;
    switch (ast.headID()) {
      case ID.BooleanRegion:
        return ast.argSize() == 2 && ast.arg2().isList() && ast.arg2().argSize() > 0
            ? booleanRegionBox(ast.arg1(), (IAST) ast.arg2(), engine)
            : null;
      case ID.RegionUnion:
      case ID.RegionSymmetricDifference: {
        double[][] box = null;
        for (IExpr part : ast) {
          box = union(box, boundingBox(part, engine));
          if (box == null) {
            return null;
          }
        }
        return box;
      }
      case ID.RegionIntersection: {
        double[][] box = null;
        for (IExpr part : ast) {
          double[][] partBox = boundingBox(part, engine);
          if (partBox != null) {
            box = box == null ? partBox : intersection(box, partBox);
          }
        }
        return box;
      }
      case ID.RegionDifference:
        return ast.argSize() >= 1 ? boundingBox(ast.arg1(), engine) : null;
      case ID.Interval:
        return null;
      default:
        break;
    }
    if (RegionDimension.getRegionDimension(region) != RegionEmbeddingDimension
        .getEmbeddingDimension(region)) {
      // rejection in a box does not reach a lower dimensional region
      return null;
    }
    IExpr bounds = engine.evaluate(F.unaryAST1(S.RegionBounds, region));
    double[][] ranges = realMatrix(bounds, engine);
    if (ranges == null || ranges[0].length != 2) {
      return null;
    }
    double[][] box = new double[2][ranges.length];
    for (int i = 0; i < ranges.length; i++) {
      box[0][i] = Math.min(ranges[i][0], ranges[i][1]);
      box[1][i] = Math.max(ranges[i][0], ranges[i][1]);
    }
    return box;
  }

  /**
   * The box of <code>BooleanRegion(f, {reg1, reg2, ...})</code>. A part whose absence makes
   * <code>f</code> false - as for an intersection - contains the region, so the boxes of such
   * parts are intersected. Otherwise, if <code>f</code> is false outside of all parts, the union
   * of the boxes of the parts contains the region.
   */
  private static double[][] booleanRegionBox(IExpr function, IAST parts, EvalEngine engine) {
    int size = parts.argSize();
    double[][][] boxes = new double[size][][];
    for (int i = 0; i < size; i++) {
      boxes[i] = boundingBox(parts.get(i + 1), engine);
    }
    double[][] box = null;
    for (int i = 0; i < size; i++) {
      if (boxes[i] == null) {
        continue;
      }
      IExpr[] arguments = new IExpr[size];
      for (int j = 0; j < size; j++) {
        arguments[j] = j == i ? S.False : F.Dummy("b" + j);
      }
      if (engine.evaluate(F.ast(arguments, function)).isFalse()) {
        box = box == null ? boxes[i] : intersection(box, boxes[i]);
      }
    }
    if (box != null) {
      return box;
    }
    IExpr[] allFalse = new IExpr[size];
    Arrays.fill(allFalse, S.False);
    if (!engine.evaluate(F.ast(allFalse, function)).isFalse()) {
      // the region reaches outside of all parts
      return null;
    }
    for (int i = 0; i < size; i++) {
      box = union(box, boxes[i]);
      if (box == null) {
        return null;
      }
    }
    return box;
  }

  private static double[][] union(double[][] a, double[][] b) {
    if (b == null) {
      return null;
    }
    if (a == null) {
      return b;
    }
    if (a[0].length != b[0].length) {
      return null;
    }
    double[][] result = new double[2][a[0].length];
    for (int i = 0; i < a[0].length; i++) {
      result[0][i] = Math.min(a[0][i], b[0][i]);
      result[1][i] = Math.max(a[1][i], b[1][i]);
    }
    return result;
  }

  private static double[][] intersection(double[][] a, double[][] b) {
    if (a[0].length != b[0].length) {
      return a;
    }
    double[][] result = new double[2][a[0].length];
    for (int i = 0; i < a[0].length; i++) {
      result[0][i] = Math.max(a[0][i], b[0][i]);
      result[1][i] = Math.min(a[1][i], b[1][i]);
    }
    return result;
  }

  // ---------------------------------------------------------------- helpers

  private static double[] uniformInBox(double[] lower, double[] upper, Random random) {
    double[] point = new double[lower.length];
    for (int i = 0; i < point.length; i++) {
      point[i] = lower[i] + (upper[i] - lower[i]) * random.nextDouble();
    }
    return point;
  }

  /** A uniformly distributed unit vector: the direction of a standard normal vector. */
  private static double[] gaussianDirection(int n, Random random) {
    double[] v = new double[n];
    double norm = 0.0;
    while (norm == 0.0) {
      norm = 0.0;
      for (int i = 0; i < n; i++) {
        v[i] = random.nextGaussian();
        norm += v[i] * v[i];
      }
    }
    norm = Math.sqrt(norm);
    for (int i = 0; i < n; i++) {
      v[i] /= norm;
    }
    return v;
  }

  private static double distance(double[] p, double[] q) {
    double sum = 0.0;
    for (int i = 0; i < p.length; i++) {
      sum += (p[i] - q[i]) * (p[i] - q[i]);
    }
    return Math.sqrt(sum);
  }

  private static double[] interpolate(double[] p, double[] q, double t) {
    double[] point = new double[p.length];
    for (int i = 0; i < p.length; i++) {
      point[i] = p[i] + t * (q[i] - p[i]);
    }
    return point;
  }

  /**
   * Chooses an index with a probability proportional to its weight, or with equal probability if
   * all weights vanish.
   */
  private static final class WeightedChoice {
    final double[] cumulative;

    WeightedChoice(double[] weights) {
      cumulative = new double[weights.length];
      double sum = 0.0;
      for (int i = 0; i < weights.length; i++) {
        sum += weights[i];
        cumulative[i] = sum;
      }
    }

    int next(Random random) {
      int n = cumulative.length;
      double total = cumulative[n - 1];
      if (total <= 0.0) {
        return random.nextInt(n);
      }
      double u = random.nextDouble() * total;
      for (int i = 0; i < n; i++) {
        if (u < cumulative[i]) {
          return i;
        }
      }
      return n - 1;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
