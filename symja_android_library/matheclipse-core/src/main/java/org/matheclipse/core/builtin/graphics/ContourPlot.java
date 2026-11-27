package org.matheclipse.core.builtin.graphics;

import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.graphics.PlotShapeProbe;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionClip;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating 2D contour plots with ContourShading, ContourStyle, and Scaling options.
 * 
 */
public class ContourPlot extends ListPlot {

  /**
   * The data is drawn as one field rather than point by point, so no wrapper is read here; the
   * label goes over the whole picture instead.
   */
  @Override
  protected boolean readsArgumentWrapper() {
    return false;
  }

  public ContourPlot() {}

  /**
   * <code>ContourPlot(f, Element({x, y}, region))</code>: the plot over the bounding box of the
   * region, restricted to the region by a region function.
   */
  private static IExpr overRegion(IAST ast, EvalEngine engine) {
    IExpr variables = ast.arg2().first();
    IExpr region = engine.evaluate(ast.arg2().second());
    if (!variables.isList2() || !variables.first().isVariable()
        || !variables.second().isVariable()) {
      return F.NIL;
    }
    IExpr bounds = engine.evaluate(F.unaryAST1(S.RegionBounds, region));
    if (!bounds.isList2() || !bounds.first().isList2() || !bounds.second().isList2()) {
      return F.NIL;
    }
    IASTAppendable plot = F.ast(S.ContourPlot);
    plot.append(ast.arg1());
    plot.append(F.List(variables.first(), bounds.first().first(), bounds.first().second()));
    plot.append(F.List(variables.second(), bounds.second().first(), bounds.second().second()));
    for (int i = 3; i < ast.size(); i++) {
      plot.append(ast.get(i));
    }
    plot.append(F.Rule(S.RegionFunction, F.Function(variables, F.RegionMember(region, variables))));
    return plot;
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize >= 2 && ast.arg2().isAST(S.Element, 3)) {
      return overRegion(originalAST, engine);
    }
    if (PlotEndpoints.degenerate(S.ContourPlot, ast, 2, 3, false, engine)) {
      return F.NIL;
    }
    // the shape tests below read the data through any display wrapper; `wrappedAST` keeps the
    // wrapper, so the label can still be put over the finished picture
    final IAST wrappedAST = ast;
    ast = PlotWrapper.stripFirstArgument(ast);
    if (argSize < 3) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // ContourLines draws the level curves themselves; without them only the shading is left.
    // ContourLabels writes each level's value onto its curve.
    boolean contourLines =
        !GraphicsOptions.optionValue(originalAST, S.ContourLines, S.True).isFalse();
    IExpr contourLabelsOption = GraphicsOptions.optionValue(originalAST, S.ContourLabels, S.None);
    boolean contourLabels = contourLabelsOption.isTrue() || contourLabelsOption.isAutomatic();

