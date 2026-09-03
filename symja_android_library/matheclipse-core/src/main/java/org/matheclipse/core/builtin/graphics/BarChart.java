package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Bar Charts.
 * <p>
 * Example: <code>BarChart[{1, 2, 3, 5}]</code>
 * <code>BarChart[{1, 2, 3}, ChartStyle -> {Red, Green, Blue}]</code>
 * <code>BarChart[{{1, 2}, {3, 4}}, ChartLayout -> "Stacked"]</code>
 */
public class BarChart extends ListPlot {

  /** Extra room left between the groups of a multi-dataset chart, in category units. */
  private static final double GROUP_GAP = 0.5;

  public BarChart() {}

  /** One bar, once the layout has decided where it goes. */
  private static final class Bar {
    /** Start of the bar along the category axis. */
    final double from;
    /** End of the bar along the category axis. */
    final double to;
    /** Where the bar starts along the value axis; non-zero for the segments of a stacked bar. */
    final double base;
    /** Where the bar reaches along the value axis. */
    final double value;
    /** Which of the chart colours this bar takes. */
    final int colorIndex;
    /** The style from a {@code Style} wrapper on the datum, or {@link F#NIL}. */
    final IExpr style;
    /** The label from a {@code Labeled} wrapper on the datum, or {@link F#NIL}. */
    final IExpr label;
    /** The number the bar stands for, before any normalisation the layout applied. */
    final IExpr datum;

    Bar(double from, double to, double base, double value, int colorIndex, IExpr style, IExpr label,
        IExpr datum) {
      this.from = from;
      this.to = to;
      this.base = base;
      this.value = value;
      this.colorIndex = colorIndex;
      this.style = style;
      this.label = label;
      this.datum = datum;
    }

