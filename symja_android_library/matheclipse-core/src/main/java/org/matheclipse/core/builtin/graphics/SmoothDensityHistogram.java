package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.hipparchus.stat.descriptive.moment.StandardDeviation;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>SmoothDensityHistogram(points)</code>, <code>SmoothDensityHistogram(points, bw)</code> -
 * the density plot of the Gaussian kernel density estimate of a list of 2D points.
 *
 * <p>
 * The bandwidth <code>bw</code> is a number for both directions, a list <code>{bx, by}</code> of
 * one for each direction, or <code>"Silverman"</code> (the default) or <code>"Scott"</code> for the
 * rule of that name applied to each coordinate. The estimate is sampled on a grid over the points
 * widened by three bandwidths on every side, and drawn by <code>ListDensityPlot</code>, which
 * receives the options.
 */
public class SmoothDensityHistogram extends AbstractFunctionEvaluator {

  /** The number of samples of the estimate in each direction. */
  private static final int GRID = 50;

  public SmoothDensityHistogram() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr data = engine.evaluate(PlotWrapper.strip(ast.arg1()));
    if (!data.isList()) {
      return F.NIL;
    }
    IAST points = (IAST) data;
    int n = points.argSize();
    double[] xs = new double[n];
    double[] ys = new double[n];
    int count = 0;
    for (int i = 1; i <= n; i++) {
      IExpr point = points.get(i);
      if (!point.isList2()) {
        return F.NIL;
      }
      double x = point.first().evalfNaN();
      double y = point.second().evalfNaN();
      if (Double.isFinite(x) && Double.isFinite(y)) {
        xs[count] = x;
        ys[count] = y;
        count++;
      }
    }
    if (count == 0) {
      return F.NIL;
    }
    xs = Arrays.copyOf(xs, count);
    ys = Arrays.copyOf(ys, count);

    IExpr bandwidthSpec = S.Automatic;
    int optionsStart = 2;
    if (ast.argSize() >= 2 && !ast.arg2().isRuleAST()) {
      bandwidthSpec = ast.arg2();
      optionsStart = 3;
      if (ast.argSize() >= 3 && !ast.arg3().isRuleAST()) {
        // the density is the only kind of distribution function drawn
        if (!ast.arg3().isString("PDF")) {
          return F.NIL;
        }
        optionsStart = 4;
      }
    }
    double[] bandwidth = bandwidth(bandwidthSpec, xs, ys);
    if (bandwidth == null) {
      return F.NIL;
    }
    final double hx = bandwidth[0];
    final double hy = bandwidth[1];
    double minX = Arrays.stream(xs).min().getAsDouble() - 3.0 * hx;
    double maxX = Arrays.stream(xs).max().getAsDouble() + 3.0 * hx;
    double minY = Arrays.stream(ys).min().getAsDouble() - 3.0 * hy;
    double maxY = Arrays.stream(ys).max().getAsDouble() + 3.0 * hy;

    final double norm = 1.0 / (count * 2.0 * Math.PI * hx * hy);
    IASTAppendable matrix = F.ListAlloc(GRID);
    for (int row = 0; row < GRID; row++) {
      double y = minY + (maxY - minY) * row / (GRID - 1);
      IASTAppendable values = F.ListAlloc(GRID);
      for (int column = 0; column < GRID; column++) {
        double x = minX + (maxX - minX) * column / (GRID - 1);
        double sum = 0.0;
        for (int k = 0; k < count; k++) {
          double u = (x - xs[k]) / hx;
          double v = (y - ys[k]) / hy;
          sum += Math.exp(-0.5 * (u * u + v * v));
        }
        values.append(F.num(sum * norm));
      }
      matrix.append(values);
    }

    IASTAppendable densityPlot = F.ast(S.ListDensityPlot, ast.size() + 1);
    densityPlot.append(matrix);
    densityPlot.append(F.Rule(S.DataRange,
        F.list(F.list(F.num(minX), F.num(maxX)), F.list(F.num(minY), F.num(maxY)))));
    for (int i = optionsStart; i < ast.size(); i++) {
      if (!ast.get(i).isRuleAST()) {
        return F.NIL;
      }
      densityPlot.append(ast.get(i));
    }
    return engine.evaluate(densityPlot);
  }

  /**
   * The bandwidths <code>{hx, hy}</code> of a specification.
   *
   * @return <code>null</code> if the specification isn't valid
   */
  private static double[] bandwidth(IExpr spec, double[] xs, double[] ys) {
    if (spec.isList2()) {
      double hx = spec.first().evalfNaN();
      double hy = spec.second().evalfNaN();
      return hx > 0.0 && hy > 0.0 && Double.isFinite(hx) && Double.isFinite(hy)
          ? new double[] {hx, hy}
          : null;
    }
    if (spec.isReal()) {
      double h = spec.evalfNaN();
      return h > 0.0 && Double.isFinite(h) ? new double[] {h, h} : null;
    }
    boolean scott = spec.isString("Scott");
    if (!scott && !spec.isString("Silverman") && spec != S.Automatic) {
      return null;
    }
    return new double[] {ruleOfThumb(xs, scott), ruleOfThumb(ys, scott)};
  }

  /**
   * Silverman's rule <code>0.9 * Min(sigma, IQR/1.34) * n^(-1/5)</code> or Scott's rule
   * <code>3.49 * sigma * n^(-1/3)</code> for one coordinate; for points without any spread the
   * bandwidth is a tenth of the range or <code>1</code>.
   */
  private static double ruleOfThumb(double[] values, boolean scott) {
    final int n = values.length;
    double sigma = n > 1 ? new StandardDeviation().evaluate(values) : 0.0;
    double h;
    if (scott) {
      h = 3.49 * sigma * Math.pow(n, -1.0 / 3.0);
    } else {
      double[] sorted = values.clone();
      Arrays.sort(sorted);
      double iqr = quantile(sorted, 0.75) - quantile(sorted, 0.25);
      double spread = iqr > 0.0 ? Math.min(sigma, iqr / 1.34) : sigma;
      h = 0.9 * spread * Math.pow(n, -0.2);
    }
    if (!(h > 0.0) || !Double.isFinite(h)) {
      double range = values.length > 0 ? Arrays.stream(values).max().getAsDouble()
          - Arrays.stream(values).min().getAsDouble() : 0.0;
      h = range > 0.0 ? range / 10.0 : 1.0;
    }
    return h;
  }

  /** The linearly interpolated quantile of sorted values. */
  private static double quantile(double[] sorted, double p) {
    double position = p * (sorted.length - 1);
    int lower = (int) Math.floor(position);
    int upper = Math.min(sorted.length - 1, lower + 1);
    return sorted[lower] + (sorted[upper] - sorted[lower]) * (position - lower);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
