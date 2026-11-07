package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BarChart3D({v1, v2, ...})</code> and <code>BarChart3D({{v1, v2, ...}, ...})</code> - a
 * chart of unit wide and unit deep blocks, one per value, standing side by side along the x axis.
 *
 * <p>
 * Every block is a <code>GraphicsComplex</code> of its eight corners and six faces, each face a
 * <code>Polygon</code> with <code>VertexColors</code> shading from the block's colour to a lighter
 * tint on one side and a darker shade on the other. The bars of one dataset form a group, and a
 * wider gap separates the groups. A flat list is drawn in the first chart colour, several datasets
 * colour their bars by the position in the dataset. <code>ChartLayout -> "Stacked"</code> piles the
 * values of a dataset into one column, the positive ones upwards and the negative ones downwards.
 */
public class BarChart3D extends AbstractEvaluator {

  /** The default colours of the bars, by position in a dataset. */
  static final double[][] COLORS = { //
      {0.95, 0.7125, 0.0}, //
      {0.0, 0.595, 0.85}, //
      {0.325, 0.65, 0.0}, //
      {0.9, 0.333, 0.09}, //
      {0.6525, 0.405, 0.9}, //
      {0.75, 0.45, 0.0}, //
      {0.0, 0.6, 0.6}};

  /** The corner indices of the six faces of a block, wound to face outwards. */
  private static final int[][] FACES = { //
      {4, 3, 2, 1}, {5, 6, 7, 8}, {2, 3, 7, 6}, {5, 8, 4, 1}, {4, 8, 7, 3}, {2, 6, 5, 1}};

  /** The tone of each face corner: 0 the colour itself, 1 the light tint, 2 the dark shade. */
  private static final int[][] TONES = { //
      {0, 0, 2, 2}, {2, 2, 0, 0}, {1, 0, 0, 1}, {0, 1, 1, 0}, {0, 0, 1, 1}, {0, 0, 1, 1}};

  /** At most this many bars are drawn. */
  private static final int MAX_BARS = 10000;

  /** One bar: its value, its colour and the datum it came from. */
  private static final class Bar {
    final double value;
    final double[] color;
    final PlotWrapper wrapper;
    /** Whether it starts a new column; in a stacked chart only the first of a dataset does. */
    final boolean newColumn;
    /** Which group of columns it belongs to. */
    final int group;

    Bar(double value, double[] color, PlotWrapper wrapper, boolean newColumn, int group) {
      this.value = value;
      this.color = color;
      this.wrapper = wrapper;
      this.newColumn = newColumn;
      this.group = group;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IASTAppendable keyLabels = F.ListAlloc();
    IExpr data = GraphicsOptions.chartDataMagnitudes(ast.arg1(), ast, keyLabels, engine);
    if (!data.isList() || data.argSize() == 0) {
      return F.NIL;
    }
    int argSize = positionalArguments(ast);
    IExpr chartStyle = GraphicsOptions.optionValue(ast, S.ChartStyle, S.Automatic);
    IExpr layout = GraphicsOptions.optionValue(ast, S.ChartLayout, S.Automatic);
    boolean stacked = layout.isString("Stacked");
    IAST datasets = GraphicsOptions.chartDatasets((IAST) data);
    // a flat list is one dataset whose values each stand in a column of their own
    boolean flat = datasets.argSize() == 1 && datasets.arg1() == data;

    List<Bar> bars = new ArrayList<>();
    for (int d = 1; d <= datasets.argSize(); d++) {
      IExpr dataset = datasets.get(d);
      if (!dataset.isList()) {
        continue;
      }
      boolean first = true;
      for (int j = 1; j <= dataset.argSize() && bars.size() < MAX_BARS; j++) {
        PlotWrapper wrapper = PlotWrapper.of(dataset.getAt(j));
        double value = wrapper.datum.evalfNaN();
        if (!Double.isFinite(value)) {
          continue;
        }
        int colorIndex = flat ? (chartStyle.isAutomatic() ? 0 : j - 1) : j - 1;
        double[] color = rgb(wrapper.style.isPresent() ? wrapper.style
            : chartStyle.isAutomatic() ? F.NIL
                : GraphicsOptions.chartStyleColor(chartStyle, colorIndex),
            colorIndex);
        bars.add(new Bar(value, color, wrapper, flat || !stacked || first, flat ? 0 : d));
        first = false;
      }
    }
    if (bars.isEmpty()) {
      return F.NIL;
    }

    // the columns along x: one wide, a small gap between the columns of a group and a gap of 0.2
    // between the groups
    int columns = 0;
    for (Bar bar : bars) {
      if (bar.newColumn) {
        columns++;
      }
    }
    double gap = 0.1 - columns / 1000.0;
    double x = 0.0;
    double up = 0.0;
    double down = 0.0;
    double zMin = 0.0;
    double zMax = 0.0;
    int previousGroup = -1;
    IASTAppendable primitives = F.ListAlloc(bars.size());
    for (Bar bar : bars) {
      if (bar.newColumn) {
        if (previousGroup >= 0) {
          x += 1.0 + (bar.group == previousGroup ? gap : 0.2);
        }
        previousGroup = bar.group;
        up = 0.0;
        down = 0.0;
      }
      double z0;
      double z1;
      if (!stacked || flat) {
        z0 = Math.min(bar.value, 0.0);
        z1 = Math.max(bar.value, 0.0);
      } else if (bar.value >= 0.0) {
        z0 = up;
        up += bar.value;
        z1 = up;
      } else {
        z1 = down;
        down += bar.value;
        z0 = down;
      }
      zMin = Math.min(zMin, z0);
      zMax = Math.max(zMax, z1);
      IAST block = block(new double[] {x, 0.0, z0}, new double[] {x + 1.0, 1.0, z1}, bar.color);
      primitives.append(bar.wrapper.hasTooltip() ? F.binaryAST2(S.Tooltip, block,
          bar.wrapper.tooltip) : block);
      if (bar.wrapper.label.isPresent()) {
        // a Labeled value is named above its bar
        primitives.append(F.List(S.Black, F.Text(bar.wrapper.label,
            F.List(F.num(x + 0.5), F.num(0.5), F.num(z1)), F.List(F.C0, F.CN1))));
      }
    }
    double width = x + 1.0;
    double depth = Math.max(1.0 / width, 0.2);
    IExpr[] defaults = { //
        F.Rule(S.Axes, S.True), //
        F.Rule(S.Boxed, S.False), //
        F.Rule(S.BoxRatios, F.List(F.C1, F.num(depth), F.num(2.0 / (1.0 + Math.sqrt(5.0))))), //
        F.Rule(S.Lighting, F.stringx("Neutral")), //
        F.Rule(S.PlotRange,
            F.List(F.List(F.num(-0.05 * width), F.num(1.05 * width)),
                F.List(F.num(-0.25), F.num(1.25)), F.List(F.num(zMin), F.num(zMax)))), //
        F.Rule(S.Ticks, F.List(F.CEmptyList, F.CEmptyList, S.Automatic)), //
        F.Rule(S.ViewPoint, F.List(F.C2, F.ZZ(-10), F.C2))};
    return Plot3DTools.graphics3D(primitives, ast, argSize, defaults, false);
  }

