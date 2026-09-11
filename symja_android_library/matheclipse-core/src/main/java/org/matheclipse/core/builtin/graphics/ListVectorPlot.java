package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
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
 * <code>array[[i, j, k]]</code> at <code>{k, j, i}</code>, as the Wolfram Language does, or spreads
 * each axis over <code>DataRange -> {{xmin, xmax}, {ymin, ymax}[, {zmin, zmax}]}</code>. A dense
 * array is thinned to at most 15 (in 3D 7) evenly spaced entries along each axis, so the arrows stay
 * readable, and its plot range is the extent of the data. A list of <code>{point, vector}</code>
 * pairs places each vector at its point.
 *
 * <p>
 * Each arrow is centred on its point and the longest spans <code>VectorScale</code> of the spacing
 * between points. They are coloured by their length unless <code>VectorColorFunction -> None</code>.
 * Entries that are not numeric vectors, and zero vectors, get no arrow. Any other option is handed on
 * to the <code>Graphics</code>/<code>Graphics3D</code>.
 */
public class ListVectorPlot extends AbstractFunctionEvaluator {

  /** How much of the spacing between points the longest arrow spans. */
  private static final double DEFAULT_SCALE = 0.9;

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

    double scale = DEFAULT_SCALE;
    boolean colored = true;
    double[][] dataRange = null;
    IASTAppendable graphicsOptions = F.ListAlloc();
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
        IExpr value = engine.evaluate(option.second());
        IExpr first = value.isList() && value.argSize() > 0 ? value.first() : value;
        if (first.isReal()) {
          scale = first.evalf();
        }
        continue;
      }
      if (key == S.VectorColorFunction) {
        colored = !engine.evaluate(option.second()).isNone();
        continue;
      }
      if (key == S.DataRange) {
        dataRange = dataRange(engine.evalN(option.second()));
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
          spacing = Math.min(spacing,
              Math.abs(step[d]) * (counts[level] - 1) / (kept[level] - 1));
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
      primitives.append(VectorPlot.color(0.0));
    }
    double[] min = new double[dimension];
    double[] max = new double[dimension];
    java.util.Arrays.fill(min, Double.MAX_VALUE);
    java.util.Arrays.fill(max, -Double.MAX_VALUE);
    for (int k = 0; k < vectors.size(); k++) {
      double[] p = tails.get(k);
      double[] v = vectors.get(k);
      IASTAppendable from = F.ListAlloc(dimension);
      IASTAppendable to = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        from.append(F.num(p[d] - v[d] * factor / 2));
        to.append(F.num(p[d] + v[d] * factor / 2));
        min[d] = Math.min(min[d], p[d]);
        max[d] = Math.max(max[d], p[d]);
      }
      IAST arrow = VectorPlot.arrow(from, to, dimension);
      primitives.append(
          colored ? F.list(VectorPlot.color(VectorPlot.norm(v) / longest), arrow) : arrow);
    }

    IASTAppendable result = dimension == 3 ? F.Graphics3D(primitives) : F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!vectors.isEmpty() && !hasOption(graphicsOptions, S.PlotRange)) {
      double pad = spacing / 2;
      IASTAppendable range = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        if (extent != null && extent[d][1] > extent[d][0]) {
          // an array's plot range is the extent of its data, as the Wolfram Language draws it
          range.append(F.list(F.num(extent[d][0]), F.num(extent[d][1])));
        } else {
          range.append(F.list(F.num(min[d] - pad), F.num(max[d] + pad)));
        }
      }
      result.append(F.Rule(S.PlotRange, range));
    }
    if (dimension == 2) {
      if (!hasOption(graphicsOptions, S.Frame)) {
        result.append(F.Rule(S.Frame, S.True));
      }
    } else if (!hasOption(graphicsOptions, S.Axes)) {
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

  private static boolean hasOption(IAST options, IExpr key) {
    for (IExpr option : options) {
      if (option.isRuleAST() && option.first() == key) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
