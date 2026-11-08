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
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Charts of data built directly from graphics primitives, or from the plot functions:
 * <code>PairedBarChart</code>, <code>PairedHistogram</code>, <code>PairedSmoothHistogram</code>,
 * <code>Histogram3D</code>, <code>SmoothHistogram</code>, <code>SmoothHistogram3D</code> and
 * <code>ClockGauge</code>.
 */
public final class DataCharts {

  /** The half width of a bar of a paired bar chart. */
  private static final double PAIRED_BAR_HALF_WIDTH = 0.457;

  /** The central gap of a paired chart on each side, relative to the largest bar. */
  private static final double PAIRED_GAP = 0.075;

  /** The bandwidths a smooth histogram extends beyond the data. */
  private static final double SMOOTH_MARGIN = 3.35;

  /** The bandwidths a two-dimensional smooth histogram extends beyond the data. */
  private static final double SMOOTH_MARGIN_2D = 3.0;

  /** The samples per axis of a two-dimensional smooth histogram. */
  private static final int SMOOTH_SAMPLES = 40;

  private DataCharts() {}

  /** The option rules at the end of <code>ast</code>. */
  private static List<IExpr> options(IAST ast) {
    List<IExpr> options = new ArrayList<>();
    for (IExpr arg : ast) {
      if (arg.isRuleAST()) {
        options.add(arg);
      }
    }
    return options;
  }

  /** The number of arguments of <code>ast</code> before its option rules. */
  private static int positional(IAST ast) {
    int n = 0;
    while (n < ast.argSize() && !ast.get(n + 1).isRuleAST()) {
      n++;
    }
    return n;
  }

  private static IASTAppendable withOptions(IExpr head, IExpr first, IAST ast) {
    IASTAppendable result = F.ast(head);
    result.append(first);
    for (IExpr option : options(ast)) {
      result.append(option);
    }
    return result;
  }

  /** The Silverman bandwidth of the data, 1 if the data has no spread. */
  private static double silverman(double[] data) {
    double[] sorted = data.clone();
    java.util.Arrays.sort(sorted);
    final int n = sorted.length;
    double mean = 0.0;
    for (double x : sorted) {
      mean += x;
    }
    mean /= n;
    double sum = 0.0;
    for (double x : sorted) {
      sum += (x - mean) * (x - mean);
    }
    double sigma = Math.sqrt(sum / (n - 1));
    double iqr = sorted[Math.min(n - 1, (3 * n) / 4)] - sorted[n / 4];
    double spread = iqr > 0.0 ? Math.min(sigma, iqr / 1.34) : sigma;
    double h = 0.9 * spread * Math.pow(n, -0.2);
    return h > 0.0 && Double.isFinite(h) ? h : 1.0;
  }

  /** The numbers of a list of at least two numbers, or <code>null</code>. */
  private static double[] numbers(IExpr list) {
    if (!list.isList() || list.argSize() < 2) {
      return null;
    }
    double[] values = list.toDoubleVector();
    if (values == null) {
      return null;
    }
    for (double value : values) {
      if (!Double.isFinite(value)) {
        return null;
      }
    }
    return values;
  }

  /**
   * <code>PairedBarChart(left, right)</code> and <code>PairedHistogram(left, right, bspec)</code> -
   * two series drawn back to back as horizontal bars: the first extends left of a central gap, the
   * second right. A histogram bins both data sets with the bins of the joined data.
   */
  public static final class PairedChart extends AbstractFunctionEvaluator {
    private final boolean histogram;

