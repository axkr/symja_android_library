package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.CurvePrimitives;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Charts of tier 3.4:
 *
 * <ul>
 * <li><code>CandlestickChart({{open, high, low, close}, ...})</code>, also with dates
 * <code>{{date, {o, h, l, c}}, ...}</code>: a wick from low to high and a body from open to close,
 * hollow where the price rose and filled where it fell;</li>
 * <li><code>PieChart3D({v1, v2, ...})</code>: the <code>SectorChart3D</code> of slices with
 * angles in proportion to the values, all of one radius and height;</li>
 * <li><code>BubbleHistogram({{x1, y1}, ...})</code>: the points counted in the cells of a grid, a
 * bubble for each cell that holds any, its size the count;</li>
 * <li><code>Dendrogram({e1, e2, ...})</code>: the tree of an agglomerative clustering of numbers or
 * vectors, drawn with orthogonal links at the heights of the merges;</li>
 * <li><code>ChartElementData(chart)</code>: the named <code>ChartElementFunction</code> values
 * Symja draws for a chart; <code>ChartElementData()</code> the names of all of them.</li>
 * </ul>
 */
public final class ChartExtras {

  private ChartExtras() {}

  private static int positional(IAST ast) {
    int n = 0;
    while (n < ast.argSize() && !ast.get(n + 1).isRuleAST()) {
      n++;
    }
    return n;
  }

