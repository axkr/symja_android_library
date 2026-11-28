package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.graphics.CurvePrimitives;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The plots of a function of three variables over a box:
 *
 * <ul>
 * <li><code>DensityPlot3D(f, {x, x0, x1}, {y, y0, y1}, {z, z0, z1})</code>: a <code>Raster3D</code>
 * of translucent cells, coloured by the value of <code>f</code> at their centres;
 * <li><code>SliceDensityPlot3D(f, surfaces, ...)</code>: <code>f</code> coloured continuously on
 * slice surfaces through the box;
 * <li><code>SliceContourPlot3D(f, surfaces, ...)</code>: the contour bands of <code>f</code> on the
 * slice surfaces, with the contour lines between them.
 * </ul>
 *
 * The slice surfaces are <code>"CenterPlanes"</code>, <code>"BackPlanes"</code>,
 * <code>"XStackedPlanes"</code>, <code>"YStackedPlanes"</code>, <code>"ZStackedPlanes"</code>
 * (also <code>{"ZStackedPlanes", n}</code> and <code>{"ZStackedPlanes", {z1, z2, ...}}</code>), an
 * equation such as <code>z == 1/2</code> or <code>x^2 + y^2 == 1</code>, <code>Sphere</code>,
 * <code>Ball</code>, <code>InfinitePlane</code>, or a list of these.
 *
 * <p>
 * <code>ColorFunction</code> is given <code>x, y, z, f</code>, a colour scheme name colours by
 * <code>f</code>. <code>OpacityFunction</code> of <code>DensityPlot3D</code> is given the scaled value
 * (the value itself with <code>OpacityFunctionScaling -&gt; False</code>).
 */
public final class VolumePlots {

  private VolumePlots() {}

  /** The cells along each axis of a <code>DensityPlot3D</code> without <code>PlotPoints</code>. */
  static final int DENSITY_POINTS = 15;

  /** At most this many cells along each axis of a <code>DensityPlot3D</code>. */
  static final int MAX_DENSITY_POINTS = 30;

  /** The samples along each side of a slice plane without <code>PlotPoints</code>. */
  static final int SLICE_POINTS = 30;

  /** At most this many samples along each side of a slice plane. */
  static final int MAX_SLICE_POINTS = 120;

  /** At most this many samples along each axis for a curved slice surface. */
  static final int MAX_IMPLICIT_POINTS = 40;

  /** How many planes a stacked slice specification without a count has. */
  static final int STACKED_PLANES = 3;

  /** The common part of the three plots: the box, the function, the options and the colours. */
  private abstract static class VolumePlot extends AbstractFunctionOptionEvaluator {

    /** The number of arguments before the ranges: 1 for the density, 2 for the slice plots. */
    abstract int rangeOffset();

    abstract IExpr plot(IAST ast, int argSize, Box box, EvalEngine engine);

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      // an option without a symbol of its own (OpacityFunction) ends the options the engine
      // separates, so the positional arguments are counted here
      int positional = 0;
      while (positional < ast.argSize() && !ast.get(positional + 1).isRuleAST()) {
        positional++;
      }
      if (positional != rangeOffset() + 3) {
        return F.NIL;
      }
      IASTAppendable rangeView = F.ListAlloc(4);
      rangeView.append(ast.arg1());
      for (int i = 1; i <= 3; i++) {
        rangeView.append(ast.get(rangeOffset() + i));
      }
      if (!Plot3DTools.validRanges3D(rangeView, topHead(), engine)) {
        return F.NIL;
      }
      Box box = new Box();
      if (!Plot3DTools.readBox(rangeView, topHead(), box.vars, box.min, box.max, engine)) {
        return F.NIL;
      }
      try {
        box.function = new MultiVariateNumerical(ast.arg1(),
            F.List(box.vars[0], box.vars[1], box.vars[2]));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
      return plot(ast, positional, box, engine);
    }

    /** The head this evaluator is registered for. */
    abstract ISymbol topHead();

    @Override
    public int[] expectedArgSize(IAST ast) {
      // the positional arguments are counted in evaluate(), after the options
      return new int[] {rangeOffset() + 3, Integer.MAX_VALUE};
    }

