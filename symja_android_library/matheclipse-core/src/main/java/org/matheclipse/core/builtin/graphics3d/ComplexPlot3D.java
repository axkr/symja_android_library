package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ComplexColoring;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code ComplexPlot3D[f, {z, zmin, zmax}]} - the modulus of a complex function as height, with its
 * argument as colour.
 */
public class ComplexPlot3D extends AbstractFunctionOptionEvaluator {

  public ComplexPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    if (argSize < 2 || !ast.arg2().isList()) {
      return F.NIL;
    }
    IExpr function = ast.arg1();
    IAST range = (IAST) ast.arg2();
    if (range.argSize() < 1 || !range.arg1().isSymbol()) {
      return Errors.printMessage(S.ComplexPlot3D, "pllim", F.list(range), engine);
    }
    ISymbol zVar = (ISymbol) range.arg1();

    double minRe = -2.0;
    double maxRe = 2.0;
    double minIm = -2.0;
    double maxIm = 2.0;
    if (range.argSize() >= 3) {
      IComplexNum min = toComplex(engine.evalN(range.arg2()));
      IComplexNum max = toComplex(engine.evalN(range.arg3()));
      minRe = min.reDoubleValue();
      minIm = min.imDoubleValue();
      maxRe = max.reDoubleValue();
      maxIm = max.imDoubleValue();
    }
    if (!(maxRe > minRe) || !(maxIm > minIm)) {
      return Errors.printMessage(S.ComplexPlot3D, "pllim", F.list(range), engine);
    }

    int[] samples = Plot3DTools.plotPoints(options[Plot3DTools.X_PLOT_POINTS], 50);
    final int rows = samples[1];
    final int cols = samples[0];
    double dRe = (maxRe - minRe) / (cols - 1);
    double dIm = (maxIm - minIm) / (rows - 1);

    // the same colouring the 2D ComplexPlot uses, so a scheme looks the same in both
    ComplexColoring coloring = ComplexColoring.of(options[Plot3DTools.X_COLOR_FUNCTION],
        options[Plot3DTools.X_COLOR_FUNCTION_SCALING],
        new double[] {minRe, maxRe, minIm, maxIm}, S.ComplexPlot3D, engine);

