package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.builtin.graphics.PlotEndpoints;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.PlotShapeProbe;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code ParametricPlot3D[{fx, fy, fz}, {u, umin, umax}]} - a space curve, and with a second
 * iterator a parametric surface.
 */
public class ParametricPlot3D extends AbstractFunctionOptionEvaluator {

  /** A curve is sampled far more finely than a surface. */
  private static final int CURVE_POINTS = 150;
  private static final int SURFACE_POINTS = 40;

  public ParametricPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (PlotEndpoints.degenerate(S.ParametricPlot3D, ast, 2, 3, false, engine)) {
      return F.NIL;
    }
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    // a function which only takes its shape once evaluated, g(t) with g(u_?NumericQ) := {...},
    // is not a list yet, and its sampled values decide what it draws
    if (argSize < 2 || ast.arg1().isAtom()) {
      return F.NIL;
    }
    boolean isSurface = argSize >= 3 && ast.arg2().isList() && ast.arg3().isList();

    List<IExpr> functions = new ArrayList<>();
    PlotWrapper.collectCurves(ast.arg1(), functions);

    int[] samples = Plot3DTools.plotPoints(options[Plot3DTools.X_PLOT_POINTS],
        isSurface ? SURFACE_POINTS : CURVE_POINTS);
    IExpr plotStyle = options[Plot3DTools.X_PLOT_STYLE];
    IExpr meshOption = options[Plot3DTools.X_MESH];
    IASTAppendable graphicsList = F.ListAlloc(functions.size());
    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);

    if (isSurface) {
      if (!ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
        return Errors.printMessage(S.ParametricPlot3D, "pllim", F.list(ast.arg2()), engine);
      }
      if (!ast.arg3().isList3() || !ast.arg3().first().isSymbol()) {
        return Errors.printMessage(S.ParametricPlot3D, "pllim", F.list(ast.arg3()), engine);
      }
      IAST uRange = (IAST) ast.arg2();
      IAST vRange = (IAST) ast.arg3();
      functions = PlotShapeProbe.split(functions, PlotShapeProbe.rangeProbes(
          new IExpr[] {uRange.arg1(), vRange.arg1()},
          new double[] {uRange.arg2().evalfNaN(), vRange.arg2().evalfNaN()},
          new double[] {uRange.arg3().evalfNaN(), vRange.arg3().evalfNaN()}), 3, false, engine);
      PlotColorFunction.Builder colorBuilder = Plot3DTools.plotColors(
          PlotColorFunction.Family.PARAMETRIC_3D_UV, options, S.ParametricPlot3D, engine);

      for (int i = 0; i < functions.size(); i++) {
        PlotWrapper each = PlotWrapper.of(functions.get(i));
        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colorBuilder != null);
        Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(i, plotStyle), meshOption);
        if (each.hasStyle()) {
          builder.setStyle(each.style);
        }
        double[][][] grid =
            createSurfaceGeometry(each.datum, uRange, vRange, samples[0], samples[1], engine,
                builder, colorBuilder, meshOption, options[Plot3DTools.X_MESH_STYLE],
                options[Plot3DTools.X_EVALUATION_MONITOR], region,
                options[Plot3DTools.X_MESH_FUNCTIONS], options[Plot3DTools.X_MESH_SHADING]);
        if (grid != null) {
          // the rim of the surface, and the rim of every hole a RegionFunction cut in it
          IExpr complex = Plot3DTools.withBoundary(builder, grid,
              options[Plot3DTools.X_BOUNDARY_STYLE], false);
          if (complex.isPresent()) {
            graphicsList.append(each.wrapTooltip(complex));
          }
        }
      }
    } else {
      if (!ast.arg2().isList()) {
        return F.NIL;
      }
      IAST range = (IAST) ast.arg2();
      if (!range.isList3() || !range.first().isSymbol()) {
        return Errors.printMessage(S.ParametricPlot3D, "pllim", F.list(range), engine);
      }
      functions = PlotShapeProbe.split(functions,
          PlotShapeProbe.rangeProbes(new IExpr[] {range.arg1()},
              new double[] {range.arg2().evalfNaN()}, new double[] {range.arg3().evalfNaN()}),
          3, false, engine);
      for (int i = 0; i < functions.size(); i++) {
        // a curve wrapped in Tooltip or Style is sampled bare: the style goes into the curve and
        // the tooltip back around the finished primitive, the way Plot3D labels one of several
        PlotWrapper each = PlotWrapper.of(functions.get(i));
        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
        // a curve is a line: no mesh, no edge form, and the ordinary plot colours
        builder.setStyle(Plot3DTools.curveStyle(i, plotStyle));
        if (each.hasStyle()) {
          // the curve's own Style comes after the plot colour, so it is the one that holds
          builder.setStyle(each.style);
        }
        createCurveGeometry(each.datum, range, samples[0], engine, builder,
            options[Plot3DTools.X_EVALUATION_MONITOR], region);
        IExpr complex = builder.build();
        if (complex.isPresent()) {
          graphicsList.append(each.wrapTooltip(complex));
        }
      }
    }

    if (graphicsList.argSize() == 0) {
      if (!PlotShapeProbe.isNoCurve(ast.arg1())) {
        return F.NIL;
      }
      // every curve was empty: an empty picture, not a call that could not be read
    }
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios,
                Plot3DTools.automaticBoxRatios(options[Plot3DTools.X_BOX_RATIOS], graphicsList)),
            F.Rule(S.Axes, S.True), F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  private void createCurveGeometry(IExpr func, IAST range, int pointsCount, EvalEngine engine,
      GraphicsComplexBuilder builder, IExpr monitor, RegionFunctionFilter region) {
    ISymbol uVar = (ISymbol) range.arg1();
    double uMin = range.arg2().evalfNaN();
    double uMax = range.arg3().evalfNaN();
    if (Double.isNaN(uMin) || Double.isNaN(uMax) || uMax <= uMin) {
      return;
    }
    double step = (uMax - uMin) / (pointsCount - 1);
    IASTAppendable currentLine = F.ListAlloc(pointsCount);

    for (int i = 0; i < pointsCount; i++) {
      double u = uMin + i * step;
      double[] point = evaluatePoint(func, engine, F.List(F.Rule(uVar, F.num(u))), monitor);
      if (point != null && region != null && !region.accepts(point[0], point[1], point[2], u)) {
        // a point outside the region is not part of the curve, the same way one the
        // parametrisation has no value at is not
        point = null;
      }
      if (point != null) {
        currentLine.append(F.ZZ(builder.addVertex(point[0], point[1], point[2], null, null)));
      } else if (currentLine.argSize() > 0) {
        // the curve is broken where it has no value, rather than jumped across
        appendLine(builder, currentLine);
        currentLine = F.ListAlloc(pointsCount);
      }
    }
    appendLine(builder, currentLine);
  }

  /** A run of a single point is not a line and would draw nothing, so it is dropped. */
  private static void appendLine(GraphicsComplexBuilder builder, IASTAppendable line) {
    if (line.argSize() >= 2) {
      builder.addPrimitive(F.Line(line));
    }
  }

  /**
   * The sampled grid, or {@code null} when the parametrisation gave nothing to draw.
   *
   * <p>
   * With explicit {@code MeshFunctions} the mesh follows levels of those functions rather than the
   * two parameters, drawn inside the surface's {@code GraphicsComplex}; {@code MeshShading} then
   * colours the bands between its lines.
   */
  private double[][][] createSurfaceGeometry(IExpr func, IAST uRange, IAST vRange, int uCount,
      int vCount, EvalEngine engine, GraphicsComplexBuilder builder,
      PlotColorFunction.Builder colorBuilder, IExpr meshOption, IExpr meshStyle, IExpr monitor,
      RegionFunctionFilter region, IExpr meshFunctions, IExpr meshShading) {
    ISymbol uVar = (ISymbol) uRange.arg1();
    double uMin = uRange.arg2().evalfNaN();
    double uMax = uRange.arg3().evalfNaN();
    ISymbol vVar = (ISymbol) vRange.arg1();
    double vMin = vRange.arg2().evalfNaN();
    double vMax = vRange.arg3().evalfNaN();
    if (Double.isNaN(uMin) || Double.isNaN(uMax) || Double.isNaN(vMin) || Double.isNaN(vMax)
        || uMax <= uMin || vMax <= vMin) {
      return null;
    }
    double uStep = (uMax - uMin) / (uCount - 1);
    double vStep = (vMax - vMin) / (vCount - 1);

    double[][][] grid = new double[uCount][vCount][];
    // the same points, region or no region, so that a cell the boundary crosses is still a cell
    double[][][] unmasked = region == null ? null : new double[uCount][vCount][];
    boolean[][] inside = region == null ? null : new boolean[uCount][vCount];
    boolean any = false;
    for (int i = 0; i < uCount; i++) {
      double u = uMin + i * uStep;
      for (int j = 0; j < vCount; j++) {
        double v = vMin + j * vStep;
        double[] point = evaluatePoint(func, engine,
            F.List(F.Rule(uVar, F.num(u)), F.Rule(vVar, F.num(v))), monitor);
        if (region != null) {
          unmasked[i][j] = point;
          inside[i][j] = point != null && region.accepts(point[0], point[1], point[2], u, v);
          if (!inside[i][j]) {
            // the cells that touch it are cut along the edge of the region instead of drawn
            point = null;
          }
        }
        grid[i][j] = point;
        any |= point != null;
      }
    }
    if (!any) {
      return null;
    }

    double[] box = Plot3DTools.extentOf(grid);
    PlotColorFunction colorMap = colorBuilder
        .ranges(box[0], box[1], box[2], box[3], box[4], box[5], uMin, uMax, vMin, vMax).build();
    IExpr[][] colors = null;
    if (colorMap != null) {
      colors = new IExpr[uCount][vCount];
      for (int i = 0; i < uCount; i++) {
        for (int j = 0; j < vCount; j++) {
          double[] p = grid[i][j];
          if (p == null) {
            continue;
          }
          // the two parameters go with the point, so a surface can be coloured by either
          colors[i][j] = colorMap.color(p[0], p[1], p[2], uMin + i * uStep, vMin + j * vStep);
        }
      }
    }

    boolean byFunctions = meshFunctions != S.Automatic && !meshFunctions.isNone()
        && !meshOption.isNone();
    List<double[][]> values = new ArrayList<>();
    List<double[]> levels = new ArrayList<>();
    if (byFunctions) {
      IAST list = meshFunctions.isList() ? (IAST) meshFunctions : F.list(meshFunctions);
      double[] start = {uMin, vMin};
      double[] step = {uStep, vStep};
      for (int k = 1; k < list.size(); k++) {
        double[][] v = Plot3DTools.meshFunctionValues(grid, list.get(k), start, step, engine);
        values.add(v);
        levels.add(Plot3DTools.meshLevels(meshOption, k - 1, v));
      }
      IExpr[][] shades = Plot3DTools.meshShading(grid, values, levels, meshShading);
      if (shades != null) {
        colors = shades;
      }
      // the parameter grid is not drawn as well: the mesh functions replace it
      meshOption = S.None;
    }

    // a parametric surface may close on itself, and welding the seam is what keeps a torus from
    // showing a crease where the last row of quads meets the first
    boolean wrapU = closes(grid, true);
    boolean wrapV = closes(grid, false);
    // the boundary is placed by halving the parameters: a parametric surface can fold, so a
    // straight line between two of its points need not lie on it at all
    Plot3DTools.RegionEdge edge = region == null ? null
        : Plot3DTools.parameterEdge(new Plot3DTools.SurfaceSampler() {
          @Override
          public double[] point(double u, double v) {
            return evaluatePoint(func, engine,
                F.List(F.Rule(uVar, F.num(u)), F.Rule(vVar, F.num(v))), F.NIL);
          }

          @Override
          public boolean inside(double[] point, double u, double v) {
            return region.accepts(point[0], point[1], point[2], u, v);
          }
        }, uMin, uStep, vMin, vStep);
    Plot3DTools.addSurface(builder, grid, wrapU, wrapV, colors, true, meshOption, meshStyle,
        unmasked, inside, edge);
    if (byFunctions) {
      // traced once the grid is in the builder: the crossings are placed between its vertices
      IASTAppendable segments = F.ListAlloc(8);
      for (int k = 0; k < values.size(); k++) {
        segments.appendArgs(Plot3DTools.meshSegments(builder, grid, values.get(k), levels.get(k)));
      }
      Plot3DTools.addMeshSegments(builder, segments, meshStyle);
    }
    return grid;
  }

  /** Whether the grid's first and last row (or column) coincide, as a periodic surface's do. */
  private static boolean closes(double[][][] grid, boolean alongU) {
    int rows = grid.length;
    int cols = grid[0].length;
    if ((alongU ? rows : cols) < 3) {
      return false;
    }
    int count = alongU ? cols : rows;
    for (int k = 0; k < count; k++) {
      double[] first = alongU ? grid[0][k] : grid[k][0];
      double[] last = alongU ? grid[rows - 1][k] : grid[k][cols - 1];
      if (first == null || last == null) {
        return false;
      }
      for (int c = 0; c < 3; c++) {
        if (Math.abs(first[c] - last[c]) > 1e-9 * (1 + Math.abs(first[c]))) {
          return false;
        }
      }
    }
    return true;
  }

  private static double[] extent(double[][][] grid) {
    double[] bounds = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE,
        Double.MAX_VALUE, -Double.MAX_VALUE};
    for (double[][] row : grid) {
      for (double[] p : row) {
        if (p == null) {
          continue;
        }
        for (int c = 0; c < 3; c++) {
          bounds[c * 2] = Math.min(bounds[c * 2], p[c]);
          bounds[c * 2 + 1] = Math.max(bounds[c * 2 + 1], p[c]);
        }
      }
    }
    return bounds;
  }

  private static double fraction(double value, double min, double max) {
    return max > min ? (value - min) / (max - min) : 0.5;
  }

  /** Evaluate the parametrisation at one parameter value. */
  static double[] evaluatePoint(IExpr func, EvalEngine engine, IAST rules, IExpr monitor) {
    Plot3DTools.monitor(monitor, engine);
    try {
      IExpr result = engine.evaluate(F.subst(func, rules));
      if (!result.isList() || ((IAST) result).argSize() < 3) {
        return null;
      }
      IAST list = (IAST) result;
      double x = list.arg1().evalfNaN();
      double y = list.arg2().evalfNaN();
      double z = list.arg3().evalfNaN();
      if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
        return new double[] {x, y, z};
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public boolean localIterators() {
    // the plot variable is local to the plot
    return true;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.surfacePlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
