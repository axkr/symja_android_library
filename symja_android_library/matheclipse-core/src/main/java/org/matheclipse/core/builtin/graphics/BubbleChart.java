package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BubbleChart({{x, y, z}, ...})</code> - the points <code>{x, y}</code> as bubbles whose area
 * stands for <code>z</code>.
 *
 * <p>
 * Every bubble is a <code>Disk({x, y}, {rx, ry})</code> whose semi-axes are the same fraction of the
 * span of the data along each axis, so in the square picture it comes out round. The largest
 * <code>z</code> gets a tenth of the span and the <em>area</em> of a bubble grows with its value,
 * <code>r = 0.1 * Sqrt(z / zMax)</code>. An axis whose values all coincide measures the bubbles in
 * twice the size of that value instead, or in 2 when it is 0. Points with <code>z &lt;= 0</code> are
 * not drawn, but still count towards the span. The bubbles are drawn largest first, so that the
 * small ones stay visible on top, and the plot range just encloses them, or the data points when
 * no bubble is drawn. <code>BubbleSizes -&gt; {smin, smax}</code> sets the smallest and largest
 * diameter as fractions of the span instead.
 *
 * <p>
 * Several datasets, <code>{{{x, y, z}, ...}, ...}</code>, each take a colour of their own; a list
 * of <code>ChartStyle</code> colours runs over the points of a single dataset.
 */
public class BubbleChart extends AbstractChart {

  /** The largest bubble radius, as a fraction of the span of the data. */
  private static final double MAX_RADIUS = 0.1;

  /** The padding around the plot range, as a fraction of the finished picture. */
  private static final double PADDING = 0.05;

  /** One bubble, once its size is known. */
  private static final class Bubble {
    final double x;
    final double y;
    final double z;
    double rx;
    double ry;
    final IExpr datum;
    /** The colour and legend entry: the dataset of several, the point of a single one. */
    final int colorIndex;

