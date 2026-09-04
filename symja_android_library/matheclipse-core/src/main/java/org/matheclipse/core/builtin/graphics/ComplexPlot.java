package org.matheclipse.core.builtin.graphics;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ComplexColoring;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Complex Plots with advanced domain coloring.
 * <p>
 * Example:
 * <code>ComplexPlot[(z^2 + 1) / (z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> "CyclicLogAbsArg"]</code>
 * <p>
 * The colouring itself lives in {@link org.matheclipse.core.graphics.ComplexColoring}, which
 * {@code ComplexPlot3D} shares, so the two plots cannot drift apart on what a scheme looks like.
 */
public class ComplexPlot extends ListPlot {

  /**
   * The data is drawn as one field rather than point by point, so no wrapper is read here; the
   * label goes over the whole picture instead.
   */
  @Override
  protected boolean readsArgumentWrapper() {
    return false;
  }

  public ComplexPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr f = ast.arg1();

    IExpr rangeSpec = ast.arg2();
    ISymbol zVar = null;
    IExpr zMin = null;
    IExpr zMax = null;

    // Parse Range {z, n} or {z, min, max}
    if (rangeSpec.isList()) {
      IAST rangeList = (IAST) rangeSpec;
      if (rangeList.argSize() >= 2 && rangeList.arg1().isSymbol()) {
        zVar = (ISymbol) rangeList.arg1();
        if (rangeList.argSize() == 2) {
          // {z, n} -> -n-nI to n+nI
          IExpr n = rangeList.arg2();
          zMin = F.Times(F.CN1, F.Plus(n, F.Times(n, F.CI))); // -n - n I
          zMax = F.Plus(n, F.Times(n, F.CI)); // n + n I
        } else if (rangeList.argSize() >= 3) {
          // {z, min, max}
          zMin = rangeList.arg2();
          zMax = rangeList.arg3();
        }
      }
    }

    if (zVar == null || zMin == null || zMax == null) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.ComplexPlot, "pllim", F.list(rangeSpec), engine);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Handle PlotLegends -> Automatic for ComplexPlot (Phase Legend)
    if (graphicsOptions.plotLegends().isAutomatic()) {
      graphicsOptions.setPlotLegends(F.BarLegend(S.Hue, F.List(F.num(-Math.PI), F.num(Math.PI))));
    }

    // Default defaults
    int plotPoints = 100; // Higher default for raster-like quality

    IExpr colorFunctionSpec =
        GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic);
    IExpr colorFunctionScaling =
        GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True);
    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key == S.PlotPoints) {
          plotPoints = val.toIntDefault(100);
        }
      }
    }

    double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
    try {
      Complex cMin = zMin.evalfc();
      Complex cMax = zMax.evalfc();
      x0 = cMin.getRealPart();
      y0 = cMin.getImaginaryPart();
      x1 = cMax.getRealPart();
      y1 = cMax.getImaginaryPart();
    } catch (Exception e) {
      return F.NIL;
    }

    double dx = (x1 - x0) / plotPoints;
    double dy = (y1 - y0) / plotPoints;

    IASTAppendable primitives = F.ListAlloc();
    // the grid becomes a single raster rather than one rectangle per cell
    IExpr[][] cells = new IExpr[plotPoints][plotPoints];
    // RegionFunction is given the sample point and the value there, both complex, which is what
    // lets a predicate be written as Function({z, f}, Abs(z) < 2) or Function({z, f}, Abs(f) < 2)
    RegionFunctionFilter region = RegionFunctionFilter.of(
        GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic), engine);
    ComplexColoring coloring = ComplexColoring.of(colorFunctionSpec, colorFunctionScaling,
        new double[] {x0, x1, y0, y1}, S.ComplexPlot, engine);

    // The values are sampled before any of them is coloured: the scaling of a ColorFunction, and
    // the schemes that shade by the rank of Abs(f), both need the whole picture before they can
    // answer for one cell of it.
    double[][] valueRe = new double[plotPoints][plotPoints];
    double[][] valueIm = new double[plotPoints][plotPoints];
    boolean[][] painted = new boolean[plotPoints][plotPoints];
    for (int i = 0; i < plotPoints; i++) {
      double rMid = x0 + (i + 0.5) * dx;
      for (int j = 0; j < plotPoints; j++) {
        double iMid = y0 + (j + 0.5) * dy;
        IExpr zVal = F.complexNum(rMid, iMid);

        try {
          // Evaluate f[z]
          IExpr valExpr = f.replaceAll(F.List(F.Rule(zVar, zVal)));
          IExpr res = engine.evaluate(valExpr);
          if (region != null && !region.accepts(zVal, res)) {
            // a cell the region rejects stays transparent, the same way one whose value is not a
            // number does
            continue;
          }
          double[] value = complexValue(res);
          if (value == null) {
            continue;
          }
          valueRe[i][j] = value[0];
          valueIm[i][j] = value[1];
          painted[i][j] = true;
          coloring.observe(rMid, iMid, value[0], value[1]);
        } catch (RuntimeException rex) {
          return Errors.printMessage(S.ComplexPlot, rex);
        }
      }
    }
    coloring.prepare();

    for (int i = 0; i < plotPoints; i++) {
      double rMid = x0 + (i + 0.5) * dx;
      for (int j = 0; j < plotPoints; j++) {
        if (painted[i][j]) {
          // j counts upwards from the bottom, while the raster rows are given top first
          cells[plotPoints - 1 - j][i] =
              coloring.color(rMid, y0 + (j + 0.5) * dy, valueRe[i][j], valueIm[i][j]);
        }
      }
    }
    // a domain colouring is a picture of a function, so the sampling grid is smoothed over
    primitives.append(GraphicsOptions.smoothRasterTopFirst(cells, x0, y0, x1, y1));

    graphicsOptions.setBoundingBox(new double[] {x0, x1, y0, y1});

    // Ensure AspectRatio -> Automatic (1:1) is default for ComplexPlot unless overridden
    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.C1);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * The value of the plotted function at one sample point, as a real and an imaginary part.
   *
   * <p>
   * A pole comes back as a pair of non-finite doubles rather than as nothing: a point where the
   * function blows up is part of the picture and is drawn white, while a point where it has no
   * numeric value at all is not, and leaves a hole.
   *
   * @return {@code {re, im}}, or {@code null} when the value is not a number
   */
  private static double[] complexValue(IExpr value) {
    if (value.isDirectedInfinity() || value == S.ComplexInfinity || value == S.Indeterminate) {
      return new double[] {Double.NaN, Double.NaN};
    }
    if (!value.isNumber()) {
      return null;
    }
    try {
      Complex c = value.evalfc();
      return new double[] {c.getRealPart(), c.getImaginaryPart()};
    } catch (RuntimeException rex) {
      return null;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);

    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.densityExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