    IExpr plotPointsOption = S.Automatic;
    int maxRecursion = -1;
    // -1: Automatic, levels at round values
    int numberOfContours = -1;
    // the levels Contours -> {c1, c2, ...} asks for, or null
    ExplicitContours explicitLevels = null;
    IExpr contourStyle = S.Automatic;
    IExpr contourShading = S.Automatic;

    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    GraphicsOptions.MatrixPlotOptions matrixOptions =
        GraphicsOptions.readMatrixPlotOptions(originalAST);
    boolean colorFunctionScaling = matrixOptions.colorFunctionScaling;
    IExpr colorFunctionOpt = matrixOptions.colorFunction;
    IExpr meshOpt = matrixOptions.mesh;
    // CPD-OFF contour and density plots sample their own grids
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();

        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.Contours:
            // CPD-ON
              if (val.isInteger()) {
                // a negative count draws no contours, like zero; -1 stands for Automatic
                numberOfContours = Math.max(0, val.toIntDefault(10));
              } else if (val.isList()) {
                explicitLevels = explicitLevels((IAST) val, engine);
              }
              break;
            case ID.ContourStyle:
              contourStyle = val;
              break;
            case ID.ContourShading:
              contourShading = val;
              break;
            case ID.PlotPoints:
              plotPointsOption = val;
              break;
            case ID.MaxRecursion:
              maxRecursion = val.toIntDefault(-1);
              break;
          }
        }
      }
    }

    int[] cells = gridCells(plotPointsOption, maxRecursion);

    IExpr functionArg = ast.arg1();
    IExpr xIter = ast.arg2();
    IExpr yIter = ast.arg3();

    double[] xRange = parseRange(xIter, engine);
    if (xRange == null || !xIter.isList() || !xIter.first().isSymbol()) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.ContourPlot, "pllim", F.list(xIter), engine);
    }
    double[] yRange = parseRange(yIter, engine);
    if (yRange == null || !yIter.isList() || !yIter.first().isSymbol()) {
      // Range specification `1` is not of the form {y, ymin, ymax}.
      // CPD-OFF contour and density plots sample their own grids
      return Errors.printMessage(S.ContourPlot, "pllim", F.list(yIter), engine);
    }

    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});

    ISymbol xVar = (ISymbol) ((IAST) xIter).arg1();
    ISymbol yVar = (ISymbol) ((IAST) yIter).arg1();

    // CPD-ON
    IASTAppendable primitives = F.ListAlloc();

    RegionFunctionFilter region = RegionFunctionFilter.of(
        GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic), engine);
    // PlotRange -> {zmin, zmax} leaves the parts whose values lie outside it blank, cut along the
    // level of the bound the way a region is cut
    // an equation is drawn where its two sides are equal; the window is not about its values
    double[] clipRange =
        DensityPlot.valueRange(GraphicsOptions.optionValue(originalAST, S.PlotRange, S.Automatic));
    RegionFunctionFilter valueRegion = clipRange == null ? region
        : RegionFunctionFilter.withValueWindow(region, 2, clipRange[0], clipRange[1], engine);
    IExpr boundaryStyle = GraphicsOptions.optionValue(originalAST, S.BoundaryStyle, S.Automatic);

    // f(x, y) with a value of {u v, u + v} is two functions, which only its value can say. A single
    // function is first read at the sample nearest the middle of the grid, and a number there is
    // kept as that sample, so that the function is not evaluated more often than the grid has
    // points.
    KnownSample middle = KnownSample.of(functionArg, xVar, yVar, xRange, yRange, cells, engine);
    if (middle == null) {
      List<IExpr> written =
          functionArg.isList() ? ((IAST) functionArg).asArgsList() : List.of(functionArg);
      List<IExpr> split = PlotShapeProbe.split(written,
          PlotShapeProbe.rangeProbes(new IExpr[] {xVar, yVar},
              new double[] {xRange[0], yRange[0]}, new double[] {xRange[1], yRange[1]}),
          PlotShapeProbe.SCALAR, false, engine);
      if (split.size() != written.size()) {
        functionArg = F.List(split.toArray(new IExpr[0]));
      }
    }

    boolean shaded = false;
    // the least and the greatest value of the shaded functions, for a legend of their colours
    final double[] valueRange = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    if (functionArg.isList()) {
      // Multiple functions/equations; with an equation among them the functions are not shaded
      // unless a ContourShading other than Automatic is given, the same as for a single equation
      IAST list = (IAST) functionArg;
      IExpr listShading = contourShading == S.Automatic && list.exists(f -> f.isAST(S.Equal, 3))
          ? S.None
          : contourShading;
      int count = 0;
      for (int i = 1; i < list.size(); i++) {
        IExpr func = list.get(i);
        shaded |= isShaded(func, listShading);

        // Determine style for this specific curve
        IExpr style = contourStyle;
        if (style.isList()) {
          style = GraphicsOptions.getPlotStyle(style, count);
        } else if (style.isAutomatic()) {
          // If Automatic, cycle colors for multiple equations
          style = GraphicsOptions.plotStyleColorExpr(count, F.NIL);
        }

        generateContours(primitives, func, xRange, yRange, xVar, yVar, cells, null,
            numberOfContours, explicitLevels,
            style, listShading, colorFunctionScaling, colorFunctionOpt, engine, true,
            contourLines, contourLabels, func.isAST(S.Equal, 3) ? region : valueRegion,
            boundaryStyle, valueRange);
        count++;
      }
    } else {
      // Single function/equation
      shaded = isShaded(functionArg, contourShading);
      generateContours(primitives, functionArg, xRange, yRange, xVar, yVar, cells, middle,
          numberOfContours, explicitLevels, contourStyle, contourShading, colorFunctionScaling, colorFunctionOpt,
          engine, false, contourLines, contourLabels,
          functionArg.isAST(S.Equal, 3) ? region : valueRegion, boundaryStyle, valueRange);
    }

    // the contours share one table of points; the mesh, which is not traced from the data, is
    // drawn next to them
    IExpr meshLines = GraphicsOptions.meshGrid(meshOpt, xRange[0], yRange[0], xRange[1], yRange[1],
        cells[0], cells[1]);
    // a plot without a point to draw has two empty lists
    IAST complex = graphicsComplex(primitives, xRange, yRange);
    IAST content = F.List(complex.arg1().argSize() > 0 ? complex : F.CEmptyList,
        meshLines.isPresent() ? meshLines : F.CEmptyList);

    IExpr legends = GraphicsOptions.optionValue(originalAST, S.PlotLegends, S.None);
    if ((legends == S.Automatic || legends.isTrue()) && shaded
        && valueRange[0] <= valueRange[1]) {
      // a bar of the colours the regions between the contours are shaded in, over their values
      graphicsOptions.setPlotLegends(F.BarLegend(F.List(
          DensityPlot.legendColorFunction(colorFunctionOpt),
          F.List(F.num(valueRange[0]), F.num(valueRange[1])))));
    }

    IExpr graphics = createGraphicsFunction(content, graphicsOptions, wrappedAST);
    if (clipRange != null) {
      // the range of the values isn't the range of the picture, which is the box of x and y
      graphics = DensityPlot.withPlotRange(graphics, F.List(F.List(F.num(xRange[0]), F.num(xRange[1])),
          F.List(F.num(yRange[0]), F.num(yRange[1]))));
    }
    if (!shaded
        && GraphicsOptions.optionValue(originalAST, S.PlotRange, S.Automatic).isAutomatic()) {
      // only curves are drawn, which need not reach the edges of the box of x and y they were
      // traced in; the box is still what the picture shows
      graphics = withPlotRange(graphics, originalAST, F.List(F.num(xRange[0]), F.num(xRange[1])),
          true, F.List(F.num(yRange[0]), F.num(yRange[1])), true);
    }
    return withAutomaticFrameLabel(graphics, originalAST, xVar, yVar);
  }

  /**
   * The plot with <code>FrameLabel -> Automatic</code> resolved: the bottom and left edges of the
   * frame are labelled with the variables of the two ranges, written edge by edge as
   * <code>{{left, right}, {bottom, top}}</code>.
   */
  static IExpr withAutomaticFrameLabel(IExpr graphics, IAST originalAST, ISymbol xVar,
      ISymbol yVar) {
    if (originalAST == null) {
      return graphics;
    }
    IExpr frameLabel = GraphicsOptions.optionValue(originalAST, S.FrameLabel, F.NIL);
    if (frameLabel.isNIL()) {
      // the default of the plot function, which SetOptions may have changed
      IExpr defaults = EvalEngine.get().evaluate(F.Options(originalAST.topHead()));
      frameLabel = defaults.isList()
          ? GraphicsOptions.optionValue((IAST) defaults, S.FrameLabel, S.None)
          : S.None;
    }
    if (!frameLabel.isAutomatic()) {
      return graphics;
    }
    IAST inner = innerGraphics(graphics);
    if (inner.isNIL()) {
      return graphics;
    }
    IAST labelled = withOption(inner, S.FrameLabel,
        F.List(F.List(yVar, S.None), F.List(xVar, S.None)));
    return graphics.isAST(S.Legended, 3) ? ((IAST) graphics).setAtCopy(1, labelled) : labelled;
  }

  /**
   * The number of grid cells along x and y.
   *
   * <p>
   * The contours are traced on a fixed grid rather than refined adaptively. Without a
   * <code>MaxRecursion</code> setting <code>PlotPoints -> n</code> is the number of cells, 25 by
   * default. With <code>MaxRecursion -> r</code> the grid is the one an adaptive refinement starts
   * from, <code>n</code> samples per axis (15 by default), with every cell split in two along each
   * axis for each of the <code>r</code> levels; <code>MaxRecursion -> 0</code> samples exactly the
   * <code>n</code> by <code>n</code> points.
   *
   * @param option the value of <code>PlotPoints</code>: a number, a list of two numbers or
   *        <code>Automatic</code>
   * @param maxRecursion the value of <code>MaxRecursion</code>, negative if not given
   */
  static int[] gridCells(IExpr option, int maxRecursion) {
    boolean refined = maxRecursion >= 0;
    int defaultPoints = refined ? 15 : 25;
    int[] cells = new int[2];
    for (int c = 0; c < 2; c++) {
      IExpr entry = option.isList2() ? option.getAt(c + 1) : option;
      int points = entry.toIntDefault(defaultPoints);
      if (points < 2) {
        points = defaultPoints;
      }
      if (refined) {
        cells[c] = Math.min(400, (points - 1) * (1 << Math.min(maxRecursion, 4)));
      } else {
        cells[c] = Math.min(400, points);
      }
    }
    return cells;
  }

  /** A sample of the grid which was evaluated before the grid was sampled. */
  private static final class KnownSample {
    final IExpr function;
    final int column;
    final int row;
    final IExpr value;

    private KnownSample(IExpr function, int column, int row, IExpr value) {
      this.function = function;
      this.column = column;
      this.row = row;
      this.value = value;
    }

    /**
     * The value of a single function at the sample nearest the middle of the grid, if it is a
     * number; <code>null</code> otherwise.
     */
    static KnownSample of(IExpr function, ISymbol xVar, ISymbol yVar, double[] xRange,
        double[] yRange, int[] cells, EvalEngine engine) {
      if (function.isList() || function.isAST(S.Equal, 3) || PlotWrapper.isWrapper(function)) {
        return null;
      }
      int column = cells[0] / 2;
      int row = cells[1] / 2;
      // the same coordinates as the grid computes them
      double x = xRange[0] + column * ((xRange[1] - xRange[0]) / cells[0]);
      double y = yRange[0] + row * ((yRange[1] - yRange[0]) / cells[1]);
      IExpr value;
      try {
        value = engine.evaluate(
            function.replaceAll(F.List(F.Rule(xVar, F.num(x)), F.Rule(yVar, F.num(y)))));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return null;
      }
      if (value.isList() || !Double.isFinite(value.evalfNaN())) {
        return null;
      }
      return new KnownSample(function, column, row, value);
    }
  }

  /**
   * Whether the bands between the contours of <code>function</code> are filled: an equation is
   * not shaded unless <code>ContourShading</code> asks for it.
   */
  private static boolean isShaded(IExpr function, IExpr contourShading) {
    if (function.isAST(S.Equal, 3) && contourShading == S.Automatic) {
      return false;
    }
    return contourShading != S.None && !contourShading.isFalse();
  }

  /**
   * The contour primitives as a <code>GraphicsComplex</code>: every point of a <code>Line</code>,
   * a <code>Polygon</code> or the position of a <code>Text</code> is replaced by its index in one
   * table of points, which the cells that meet at a point share.
   *
   * @param xRange the plotted range of x; points closer than 2^-24 of it are the same point
   * @param yRange the plotted range of y
   */
  static IAST graphicsComplex(IAST primitives, double[] xRange, double[] yRange) {
    IASTAppendable points = F.ListAlloc();
    java.util.Map<Long, Integer> indexOf = new java.util.HashMap<>();
    IExpr data = indexPrimitives(primitives, xRange, yRange, points, indexOf);
    return F.binaryAST2(S.GraphicsComplex, points, data);
  }

  private static IExpr indexPrimitives(IExpr expr, double[] xRange, double[] yRange,
      IASTAppendable points, java.util.Map<Long, Integer> indexOf) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (IExpr x : list) {
        IExpr indexed = indexPrimitives(x, xRange, yRange, points, indexOf);
        if (indexed.isPresent()) {
          result.append(indexed);
        }
      }
      return result;
    }
    if ((expr.isAST(S.Line, 2) || expr.isAST(S.Polygon, 2) || expr.isAST(S.Tooltip))
        && expr.argSize() >= 1) {
      IExpr first = expr.isAST(S.Tooltip)
          ? indexPrimitives(expr.first(), xRange, yRange, points, indexOf)
          : indexPoints(expr.first(), xRange, yRange, points, indexOf);
      if (!expr.isAST(S.Tooltip) && first.isList() && first.first().isInteger()) {
        // points of a piece which became one point of the table: a line which shrank to a point,
        // a polygon which shrank to a line, is left out
        first = withoutRepeatedIndices((IAST) first, expr.isAST(S.Line) ? 2 : 3);
        if (first.isNIL()) {
          return F.NIL;
        }
      }
      return ((IAST) expr).setAtCopy(1, first);
    }
    if (expr.isAST(S.Text) && expr.argSize() >= 2) {
      return ((IAST) expr).setAtCopy(2,
          indexPoints(expr.second(), xRange, yRange, points, indexOf));
    }
    return expr;
  }

  /**
   * The indices with repeated neighbours left out, or {@link F#NIL} if fewer than
   * <code>least</code> distinct ones remain.
   */
  private static IExpr withoutRepeatedIndices(IAST indices, int least) {
    IASTAppendable distinct = F.ListAlloc(indices.argSize());
    for (IExpr i : indices) {
      if (distinct.argSize() == 0 || !distinct.last().equals(i)) {
        distinct.append(i);
      }
    }
    if (least > 2) {
      // a polygon is closed: its last point meets its first
      while (distinct.argSize() > 1 && distinct.last().equals(distinct.arg1())) {
        distinct.remove(distinct.argSize());
      }
    }
    return distinct.argSize() >= least ? distinct : F.NIL;
  }

  /** A point, or a nested list of points, with every point replaced by its index. */
  private static IExpr indexPoints(IExpr expr, double[] xRange, double[] yRange,
      IASTAppendable points, java.util.Map<Long, Integer> indexOf) {
    if (expr.isList2() && expr.first().isReal() && expr.second().isReal()) {
      // points which agree to 2^-24 of the plotted range are the same point: two neighbouring
      // cells compute the place where a contour crosses their common edge each from its own corner
      long column = gridStep(expr.first().evalf(), xRange);
      long row = gridStep(expr.second().evalf(), yRange);
      long key = (column << 32) ^ (row & 0xffffffffL);
      Integer index = indexOf.get(key);
      if (index == null) {
        points.append(expr);
        index = points.argSize();
        indexOf.put(key, index);
      }
      return F.ZZ(index);
    }
    if (expr.isList()) {
      return ((IAST) expr).map(x -> indexPoints(x, xRange, yRange, points, indexOf), 1);
    }
    return expr;
  }

  /** The position of <code>value</code> in <code>range</code>, in steps of 2^-24 of its width. */
  private static long gridStep(double value, double[] range) {
    double width = range[1] - range[0];
    double scaled = width > 0.0 ? (value - range[0]) / width : value;
    return Math.round(scaled * (1 << 24));
  }

  /**
   * The levels of <code>Contours -&gt; {c1, c2, ...}</code> or <code>{{c1, style1}, ...}</code>.
   */
  static final class ExplicitContours {
    /** the levels in increasing order */
    final double[] sorted;
    /** the style of each level in {@link #sorted}, or {@link F#NIL} where none is given */
    final IExpr[] styles;

    ExplicitContours(double[] levels, IExpr[] levelStyles) {
      Integer[] order = new Integer[levels.length];
      for (int i = 0; i < order.length; i++) {
        order[i] = i;
      }
      // a stable sort: of a level given more than once the first entry comes first
      java.util.Arrays.sort(order, (a, b) -> Double.compare(levels[a], levels[b]));
      double[] distinct = new double[levels.length];
      IExpr[] distinctStyles = new IExpr[levels.length];
      int count = 0;
      for (int i = 0; i < order.length; i++) {
        double level = levels[order[i]];
        IExpr style = levelStyles[order[i]];
        if (count > 0 && distinct[count - 1] == level) {
          // a level is drawn once, in the first style given for it
          if (distinctStyles[count - 1].isNIL()) {
            distinctStyles[count - 1] = style;
          }
          continue;
        }
        distinct[count] = level;
        distinctStyles[count++] = style;
      }
      this.sorted = java.util.Arrays.copyOf(distinct, count);
      this.styles = java.util.Arrays.copyOf(distinctStyles, count);
    }
  }

  /**
   * The distance below which two end points of contour pieces along one axis are the same point:
   * a tiny part of a sampling step, but no less than a few units in the last place of the
   * coordinates, which far from the origin are coarser than that.
   */
  private static double joinTolerance(double step, double[] range) {
    double magnitude = Math.max(Math.abs(range[0]), Math.abs(range[1]));
    return Math.max(1.0e-9 * step, 64 * Math.ulp(magnitude));
  }

  /**
   * The values of <code>Contours -&gt; {c1, c2, ...}</code> or <code>{{c1, style1}, ...}</code>;
   * values which aren't real numbers are left out.
   */
  private static ExplicitContours explicitLevels(IAST list, EvalEngine engine) {
    double[] values = new double[list.argSize()];
    IExpr[] styles = new IExpr[list.argSize()];
    int count = 0;
    for (IExpr entry : list) {
      IExpr value = entry;
      IExpr style = F.NIL;
      if (entry.isList() && entry.argSize() >= 1) {
        value = entry.first();
        if (entry.argSize() == 2) {
          style = entry.second();
        } else if (entry.argSize() > 2) {
          style = ((IAST) entry).rest().setAtCopy(0, S.Directive);
        }
      }
      double level = engine.evalQuiet(F.N(value)).evalfNaN();
      if (Double.isFinite(level)) {
        styles[count] = style;
        values[count++] = level;
      }
    }
    return new ExplicitContours(java.util.Arrays.copyOf(values, count),
        java.util.Arrays.copyOf(styles, count));
  }

  private void generateContours(IASTAppendable primitives, IExpr function, double[] xRange,
      double[] yRange, ISymbol xVar, ISymbol yVar, int[] cells, KnownSample known,
      int numberOfContours, ExplicitContours explicitLevels,
      IExpr contourStyle, IExpr contourShading, boolean colorFunctionScaling,
      IExpr colorFunctionOpt, EvalEngine engine, boolean isMulti, boolean contourLines,
      boolean contourLabels, RegionFunctionFilter region, IExpr boundaryStyle,
      double[] valueRange) {
    // an equation is not shaded unless the shading is specified
    final boolean shaded = isShaded(function, contourShading);
    boolean isEquation = false;
    if (function.isAST(S.Equal, 3)) {
      isEquation = true;
      function = F.Subtract(function.first(), function.second());
    }

    // CPD-OFF contour and density plots sample their own grids
    int gridX = cells[0];
    int gridY = cells[1];
    double[][] zGrid = new double[gridX + 1][gridY + 1];
    double stepX = (xRange[1] - xRange[0]) / gridX;
    double stepY = (yRange[1] - yRange[0]) / gridY;

    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;
    // CPD-ON

    // 1. Compute Grid
    boolean[][] defined = new boolean[gridX + 1][gridY + 1];
    // CPD-OFF grid sampling kept per plot, the numeric evaluation differs
    boolean[][] inside = new boolean[gridX + 1][gridY + 1];
    for (int i = 0; i <= gridX; i++) {
      double xVal = xRange[0] + i * stepX;
      for (int j = 0; j <= gridY; j++) {
        double yVal = yRange[0] + j * stepY;
        double z = Double.NaN;
        try {
          IExpr result;
          if (known != null && known.function == function && i == known.column
              && j == known.row) {
            // the sample the shape of the function was read from is not evaluated again
            result = known.value;
          } else {
            IExpr valExpr = function
                .replaceAll(F.List(F.Rule(xVar, F.num(xVal)), F.Rule(yVar, F.num(yVal))));
            result = engine.evaluate(valExpr);
          }
          z = result.evalf();
        } catch (Exception e) {
          z = Double.NaN;
        }
        // CPD-ON

        zGrid[i][j] = z;
        // The value is kept even where the region rejects it: a cell the edge of the region runs
        // through is clipped rather than dropped, and clipping interpolates across the cell, so it
        // needs the value at every corner. Whether a corner is part of the picture is a separate
        // question, and the answer to it is what the levels and the outline are built from.
        inside[i][j] = Double.isFinite(z) && (region == null || region.accepts(xVal, yVal, z));
        defined[i][j] = inside[i][j];
        if (inside[i][j]) {
          if (z < minZ)
            minZ = z;
          if (z > maxZ)
            maxZ = z;
        }
      }
    }

    if (minZ == Double.MAX_VALUE)
      return;
    if (shaded && !isEquation) {
      valueRange[0] = Math.min(valueRange[0], minZ);
      valueRange[1] = Math.max(valueRange[1], maxZ);
    }

    // one argument, the average of the two levels the band lies between, which is what the
    // reference means by "the average of the scaled values of f for each pair of successive
    // contour levels" - the scaling is a straight line, so averaging before it is the same thing
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.FIELD_2D, colorFunctionOpt, F.booleSymbol(colorFunctionScaling),
            S.ContourPlot, engine)
        .range(1, minZ, maxZ).sink(PlotColorFunction.Sink.FLAT).fallback(this::getShadingColor)
        .build();

    // 2. Determine Levels
    double[] levels;
    if (isEquation) {
      levels = new double[] {0.0};
    } else if (explicitLevels != null) {
      levels = explicitLevels.sorted;
    } else if (numberOfContours < 0) {
      // about ten levels at round values strictly between the least and the greatest value
      levels = TickGenerator.interiorDivisions(minZ, maxZ, 10);
    } else {
      levels = new double[numberOfContours];
      double levelStep = (maxZ - minZ) / (numberOfContours + 1);
      for (int k = 0; k < numberOfContours; k++) {
        levels[k] = minZ + (k + 1) * levelStep;
      }
    }

    // The cells the edge of the region runs through, and the line it follows through each. A cell
    // that is wholly in or wholly out is not listed here: it is drawn, or not, exactly as before.
    double[][][][] cellClip = regionClips(inside, region, zGrid, xRange[0], yRange[0], stepX, stepY,
        gridX, gridY);

    // 3. Shading (Polygons)
    if (shaded) {
      for (int k = -1; k < levels.length; k++) {
        double level = (k == -1) ? minZ - 1.0 : levels[k];

        IExpr color;
        if (contourShading.isList()) {
          color = GraphicsOptions.getPlotStyle(contourShading, k + 1);
        } else {
          // an explicit level may lie outside the values; the band colour stays within them
          double lower = (k == -1) ? minZ : Math.min(Math.max(levels[k], minZ), maxZ);
          double upper =
              (k == levels.length - 1) ? maxZ : Math.min(Math.max(levels[k + 1], minZ), maxZ);
          double bandZ = (lower + upper) * 0.5;
          // an equation has no range of values to place a band within, so it is left unscaled
          color = colorMap != null && !isEquation ? colorMap.color(bandZ)
              : getShadingColor((colorFunctionScaling && !isEquation)
                  ? (bandZ - minZ) / (maxZ - minZ)
                  : bandZ);
        }

        IASTAppendable polygons = F.ListAlloc();
        marchGrid(polygons, S.Polygon, ContourPlot::processCellPolygon, zGrid, inside, cellClip,
            xRange[0], yRange[0], stepX, stepY, gridX, gridY, level);

        if (polygons.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          group.append(F.EdgeForm(S.None));
          group.append(color);
          for (IExpr poly : polygons)
            // CPD-OFF contour and density plots sample their own grids
            group.append(poly);
          primitives.append(group);
        }
      }
    }

    // 4. Contour Lines
    if (contourLines && contourStyle != S.None && !contourStyle.isFalse()) {
      // the lines are drawn from the highest level down; an explicit level with a style of its
      // own is drawn in just that style
      final boolean given = !isEquation && explicitLevels != null;
      for (int k = levels.length - 1; k >= 0; k--) {
        double level = levels[k];
        IASTAppendable lineSegments = F.ListAlloc();

        IExpr currentStyle = contourStyle;
        final boolean ownStyle = given && explicitLevels.styles[k].isPresent();
        if (ownStyle) {
          currentStyle = explicitLevels.styles[k];
        } else if (currentStyle == S.Automatic) {
          // Single plot default: Gray
          // CPD-ON
          // Multi plot default: Passed in via argument (handled in evaluate loop)
          currentStyle = isMulti ? contourStyle : (isEquation ? S.Black : F.GrayLevel(0.5));
        } else if (currentStyle.isList() && !isMulti) {
          // If it's a list of styles for LEVELS (not equations), pick per level
          currentStyle = GraphicsOptions.getPlotStyle(currentStyle, k);
        }
        // Note: If isMulti is true, contourStyle passed here is already the specific color for
        // this equation

        marchGrid(lineSegments, S.Line, ContourPlot::processCellLine, zGrid, inside, cellClip,
            xRange[0], yRange[0], stepX, stepY, gridX, gridY, level);

        if (lineSegments.argSize() > 0) {
          lineSegments = joinSegments(lineSegments, xRange[0], yRange[0],
              joinTolerance(stepX, xRange), joinTolerance(stepY, yRange));
          IASTAppendable group = F.ListAlloc();
          // the lines of a level are half transparent with butt ends, an equation's curve keeps
          // its style as given
          group.append(isEquation || ownStyle ? styleDirective(currentStyle)
              : levelDirective(currentStyle));

          for (IExpr line : lineSegments)
            group.append(line);
          primitives.append(group);
          if (contourLabels) {
            IExpr label = levelLabel(lineSegments, level);
            if (label.isPresent()) {
              primitives.append(label);
            }
          }
        }
      }
    }

    appendBoundary(primitives, defined, xRange[0], yRange[0], stepX, stepY, boundaryStyle);
  }

  /**
   * The line the edge of the region follows through each cell it runs through.
   *
   * <p>
   * Masking a grid can only drop whole cells, which turns a smooth region boundary into a staircase
   * with one step per sample. Clipping each straddled cell to the line between the two points where
   * the region crosses its edges follows the boundary instead. Inside one cell a smooth curve is
   * very nearly straight, so at any density worth plotting at the difference is invisible.
   *
   * <p>
   * The value at a crossing is interpolated from the four corners rather than evaluated again: the
   * boundary is already being approximated to first order inside the cell, and a region written on
   * the value rather than on the position would otherwise cost a fresh evaluation of the plotted
   * function at every step of every bisection.
   *
   * @return {@code null} when there is no region at all, otherwise an entry per cell which is
   *         {@code null} unless that cell needs clipping
   */
  static double[][][][] regionClips(boolean[][] inside, RegionFunctionFilter region,
      double[][] zGrid, double x0, double y0, double stepX, double stepY, int gridX, int gridY) {
    if (region == null) {
      return null;
    }
    double[][][][] clips = new double[gridX][gridY][][];
    for (int i = 0; i < gridX; i++) {
      double x = x0 + i * stepX;
      for (int j = 0; j < gridY; j++) {
        double y = y0 + j * stepY;
        boolean[] corners = {inside[i][j], inside[i + 1][j], inside[i + 1][j + 1],
            inside[i][j + 1]};
        if (corners[0] == corners[1] && corners[1] == corners[2] && corners[2] == corners[3]) {
          continue; // wholly in or wholly out; nothing to cut
        }
        final double v00 = zGrid[i][j];
        final double v10 = zGrid[i + 1][j];
        final double v11 = zGrid[i + 1][j + 1];
        final double v01 = zGrid[i][j + 1];
        if (!Double.isFinite(v00) || !Double.isFinite(v10) || !Double.isFinite(v11)
            || !Double.isFinite(v01)) {
          continue; // a corner without a value: the cell is dropped anyway
        }
        RegionClip.Membership member = (px, py) -> {
          double u = (px - x) / stepX;
          double v = (py - y) / stepY;
          double value = v00 * (1 - u) * (1 - v) + v10 * u * (1 - v) + v11 * u * v
              + v01 * (1 - u) * v;
          return region.accepts(px, py, value);
        };
        double[][] cellCorners = {{x, y}, {x + stepX, y}, {x + stepX, y + stepY}, {x, y + stepY}};
        clips[i][j] = RegionClip.cellBoundary(member, cellCorners, corners);
      }
    }
    return clips;
  }

  /**
   * Appends what one cell draws, cut to the region when its edge runs through that cell.
   *
   * <p>
   * The marching squares routines write straight into the output, so a clipped cell is drawn into a
   * scratch list first and each primitive is then cut. Both the shaded bands and the contour lines
   * are pieces of a cell that a straight line already cut off, so both stay convex and one pass of
   * the clipper is enough.
   *
   * @param clip {@code null} to append what the cell draws unchanged
   * @param head {@code Polygon} or {@code Line}, which is what the cell draws
   */
  static void clipped(IASTAppendable out, double[][] clip, ISymbol head,
      java.util.function.Consumer<IASTAppendable> draw) {
    if (clip == null) {
      draw.accept(out);
      return;
    }
    IASTAppendable raw = F.ListAlloc(4);
    draw.accept(raw);
    for (IExpr primitive : raw) {
      if (!primitive.isAST1() || !primitive.first().isList()) {
        continue;
      }
      IAST points = (IAST) primitive.first();
      double[][] corners = new double[points.argSize()][];
      boolean readable = true;
      for (int k = 1; k <= points.argSize(); k++) {
        IExpr point = points.get(k);
        if (!point.isList2()) {
          readable = false;
          break;
        }
        corners[k - 1] =
            new double[] {point.first().evalfNaN(), point.second().evalfNaN()};
      }
      if (!readable) {
        continue;
      }
      double[][] cut = head == S.Line && corners.length == 2
          ? RegionClip.clipSegment(corners[0], corners[1], clip[0], clip[1], clip[2][0], clip[2][1])
          : RegionClip.clipPolygon(corners, clip[0], clip[1], clip[2][0], clip[2][1]);
      if (cut == null) {
        continue;
      }
      IASTAppendable list = F.ListAlloc(cut.length);
      for (double[] point : cut) {
        list.append(F.List(F.num(point[0]), F.num(point[1])));
      }
      out.append(F.unaryAST1(head, list));
    }
  }

  /**
   * Draws the outline of what was plotted, when {@code BoundaryStyle} asks for one.
   *
   * <p>
   * That is the rim of the sampled rectangle, and the rim of every hole in it - so it is also the
   * edge a {@code RegionFunction} cuts, without the option having to know a region was given.
   */
  static void appendBoundary(IASTAppendable primitives, boolean[][] defined, double x0, double y0,
      double stepX, double stepY, IExpr boundaryStyle) {
    if (boundaryStyle == null || !boundaryStyle.isPresent() || boundaryStyle.isAutomatic()
        || boundaryStyle.isNone()) {
      return;
    }
    IAST boundary = GraphicsOptions.gridBoundary(defined, x0, y0, stepX, stepY);
    if (boundary.argSize() == 0) {
      return;
    }
    IASTAppendable group = F.ListAlloc(boundary.size() + 1);
    group.append(boundaryStyle);
    group.appendArgs(boundary);
    primitives.append(group);
  }

  /**
   * The style of one contour as it stands in front of its lines: a list <code>{d1, {d2, d3},
   * ...}</code> of directives becomes <code>Directive(d1, d2, d3, ...)</code>, any other style is
   * kept.
   */
  static IExpr styleDirective(IExpr style) {
    if (!style.isList()) {
      return style;
    }
    IASTAppendable directive = F.ast(S.Directive);
    appendDirectives(directive, style);
    return directive;
  }

  /**
   * The style of the lines of one contour level: <code>Directive(Opacity(0.5), CapForm("Butt"),
   * d1, d2, ...)</code> with the directives <code>d1, d2, ...</code> of the style, a list or a
   * <code>Directive</code> of them or a single one.
   */
  static IExpr levelDirective(IExpr style) {
    IASTAppendable directive = F.ast(S.Directive);
    directive.append(F.unaryAST1(S.Opacity, F.num(0.5)));
    directive.append(F.unaryAST1(S.CapForm, F.stringx("Butt")));
    appendDirectives(directive, style);
    return directive;
  }

  private static void appendDirectives(IASTAppendable directive, IExpr style) {
    if (style.isList() || style.isAST(S.Directive)) {
      for (IExpr d : (IAST) style) {
        appendDirectives(directive, d);
      }
    } else {
      directive.append(style);
    }
  }

  /**
   * The pieces of one level joined into polylines: pieces of neighbouring cells meet in the point
   * where the contour crosses their common edge, and the chains of pieces through such points
   * become one <code>Line</code> each - one per closed loop or open curve of the level.
   *
   * @param lineSegments <code>Line({p1, p2, ...})</code> primitives; any other primitive is kept
   * @param x0 the x coordinate the end points are compared relative to
   * @param y0 the y coordinate the end points are compared relative to
   * @param toleranceX the x distance below which two end points are the same point
   * @param toleranceY the y distance below which two end points are the same point
   */
  static IASTAppendable joinSegments(IAST lineSegments, double x0, double y0, double toleranceX,
      double toleranceY) {
    final double[] origin = {x0, y0};
    final double[] tolerance = {toleranceX, toleranceY};
    final int n = lineSegments.argSize();
    double[][][] pieces = new double[n][][];
    java.util.Map<String, java.util.List<Integer>> byEnd = new java.util.HashMap<>();
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      IExpr line = lineSegments.get(i + 1);
      double[][] points = linePoints(line);
      if (points == null) {
        result.append(line);
        continue;
      }
      pieces[i] = points;
      byEnd.computeIfAbsent(pointKey(points[0], origin, tolerance),
          key -> new java.util.ArrayList<>()).add(i);
      byEnd.computeIfAbsent(pointKey(points[points.length - 1], origin, tolerance),
          key -> new java.util.ArrayList<>()).add(i);
    }
    boolean[] used = new boolean[n];
    for (int i = 0; i < n; i++) {
      if (pieces[i] == null || used[i]) {
        continue;
      }
      used[i] = true;
      java.util.ArrayDeque<double[]> chain = new java.util.ArrayDeque<>();
      for (double[] p : pieces[i]) {
        chain.addLast(p);
      }
      // grow the chain at its end, then at its start
      for (boolean atEnd : new boolean[] {true, false}) {
        while (true) {
          String key = pointKey(atEnd ? chain.peekLast() : chain.peekFirst(), origin, tolerance);
          int next = -1;
          for (int j : byEnd.getOrDefault(key, java.util.Collections.emptyList())) {
            if (!used[j]) {
              next = j;
              break;
            }
          }
          if (next < 0) {
            break;
          }
          used[next] = true;
          double[][] piece = pieces[next];
          // the piece runs away from the shared point, in whichever direction it was stored
          boolean forward = pointKey(piece[0], origin, tolerance).equals(key);
          for (int k = 1; k < piece.length; k++) {
            double[] p = forward ? piece[k] : piece[piece.length - 1 - k];
            if (atEnd) {
              chain.addLast(p);
            } else {
              chain.addFirst(p);
            }
          }
        }
      }
      IASTAppendable points = F.ListAlloc(chain.size());
      for (double[] p : chain) {
        points.append(F.List(F.num(p[0]), F.num(p[1])));
      }
      result.append(F.Line(points));
    }
    return result;
  }

  /** The points of <code>Line({p1, p2, ...})</code>, or <code>null</code> for anything else. */
  private static double[][] linePoints(IExpr line) {
    if (!line.isAST(S.Line, 2) || !line.first().isList() || line.first().argSize() < 2) {
      return null;
    }
    IAST list = (IAST) line.first();
    double[][] points = new double[list.argSize()][];
    for (int k = 0; k < points.length; k++) {
      IExpr p = list.get(k + 1);
      if (!p.isList2()) {
        return null;
      }
      double x = p.first().evalfNaN();
      double y = p.second().evalfNaN();
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        return null;
      }
      points[k] = new double[] {x, y};
    }
    return points;
  }

  /**
   * The offset of the point from <code>origin</code> rounded to multiples of
   * <code>tolerance</code>, as a map key; relative to the plot's corner the multiples stay far
   * below the range of a <code>long</code>.
   */
  private static String pointKey(double[] p, double[] origin, double[] tolerance) {
    return Math.round((p[0] - origin[0]) / tolerance[0]) + ","
        + Math.round((p[1] - origin[1]) / tolerance[1]);
  }

  /**
   * The value of a contour, written where that contour runs.
   *
   * <p>
   * The label goes on the middle segment of the level, which keeps it away from the edges of the
   * picture where a contour is most likely to be clipped.
   *
   * @param lineSegments the {@code Line} primitives making up one level
   * @param level the value the contour stands for
   * @return the label, or {@link F#NIL} when the level has no segment to put it on
   */
  private static IExpr levelLabel(IAST lineSegments, double level) {
    if (lineSegments.argSize() < 1) {
      return F.NIL;
    }
    IExpr segment = lineSegments.get(1 + lineSegments.argSize() / 2);
    if (!segment.isAST(S.Line, 2) || !segment.first().isList()) {
      return F.NIL;
    }
    IAST points = (IAST) segment.first();
    if (points.argSize() < 2 || !points.arg1().isList() || !points.arg2().isList()) {
      return F.NIL;
    }
    double x =
        (((IAST) points.arg1()).arg1().evalfNaN() + ((IAST) points.arg2()).arg1().evalfNaN()) / 2.0;
    double y =
        (((IAST) points.arg1()).arg2().evalfNaN() + ((IAST) points.arg2()).arg2().evalfNaN()) / 2.0;
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return F.NIL;
    }
    return F.List(S.Black, F.Text(F.num(level), F.List(F.num(x), F.num(y))));
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(labelledContent(primitives, plotAST));
    result.appendArgs(graphicsOptions.getGraphicsRules());
    return GraphicsOptions.legended(result);
  }

  private IExpr getShadingColor(double t) {
    double val = t;
    if (val < 0 || val > 1.0) {
      val = val - Math.floor(val);
    }
    return GraphicsOptions.getFieldColor(val);
  }

  /** One marching squares cell: {@link #processCellPolygon} or {@link #processCellLine}. */
  @FunctionalInterface
  interface CellMarcher {
    void march(IASTAppendable out, double x, double y, double dx, double dy, double v0, double v1,
        double v2, double v3, double level);
  }

  /**
   * Run a marching squares step over every cell of the grid at one level. With a region
   * (<code>cellClip != null</code>) a cell wholly outside is skipped, a cell the region edge crosses
   * once is cut along that edge, and a cell it crosses more than once is left out: no single line
   * describes it, so it stays as it was before there was any clipping at all.
   *
   * @param out the primitives
   * @param head {@code Polygon} or {@code Line}, which is what <code>marcher</code> draws
   * @param zGrid the values at the grid points
   * @param inside which grid points lie in the region; only read if <code>cellClip != null</code>
   * @param cellClip the line the region edge follows through each cell, or <code>null</code>
   */
  static void marchGrid(IASTAppendable out, ISymbol head, CellMarcher marcher, double[][] zGrid,
      boolean[][] inside, double[][][][] cellClip, double xMin, double yMin, double stepX,
      double stepY, int gridX, int gridY, double level) {
    for (int i = 0; i < gridX; i++) {
      for (int j = 0; j < gridY; j++) {
        if (cellClip != null && !inside[i][j] && !inside[i + 1][j] && !inside[i + 1][j + 1]
            && !inside[i][j + 1]) {
          continue;
        }
        double[][] clip = cellClip == null ? null : cellClip[i][j];
        if (cellClip != null && clip == null && !(inside[i][j] && inside[i + 1][j]
            && inside[i + 1][j + 1] && inside[i][j + 1])) {
          continue;
        }
        final double x = xMin + i * stepX;
        final double y = yMin + j * stepY;
        final double c00 = zGrid[i][j];
        final double c10 = zGrid[i + 1][j];
        final double c11 = zGrid[i + 1][j + 1];
        final double c01 = zGrid[i][j + 1];
        clipped(out, clip, head,
            o -> marcher.march(o, x, y, stepX, stepY, c00, c10, c11, c01, level));
      }
    }
  }

  // --- Marching Squares: Polygons (shared with ListContourPlot) ---

  /**
   * The marching squares case of a cell: bit <code>k</code> is set if corner <code>k</code> is at
   * or above <code>level</code>.
   *
   * @return <code>-1</code> for an incomplete cell. A corner without a value is not "below the
   *         level": comparing NaN with >= says false, which would classify the cell as filled up to
   *         that corner and then interpolate across the edge, producing NaN coordinates. An
   *         incomplete cell has no interior to shade and no contour to trace - that is what leaves
   *         a hole where a RegionFunction cuts, or where the function has no value.
   */
  static int cellIndex(double v0, double v1, double v2, double v3, double level) {
    if (!Double.isFinite(v0) || !Double.isFinite(v1) || !Double.isFinite(v2)
        || !Double.isFinite(v3)) {
      return -1;
    }
    int index = 0;
    if (v0 >= level)
      index |= 1;
    if (v1 >= level)
      index |= 2;
    if (v2 >= level)
      index |= 4;
    if (v3 >= level)
      index |= 8;
    return index;
  }

  static void processCellPolygon(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {
    int index = cellIndex(v0, v1, v2, v3, level);
    if (index <= 0)
      return;
    if (index == 15) {
      out.append(F.Polygon(F.List(F.List(F.num(x), F.num(y)), F.List(F.num(x + dx), F.num(y)),
          F.List(F.num(x + dx), F.num(y + dy)), F.List(F.num(x), F.num(y + dy)))));
      return;
    }

    double[][] pts = new double[4][2];
    if ((index & 1) != ((index >> 1) & 1))
      pts[0] = interp(x, y, dx, dy, 0, v0, v1, level);
    if ((index & 2) != ((index >> 2) & 1))
      pts[1] = interp(x, y, dx, dy, 1, v1, v2, level);
    if ((index & 4) != ((index >> 3) & 1))
      pts[2] = interp(x, y, dx, dy, 2, v3, v2, level);
    if ((index & 8) != ((index) & 1))
      pts[3] = interp(x, y, dx, dy, 3, v0, v3, level);

    IASTAppendable polyPts = F.ListAlloc();

    if (index == 1) {
      add(polyPts, pts[3]);
      add(polyPts, x, y);
      add(polyPts, pts[0]);
    } else if (index == 2) {
      add(polyPts, pts[0]);
      add(polyPts, x + dx, y);
      add(polyPts, pts[1]);
    } else if (index == 4) {
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, pts[2]);
    } else if (index == 8) {
      add(polyPts, pts[2]);
      add(polyPts, x, y + dy);
      add(polyPts, pts[3]);
    } else if (index == 3) {
      add(polyPts, pts[3]);
      add(polyPts, x, y);
      add(polyPts, x + dx, y);
      add(polyPts, pts[1]);
    } else if (index == 6) {
      add(polyPts, pts[0]);
      add(polyPts, x + dx, y);
      add(polyPts, x + dx, y + dy);
      add(polyPts, pts[2]);
    } else if (index == 12) {
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x, y + dy);
      add(polyPts, pts[3]);
    } else if (index == 9) {
      add(polyPts, pts[2]);
      add(polyPts, x, y + dy);
      add(polyPts, x, y);
      add(polyPts, pts[0]);
    } else if (index == 14) {
      add(polyPts, pts[0]);
      add(polyPts, pts[3]);
      add(polyPts, x, y + dy);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x + dx, y);
    } else if (index == 13) {
      add(polyPts, pts[1]);
      add(polyPts, pts[0]);
      add(polyPts, x, y);
      add(polyPts, x, y + dy);
      add(polyPts, x + dx, y + dy);
    } else if (index == 11) {
      add(polyPts, pts[2]);
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y);
      add(polyPts, x, y);
      add(polyPts, x, y + dy);
    } else if (index == 7) {
      add(polyPts, pts[3]);
      add(polyPts, pts[2]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, x + dx, y);
      add(polyPts, x, y);
    } else if (index == 5) {
      // a saddle joins its corners 0 and 2, here above the level, and cuts off the corners 1 and 3
      add(polyPts, x, y);
      add(polyPts, pts[0]);
      add(polyPts, pts[1]);
      add(polyPts, x + dx, y + dy);
      add(polyPts, pts[2]);
      add(polyPts, pts[3]);
    } else if (index == 10) {
      // the corners 0 and 2 below the level are joined: the corners 1 and 3 above it are cut off
      IASTAppendable t1 = F.ListAlloc();
      add(t1, pts[0]);
      add(t1, x + dx, y);
      add(t1, pts[1]);
      appendPolygon(out, t1, dx, dy);
      IASTAppendable t2 = F.ListAlloc();
      add(t2, pts[2]);
      add(t2, x, y + dy);
      add(t2, pts[3]);
      appendPolygon(out, t2, dx, dy);
      return;
    }

    appendPolygon(out, polyPts, dx, dy);
  }

  /**
   * Append the polygon through <code>points</code> with repeated points left out, or nothing if
   * fewer than three distinct points remain: a corner exactly at the level is also where the
   * crossings on its two edges lie, and the piece of the cell cut off there has no area.
   */
  private static void appendPolygon(IASTAppendable out, IAST points, double dx, double dy) {
    IASTAppendable distinct = F.ListAlloc(points.argSize());
    for (IExpr p : points) {
      if (distinct.argSize() == 0 || !samePoint(distinct.last(), p, dx, dy)) {
        distinct.append(p);
      }
    }
    while (distinct.argSize() > 1 && samePoint(distinct.last(), distinct.arg1(), dx, dy)) {
      distinct.remove(distinct.argSize());
    }
    if (distinct.argSize() >= 3) {
      out.append(F.Polygon(distinct));
    }
  }

  private static boolean samePoint(IExpr p, IExpr q, double dx, double dy) {
    return samePoint(p.first().evalf(), p.second().evalf(), q.first().evalf(),
        q.second().evalf(), dx, dy);
  }

  /**
   * Whether two points of a cell <code>dx</code> by <code>dy</code> large are the same up to the
   * rounding of the interpolation along its edges.
   */
  private static boolean samePoint(double x1, double y1, double x2, double y2, double dx,
      double dy) {
    return Math.abs(x1 - x2) <= 1.0e-9 * Math.abs(dx) && Math.abs(y1 - y2) <= 1.0e-9 * Math.abs(dy);
  }

  static void add(IASTAppendable list, double[] p) {
    list.append(F.List(F.num(p[0]), F.num(p[1])));
  }

  static void add(IASTAppendable list, double x, double y) {
    list.append(F.List(F.num(x), F.num(y)));
  }

  // --- Marching Squares: Lines ---

  static void processCellLine(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {
    int index = cellIndex(v0, v1, v2, v3, level);
    if (index < 0) {
      return;
    }
    int[] edges = ISO_LINES[index];
    if (index == 5) {
      // a saddle joins its corners (x, y) and (x + dx, y + dy), whichever side of the level they
      // are on: the lines cut off the other two corners, the borders of the filled cells
      edges = ISO_LINES[10];
    }
    for (int i = 0; i < edges.length; i += 2) {
      int e1 = edges[i];
      int e2 = edges[i + 1];
      if (e1 == -1)
        break;
      double[] p1 = interp(x, y, dx, dy, e1, v0, v1, v2, v3, level);
      double[] p2 = interp(x, y, dx, dy, e2, v0, v1, v2, v3, level);
      if (samePoint(p1[0], p1[1], p2[0], p2[1], dx, dy)) {
        // a corner exactly at the level: both ends meet in it, and the neighbouring cell draws
        // the contour through it
        continue;
      }
      out.append(
          F.Line(F.List(F.List(F.num(p1[0]), F.num(p1[1])), F.List(F.num(p2[0]), F.num(p2[1])))));
    }
  }

  static double[] interp(double x, double y, double dx, double dy, int edge, double v0, double v1,
      double v2, double v3, double level) {
    return interp(x, y, dx, dy, edge, getVal(edge, 0, v0, v1, v2, v3),
        getVal(edge, 1, v0, v1, v2, v3), level);
  }

  static double getVal(int edge, int endpoint, double v0, double v1, double v2, double v3) {
    if (edge == 0)
      return (endpoint == 0) ? v0 : v1;
    if (edge == 1)
      return (endpoint == 0) ? v1 : v2;
    if (edge == 2)
      return (endpoint == 0) ? v3 : v2;
    if (edge == 3)
      return (endpoint == 0) ? v0 : v3;
    return 0;
  }

  static double[] interp(double x, double y, double dx, double dy, int edge, double valA,
      double valB, double level) {
    double mu = (level - valA) / (valB - valA);
    if (edge == 0)
      return new double[] {x + mu * dx, y};
    if (edge == 1)
      return new double[] {x + dx, y + mu * dy};
    if (edge == 2)
      return new double[] {x + mu * dx, y + dy};
    if (edge == 3)
      return new double[] {x, y + mu * dy};
    return new double[] {x, y};
  }

  static final int[][] ISO_LINES = {{-1, -1, -1, -1}, {3, 0, -1, -1}, {0, 1, -1, -1},
      {3, 1, -1, -1}, {1, 2, -1, -1}, {3, 0, 1, 2}, {0, 2, -1, -1}, {3, 2, -1, -1}, {2, 3, -1, -1},
      {2, 0, -1, -1}, {0, 1, 2, 3}, {2, 1, -1, -1}, {1, 3, -1, -1}, {1, 0, -1, -1}, {0, 3, -1, -1},
      {-1, -1, -1, -1}};

  private double[] parseRange(IExpr iter, EvalEngine engine) {
    if (iter.isList() && iter.argSize() >= 3) {
      try {
        double min = engine.evalDouble(((IAST) iter).arg2());
        double max = engine.evalDouble(((IAST) iter).arg3());
        return new double[] {min, max};
      } catch (Exception e) {
      }
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  protected int dependentRangeOffset() {
    return 0;
  }

  @Override
  protected boolean samplesFirstArgument() {
    // the function is evaluated at the sample points: a function which iterates until its values
    // agree never stops on symbolic coordinates
    // only the plot of a function; a plot of data or of a predicate reads its argument evaluated
    return getClass() == ContourPlot.class;
  }

  @Override
  protected long symbolicFirstArgumentMillis() {
    // D(f, x) or an expansion is still done once with symbolic coordinates, if it finishes
    return PLOT_SYMBOLIC_MILLIS;
  }

  @Override
  public boolean localIterators() {
    // the plot variable is local to the plot
    return true;
  }

  @Override
  protected boolean compoundIteratorVariables() {
    // a plot variable like x(1) is replaced by a unique symbol
    return true;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] optionValues = GraphicsOptions.contourPlotDefaultOptionValues(false, false);
    optionValues[GraphicsOptions.X_AXES] = S.False;
    optionValues[GraphicsOptions.X_FRAME] = S.True;
    optionValues[GraphicsOptions.X_ASPECTRATIO] = F.C1;
    GraphicsOptions.OptionSet optionSet =
        GraphicsOptions.contourExtras(new GraphicsOptions.OptionSet()
            .add(GraphicsOptions.contourPlotDefaultOptionKeys(), optionValues));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
    // setOptions(newSymbol, //
    // new IBuiltInSymbol[] {S.ContourShading, S.ColorFunctionScaling, S.ContourStyle,
    // S.AspectRatio, S.Frame, S.Axes}, //
    // new IExpr[] {S.Automatic, S.True, S.Automatic, F.C1, S.True, S.False} //
    // );
  }
}
