package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Density plot of explicit data.
 *
 * <p>
 * Example: <code>ListDensityPlot[Table[Sin[i] Cos[j], {i, 10}, {j, 10}]]</code>,
 * <code>ListDensityPlot[{{0, 0, 1}, {1, 0, 2}, {0, 1, 3}, {1, 1, 4}}]</code>
 *
 * <p>
 * The data is read into a grid by {@link ListContourPlot#prepareGridData}, so a matrix of values
 * and a list of <code>{x, y, z}</code> triples are accepted in the same forms the contour plots
 * accept them. The grid is then painted as a single {@code Raster} using the colour scheme of
 * {@link DensityPlot}, sampled finely enough that {@code InterpolationOrder -> 1} reads as a smooth
 * gradient rather than as the data cells.
 */
public class ListDensityPlot extends DensityPlot {

  /**
   * The sampling grid is refined until it is about this wide, which is what turns four data points
   * into a gradient instead of into four flat squares. Rasters are drawn cell by cell, so this is
   * also the cost of the picture, and it is a target rather than a minimum: data that is already
   * finer than this is drawn at its own resolution.
   */
  private static final int TARGET_RESOLUTION = 200;

  /** Upper bound on the refinement of a coarse grid. */
  private static final int MAX_REFINEMENT = 16;

  public ListDensityPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = engine.evaluate(ast.arg1());
    if (!dataArg.isList()) {
      // a SparseArray or a packed numeric matrix is not a list until it is expanded into one
      dataArg = dataArg.normal(false);
      if (!dataArg.isList()) {
        return F.NIL;
      }
    }
    // MaxPlotPoints paints a large grid from a sample of it rather than from every value
    IAST thinned = GraphicsOptions.downsampleMatrix((IAST) dataArg,
        GraphicsOptions.optionValue(originalAST, S.MaxPlotPoints, S.Automatic).toIntDefault(-1));
    if (thinned.isPresent()) {
      dataArg = thinned;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    boolean colorFunctionScaling = true;
    IExpr colorFunctionOpt = S.Automatic;
    IExpr meshOpt = S.None;
    IExpr dataRange = S.Automatic;
    int interpolationOrder = 1;
    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.ColorFunctionScaling:
              if (val.isFalse()) {
                colorFunctionScaling = false;
              }
              break;
            case ID.ColorFunction:
              colorFunctionOpt = val;
              break;
            case ID.Mesh:
              meshOpt = val;
              break;
            case ID.DataRange:
              dataRange = val;
              break;
            case ID.InterpolationOrder:
              if (val != S.Automatic) {
                interpolationOrder = val.toIntDefault();
                if (interpolationOrder < 0 || interpolationOrder > 6) {
                  // Index `1` should be a machine sized integer between `2` and `3`.
                  return Errors.printMessage(S.ListDensityPlot, "invidx2",
                      F.List(S.InterpolationOrder, F.C0, F.ZZ(6)), engine);
                }
              }
              break;
          }
        }
      }
    }

    ListContourPlot.GridData gridData =
        ListContourPlot.prepareGridData(dataArg, dataRange, TARGET_RESOLUTION / 4, engine);
    if (gridData == null || gridData.minZ == Double.MAX_VALUE) {
      return F.NIL;
    }
    boolean[][] defined = ListContourPlot.applyRegionFunction(gridData, RegionFunctionFilter
        .of(GraphicsOptions.optionValue(originalAST, S.RegionFunction, S.Automatic), engine));
    if (gridData.minZ > gridData.maxZ) {
      // the region left nothing to paint, which is the same as data with no value in it
      return F.NIL;
    }

    // zGrid is indexed [x][y], and holds the values at the grid nodes rather than in its cells
    int nodesX = gridData.zGrid.length;
    int nodesY = gridData.zGrid[0].length;
    if (nodesX < 1 || nodesY < 1) {
      return F.NIL;
    }

    double[] xRange = spread(gridData.xMin, gridData.xMax);
    double[] yRange = spread(gridData.yMin, gridData.yMax);

    int cellsX = refine(nodesX);
    int cellsY = refine(nodesY);

    java.util.function.DoubleFunction<IExpr> colorMap =
        GraphicsOptions.colorFunction(colorFunctionOpt, engine, this::getDensityColor);
    double spanZ = gridData.maxZ - gridData.minZ;

    IExpr[][] cells = new IExpr[cellsY][cellsX];
    for (int i = 0; i < cellsX; i++) {
      // the value of a cell is the one at its centre, so that a cell is not biased towards the
      // node on one of its sides
      double gx = (nodesX - 1) * (i + 0.5) / cellsX;
      for (int j = 0; j < cellsY; j++) {
        double gy = (nodesY - 1) * (j + 0.5) / cellsY;

        double z = interpolationOrder == 0 //
            ? nearest(gridData.zGrid, gx, gy)
            : bilinear(gridData.zGrid, gx, gy);
        if (!Double.isFinite(z)) {
          // a cell without a value stays transparent rather than being painted in a colour that
          // the data never had
          continue;
        }

        double t = z;
        if (colorFunctionScaling) {
          t = (Math.abs(spanZ) > 1e-9) ? (z - gridData.minZ) / spanZ : 0.5;
        }
        // j counts upwards from the bottom, while the raster rows are given top first
        cells[cellsY - 1 - j][i] = colorMap.apply(t);
      }
    }

    IASTAppendable primitives = F.ListAlloc();
    primitives.append(
        GraphicsOptions.rasterTopFirst(cells, xRange[0], yRange[0], xRange[1], yRange[1]));
    // Mesh draws the data grid, not the sampling grid it was painted from
    IExpr meshLines = GraphicsOptions.meshGrid(meshOpt, xRange[0], yRange[0], xRange[1], yRange[1],
        Math.max(1, nodesX - 1), Math.max(1, nodesY - 1));
    if (meshLines.isPresent()) {
      primitives.append(meshLines);
    }
    ContourPlot.appendBoundary(primitives, defined, gridData.xMin, gridData.yMin, gridData.stepX,
        gridData.stepY, GraphicsOptions.optionValue(originalAST, S.BoundaryStyle, S.Automatic));

    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * The number of raster cells to draw a row of {@code nodes} data values with.
   *
   * <p>
   * Data coarser than the target resolution is refined so that the interpolation is visible, and
   * data finer than it is drawn one cell per value.
   */
  private static int refine(int nodes) {
    int cells = Math.max(1, nodes - 1);
    int factor = Math.max(1, Math.min(MAX_REFINEMENT, TARGET_RESOLUTION / cells));
    return cells * factor;
  }

  /**
   * A range that is never empty, so that a single row or column of data still has an extent to be
   * drawn in.
   */
  private static double[] spread(double min, double max) {
    if (!(max > min)) {
      return new double[] {min - 0.5, min + 0.5};
    }
    return new double[] {min, max};
  }

  /**
   * The value at a point of the grid, interpolated from the four nodes around it.
   *
   * @param gx position in node coordinates, from 0 to the last x index
   * @param gy position in node coordinates, from 0 to the last y index
   * @return {@code Double.NaN} if any of the four nodes has no value
   */
  private static double bilinear(double[][] zGrid, double gx, double gy) {
    int lastX = zGrid.length - 1;
    int lastY = zGrid[0].length - 1;
    int x0 = clamp((int) Math.floor(gx), 0, lastX);
    int y0 = clamp((int) Math.floor(gy), 0, lastY);
    int x1 = Math.min(x0 + 1, lastX);
    int y1 = Math.min(y0 + 1, lastY);
    double fx = clamp(gx - x0, 0.0, 1.0);
    double fy = clamp(gy - y0, 0.0, 1.0);

    double v00 = zGrid[x0][y0];
    double v10 = zGrid[x1][y0];
    double v01 = zGrid[x0][y1];
    double v11 = zGrid[x1][y1];
    if (!Double.isFinite(v00) || !Double.isFinite(v10) || !Double.isFinite(v01)
        || !Double.isFinite(v11)) {
      return Double.NaN;
    }
    double bottom = v00 + (v10 - v00) * fx;
    double top = v01 + (v11 - v01) * fx;
    return bottom + (top - bottom) * fy;
  }

  /** The value of the node nearest to a point of the grid, which draws the data as flat cells. */
  private static double nearest(double[][] zGrid, double gx, double gy) {
    int x = clamp((int) Math.round(gx), 0, zGrid.length - 1);
    int y = clamp((int) Math.round(gy), 0, zGrid[0].length - 1);
    return zGrid[x][y];
  }

  private static int clamp(int value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }

  private static double clamp(double value, double min, double max) {
    return value < min ? min : (value > max ? max : value);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, true);
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions
        .densityExtras(
            new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(),
                defaults))
        // only the list form takes a data range, an interpolation order and a point limit
        .add(S.Automatic, S.DataRange, S.InterpolationOrder, S.MaxPlotPoints);
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
