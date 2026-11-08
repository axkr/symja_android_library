package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ListVectorPlot[array]</code>, <code>ListVectorPlot[{{{x, y}, {vx, vy}}, ...}]</code> and
 * their 3D forms <code>ListVectorPlot3D</code> - a vector field given as data, drawn as arrows the
 * way {@link VectorPlot} draws a function.
 *
 * <p>
 * An array of vectors places <code>array[[i, j]]</code> at <code>{j, i}</code> and
 * <code>array[[i, j, k]]</code> at <code>{k, j, i}</code>, or spreads each axis over
 * <code>DataRange -> {{xmin, xmax}, {ymin, ymax}[, {zmin, zmax}]}</code>. A dense array is thinned
 * to at most 15 (in 3D 7) evenly spaced entries along each axis, so the arrows stay readable, and
 * its plot range is the extent of the data. A list of <code>{point, vector}</code> pairs places
 * each vector at its point in space. In the plane the field is interpolated linearly between the
 * points and drawn on <code>VectorPlot</code>'s lattice over the rectangle they span; points which
 * all lie on one line give no arrows.
 *
 * <p>
 * Each arrow is centred on its point and the longest spans <code>VectorScale</code> of the spacing
 * between points. They are coloured by their length unless
 * <code>VectorColorFunction -> None</code>. Entries that are not numeric vectors, and zero vectors,
 * get no arrow. Any other option is handed on to the <code>Graphics</code>/<code>Graphics3D</code>.
 */
public class ListVectorPlot extends AbstractFunctionEvaluator {

  /** 2 for ListVectorPlot, 3 for ListVectorPlot3D. */
  private final int dimension;

  public ListVectorPlot(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || !ast.arg1().isList()) {
      return F.NIL;
    }
    IExpr evaluated = engine.evalN(ast.arg1());
    if (!evaluated.isList() || evaluated.argSize() == 0) {
      return F.NIL;
    }
    IAST data = (IAST) evaluated;

    double scale = VectorPlot.DEFAULT_SCALE;
    boolean colored = true;
    boolean scaledLengths = false;
    double[][] dataRange = null;
    IASTAppendable graphicsOptions = F.ListAlloc();
    // CPD-OFF the vector plots handle different option keys
    for (int i = 2; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (!option.isRuleAST()) {
        continue;
      }
      IExpr key = option.first();
      if (key == S.VectorPoints) {
        continue;
      }
      if (key == S.VectorScale) {
        scale = VectorPlot.vectorScale(engine.evaluate(option.second()), scale);
        continue;
      }
      if (key == S.VectorColorFunction) {
        colored = !engine.evaluate(option.second()).isNone();
        // CPD-ON
        continue;
      }
      if (key == S.DataRange) {
        dataRange = dataRange(engine.evalN(option.second()));
        continue;
      }
      if (key == S.VectorScaling) {
        scaledLengths = engine.evaluate(option.second()) == S.Automatic;
        continue;
      }
      graphicsOptions.append(option);
    }

    List<double[]> tails = new ArrayList<>();
    List<double[]> vectors = new ArrayList<>();
    double spacing;
    // for an array: the extent of the data along each coordinate, which is the plot range
    double[][] extent = null;
    if (isPairList(data)) {
      for (IExpr pair : data) {
        tails.add(vector(pair.first()));
        vectors.add(vector(pair.second()));
      }
      spacing = pairSpacing(tails);
      if (dimension == 2) {
        // the field is interpolated between the points and drawn like VectorPlot's over the
        // rectangle they span
        IAST graphics = tails.size() >= 3 ? interpolatedPlot(tails, vectors, ast, scale,
            scaledLengths, colored, graphicsOptions, engine) : F.NIL;
        if (graphics.isPresent()) {
          return graphics;
        }
        // points on one line have no triangles to interpolate over: no arrows
        tails.clear();
        vectors.clear();
      }
    } else {
      // counts[level]: the longest list at that nesting level, level 0 the outermost
      int[] counts = new int[dimension];
      if (!measure(data, 0, counts)) {
        return F.NIL;
      }
      // a dense array is thinned to at most maxPerAxis evenly spaced entries per level
      int maxPerAxis = dimension == 3 ? 7 : 15;
      boolean[][] keep = new boolean[dimension][];
      int[] kept = new int[dimension];
      for (int level = 0; level < dimension; level++) {
        int count = counts[level];
        int m = Math.min(count, maxPerAxis);
        kept[level] = m;
        keep[level] = new boolean[count + 1];
        for (int k = 0; k < m; k++) {
          int index = m == 1 ? 1 : 1 + (int) Math.round((double) k * (count - 1) / (m - 1));
          keep[level][index] = true;
        }
      }
      // coordinate d runs along nesting level dimension-1-d: array[[i, j]] is at {j, i}
      double[] origin = new double[dimension];
      double[] step = new double[dimension];
      extent = new double[dimension][];
      for (int d = 0; d < dimension; d++) {
        int count = counts[dimension - 1 - d];
        origin[d] = 1.0;
        step[d] = 1.0;
        if (dataRange != null) {
          origin[d] = dataRange[d][0];
          step[d] = count > 1 ? (dataRange[d][1] - dataRange[d][0]) / (count - 1) : 1.0;
        }
        double end = origin[d] + (count - 1) * step[d];
        extent[d] = new double[] {Math.min(origin[d], end), Math.max(origin[d], end)};
      }
      collect(data, 0, new int[dimension], origin, step, keep, tails, vectors);
      spacing = Double.MAX_VALUE;
      for (int d = 0; d < dimension; d++) {
        int level = dimension - 1 - d;
        if (kept[level] > 1) {
          spacing = Math.min(spacing, Math.abs(step[d]) * (counts[level] - 1) / (kept[level] - 1));
        }
      }
      if (spacing == Double.MAX_VALUE || spacing == 0.0) {
        spacing = 1.0;
      }
    }

