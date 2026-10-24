package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
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
 * <code>RectangleChart({{w1, h1}, {w2, h2}, ...})</code> - a bar chart whose bars have the width
 * <code>wi</code> and the height <code>hi</code>, side by side from <code>x = 0</code>.
 *
 * <p>
 * Several datasets, <code>{{{w, h}, ...}, ...}</code>, are drawn one after the other with a wider
 * gap between them, and their bars are coloured by their position within the dataset. An entry
 * which is not a pair of numbers is left out.
 */
public class RectangleChart extends ListPlot {

  /** The gap between the datasets of a chart, unless <code>BarSpacing</code> sets one. */
  private static final double DATASET_GAP = 0.2;

  /** One bar, once the layout has decided where it goes. */
  public static final class Box {
    /** Start of the bar along the x axis. */
    public final double from;
    /** The sizes of the bar after its width: the height, and for a cuboid also the depth. */
    public final double[] sizes;
    /** The position of the bar within its dataset, from 0. */
    public final int position;
    /** The entry the bar stands for, with its display wrappers. */
    public final IExpr item;

    Box(double from, double[] sizes, int position, IExpr item) {
      this.from = from;
      this.sizes = sizes;
      this.position = position;
      this.item = item;
    }

    /** End of the bar along the x axis. */
    public double to() {
      return from + sizes[0];
    }
  }