  private static @AllowNIL IExpr option(IAST ast, int from, ISymbol name) {
    for (int i = from; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first() == name) {
        return arg.second();
      }
    }
    return F.NIL;
  }

  // ---------------------------------------------------------------- CandlestickChart

  /** The colour of a rising candle. */
  private static final IAST CANDLE_RISING = F.RGBColor(0.307, 0.704, 0.058);

  /** The colour of a falling candle. */
  private static final IAST CANDLE_FALLING = F.RGBColor(0.834, 0.075, 0.053);

  /** Half the width of a candle's body; the candles stand at 1, 2, 3, ... */
  private static final double CANDLE_HALF_WIDTH = 0.096;

  /**
   * <code>CandlestickChart({{o, h, l, c}, ...})</code> and <code>CandlestickChart({{date, {o, h,
   * l, c}}, ...})</code>: the candles stand at <code>1, 2, 3, ...</code> in the order of the data.
   * A body spans open to close, green when the price rose and red when it fell, with the wicks
   * from the body to the high and the low. A bottom axis carries the dates (or the numbers of the
   * candles); <code>ChartStyle -&gt; {rise, fall}</code> or a single colour replaces the colours.
   */
  public static final class CandlestickChart extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 1) {
        return F.NIL;
      }
      IExpr data = engine.evaluate(ast.arg1());
      if (!data.isList() || data.argSize() == 0) {
        return F.NIL;
      }
      int n = data.argSize();
      double[][] ohlc = new double[n][];
      IExpr[] dates = new IExpr[n];
      for (int i = 0; i < n; i++) {
        IExpr item = data.getAt(i + 1);
        IExpr prices = item;
        if (item.isList2() && item.second().isList() && item.second().argSize() == 4) {
          // {date, {o, h, l, c}}
          IExpr time = engine.evaluate(F.unaryAST1(S.AbsoluteTime, item.first()));
          if (!time.isReal()) {
            return F.NIL;
          }
          dates[i] = item.first();
          prices = item.second();
        }
        ohlc[i] = prices4(prices);
        if (ohlc[i] == null) {
          return Errors.printMessage(S.CandlestickChart, "arrayerr", F.list(ast.arg1()), engine);
        }
      }
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (double[] p : ohlc) {
        low = Math.min(low, p[2]);
        high = Math.max(high, p[1]);
      }
      double base = high > low ? low - 0.1035 * (high - low) : low - 1;
      // ChartStyle -> {rise, fall}; a single colour, or a list of one, colours both
      IExpr style = option(ast, 2, S.ChartStyle);
      if (style.isList() && style.argSize() == 1) {
        style = style.first();
      }
      IExpr risingColor = style.isList() && style.argSize() >= 2 ? style.first()
          : style.isPresent() && !style.isAutomatic() && !style.isList() ? style : CANDLE_RISING;
      IExpr fallingColor = style.isList() && style.argSize() >= 2 ? style.second()
          : style.isPresent() && !style.isAutomatic() && !style.isList() ? style : CANDLE_FALLING;
      IASTAppendable rising = F.ListAlloc(2 * n + 2);
      rising.append(risingColor);
      rising.append(F.EdgeForm(risingColor));
      IASTAppendable falling = F.ListAlloc(2 * n + 2);
      falling.append(fallingColor);
      falling.append(F.EdgeForm(fallingColor));
      for (int i = 0; i < n; i++) {
        double[] p = ohlc[i];
        double x = i + 1;
        double bottom = Math.min(p[0], p[3]);
        double top = Math.max(p[0], p[3]);
        IASTAppendable candle = p[3] >= p[0] ? rising : falling;
        IASTAppendable wicks = F.ListAlloc(2);
        if (p[2] < bottom) {
          wicks.append(F.List(F.List(F.num(x), F.num(p[2])), F.List(F.num(x), F.num(bottom))));
        }
        if (p[1] > top) {
          wicks.append(F.List(F.List(F.num(x), F.num(p[1])), F.List(F.num(x), F.num(top))));
        }
        if (wicks.argSize() > 0) {
          candle.append(F.Line(wicks));
        }
        if (top > bottom) {
          candle.append(F.Rectangle(F.List(F.num(x - CANDLE_HALF_WIDTH), F.num(bottom)),
              F.List(F.num(x + CANDLE_HALF_WIDTH), F.num(top))));
        } else {
          candle.append(F.Line(F.List(F.List(F.num(x - CANDLE_HALF_WIDTH), F.num(top)),
              F.List(F.num(x + CANDLE_HALF_WIDTH), F.num(top)))));
        }
      }
      IASTAppendable content = F.ListAlloc(3);
      content.append(rising);
      content.append(falling);
      content.append(bottomAxis(n, base, candleLabels(dates, engine)));
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 8);
      graphics.append(content);
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() != S.ChartStyle) {
          graphics.append(arg);
        }
      }
      appendDefault(graphics, ast, S.AspectRatio, F.Power(S.GoldenRatio, F.CN1));
      appendDefault(graphics, ast, S.Axes, F.List(S.False, S.True));
      appendDefault(graphics, ast, S.AxesOrigin, F.List(F.num(0.5), F.num(base)));
      appendDefault(graphics, ast, S.GridLines, F.List(S.None, S.Automatic));
      appendDefault(graphics, ast, S.GridLinesStyle,
          F.binaryAST2(S.Directive, F.GrayLevel(F.num(0.5)), F.Opacity(F.num(0.4))));
      appendDefault(graphics, ast, S.PlotRange,
          F.List(F.List(F.num(0.5), F.num(n + 0.5)), F.List(F.num(base), S.All)));
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** Appends <code>name -&gt; value</code> unless the call gives the option. */
  private static void appendDefault(IASTAppendable graphics, IAST ast, ISymbol name,
      IExpr value) {
    if (option(ast, 2, name).isNIL()) {
      graphics.append(F.Rule(name, value));
    }
  }

  /**
   * The labels of the candles: their numbers, or their dates - the day when all fall into one
   * month, the month and day within one year, else the month and year. At most about 8 candles
   * are labelled.
   */
  private static String[] candleLabels(IExpr[] dates, EvalEngine engine) {
    int n = dates.length;
    String[] labels = new String[n];
    int step = (n + 7) / 8;
    boolean dated = true;
    for (IExpr date : dates) {
      dated &= date != null;
    }
    IExpr format = F.List(F.$str("DayShort"));
    if (dated) {
      String firstMonth = dateString(dates[0], F.List(F.$str("Year"), F.$str("Month")), engine);
      String firstYear = dateString(dates[0], F.List(F.$str("Year")), engine);
      boolean sameMonth = true;
      boolean sameYear = true;
      for (IExpr date : dates) {
        sameMonth &= firstMonth.equals(
            dateString(date, F.List(F.$str("Year"), F.$str("Month")), engine));
        sameYear &= firstYear.equals(dateString(date, F.List(F.$str("Year")), engine));
      }
      format = sameMonth ? F.List(F.$str("DayShort"))
          : sameYear ? F.List(F.$str("MonthNameShort"), F.$str(" "), F.$str("DayShort"))
              : F.List(F.$str("MonthNameShort"), F.$str(" "), F.$str("Year"));
    }
    for (int i = 0; i < n; i += step) {
      labels[i] = dated ? dateString(dates[i], format, engine) : Integer.toString(i + 1);
    }
    return labels;
  }

  private static String dateString(IExpr date, IExpr format, EvalEngine engine) {
    return engine.evaluate(F.binaryAST2(S.DateString, date, format)).toString();
  }

  /**
   * The bottom axis of a candlestick chart at height <code>base</code>: a line with ticks between
   * the candles and the labels below the candles.
   */
  private static IAST bottomAxis(int n, double base, String[] labels) {
    IASTAppendable axis = F.ListAlloc(2 * n + 2);
    axis.append(F.GrayLevel(F.num(0.4)));
    axis.append(F.Line(F.List(F.List(F.num(0.5), F.num(base)),
        F.List(F.num(n + 0.5), F.num(base)))));
    for (int i = 1; i < n; i++) {
      IAST at = F.List(F.num(i + 0.5), F.num(base));
      axis.append(F.Line(F.List(at, F.binaryAST2(S.Offset, F.List(F.C0, F.CN4), at))));
    }
    for (int i = 0; i < n; i++) {
      if (labels[i] != null) {
        axis.append(F.Text(F.$str(labels[i]),
            F.binaryAST2(S.Offset, F.List(F.C0, F.ZZ(-5)), F.List(F.num(i + 1), F.num(base))),
            F.List(F.C0, F.C1)));
      }
    }
    return axis;
  }

  /** <code>{open, high, low, close}</code> as numbers, or {@code null}. */
  private static double[] prices4(IExpr prices) {
    if (!prices.isList() || prices.argSize() != 4) {
      return null;
    }
    double[] p = new double[4];
    for (int k = 0; k < 4; k++) {
      p[k] = prices.getAt(k + 1).evalfNaN();
      if (!Double.isFinite(p[k])) {
        return null;
      }
    }
    return p;
  }

  // ---------------------------------------------------------------- PieChart3D

  /** The height of the slices of a <code>PieChart3D</code>; the box is flattened to 0.1. */
  private static final double PIE_HEIGHT = 1.0;

  /**
   * <code>PieChart3D({v1, v2, ...}, options)</code>: slices of radius 1 and height 1 in a box
   * flattened to <code>BoxRatios -&gt; {1, 1, 0.1}</code>, clockwise from the left. Several datasets
   * are rings 0.5 apart around the first one, each coloured from the first colour on.
   */
  public static final class PieChart3D extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (positional(ast) != 1) {
        return F.NIL;
      }
      IExpr data = engine.evaluate(ast.arg1());
      if (!data.isList() || data.argSize() == 0) {
        return F.NIL;
      }
      IAST list = (IAST) data;
      IExpr sectors;
      if (list.forAll(x -> x.isList())) {
        IASTAppendable datasets = F.ListAlloc(list.argSize());
        for (IExpr dataset : list) {
          IExpr ring = slices((IAST) dataset);
          if (ring.isNIL()) {
            return F.NIL;
          }
          datasets.append(ring);
        }
        sectors = datasets;
      } else {
        sectors = slices(list);
      }
      if (sectors.isNIL()) {
        return F.NIL;
      }
      IASTAppendable chart = F.ast(S.SectorChart3D, ast.size());
      chart.append(sectors);
      chart.appendAll(ast, 2, ast.size());
      return org.matheclipse.core.builtin.graphics3d.SectorChart3D.chart(chart, true);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The sectors <code>{v, 1, height}</code> of the values of a pie. */
  private static @AllowNIL IExpr slices(IAST values) {
    IASTAppendable sectors = F.ListAlloc(values.argSize());
    for (IExpr v : values) {
      if (!v.isReal() || v.evalfNaN() < 0) {
        return F.NIL;
      }
      sectors.append(F.List(v, F.C1, F.num(PIE_HEIGHT)));
    }
    return sectors;
  }

  // ---------------------------------------------------------------- BubbleHistogram

  /**
   * <code>BubbleHistogram({{x1, y1}, ...})</code>, <code>BubbleHistogram(data, {d})</code> with
   * cells of width <code>d</code> in both directions, and <code>BubbleHistogram(data, {dx,
   * dy})</code>: the cells start at the multiples of the widths (along an axis whose coordinates
   * are all multiples of the width they are centred on them), and each cell which holds points
   * gets a bubble at the mean of its points. The radius grows linearly with the number of points, from
   * a tenth to half of the cell, and the colour runs from blue (fewest) over red to yellow (most);
   * <code>ChartStyle</code> gives one colour for all bubbles or a list taken in turn.
   * By default the cells are of a round width giving about <code>1 + log2(n)</code> of them along
   * each axis.
   */
  public static final class BubbleHistogram extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      int n = positional(ast);
      if (n < 1 || n > 2) {
        return F.NIL;
      }
      IExpr data = engine.evalN(ast.arg1());
      if (!data.isList() || data.argSize() == 0) {
        return F.NIL;
      }
      List<double[]> points = new ArrayList<>();
      for (IExpr p : (IAST) data) {
        double[] xy = p.isList2() ? new double[] {p.first().evalfNaN(), p.second().evalfNaN()}
            : null;
        if (xy == null || !Double.isFinite(xy[0]) || !Double.isFinite(xy[1])) {
          return Errors.printMessage(S.BubbleHistogram, "arrayerr", F.list(ast.arg1()), engine);
        }
        points.add(xy);
      }
      double[] min = {Double.MAX_VALUE, Double.MAX_VALUE};
      double[] max = {-Double.MAX_VALUE, -Double.MAX_VALUE};
      for (double[] p : points) {
        for (int a = 0; a < 2; a++) {
          min[a] = Math.min(min[a], p[a]);
          max[a] = Math.max(max[a], p[a]);
        }
      }
      double[] width = new double[2];
      if (n == 2) {
        IExpr spec = engine.evalN(ast.arg2());
        if (!spec.isList() || spec.argSize() < 1 || spec.argSize() > 2) {
          return F.NIL;
        }
        width[0] = spec.first().evalfNaN();
        width[1] = spec.argSize() == 2 ? spec.second().evalfNaN() : width[0];
        if (!(width[0] > 0) || !(width[1] > 0)) {
          return F.NIL;
        }
      } else {
        double bins = 1 + Math.ceil(Math.log(points.size()) / Math.log(2));
        for (int a = 0; a < 2; a++) {
          width[a] = roundWidth((max[a] - min[a]) / bins);
        }
      }
      // the cells start at the multiples of the widths, or are centred on them when all
      // coordinates are multiples; a bubble sits at the mean of its points
      double[] shift = new double[2];
      for (int a = 0; a < 2; a++) {
        boolean multiples = true;
        for (double[] p : points) {
          double q = p[a] / width[a];
          multiples &= Math.abs(q - Math.rint(q)) < 1.0e-9;
        }
        shift[a] = multiples ? 0.5 : 0.0;
      }
      java.util.Map<List<Long>, double[]> cells = new java.util.LinkedHashMap<>();
      for (double[] p : points) {
        double qx = Math.floor(p[0] / width[0] + shift[0]);
        double qy = Math.floor(p[1] / width[1] + shift[1]);
        if (!(Math.abs(qx) < CELL_INDEX_LIMIT) || !(Math.abs(qy) < CELL_INDEX_LIMIT)) {
          // the cells are too small to be numbered
          return F.NIL;
        }
        List<Long> cell = List.of((long) qx, (long) qy);
        double[] sum = cells.computeIfAbsent(cell, c -> new double[3]);
        sum[0] += p[0];
        sum[1] += p[1];
        sum[2]++;
      }
      int most = 0;
      int least = Integer.MAX_VALUE;
      long[] kMin = {Long.MAX_VALUE, Long.MAX_VALUE};
      long[] kMax = {Long.MIN_VALUE, Long.MIN_VALUE};
      for (java.util.Map.Entry<List<Long>, double[]> e : cells.entrySet()) {
        int count = (int) e.getValue()[2];
        most = Math.max(most, count);
        least = Math.min(least, count);
        for (int a = 0; a < 2; a++) {
          kMin[a] = Math.min(kMin[a], e.getKey().get(a));
          kMax[a] = Math.max(kMax[a], e.getKey().get(a));
        }
      }
      IExpr style = option(ast, n + 1, S.ChartStyle);
      IASTAppendable bubbles = F.ListAlloc(cells.size());
      int index = 0;
      for (double[] sum : cells.values()) {
        int count = (int) sum[2];
        // the radius grows linearly with the count, up to half the cell
        double r = 0.1 + 0.4 * count / most;
        double t = most > least ? (double) (count - least) / (most - least) : 0.5;
        IExpr color = style.isList() && style.argSize() > 0
            ? style.getAt(index % style.argSize() + 1)
            : style.isPresent() && !style.isAutomatic() ? style : bubbleColor(t);
        index++;
        bubbles.append(F.List(F.EdgeForm(S.None), color,
            F.Disk(F.List(F.num(sum[0] / count), F.num(sum[1] / count)),
                F.List(F.num(r * width[0]), F.num(r * width[1])))));
      }
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 4);
      graphics.append(bubbles);
      // the options of the picture; the chart's own options are read above
      for (int i = n + 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && !isChartOption(arg.first())) {
          graphics.append(arg);
        }
      }
      appendDefault(graphics, ast, S.Frame, S.True);
      appendDefault(graphics, ast, S.AspectRatio, F.C1);
      appendDefault(graphics, ast, S.PlotRange, F.List(
          F.List(F.num((kMin[0] - shift[0]) * width[0]), F.num((kMax[0] + 1 - shift[0]) * width[0])),
          F.List(F.num((kMin[1] - shift[1]) * width[1]),
              F.num((kMax[1] + 1 - shift[1]) * width[1]))));
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The options of a chart, which are no options of its picture. */
  private static boolean isChartOption(IExpr key) {
    return key == S.ChartStyle || key == S.ChartElementFunction || key == S.ChartElements
        || key == S.ChartLabels || key == S.ChartLegends || key == S.ChartLayout
        || key == S.ChartBaseStyle || key == S.BubbleSizes
        || key == S.LabelingFunction;
  }

  /** The largest cell number of a bubble histogram, so that it fits into a long exactly. */
  private static final double CELL_INDEX_LIMIT = 0x1p52;

  /** The colours of the fewest, the middle and the most points in a cell. */
  private static final double[][] BUBBLE_COLORS = {{0.0, 0.08, 0.697}, {0.83, 0.313, 0.372},
      {1.0, 0.745, 0.044}};

  /** The colour of a bubble, <code>t</code> from 0 (fewest points) to 1 (most points). */
  private static IAST bubbleColor(double t) {
    double u = 2 * Math.min(1, Math.max(0, t));
    int k = Math.min(1, (int) u);
    double f = u - k;
    double[] a = BUBBLE_COLORS[k];
    double[] b = BUBBLE_COLORS[k + 1];
    return F.RGBColor(a[0] + f * (b[0] - a[0]), a[1] + f * (b[1] - a[1]),
        a[2] + f * (b[2] - a[2]));
  }

  /** A width of 1, 2 or 5 times a power of ten, not smaller than <code>w</code>. */
  private static double roundWidth(double w) {
    if (!(w > 0) || !Double.isFinite(w)) {
      return 1.0;
    }
    double power = Math.pow(10, Math.floor(Math.log10(w)));
    for (double m : new double[] {1, 2, 5, 10}) {
      if (m * power >= w * (1 - 1e-12)) {
        return m * power;
      }
    }
    return 10 * power;
  }

  // ---------------------------------------------------------------- Dendrogram

  /** A cluster: its leaves in drawing order, the height it was merged at, its drawn position. */
  private static final class Cluster {
    final List<Integer> leaves;
    final double height;
    final double x;

    Cluster(List<Integer> leaves, double height, double x) {
      this.leaves = leaves;
      this.height = height;
      this.x = x;
    }
  }

  /**
   * <code>Dendrogram({e1, e2, ...})</code> of numbers or vectors, <code>Dendrogram({e1 -&gt; l1,
   * ...})</code> and <code>Dendrogram(data -&gt; labels)</code> with labels. The clusters nearest
   * to each other are merged, step by step, by their <code>DistanceFunction</code> distance
   * (default <code>EuclideanDistance</code>); the option <code>ClusterDissimilarityFunction</code>
   * chooses how the distance of two clusters is measured: <code>"Single"</code> (the default, the
   * nearest members), <code>"Complete"</code> (the farthest), <code>"Average"</code> or
   * <code>"Ward"</code>. Leaves sit at <code>1, 2, ...</code> along the bottom, a merge at its
   * distance above.
   */
  public static final class Dendrogram extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      // the data may be a rule data -> labels itself, so the options start after it
      if (ast.argSize() < 1 || (ast.argSize() >= 2 && !ast.arg2().isRuleAST())) {
        return F.NIL;
      }
      IExpr data = engine.evaluate(ast.arg1());
      IExpr labelList = F.NIL;
      if (data.isRuleAST() && data.first().isList() && data.second().isList()
          && data.first().argSize() == data.second().argSize()) {
        labelList = data.second();
        data = data.first();
      }
      if (!data.isList() || data.argSize() < 2) {
        return F.NIL;
      }
      IAST elements = (IAST) data;
      if (labelList.isNIL() && elements.forAll(x -> x.isRuleAST())) {
        labelList = elements.map(x -> x.second(), 1);
        elements = elements.map(x -> x.first(), 1);
      }
      int n = elements.argSize();
      double[][] vectors = new double[n][];
      for (int i = 0; i < n; i++) {
        vectors[i] = vector(engine.evalN(elements.get(i + 1)));
        if (vectors[i] == null || (i > 0 && vectors[i].length != vectors[0].length)) {
          return F.NIL;
        }
      }
      IExpr distanceFunction = option(ast, 2, S.DistanceFunction);
      double[][] distance = new double[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          double d = distance(vectors[i], vectors[j], elements.get(i + 1), elements.get(j + 1),
              distanceFunction, engine);
          if (!Double.isFinite(d)) {
            return F.NIL;
          }
          distance[i][j] = distance[j][i] = d;
        }
        engine.checkTimeLimit();
      }
      IExpr linkageSpec = CurvePrimitives.optionByName(ast, "ClusterDissimilarityFunction");
      String linkage = linkageSpec.isString() ? linkageSpec.toString() : "Single";
      List<IAST> links = new ArrayList<>();
      List<Integer> order = agglomerate(distance, linkage, links);
      if (order == null) {
        return F.NIL;
      }
      IASTAppendable content = F.ListAlloc(3);
      IASTAppendable linkGroup = F.ListAlloc(links.size() + 1);
      linkGroup.append(F.GrayLevel(F.num(0.65)));
      // the links were drawn with the order of the leaves still open: put each leaf in its place
      int[] position = new int[n];
      for (int k = 0; k < n; k++) {
        position[order.get(k)] = k + 1;
      }
      for (IAST link : links) {
        linkGroup.append(F.Line(placed(link, position)));
      }
      content.append(linkGroup);
      IASTAppendable texts = F.ListAlloc(n + 1);
      texts.append(F.GrayLevel(F.C0));
      for (int k = 0; k < n; k++) {
        int leaf = order.get(k);
        IExpr label = labelList.isPresent() ? labelList.getAt(leaf + 1) : elements.get(leaf + 1);
        texts.append(F.Text(label, F.List(F.ZZ(k + 1), F.C0), F.List(F.C0, F.C1)));
      }
      content.append(texts);
      IASTAppendable graphics = F.ast(S.Graphics, ast.size() + 2);
      graphics.append(content);
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST() && arg.first() != S.DistanceFunction && !(arg.first().isSymbol()
            && ((ISymbol) arg.first()).getSymbolName()
                .equalsIgnoreCase("ClusterDissimilarityFunction"))) {
          graphics.append(arg);
        }
      }
      appendDefault(graphics, ast, S.AspectRatio, F.Power(F.C2, F.CN1D2));
      return graphics;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** A number or a vector of numbers, or {@code null}. */
  private static double[] vector(IExpr e) {
    if (e.isReal()) {
      return new double[] {e.evalfNaN()};
    }
    if (e.isList() && e.argSize() > 0) {
      double[] v = new double[e.argSize()];
      for (int i = 0; i < v.length; i++) {
        IExpr c = e.getAt(i + 1);
        if (!c.isReal()) {
          return null;
        }
        v[i] = c.evalfNaN();
      }
      return v;
    }
    return null;
  }

  private static double distance(double[] a, double[] b, IExpr ea, IExpr eb, IExpr function,
      EvalEngine engine) {
    if (function.isPresent() && !function.isAutomatic() && function != S.EuclideanDistance) {
      return engine.evaluate(F.binaryAST2(function, ea, eb)).evalfNaN();
    }
    double sum = 0;
    for (int k = 0; k < a.length; k++) {
      sum += (a[k] - b[k]) * (a[k] - b[k]);
    }
    return Math.sqrt(sum);
  }

  /**
   * Merge the clusters nearest to each other until one is left. Each merge adds its link to
   * <code>links</code> as <code>{{leaf, h1}, {leaf, h}, {leaf, h}, {leaf, h2}}</code>, its ends
   * named by a leaf of each side and the place of the link along the bottom still to be decided
   * (see {@link #placed}).
   *
   * @return the order of the leaves along the bottom, or {@code null} for an unknown linkage
   */
  private static List<Integer> agglomerate(double[][] distance, String linkage,
      List<IAST> links) {
    int n = distance.length;
    if (!new TreeSet<>(List.of("Single", "Complete", "Average", "Ward")).contains(linkage)) {
      return null;
    }
    List<List<Integer>> clusters = new ArrayList<>();
    List<Double> heights = new ArrayList<>();
    // a cluster's position along the bottom is the mean of its two parts' positions: kept as the
    // pair of leaves the link is hung between, resolved once the leaf order is known
    List<double[]> anchors = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      List<Integer> single = new ArrayList<>();
      single.add(i);
      clusters.add(single);
      heights.add(0.0);
      anchors.add(new double[] {i});
    }
    while (clusters.size() > 1) {
      int bestA = -1;
      int bestB = -1;
      double best = Double.POSITIVE_INFINITY;
      for (int a = 0; a < clusters.size(); a++) {
        for (int b = a + 1; b < clusters.size(); b++) {
          double d = linkageDistance(distance, clusters.get(a), clusters.get(b), linkage);
          if (d < best) {
            best = d;
            bestA = a;
            bestB = b;
          }
        }
      }
      List<Integer> left = clusters.get(bestA);
      List<Integer> right = clusters.get(bestB);
      double height = Math.max(best, Math.max(heights.get(bestA), heights.get(bestB)));
      links.add(F.List(anchor(anchors.get(bestA), heights.get(bestA)),
          anchor(anchors.get(bestA), height), anchor(anchors.get(bestB), height),
          anchor(anchors.get(bestB), heights.get(bestB))));
      // the part holding the earlier element goes left, so the leaves keep the order of the data
      // as far as the tree allows
      boolean swap = java.util.Collections.min(right) < java.util.Collections.min(left);
      List<Integer> merged = new ArrayList<>(swap ? right : left);
      merged.addAll(swap ? left : right);
      double[] mergedAnchor = new double[anchors.get(bestA).length + anchors.get(bestB).length];
      System.arraycopy(anchors.get(bestA), 0, mergedAnchor, 0, anchors.get(bestA).length);
      System.arraycopy(anchors.get(bestB), 0, mergedAnchor, anchors.get(bestA).length,
          anchors.get(bestB).length);
      clusters.remove(bestB);
      clusters.remove(bestA);
      heights.remove(bestB);
      heights.remove(bestA);
      anchors.remove(bestB);
      anchors.remove(bestA);
      clusters.add(merged);
      heights.add(height);
      anchors.add(mergedAnchor);
    }
    return clusters.get(0);
  }

  /**
   * The point of a link at a cluster: <code>{{leaves...}, h}</code>, whose position is the mean of
   * the positions of the cluster's two halves, worked out once the leaves are placed.
   */
  private static IAST anchor(double[] leaves, double height) {
    IASTAppendable list = F.ListAlloc(leaves.length);
    for (double leaf : leaves) {
      list.append(F.ZZ((int) leaf));
    }
    return F.List(list, F.num(height));
  }

  /** A link with the positions of its ends worked out: the middle of the leaves they hang from. */
  private static IAST placed(IAST link, int[] position) {
    IASTAppendable points = F.ListAlloc(4);
    for (IExpr point : link) {
      IAST leaves = (IAST) point.first();
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      for (IExpr leaf : leaves) {
        int p = position[leaf.toIntDefault()];
        min = Math.min(min, p);
        max = Math.max(max, p);
      }
      points.append(F.List(F.num((min + max) / 2.0), point.second()));
    }
    return points;
  }

  private static double linkageDistance(double[][] d, List<Integer> a, List<Integer> b,
      String linkage) {
    switch (linkage) {
      case "Complete": {
        double max = 0;
        for (int i : a) {
          for (int j : b) {
            max = Math.max(max, d[i][j]);
          }
        }
        return max;
      }
      case "Average": {
        double sum = 0;
        for (int i : a) {
          for (int j : b) {
            sum += d[i][j];
          }
        }
        return sum / (a.size() * b.size());
      }
      case "Ward": {
        // the growth of the sum of squares, from the squared distances
        double ab = 0;
        double aa = 0;
        double bb = 0;
        for (int i : a) {
          for (int j : b) {
            ab += d[i][j] * d[i][j];
          }
        }
        for (int i : a) {
          for (int j : a) {
            aa += d[i][j] * d[i][j];
          }
        }
        for (int i : b) {
          for (int j : b) {
            bb += d[i][j] * d[i][j];
          }
        }
        int na = a.size();
        int nb = b.size();
        double increase = ab / (na * nb) - aa / (2.0 * na * na) - bb / (2.0 * nb * nb);
        return Math.sqrt(Math.max(0.0, 2.0 * na * nb / (na + nb) * increase));
      }
      default: {
        double min = Double.POSITIVE_INFINITY;
        for (int i : a) {
          for (int j : b) {
            min = Math.min(min, d[i][j]);
          }
        }
        return min;
      }
    }
  }

  // ---------------------------------------------------------------- ChartElementData

  /** The charts and the named chart elements Symja draws for them. */
  private static final String[][] CHART_ELEMENTS = {
      {"BarChart", "GlassRectangle", "Rectangle"}, {"BubbleChart", "Bubble", "NoiseBubble"},
      {"BubbleHistogram", "Bubble"}, {"DistributionChart", "Density", "HistogramDensity",
          "Quantile"},
      {"Histogram", "Rectangle"},
      {"PieChart3D", "CylindricalSector3D", "ProfileSector3D", "TorusSector3D"},
      {"SectorChart3D", "CylindricalSector3D", "ProfileSector3D", "TorusSector3D"}};

  /**
   * <code>ChartElementData(chart)</code>: the names of the <code>ChartElementFunction</code>
   * values Symja draws for <code>chart</code> (a name or a symbol); <code>ChartElementData()</code>
   * the names of the charts. Another name prints the <code>notent</code> message.
   */
  public static final class ChartElementData extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(IAST ast, EvalEngine engine) {
      if (ast.argSize() == 0) {
        TreeSet<String> charts = new TreeSet<>();
        for (String[] row : CHART_ELEMENTS) {
          charts.add(row[0]);
        }
        return strings(charts);
      }
      if (ast.argSize() != 1) {
        return F.NIL;
      }
      IExpr chart = ast.arg1();
      String name = chart.isString() ? chart.toString()
          : chart.isSymbol() ? ((ISymbol) chart).getSymbolName() : null;
      if (name == null) {
        return F.NIL;
      }
      for (String[] row : CHART_ELEMENTS) {
        if (row[0].equalsIgnoreCase(name)) {
          List<String> names = new ArrayList<>();
          for (int k = 1; k < row.length; k++) {
            names.add(row[k]);
          }
          return strings(names);
        }
      }
      // `1` is not a known entity, class or tag for `2`. Use `2`[] for a list of entities.
      return Errors.printMessage(S.ChartElementData, "notent", F.list(chart, S.ChartElementData),
          engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.addAttributes(ISymbol.READPROTECTED);
    }
  }

  private static IAST strings(java.util.Collection<String> names) {
    IASTAppendable list = F.ListAlloc(names.size());
    for (String name : names) {
      list.append(F.stringx(name));
    }
    return list;
  }
}
