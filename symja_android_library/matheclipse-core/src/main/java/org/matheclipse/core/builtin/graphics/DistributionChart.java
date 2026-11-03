package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DistributionChart({data1, data2, ...})</code> - a chart of the distributions of the
 * datasets: every dataset is a violin, the Gaussian kernel density estimate of its values drawn
 * symmetrically around its position on the horizontal axis, scaled so that the widest violin has
 * the full width of its slot. The bandwidth is Silverman's rule of thumb. <code>ChartStyle</code>,
 * <code>ChartLabels</code> and <code>ChartLegends</code> are read as for the other charts.
 */
public class DistributionChart extends AbstractChart {

  /** The number of samples of the density along the values. */
  private static final int SAMPLES = 60;

  /** The half width of the widest violin. */
  private static final double HALF_WIDTH = 0.4;

  public DistributionChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }
    IExpr dataArg = RectangleChart.chartData(engine.evaluate(ast.arg1()), engine);
    if (!dataArg.isList() || dataArg.argSize() == 0) {
      return F.NIL;
    }
    IAST datasets = dataArg.first().isList() ? (IAST) dataArg : F.list(dataArg);
    double[][] values = new double[datasets.argSize()][];
    for (int i = 0; i < values.length; i++) {
      IExpr dataset = datasets.get(i + 1);
      values[i] = dataset.isList() ? dataset.toDoubleVector() : null;
      if (values[i] == null || values[i].length == 0) {
        return F.NIL;
      }
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    // the densities on a grid over the values of each dataset, widened by the bandwidth
    double[][] grid = new double[values.length][];
    double[][] density = new double[values.length][];
    double largest = 0.0;
    double yMin = Double.POSITIVE_INFINITY;
    double yMax = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < values.length; i++) {
      double[] data = values[i];
      double bandwidth = bandwidth(data);
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for (double v : data) {
        min = Math.min(min, v);
        max = Math.max(max, v);
      }
      min -= 2.0 * bandwidth;
      max += 2.0 * bandwidth;
      grid[i] = new double[SAMPLES];
      density[i] = new double[SAMPLES];
      for (int k = 0; k < SAMPLES; k++) {
        double y = min + (max - min) * k / (SAMPLES - 1);
        double sum = 0.0;
        for (double v : data) {
          double u = (y - v) / bandwidth;
          sum += Math.exp(-0.5 * u * u);
        }
        grid[i][k] = y;
        density[i][k] = sum / (data.length * bandwidth * Math.sqrt(2.0 * Math.PI));
        largest = Math.max(largest, density[i][k]);
      }
      yMin = Math.min(yMin, min);
      yMax = Math.max(yMax, max);
    }

    IASTAppendable primitives = F.ListAlloc(values.length);
    for (int i = 0; i < values.length; i++) {
      double x = i + 1.0;
      IASTAppendable outline = F.ListAlloc(2 * SAMPLES);
      for (int k = 0; k < SAMPLES; k++) {
        outline.append(F.list(F.num(x + HALF_WIDTH * density[i][k] / largest), F.num(grid[i][k])));
      }
      for (int k = SAMPLES - 1; k >= 0; k--) {
        outline.append(F.list(F.num(x - HALF_WIDTH * density[i][k] / largest), F.num(grid[i][k])));
      }
      IExpr color = chartStyle.isAutomatic() || chartStyle.isString()
          ? GraphicsOptions.chartStyleColorExpr(i + 1)
          : chartStyle.isList() ? GraphicsOptions.getPlotStyle(chartStyle, i + 1) : chartStyle;
      IASTAppendable group = F.ListAlloc(3);
      group.append(color);
      group.append(F.EdgeForm(F.GrayLevel(F.num(0.5))));
      group.append(F.Polygon(outline));
      primitives.append(group);
      if (chartLabels.isList() && i < chartLabels.argSize()) {
        primitives.append(F.list(F.GrayLevel(F.C0),
            F.Text(chartLabels.getAt(i + 1), F.list(F.num(x), F.num(yMin)), F.list(F.C0, F.C1))));
      }
    }
    graphicsOptions.setBoundingBox(new double[] {0.5, values.length + 0.5, yMin, yMax});
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** Silverman's rule of thumb, or 1 for data without spread. */
  private static double bandwidth(double[] data) {
    int n = data.length;
    double mean = 0.0;
    for (double v : data) {
      mean += v;
    }
    mean /= n;
    double variance = 0.0;
    for (double v : data) {
      variance += (v - mean) * (v - mean);
    }
    double sd = n > 1 ? Math.sqrt(variance / (n - 1)) : 0.0;
    double[] sorted = data.clone();
    java.util.Arrays.sort(sorted);
    double iqr = quantile(sorted, 0.75) - quantile(sorted, 0.25);
    double spread = iqr > 0.0 ? Math.min(sd, iqr / 1.34) : sd;
    double h = 0.9 * spread * Math.pow(n, -0.2);
    return h > 0.0 ? h : 1.0;
  }

  private static double quantile(double[] sorted, double p) {
    double position = p * (sorted.length - 1);
    int lower = (int) Math.floor(position);
    int upper = Math.min(sorted.length - 1, lower + 1);
    return sorted[lower] + (position - lower) * (sorted[upper] - sorted[lower]);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  protected void chartDefaults(IExpr[] defaults) {
    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.False);
    defaults[GraphicsOptions.X_FRAME] = S.True;
  }
}