    public PairedChart(boolean histogram) {
      this.histogram = histogram;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional < 2 || !ast.arg1().isList() || !ast.arg2().isList()) {
        return F.NIL;
      }
      List<double[]> bars = histogram ? histogramBars(ast, positional, engine) : barChartBars(ast);
      if (bars == null || bars.isEmpty()) {
        return F.NIL;
      }
      double largest = 0.0;
      double bottom = Double.POSITIVE_INFINITY;
      double top = Double.NEGATIVE_INFINITY;
      for (double[] bar : bars) {
        largest = Math.max(largest, Math.max(Math.abs(bar[2]), Math.abs(bar[3])));
        bottom = Math.min(bottom, bar[0]);
        top = Math.max(top, bar[1]);
      }
      // a histogram labels its bins between the halves, so its gap is sized for those labels
      PairedAxis axis =
          histogram ? PairedAxis.of(bottom, top, 0.05) : PairedAxis.barEnds(bottom, top);
      double gap = histogram ? axis.gap(largest)
          : PAIRED_GAP * Math.max(largest, Double.MIN_VALUE);
      IASTAppendable left = F.ListAlloc(bars.size() + 1);
      IASTAppendable right = F.ListAlloc(bars.size() + 1);
      left.append(RectangleChart.color(S.Automatic, 0));
      right.append(RectangleChart.color(S.Automatic, 1));
      for (double[] bar : bars) {
        if (bar[2] != 0.0) {
          left.append(pairedBar(histogram, -gap - bar[2], bar[0], -gap, bar[1]));
        }
        if (bar[3] != 0.0) {
          right.append(histogram ? pairedBar(true, gap + bar[3], bar[0], gap, bar[1])
              : pairedBar(false, gap, bar[0], gap + bar[3], bar[1]));
        }
      }
      double extent = 1.05 * (largest + gap);
      return pairedGraphics(F.List(F.EdgeForm(F.GrayLevel(F.num(0.5))), left, right,
          axis.primitives(gap, extent, histogram ? PairedAxis.HISTOGRAM : PairedAxis.BAR)), ast);
    }

    /**
     * A bar between two corners; a histogram writes its corners as <code>Offset</code>s of no
     * displacement.
     */
    private static IAST pairedBar(boolean histogram, double x0, double y0, double x1, double y1) {
      IAST p0 = F.List(F.num(x0), F.num(y0));
      IAST p1 = F.List(F.num(x1), F.num(y1));
      if (histogram) {
        IAST none = F.List(F.C0, F.C0);
        return F.Rectangle(F.binaryAST2(S.Offset, none, p0), F.binaryAST2(S.Offset, none, p1));
      }
      return F.Rectangle(p0, p1);
    }

    /** The bars <code>{bottom, top, left length, right length}</code> of a bar chart. */
    private static List<double[]> barChartBars(IAST ast) {
      double[] left = ast.arg1().toDoubleVector();
      double[] right = ast.arg2().toDoubleVector();
      if (left == null || right == null) {
        return null;
      }
      List<double[]> bars = new ArrayList<>();
      for (int k = 0; k < Math.max(left.length, right.length); k++) {
        double y = k + 1.0;
        bars.add(new double[] {y - PAIRED_BAR_HALF_WIDTH, y + PAIRED_BAR_HALF_WIDTH,
            k < left.length ? left[k] : 0.0, k < right.length ? right[k] : 0.0});
      }
      return bars;
    }