    double longest = 0.0;
    for (int k = vectors.size() - 1; k >= 0; k--) {
      double length = VectorPlot.norm(vectors.get(k));
      if (length == 0.0 || !Double.isFinite(length)) {
        tails.remove(k);
        vectors.remove(k);
      } else {
        longest = Math.max(longest, length);
      }
    }
    double factor = longest > 0.0 ? scale * spacing / longest : 0.0;

    IASTAppendable primitives = F.ListAlloc(vectors.size() + 2);
    primitives.append(VectorPlot.arrowheads(dimension == 2 ? 0.028125 : VectorPlot.ARROWHEAD_3D));
    if (!colored) {
      primitives.append(VectorPlot.plainColor());
    }
    double[] min = new double[dimension];
    double[] max = new double[dimension];
    java.util.Arrays.fill(min, Double.MAX_VALUE);
    java.util.Arrays.fill(max, -Double.MAX_VALUE);
    for (int k = 0; k < vectors.size(); k++) {
      double[] p = tails.get(k);
      double[] v = vectors.get(k);
      // CPD-OFF the vector plots handle different option keys
      IASTAppendable from = F.ListAlloc(dimension);
      IASTAppendable to = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        from.append(F.num(p[d] - v[d] * factor / 2));
        to.append(F.num(p[d] + v[d] * factor / 2));
        // CPD-ON
        min[d] = Math.min(min[d], p[d]);
        max[d] = Math.max(max[d], p[d]);
      }
      IAST arrow = VectorPlot.arrow(from, to, dimension);
      primitives
          .append(colored ? F.list(VectorPlot.color(VectorPlot.norm(v) / longest), arrow) : arrow);
    }

    IASTAppendable result = dimension == 3 ? F.Graphics3D(primitives) : F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!vectors.isEmpty() && !VectorPlot.hasOption(graphicsOptions, S.PlotRange)) {
      double pad = spacing / 2;
      IASTAppendable range = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        if (extent != null && extent[d][1] > extent[d][0]) {
          // an array's plot range is the extent of its data
          range.append(F.list(F.num(extent[d][0]), F.num(extent[d][1])));
        } else {
          range.append(F.list(F.num(min[d] - pad), F.num(max[d] + pad)));
        }
      }
      result.append(F.Rule(S.PlotRange, range));
    }
    if (dimension == 2) {
      if (!VectorPlot.hasOption(graphicsOptions, S.Frame)) {
        result.append(F.Rule(S.Frame, S.True));
      }
    } else if (!VectorPlot.hasOption(graphicsOptions, S.Axes)) {
      result.append(F.Rule(S.Axes, S.True));
    }
    return result;
  }

  /**
   * Records the longest list at each nesting level; <code>false</code> if the array is not nested
   * <code>dimension</code> levels deep.
   */
  private boolean measure(IAST list, int level, int[] counts) {
    counts[level] = Math.max(counts[level], list.argSize());
    if (level == dimension - 1) {
      return true;
    }
    for (IExpr element : list) {
      if (!element.isList() || !measure((IAST) element, level + 1, counts)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Walks the array, placing the vector at <code>index</code> (1-based, outermost first); entries
   * the thinning dropped are skipped.
   */
  private void collect(IAST list, int level, int[] index, double[] origin, double[] step,
      boolean[][] keep, List<double[]> tails, List<double[]> vectors) {
    for (int i = 1; i < list.size(); i++) {
      if (!keep[level][i]) {
        continue;
      }
      index[level] = i;
      IExpr element = list.get(i);
      if (level < dimension - 1) {
        if (element.isList()) {
          collect((IAST) element, level + 1, index, origin, step, keep, tails, vectors);
        }
        continue;
      }
      double[] v = vector(element);
      if (v == null) {
        continue;
      }
      double[] point = new double[dimension];
      for (int d = 0; d < dimension; d++) {
        point[d] = origin[d] + (index[dimension - 1 - d] - 1) * step[d];
      }
      tails.add(point);
      vectors.add(v);
    }
  }

  /** <code>{{point, vector}, ...}</code>: every entry a pair of numeric vectors. */
  private boolean isPairList(IAST data) {
    for (IExpr entry : data) {
      if (!entry.isList2() || vector(entry.first()) == null || vector(entry.second()) == null) {
        return false;
      }
    }
    return true;
  }

  /** A numeric vector with <code>dimension</code> components, or <code>null</code>. */
  private double[] vector(IExpr expr) {
    if (!expr.isList() || expr.argSize() != dimension) {
      return null;
    }
    double[] result = new double[dimension];
    for (int d = 0; d < dimension; d++) {
      IExpr component = expr.getAt(d + 1);
      if (!component.isReal()) {
        return null;
      }
      result[d] = component.evalf();
    }
    return result;
  }

  /** <code>{{xmin, xmax}, {ymin, ymax}[, {zmin, zmax}]}</code>, or <code>null</code>. */
  private double[][] dataRange(IExpr value) {
    if (!value.isList() || value.argSize() != dimension) {
      return null;
    }
    double[][] range = new double[dimension][];
    for (int d = 0; d < dimension; d++) {
      IExpr axis = value.getAt(d + 1);
      if (!axis.isList2() || !axis.first().isReal() || !axis.second().isReal()) {
        return null;
      }
      range[d] = new double[] {axis.first().evalf(), axis.second().evalf()};
    }
    return range;
  }

  /**
   * The plane field given by <code>{point, vector}</code> pairs, linear on the triangles of the
   * points' Delaunay triangulation and, outside of them, continued linearly from the nearest
   * triangle, as <code>VectorPlot</code> draws it over the bounding box of the points.
   *
   * @return {@link F#NIL} if the points have no triangulation
   */
  private static IAST interpolatedPlot(List<double[]> points, List<double[]> vectors, IAST ast,
      double scale, boolean scaledLengths, boolean colored, IAST graphicsOptions,
      EvalEngine engine) {
    int n = points.size();
    double[] x = new double[n];
    double[] y = new double[n];
    double[] min = {Double.MAX_VALUE, Double.MAX_VALUE};
    double[] max = {-Double.MAX_VALUE, -Double.MAX_VALUE};
    for (int i = 0; i < n; i++) {
      x[i] = points.get(i)[0];
      y[i] = points.get(i)[1];
      min[0] = Math.min(min[0], x[i]);
      min[1] = Math.min(min[1], y[i]);
      max[0] = Math.max(max[0], x[i]);
      max[1] = Math.max(max[1], y[i]);
    }
    List<int[]> triangles = MeshFunctions.delaunayTriangles(x, y);
    if (triangles == null || triangles.isEmpty() || !(max[0] > min[0]) || !(max[1] > min[1])) {
      return F.NIL;
    }
    TriangleBuckets buckets = new TriangleBuckets(triangles, x, y, min, max);
    java.util.function.Function<double[], double[]> field = p -> {
      // the triangle containing the point, found among those whose bounding boxes share its
      // bucket; else the one with the largest smallest barycentric coordinate, the nearest one
      for (int[] t : buckets.candidates(p)) {
        double[] w = barycentric(p, x, y, t);
        if (w != null && Math.min(w[0], Math.min(w[1], w[2])) >= -1e-12) {
          return interpolated(w, t, vectors);
        }
      }
      double best = -Double.MAX_VALUE;
      double[] weights = null;
      int[] corners = null;
      for (int[] t : triangles) {
        double[] w = barycentric(p, x, y, t);
        if (w != null) {
          double smallest = Math.min(w[0], Math.min(w[1], w[2]));
          if (smallest > best) {
            best = smallest;
            weights = w;
            corners = t;
          }
        }
      }
      return corners == null ? null : interpolated(weights, corners, vectors);
    };
    return VectorPlot.planeGraphics(field, min, max, VectorPlot.planeVectorPoints(ast, 2, engine),
        scale, scaledLengths, true, F.NIL, colored, null, graphicsOptions, engine);
  }

  /** The vector with the barycentric <code>weights</code> of the corners of a triangle. */
  private static double[] interpolated(double[] weights, int[] corners, List<double[]> vectors) {
    double[] result = new double[2];
    for (int k = 0; k < 3; k++) {
      double[] v = vectors.get(corners[k]);
      result[0] += weights[k] * v[0];
      result[1] += weights[k] * v[1];
    }
    return result;
  }

  /**
   * The triangles sorted into the cells of a grid over the bounding box of the points, each into
   * the cells its own bounding box meets, so that the triangle containing a point is among the
   * few of its cell.
   */
  private static final class TriangleBuckets {
    private final List<List<int[]>> cells;
    private final int side;
    private final double[] min;
    private final double cellWidth;
    private final double cellHeight;

    TriangleBuckets(List<int[]> triangles, double[] x, double[] y, double[] min, double[] max) {
      this.side = Math.max(1, Math.min(256, (int) Math.ceil(Math.sqrt(triangles.size()))));
      this.min = min;
      this.cellWidth = (max[0] - min[0]) / side;
      this.cellHeight = (max[1] - min[1]) / side;
      this.cells = new ArrayList<>(side * side);
      for (int i = 0; i < side * side; i++) {
        cells.add(new ArrayList<>(4));
      }
      for (int[] t : triangles) {
        double left = Math.min(x[t[0]], Math.min(x[t[1]], x[t[2]]));
        double right = Math.max(x[t[0]], Math.max(x[t[1]], x[t[2]]));
        double bottom = Math.min(y[t[0]], Math.min(y[t[1]], y[t[2]]));
        double top = Math.max(y[t[0]], Math.max(y[t[1]], y[t[2]]));
        for (int r = row(bottom); r <= row(top); r++) {
          for (int c = column(left); c <= column(right); c++) {
            cells.get(r * side + c).add(t);
          }
        }
      }
    }

    private int column(double value) {
      return Math.max(0, Math.min(side - 1, (int) Math.floor((value - min[0]) / cellWidth)));
    }

    private int row(double value) {
      return Math.max(0, Math.min(side - 1, (int) Math.floor((value - min[1]) / cellHeight)));
    }

    /** The triangles which may contain the point. */
    List<int[]> candidates(double[] p) {
      return cells.get(row(p[1]) * side + column(p[0]));
    }
  }

  /** The barycentric coordinates of a point in a triangle, <code>null</code> for a flat one. */
  private static double[] barycentric(double[] p, double[] x, double[] y, int[] t) {
    double x1 = x[t[0]];
    double y1 = y[t[0]];
    double x2 = x[t[1]];
    double y2 = y[t[1]];
    double x3 = x[t[2]];
    double y3 = y[t[2]];
    double det = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);
    if (Math.abs(det) < 1e-300) {
      return null;
    }
    double w1 = ((y2 - y3) * (p[0] - x3) + (x3 - x2) * (p[1] - y3)) / det;
    double w2 = ((y3 - y1) * (p[0] - x3) + (x1 - x3) * (p[1] - y3)) / det;
    return new double[] {w1, w2, 1.0 - w1 - w2};
  }

  /** The typical distance between scattered points: the side of the cell each one has. */
  private double pairSpacing(List<double[]> points) {
    if (points.size() < 2) {
      return 1.0;
    }
    double volume = 1.0;
    int spread = 0;
    double widest = 0.0;
    for (int d = 0; d < dimension; d++) {
      double lo = Double.MAX_VALUE;
      double hi = -Double.MAX_VALUE;
      for (double[] p : points) {
        lo = Math.min(lo, p[d]);
        hi = Math.max(hi, p[d]);
      }
      double extent = hi - lo;
      widest = Math.max(widest, extent);
      if (extent > 0) {
        volume *= extent;
        spread++;
      }
    }
    double spacing;
    if (spread == dimension) {
      spacing = Math.pow(volume / points.size(), 1.0 / dimension);
    } else if (spread > 0) {
      spacing = widest / (points.size() - 1);
    } else {
      spacing = 1.0;
    }
    return spacing > 0 ? spacing : 1.0;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