  /**
   * The block from corner <code>lo</code> to corner <code>hi</code> in colour <code>c</code>: faint
   * edges, and faces blending from the colour towards a tint four fifths of the way to white on one
   * side and a shade of half its brightness on the other.
   */
  static IAST block(double[] lo, double[] hi, double[] c) {
    IAST[] tones = {rgbColor(c[0], c[1], c[2]),
        rgbColor(c[0] + (1.0 - c[0]) * 0.8, c[1] + (1.0 - c[1]) * 0.8, c[2] + (1.0 - c[2]) * 0.8),
        rgbColor(c[0] / 2.0, c[1] / 2.0, c[2] / 2.0)};
    IASTAppendable corners = F.ListAlloc(8);
    for (int i = 0; i < 8; i++) {
      // x is low for the first four corners, y high for the middle two of each four, and z high
      // for the last two of each four
      int inFour = i % 4;
      corners.append(F.List(F.num(i < 4 ? lo[0] : hi[0]),
          F.num(inFour == 1 || inFour == 2 ? hi[1] : lo[1]), F.num(inFour >= 2 ? hi[2] : lo[2])));
    }
    IASTAppendable faces = F.ListAlloc(FACES.length);
    for (int f = 0; f < FACES.length; f++) {
      IASTAppendable indices = F.ListAlloc(4);
      IASTAppendable colors = F.ListAlloc(4);
      for (int v = 0; v < 4; v++) {
        indices.append(F.ZZ(FACES[f][v]));
        colors.append(tones[TONES[f][v]]);
      }
      faces.append(F.binaryAST2(S.Polygon, indices, F.Rule(S.VertexColors, colors)));
    }
    IAST complex = F.binaryAST2(S.GraphicsComplex, corners, faces);
    return F.List(F.unaryAST1(S.EdgeForm, F.Directive(F.GrayLevel(F.C0), F.Opacity(F.num(0.1)))),
        tones[0], F.binaryAST2(S.Style, complex, F.Rule(S.Lighting,
            F.List(F.List(F.stringx("Ambient"), F.GrayLevel(F.C1))))));
  }

  /** The number of arguments before the first option. */
  static int positionalArguments(IAST ast) {
    int argSize = 1;
    while (argSize < ast.argSize() && !ast.get(argSize + 1).isRuleAST()) {
      argSize++;
    }
    return argSize;
  }

  /** The default chart colour number <code>index</code>, cycled. */
  static double[] defaultColor(int index) {
    return COLORS[Math.floorMod(index, COLORS.length)];
  }

  /**
   * The red, green and blue parts of a colour, or of chart colour <code>index</code> when
   * <code>color</code> is {@link F#NIL} or no colour.
   */
  static double[] rgb(IExpr color, int index) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor) && color.argSize() >= 3) {
        double r = color.first().evalfNaN();
        double g = color.second().evalfNaN();
        double b = ((IAST) color).arg3().evalfNaN();
        if (Double.isFinite(r) && Double.isFinite(g) && Double.isFinite(b)) {
          return new double[] {r, g, b};
        }
      }
      java.awt.Color parsed = ColorUtil.parse(color);
      if (parsed != null) {
        return new double[] {parsed.getRed() / 255.0, parsed.getGreen() / 255.0,
            parsed.getBlue() / 255.0};
      }
    }
    return defaultColor(index);
  }

  static IAST rgbColor(double r, double g, double b) {
    return F.RGBColor(F.num(r), F.num(g), F.num(b));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