    /** The bars with the bins of the joined data. */
    private static List<double[]> histogramBars(IAST ast, int positional, EvalEngine engine) {
      IExpr joined = F.Join(ast.arg1(), ast.arg2());
      IExpr list = engine.evaluate(
          positional >= 3 ? F.binaryAST2(S.HistogramList, joined, ast.arg3())
              : F.unaryAST1(S.HistogramList, joined));
      if (!list.isList2() || !list.first().isList()) {
        return null;
      }
      IExpr edgeList = list.first();
      double[] edges = edgeList.toDoubleVector();
      IExpr left = engine.evaluate(F.binaryAST2(S.BinCounts, ast.arg1(), F.List(edgeList)));
      IExpr right = engine.evaluate(F.binaryAST2(S.BinCounts, ast.arg2(), F.List(edgeList)));
      double[] leftCounts = left.isList() ? left.toDoubleVector() : null;
      double[] rightCounts = right.isList() ? right.toDoubleVector() : null;
      if (edges == null || leftCounts == null || rightCounts == null
          || leftCounts.length != edges.length - 1 || rightCounts.length != edges.length - 1) {
        return null;
      }
      List<double[]> bars = new ArrayList<>();
      for (int k = 0; k < leftCounts.length; k++) {
        bars.add(new double[] {edges[k], edges[k + 1], leftCounts[k], rightCounts[k]});
      }
      return bars;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>Graphics(primitives, opts)</code> of a paired chart: the caller's options, then the golden
   * aspect ratio, the whole plot range and a padding of 2% around everything drawn.
   */
  private static IAST pairedGraphics(IAST primitives, IAST ast) {
    IASTAppendable graphics = withOptions(S.Graphics, primitives, ast);
    IExpr[][] defaults = {{S.AspectRatio, F.Power(S.GoldenRatio, F.CN1)},
        {S.PlotRange, S.All}, {S.PlotRangePadding, F.unaryAST1(S.Scaled, F.num(0.02))}};
    for (IExpr[] option : defaults) {
      boolean given = false;
      for (IExpr rule : options(ast)) {
        given |= rule.first() == option[0];
      }
      if (!given) {
        graphics.append(F.Rule(option[0], option[1]));
      }
    }
    return graphics;
  }

  /**
   * The axes of a paired chart: a value axis running up each side of the central gap and a
   * magnitude axis under each half, all in a thin dark gray.
   */
  private static final class PairedAxis {
    /** Value ticks point into the gap, whose middle holds their labels. */
    static final int HISTOGRAM = 0;
    /** Value ticks point away from the gap and are labelled just right of its left edge. */
    static final int SMOOTH = 1;
    /** The value axes are unlabelled and marked only at the ends of the bars. */
    static final int BAR = 2;

    final double lo;
    final double hi;
    final double[] majors;
    final String[] labels;
    final double[] minors;

    private PairedAxis(double lo, double hi, double[] majors, String[] labels, double[] minors) {
      this.lo = lo;
      this.hi = hi;
      this.majors = majors;
      this.labels = labels;
      this.minors = minors;
    }

    /** The value axis over lo..hi widened by <code>pad</code> of the span at both ends. */
    static PairedAxis of(double lo, double hi, double pad) {
      if (!(Double.isFinite(lo) && Double.isFinite(hi) && hi > lo)) {
        lo = 0.0;
        hi = 1.0;
      }
      double margin = pad * (hi - lo);
      lo -= margin;
      hi += margin;
      double step = valueStep(hi - lo);
      List<double[]> ticks = ticks(lo, hi, step);
      String[] labels = new String[ticks.get(0).length];
      for (int i = 0; i < labels.length; i++) {
        labels[i] = org.matheclipse.core.graphics.svg.TickGenerator.format(ticks.get(0)[i], step);
      }
      return new PairedAxis(lo, hi, ticks.get(0), labels, ticks.get(1));
    }

    /**
     * The value axis of a bar chart: from 4% of the bars' span below the lowest bar, where the
     * magnitude axes run, to the top of the highest, marked at the two ends of the bars.
     */
    static PairedAxis barEnds(double bottom, double top) {
      if (!(Double.isFinite(bottom) && top > bottom)) {
        bottom = 0.0;
        top = 1.0;
      }
      return new PairedAxis(bottom - 0.04 * (top - bottom), top, new double[] {bottom, top},
          new String[] {"", ""}, new double[0]);
    }

    /**
     * Half the gap between the halves for a largest bar <code>largest</code>: the gap takes
     * <code>(19 + 6 d + 3 s) / 360</code> of the half width, <code>d</code> and <code>s</code>
     * being the digits and other characters of the widest value label.
     */
    double gap(double largest) {
      double units = 25.0;
      for (String label : labels) {
        int digits = 0;
        for (int i = 0; i < label.length(); i++) {
          if (Character.isDigit(label.charAt(i))) {
            digits++;
          }
        }
        int marks = label.length() - digits;
        units = Math.max(units, 19.0 + 6.0 * digits - 0.5 * Math.max(digits - 2, 0) + 3.0 * marks);
      }
      double r = units / 360.0;
      return Math.max(largest, Double.MIN_VALUE) * r / (1.0 - r);
    }

    /**
     * The axes of a chart whose halves start <code>gap</code> from the centre and whose magnitude
     * axes end <code>extent</code> from it. Tick marks are given in printer's points at the default
     * image size.
     */
    IAST primitives(double gap, double extent, int look) {
      // data units per printer's point across the padded plot range
      double dx = 2.0 * extent * 1.04 / 360.0;
      double dy = (hi - lo) * 1.04 / (360.0 * 2.0 / (1.0 + Math.sqrt(5.0)));
      IASTAppendable prims = F.ListAlloc();
      prims.append(F.GrayLevel(0.4));
      prims.append(F.AbsoluteThickness(F.num(0.2)));
      double span = extent - gap;
      double step = look == BAR ? barMagnitudeStep(extent) : magnitudeStep(span);
      double inward = look == SMOOTH ? -1.0 : 1.0;
      double majorLength = look == BAR ? 3.0 : 4.0;
      double labelDrop = look == SMOOTH ? -3.0 : -7.0;
      List<double[]> counts = ticks(0.0, span, step);
      for (double side = -1.0; side <= 1.0; side += 2.0) {
        prims.append(line(side * gap, lo, side * extent, lo));
        prims.append(line(side * gap, lo, side * gap, hi));
        for (double v : counts.get(0)) {
          double x = side * (gap + v);
          prims.append(line(x, lo, x, lo - 4.0 * dy));
          prims.append(label(number(v), offset(0.0, labelDrop, x, lo), 0.0, 1.0));
        }
        for (double v : counts.get(1)) {
          double x = side * (gap + v);
          prims.append(line(x, lo, x, lo - 2.5 * dy));
        }
        for (double y : majors) {
          prims.append(line(side * gap, y, side * (gap - inward * majorLength * dx), y));
        }
        for (double y : minors) {
          prims.append(line(side * gap, y, side * (gap - inward * 2.5 * dx), y));
        }
      }
      if (look != BAR) {
        for (double y : majors) {
          prims.append(look == HISTOGRAM
              ? label(F.unaryAST1(S.Pane, number(y)), F.List(F.CD0, F.num(y)), 0.0, 0.0)
              : label(number(y), offset(3.0, 0.0, -0.8 * gap, y), -1.0, 0.0));
        }
      }
      return prims;
    }

    private static IAST line(double x0, double y0, double x1, double y1) {
      return F.Line(F.List(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));
    }

    /** The point (x, y) moved by (dx, dy) printer's points. */
    private static IAST offset(double dx, double dy, double x, double y) {
      return F.binaryAST2(S.Offset, F.List(F.num(dx), F.num(dy)),
          F.List(F.num(x), F.num(y)));
    }

    private static IAST label(IExpr content, IAST position, double ax, double ay) {
      return F.Text(F.ternaryAST3(S.Style, content, F.ZZ(10), F.GrayLevel(0.25)), position,
          F.List(F.num(ax), F.num(ay)));
    }

    /** An integer as an integer, anything else as a machine number. */
    private static IExpr number(double v) {
      return Math.abs(v - Math.rint(v)) < 1e-9 && Math.abs(v) < 1e15 ? F.ZZ((long) Math.rint(v))
          : F.num(v);
    }

    /**
     * The major ticks <code>step</code> apart in lo..hi, and the minor ticks between them: five
     * to a major interval, or four when the step is a 2 times a power of ten.
     */
    private static List<double[]> ticks(double lo, double hi, double step) {
      List<double[]> result = new ArrayList<>();
      result.add(multiples(lo, hi, step, Double.NaN));
      double mantissa = step / Math.pow(10.0, Math.floor(Math.log10(Math.abs(step))));
      double minor = step / (Math.abs(mantissa - 2.0) < 1e-6 ? 4 : 5);
      result.add(multiples(lo, hi, minor, step));
      return result;
    }

    /** The multiples of <code>step</code> in lo..hi, without those of <code>skip</code>. */
    private static double[] multiples(double lo, double hi, double step, double skip) {
      long first = (long) Math.ceil(lo / step - 1e-9);
      long last = (long) Math.floor(hi / step + 1e-9);
      if (!(step > 0.0) || last - first > 1000) {
        return new double[0];
      }
      List<Double> values = new ArrayList<>();
      for (long k = first; k <= last; k++) {
        double v = k == 0 ? 0.0 : k * step;
        if (Double.isNaN(skip) || Math.abs(Math.rint(v / skip) * skip - v) >= skip * 1e-9) {
          values.add(v);
        }
      }
      double[] result = new double[values.size()];
      for (int i = 0; i < result.length; i++) {
        result[i] = values.get(i);
      }
      return result;
    }

    /** The smallest 1, 2 or 5 times a power of ten that cuts <code>span</code> into fewer than 6. */
    private static double valueStep(double span) {
      if (!(span > 0.0 && Double.isFinite(span))) {
        return 1.0;
      }
      double mag = Math.pow(10.0, Math.floor(Math.log10(span / 6.0)));
      for (int i = 0; i < 40; i++, mag *= 10.0) {
        for (double m : new double[] {1.0, 2.0, 5.0}) {
          if (span / (m * mag) < 6.0 * (1.0 - 1e-12)) {
            return m * mag;
          }
        }
      }
      return span;
    }

    /** The largest 1, 2, 2.5 or 5 times a power of ten that fits into <code>span</code> twice. */
    private static double magnitudeStep(double span) {
      if (!(span > 0.0 && Double.isFinite(span))) {
        return 1.0;
      }
      double mag = Math.pow(10.0, Math.floor(Math.log10(span)));
      for (int i = 0; i < 40; i++, mag /= 10.0) {
        for (double m : new double[] {5.0, 2.5, 2.0, 1.0}) {
          if (span / (m * mag) >= 2.0 * (1.0 - 1e-12)) {
            return m * mag;
          }
        }
      }
      return span;
    }

    /**
     * The 1, 2, 2.5 or 5 times a power of ten that cuts the magnitude axes of a bar chart, reaching
     * <code>extent</code> from the centre, into closest to three intervals.
     */
    private static double barMagnitudeStep(double extent) {
      if (!(extent > 0.0 && Double.isFinite(extent))) {
        return 1.0;
      }
      double base = Math.pow(10.0, Math.floor(Math.log10(extent)) - 1.0);
      double best = 1.0;
      double bestOff = Double.POSITIVE_INFINITY;
      for (double scale : new double[] {1.0, 10.0, 100.0}) {
        for (double m : new double[] {1.0, 2.0, 2.5, 5.0}) {
          double s = m * scale * base;
          double off = Math.abs(extent / s - 3.0);
          if (off < bestOff) {
            bestOff = off;
            best = s;
          }
        }
      }
      return best;
    }
  }

  /**
   * <code>PairedSmoothHistogram(left, right)</code> and
   * <code>PairedSmoothHistogram(left, right, bw)</code> - the Gaussian kernel density estimates of
   * two data sets drawn back to back along a vertical value axis, the first to the left. Each curve
   * spans the range a smooth histogram draws its data over, and the halves are a tenth of the
   * largest density apart.
   */
  public static final class PairedSmoothHistogram extends AbstractFunctionEvaluator {
    private static final int SAMPLES = 400;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional < 2 || positional > 3) {
        return F.NIL;
      }
      double fixed = Double.NaN;
      if (positional == 3 && !ast.arg3().isAutomatic()) {
        fixed = ast.arg3().evalfNaN();
        if (!(fixed > 0.0)) {
          return F.NIL;
        }
      }
      double[][] halves = new double[2][];
      double[][] samples = new double[2][];
      double lo = Double.POSITIVE_INFINITY;
      double hi = Double.NEGATIVE_INFINITY;
      double largest = Double.MIN_VALUE;
      for (int side = 0; side < 2; side++) {
        double[] values = numbers(ast.get(side + 1));
        if (values == null) {
          return F.NIL;
        }
        double h = Double.isNaN(fixed) ? silverman(values) : fixed;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double v : values) {
          min = Math.min(min, v);
          max = Math.max(max, v);
        }
        double a = min - SMOOTH_MARGIN * h;
        double b = max + SMOOTH_MARGIN * h;
        lo = Math.min(lo, a);
        hi = Math.max(hi, b);
        // the ends of each curve are on the axis
        halves[side] = new double[SAMPLES + 1];
        samples[side] = new double[SAMPLES + 1];
        double norm = 1.0 / (values.length * h * Math.sqrt(2.0 * Math.PI));
        for (int k = 0; k <= SAMPLES; k++) {
          engine.checkTimeLimit();
          double t = a + (b - a) * k / SAMPLES;
          double density = 0.0;
          if (k > 0 && k < SAMPLES) {
            for (double v : values) {
              double u = (t - v) / h;
              density += Math.exp(-0.5 * u * u);
            }
            density *= norm;
          }
          halves[side][k] = t;
          samples[side][k] = density;
          largest = Math.max(largest, density);
        }
      }
      double gap = 0.05 * largest;
      IASTAppendable curves = F.ListAlloc(4);
      // the first colour of the plot cycle
      curves.append(F.RGBColor(0.24, 0.6, 0.8));
      curves.append(F.AbsoluteThickness(F.C2));
      for (int side = 0; side < 2; side++) {
        double sign = side == 0 ? -1.0 : 1.0;
        IASTAppendable points = F.ListAlloc(SAMPLES + 1);
        for (int k = 0; k <= SAMPLES; k++) {
          points.append(
              F.List(F.num(sign * (gap + samples[side][k])), F.num(halves[side][k])));
        }
        curves.append(F.Line(points));
      }
      return pairedGraphics(F.List(curves,
          PairedAxis.of(lo, hi, 0.0).primitives(gap, gap + largest, PairedAxis.SMOOTH)), ast);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /** The bin edges of one coordinate of the data of a <code>Histogram3D</code>. */
  private static double[] binEdges(IExpr values, IAST ast, int positional, EvalEngine engine) {
    IExpr list = engine.evaluate(
        positional >= 2 ? F.binaryAST2(S.HistogramList, values, ast.arg2())
            : F.unaryAST1(S.HistogramList, values));
    if (!list.isList2() || !list.first().isList() || list.first().argSize() < 2) {
      return null;
    }
    return list.first().toDoubleVector();
  }

