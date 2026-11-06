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
 * <code>ChartElementFunction -> "Quantile"</code> or <code>"Density"</code> draws a framed column
 * per dataset instead, laid out like the boxes of a <code>BoxWhiskerChart</code>.
 */
public class DistributionChart extends AbstractChart {

  /** The number of samples of the density along the values. */
  private static final int SAMPLES = 60;

  /** The half width of the widest violin. */
  private static final double HALF_WIDTH = 0.4;

  /** The number of bands of a density column. */
  private static final int STRIPS = 120;

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
    IExpr elementFunction =
        GraphicsOptions.optionValue(originalAST, S.ChartElementFunction, S.Automatic);
    boolean columns = elementFunction.isString("Quantile") || elementFunction.isString("Density");
    // every dataset is drawn; an entry past the last one names it in the first dataset's fill
    graphicsOptions.setChartLegend(chartLegends, i -> {
      int dataset = i < values.length ? i : 0;
      return columns ? columnFill(chartStyle, dataset) : violinFill(chartStyle, dataset);
    });
    if (columns) {
      return columnChart(values, elementFunction.isString("Quantile"), chartStyle, chartLabels,
          graphicsOptions, originalAST, ast, engine);
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
      IExpr color = violinFill(chartStyle, i);
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

  /** The fill of the violin of dataset <code>i</code> (counted from 0). */
  private static IExpr violinFill(IExpr chartStyle, int i) {
    return chartStyle.isAutomatic() || chartStyle.isString()
        ? GraphicsOptions.chartStyleColorExpr(i + 1)
        : chartStyle.isList() ? GraphicsOptions.getPlotStyle(chartStyle, i + 1) : chartStyle;
  }

  /**
   * The fill of the quantile or density column of dataset <code>i</code> (counted from 0): the
   * columns share the first colour, even when a legend names them.
   */
  private static IExpr columnFill(IExpr chartStyle, int i) {
    return chartStyle.isAutomatic() ? GraphicsOptions.chartStyleColorExpr(0)
        : chartStyle.isList() && chartStyle.argSize() > 0
            ? chartStyle.getAt(1 + i % chartStyle.argSize())
            : chartStyle;
  }

  /**
   * The chart of <code>ChartElementFunction -> "Quantile"</code> or <code>"Density"</code>: one
   * column per dataset from its smallest to its largest value. A quantile column is crossed at the
   * quartiles and the median by lines in its fill darkened by 30%; a density column is shaded
   * from the fill, where the kernel density estimate peaks, towards white and dark gray, where it is
   * lowest.
   */
  private IExpr columnChart(double[][] values, boolean quantile, IExpr chartStyle,
      IExpr chartLabels, GraphicsOptions graphicsOptions, IAST originalAST, IAST ast,
      EvalEngine engine) {
    int n = values.length;
    BoxWhiskerChart.BoxSlots slots = new BoxWhiskerChart.BoxSlots(n, BoxWhiskerChart
        .barSpacing(GraphicsOptions.optionValue(originalAST, S.BarSpacing, S.Automatic)), false);
    BoxWhiskerChart.Orientation orientation = new BoxWhiskerChart.Orientation(
        GraphicsOptions.optionValue(originalAST, S.BarOrigin, S.Bottom));
    IAST edge = F.EdgeForm(F.Directive(F.Opacity(F.num(0.5)),
        F.unaryAST1(S.AbsoluteThickness, F.C1)));
    double lo = Double.POSITIVE_INFINITY;
    double hi = Double.NEGATIVE_INFINITY;
    IASTAppendable columns = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      double[] sorted = values[i].clone();
      java.util.Arrays.sort(sorted);
      lo = Math.min(lo, sorted[0]);
      hi = Math.max(hi, sorted[sorted.length - 1]);
      IExpr fill = columnFill(chartStyle, i);
      double[] rgb = rgbOf(fill);
      double c = i + 1.0;
      IASTAppendable column = F.ListAlloc();
      if (quantile) {
        column.append(orientation.rectangle(c - slots.half, sorted[0], c + slots.half,
            sorted[sorted.length - 1]));
        IExpr lineColor = rgb == null ? F.GrayLevel(0.35)
            : F.RGBColor(0.7 * rgb[0], 0.7 * rgb[1], 0.7 * rgb[2]);
        for (double p : new double[] {0.25, 0.5, 0.75}) {
          // the ceil(n p)-th smallest value
          int k = (int) Math.ceil(sorted.length * p);
          double v = sorted[Math.max(1, Math.min(sorted.length, k)) - 1];
          column.append(F.List(lineColor, F.Line(F.List(orientation.point(c - slots.half, v),
              orientation.point(c + slots.half, v)))));
        }
      } else {
        densityStrips(sorted, c, slots.half, orientation,
            rgb == null ? new double[] {1.0, 0.835, 0.25} : rgb, column, engine);
      }
      columns.append(F.List(F.Directive(edge, fill), column));
    }
    IAST primitives = F.List(orientation.rangePoint(slots.low(), slots.high(n), lo, hi), columns);
    if (GraphicsOptions.optionValue(originalAST, S.FrameTicks, null) == null) {
      double span = hi - lo;
      graphicsOptions.addOption(F.Rule(S.FrameTicks,
          orientation.frameTicks(n, chartLabels, lo - 0.11 * span, hi + 0.11 * span)));
    }
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * The shading of a density column, appended to <code>column</code>: the column's rectangle,
   * overlaid by a <code>GraphicsComplex</code> of the bands between the samples of the density,
   * whose two sides are shaded towards white and towards dark gray where the density is lowest and
   * meet in the fill where it peaks.
   */
  private static void densityStrips(double[] sorted, double c, double half,
      BoxWhiskerChart.Orientation orientation, double[] rgb, IASTAppendable column,
      EvalEngine engine) {
    double h = bandwidth(sorted);
    double lo = sorted[0];
    double hi = sorted[sorted.length - 1];
    if (hi <= lo) {
      lo -= h;
      hi += h;
    }
    double[] density = new double[STRIPS + 1];
    double dMin = Double.POSITIVE_INFINITY;
    double dMax = Double.NEGATIVE_INFINITY;
    for (int k = 0; k <= STRIPS; k++) {
      engine.checkTimeLimit();
      double y = lo + (hi - lo) * k / STRIPS;
      double sum = 0.0;
      for (double v : sorted) {
        double u = (y - v) / h;
        sum += Math.exp(-0.5 * u * u);
      }
      density[k] = sum;
      dMin = Math.min(dMin, sum);
      dMax = Math.max(dMax, sum);
    }
    IASTAppendable vertices = F.ListAlloc(2 * STRIPS + 2);
    IASTAppendable colors = F.ListAlloc(2 * STRIPS + 2);
    for (int k = 0; k <= STRIPS; k++) {
      double y = lo + (hi - lo) * k / STRIPS;
      double t = dMax > dMin ? (density[k] - dMin) / (dMax - dMin) : 1.0;
      vertices.append(orientation.point(c - half, y));
      vertices.append(orientation.point(c + half, y));
      colors.append(F.RGBColor(1.0 - t * (1.0 - rgb[0]), 1.0 - t * (1.0 - rgb[1]),
          1.0 - t * (1.0 - rgb[2])));
      colors.append(
          F.RGBColor(0.1 + t * (rgb[0] - 0.1), 0.1 + t * (rgb[1] - 0.1), 0.1 + t * (rgb[2] - 0.1)));
    }
    IASTAppendable bands = F.ListAlloc(STRIPS);
    for (int k = 0; k < STRIPS; k++) {
      bands.append(F.List(F.ZZ(2 * k + 1), F.ZZ(2 * k + 2), F.ZZ(2 * k + 4), F.ZZ(2 * k + 3)));
    }
    column.append(F.headAST0(S.EdgeForm));
    column.append(orientation.rectangle(c - half, lo, c + half, hi));
    column.append(F.ternaryAST3(S.GraphicsComplex, vertices, F.Polygon(bands),
        F.Rule(S.VertexColors, colors)));
  }

  /** The red, green and blue of a plain <code>RGBColor</code>, or <code>null</code>. */
  private static double[] rgbOf(IExpr color) {
    if (!color.isAST(S.RGBColor, 4)) {
      return null;
    }
    double[] rgb = new double[3];
    for (int i = 0; i < 3; i++) {
      rgb[i] = color.getAt(i + 1).evalfNaN();
      if (!Double.isFinite(rgb[i])) {
        return null;
      }
    }
    return rgb;
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
