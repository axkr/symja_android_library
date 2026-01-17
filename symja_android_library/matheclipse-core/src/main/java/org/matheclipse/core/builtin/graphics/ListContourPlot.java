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
 * Functions for generating 2D contour plots from list data. Supports {{z1, z2,...}, ...} (Regular
 * Grid) and {{x1,y1,z1}, ...} (Irregular/Scattered).
 */
public class ListContourPlot extends ContourPlot {

  public ListContourPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = ast.arg1();
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Default Options
    IExpr contoursOption = S.Automatic; // Store the full expression, not just an int
    IExpr contourStyle = S.Automatic;
    IExpr contourShading = S.Automatic;
    boolean colorFunctionScaling = true;
    IExpr dataRange = S.Automatic;
    int gridResolution = 25;

    // Parse Options
    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();

        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.Contours:
              contoursOption = val; // Capture the full value (Integer, List, or Automatic)
              break;
            case ID.ContourStyle:
              contourStyle = val;
              break;
            case ID.ContourShading:
              contourShading = val;
              break;
            case ID.ColorFunctionScaling:
              if (val.isFalse()) {
                colorFunctionScaling = false;
              }
              break;
            case ID.DataRange:
              dataRange = val;
              break;
            case ID.MaxPlotPoints:
              // case ID.PlotPoints:
              if (val.isInteger()) {
                gridResolution = val.toIntDefault(25);
              } else if (val.isList() && val.size() >= 3) {
                gridResolution = ((IAST) val).arg1().toIntDefault(25);
              }
              break;
          }
        }
      }
    }

    // Prepare Grid Data
    GridData gridData = prepareGridData(dataArg, dataRange, gridResolution, engine);
    if (gridData == null) {
      return F.NIL;
    }

    graphicsOptions
        .setBoundingBox(new double[] {gridData.xMin, gridData.xMax, gridData.yMin, gridData.yMax});

    // Generate Primitives
    IASTAppendable primitives = F.ListAlloc();
    generateContoursFromGrid(primitives, gridData, contoursOption, contourStyle, contourShading,
        colorFunctionScaling);

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  // Internal class to hold processed grid data
  private static class GridData {
    double[][] zGrid;
    double xMin, xMax, yMin, yMax;
    double stepX, stepY;
    double minZ = Double.MAX_VALUE;
    double maxZ = -Double.MAX_VALUE;

    public GridData(int width, int height) {
      zGrid = new double[width][height];
    }
  }

  private GridData prepareGridData(IExpr data, IExpr dataRange, int gridRes, EvalEngine engine) {
    if (!data.isList())
      return null;
    IAST list = (IAST) data;
    if (list.isEmpty())
      return null;

    IExpr first = list.arg1();

    // Heuristic: If cols != 3, it's definitely a matrix.
    boolean isMatrix = true;
    int rows = list.argSize();
    int cols = 0;

    if (first.isList()) {
      cols = ((IAST) first).argSize();
    }

    boolean potentialTriples = (cols == 3);

    if (!potentialTriples || (dataRange.isPresent() && !dataRange.equals(S.Automatic))) {
      return processMatrixData(list, rows, cols, dataRange);
    }

    try {
      return processIrregularData(list, gridRes, engine);
    } catch (Exception e) {
      return processMatrixData(list, rows, cols, dataRange);
    }
  }

  private GridData processMatrixData(IAST list, int rows, int cols, IExpr dataRange) {
    GridData gd = new GridData(cols, rows);
    gd.xMin = 1.0;
    gd.xMax = cols;
    gd.yMin = 1.0;
    gd.yMax = rows;

    if (dataRange.isList() && dataRange.size() >= 3) {
      try {
        IAST dr = (IAST) dataRange;
        IAST xR = (IAST) dr.arg1();
        IAST yR = (IAST) dr.arg2();
        gd.xMin = xR.arg1().evalf();
        gd.xMax = xR.arg2().evalf();
        gd.yMin = yR.arg1().evalf();
        gd.yMax = yR.arg2().evalf();
      } catch (RuntimeException rex) {
        Errors.printMessage(S.ListContourPlot, rex);
        return null;
      }
    }

    gd.stepX = (cols > 1) ? (gd.xMax - gd.xMin) / (cols - 1) : 1.0;
    gd.stepY = (rows > 1) ? (gd.yMax - gd.yMin) / (rows - 1) : 1.0;

    for (int i = 0; i < rows; i++) {
      IExpr rowExpr = list.get(i + 1);
      if (rowExpr.isList()) {
        IAST rowAst = (IAST) rowExpr;
        for (int j = 0; j < cols; j++) {
          if (j < rowAst.argSize()) {
            double val = rowAst.get(j + 1).evalDouble();
            gd.zGrid[j][i] = val; // Transpose: row index i -> y, col index j -> x

            if (Double.isFinite(val)) {
              if (val < gd.minZ)
                gd.minZ = val;
              if (val > gd.maxZ)
                gd.maxZ = val;
            }
          } else {
            gd.zGrid[j][i] = Double.NaN;
          }
        }
      }
    }
    return gd;
  }

  private GridData processIrregularData(IAST list, int gridRes, EvalEngine engine) {
    int n = list.argSize();
    double[] x = new double[n];
    double[] y = new double[n];
    double[] z = new double[n];

    double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
    double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

    for (int i = 0; i < n; i++) {
      IAST tuple = (IAST) list.get(i + 1);
      x[i] = tuple.arg1().evalDouble();
      y[i] = tuple.arg2().evalDouble();
      z[i] = tuple.arg3().evalDouble();

      if (x[i] < minX)
        minX = x[i];
      if (x[i] > maxX)
        maxX = x[i];
      if (y[i] < minY)
        minY = y[i];
      if (y[i] > maxY)
        maxY = y[i];
    }

    int gridX = gridRes;
    int gridY = gridRes;
    GridData gd = new GridData(gridX + 1, gridY + 1);
    gd.xMin = minX;
    gd.xMax = maxX;
    gd.yMin = minY;
    gd.yMax = maxY;
    gd.stepX = (maxX - minX) / gridX;
    gd.stepY = (maxY - minY) / gridY;

    double p = 2.0; // Power for IDW

    for (int i = 0; i <= gridX; i++) {
      double cx = minX + i * gd.stepX;
      for (int j = 0; j <= gridY; j++) {
        double cy = minY + j * gd.stepY;

        double num = 0.0;
        double den = 0.0;
        boolean exact = false;

        for (int k = 0; k < n; k++) {
          double d2 = (cx - x[k]) * (cx - x[k]) + (cy - y[k]) * (cy - y[k]);
          if (d2 < 1.0e-10) {
            gd.zGrid[i][j] = z[k];
            exact = true;
            break;
          }
          double w = 1.0 / Math.pow(d2, p / 2.0);
          num += w * z[k];
          den += w;
        }

        if (!exact) {
          double val = (den == 0.0) ? 0.0 : num / den;
          gd.zGrid[i][j] = val;
        }

        double finalVal = gd.zGrid[i][j];
        if (Double.isFinite(finalVal)) {
          if (finalVal < gd.minZ)
            gd.minZ = finalVal;
          if (finalVal > gd.maxZ)
            gd.maxZ = finalVal;
        }
      }
    }
    return gd;
  }

  private void generateContoursFromGrid(IASTAppendable primitives, GridData gd,
      IExpr contoursOption, IExpr contourStyle, IExpr contourShading,
      boolean colorFunctionScaling) {

    if (gd.minZ == Double.MAX_VALUE) {
      return;
    }

    // --- Determine Levels Logic ---
    double[] levels = null;
    int numberOfContours = 10; // Default

    // TODO Explicit List: Contours -> {v1, v2, ...}
    // Also handles {Automatic, n} if not caught by simple check
    if (contoursOption.isList()) {
      IAST list = (IAST) contoursOption;
      // Check for {Automatic, n}
      if (list.size() >= 2 && list.arg1().equals(S.Automatic) && list.arg2().isInteger()) {
        numberOfContours = list.arg2().toIntDefault(10);
      } else {
        // Explicit Values
        levels = list.toDoubleVectorIgnore();
      }
    } else if (contoursOption.isInteger()) {
      numberOfContours = contoursOption.toIntDefault(10);
    }

    // If levels weren't explicit, calculate them now based on count
    if (levels == null) {
      levels = new double[numberOfContours];
      double levelStep = (gd.maxZ - gd.minZ) / (numberOfContours + 1);
      for (int k = 0; k < numberOfContours; k++) {
        levels[k] = gd.minZ + (k + 1) * levelStep;
      }
    }
    // ------------------------------

    int gridX = gd.zGrid.length - 1;
    int gridY = gd.zGrid[0].length - 1;

    // 1. Shading (Polygons)
    if (!contourShading.equals(S.None) && !contourShading.isFalse()) {
      for (int k = -1; k < levels.length; k++) {
        double level = (k == -1) ? gd.minZ - 1.0 : levels[k];

        IExpr color;
        if (contourShading.isList()) {
          color = GraphicsOptions.getPlotStyle(contourShading, k + 1);
        } else {
          double lower = (k == -1) ? gd.minZ : levels[k];
          double upper = (k == levels.length - 1) ? gd.maxZ : levels[k + 1];
          double bandZ = (lower + upper) * 0.5;
          double t = colorFunctionScaling ? (bandZ - gd.minZ) / (gd.maxZ - gd.minZ) : bandZ;
          color = getShadingColor(t);
        }

        IASTAppendable polygons = F.ListAlloc();
        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = gd.xMin + i * gd.stepX;
            double y = gd.yMin + j * gd.stepY;
            processCellPolygon(polygons, x, y, gd.stepX, gd.stepY, gd.zGrid[i][j],
                gd.zGrid[i + 1][j], gd.zGrid[i + 1][j + 1], gd.zGrid[i][j + 1], level);
          }
        }

        if (polygons.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          group.append(F.EdgeForm(S.None));
          group.append(color);
          group.appendArgs(polygons);
          primitives.append(group);
        }
      }
    }

    // 2. Contour Lines
    if (!contourStyle.equals(S.None) && !contourStyle.isFalse()) {
      for (int k = 0; k < levels.length; k++) {
        double level = levels[k];
        IASTAppendable lineSegments = F.ListAlloc();

        IExpr currentStyle = contourStyle;
        if (currentStyle.equals(S.Automatic)) {
          currentStyle = F.GrayLevel(0.5);
        } else if (currentStyle.isList()) {
          currentStyle = GraphicsOptions.getPlotStyle(currentStyle, k);
        }

        for (int i = 0; i < gridX; i++) {
          for (int j = 0; j < gridY; j++) {
            double x = gd.xMin + i * gd.stepX;
            double y = gd.yMin + j * gd.stepY;
            processCellLine(lineSegments, x, y, gd.stepX, gd.stepY, gd.zGrid[i][j],
                gd.zGrid[i + 1][j], gd.zGrid[i + 1][j + 1], gd.zGrid[i][j + 1], level);
          }
        }

        if (lineSegments.argSize() > 0) {
          IASTAppendable group = F.ListAlloc();
          if (currentStyle.isList())
            group.append(F.Directive(currentStyle));
          else
            group.append(currentStyle);

          group.appendArgs(lineSegments);
          primitives.append(group);
        }
      }
    }
  }

  // --- Marching Squares Implementation ---

  private IExpr getShadingColor(double t) {
    double val = t;
    if (val < 0 || val > 1.0) {
      val = val - Math.floor(val);
    }
    return F.Hue(F.num(0.66 * (1.0 - val)));
  }

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

    switch (index) {
      case 1:
        add(polyPts, pts[3]);
        add(polyPts, x, y);
        add(polyPts, pts[0]);
        break;
      case 2:
        add(polyPts, pts[0]);
        add(polyPts, x + dx, y);
        add(polyPts, pts[1]);
        break;
      case 4:
        add(polyPts, pts[1]);
        add(polyPts, x + dx, y + dy);
        add(polyPts, pts[2]);
        break;
      case 8:
        add(polyPts, pts[2]);
        add(polyPts, x, y + dy);
        add(polyPts, pts[3]);
        break;
      case 3:
        add(polyPts, pts[3]);
        add(polyPts, x, y);
        add(polyPts, x + dx, y);
        add(polyPts, pts[1]);
        break;
      case 6:
        add(polyPts, pts[0]);
        add(polyPts, x + dx, y);
        add(polyPts, x + dx, y + dy);
        add(polyPts, pts[2]);
        break;
      case 12:
        add(polyPts, pts[1]);
        add(polyPts, x + dx, y + dy);
        add(polyPts, x, y + dy);
        add(polyPts, pts[3]);
        break;
      case 9:
        add(polyPts, pts[2]);
        add(polyPts, x, y + dy);
        add(polyPts, x, y);
        add(polyPts, pts[0]);
        break;
      case 14:
        add(polyPts, pts[0]);
        add(polyPts, pts[3]);
        add(polyPts, x, y + dy);
        add(polyPts, x + dx, y + dy);
        add(polyPts, x + dx, y);
        break;
      case 13:
        add(polyPts, pts[1]);
        add(polyPts, pts[0]);
        add(polyPts, x, y);
        add(polyPts, x, y + dy);
        add(polyPts, x + dx, y + dy);
        break;
      case 11:
        add(polyPts, pts[2]);
        add(polyPts, pts[1]);
        add(polyPts, x + dx, y);
        add(polyPts, x, y);
        add(polyPts, x, y + dy);
        break;
      case 7:
        add(polyPts, pts[3]);
        add(polyPts, pts[2]);
        add(polyPts, x + dx, y + dy);
        add(polyPts, x + dx, y);
        add(polyPts, x, y);
        break;
      case 5: // Saddle
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
      case 10: // Saddle
        IASTAppendable t3 = F.ListAlloc();
        add(t3, pts[0]);
        add(t3, x + dx, y);
        add(t3, pts[1]);
        out.append(F.Polygon(t3));
        IASTAppendable t4 = F.ListAlloc();
        add(t4, pts[2]);
        add(t4, x, y + dy);
        add(t4, pts[3]);
        out.append(F.Polygon(t4));
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

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // ListContourPlot shares most defaults with ContourPlot (Frame->True, Axes->False)
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.JSForm, S.Filling, S.Axes, S.PlotRange, S.$Scaling, S.Joined, // 6
            S.PlotLegends, S.PlotLabel, S.AxesLabel, S.PlotStyle, S.GridLines, S.PlotLabels, // 6
            S.FillingStyle, S.AspectRatio, S.Frame, S.ContourShading, S.ColorFunctionScaling, // 5
            S.ContourStyle, S.Background, S.Epilog, // 3
            S.ColorFunctionScaling, S.Contours, S.ContourStyle, S.ContourShading, S.DataRange, // 5
            S.MaxPlotPoints}, //
        new IExpr[] {//
            S.False, S.None, S.False, S.Automatic, S.Automatic, S.False, // 6
            S.None, S.None, S.None, S.None, S.None, S.None, // 6
            S.Automatic, F.C1, S.True, S.Automatic, S.True, // 5
            S.Automatic, S.None, F.CEmptyList, // 3
            S.True, S.Automatic, S.Automatic, S.Automatic, S.Automatic, // 5
            S.Automatic});
  }
}
