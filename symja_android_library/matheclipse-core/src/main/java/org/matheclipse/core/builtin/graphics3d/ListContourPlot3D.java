package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

/**
 * <code>ListContourPlot3D(array)</code> - the surfaces on which the values of a three dimensional
 * array of samples take given values, extracted with marching cubes.
 *
 * <p>
 * The value <code>array[[i, j, k]]</code> sits at <code>x = k - 1</code>, <code>y = j - 1</code>,
 * <code>z = i - 1</code>, unless <code>DataRange -> {{x0, x1}, {y0, y1}, {z0, z1}}</code> spreads
 * the samples evenly over that box instead. <code>Contours</code> chooses the levels as in
 * <code>ContourPlot3D</code>.
 */
public class ListContourPlot3D extends AbstractFunctionOptionEvaluator {

  /** The option table: the list plot block, the contour options, then the unread frame options. */
  private static final GraphicsOptions.OptionSet OPTIONS = Plot3DTools.frameExtras(
      Plot3DTools.listExtras(Plot3DTools.base3D()).add(S.Automatic, S.Contours, S.ContourStyle));

  private static final int X_CONTOURS = Plot3DTools.indexOf(OPTIONS, S.Contours);

  private static final int X_CONTOUR_STYLE = Plot3DTools.indexOf(OPTIONS, S.ContourStyle);

  /** At most this many samples are read along an axis. */
  private static final int MAX_SAMPLES = 200;

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    ast = PlotWrapper.stripFirstArgument(ast);
    IExpr data = ast.arg1();
    if (!data.isList()) {
      data = data.normal(false);
    }
    double[][][] grid = grid(data);
    if (grid == null) {
      // `1` must be a valid array or a list of valid arrays.
      return Errors.printMessage(S.ListContourPlot3D, "arrayerr", F.list(ast.arg1()), engine);
    }
    int[] counts = {grid.length, grid[0].length, grid[0][0].length};
    double[] min = new double[3];
    double[] max = new double[3];
    boolean dataRange = dataRange(options[Plot3DTools.X_DATA_RANGE], min, max);
    if (!dataRange) {
      for (int c = 0; c < 3; c++) {
        min[c] = 0.0;
        max[c] = counts[c] - 1.0;
      }
    }

    double[] levels = ContourPlot3D.contourLevels(options[X_CONTOURS], grid, false);
    if (levels.length == 0) {
      return F.NIL;
    }
    IExpr contourStyle = options[X_CONTOUR_STYLE];
    IExpr styleSource =
        contourStyle == S.Automatic ? options[Plot3DTools.X_PLOT_STYLE] : contourStyle;
    PlotColorFunction levelColors = Plot3DTools
        .plotColors(PlotColorFunction.Family.CONTOUR_3D, options, S.ListContourPlot3D, engine)
        .ranges(min[0], max[0], min[1], max[1], min[2], max[2], levels[0],
            levels[levels.length - 1])
        .build();
    boolean ownLights =
        levelColors == null && (styleSource == null || styleSource.isAutomatic());

    double[][] axis = ContourPlot3D.axis(min, max, counts);
    IASTAppendable surfaces = F.ListAlloc(levels.length);
    for (int i = 0; i < levels.length; i++) {
      IExpr style =
          ContourPlot3D.levelStyle(i, levels[i], levelColors, ownLights, styleSource, min, max);
      IExpr complex = ContourPlot3D.surface(grid, axis, levels[i], style,
          options[Plot3DTools.X_MESH], options[Plot3DTools.X_BOUNDARY_STYLE]);
      if (complex.isPresent()) {
        surfaces.append(complex);
      }
    }
    IExpr[] defaults = Plot3DTools.cubeDefaults(min, max, !ownLights);
    if (!dataRange) {
      // without a DataRange the box is fitted to the surfaces
      defaults[0] = F.Rule(S.PlotRange, S.All);
    }
    return Plot3DTools.graphics3D(surfaces, originalAST, argSize, defaults);
  }

  /**
   * The samples of a rectangular array <code>array[[i, j, k]]</code> as <code>grid[k][j][i]</code>,
   * indexed by x, y and z; <code>NaN</code> where a sample is no number.
   *
   * @return <code>null</code> if <code>data</code> is no rectangular array of at least two samples
   *         along every axis
   */
  private static double[][][] grid(IExpr data) {
    if (!data.isList() || data.argSize() < 2 || !data.first().isList()
        || !data.first().first().isList()) {
      return null;
    }
    int nz = data.argSize();
    int ny = data.first().argSize();
    int nx = data.first().first().argSize();
    if (nx < 2 || ny < 2 || nx > MAX_SAMPLES || ny > MAX_SAMPLES || nz > MAX_SAMPLES) {
      return null;
    }
    double[][][] grid = new double[nx][ny][nz];
    for (int i = 1; i <= nz; i++) {
      IExpr plane = data.getAt(i);
      if (!plane.isList() || plane.argSize() != ny) {
        return null;
      }
      for (int j = 1; j <= ny; j++) {
        IExpr row = plane.getAt(j);
        if (!row.isList() || row.argSize() != nx) {
          return null;
        }
        for (int k = 1; k <= nx; k++) {
          grid[k - 1][j - 1][i - 1] = row.getAt(k).evalfNaN();
        }
      }
    }
    return grid;
  }

  /** Read <code>DataRange -> {{x0, x1}, {y0, y1}, {z0, z1}}</code>; false if it is not that. */
  private static boolean dataRange(IExpr option, double[] min, double[] max) {
    if (!option.isList() || option.argSize() != 3) {
      return false;
    }
    for (int c = 0; c < 3; c++) {
      IExpr range = option.getAt(c + 1);
      if (!range.isList2()) {
        return false;
      }
      min[c] = range.first().evalfNaN();
      max[c] = range.second().evalfNaN();
      if (!Double.isFinite(min[c]) || !Double.isFinite(max[c]) || !(max[c] > min[c])) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.putMessage(IPatternMatcher.SET, "arrayerr",
        F.stringx("`1` must be a valid array or a list of valid arrays."));
    setOptions(newSymbol, OPTIONS.keys(), OPTIONS.values());
  }
}
