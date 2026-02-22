package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
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

  public ContourPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 3) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    int plotPoints = 25;
    int numberOfContours = 10;
    IExpr contourStyle = S.Automatic;
    IExpr contourShading = S.Automatic;
    boolean colorFunctionScaling = true;

    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();

        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.Contours:
              if (val.isInteger())
                numberOfContours = val.toIntDefault(10);
              break;
            case ID.ContourStyle:
              contourStyle = val;
              break;
            case ID.ContourShading:
              contourShading = val;
              break;
            case ID.ColorFunctionScaling:
              if (val.isFalse())
                colorFunctionScaling = false;
              break;
            case ID.PlotPoints:
              plotPoints = val.toIntDefault(25);
              break;
          }
        }
      }
    }

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
      return Errors.printMessage(S.ContourPlot, "pllim", F.list(yIter), engine);
    }

    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});

    ISymbol xVar = (ISymbol) ((IAST) xIter).arg1();
    ISymbol yVar = (ISymbol) ((IAST) yIter).arg1();

    IASTAppendable primitives = F.ListAlloc();

    if (functionArg.isList()) {
      // Multiple functions/equations
      IAST list = (IAST) functionArg;
      int count = 0;
      for (int i = 1; i < list.size(); i++) {
        IExpr func = list.get(i);

        // Determine style for this specific curve
        IExpr style = contourStyle;
        if (style.isList()) {
          style = GraphicsOptions.getPlotStyle(style, count);
        } else if (style.isAutomatic()) {
          // If Automatic, cycle colors for multiple equations
          style = GraphicsOptions.plotStyleColorExpr(count, F.NIL);
        }

        generateContours(primitives, func, xRange, yRange, xVar, yVar, plotPoints, numberOfContours,
            style, contourShading, colorFunctionScaling, engine, true);
        count++;
      }
    } else {
      // Single function/equation
      generateContours(primitives, functionArg, xRange, yRange, xVar, yVar, plotPoints,
          numberOfContours, contourStyle, contourShading, colorFunctionScaling, engine, false);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private void generateContours(IASTAppendable primitives, IExpr function, double[] xRange,
      double[] yRange, ISymbol xVar, ISymbol yVar, int plotPoints, int numberOfContours,
      IExpr contourStyle, IExpr contourShading, boolean colorFunctionScaling, EvalEngine engine,
      boolean isMulti) {

    boolean isEquation = false;
    if (function.isAST(S.Equal, 3)) {
      isEquation = true;
      function = F.Subtract(function.first(), function.second());
    }

    // Default shading to None for equations unless specified
    if (isEquation && contourShading.equals(S.Automatic)) {
      contourShading = S.None;
    }

    int gridX = plotPoints;
    int gridY = plotPoints;
    double[][] zGrid = new double[gridX + 1][gridY + 1];
    double stepX = (xRange[1] - xRange[0]) / gridX;
    double stepY = (yRange[1] - yRange[0]) / gridY;

    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;

    // 1. Compute Grid
    for (int i = 0; i <= gridX; i++) {
      double xVal = xRange[0] + i * stepX;
      for (int j = 0; j <= gridY; j++) {
        double yVal = yRange[0] + j * stepY;
        IExpr valExpr =
            function.replaceAll(F.List(F.Rule(xVar, F.num(xVal)), F.Rule(yVar, F.num(yVal))));

        double z = Double.NaN;
        try {
          IExpr result = engine.evaluate(valExpr);
          z = result.evalDouble();
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
      return;

    // 2. Determine Levels
    double[] levels;
    if (isEquation) {
      levels = new double[] {0.0};
    } else {
      levels = new double[numberOfContours];
      double levelStep = (maxZ - minZ) / (numberOfContours + 1);
      for (int k = 0; k < numberOfContours; k++) {
        levels[k] = minZ + (k + 1) * levelStep;
      }
    }

    // 3. Shading (Polygons)
    if (!contourShading.equals(S.None) && !contourShading.isFalse()) {
      for (int k = -1; k < levels.length; k++) {
        double level = (k == -1) ? minZ - 1.0 : levels[k];

        IExpr color;
        if (contourShading.isList()) {
          color = GraphicsOptions.getPlotStyle(contourShading, k + 1);
        } else {
          double lower = (k == -1) ? minZ : levels[k];
          double upper = (k == levels.length - 1) ? maxZ : levels[k + 1];
          double bandZ = (lower + upper) * 0.5;
          double t = (colorFunctionScaling && !isEquation) ? (bandZ - minZ) / (maxZ - minZ) : bandZ;
          color = getShadingColor(t);
        }

        IASTAppendable polygons = F.ListAlloc();
        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = xRange[0] + i * stepX;
            double y = yRange[0] + j * stepY;
            processCellPolygon(polygons, x, y, stepX, stepY, zGrid[i][j], zGrid[i + 1][j],
                zGrid[i + 1][j + 1], zGrid[i][j + 1], level);
          }
        }

        if (polygons.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          group.append(F.EdgeForm(S.None));
          group.append(color);
          for (IExpr poly : polygons)
            group.append(poly);
          primitives.append(group);
        }
      }
    }

    // 4. Contour Lines
    if (!contourStyle.equals(S.None) && !contourStyle.isFalse()) {
      for (int k = 0; k < levels.length; k++) {
        double level = levels[k];
        IASTAppendable lineSegments = F.ListAlloc();

        IExpr currentStyle = contourStyle;
        if (currentStyle.equals(S.Automatic)) {
          // Single plot default: Gray
          // Multi plot default: Passed in via argument (handled in evaluate loop)
          currentStyle = isMulti ? contourStyle : (isEquation ? S.Black : F.GrayLevel(0.5));
        } else if (currentStyle.isList() && !isMulti) {
          // If it's a list of styles for LEVELS (not equations), pick per level
          currentStyle = GraphicsOptions.getPlotStyle(currentStyle, k);
        }
        // Note: If isMulti is true, contourStyle passed here is already the specific color for
        // this equation

        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = xRange[0] + i * stepX;
            double y = yRange[0] + j * stepY;
            processCellLine(lineSegments, x, y, stepX, stepY, zGrid[i][j], zGrid[i + 1][j],
                zGrid[i + 1][j + 1], zGrid[i][j + 1], level);
          }
        }

        if (lineSegments.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          if (currentStyle.isList())
            group.append(F.Directive(currentStyle));
          else
            group.append(currentStyle);

          for (IExpr line : lineSegments)
            group.append(line);
          primitives.append(group);
        }
      }
    }
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
  }

  private IExpr getShadingColor(double t) {
    double val = t;
    if (val < 0 || val > 1.0) {
      val = val - Math.floor(val);
    }
    return F.Hue(F.num(0.66 * (1.0 - val)));
  }

  // --- Marching Squares: Polygons ---

  private void processCellPolygon(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {
    int index = 0;
    if (v0 >= level)
      index |= 1;
    if (v1 >= level)
      index |= 2;
    if (v2 >= level)
      index |= 4;
    if (v3 >= level)
      index |= 8;

    if (index == 0)
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
    } else if (index == 5) { // Saddle
      IASTAppendable t1 = F.ListAlloc();
      add(t1, pts[3]);
      add(t1, x, y);
      add(t1, pts[0]);
      out.append(F.Polygon(t1));
      IASTAppendable t2 = F.ListAlloc();
      add(t2, pts[1]);
      add(t2, x + dx, y + dy);
      add(t2, pts[2]);
      out.append(F.Polygon(t2));
      return;
    } else if (index == 10) { // Saddle
      IASTAppendable t1 = F.ListAlloc();
      add(t1, pts[0]);
      add(t1, x + dx, y);
      add(t1, pts[1]);
      out.append(F.Polygon(t1));
      IASTAppendable t2 = F.ListAlloc();
      add(t2, pts[2]);
      add(t2, x, y + dy);
      add(t2, pts[3]);
      out.append(F.Polygon(t2));
      return;
    }

    if (polyPts.argSize() > 0) {
      out.append(F.Polygon(polyPts));
    }
  }

  private void add(IASTAppendable list, double[] p) {
    list.append(F.List(F.num(p[0]), F.num(p[1])));
  }

  private void add(IASTAppendable list, double x, double y) {
    list.append(F.List(F.num(x), F.num(y)));
  }

  // --- Marching Squares: Lines ---

  private void processCellLine(IASTAppendable out, double x, double y, double dx, double dy,
      double v0, double v1, double v2, double v3, double level) {

    int index = 0;
    if (v0 >= level)
      index |= 1;
    if (v1 >= level)
      index |= 2;
    if (v2 >= level)
      index |= 4;
    if (v3 >= level)
      index |= 8;

    int[] edges = ISO_LINES[index];
    for (int i = 0; i < edges.length; i += 2) {
      int e1 = edges[i];
      int e2 = edges[i + 1];
      if (e1 == -1)
        break;
      double[] p1 = interp(x, y, dx, dy, e1, v0, v1, v2, v3, level);
      double[] p2 = interp(x, y, dx, dy, e2, v0, v1, v2, v3, level);
      out.append(
          F.Line(F.List(F.List(F.num(p1[0]), F.num(p1[1])), F.List(F.num(p2[0]), F.num(p2[1])))));
    }
  }

  private double[] interp(double x, double y, double dx, double dy, int edge, double v0, double v1,
      double v2, double v3, double level) {
    return interp(x, y, dx, dy, edge, getVal(edge, 0, v0, v1, v2, v3),
        getVal(edge, 1, v0, v1, v2, v3), level);
  }

  private double getVal(int edge, int endpoint, double v0, double v1, double v2, double v3) {
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

  private double[] interp(double x, double y, double dx, double dy, int edge, double valA,
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

  private static final int[][] ISO_LINES = {{-1, -1, -1, -1}, {3, 0, -1, -1}, {0, 1, -1, -1},
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
  public void setUp(final ISymbol newSymbol) {
    IExpr[] optionValues = GraphicsOptions.contourPlotDefaultOptionValues(false, false);
    optionValues[GraphicsOptions.X_AXES] = S.False;
    optionValues[GraphicsOptions.X_FRAME] = S.True;
    optionValues[GraphicsOptions.X_ASPECTRATIO] = F.C1;
    setOptions(newSymbol, GraphicsOptions.contourPlotDefaultOptionKeys(), optionValues);
    // setOptions(newSymbol, //
    // new IBuiltInSymbol[] {S.ContourShading, S.ColorFunctionScaling, S.ContourStyle,
    // S.AspectRatio, S.Frame, S.Axes}, //
    // new IExpr[] {S.Automatic, S.True, S.Automatic, F.C1, S.True, S.False} //
    // );
  }
}
