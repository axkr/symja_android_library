package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;

/**
 * Functions for generating Box-and-Whisker Charts.
 * <p>
 * Every dataset is a box from its first to its third quartile, filled and without an edge, crossed
 * by a white median line, with whiskers to the extreme values ended by short fences. The result is
 * a framed <code>Graphics</code> with a tick under each box, labelled by <code>ChartLabels</code>.
 * The second argument chooses the marks: <code>"Basic"</code> (no fences), <code>"Mean"</code> (a
 * black mean line), <code>"Notched"</code> (a notch around the median) and <code>"Outliers"</code>
 * (whiskers stop at the last value within 1.5 interquartile ranges of the box, the values beyond
 * are inset markers, gray beyond 3 ranges). <code>BarOrigin -> Left | Right | Top</code> turns the
 * chart. Exact data gives exact quartiles, whiskers, outliers and mean in the coordinates.
 * <p>
 * Example: <code>BoxWhiskerChart[RandomReal[NormalDistribution[0, 1], {5, 20}], "Notched"]</code>
 */
public class BoxWhiskerChart extends AbstractChart {

  /**
   * The geometry of <code>n</code> boxes (or columns) centred on 1, 2, ..., n: the half width of a
   * box and how far the plot range reaches beyond the outer box edges.
   */
  static final class BoxSlots {
    final double half;
    final double outer;

    /**
     * @param spacing an explicit <code>BarSpacing</code>, as a fraction of the box width, or
     *        <code>NaN</code> for the automatic one, which narrows the gaps as the count grows
     * @param whiskerCaps whether the chart leaves the extra room a box and whisker chart has
     */
    BoxSlots(int n, double spacing, boolean whiskerCaps) {
      int count = Math.max(n, 1);
      double s = Double.isFinite(spacing) && spacing >= 0.0 ? spacing
          : 1.0 / (0.645 + 0.1453 * (count - 1));
      half = 0.5 / (1.0 + s);
      double gap = 0.5 - half;
      // a lone box gets a whole slot either side; more boxes settle towards half a gap
      double reach = gap + (1.0 - gap) * Math.exp(-1.829 * (count - 1));
      outer = whiskerCaps ? reach : reach - 0.3 * gap;
    }

    double low() {
      return 1.0 - half - outer;
    }

    double high(int n) {
      return n + half + outer;
    }
  }

  /**
   * Where the boxes grow from: a horizontal chart swaps the coordinates, and a chart growing from
   * the top or the right negates the values and labels the value axis with their magnitudes.
   */
  static final class Orientation {
    final boolean horizontal;
    final boolean mirrored;

    Orientation(IExpr barOrigin) {
      int origin = GraphicsOptions.barOrigin(barOrigin);
      horizontal = GraphicsOptions.barsAreHorizontal(origin);
      mirrored = GraphicsOptions.barsAreReversed(origin);
    }

    /** The plot coordinates <code>{x, y}</code> of category coordinate c and value v. */
    double[] coords(double c, double v) {
      double w = mirrored ? -v : v;
      return horizontal ? new double[] {w, c} : new double[] {c, w};
    }

    IAST point(double c, double v) {
      double[] xy = coords(c, v);
      return F.List(F.num(xy[0]), F.num(xy[1]));
    }

    /** The point at category coordinate c and the value v, kept exact if it is exact. */
    IAST point(double c, IExpr v) {
      IExpr w = mirrored ? v.negate() : v;
      return horizontal ? F.List(w, F.num(c)) : F.List(F.num(c), w);
    }

    /** The axis aligned rectangle with the opposite corners (c0, v0) and (c1, v1). */
    IAST rectangle(double c0, double v0, double c1, double v1) {
      double[] a = coords(c0, v0);
      double[] b = coords(c1, v1);
      return F.Rectangle(F.List(F.num(Math.min(a[0], b[0])), F.num(Math.min(a[1], b[1]))),
          F.List(F.num(Math.max(a[0], b[0])), F.num(Math.max(a[1], b[1]))));
    }