  public RectangleChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }
    IExpr dataArg = engine.evaluate(ast.arg1());
    IAST datasets = datasets(dataArg, 2);
    if (datasets == null) {
      if (dataArg.isList()) {
        // `1` is not a valid dataset or a list of datasets.
        return Errors.printMessage(ast.topHead(), "ldata", F.list(dataArg), engine);
      }
      return F.NIL;
    }
    IExpr barSpacing = GraphicsOptions.optionValue(originalAST, S.BarSpacing, S.Automatic);
    List<Box> boxes = layout(datasets, 2, barSpacing);
    if (boxes.isEmpty()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    boolean byPosition = datasets.argSize() > 1 || !chartStyle.isAutomatic();
    IASTAppendable primitives = F.ListAlloc(boxes.size() + 1);
    // the style of the chart as a whole, which the style of each bar then refines
    primitives.append(color(chartStyle, 0));
    double xMin = 0.0;
    double xMax = 0.0;
    double yMin = 0.0;
    double yMax = 0.0;
    for (Box box : boxes) {
      PlotWrapper wrapper = PlotWrapper.of(box.item);
      IExpr color =
          wrapper.style.isPresent() ? wrapper.style : color(chartStyle, byPosition ? box.position : 0);
      IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color,
          wrapper.style.isPresent() || !chartStyle.isAutomatic());
      IASTAppendable group = F.ListAlloc(2);
      if (style.isPresent()) {
        group.append(style);
      }
      group.append(F.ternaryAST3(S.Rectangle, F.List(F.num(box.from), F.CD0),
          F.List(F.num(box.to()), F.num(box.sizes[1])),
          F.Rule(F.stringx("RoundingRadius"), F.C0)));
      primitives.append(wrapper.hasTooltip() ? F.binaryAST2(S.Tooltip, group, wrapper.tooltip)
          : group);
      xMin = Math.min(xMin, box.from);
      xMax = Math.max(xMax, box.to());
      yMin = Math.min(yMin, box.sizes[1]);
      yMax = Math.max(yMax, box.sizes[1]);
    }
    if (datasets.argSize() > 1) {
      // the half gap in front of the first dataset is matched by one after the last
      xMax += gaps(barSpacing, boxes.size())[1] / 2.0;
    }
    // a little room above the tallest bar
    double pad = (yMax - yMin) * 0.05;
    graphicsOptions.setBoundingBox(new double[] {xMin, xMax, yMin < 0 ? yMin - pad : yMin,
        yMax > 0 ? yMax + pad : yMax});
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** The colour of the bar at <code>index</code>; a list of styles is one directive. */
  public static IExpr color(IExpr chartStyle, int index) {
    IExpr color = GraphicsOptions.chartStyleColor(chartStyle, index);
    if (color.isList()) {
      IASTAppendable directive = F.ast(S.Directive, color.argSize());
      directive.appendArgs((IAST) color);
      return directive;
    }
    return color;
  }

  /**
   * The datasets of the first argument: one list of entries, or a list of such lists. An entry is
   * a list of <code>dimension</code> values.
   *
   * @return <code>null</code> if the argument holds neither
   */
  public static IAST datasets(IExpr arg, int dimension) {
    if (!arg.isList() || arg.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) arg;
    if (list.exists(x -> isEntry(x, dimension))) {
      return F.List(list);
    }
    for (IExpr dataset : list) {
      if (!dataset.isList() || !((IAST) dataset).exists(x -> isEntry(x, dimension))) {
        return null;
      }
    }
    return list;
  }

  private static boolean isEntry(IExpr item, int dimension) {
    IExpr datum = PlotWrapper.strip(item);
    return datum.isList() && datum.argSize() == dimension && ((IAST) datum).forAll(x -> !x.isList());
  }

  /**
   * The gap between two bars of a dataset and the gap between two datasets.
   *
   * <p>
   * By default the gap between the bars narrows a little as there are more of them: a tenth less a
   * thousandth per bar, but not below <code>0.05</code>.
   */
  private static double[] gaps(IExpr barSpacing, int count) {
    double gap = Math.max(0.05, 0.1 - 0.001 * count);
    double datasetGap = DATASET_GAP;
    if (barSpacing.isNone()) {
      return new double[] {0.0, 0.0};
    }
    if (barSpacing.isList() && barSpacing.argSize() == 2) {
      double inner = spacing(barSpacing.first(), gap);
      double outer = spacing(barSpacing.second(), datasetGap);
      return new double[] {inner, outer};
    }
    return new double[] {spacing(barSpacing, gap), datasetGap};
  }

  private static double spacing(IExpr value, double defaultValue) {
    if (value.isNone()) {
      return 0.0;
    }
    double v = value.evalfNaN();
    return Double.isFinite(v) && v >= 0.0 ? v : defaultValue;
  }

  /**
   * Place the bars of the datasets side by side along the x axis.
   *
   * @param datasets the datasets, see {@link #datasets(IExpr, int)}
   * @param dimension the number of values of an entry: its width and its other sizes
   * @param barSpacing the value of the <code>BarSpacing</code> option
   * @return the bars; an entry whose values are not all real numbers is left out
   */
  public static List<Box> layout(IAST datasets, int dimension, IExpr barSpacing) {
    List<Box> entries = new ArrayList<>();
    List<Integer> datasetOf = new ArrayList<>();
    for (int d = 1; d <= datasets.argSize(); d++) {
      IAST dataset = (IAST) datasets.get(d);
      for (int j = 1; j <= dataset.argSize(); j++) {
        IExpr item = dataset.get(j);
        if (!isEntry(item, dimension)) {
          continue;
        }
        IAST datum = (IAST) PlotWrapper.strip(item);
        double[] sizes = new double[dimension];
        boolean finite = true;
        for (int k = 0; k < dimension; k++) {
          sizes[k] = datum.get(k + 1).evalfNaN();
          finite &= Double.isFinite(sizes[k]);
        }
        if (finite) {
          // a bar is as wide as its width says, whichever its sign
          sizes[0] = Math.abs(sizes[0]);
          entries.add(new Box(0.0, sizes, j - 1, item));
          datasetOf.add(d);
        }
      }
    }
    double[] gaps = gaps(barSpacing, entries.size());
    boolean several = datasets.argSize() > 1;
    // with several datasets each is set off by half the dataset gap on either side
    double cursor = several ? gaps[1] / 2.0 : 0.0;
    List<Box> boxes = new ArrayList<>(entries.size());
    for (int i = 0; i < entries.size(); i++) {
      Box entry = entries.get(i);
      if (i > 0) {
        cursor += datasetOf.get(i).equals(datasetOf.get(i - 1)) ? gaps[0] : gaps[1];
      }
      boxes.add(new Box(cursor, entry.sizes, entry.position, entry.item));
      cursor += entry.sizes[0];
    }
    return boxes;
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
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;
    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.True);
    defaults[GraphicsOptions.X_FRAME] = S.False;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