    @Override
    protected long symbolicFirstArgumentMillis() {
      return PLOT_SYMBOLIC_MILLIS;
    }

    @Override
    public boolean localIterators() {
      return true;
    }

    @Override
    protected boolean compoundIteratorVariables() {
      return true;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      GraphicsOptions.OptionSet options = Plot3DTools
          .frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()).add(S.Automatic,
              S.Contours, S.ContourStyle, S.ContourShading));
      setOptions(newSymbol, options.keys(), options.values());
    }
  }

  /** The box of a plot, its variables and the function sampled in it. */
  static final class Box {
    final ISymbol[] vars = new ISymbol[3];
    final double[] min = new double[3];
    final double[] max = new double[3];
    MultiVariateNumerical function;

    double value(double x, double y, double z) {
      double v = function.value(new double[] {x, y, z});
      return Double.isFinite(v) ? v : Double.NaN;
    }

    double center(int axis) {
      return (min[axis] + max[axis]) / 2.0;
    }
  }

  /**
   * The colours of a plot's values: its <code>ColorFunction</code>, or the default field gradient.
   */
  private static final class ValueColors {
    private final PlotColorFunction colorFunction;
    private final double low;
    private final double high;

    ValueColors(IAST ast, ISymbol head, Box box, double low, double high, EvalEngine engine) {
      this.low = low;
      this.high = high;
      this.colorFunction = PlotColorFunction
          .of(PlotColorFunction.Family.CONTOUR_3D,
              GraphicsOptions.optionValue(ast, S.ColorFunction, S.Automatic),
              GraphicsOptions.optionValue(ast, S.ColorFunctionScaling, S.True), head, engine)
          .sink(PlotColorFunction.Sink.FLAT)
          .ranges(box.min[0], box.max[0], box.min[1], box.max[1], box.min[2], box.max[2], low,
              high)
          .build();
    }

    double scaled(double value) {
      return high > low ? Math.max(0.0, Math.min(1.0, (value - low) / (high - low))) : 0.5;
    }

    /** The colour at a point with the value there, as one colour expression. */
    IExpr color(double x, double y, double z, double value) {
      IExpr color = colorFunction != null ? colorFunction.color(x, y, z, value) : F.NIL;
      if (color.isPresent() && ColorUtil.parse(color) == null) {
        color = GraphicsOptions.toColorExpr(color, F.NIL);
      }
      return color.isPresent() ? color : GraphicsOptions.getFieldColor(scaled(value));
    }
  }

  // ---------------------------------------------------------------- DensityPlot3D

  /** <code>DensityPlot3D(f, {x, x0, x1}, {y, y0, y1}, {z, z0, z1})</code>. */
  public static final class DensityPlot3D extends VolumePlot {

    @Override
    int rangeOffset() {
      return 1;
    }

    @Override
    ISymbol topHead() {
      return S.DensityPlot3D;
    }

    @Override
    IExpr plot(IAST ast, int argSize, Box box, EvalEngine engine) {
      int n = Plot3DTools.plotPoints(GraphicsOptions.optionValue(ast, S.PlotPoints, S.Automatic),
          DENSITY_POINTS, MAX_DENSITY_POINTS)[0];
      // the samples are taken at the points of a grid, the faces of the box included
      double[][][] values = new double[n][n][n];
      double low = Double.POSITIVE_INFINITY;
      double high = Double.NEGATIVE_INFINITY;
      for (int k = 0; k < n; k++) {
        double z = sample(box, 2, k, n);
        for (int j = 0; j < n; j++) {
          double y = sample(box, 1, j, n);
          for (int i = 0; i < n; i++) {
            double v = box.value(sample(box, 0, i, n), y, z);
            values[k][j][i] = v;
            if (Double.isFinite(v)) {
              low = Math.min(low, v);
              high = Math.max(high, v);
            }
          }
        }
        engine.checkTimeLimit();
      }
      if (!(low <= high)) {
        return F.NIL;
      }
      // the cells hold the values scaled into 0 ... 1; the colour function gives each its colour
      // and its opacity
      List<Double> scaled = new ArrayList<>(n * n * n);
      IASTAppendable layers = F.ListAlloc(n);
      for (int k = 0; k < n; k++) {
        IASTAppendable rows = F.ListAlloc(n);
        for (int j = 0; j < n; j++) {
          IASTAppendable cells = F.ListAlloc(n);
          for (int i = 0; i < n; i++) {
            double v = values[k][j][i];
            if (!Double.isFinite(v)) {
              // no value: a clear cell
              cells.append(F.List(F.C0, F.C0, F.C0, F.C0));
              continue;
            }
            double t = high > low ? (v - low) / (high - low) : 0.5;
            scaled.add(t);
            cells.append(F.num(t));
          }
          rows.append(cells);
        }
        layers.append(rows);
      }
      IExpr colorFunction = F.Function(F.binaryAST2(S.Opacity,
          opacityExpression(ast, scaled, low, high), colorExpression(ast, low, high)));
      IASTAppendable raster = F.ast(S.Raster3D, 3);
      raster.append(layers);
      raster.append(F.List(F.List(F.num(box.min[0]), F.num(box.min[1]), F.num(box.min[2])),
          F.List(F.num(box.max[0]), F.num(box.max[1]), F.num(box.max[2]))));
      raster.append(F.Rule(S.ColorFunction, colorFunction));
      return Plot3DTools.graphics3D(F.List(raster), ast, argSize,
          Plot3DTools.cubeDefaults(box.min, box.max, true));
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      super.setUp(newSymbol);
      newSymbol.addAttributes(ISymbol.READPROTECTED);
    }
  }

  /** Sample <code>k</code> of <code>n</code> along an axis, the first and the last on the faces. */
  private static double sample(Box box, int axis, int k, int n) {
    return n == 1 ? box.center(axis)
        : box.min[axis] + (box.max[axis] - box.min[axis]) * k / (n - 1);
  }

  /**
   * The colour of a scaled value <code>#</code>: <code>ColorFunction</code> as a scheme name or a
   * function (of the value itself with <code>ColorFunctionScaling -&gt; False</code>), by default
   * the field gradient.
   */
  private static IExpr colorExpression(IAST ast, double low, double high) {
    IExpr spec = GraphicsOptions.optionValue(ast, S.ColorFunction, S.Automatic);
    if (spec.isNIL() || spec.isAutomatic() || spec.isNone()) {
      return F.binaryAST2(S.Blend, GraphicsOptions.fieldGradientColors(), F.Slot1);
    }
    if (spec.isString()) {
      return F.unaryAST1(F.unaryAST1(S.ColorData, spec), F.Slot1);
    }
    return F.unaryAST1(spec, valueArgument(ast, "ColorFunctionScaling", low, high));
  }

  /** <code>#</code>, or the value it was scaled from when the scaling option is False. */
  private static IExpr valueArgument(IAST ast, String scalingOption, double low, double high) {
    IExpr scaling = CurvePrimitives.optionByName(ast, scalingOption);
    return scaling.isFalse() ? F.Plus(F.num(low), F.Times(F.num(high - low), F.Slot1)) : F.Slot1;
  }

  /** The opacity of a cell where none is asked for, and of the frequent values by default. */
  private static final double OPACITY = 0.5;

  /** The opacity of the most frequent values by default: nearly clear. */
  private static final double FREQUENT_OPACITY = 0.01;

  /**
   * The opacity of a scaled value <code>#</code>: <code>OpacityFunction</code> as a number or a
   * function (of the value itself with <code>OpacityFunctionScaling -&gt; False</code>). By default
   * the values are counted into 20 bins: the most frequent bins, as many as hold 80% of the
   * values, are nearly clear, and the rest is half opaque, so that what stands out from the bulk
   * of the volume shows.
   */
  private static IExpr opacityExpression(IAST ast, List<Double> scaled, double low,
      double high) {
    IExpr spec = CurvePrimitives.optionByName(ast, "OpacityFunction");
    if (spec.isReal()) {
      return F.num(clamp(spec.evalfNaN()));
    }
    if (spec.isPresent() && !spec.isAutomatic() && !spec.isNone()) {
      return F.unaryAST1(spec, valueArgument(ast, "OpacityFunctionScaling", low, high));
    }
    int bins = 20;
    int[] counts = new int[bins];
    for (double t : scaled) {
      counts[Math.min(bins - 1, (int) (t * bins))]++;
    }
    Integer[] order = new Integer[bins];
    for (int b = 0; b < bins; b++) {
      order[b] = b;
    }
    // the fullest bins first; of two equally full ones the higher
    java.util.Arrays.sort(order, (a, b) -> counts[b] != counts[a]
        ? Integer.compare(counts[b], counts[a])
        : Integer.compare(b, a));
    boolean[] frequent = new boolean[bins];
    int covered = 0;
    for (int b : order) {
      if (scaled.isEmpty() || covered >= 0.8 * scaled.size()) {
        break;
      }
      frequent[b] = true;
      covered += counts[b];
    }
    IASTAppendable pieces = F.ListAlloc();
    for (int b = 0; b < bins; b++) {
      if (!frequent[b]) {
        continue;
      }
      int end = b;
      while (end + 1 < bins && frequent[end + 1]) {
        end++;
      }
      double from = (double) b / bins;
      double to = (end + 1.0) / bins;
      IExpr upper = end + 1 == bins ? F.LessEqual(F.Slot1, F.num(to)) : F.Less(F.Slot1, F.num(to));
      pieces.append(F.List(F.num(FREQUENT_OPACITY),
          F.And(F.LessEqual(F.num(from), F.Slot1), upper)));
      b = end;
    }
    return pieces.argSize() == 0 ? F.num(OPACITY) : F.Piecewise(pieces, F.num(OPACITY));
  }

  private static double clamp(double v) {
    return Math.max(0.0, Math.min(1.0, v));
  }

  // ---------------------------------------------------------------- slice surfaces

  /**
   * A plot on slice surfaces: the surfaces as triangles, with the value of the function at their
   * vertices.
   */
  private abstract static class SlicePlot extends VolumePlot {

    @Override
    int rangeOffset() {
      return 2;
    }

    @Override
    IExpr plot(IAST ast, int argSize, Box box, EvalEngine engine) {
      GraphicsComplexBuilder builder = sliceSurfaces(ast, box, engine);
      if (builder == null) {
        return F.NIL;
      }
      int count = builder.vertexCount();
      double[] values = new double[count];
      double[] range = sampleVertices(builder, box, values);
      if (range == null) {
        return F.NIL;
      }
      double low = range[0];
      double high = range[1];
      return slice(ast, argSize, box, builder, values, low, high, engine);
    }

    /**
     * The picture of the slices.
     *
     * @param values the value at each vertex of <code>builder</code>, <code>NaN</code> where there
     *        is none
     * @param low the smallest value
     * @param high the largest value
     */
    abstract IExpr slice(IAST ast, int argSize, Box box, GraphicsComplexBuilder builder,
        double[] values, double low, double high, EvalEngine engine);
  }

  /** <code>SliceDensityPlot3D(f, surfaces, {x, ...}, {y, ...}, {z, ...})</code>. */
  public static final class SliceDensityPlot3D extends SlicePlot {

    @Override
    ISymbol topHead() {
      return S.SliceDensityPlot3D;
    }

    @Override
    IExpr slice(IAST ast, int argSize, Box box, GraphicsComplexBuilder builder, double[] values,
        double low, double high, EvalEngine engine) {
      int count = values.length;
      ValueColors colors = new ValueColors(ast, S.SliceDensityPlot3D, box, low, high, engine);
      IASTAppendable vertexColors = F.ListAlloc(count);
      for (int v = 0; v < count; v++) {
        double[] p = builder.coordinatesOf(v + 1);
        vertexColors.append(Double.isFinite(values[v]) ? colors.color(p[0], p[1], p[2], values[v])
            : F.GrayLevel(F.num(0.5)));
        if (v % 256 == 0) {
          engine.checkTimeLimit();
        }
      }
      builder.setVertexColors(vertexColors);
      builder.setStyle(F.EdgeForm(S.None));
      // the rim of the slices
      IExpr rim = builder.openEdgeLine();
      if (rim.isPresent()) {
        builder.addPrimitive(F.List(BOUNDARY_STYLE, rim));
      }
      return Plot3DTools.graphics3D(F.List(builder.build()), ast, argSize,
          Plot3DTools.cubeDefaults(box.min, box.max, true));
    }
  }

  /** <code>SliceContourPlot3D(f, surfaces, {x, ...}, {y, ...}, {z, ...})</code>. */
  public static final class SliceContourPlot3D extends SlicePlot {

    @Override
    ISymbol topHead() {
      return S.SliceContourPlot3D;
    }

    @Override
    IExpr slice(IAST ast, int argSize, Box box, GraphicsComplexBuilder builder, double[] values,
        double low, double high, EvalEngine engine) {
      int count = values.length;
      double[] levels =
          contourLevels(GraphicsOptions.optionValue(ast, S.Contours, S.Automatic), low, high);
      // the band edges: the lowest value, the levels inside the range, the highest value
      double[] edges = new double[levels.length + 2];
      edges[0] = low;
      System.arraycopy(levels, 0, edges, 1, levels.length);
      edges[edges.length - 1] = high;
      ValueColors colors = new ValueColors(ast, S.SliceContourPlot3D, box, low, high, engine);
      int bands = edges.length - 1;
      List<List<double[][]>> bandPolygons = new ArrayList<>(bands);
      for (int b = 0; b < bands; b++) {
        bandPolygons.add(new ArrayList<>());
      }
      // the pieces of the contour line of each level
      List<List<double[][]>> lines = new ArrayList<>(levels.length);
      for (int l = 0; l < levels.length; l++) {
        lines.add(new ArrayList<>());
      }
      for (int t = 1; t <= builder.polygonCount(); t++) {
        int[] corners = builder.polygon(t);
        if (corners.length != 3) {
          continue;
        }
        double[][] triangle = new double[3][];
        boolean defined = true;
        for (int c = 0; c < 3; c++) {
          double[] p = builder.coordinatesOf(corners[c]);
          double v = values[corners[c] - 1];
          defined &= Double.isFinite(v);
          triangle[c] = new double[] {p[0], p[1], p[2], v};
        }
        if (!defined) {
          continue;
        }
        for (int b = 0; b < bands; b++) {
          List<double[]> piece = clip(java.util.Arrays.asList(triangle), edges[b], true);
          piece = clip(piece, edges[b + 1], false);
          if (piece.size() >= 3) {
            bandPolygons.get(b).add(piece.toArray(new double[0][]));
          }
        }
        for (int l = 0; l < levels.length; l++) {
          double[][] segment = isoSegment(triangle, levels[l]);
          if (segment != null) {
            lines.get(l).add(segment);
          }
        }
        if (t % 256 == 0) {
          engine.checkTimeLimit();
        }
      }
      IASTAppendable content = F.ListAlloc(bands + 2);
      content.append(F.EdgeForm(S.None));
      for (int b = 0; b < bands; b++) {
        List<double[][]> polygons = bandPolygons.get(b);
        if (polygons.isEmpty()) {
          continue;
        }
        double mid = (edges[b] + edges[b + 1]) / 2.0;
        IASTAppendable list = F.ListAlloc(polygons.size());
        double[] centroid = new double[3];
        for (double[][] polygon : polygons) {
          IASTAppendable points = F.ListAlloc(polygon.length);
          for (double[] p : polygon) {
            points.append(F.List(F.num(p[0]), F.num(p[1]), F.num(p[2])));
          }
          list.append(points);
          centroid[0] += polygon[0][0] / polygons.size();
          centroid[1] += polygon[0][1] / polygons.size();
          centroid[2] += polygon[0][2] / polygons.size();
        }
        content.append(F.List(colors.color(centroid[0], centroid[1], centroid[2], mid),
            F.Polygon(list)));
      }
      IExpr contourStyle = GraphicsOptions.optionValue(ast, S.ContourStyle, S.Automatic);
      if (!contourStyle.isNone() && !contourStyle.isFalse()) {
        // one line for each level
        IASTAppendable levelLines = F.ListAlloc(levels.length + 1);
        levelLines.append(contourStyle.isAutomatic() ? F.GrayLevel(F.num(0.25)) : contourStyle);
        for (List<double[][]> level : lines) {
          if (level.isEmpty()) {
            continue;
          }
          IASTAppendable segments = F.ListAlloc(level.size());
          for (double[][] segment : level) {
            segments.append(F.List(point3(segment[0]), point3(segment[1])));
          }
          levelLines.append(F.Line(segments));
        }
        if (levelLines.argSize() > 1) {
          content.append(levelLines);
        }
      }
      // the rim of the slices
      IASTAppendable rim = F.ListAlloc();
      for (int[] loop : builder.openEdgeLoops()) {
        IASTAppendable points = F.ListAlloc(loop.length);
        for (int v : loop) {
          points.append(point3(builder.coordinatesOf(v)));
        }
        rim.append(points);
      }
      if (rim.argSize() > 0) {
        content.append(F.List(BOUNDARY_STYLE, F.Line(rim.argSize() == 1 ? rim.arg1() : rim)));
      }
      return Plot3DTools.graphics3D(content, ast, argSize,
          Plot3DTools.cubeDefaults(box.min, box.max, true));
    }
  }

  /**
   * The value of the function at every vertex, into <code>values</code>.
   *
   * @return the smallest and the largest value, or {@code null} when there is none
   */
  private static double[] sampleVertices(GraphicsComplexBuilder builder, Box box,
      double[] values) {
    double low = Double.POSITIVE_INFINITY;
    double high = Double.NEGATIVE_INFINITY;
    for (int v = 0; v < values.length; v++) {
      double[] p = builder.coordinatesOf(v + 1);
      values[v] = box.value(p[0], p[1], p[2]);
      if (Double.isFinite(values[v])) {
        low = Math.min(low, values[v]);
        high = Math.max(high, values[v]);
      }
    }
    return low <= high ? new double[] {low, high} : null;
  }

  /** The colour of the rim of the slice surfaces. */
  private static final IAST BOUNDARY_STYLE = F.GrayLevel(F.num(0.3));

  private static IAST point3(double[] p) {
    return F.List(F.num(p[0]), F.num(p[1]), F.num(p[2]));
  }

  /**
   * The contour levels inside <code>low ... high</code>: <code>Automatic</code> about ten round
   * values, <code>n</code> that many evenly spaced ones, a list those of its values which are
   * numbers.
   */
  static double[] contourLevels(IExpr spec, double low, double high) {
    if (!(high > low)) {
      return new double[0];
    }
    if (spec.isList()) {
      List<Double> levels = new ArrayList<>();
      for (IExpr level : (IAST) spec) {
        double v = (level.isList() && level.argSize() >= 1 ? level.first() : level).evalfNaN();
        if (Double.isFinite(v) && v > low && v < high) {
          levels.add(v);
        }
      }
      java.util.Collections.sort(levels);
      return levels.stream().mapToDouble(Double::doubleValue).toArray();
    }
    int n = spec.toIntDefault(-1);
    if (n > 0) {
      double[] levels = new double[n];
      for (int i = 0; i < n; i++) {
        levels[i] = low + (high - low) * (i + 1) / (n + 1);
      }
      return levels;
    }
    return TickGenerator.interiorDivisions(low, high, 10);
  }

  /**
   * The part of a polygon whose values are at least (<code>above</code>) or at most
   * <code>level</code>; the corners are <code>{x, y, z, value}</code> and new corners are put where
   * the value, linear along an edge, crosses the level.
   */
  static List<double[]> clip(List<double[]> polygon, double level, boolean above) {
    List<double[]> out = new ArrayList<>();
    int n = polygon.size();
    for (int i = 0; i < n; i++) {
      double[] a = polygon.get(i);
      double[] b = polygon.get((i + 1) % n);
      boolean aIn = above ? a[3] >= level : a[3] <= level;
      boolean bIn = above ? b[3] >= level : b[3] <= level;
      if (aIn) {
        out.add(a);
      }
      if (aIn != bIn) {
        double t = (level - a[3]) / (b[3] - a[3]);
        out.add(new double[] {a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1]),
            a[2] + t * (b[2] - a[2]), level});
      }
    }
    return out;
  }

  /** The piece of the contour line at <code>level</code> across a triangle, or null. */
  private static double[][] isoSegment(double[][] triangle, double level) {
    List<double[]> points = new ArrayList<>(2);
    for (int i = 0; i < 3; i++) {
      double[] a = triangle[i];
      double[] b = triangle[(i + 1) % 3];
      if ((a[3] < level) != (b[3] < level)) {
        double t = (level - a[3]) / (b[3] - a[3]);
        points.add(new double[] {a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1]),
            a[2] + t * (b[2] - a[2])});
      }
    }
    return points.size() == 2 ? new double[][] {points.get(0), points.get(1)} : null;
  }

  /**
   * The triangles of the slice surfaces the second argument of a slice plot names.
   *
   * @return the surfaces, or {@code null} after a message when the specification names none
   */
  private static GraphicsComplexBuilder sliceSurfaces(IAST ast, Box box, EvalEngine engine) {
    int points = Plot3DTools.plotPoints(GraphicsOptions.optionValue(ast, S.PlotPoints,
        S.Automatic), SLICE_POINTS, MAX_SLICE_POINTS)[0];
    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
    if (!addSurfaces(builder, ast.arg2(), box, points, engine) || builder.polygonCount() == 0) {
      // Slice specification `1` should be a named slice, equation, surface or volume region or
      // list of slices.
      Errors.printMessage(ast.topHead(), "slice", F.list(ast.arg2()), engine);
      return null;
    }
    return builder;
  }

  private static boolean addSurfaces(GraphicsComplexBuilder builder, IExpr spec, Box box,
      int points, EvalEngine engine) {
    if (spec.isString()) {
      return addNamed(builder, spec.toString(), F.NIL, box, points);
    }
    if (spec.isList()) {
      IAST list = (IAST) spec;
      if (list.argSize() == 2 && list.arg1().isString()) {
        return addNamed(builder, list.arg1().toString(), list.arg2(), box, points);
      }
      boolean all = list.argSize() > 0;
      for (IExpr item : list) {
        all &= addSurfaces(builder, item, box, points, engine);
      }
      return all;
    }
    IExpr g = levelFunction(spec, box);
    if (g.isNIL()) {
      return false;
    }
    int axis = axisPlane(g, box);
    if (axis >= 0) {
      double c = planeConstant(g, box, axis);
      if (Double.isFinite(c)) {
        addPlane(builder, box, axis, c, points);
        return true;
      }
    }
    // a curved surface: the zero set of g, found by marching cubes
    int n = Math.max(3, Math.min(points, MAX_IMPLICIT_POINTS));
    double[][][] grid = MarchingCubes.sample(g, box.vars, box.min, box.max, F.NIL, n, engine,
        null);
    if (grid == null) {
      return false;
    }
    MarchingCubes.marchingCubes(builder, grid, 0.0, box.min, box.max, n);
    return true;
  }

  /**
   * The function whose zero set a surface is: <code>lhs - rhs</code> for an equation, the squared
   * distance minus the squared radius for a sphere or ball, the distance along the normal for an
   * infinite plane.
   */
  private static @AllowNIL IExpr levelFunction(IExpr spec, Box box) {
    IAST point = F.List(box.vars[0], box.vars[1], box.vars[2]);
    if (spec.isAST(S.Equal, 3)) {
      return F.Subtract(spec.first(), spec.second());
    }
    if (spec.isAST(S.Sphere) || spec.isAST(S.Ball)) {
      IExpr center = spec.argSize() >= 1 ? spec.first() : F.List(F.C0, F.C0, F.C0);
      IExpr radius = spec.argSize() >= 2 ? spec.second() : F.C1;
      if (!center.isList() || center.argSize() != 3) {
        return F.NIL;
      }
      return F.Subtract(F.Dot(F.Subtract(point, center), F.Subtract(point, center)),
          F.Sqr(radius));
    }
    if (spec.isAST(S.InfinitePlane) && spec.argSize() >= 1) {
      IExpr normal;
      IExpr p;
      if (spec.argSize() == 1 && spec.first().isList() && spec.first().argSize() == 3) {
        IAST pts = (IAST) spec.first();
        p = pts.arg1();
        normal = F.Cross(F.Subtract(pts.arg2(), pts.arg1()), F.Subtract(pts.arg3(), pts.arg1()));
      } else if (spec.argSize() == 2 && spec.second().isList() && spec.second().argSize() == 2) {
        p = spec.first();
        normal = F.Cross(spec.second().first(), spec.second().second());
      } else {
        return F.NIL;
      }
      return F.Dot(normal, F.Subtract(point, p));
    }
    return F.NIL;
  }

  /**
   * The axis a surface <code>g == 0</code> is a plane across, when <code>g</code> depends on one
   * variable alone and linearly; <code>-1</code> otherwise.
   */
  private static int axisPlane(IExpr g, Box box) {
    int axis = -1;
    for (int a = 0; a < 3; a++) {
      if (!g.isFree(box.vars[a])) {
        if (axis >= 0) {
          return -1;
        }
        axis = a;
      }
    }
    return axis >= 0 && g.isPolynomial(box.vars[axis]) && polynomialDegree(g, box.vars[axis]) == 1
        ? axis
        : -1;
  }

  private static long polynomialDegree(IExpr g, ISymbol variable) {
    IExpr degree = EvalEngine.get().evaluate(F.Exponent(g, variable));
    return degree.toLongDefault(-1L);
  }

  /** Where the linear function <code>g</code> of the variable of <code>axis</code> is zero. */
  private static double planeConstant(IExpr g, Box box, int axis) {
    EvalEngine engine = EvalEngine.get();
    double g0 = engine.evaluate(F.subst(g, box.vars[axis], F.C0)).evalfNaN();
    double g1 = engine.evaluate(F.subst(g, box.vars[axis], F.C1)).evalfNaN();
    double slope = g1 - g0;
    return slope != 0 && Double.isFinite(slope) ? -g0 / slope : Double.NaN;
  }

  /** The named plane sets, with the count or the positions of a stacked set. */
  private static boolean addNamed(GraphicsComplexBuilder builder, String name, IExpr argument,
      Box box, int points) {
    switch (name) {
      case "CenterPlanes":
        for (int a = 0; a < 3; a++) {
          addPlane(builder, box, a, box.center(a), points);
        }
        return true;
      case "BackPlanes":
        // the three faces of the box away from the default view point
        addPlane(builder, box, 0, box.min[0], points);
        addPlane(builder, box, 1, box.max[1], points);
        addPlane(builder, box, 2, box.min[2], points);
        return true;
      case "XStackedPlanes":
      case "YStackedPlanes":
      case "ZStackedPlanes": {
        int axis = name.charAt(0) - 'X';
        for (double c : stackedPositions(argument, box, axis)) {
          addPlane(builder, box, axis, c, points);
        }
        return true;
      }
      default:
        return false;
    }
  }

  /**
   * The positions of stacked planes: <code>n</code> planes evenly inside the box, or the listed
   * positions.
   */
  private static double[] stackedPositions(IExpr argument, Box box, int axis) {
    if (argument.isList()) {
      IAST list = (IAST) argument;
      double[] positions = new double[list.argSize()];
      for (int i = 0; i < positions.length; i++) {
        positions[i] = list.get(i + 1).evalfNaN();
      }
      return positions;
    }
    int n = argument.isPresent() ? argument.toIntDefault(STACKED_PLANES) : STACKED_PLANES;
    n = Math.max(1, n);
    double[] positions = new double[n];
    for (int i = 0; i < n; i++) {
      positions[i] = box.min[axis] + (box.max[axis] - box.min[axis]) * (i + 1) / (n + 1);
    }
    return positions;
  }

  /** A plane across <code>axis</code> at <code>c</code>, as a grid of triangles. */
  private static void addPlane(GraphicsComplexBuilder builder, Box box, int axis, double c,
      int points) {
    if (!(c >= box.min[axis] && c <= box.max[axis])) {
      return;
    }
    int a = axis == 0 ? 1 : 0;
    int b = axis == 2 ? 1 : 2;
    int n = Math.max(2, points);
    int[][] index = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double[] p = new double[3];
        p[axis] = c;
        p[a] = box.min[a] + (box.max[a] - box.min[a]) * i / (n - 1);
        p[b] = box.min[b] + (box.max[b] - box.min[b]) * j / (n - 1);
        index[i][j] = builder.addVertex(p[0], p[1], p[2], null, null);
      }
    }
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - 1; j++) {
        builder.addPolygon(index[i][j], index[i + 1][j], index[i + 1][j + 1]);
        builder.addPolygon(index[i][j], index[i + 1][j + 1], index[i][j + 1]);
      }
    }
  }
}