    /**
     * The <code>FrameTicks</code> of a chart of <code>n</code> categories: a tick at each category
     * on the edge facing the category axis, labelled by <code>labels</code>, and unlabelled ones
     * opposite; automatic value ticks, or for a mirrored chart ticks at the negated values over
     * <code>lo..hi</code> labelled with the values.
     */
    IAST frameTicks(int n, IExpr labels, double lo, double hi) {
      IASTAppendable labelled = F.ListAlloc(n);
      IASTAppendable plain = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        IExpr label = labels.isList() && i <= labels.argSize() ? labels.getAt(i) : F.CEmptyString;
        labelled.append(F.List(F.num(i), label, F.List(F.num(0.008), F.C0)));
        plain.append(F.List(F.num(i), F.CEmptyString, F.List(F.num(0.008), F.C0)));
      }
      IAST values = mirrored ? F.List(mirroredTicks(lo, hi, true), mirroredTicks(lo, hi, false))
          : F.List(S.Automatic, S.Automatic);
      IAST categories = F.List(labelled, plain);
      return horizontal ? F.List(categories, values) : F.List(values, categories);
    }

    private static IAST mirroredTicks(double lo, double hi, boolean labelled) {
      IASTAppendable ticks = F.ListAlloc();
      for (TickGenerator.Tick tick : TickGenerator.linear(lo, hi)) {
        double v = tick.value;
        IExpr label = !labelled ? F.CEmptyString
            : v == Math.rint(v) && Math.abs(v) < 1e15 ? F.ZZ((long) v) : F.num(v);
        ticks.append(F.List(F.num(v == 0.0 ? 0.0 : -v), label, F.List(F.num(0.00625), F.C0)));
      }
      return ticks;
    }