    Bubble(double x, double y, double z, IExpr datum, int colorIndex) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.datum = datum;
      this.colorIndex = colorIndex;
    }
  }

  public BubbleChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }
    IASTAppendable keyLabels = F.ListAlloc();
    IExpr dataArg = GraphicsOptions.chartData(engine.evaluate(ast.arg1()), keyLabels);
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr styleOption = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    // None sets no style of its own: the default colours
    final IExpr chartStyle = styleOption.isNone() ? S.Automatic : styleOption;
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr elementFunction =
        GraphicsOptions.optionValue(originalAST, S.ChartElementFunction, S.Automatic);
    double[] bubbleSizes =
        bubbleSizes(GraphicsOptions.optionValue(originalAST, S.BubbleSizes, S.Automatic));

    boolean several = isSeveralDatasets((IAST) dataArg);
    IAST datasets = several ? (IAST) dataArg : F.List(dataArg);
    // the default colours run over the datasets; a single one is drawn in the first colour
    boolean colorByPoint = !several && !chartStyle.isAutomatic();
    List<Bubble> bubbles = new ArrayList<>();
    double[] xRange = {Double.MAX_VALUE, -Double.MAX_VALUE};
    double[] yRange = {Double.MAX_VALUE, -Double.MAX_VALUE};
    for (int d = 1; d < datasets.size(); d++) {
      IExpr datasetExpr = datasets.get(d);
      if (!datasetExpr.isList()) {
        continue;
      }
      IAST dataset = (IAST) datasetExpr;
      int pointIndex = 0;
      for (int i = 1; i < dataset.size(); i++) {
        IExpr item = PlotWrapper.strip(dataset.get(i));
        if (!item.isList() || item.argSize() < 3) {
          continue;
        }
        IAST triple = (IAST) item;
        double x = triple.arg1().evalfNaN();
        double y = triple.arg2().evalfNaN();
        double z = triple.arg3().evalfNaN();
        if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
          widen(xRange, x);
          widen(yRange, y);
          if (z > 0) {
            bubbles.add(new Bubble(x, y, z, triple, colorByPoint ? pointIndex : d - 1));
          }
          pointIndex++;
        }
      }
    }
    if (xRange[0] > xRange[1]) {
      return F.NIL;
    }

    double xSpan = span(xRange);
    double ySpan = span(yRange);
    double zMin = Double.MAX_VALUE;
    double zMax = -Double.MAX_VALUE;
    for (Bubble bubble : bubbles) {
      zMin = Math.min(zMin, bubble.z);
      zMax = Math.max(zMax, bubble.z);
    }
    // a stable sort keeps bubbles of the same size in the order they were given
    bubbles.sort((a, b) -> Double.compare(b.z, a.z));

    IASTAppendable primitives = F.ListAlloc(bubbles.size() + 1);
    double[] xBounds = {Double.MAX_VALUE, -Double.MAX_VALUE};
    double[] yBounds = {Double.MAX_VALUE, -Double.MAX_VALUE};
    // a legend entry names one dataset, in the fill its bubbles were drawn with
    GraphicsOptions.LegendColors legendColors =
        new GraphicsOptions.LegendColors(GraphicsOptions.chartStyleColor(chartStyle, 0));
    // the wobbly rim of a noise bubble reaches beyond the radius
    double rim = elementFunction.isString("NoiseBubble") ? NOISE_RIM : 1.0;
    Map<Integer, IAST> directives = new HashMap<>();
    for (Bubble bubble : bubbles) {
      double fraction = radiusFraction(bubble.z, zMin, zMax, bubbleSizes);
      bubble.rx = fraction * xSpan;
      bubble.ry = fraction * ySpan;
      widen(xBounds, bubble.x - rim * bubble.rx);
      widen(xBounds, bubble.x + rim * bubble.rx);
      widen(yBounds, bubble.y - rim * bubble.ry);
      widen(yBounds, bubble.y + rim * bubble.ry);

      IExpr color = GraphicsOptions.chartStyleColor(chartStyle, bubble.colorIndex);
      legendColors.record(bubble.colorIndex, color);
      IAST directive = directives.computeIfAbsent(bubble.colorIndex,
          k -> bubbleDirective(baseStyle, color, chartStyle));
      primitives.append(F.List(directive, bubblePrimitive(elementFunction, bubble, engine)));
    }

    if (bubbles.isEmpty()) {
      // without a bubble the range encloses the data points
      xBounds = pointBounds(xRange, xSpan);
      yBounds = pointBounds(yRange, ySpan);
    }
    graphicsOptions.setBoundingBox(new double[] {xBounds[0], xBounds[1], yBounds[0], yBounds[1]});
    // the shapes of a caller's element function may lie anywhere, so their range is left to the
    // renderer
    if (!isCustomElementFunction(elementFunction)
        && GraphicsOptions.optionValue(originalAST, S.PlotRange, F.NIL).isNIL()) {
      graphicsOptions.addOption(F.Rule(S.PlotRange,
          F.List(F.List(F.num(xBounds[0]), F.num(xBounds[1])),
              F.List(F.num(yBounds[0]), F.num(yBounds[1])))));
    }
    if (GraphicsOptions.optionValue(originalAST, S.PlotRangePadding, F.NIL).isNIL()) {
      IAST sides = F.List(F.Scaled(PADDING), F.Scaled(PADDING));
      graphicsOptions.addOption(F.Rule(S.PlotRangePadding, F.List(sides, sides)));
    }
    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.C1);
    }
    graphicsOptions.setChartLegend(chartLegends, legendColors);
    // the bubbles are grouped under one directive in the first colour; a list of styles leaves the
    // group in the default colour, as each bubble sets its own
    final IExpr groupStyle = chartStyle.isList() ? S.Automatic : chartStyle;
    IAST group = F.List(bubbleDirective(baseStyle,
        GraphicsOptions.chartStyleColor(groupStyle, 0), groupStyle), primitives);
    return createGraphicsFunction(F.List(group), graphicsOptions, ast);
  }

  /**
   * The style of a bubble: a thin translucent black edge, then the colour and the base style, all
   * in one flat {@code Directive}.
   */
  private static IAST bubbleDirective(IExpr baseStyle, IExpr color, IExpr chartStyle) {
    IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, !chartStyle.isAutomatic());
    IASTAppendable directive = F.ast(S.Directive, 3);
    directive.append(F.EdgeForm(F.Directive(F.GrayLevel(F.C0), F.Opacity(F.num(0.3)))));
    if (style.isAST(S.Directive)) {
      directive.appendArgs((IAST) style);
    } else if (style.isPresent()) {
      directive.append(style);
    }
    return directive;
  }

  /**
   * The smallest and largest diameter of {@code BubbleSizes -> {smin, smax}}, as fractions of the
   * span, or {@code null} for {@code Automatic} and any setting that is not a pair of numbers.
   */
  private static double[] bubbleSizes(IExpr value) {
    if (value.isList() && value.argSize() == 2) {
      double min = value.first().evalfNaN();
      double max = value.second().evalfNaN();
      if (Double.isFinite(min) && Double.isFinite(max) && min >= 0 && max >= 0) {
        return new double[] {min, max};
      }
    }
    return null;
  }

  /**
   * The range of the data points along one axis, widened by half the span on each side when the
   * values all coincide.
   */
  private static double[] pointBounds(double[] range, double span) {
    if (range[1] > range[0]) {
      return new double[] {range[0], range[1]};
    }
    return new double[] {range[0] - span / 2.0, range[1] + span / 2.0};
  }

  /** Widen the interval {@code {min, max}} so that it holds {@code value}. */
  private static void widen(double[] range, double value) {
    range[0] = Math.min(range[0], value);
    range[1] = Math.max(range[1], value);
  }

  /**
   * The length the bubble radii along one axis are measured in: the spread of the values, or, when
   * they all coincide, twice their size (2 when they are all 0).
   */
  private static double span(double[] range) {
    double span = range[1] - range[0];
    if (span > 0) {
      return span;
    }
    return range[0] != 0 ? 2.0 * Math.abs(range[0]) : 2.0;
  }

  /**
   * The radius of a bubble of size {@code z}, as a fraction of the span.
   *
   * @param bubbleSizes the smallest and largest diameter as fractions of the span, between which
   *        the radius runs linearly in {@code Sqrt(z)}; {@code null} for a radius that is a tenth of
   *        the span at {@code zMax} and keeps the area proportional to {@code z}
   */
  private static double radiusFraction(double z, double zMin, double zMax, double[] bubbleSizes) {
    if (bubbleSizes == null) {
      return MAX_RADIUS * Math.sqrt(z / zMax);
    }
    double rMin = bubbleSizes[0] / 2.0;
    double rMax = bubbleSizes[1] / 2.0;
    double lo = Math.sqrt(zMin);
    double hi = Math.sqrt(zMax);
    if (!(hi > lo)) {
      return rMin;
    }
    return rMin + (rMax - rMin) * (Math.sqrt(z) - lo) / (hi - lo);
  }

  /**
   * One bubble, through {@code ChartElementFunction} when the caller supplied one.
   *
   * <p>
   * A function is handed the bubble's bounding box, its data point and its position, as
   * {@code f[{{x0, x1}, {y0, y1}}, {x, y, z}, {}]}. Of the named element functions only
   * {@code "NoiseBubble"} is imitated, by a wobbly outline; the rest fall back to a plain disk.
   */
  private static IExpr bubblePrimitive(IExpr elementFunction, Bubble bubble, EvalEngine engine) {
    IAST center = F.List(F.num(bubble.x), F.num(bubble.y));
    IAST radii = F.List(F.num(bubble.rx), F.num(bubble.ry));
    if (elementFunction != null && elementFunction.isString("NoiseBubble")) {
      return noiseBubble(bubble);
    }
    if (isCustomElementFunction(elementFunction)) {
      IAST extent = F.List(F.List(F.num(bubble.x - bubble.rx), F.num(bubble.x + bubble.rx)),
          F.List(F.num(bubble.y - bubble.ry), F.num(bubble.y + bubble.ry)));
      IExpr drawn = engine.evaluate(F.ternaryAST3(elementFunction, extent, bubble.datum, F.List()));
      if (drawn.isPresent() && !drawn.isAST(elementFunction.head())) {
        return drawn;
      }
    }
    return F.binaryAST2(S.Disk, center, radii);
  }

  /** A {@code ChartElementFunction} of the caller's, as opposed to a named one or the default. */
  private static boolean isCustomElementFunction(IExpr elementFunction) {
    return elementFunction != null && elementFunction.isPresent() && !elementFunction.isString()
        && elementFunction != S.Automatic && !elementFunction.isNone();
  }

  /** How many corners a {@code "NoiseBubble"} outline is drawn with. */
  private static final int NOISE_STEPS = 36;

  /** The amplitudes of the two waves on the rim of a {@code "NoiseBubble"}. */
  private static final double NOISE_WAVE3 = 0.08;

  private static final double NOISE_WAVE7 = 0.05;

  /** How far a {@code "NoiseBubble"} rim reaches at most, as a multiple of the radius. */
  private static final double NOISE_RIM = 1.0 + NOISE_WAVE3 + NOISE_WAVE7;

  /**
   * A bubble with a wobbly rim, which is what {@code "NoiseBubble"} draws. The wobble is a fixed
   * function of the angle and the bubble's own position, so the same chart comes out the same way
   * twice.
   */
  private static IAST noiseBubble(Bubble bubble) {
    IASTAppendable points = F.ListAlloc(NOISE_STEPS);
    double phase = bubble.x * 12.9898 + bubble.y * 78.233;
    for (int i = 0; i < NOISE_STEPS; i++) {
      double angle = 2.0 * Math.PI * i / NOISE_STEPS;
      double wobble =
          1.0 + NOISE_WAVE3 * Math.sin(3.0 * angle + phase)
              + NOISE_WAVE7 * Math.sin(7.0 * angle - phase);
      points.append(F.List(F.num(bubble.x + wobble * bubble.rx * Math.cos(angle)),
          F.num(bubble.y + wobble * bubble.ry * Math.sin(angle))));
    }
    return F.unaryAST1(S.Polygon, points);
  }

  /** {@code {{x,y,z}, ...}} is one dataset; {@code {{{x,y,z}, ...}, ...}} is several. */
  private static boolean isSeveralDatasets(IAST data) {
    for (int i = 1; i < data.size(); i++) {
      IExpr item = data.get(i);
      if (item.isList() && item.argSize() > 0 && item.first().isList()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  protected GraphicsOptions.OptionSet optionExtras(GraphicsOptions.OptionSet optionSet) {
    return optionSet.add(S.Automatic, S.BubbleSizes);
  }

  @Override
  protected void chartDefaults(IExpr[] defaults) {
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;
  }
}
