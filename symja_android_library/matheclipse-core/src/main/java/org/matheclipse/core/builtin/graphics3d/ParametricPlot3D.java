package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
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
    if (argSize < 2 || !ast.arg1().isList()) {
      return F.NIL;
    }
    boolean isSurface = argSize >= 3 && ast.arg2().isList() && ast.arg3().isList();

    List<IExpr> functions = new ArrayList<>();
    IAST listArg = (IAST) ast.arg1();
    if (listArg.argSize() > 0 && listArg.arg1().isList()) {
      for (int i = 1; i <= listArg.argSize(); i++) {
        functions.add(listArg.get(i));
      }
    } else {
      functions.add(listArg);
    }

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
      Plot3DTools.ColorMap colorMap = Plot3DTools.colorMap(options[Plot3DTools.X_COLOR_FUNCTION],
          options[Plot3DTools.X_COLOR_FUNCTION_SCALING], engine);

      for (int i = 0; i < functions.size(); i++) {
        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colorMap != null);
        Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(i, plotStyle), meshOption);
        double[][][] grid =
            createSurfaceGeometry(functions.get(i), uRange, vRange, samples[0], samples[1], engine,
                builder, colorMap, meshOption, options[Plot3DTools.X_MESH_STYLE],
                options[Plot3DTools.X_EVALUATION_MONITOR], region);
        if (grid != null) {
          // the rim of the surface, and the rim of every hole a RegionFunction cut in it
          IExpr complex = Plot3DTools.withBoundary(builder.build(), grid,
              options[Plot3DTools.X_BOUNDARY_STYLE]);
          if (complex.isPresent()) {
            graphicsList.append(complex);
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
      for (int i = 0; i < functions.size(); i++) {
        GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
        // a curve is a line: no mesh, no edge form, and the ordinary plot colours
        builder.setStyle(Plot3DTools.curveStyle(i, plotStyle));
        createCurveGeometry(functions.get(i), range, samples[0], engine, builder,
            options[Plot3DTools.X_EVALUATION_MONITOR], region);
        IExpr complex = builder.build();
        if (complex.isPresent()) {
          graphicsList.append(complex);
        }
      }
    }

    if (graphicsList.argSize() == 0) {
      return F.NIL;
    }
    return Plot3DTools.graphics3D(graphicsList, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios, options[Plot3DTools.X_BOX_RATIOS]), F.Rule(S.Axes, S.True),
            F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
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

  /** The sampled grid, or {@code null} when the parametrisation gave nothing to draw. */
  private double[][][] createSurfaceGeometry(IExpr func, IAST uRange, IAST vRange, int uCount,
      int vCount, EvalEngine engine, GraphicsComplexBuilder builder, Plot3DTools.ColorMap colorMap,
      IExpr meshOption, IExpr meshStyle, IExpr monitor, RegionFunctionFilter region) {
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

    IExpr[][] colors = null;
    if (colorMap != null) {
      colors = new IExpr[uCount][vCount];
      double[] bounds = extent(grid);
      for (int i = 0; i < uCount; i++) {
        for (int j = 0; j < vCount; j++) {
          double[] p = grid[i][j];
          if (p == null) {
            continue;
          }
          colors[i][j] = colorMap.isScaled()
              ? colorMap.apply(fraction(p[0], bounds[0], bounds[1]),
                  fraction(p[1], bounds[2], bounds[3]), fraction(p[2], bounds[4], bounds[5]))
              : colorMap.apply(p[0], p[1], p[2]);
        }
      }
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
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.surfacePlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