  /** The index of the bin of <code>value</code>, or <code>-1</code>. */
  private static int binOf(double[] edges, double value) {
    int n = edges.length - 1;
    for (int i = 0; i < n; i++) {
      if (value >= edges[i] && (i == n - 1 ? value <= edges[i + 1] : value < edges[i + 1])) {
        return i;
      }
    }
    return -1;
  }

  /** The first and the second coordinates of a list of pairs of numbers, or <code>null</code>. */
  private static double[][] pairs(IExpr data) {
    if (!data.isList() || data.argSize() == 0) {
      return null;
    }
    IAST points = (IAST) data;
    double[][] xy = new double[2][points.argSize()];
    for (int i = 0; i < points.argSize(); i++) {
      IExpr point = points.get(i + 1);
      xy[0][i] = point.isList2() ? point.first().evalfNaN() : Double.NaN;
      xy[1][i] = point.isList2() ? point.second().evalfNaN() : Double.NaN;
      if (!Double.isFinite(xy[0][i]) || !Double.isFinite(xy[1][i])) {
        return null;
      }
    }
    return xy;
  }

  /**
   * <code>Histogram3D({{x1, y1}, {x2, y2}, ...})</code> and <code>Histogram3D(data, bspec)</code> -
   * one cuboid for each non-empty bin of the points, as high as the bin has points.
   */
  public static final class Histogram3D extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      double[][] xy = pairs(ast.arg1());
      if (xy == null || positional > 2) {
        return F.NIL;
      }
      double[] xEdges = binEdges(F.List(xy[0]), ast, positional, engine);
      double[] yEdges = binEdges(F.List(xy[1]), ast, positional, engine);
      if (xEdges == null || yEdges == null) {
        return F.NIL;
      }
      int[][] counts = new int[xEdges.length - 1][yEdges.length - 1];
      for (int k = 0; k < xy[0].length; k++) {
        int i = binOf(xEdges, xy[0][k]);
        int j = binOf(yEdges, xy[1][k]);
        if (i >= 0 && j >= 0) {
          counts[i][j]++;
        }
      }
      IASTAppendable cuboids = F.ListAlloc();
      cuboids.append(RectangleChart.color(S.Automatic, 0));
      for (int i = 0; i < counts.length; i++) {
        for (int j = 0; j < counts[i].length; j++) {
          if (counts[i][j] > 0) {
            cuboids.append(F.Cuboid(F.List(F.num(xEdges[i]), F.num(yEdges[j]), F.CD0),
                F.List(F.num(xEdges[i + 1]), F.num(yEdges[j + 1]), F.num(counts[i][j]))));
          }
        }
      }
      return withOptions(S.Graphics3D, cuboids, ast);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>SmoothHistogram(data)</code> and <code>SmoothHistogram({data1, ...}, bw, dfun)</code> - the
   * curves of the <code>SmoothKernelDistribution</code> of each data set: its <code>"PDF"</code>,
   * <code>"CDF"</code>, <code>"SF"</code> or <code>"HF"</code>, over the data widened by a few
   * bandwidths.
   */
  public static final class SmoothHistogram extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional > 3 || !ast.arg1().isList()) {
        return F.NIL;
      }
      IAST data = (IAST) ast.arg1();
      IAST datasets = data.forAll(x -> x.isList()) ? data : F.List(data);
      if (datasets.isEmpty()) {
        return F.NIL;
      }
      IExpr bandwidth = positional >= 2 ? ast.arg2() : S.Automatic;
      double fixed = bandwidth.isAutomatic() ? Double.NaN : bandwidth.evalfNaN();
      if (!bandwidth.isAutomatic() && !(fixed > 0.0)) {
        return F.NIL;
      }
      IExpr distributionFunction = positional >= 3 ? distributionFunction(ast.arg3()) : S.PDF;
      if (distributionFunction.isNIL()) {
        return F.NIL;
      }
      ISymbol x = F.Dummy("$x");
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      IASTAppendable curves = F.ListAlloc(datasets.argSize());
      for (IExpr dataset : datasets) {
        double[] values = numbers(dataset);
        if (values == null) {
          return F.NIL;
        }
        double h = bandwidth.isAutomatic() ? silverman(values) : fixed;
        for (double value : values) {
          low = Math.min(low, value - SMOOTH_MARGIN * h);
          high = Math.max(high, value + SMOOTH_MARGIN * h);
        }
        IExpr distribution = F.binaryAST2(S.SmoothKernelDistribution, dataset, F.num(h));
        curves.append(F.binaryAST2(distributionFunction, distribution, x));
      }
      IExpr body = curves.argSize() == 1 ? curves.arg1() : curves;
      IASTAppendable plot = F.ast(S.Plot);
      plot.append(body);
      plot.append(F.List(x, F.num(low), F.num(high)));
      boolean plotRange = false;
      for (IExpr option : options(ast)) {
        plot.append(option);
        plotRange |= option.first() == S.PlotRange;
      }
      if (!plotRange) {
        plot.append(F.Rule(S.PlotRange, S.All));
      }
      return plot;
    }

    private static IExpr distributionFunction(IExpr name) {
      if (name.isString("PDF")) {
        return S.PDF;
      }
      if (name.isString("CDF")) {
        return S.CDF;
      }
      if (name.isString("SF")) {
        return S.SurvivalFunction;
      }
      return name.isString("HF") ? S.HazardFunction : F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>SmoothHistogram3D({{x1, y1}, ...}, bspec)</code> - the Gaussian kernel density estimate of
   * points in the plane as a surface, with the bandwidth <code>bspec</code> or <code>{hx, hy}</code>
   * and by default the Silverman rule in each coordinate.
   */
  public static final class SmoothHistogram3D extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      double[][] xy = pairs(ast.arg1());
      if (xy == null || xy[0].length < 2 || positional > 3
          || (positional == 3 && !ast.arg3().isString("PDF"))) {
        return F.NIL;
      }
      double hx = silverman(xy[0]);
      double hy = silverman(xy[1]);
      if (positional >= 2 && !ast.arg2().isAutomatic() && !ast.arg2().isString("Silverman")) {
        IExpr bandwidth = ast.arg2();
        hx = bandwidth.isList2() ? bandwidth.first().evalfNaN() : bandwidth.evalfNaN();
        hy = bandwidth.isList2() ? bandwidth.second().evalfNaN() : hx;
        if (!(hx > 0.0) || !(hy > 0.0)) {
          return F.NIL;
        }
      }
      double[] xRange = range(xy[0], hx);
      double[] yRange = range(xy[1], hy);
      final int n = xy[0].length;
      double norm = 1.0 / (n * 2.0 * Math.PI * hx * hy);
      IASTAppendable rows = F.ListAlloc(SMOOTH_SAMPLES);
      for (int i = 0; i < SMOOTH_SAMPLES; i++) {
        engine.checkTimeLimit();
        double y = yRange[0] + (yRange[1] - yRange[0]) * i / (SMOOTH_SAMPLES - 1);
        double[] row = new double[SMOOTH_SAMPLES];
        for (int j = 0; j < SMOOTH_SAMPLES; j++) {
          double x = xRange[0] + (xRange[1] - xRange[0]) * j / (SMOOTH_SAMPLES - 1);
          double density = 0.0;
          for (int k = 0; k < n; k++) {
            double u = (x - xy[0][k]) / hx;
            double v = (y - xy[1][k]) / hy;
            density += Math.exp(-0.5 * (u * u + v * v));
          }
          row[j] = density * norm;
        }
        rows.append(F.List(row));
      }
      IASTAppendable plot = withOptions(S.ListPlot3D, rows, ast);
      plot.append(F.Rule(S.DataRange, F.List(F.List(F.num(xRange[0]), F.num(xRange[1])),
          F.List(F.num(yRange[0]), F.num(yRange[1])))));
      return plot;
    }

    private static double[] range(double[] values, double h) {
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (double value : values) {
        low = Math.min(low, value - SMOOTH_MARGIN_2D * h);
        high = Math.max(high, value + SMOOTH_MARGIN_2D * h);
      }
      return new double[] {low, high};
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>ClockGauge()</code>, <code>ClockGauge({h, m, s})</code>,
   * <code>ClockGauge(DateObject(...))</code> and <code>ClockGauge(seconds)</code> - an analog clock
   * face with the dial, a tick and a number for each hour and the hour, minute and second hands of
   * that time of day.
   */
  public static final class ClockGauge extends AbstractFunctionEvaluator {

    private static final int HOURS = 12;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double[] time = time(ast, engine);
      if (time == null) {
        return F.NIL;
      }
      IASTAppendable face = F.ListAlloc();
      face.append(F.Circle(F.List(F.CD0, F.CD0), F.CD1));
      for (int k = 0; k < HOURS; k++) {
        double turns = k / (double) HOURS;
        face.append(F.Line(F.List(at(turns, 0.88), at(turns, 0.98))));
        face.append(F.Text(F.ZZ(k == 0 ? HOURS : k), at(turns, 0.75)));
      }
      double seconds = time[2] + 60.0 * time[1] + 3600.0 * time[0];
      face.append(hand(seconds / 43200.0, 0.5, 4.0));
      face.append(hand(seconds / 3600.0, 0.8, 3.0));
      face.append(F.List(F.RGBColor(F.C1, F.C0, F.C0), hand(seconds / 60.0, 0.85, 1.0)));
      IASTAppendable graphics = F.ast(S.Graphics);
      graphics.append(face);
      for (IExpr option : options(ast)) {
        // the options of the gauge scale don't apply to the graphics
        if (option.first() != S.ScaleRanges) {
          graphics.append(option);
        }
      }
      return graphics;
    }

    /** <code>{hours, minutes, seconds}</code> of the arguments, or <code>null</code>. */
    private static double[] time(IAST ast, EvalEngine engine) {
      int positional = positional(ast);
      if (positional > 1) {
        return null;
      }
      if (positional == 0) {
        return dateTime(engine.evaluate(F.ast(S.DateList)));
      }
      IExpr arg = ast.arg1();
      if (arg.isList() && arg.argSize() >= 1 && arg.argSize() <= 3) {
        double[] time = new double[3];
        for (int i = 0; i < arg.argSize(); i++) {
          time[i] = arg.getAt(i + 1).evalfNaN();
          if (!Double.isFinite(time[i])) {
            return null;
          }
        }
        return time;
      }
      if (arg.isAST(S.DateObject)) {
        return dateTime(engine.evaluate(F.unaryAST1(S.DateList, arg)));
      }
      double seconds = arg.evalfNaN();
      if (!Double.isFinite(seconds)) {
        return null;
      }
      seconds = seconds - 86400.0 * Math.floor(seconds / 86400.0);
      return new double[] {Math.floor(seconds / 3600.0), Math.floor((seconds % 3600.0) / 60.0),
          seconds % 60.0};
    }

    private static double[] dateTime(IExpr dateList) {
      if (!dateList.isList() || dateList.argSize() < 6) {
        return null;
      }
      return new double[] {dateList.getAt(4).evalfNaN(), dateList.getAt(5).evalfNaN(),
          dateList.getAt(6).evalfNaN()};
    }

    /** The point at <code>radius</code> after <code>turns</code> clockwise from 12 o'clock. */
    private static IAST at(double turns, double radius) {
      double angle = Math.PI / 2.0 - turns * 2.0 * Math.PI;
      return F.List(F.num(radius * Math.cos(angle)), F.num(radius * Math.sin(angle)));
    }

    private static IAST hand(double turns, double radius, double thickness) {
      return F.List(F.AbsoluteThickness(F.num(thickness)),
          F.Line(F.List(F.List(F.CD0, F.CD0), at(turns - Math.floor(turns), radius))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }
}