    double center() {
      return (from + to) / 2.0;
    }
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    // a Dataset plots its rows and an Association its values, labelled by its keys
    IASTAppendable keyLabels = F.ListAlloc();
    IExpr dataArg = GraphicsOptions.chartData(engine.evaluate(ast.arg1()), keyLabels);
    if (!dataArg.isList()) {
      return F.NIL;
    }
    dataArg = QuantityFunctions.quantityPlotMagnitudes(dataArg,
        GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic), engine);

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr barSpacing = GraphicsOptions.optionValue(originalAST, S.BarSpacing, S.Automatic);
    IExpr chartLabels = GraphicsOptions.chartLabels(
        GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None), keyLabels);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr layout = GraphicsOptions.optionValue(originalAST, S.ChartLayout, S.Automatic);
    IExpr labelingFunction =
        GraphicsOptions.optionValue(originalAST, S.LabelingFunction, S.Automatic);
    IExpr elementFunction =
        GraphicsOptions.optionValue(originalAST, S.ChartElementFunction, S.Automatic);
    int barOrigin =
        GraphicsOptions.barOrigin(GraphicsOptions.optionValue(originalAST, S.BarOrigin, S.Bottom));

    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    IAST datasets = GraphicsOptions.chartDatasets((IAST) dataArg);
    int datasetCount = datasets.argSize();

    String layoutName = layout.isString() ? layout.toString() : "";
    boolean stacked = "Stacked".equals(layoutName) || "Percentile".equals(layoutName);
    boolean percentile = "Percentile".equals(layoutName);

    double spacing = 0.1;
    if (barSpacing.isNumber()) {
      double spacingValue = barSpacing.evalfNaN();
      if (Double.isFinite(spacingValue) && spacingValue >= 0) {
        spacing = spacingValue;
      }
    }
    double barWidth = 1.0 / (1.0 + spacing);

    List<Bar> bars = new ArrayList<>();
    double catMax;
    if (stacked) {
      // one bar per dataset, its values laid end to end
      for (int d = 1; d <= datasetCount; d++) {
        IExpr datasetExpr = datasets.get(d);
        if (!datasetExpr.isList()) {
          continue;
        }
        IAST dataset = (IAST) datasetExpr;
        double total = 0.0;
        if (percentile) {
          for (int j = 1; j < dataset.size(); j++) {
            double v = value(dataset.get(j));
            if (Double.isFinite(v)) {
              total += Math.abs(v);
            }
          }
        }
        double center = d;
        double base = 0.0;
        for (int j = 1; j < dataset.size(); j++) {
          IExpr item = dataset.get(j);
          double v = value(item);
          if (!Double.isFinite(v)) {
            continue;
          }
          double scaled = percentile ? (total > 0 ? v / total : 0.0) : v;
          bars.add(new Bar(center - barWidth / 2.0, center + barWidth / 2.0, base, base + scaled,
              j - 1, styleOf(item), labelOf(item), datum(item)));
          base += scaled;
        }
      }
      catMax = datasetCount + 0.5;
    } else {
      // the bars of a dataset sit side by side, with a gap between the datasets
      double cursor = 0.0;
      for (int d = 1; d <= datasetCount; d++) {
        IExpr datasetExpr = datasets.get(d);
        if (!datasetExpr.isList()) {
          continue;
        }
        IAST dataset = (IAST) datasetExpr;
        for (int j = 1; j < dataset.size(); j++) {
          IExpr item = dataset.get(j);
          double v = value(item);
          if (!Double.isFinite(v)) {
            continue;
          }
          cursor += 1.0;
          // a single dataset is drawn in one colour; several datasets colour by position
          int colorIndex = datasetCount == 1 ? 0 : j - 1;
          bars.add(new Bar(cursor - barWidth / 2.0, cursor + barWidth / 2.0, 0.0, v, colorIndex,
              styleOf(item), labelOf(item), datum(item)));
        }
        if (d < datasetCount) {
          cursor += GROUP_GAP;
        }
      }
      catMax = cursor + 0.5;
    }

    if (bars.isEmpty()) {
      return F.NIL;
    }

    double minValue = 0.0;
    double maxValue = 0.0;
    for (Bar bar : bars) {
      minValue = Math.min(minValue, Math.min(bar.base, bar.value));
      maxValue = Math.max(maxValue, Math.max(bar.base, bar.value));
    }

    IASTAppendable primitives = F.ListAlloc(bars.size() * 2 + 2);
    int labelPlacement = GraphicsOptions.labelingPlacement(labelingFunction);

    // a ColorFunction colours each bar by its own value, and outranks ChartStyle for the colour
    PlotColorFunction barColors = PlotColorFunction
        .of(PlotColorFunction.Family.CHART,
            GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic),
            GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True), S.BarChart,
            engine)
        .range(1, minValue, maxValue).build();

    for (Bar bar : bars) {
      IExpr functionColor = barColors == null ? F.NIL : barColors.color(bar.value);
      IExpr color = functionColor.isPresent() ? functionColor
          : bar.style.isPresent() ? bar.style : chartColor(chartStyle, bar.colorIndex);
      boolean colorIsExplicit =
          functionColor.isPresent() || bar.style.isPresent() || !chartStyle.isAutomatic();
      IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, colorIsExplicit);

      IASTAppendable group = F.ListAlloc(4);
      if (style.isPresent()) {
        group.append(style);
      }
      appendElement(group, elementFunction, barOrigin, bar, engine);
      primitives.append(group);

      // the value written on the bar, which LabelingFunction asks for
      IExpr valueLabel = GraphicsOptions.labelingText(labelingFunction, bar.datum, engine);
      if (valueLabel.isPresent()) {
        primitives.append(valueLabel(valueLabel, bar, barOrigin, labelPlacement));
      }
      // a Labeled wrapper on the datum labels that bar directly
      if (bar.label.isPresent()) {
        primitives.append(valueLabel(bar.label, bar, barOrigin, GraphicsOptions.LABELING_ABOVE));
      }
    }

    // ChartLabels name the categories, so they sit outside the value axis
    if (chartLabels.isList()) {
      appendCategoryLabels(primitives, (IAST) chartLabels, bars, datasets, datasetCount, stacked,
          barOrigin);
    }

    applyOrientation(graphicsOptions, barOrigin, catMax, minValue, maxValue);
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * Draw one bar, through {@code ChartElementFunction} when the caller supplied one.
   *
   * <p>
   * A function is handed the bar's rectangle, its value and its position, as
   * {@code f[{{x0, x1}, {y0, y1}}, value, {dataset, index}]}. The named element functions are
   * mostly gradient fills that this renderer has no equivalent for, so only
   * {@code "GlassRectangle"} is imitated and the rest fall back to a plain rectangle.
   */
  private static void appendElement(IASTAppendable group, IExpr elementFunction, int barOrigin,
      Bar bar, EvalEngine engine) {
    IAST rectangle = GraphicsOptions.barRectangle(barOrigin, bar.from, bar.to, bar.base, bar.value);

    if (elementFunction != null && elementFunction.isString("GlassRectangle")) {
      group.append(rectangle);
      // a translucent white band over the lower part of the bar reads as a highlight
      double mid = bar.base + (bar.value - bar.base) * 0.45;
      group.append(F.Opacity(F.num(0.25)));
      group.append(S.White);
      group.append(GraphicsOptions.barRectangle(barOrigin, bar.from, bar.to, bar.base, mid));
      group.append(F.Opacity(F.C1));
      return;
    }
    if (elementFunction != null && !elementFunction.isString() && elementFunction.isPresent()
        && elementFunction != S.Automatic && !elementFunction.isNone()) {
      double sign = GraphicsOptions.barsAreReversed(barOrigin) ? -1.0 : 1.0;
      IAST extent = GraphicsOptions.barsAreHorizontal(barOrigin)
          ? F.List(F.List(F.num(sign * bar.base), F.num(sign * bar.value)),
              F.List(F.num(bar.from), F.num(bar.to)))
          : F.List(F.List(F.num(bar.from), F.num(bar.to)),
              F.List(F.num(sign * bar.base), F.num(sign * bar.value)));
      IExpr drawn = engine.evaluate(F.ternaryAST3(elementFunction, extent, bar.datum, F.List()));
      if (drawn.isPresent() && !drawn.isAST(elementFunction.head())) {
        group.append(drawn);
        return;
      }
    }
    group.append(rectangle);
  }

  /** A label written on a bar, placed as {@code LabelingFunction} asked. */
  private static IAST valueLabel(IExpr text, Bar bar, int barOrigin, int placement) {
    double sign = GraphicsOptions.barsAreReversed(barOrigin) ? -1.0 : 1.0;
    double along;
    double offsetX;
    double offsetY;
    switch (placement) {
      case GraphicsOptions.LABELING_CENTER:
        along = (bar.base + bar.value) / 2.0;
        offsetX = 0;
        offsetY = 0;
        break;
      case GraphicsOptions.LABELING_BELOW:
        along = bar.base;
        offsetX = 0;
        offsetY = 1;
        break;
      default:
        along = bar.value;
        offsetX = 0;
        offsetY = -1;
        break;
    }
    if (GraphicsOptions.barsAreHorizontal(barOrigin)) {
      // the value axis runs sideways, so the offset that pushed the label clear turns with it
      double swap = offsetY;
      offsetY = 0;
      offsetX = swap;
      return F.List(S.Black, F.Text(text, F.List(F.num(sign * along), F.num(bar.center())),
          F.List(F.num(sign * offsetX), F.num(offsetY))));
    }
    return F.List(S.Black, F.Text(text, F.List(F.num(bar.center()), F.num(sign * along)),
        F.List(F.num(offsetX), F.num(sign * offsetY))));
  }

  /**
   * Category names written outside the value axis.
   *
   * <p>
   * With one dataset each bar is a category; with several the categories are the groups, so the
   * label goes under the middle of the group.
   */
  private static void appendCategoryLabels(IASTAppendable primitives, IAST chartLabels,
      List<Bar> bars, IAST datasets, int datasetCount, boolean stacked, int barOrigin) {
    List<Double> centers = new ArrayList<>();
    if (datasetCount == 1 && !stacked) {
      for (Bar bar : bars) {
        centers.add(bar.center());
      }
    } else if (stacked) {
      for (int d = 1; d <= datasetCount; d++) {
        centers.add((double) d);
      }
    } else {
      int index = 0;
      for (int d = 1; d <= datasetCount; d++) {
        IExpr dataset = datasets.get(d);
        int size = dataset.isList() ? ((IAST) dataset).argSize() : 0;
        if (size == 0 || index >= bars.size()) {
          continue;
        }
        int last = Math.min(index + size, bars.size()) - 1;
        centers.add((bars.get(index).center() + bars.get(last).center()) / 2.0);
        index += size;
      }
    }
    for (int i = 0; i < centers.size() && i < chartLabels.argSize(); i++) {
      IExpr label = chartLabels.get(i + 1);
      double center = centers.get(i);
      if (GraphicsOptions.barsAreHorizontal(barOrigin)) {
        primitives.append(
            F.List(S.Black, F.Text(label, F.List(F.C0, F.num(center)), F.List(F.C1, F.C0))));
      } else {
        primitives.append(
            F.List(S.Black, F.Text(label, F.List(F.num(center), F.C0), F.List(F.C0, F.C1))));
      }
    }
  }

  /**
   * Point the axes and the plot range the way {@code BarOrigin} asks.
   *
   * <p>
   * Bars that hang from the top, or grow leftwards, are drawn at negated coordinates. The value
   * axis then needs tick labels that read positive again.
   */
  private static void applyOrientation(GraphicsOptions graphicsOptions, int barOrigin,
      double catMax, double minValue, double maxValue) {
    double sign = GraphicsOptions.barsAreReversed(barOrigin) ? -1.0 : 1.0;
    double lo = Math.min(sign * minValue, sign * maxValue);
    double hi = Math.max(sign * minValue, sign * maxValue);
    // a little room above the tallest bar
    double pad = (hi - lo) * 0.05;
    if (sign > 0) {
      hi += pad;
    } else {
      lo -= pad;
    }

    if (GraphicsOptions.barsAreHorizontal(barOrigin)) {
      graphicsOptions.setBoundingBox(new double[] {lo, hi, 0.5, catMax});
      graphicsOptions.setAxes(F.List(S.True, S.False));
      if (GraphicsOptions.barsAreReversed(barOrigin)) {
        graphicsOptions.addOption(
            F.Rule(S.Ticks, F.List(GraphicsOptions.reversedValueTicks(maxValue), S.Automatic)));
      }
    } else {
      graphicsOptions.setBoundingBox(new double[] {0.5, catMax, lo, hi});
      graphicsOptions.setAxes(F.List(S.False, S.True));
      if (GraphicsOptions.barsAreReversed(barOrigin)) {
        graphicsOptions.addOption(
            F.Rule(S.Ticks, F.List(S.Automatic, GraphicsOptions.reversedValueTicks(maxValue))));
      }
    }
  }

  private static IExpr chartColor(IExpr chartStyle, int index) {
    if (chartStyle.isList()) {
      return GraphicsOptions.getPlotStyle(chartStyle, index);
    }
    if (!chartStyle.isAutomatic()) {
      return chartStyle;
    }
    return GraphicsOptions.chartStyleColorExpr(index);
  }

  /** The style of a {@code Style[value, style]} datum, or {@link F#NIL}. */
  private static IExpr styleOf(IExpr item) {
    return item.isAST(S.Style, 3) ? ((IAST) item).arg2() : F.NIL;
  }

  /** The label of a {@code Labeled[value, label]} datum, or {@link F#NIL}. */
  private static IExpr labelOf(IExpr item) {
    return item.isAST(S.Labeled, 3) ? ((IAST) item).arg2() : F.NIL;
  }

  /** The datum itself, with any {@code Labeled} or {@code Style} wrapper taken off. */
  private static IExpr datum(IExpr item) {
    if (item.isAST(S.Labeled) || item.isAST(S.Style)) {
      return ((IAST) item).arg1();
    }
    return item;
  }

  private static double value(IExpr item) {
    IExpr expr = datum(item);
    try {
      if (expr instanceof INumber) {
        return ((INumber) expr).reDoubleValue();
      }
      return expr.evalDouble();
    } catch (RuntimeException e) {
      return Double.NaN;
    }
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts and rasters draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;

    // BarChart Defaults
    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.True); // X axis usually hidden/replaced by
                                                                // labels, Y axis shown
    defaults[GraphicsOptions.X_FRAME] = S.False;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
