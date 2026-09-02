package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
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

    Plot3DTools.ColorMap colorMap = Plot3DTools.colorMap(options[Plot3DTools.X_COLOR_FUNCTION],
        options[Plot3DTools.X_COLOR_FUNCTION_SCALING], engine);

    // RegionFunction is given the sample point and the value there, both complex, which is what
    // lets a predicate be written as Function({z, f}, Abs(z) < 2) or Function({z, f}, Abs(f) < 2)
    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);
    double[][][] grid = new double[rows][cols][];
    IExpr[][] colors = new IExpr[rows][cols];
    double[] heights = new double[rows * cols];
    int finiteCount = 0;

    for (int i = 0; i < rows; i++) {
      double im = minIm + i * dIm;
      for (int j = 0; j < cols; j++) {
        double re = minRe + j * dRe;
        double height = Double.NaN;
        double arg = 0.0;
        IExpr zVal = F.complexNum(re, im);
        IExpr value = F.NIL;
        try {
          Plot3DTools.monitor(options[Plot3DTools.X_EVALUATION_MONITOR], engine);
          value = engine.evalN(F.subst(function, F.Rule(zVar, zVal)));
          if (value instanceof INumber) {
            IComplexNum cn = toComplex(value);
            height = cn.dabs();
            arg = cn.complexArg().evalfNaN();
          }
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
        }
        if (region != null && !region.accepts(zVal, value)) {
          // a hole in the surface: addSurface skips every quad that touches it, and the rim of
          // the hole is what BoundaryStyle then outlines
          continue;
        }
        if (Double.isFinite(height)) {
          heights[finiteCount++] = height;
        }
        grid[i][j] = new double[] {re, im, height};
        double hue = (arg + Math.PI) / (2 * Math.PI);
        // the argument runs round the colour wheel, which is the convention for a domain colouring
        colors[i][j] = colorMap != null ? colorMap.apply(re, im, hue)
            : F.Hue(F.num(hue), F.num(0.6), F.num(1.0));
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
        if (grid[i][j] == null) {
          continue;
        }
        double h = grid[i][j][2];
        if (!Double.isFinite(h) || h > maxHeight) {
          grid[i][j][2] = maxHeight;
        }
      }
    }

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, true);
    Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(0, options[Plot3DTools.X_PLOT_STYLE]),
        options[Plot3DTools.X_MESH]);
    Plot3DTools.addSurface(builder, grid, false, false, colors, true, options[Plot3DTools.X_MESH],
        options[Plot3DTools.X_MESH_STYLE]);
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
