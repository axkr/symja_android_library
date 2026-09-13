package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>BubbleChart({{x, y, z}, ...})</code> - the points <code>{x, y}</code> as bubbles whose area
 * stands for <code>z</code>.
 *
 * <p>
 * The bubbles run from a hundredth to a tenth of the width of the data, and their <em>area</em> is
 * what grows with the value, as Mathematica draws them. Several datasets, <code>{{{x, y, z},
 * ...}, ...}</code>, each take a colour of their own.
 */
public class BubbleChart extends ListPlot {

  /** The smallest bubble radius, as a fraction of the width of the data. */
  private static final double MIN_RADIUS = 0.01;

  /** The largest bubble radius, as a fraction of the width of the data. */
  private static final double MAX_RADIUS = 0.1;

  /** One bubble, once its size is known. */
  private static final class Bubble {
    final double x;
    final double y;
    final double rx;
    final double ry;
    final IExpr datum;
    final int colorIndex;

    Bubble(double x, double y, double rx, double ry, IExpr datum, int colorIndex) {
      this.x = x;
      this.y = y;
      this.rx = rx;
      this.ry = ry;
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
    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr elementFunction =
        GraphicsOptions.optionValue(originalAST, S.ChartElementFunction, S.Automatic);
    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    IAST datasets = bubbleDatasets((IAST) dataArg);
    List<double[]> points = new ArrayList<>();
    List<IExpr> data = new ArrayList<>();
    List<Integer> colorIndices = new ArrayList<>();
    for (int d = 1; d < datasets.size(); d++) {
      IExpr datasetExpr = datasets.get(d);
      if (!datasetExpr.isList()) {
        continue;
      }
      IAST dataset = (IAST) datasetExpr;
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
          points.add(new double[] {x, y, z});
          data.add(triple);
          colorIndices.add(d - 1);
        }
      }
    }
    if (points.isEmpty()) {
      return F.NIL;
    }

    double xMin = Double.MAX_VALUE;
    double xMax = -Double.MAX_VALUE;
    double yMin = Double.MAX_VALUE;
    double yMax = -Double.MAX_VALUE;
    double zMin = Double.MAX_VALUE;
    double zMax = -Double.MAX_VALUE;
    for (double[] point : points) {
      xMin = Math.min(xMin, point[0]);
      xMax = Math.max(xMax, point[0]);
      yMin = Math.min(yMin, point[1]);
      yMax = Math.max(yMax, point[1]);
      zMin = Math.min(zMin, point[2]);
      zMax = Math.max(zMax, point[2]);
    }
    double width = xMax - xMin;
    if (!(width > 0)) {
      width = 1.0;
    }
    double height = yMax - yMin;
    if (!(height > 0)) {
      height = width;
    }
    double smallest = MIN_RADIUS * width;
    double largest = MAX_RADIUS * width;

    List<Bubble> bubbles = new ArrayList<>(points.size());
    for (int i = 0; i < points.size(); i++) {
      double[] point = points.get(i);
      // the area of a bubble is what the value stands for, so the radius grows with its root
      double t = zMax > zMin ? (point[2] - zMin) / (zMax - zMin) : 0.5;
      double rx = Math.sqrt(smallest * smallest + t * (largest * largest - smallest * smallest));
      // the picture is square, so a bubble is round only when its radii follow the ranges
      double ry = rx * height / width;
      bubbles.add(new Bubble(point[0], point[1], rx, ry, data.get(i), colorIndices.get(i)));
    }

    IASTAppendable primitives = F.ListAlloc(bubbles.size() + 1);
    double maxRx = 0;
    double maxRy = 0;
    for (Bubble bubble : bubbles) {
      IExpr color = GraphicsOptions.chartStyleColor(chartStyle, bubble.colorIndex);
      IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, !chartStyle.isAutomatic());
      IASTAppendable group = F.ListAlloc(3);
      // Mathematica outlines every bubble with a thin translucent black edge
      group.append(F.EdgeForm(F.Directive(F.GrayLevel(F.C0), F.Opacity(F.num(0.3)))));
      if (style.isPresent()) {
        group.append(style);
      }
      group.append(bubblePrimitive(elementFunction, bubble, engine));
      primitives.append(group);
      maxRx = Math.max(maxRx, bubble.rx);
      maxRy = Math.max(maxRy, bubble.ry);
    }

    graphicsOptions
        .setBoundingBox(new double[] {xMin - maxRx, xMax + maxRx, yMin - maxRy, yMax + maxRy});
    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.C1);
    }
    return createGraphicsFunction(primitives, graphicsOptions, ast);
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
    if (elementFunction != null && elementFunction.isPresent() && !elementFunction.isString()
        && elementFunction != S.Automatic && !elementFunction.isNone()) {
      IAST extent = F.List(F.List(F.num(bubble.x - bubble.rx), F.num(bubble.x + bubble.rx)),
          F.List(F.num(bubble.y - bubble.ry), F.num(bubble.y + bubble.ry)));
      IExpr drawn = engine.evaluate(F.ternaryAST3(elementFunction, extent, bubble.datum, F.List()));
      if (drawn.isPresent() && !drawn.isAST(elementFunction.head())) {
        return drawn;
      }
    }
    return F.binaryAST2(S.Disk, center, radii);
  }

  /** How many corners a {@code "NoiseBubble"} outline is drawn with. */
  private static final int NOISE_STEPS = 36;

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
          1.0 + 0.08 * Math.sin(3.0 * angle + phase) + 0.05 * Math.sin(7.0 * angle - phase);
      points.append(F.List(F.num(bubble.x + wobble * bubble.rx * Math.cos(angle)),
          F.num(bubble.y + wobble * bubble.ry * Math.sin(angle))));
    }
    return F.unaryAST1(S.Polygon, points);
  }

  /** {@code {{x,y,z}, ...}} is one dataset; {@code {{{x,y,z}, ...}, ...}} is several. */
  private static IAST bubbleDatasets(IAST data) {
    for (int i = 1; i < data.size(); i++) {
      IExpr item = data.get(i);
      if (item.isList() && item.argSize() > 0 && item.first().isList()) {
        return data;
      }
    }
    return F.List(data);
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getGraphicsRules());
    return GraphicsOptions.legended(result);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
