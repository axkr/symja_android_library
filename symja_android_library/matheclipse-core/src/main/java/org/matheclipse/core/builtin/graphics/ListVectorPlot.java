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
 * <code>ListVectorPlot[array]</code> and <code>ListVectorPlot[{{{x, y}, {vx, vy}}, ...}]</code> - a
 * vector field given as data, drawn as arrows the way {@link VectorPlot} draws a function.
 *
 * <p>
 * An array of vectors places <code>array[[i, j]]</code> at <code>{j, i}</code>, as the Wolfram
 * Language does, or spreads the columns and rows over <code>DataRange -> {{xmin, xmax}, {ymin,
 * ymax}}</code>. A list of <code>{point, vector}</code> pairs places each vector at its point.
 *
 * <p>
 * Each arrow is centred on its point and the longest spans <code>VectorScale</code> of the spacing
 * between points. They are coloured by their length unless <code>VectorColorFunction -> None</code>.
 * Entries that are not numeric vectors, and zero vectors, get no arrow. Any other option is handed on
 * to the <code>Graphics</code>.
 */
public class ListVectorPlot extends AbstractFunctionEvaluator {

  /** How much of the spacing between points the longest arrow spans. */
  private static final double DEFAULT_SCALE = 0.9;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() < 1 || !ast.arg1().isList()) {
      return F.NIL;
    }
    IAST data = (IAST) engine.evalN(ast.arg1());
    if (!data.isList() || data.argSize() == 0) {
      return F.NIL;
    }

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
    if (isPairList(data)) {
      for (IExpr pair : data) {
        double[] point = vector(pair.first());
        double[] v = vector(pair.second());
        if (point != null && v != null) {
          tails.add(point);
          vectors.add(v);
        }
      }
      spacing = pairSpacing(tails);
    } else {
      int rows = data.argSize();
      int columns = 0;
      for (IExpr row : data) {
        if (!row.isList()) {
          return F.NIL;
        }
        columns = Math.max(columns, row.argSize());
      }
      double x0 = 1.0;
      double dx = 1.0;
      double y0 = 1.0;
      double dy = 1.0;
      if (dataRange != null) {
        x0 = dataRange[0][0];
        dx = columns > 1 ? (dataRange[0][1] - dataRange[0][0]) / (columns - 1) : 1.0;
        y0 = dataRange[1][0];
        dy = rows > 1 ? (dataRange[1][1] - dataRange[1][0]) / (rows - 1) : 1.0;
      }
      for (int i = 1; i <= rows; i++) {
        IAST row = (IAST) data.get(i);
        for (int j = 1; j <= row.argSize(); j++) {
          double[] v = vector(row.get(j));
          if (v != null) {
            tails.add(new double[] {x0 + (j - 1) * dx, y0 + (i - 1) * dy});
            vectors.add(v);
          }
        }
      }
      spacing = Math.min(columns > 1 ? Math.abs(dx) : Double.MAX_VALUE,
          rows > 1 ? Math.abs(dy) : Double.MAX_VALUE);
      if (spacing == Double.MAX_VALUE) {
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

    IASTAppendable primitives = F.ListAlloc(vectors.size() + 1);
    if (!colored) {
      primitives.append(VectorPlot.color(0.0));
    }
    double[] min = {Double.MAX_VALUE, Double.MAX_VALUE};
    double[] max = {-Double.MAX_VALUE, -Double.MAX_VALUE};
    for (int k = 0; k < vectors.size(); k++) {
      double[] p = tails.get(k);
      double[] v = vectors.get(k);
      IAST from = F.list(F.num(p[0] - v[0] * factor / 2), F.num(p[1] - v[1] * factor / 2));
      IAST to = F.list(F.num(p[0] + v[0] * factor / 2), F.num(p[1] + v[1] * factor / 2));
      IAST arrow = F.Arrow(F.list(from, to));
      primitives.append(
          colored ? F.list(VectorPlot.color(VectorPlot.norm(v) / longest), arrow) : arrow);
      for (int d = 0; d < 2; d++) {
        min[d] = Math.min(min[d], p[d]);
        max[d] = Math.max(max[d], p[d]);
      }
    }

    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions);
    if (!vectors.isEmpty() && !hasOption(graphicsOptions, S.PlotRange)) {
      double pad = spacing / 2;
      result.append(F.Rule(S.PlotRange, F.list(F.list(F.num(min[0] - pad), F.num(max[0] + pad)),
          F.list(F.num(min[1] - pad), F.num(max[1] + pad)))));
    }
    if (!hasOption(graphicsOptions, S.Frame)) {
      result.append(F.Rule(S.Frame, S.True));
    }
    return result;
  }

  /** <code>{{{x, y}, {vx, vy}}, ...}</code>: every entry a pair of numeric 2-vectors. */
  private static boolean isPairList(IAST data) {
    for (IExpr entry : data) {
      if (!entry.isList2() || vector(entry.first()) == null || vector(entry.second()) == null) {
        return false;
      }
    }
    return true;
  }

  /** A numeric 2-vector, or <code>null</code>. */
  private static double[] vector(IExpr expr) {
    if (!expr.isList2() || !expr.first().isReal() || !expr.second().isReal()) {
      return null;
    }
    return new double[] {expr.first().evalf(), expr.second().evalf()};
  }

  /** <code>{{xmin, xmax}, {ymin, ymax}}</code>, or <code>null</code>. */
  private static double[][] dataRange(IExpr value) {
    if (!value.isList2()) {
      return null;
    }
    double[] x = vector(value.first());
    double[] y = vector(value.second());
    return x == null || y == null ? null : new double[][] {x, y};
  }

  /** The typical distance between scattered points: the side of the area each one has. */
  private static double pairSpacing(List<double[]> points) {
    if (points.size() < 2) {
      return 1.0;
    }
    double[] min = {Double.MAX_VALUE, Double.MAX_VALUE};
    double[] max = {-Double.MAX_VALUE, -Double.MAX_VALUE};
    for (double[] p : points) {
      for (int d = 0; d < 2; d++) {
        min[d] = Math.min(min[d], p[d]);
        max[d] = Math.max(max[d], p[d]);
      }
    }
    double width = max[0] - min[0];
    double height = max[1] - min[1];
    double spacing;
    if (width > 0 && height > 0) {
      spacing = Math.sqrt(width * height / points.size());
    } else {
      spacing = Math.max(width, height) / (points.size() - 1);
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
