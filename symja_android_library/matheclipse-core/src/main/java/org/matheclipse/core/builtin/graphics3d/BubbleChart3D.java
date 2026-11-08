package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BubbleChart3D({{x, y, z, s}, ...})</code> - a sphere at every point <code>{x, y, z}</code>
 * whose volume stands for the size <code>s</code>.
 *
 * <p>
 * The largest sphere's radius is a fifth of the smallest extent of the points along an axis, and
 * the others shrink with the cube root of their size. Several datasets, <code>{{{x, y, z, s},
 * ...}, ...}</code>, each take a chart colour of their own.
 */
public class BubbleChart3D extends AbstractEvaluator {

  /** The largest radius as a fraction of the smallest extent of the points. */
  private static final double MAX_RADIUS = 0.2;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr data = GraphicsOptions.chartData(ast.arg1(), F.ListAlloc());
    if (!data.isList() || data.argSize() == 0) {
      return F.NIL;
    }
    int argSize = BarChart3D.positionalArguments(ast);
    IExpr chartStyle = GraphicsOptions.optionValue(ast, S.ChartStyle, S.Automatic);
    // one dataset of quadruples, or a list of them
    IAST datasets = quadruple(PlotWrapper.strip(data.first())) != null ? F.List(data) : (IAST) data;
    List<List<double[]>> sets = new ArrayList<>();
    List<double[]> all = new ArrayList<>();
    for (IExpr dataset : datasets) {
      List<double[]> set = new ArrayList<>();
      if (dataset.isList()) {
        for (IExpr item : (IAST) dataset) {
          double[] point = quadruple(PlotWrapper.strip(item));
          if (point != null) {
            set.add(point);
            all.add(point);
          }
        }
      }
      sets.add(set);
    }
    if (all.isEmpty()) {
      return F.NIL;
    }
    double extent = Double.MAX_VALUE;
    for (int c = 0; c < 3; c++) {
      double low = Double.MAX_VALUE;
      double high = -Double.MAX_VALUE;
      for (double[] p : all) {
        low = Math.min(low, p[c]);
        high = Math.max(high, p[c]);
      }
      if (high > low) {
        extent = Math.min(extent, high - low);
      }
    }
    if (extent == Double.MAX_VALUE) {
      extent = 1.0;
    }
    double largest = 0.0;
    for (double[] p : all) {
      largest = Math.max(largest, Math.abs(p[3]));
    }
    double maxRadius = MAX_RADIUS * extent;
    IASTAppendable primitives = F.ListAlloc(sets.size());
    for (int d = 0; d < sets.size(); d++) {
      if (sets.get(d).isEmpty()) {
        continue;
      }
      IASTAppendable group = F.ListAlloc(sets.get(d).size() + 2);
      group.append(chartStyle.isAutomatic()
          ? BarChart3D.rgbColor(BarChart3D.defaultColor(d)[0], BarChart3D.defaultColor(d)[1],
              BarChart3D.defaultColor(d)[2])
          : GraphicsOptions.chartStyleColor(chartStyle, d));
      group.append(F.binaryAST2(S.Specularity, F.GrayLevel(F.C1), F.ZZ(20)));
      for (double[] p : sets.get(d)) {
        double radius =
            largest > 0.0 ? maxRadius * Math.cbrt(Math.abs(p[3]) / largest) : maxRadius;
        group.append(F.binaryAST2(S.Sphere, F.List(F.num(p[0]), F.num(p[1]), F.num(p[2])),
            F.num(radius)));
      }
      primitives.append(group);
    }
    IExpr[] defaults = { //
        F.Rule(S.Axes, S.True), //
        F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.C1)), //
        F.Rule(S.Lighting, F.stringx("Neutral"))};
    return Plot3DTools.graphics3D(primitives, ast, argSize, defaults, false);
  }

  /** The four numbers of <code>{x, y, z, s}</code>, or <code>null</code>. */
  private static double[] quadruple(IExpr item) {
    if (!item.isList() || item.argSize() != 4) {
      return null;
    }
    double[] result = new double[4];
    for (int i = 0; i < 4; i++) {
      result[i] = item.getAt(i + 1).evalfNaN();
      if (!Double.isFinite(result[i])) {
        return null;
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
