package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating 2D density plots.
 * <p>
 * Example: <code>DensityPlot[Sin[x] * Cos[y], {x, -2, 2}, {y, -2, 2}]</code>
 */
public class DensityPlot extends ListPlot {

  // "Sunset" color map control points (RGB)
  private static final double[][] COLOR_MAP = {{0.10, 0.05, 0.30}, // Dark Indigo
      {0.35, 0.10, 0.55}, // Purple
      {0.75, 0.15, 0.45}, // Red-Magenta
      {0.95, 0.55, 0.15}, // Orange
      {0.98, 0.90, 0.60} // Pale Yellow
  };

  public DensityPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 3) {
      return F.NIL;
    }

    // 1. Initialize Options
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Increase default resolution to 50 for smoother gradients
    int plotPoints = 50;
    boolean colorFunctionScaling = true;

    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();

        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.ColorFunctionScaling:
              if (val.isFalse())
                colorFunctionScaling = false;
              break;
            case ID.PlotPoints:
              plotPoints = val.toIntDefault(50);
              break;
          }
        }
      }
    }

    IExpr function = ast.arg1();
    IExpr xIter = ast.arg2();
    IExpr yIter = ast.arg3();

    // 2. Parse Ranges
    double[] xRange = parseRange(xIter, engine);
    double[] yRange = parseRange(yIter, engine);
    if (xRange == null || yRange == null) {
      return F.NIL;
    }

    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});

    ISymbol xVar = (ISymbol) ((IAST) xIter).arg1();
    ISymbol yVar = (ISymbol) ((IAST) yIter).arg1();

    // 3. Generate Grid
    int gridX = plotPoints;
    int gridY = plotPoints;
    double[][] zGrid = new double[gridX + 1][gridY + 1];
    double stepX = (xRange[1] - xRange[0]) / gridX;
    double stepY = (yRange[1] - yRange[0]) / gridY;

    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;

    for (int i = 0; i <= gridX; i++) {
      double xVal = xRange[0] + i * stepX;
      for (int j = 0; j <= gridY; j++) {
        double yVal = yRange[0] + j * stepY;

        IExpr valExpr =
            function.replaceAll(F.List(F.Rule(xVar, F.num(xVal)), F.Rule(yVar, F.num(yVal))));

        double z = Double.NaN;
        try {
          IExpr result = engine.evaluate(valExpr);
          // Use evalf() per user guidelines, then extract value
          z = result.evalf();
          // if (num instanceof INumber) {
          // z = ((INumber) num).reDoubleValue();
          // }
        } catch (Exception e) {
          z = Double.NaN;
        }

        zGrid[i][j] = z;
        if (Double.isFinite(z)) {
          if (z < minZ)
            minZ = z;
          if (z > maxZ)
            maxZ = z;
        }
      }
    }

    if (minZ == Double.MAX_VALUE)
      return F.NIL;

    // 4. Generate Primitives (Rectangles)
    IASTAppendable primitives = F.ListAlloc();
    // Use EdgeForm[None] globally to remove grid lines between rectangles
    primitives.append(F.EdgeForm(S.None));

    for (int i = 0; i < gridX; i++) {
      double x = xRange[0] + i * stepX;
      for (int j = 0; j < gridY; j++) {
        double y = yRange[0] + j * stepY;

        // Calculate representative Z for the cell (average of 4 corners)
        double v00 = zGrid[i][j];
        double v10 = zGrid[i + 1][j];
        double v01 = zGrid[i][j + 1];
        double v11 = zGrid[i + 1][j + 1];

        if (Double.isNaN(v00) || Double.isNaN(v10) || Double.isNaN(v01) || Double.isNaN(v11)) {
          continue;
        }

        double cellZ = (v00 + v10 + v01 + v11) / 4.0;

        double t = 0.5;
        if (colorFunctionScaling) {
          if (Math.abs(maxZ - minZ) > 1e-9) {
            t = (cellZ - minZ) / (maxZ - minZ);
          }
        } else {
          t = cellZ;
        }

        IExpr color = getDensityColor(t);

        primitives.append(color);
        primitives.append(
            F.Rectangle(F.List(F.num(x), F.num(y)), F.List(F.num(x + stepX), F.num(y + stepY))));
      }
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * Interpolates colors to match the "Sunset" density gradient. Maps t [0..1] -> [Deep Purple ...
   * Yellow]
   */
  private IExpr getDensityColor(double t) {
    if (t < 0)
      t = 0;
    if (t > 1)
      t = 1;

    int n = COLOR_MAP.length - 1;
    double pos = t * n;
    int idx = (int) pos;
    if (idx >= n) {
      double[] c = COLOR_MAP[n];
      return F.RGBColor(c[0], c[1], c[2]);
    }

    double frac = pos - idx;
    double[] c1 = COLOR_MAP[idx];
    double[] c2 = COLOR_MAP[idx + 1];

    double r = c1[0] + (c2[0] - c1[0]) * frac;
    double g = c1[1] + (c2[1] - c1[1]) * frac;
    double b = c1[2] + (c2[2] - c1[2]) * frac;

    return F.RGBColor(r, g, b);
  }

  private double[] parseRange(IExpr iter, EvalEngine engine) {
    if (iter.isList() && iter.size() >= 3) {
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
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST astPlot) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