    /**
     * The invisible point pair that fixes the plot range: the category extent c0..c1 and the values
     * lo..hi widened by 5% each side.
     */
    IAST rangePoint(double c0, double c1, double lo, double hi) {
      double pad = hi > lo ? 0.05 * (hi - lo) : 0.0;
      return F.List(F.Opacity(F.C0),
          F.Point(F.List(point(c0, lo - pad), point(c1, hi + pad))));
    }
  }

  /** The marks a box and whisker specification asks for. */
  private static final class Marks {
    boolean basic;
    boolean mean;
    boolean notched;
    boolean outliers;
    IExpr whiskerStyle = F.NIL;
    IExpr fenceStyle = F.NIL;
    double fenceWidth = Double.NaN;

    void read(IExpr spec) {
      if (spec.isString()) {
        String name = spec.toString();
        basic |= name.equals("Basic");
        mean |= name.equals("Mean");
        notched |= name.equals("Notched");
        outliers |= name.equals("Outliers");
        return;
      }
      if (!spec.isList()) {
        return;
      }
      for (IExpr item : (IAST) spec) {
        if (item.isString()) {
          read(item);
        } else if (item.isList() && item.argSize() > 0 && item.first().isString()) {
          readElement((IAST) item);
        }
      }
    }

    /** One element <code>{"name", settings...}</code> of a list specification. */
    private void readElement(IAST element) {
      String name = element.first().toString();
      if (name.equals("Outliers") || name.equals("FarOutliers")) {
        outliers = true;
      } else if (name.equals("MeanMarker")) {
        mean = true;
      } else if (name.equals("Whiskers") && element.argSize() > 1) {
        whiskerStyle = directive(element, 2);
      } else if (name.equals("Fences")) {
        boolean sized = element.argSize() >= 2 && element.arg2().isReal();
        fenceWidth = sized ? element.arg2().evalfNaN() : 1.0;
        IAST directive = directive(element, sized ? 3 : 2);
        fenceStyle = directive.argSize() > 0 ? directive : F.GrayLevel(F.C0);
      }
    }

    /** <code>Directive[...]</code> of the settings of an element from position <code>first</code>. */
    private static IAST directive(IAST element, int first) {
      IASTAppendable directive = F.ast(S.Directive);
      for (int i = first; i < element.size(); i++) {
        directive.append(element.get(i));
      }
      return directive;
    }
  }

  /** The marker of an outlier, drawn as a character centred on its value. */
  private static final String OUTLIER_MARKER = "\u25CF";

  public BoxWhiskerChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IASTAppendable keyLabels = F.ListAlloc();
    IExpr dataArg = GraphicsOptions.chartDataMagnitudes(engine.evaluate(ast.arg1()), originalAST,
        keyLabels, engine);
    if (dataArg.isNIL()) {
      return F.NIL;
    }

    Marks marks = new Marks();
    for (int i = 2; i <= argSize; i++) {
      if (!ast.get(i).isRuleAST()) {
        marks.read(engine.evaluate(ast.get(i)));
      }
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions.chartLabels(
        GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None), keyLabels);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    Orientation orientation =
        new Orientation(GraphicsOptions.optionValue(originalAST, S.BarOrigin, S.Bottom));

    IExpr chartElementsOption =
        GraphicsOptions.optionValue(originalAST, S.ChartElements, S.Automatic);
    IExpr chartElements = chartElementsOption.isAutomatic() || chartElementsOption.isNone() ? F.NIL
        : chartElementsOption;

    IAST dataList = (IAST) dataArg;

    // a wrapper around the whole argument comes off before the single/multiple shape is read,
    // since Tooltip({1,2,3}, "a") is still one dataset
    final PlotWrapper outerWrapper = PlotWrapper.of(dataList);
    if (outerWrapper.datum.isList()) {
      dataList = (IAST) outerWrapper.datum;
    }
    boolean isMultiDataset = false;
    if (dataList.argSize() > 0 && PlotWrapper.strip(dataList.arg1()).isList()) {
      isMultiDataset = true;
    }
    IAST datasets = isMultiDataset ? dataList : F.List(dataList);
    int count = datasets.argSize();

    BoxSlots slots = new BoxSlots(count,
        barSpacing(GraphicsOptions.optionValue(originalAST, S.BarSpacing, S.Automatic)), true);
    double half = slots.half;
    double cap = half / 2.0;

    // A ColorFunction colours each box by the value it stands for, which for a box and whisker
    // chart is its median: the one number the box is drawn around. The scale runs over the medians
    // of all the datasets, so the colours say how they compare with one another.
    double medianMin = Double.MAX_VALUE;
    double medianMax = -Double.MAX_VALUE;
    for (int i = 1; i <= count; i++) {
      double m = medianOf(PlotWrapper.strip(datasets.get(i)));
      if (!Double.isNaN(m)) {
        medianMin = Math.min(medianMin, m);
        medianMax = Math.max(medianMax, m);
      }
    }
    PlotColorFunction boxColors = PlotColorFunction
        .of(PlotColorFunction.Family.CHART,
            GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic),
            GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True),
            S.BoxWhiskerChart, engine)
        .range(1, medianMin, medianMax).build();

    IASTAppendable boxes = F.ListAlloc(count);
    // the fill of each box, which a list of legend labels shows as its swatches
    // a legend entry names one dataset, in the fill its box was drawn with; a dataset without a
    // box keeps its style colour
    GraphicsOptions.LegendColors legendColors =
        new GraphicsOptions.LegendColors(i -> getChartStyle(chartStyle, i));
    double minVal = Double.MAX_VALUE;
    double maxVal = -Double.MAX_VALUE;
    for (int i = 1; i <= count; i++) {
      PlotWrapper datasetWrapper = PlotWrapper.of(datasets.get(i));
      IExpr datasetExpr = datasetWrapper.datum;
      if (!datasetExpr.isList()) {
        continue;
      }
      // exact data keeps exact quartiles, whiskers and mean; machine data stays machine data
      IRational[] exact = exactValues((IAST) datasetExpr);
      double[] sorted = exact != null ? doubles(exact) : values((IAST) datasetExpr);
      int n = sorted.length;
      if (n == 0) {
        continue;
      }
      Arrays.sort(sorted);
      minVal = Math.min(minVal, sorted[0]);
      maxVal = Math.max(maxVal, sorted[n - 1]);
      IExpr q1Value = quartile(sorted, exact, F.QQ(n + 2, 4));
      IExpr medianValue = quartile(sorted, exact, F.QQ(n + 1, 2));
      IExpr q3Value = quartile(sorted, exact, F.QQ(3 * n + 2, 4));
      double q1 = q1Value.evalf();
      double median = medianValue.evalf();
      double q3 = q3Value.evalf();
      double iqr = q3 - q1;

      // with outliers marked, the whiskers stop at the last value inside the 1.5 IQR fences
      IExpr whiskerLow = valueAt(sorted, exact, 0);
      IExpr whiskerHigh = valueAt(sorted, exact, n - 1);
      double low = sorted[0];
      double high = sorted[n - 1];
      if (marks.outliers) {
        whiskerLow = q1Value;
        whiskerHigh = q3Value;
        low = q1;
        high = q3;
        for (int k = 0; k < n; k++) {
          double v = sorted[k];
          if (v >= q1 - 1.5 * iqr && v <= q3 + 1.5 * iqr) {
            if (v < low) {
              low = v;
              whiskerLow = valueAt(sorted, exact, k);
            }
            if (v > high) {
              high = v;
              whiskerHigh = valueAt(sorted, exact, k);
            }
          }
        }
      }

      double c = i;
      double left = c - half;
      double right = c + half;
      IExpr functionColor = boxColors == null ? F.NIL : boxColors.color(median);
      IExpr color =
          functionColor.isPresent() ? functionColor : getChartStyle(chartStyle, i - 1);
      legendColors.record(i - 1, color);

      IASTAppendable group = F.ListAlloc();
      group.append(F.Directive(F.EdgeForm(F.None), color));
      if (marks.notched) {
        // the notch reaches median +- 1.7 * 1.25 IQR / (1.35 Sqrt(n)), clipped to the box
        double depth = 1.7 * 1.25 * iqr / (1.35 * Math.sqrt(n));
        double notchLow = Math.max(median - depth, q1);
        double notchHigh = Math.min(median + depth, q3);
        group.append(F.Polygon(F.List(orientation.point(left, q3Value),
            orientation.point(right, q3Value), orientation.point(right, notchHigh),
            orientation.point(c + cap, medianValue), orientation.point(right, notchLow),
            orientation.point(right, q1Value), orientation.point(left, q1Value),
            orientation.point(left, notchLow), orientation.point(c - cap, medianValue),
            orientation.point(left, notchHigh))));
      } else if (chartElements.isPresent()) {
        // ChartElements replaces the box with the caller's own drawing, stretched to fill it
        double[] a = orientation.coords(left, q1);
        double[] b = orientation.coords(right, q3);
        group.append(fittedElement(chartElements, Math.min(a[0], b[0]), Math.min(a[1], b[1]),
            Math.max(a[0], b[0]), Math.max(a[1], b[1])));
      } else {
        group.append(F.Polygon(F.List(orientation.point(left, q3Value),
            orientation.point(right, q3Value), orientation.point(right, q1Value),
            orientation.point(left, q1Value))));
      }

      if (marks.outliers) {
        // every outlier is a marker of its own; the far ones, beyond 3 IQR, are gray
        for (int far = 0; far <= 1; far++) {
          IASTAppendable markers = F.ListAlloc();
          IExpr level = far == 1 ? F.num(0.5) : F.C0;
          for (int k = 0; k < n; k++) {
            double v = sorted[k];
            boolean isFar = v < q1 - 3.0 * iqr || v > q3 + 3.0 * iqr;
            if ((v < low || v > high) && isFar == (far == 1)) {
              markers.append(F.binaryAST2(S.Inset,
                  F.ternaryAST3(S.Style, F.stringx(OUTLIER_MARKER), F.ZZ(10), F.GrayLevel(level)),
                  orientation.point(c, valueAt(sorted, exact, k))));
            }
          }
          if (markers.argSize() > 0) {
            group.append(markers);
          }
        }
      }

      // each line group starts from black, which its own style then refines
      double medianHalf = marks.notched ? cap : half;
      group.append(F.List(F.GrayLevel(F.C0), F.GrayLevel(F.C1),
          F.Line(F.List(orientation.point(c + medianHalf, medianValue),
              orientation.point(c - medianHalf, medianValue)))));
      if (marks.mean) {
        IExpr mean = mean(sorted, exact);
        group.append(F.List(F.GrayLevel(F.C0), F.GrayLevel(F.C0), F.Line(
            F.List(orientation.point(c + cap, mean), orientation.point(c - cap, mean)))));
      }
      group.append(F.List(F.GrayLevel(F.C0), F.unaryAST1(S.CapForm, F.stringx("Butt")),
          marks.whiskerStyle.isPresent() ? F.Directive(F.GrayLevel(F.C0), marks.whiskerStyle)
              : F.GrayLevel(F.num(0.5)),
          F.Line(F.List(F.List(orientation.point(c, q1Value), orientation.point(c, whiskerLow)),
              F.List(orientation.point(c, q3Value), orientation.point(c, whiskerHigh))))));
      if (!marks.basic || marks.fenceStyle.isPresent()) {
        double fenceHalf = marks.fenceStyle.isPresent() ? half * marks.fenceWidth : cap;
        group.append(F.List(F.GrayLevel(F.C0),
            marks.fenceStyle.isPresent() ? marks.fenceStyle : F.GrayLevel(F.num(0.5)),
            F.Line(F.List(
                F.List(orientation.point(c + fenceHalf, whiskerLow),
                    orientation.point(c - fenceHalf, whiskerLow)),
                F.List(orientation.point(c + fenceHalf, whiskerHigh),
                    orientation.point(c - fenceHalf, whiskerHigh))))));
      }

      // the label covers the whole box, whiskers and median line included, so it answers a hover
      // anywhere on the element it names; a self labelling wrapper names it by its median
      IExpr boxTooltip = outerWrapper.tooltipOf(datasetWrapper);
      boxes.append(boxTooltip.isPresent()
          ? F.binaryAST2(S.Tooltip, group,
              outerWrapper.tooltipLabelsItself || datasetWrapper.tooltipLabelsItself
                  ? F.num(median)
                  : boxTooltip)
          : group);
    }
    if (minVal > maxVal) {
      return F.NIL;
    }
    graphicsOptions.setChartLegend(chartLegends, legendColors);

    IAST primitives =
        F.List(orientation.rangePoint(slots.low(), slots.high(count), minVal, maxVal),
            F.List(F.unaryAST1(S.CapForm, S.None), boxes));
    if (GraphicsOptions.optionValue(originalAST, S.FrameTicks, null) == null) {
      double span = maxVal - minVal;
      graphicsOptions.addOption(F.Rule(S.FrameTicks, orientation.frameTicks(count, chartLabels,
          minVal - 0.11 * span, maxVal + 0.11 * span)));
    }
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** <code>BarSpacing -> s</code> or <code>{s, ...}</code>, <code>NaN</code> for anything else. */
  static double barSpacing(IExpr value) {
    IExpr s = value.isList() && value.argSize() > 0 ? value.first() : value;
    return s.isReal() ? s.evalfNaN() : Double.NaN;
  }

  /** The finite numbers of a dataset, unwrapped. */
  private static double[] values(IAST dataset) {
    double[] values = new double[dataset.argSize()];
    int n = 0;
    for (int k = 1; k <= dataset.argSize(); k++) {
      IExpr e = PlotWrapper.strip(dataset.get(k));
      double d = e instanceof INumber ? ((INumber) e).reDoubleValue() : e.evalfNaN();
      if (Double.isFinite(d)) {
        values[n++] = d;
      }
    }
    return Arrays.copyOf(values, n);
  }

  /**
   * The values of a dataset sorted, if they are all exact rational numbers, or <code>null</code>.
   */
  private static IRational[] exactValues(IAST dataset) {
    IRational[] values = new IRational[dataset.argSize()];
    for (int k = 1; k <= dataset.argSize(); k++) {
      IExpr e = PlotWrapper.strip(dataset.get(k));
      if (!e.isRational()) {
        return null;
      }
      values[k - 1] = (IRational) e;
    }
    Arrays.sort(values);
    return values;
  }

  private static double[] doubles(IRational[] values) {
    double[] result = new double[values.length];
    for (int k = 0; k < values.length; k++) {
      result[k] = values[k].doubleValue();
    }
    return result;
  }

  /** The sorted value at the 0-based index <code>k</code>, exact if the data is exact. */
  private static IExpr valueAt(double[] sorted, IRational[] exact, int k) {
    return exact != null ? exact[k] : F.num(sorted[k]);
  }

  /**
   * The value at the 1-based position <code>pos</code> of sorted data, interpolated linearly
   * between neighbours, exact if the data is exact.
   */
  private static IExpr quartile(double[] sorted, IRational[] exact, IRational pos) {
    if (exact == null) {
      return F.num(quartilePosition(sorted, pos.doubleValue()));
    }
    int n = exact.length;
    IRational index = pos.subtract(F.C1);
    if (index.isNegative()) {
      index = F.C0;
    } else if (index.isGT(F.ZZ(n - 1))) {
      index = F.ZZ(n - 1);
    }
    int lo = index.floor().toIntDefault();
    int hi = Math.min(lo + 1, n - 1);
    IRational fraction = index.subtract(F.ZZ(lo));
    return exact[lo].add(fraction.multiply(exact[hi].subtract(exact[lo])));
  }

  /** The mean of sorted data, exact if the data is exact. */
  private static IExpr mean(double[] sorted, IRational[] exact) {
    if (exact != null) {
      IRational sum = F.C0;
      for (IRational v : exact) {
        sum = sum.add(v);
      }
      return sum.divideBy(F.ZZ(exact.length));
    }
    double sum = 0.0;
    for (double v : sorted) {
      sum += v;
    }
    return F.num(sum / sorted.length);
  }

  /**
   * The value at the 1-based position <code>pos</code> of sorted data, interpolated linearly
   * between neighbours: the quartiles are at positions (n+2)/4, (n+1)/2 and (3n+2)/4.
   */
  private static double quartilePosition(double[] sorted, double pos) {
    int n = sorted.length;
    double index = Math.max(0.0, Math.min(n - 1.0, pos - 1.0));
    int lo = (int) Math.floor(index);
    int hi = Math.min((int) Math.ceil(index), n - 1);
    double fraction = index - lo;
    return sorted[lo] * (1.0 - fraction) + sorted[hi] * fraction;
  }


  /**
   * The caller's own drawing, stretched to fill the box.
   *
   * <p>
   * The drawing is measured and then mapped onto the rectangle, so a graphic drawn at any scale
   * lands in the right place. Only the primitives that carry an obvious extent are measured; a
   * drawing made of anything else is assumed to occupy the unit square.
   */
  private static IExpr fittedElement(IExpr element, double x0, double y0, double x1, double y1) {
    IExpr body =
        element.isAST(S.Graphics) && element.size() > 1 ? ((IAST) element).arg1() : element;
    double[] bounds = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
    accumulateBounds(body, bounds);
    if (bounds[0] > bounds[1] || bounds[2] > bounds[3]) {
      bounds = new double[] {0, 1, 0, 1};
    }
    double width = bounds[1] - bounds[0];
    double height = bounds[3] - bounds[2];
    if (width <= 0) {
      width = 1;
    }
    if (height <= 0) {
      height = 1;
    }
    double sx = (x1 - x0) / width;
    double sy = (y1 - y0) / height;
    IAST matrix = F.List(F.List(F.num(sx), F.C0), F.List(F.C0, F.num(sy)));
    IAST vector = F.List(F.num(x0 - bounds[0] * sx), F.num(y0 - bounds[2] * sy));
    return F.binaryAST2(S.GeometricTransformation, body, F.List(matrix, vector));
  }

  /** Grow {@code bounds}, as {xMin, xMax, yMin, yMax}, to hold a drawing. */
  private static void accumulateBounds(IExpr expr, double[] bounds) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (isPoint(list)) {
        addPoint(list, bounds);
        return;
      }
      for (int i = 1; i < list.size(); i++) {
        accumulateBounds(list.get(i), bounds);
      }
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Disk) || ast.isAST(S.Circle)) {
      double cx = 0;
      double cy = 0;
      if (ast.argSize() >= 1 && ast.arg1().isList() && ast.arg1().size() == 3) {
        cx = ((IAST) ast.arg1()).arg1().evalfNaN();
        cy = ((IAST) ast.arg1()).arg2().evalfNaN();
      }
      double r = 1;
      if (ast.argSize() >= 2 && ast.arg2().isNumber()) {
        r = ast.arg2().evalfNaN();
      }
      if (Double.isFinite(cx) && Double.isFinite(cy) && Double.isFinite(r)) {
        grow(bounds, cx - r, cy - r);
        grow(bounds, cx + r, cy + r);
      }
      return;
    }
    if (ast.isAST(S.Rectangle) || ast.isAST(S.Line) || ast.isAST(S.Polygon) || ast.isAST(S.Point)) {
      for (int i = 1; i < ast.size(); i++) {
        accumulateBounds(ast.get(i), bounds);
      }
      if (ast.isAST(S.Rectangle) && ast.argSize() == 1) {
        // Rectangle[{x, y}] is the unit square with that corner
        double[] corner =
            new double[] {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
        accumulateBounds(ast.arg1(), corner);
        if (corner[0] <= corner[1]) {
          grow(bounds, corner[0] + 1, corner[2] + 1);
        }
      }
    }
  }

  /** Whether a list is a single {x, y} point rather than a list of them. */
  private static boolean isPoint(IAST list) {
    return list.argSize() == 2 && list.arg1().isNumericFunction(true)
        && list.arg2().isNumericFunction(true);
  }

  private static void addPoint(IAST point, double[] bounds) {
    double x = point.arg1().evalfNaN();
    double y = point.arg2().evalfNaN();
    if (Double.isFinite(x) && Double.isFinite(y)) {
      grow(bounds, x, y);
    }
  }

  private static void grow(double[] bounds, double x, double y) {
    bounds[0] = Math.min(bounds[0], x);
    bounds[1] = Math.max(bounds[1], x);
    bounds[2] = Math.min(bounds[2], y);
    bounds[3] = Math.max(bounds[3], y);
  }

  /** The median of one dataset, or {@code NaN} when it holds no numbers. */
  private static double medianOf(IExpr datasetExpr) {
    if (!datasetExpr.isList()) {
      return Double.NaN;
    }
    double[] sorted = values((IAST) datasetExpr);
    if (sorted.length == 0) {
      return Double.NaN;
    }
    Arrays.sort(sorted);
    return quartilePosition(sorted, (sorted.length + 1.0) / 2.0);
  }

  /**
   * The fill of box <code>index</code>. Without a <code>ChartStyle</code> every box takes the first
   * chart colour, even when a legend names the datasets.
   */
  private IExpr getChartStyle(IExpr styleOption, int index) {
    if (styleOption.isAutomatic()) {
      return GraphicsOptions.chartStyleColorExpr(0);
    }
    if (styleOption.isList()) {
      return GraphicsOptions.getPlotStyle(styleOption, index);
    }
    return styleOption;
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
