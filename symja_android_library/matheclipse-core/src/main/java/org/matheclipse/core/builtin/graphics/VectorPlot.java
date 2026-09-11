package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>VectorPlot[{vx, vy}, {x, xmin, xmax}, {y, ymin, ymax}]</code> and
 * <code>VectorPlot3D[{vx, vy, vz}, {x, ...}, {y, ...}, {z, ...}]</code> - the vector field drawn as
 * arrows on a regular grid.
 *
 * <p>
 * Each arrow is centred on its grid point and the longest one spans <code>VectorScale</code> of the
 * grid spacing, so the arrows never overlap. They are coloured by their length unless
 * <code>VectorColorFunction -> None</code>. Points where the field has no numeric value, and
 * points where it is zero, get no arrow.
 *
 * <p>
 * Options: <code>VectorPoints -> n | {nx, ny[, nz]}</code>, <code>VectorScale -> s</code>,
 * <code>VectorColorFunction -> None</code>; any other option is handed on to the
 * <code>Graphics</code>/<code>Graphics3D</code>.
 */
public class VectorPlot extends AbstractFunctionEvaluator {

  /** How much of the grid spacing the longest arrow spans. */
  private static final double DEFAULT_SCALE = 0.9;

  /** 2 for VectorPlot, 3 for VectorPlot3D. */
  private final int dimension;

  public VectorPlot(int dimension) {
    this.dimension = dimension;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < dimension + 1) {
      return F.NIL;
    }
    IExpr field = ast.arg1();
    ISymbol[] variables = new ISymbol[dimension];
    double[] min = new double[dimension];
    double[] max = new double[dimension];
    for (int d = 0; d < dimension; d++) {
      IExpr iterator = ast.get(d + 2);
      if (!iterator.isList3() || !iterator.first().isSymbol()) {
        return F.NIL;
      }
      variables[d] = (ISymbol) iterator.first();
      try {
        min[d] = engine.evalDouble(((IAST) iterator).arg2());
        max[d] = engine.evalDouble(((IAST) iterator).arg3());
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      if (!(max[d] > min[d])) {
        return F.NIL;
      }
    }

    int[] points = vectorPoints(ast, engine);
    double scale = DEFAULT_SCALE;
    boolean colored = true;
    IASTAppendable graphicsOptions = F.ListAlloc();
    for (int i = dimension + 2; i < ast.size(); i++) {
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
      graphicsOptions.append(option);
    }

    // sample the field
    int total = 1;
    for (int n : points) {
      total *= n;
    }
    double[][] tails = new double[total][];
    double[][] vectors = new double[total][];
    double longest = 0.0;
    int[] index = new int[dimension];
    for (int k = 0; k < total; k++) {
      int rest = k;
      double[] point = new double[dimension];
      for (int d = dimension - 1; d >= 0; d--) {
        index[d] = rest % points[d];
        rest /= points[d];
        point[d] = points[d] == 1 ? (min[d] + max[d]) / 2
            : min[d] + (max[d] - min[d]) * index[d] / (points[d] - 1);
      }
      double[] vector = fieldAt(field, variables, point, engine);
      if (vector == null) {
        continue;
      }
      double length = norm(vector);
      if (length == 0.0 || Double.isNaN(length) || Double.isInfinite(length)) {
        continue;
      }
      tails[k] = point;
      vectors[k] = vector;
      longest = Math.max(longest, length);
    }

    // the longest arrow spans `scale` of the smallest grid spacing
    double spacing = Double.MAX_VALUE;
    for (int d = 0; d < dimension; d++) {
      if (points[d] > 1) {
        spacing = Math.min(spacing, (max[d] - min[d]) / (points[d] - 1));
      }
    }
    if (spacing == Double.MAX_VALUE) {
      spacing = max[0] - min[0];
    }
    double factor = longest > 0.0 ? scale * spacing / longest : 0.0;

    IASTAppendable primitives = F.ListAlloc();
    if (!colored) {
      primitives.append(color(0.0));
    }
    for (int k = 0; k < total; k++) {
      if (vectors[k] == null) {
        continue;
      }
      double[] p = tails[k];
      double[] v = vectors[k];
      IASTAppendable from = F.ListAlloc(dimension);
      IASTAppendable to = F.ListAlloc(dimension);
      for (int d = 0; d < dimension; d++) {
        from.append(F.num(p[d] - v[d] * factor / 2));
        to.append(F.num(p[d] + v[d] * factor / 2));
      }
      IAST arrow = F.Arrow(F.list(from, to));
      primitives.append(colored ? F.list(color(norm(v) / longest), arrow) : arrow);
    }

    IASTAppendable range = F.ListAlloc(dimension);
    for (int d = 0; d < dimension; d++) {
      range.append(F.list(F.num(min[d]), F.num(max[d])));
    }
    IASTAppendable result = dimension == 3 ? F.Graphics3D(primitives) : F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!hasOption(graphicsOptions, S.PlotRange)) {
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

  /** The number of grid points along each axis. */
  private int[] vectorPoints(IAST ast, EvalEngine engine) {
    int[] points = new int[dimension];
    java.util.Arrays.fill(points, dimension == 3 ? 7 : 15);
    for (int i = dimension + 2; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first() == S.VectorPoints) {
        IExpr value = engine.evaluate(option.second());
        if (value.isList() && value.argSize() == dimension) {
          for (int d = 0; d < dimension; d++) {
            int n = value.getAt(d + 1).toIntDefault();
            if (n >= 1) {
              points[d] = n;
            }
          }
        } else {
          int n = value.toIntDefault();
          if (n >= 1) {
            java.util.Arrays.fill(points, n);
          }
        }
      }
    }
    return points;
  }

  /** The field's value at <code>point</code>, or <code>null</code> where it is not numeric. */
  private double[] fieldAt(IExpr field, ISymbol[] variables, double[] point, EvalEngine engine) {
    IASTAppendable assignments = F.ListAlloc(dimension);
    for (int d = 0; d < dimension; d++) {
      assignments.append(F.Set(variables[d], F.num(point[d])));
    }
    IExpr value;
    try {
      value = engine.evalQuiet(F.N(F.Block(assignments, field)));
    } catch (RuntimeException rex) {
      return null;
    }
    if (!value.isList() || value.argSize() != dimension) {
      return null;
    }
    double[] vector = new double[dimension];
    for (int d = 0; d < dimension; d++) {
      IExpr component = value.getAt(d + 1);
      if (!component.isReal()) {
        return null;
      }
      vector[d] = component.evalf();
    }
    return vector;
  }

  private static double norm(double[] vector) {
    double sum = 0.0;
    for (double c : vector) {
      sum += c * c;
    }
    return Math.sqrt(sum);
  }

  /** From blue for the shortest arrow to red for the longest, <code>t</code> in 0..1. */
  private static IAST color(double t) {
    double r = 0.18 + 0.72 * t;
    double g = 0.36 - 0.16 * t;
    double b = 0.75 - 0.6 * t;
    return F.RGBColor(F.num(r), F.num(g), F.num(b));
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

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