    // RegionFunction is given the sample point and the value there, both complex, which is what
    // lets a predicate be written as Function({z, f}, Abs(z) < 2) or Function({z, f}, Abs(f) < 2)
    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);
    // the same points, region or no region, so that a cell the boundary crosses is still a cell
    double[][][] unmasked = region == null ? null : new double[rows][cols][];
    boolean[][] insideGrid = region == null ? null : new boolean[rows][cols];
    double[][][] grid = new double[rows][cols][];
    IExpr[][] colors = new IExpr[rows][cols];
    // the value at each vertex, kept until every one of them has been seen: the colouring scales
    // and ranks the values against each other, so none can be coloured before all are sampled
    double[][] valueRe = new double[rows][cols];
    double[][] valueIm = new double[rows][cols];
    double[] heights = new double[rows * cols];
    int finiteCount = 0;

    for (int i = 0; i < rows; i++) {
      double im = minIm + i * dIm;
      for (int j = 0; j < cols; j++) {
        double re = minRe + j * dRe;
        double height = Double.NaN;
        double fre = Double.NaN;
        double fim = Double.NaN;
        IExpr zVal = F.complexNum(re, im);
        IExpr value = F.NIL;
        try {
          Plot3DTools.monitor(options[Plot3DTools.X_EVALUATION_MONITOR], engine);
          value = engine.evalN(F.subst(function, F.Rule(zVar, zVal)));
          if (value instanceof INumber) {
            IComplexNum cn = toComplex(value);
            height = cn.dabs();
            fre = cn.reDoubleValue();
            fim = cn.imDoubleValue();
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
        if (region != null) {
          unmasked[i][j] = new double[] {re, im, height};
          insideGrid[i][j] = region.accepts(zVal, value);
          if (!insideGrid[i][j]) {
            // the cells that touch it are cut along the edge of the region instead of drawn, and
            // the rim of the hole is what BoundaryStyle then outlines
            continue;
          }
        }
        if (Double.isFinite(height)) {
          heights[finiteCount++] = height;
        }
        grid[i][j] = new double[] {re, im, height};
        valueRe[i][j] = fre;
        valueIm[i][j] = fim;
        coloring.observe(re, im, fre, fim);
      }
    }
    coloring.prepare();
    for (int i = 0; i < rows; i++) {
      double im = minIm + i * dIm;
      for (int j = 0; j < cols; j++) {
        if (grid[i][j] != null) {
          colors[i][j] = coloring.color(minRe + j * dRe, im, valueRe[i][j], valueIm[i][j]);
        }
      }
    }
    if (finiteCount == 0) {
      return F.NIL;
    }

    // a pole would otherwise take the whole box with it, so the height is capped at the body of
    // the data and the pole comes out as a flat topped column
    double[] valid = new double[finiteCount];
    System.arraycopy(heights, 0, valid, 0, finiteCount);
    double[] plotRange = GraphicsUtil.automaticPlotRange3D(valid);
    double maxHeight = plotRange[1] > plotRange[0] ? plotRange[1] : 1.0;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        for (double[] point : new double[][] {grid[i][j],
            unmasked == null ? null : unmasked[i][j]}) {
          if (point == null) {
            continue;
          }
          double h = point[2];
          if (!Double.isFinite(h) || h > maxHeight) {
            point[2] = maxHeight;
          }
        }
      }
    }
    final double capped = maxHeight;

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, true);
    Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(0, options[Plot3DTools.X_PLOT_STYLE]),
        options[Plot3DTools.X_MESH]);
    // the boundary is placed by halving the sample point rather than the drawn coordinates, so
    // that the height at the crossing is the one the function really takes there
    Plot3DTools.RegionEdge edge = region == null ? null
        : Plot3DTools.parameterEdge(new Plot3DTools.SurfaceSampler() {
          @Override
          public double[] point(double re, double im) {
            double height = heightAt(function, zVar, re, im, engine);
            return new double[] {re, im,
                Double.isFinite(height) ? Math.min(height, capped) : capped};
          }

          @Override
          public boolean inside(double[] at, double re, double im) {
            IExpr value = valueAt(function, zVar, re, im, engine);
            return region.accepts(F.complexNum(re, im), value);
          }
        }, minRe, dRe, minIm, dIm);
    Plot3DTools.addSurface(builder, grid, false, false, colors, true, options[Plot3DTools.X_MESH],
        options[Plot3DTools.X_MESH_STYLE], unmasked, insideGrid, edge);
    IExpr graphicsComplex = builder.build();
    if (graphicsComplex.isNIL()) {
      return F.NIL;
    }
    // the rim of the domain, drawn outside the GraphicsComplex so it keeps its own colour rather
    // than being shaded like the surface underneath it
    IExpr boundaryStyle = options[Plot3DTools.X_BOUNDARY_STYLE];
    if (!boundaryStyle.isNone() && grid.length > 1 && grid[0].length > 1) {
      // A region punches holes in the rectangle, so the rim is no longer one closed line round it.
      // The shared outline follows the edge of whatever was drawn, holes included, which is the
      // edge the region cut.
      IExpr outline = region == null ? boundaryLine(grid) : Plot3DTools.surfaceBoundary(grid);
      graphicsComplex = F.List(graphicsComplex,
          boundaryStyle == S.Automatic ? S.Black : boundaryStyle, outline);
    }

    return Plot3DTools.graphics3D(graphicsComplex, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS), F.Rule(S.Axes, S.True),
            // the colour carries the meaning here, so the lights must not tint it
            F.Rule(S.Lighting, F.stringx("Neutral"))});
  }

  /** The function at one point of the plane, or {@code F.NIL} where it has no value. */
  private static IExpr valueAt(IExpr function, ISymbol zVar, double re, double im,
      EvalEngine engine) {
    try {
      return engine.evalN(F.subst(function, F.Rule(zVar, F.complexNum(re, im))));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /** Its modulus, which is the height the surface is drawn at. */
  private static double heightAt(IExpr function, ISymbol zVar, double re, double im,
      EvalEngine engine) {
    IExpr value = valueAt(function, zVar, re, im, engine);
    return value instanceof INumber ? F.complexNum(((INumber) value).reDoubleValue(),
        ((INumber) value).imDoubleValue()).dabs() : Double.NaN;
  }

  private IComplexNum toComplex(IExpr expr) {
    if (expr instanceof INumber) {
      if (expr instanceof IComplexNum) {
        return (IComplexNum) expr;
      }
      return F.complexNum(((INumber) expr).reDoubleValue(), 0.0);
    }
    try {
      if (expr.isNumber()) {
        return F.complexNum(expr.evalfc());
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.complexNum(0.0, 0.0);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  /** The four edges of the sampled rectangle, as one closed line through the surface. */
  private static IExpr boundaryLine(double[][][] grid) {
    int n = grid.length;
    int m = grid[0].length;
    IASTAppendable points = F.ListAlloc(2 * (n + m));
    for (int i = 0; i < n; i++) {
      points.append(point(grid[i][0]));
    }
    for (int j = 1; j < m; j++) {
      points.append(point(grid[n - 1][j]));
    }
    for (int i = n - 2; i >= 0; i--) {
      points.append(point(grid[i][m - 1]));
    }
    for (int j = m - 2; j >= 0; j--) {
      points.append(point(grid[0][j]));
    }
    return F.Line(points);
  }

  private static IAST point(double[] xyz) {
    return F.List(F.num(xyz[0]), F.num(xyz[1]), F.num(xyz[2]));
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // The surface is read by its colour, so draws no mesh over it and outlines the domain
    // in black instead. Both are changes of default only: they are applied with override so that
    // the options keep the positions the shared X_* constants name them by. Declaring them ahead
    // of the shared block instead would put every later option one place along, and each plot
    // would quietly start reading its neighbour's value.
    GraphicsOptions.OptionSet options = Plot3DTools
        .frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()) //
            .add(S.Automatic, S.DataRange)) //
        .override(S.Mesh, S.None) //
        .override(S.BoundaryStyle, S.Black);
    setOptions(newSymbol, options.keys(), options.values());
  }
}
