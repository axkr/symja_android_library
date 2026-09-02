package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.graphics.MarchingCubesTables;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code ContourPlot3D[f == g, {x, ..}, {y, ..}, {z, ..}]} - the surface on which a function of
 * three variables takes a given value, extracted with marching cubes.
 */
public class ContourPlot3D extends AbstractFunctionOptionEvaluator {

  /**
   * Samples per axis by default. The cost is cubic, so this is the point where the surface is
   * smooth enough to read while the plot still appears at once; the previous default of 45 meant
   * ninety thousand evaluations for every call.
   */
  private static final int DEFAULT_PLOT_POINTS = 25;

  /** An upper bound, because a cubic grid gets out of hand quickly. */
  private static final int MAX_PLOT_POINTS = 80;

  public ContourPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 4) {
      return F.NIL;
    }
    for (int i = 2; i <= 4; i++) {
      IExpr range = ast.get(i);
      if (!range.isList3() || !range.first().isSymbol()) {
        return Errors.printMessage(S.ContourPlot3D, "pllim", F.list(range), engine);
      }
    }

    IExpr expr = ast.arg1();
    boolean isEquation = expr.isAST(S.Equal, 3);
    if (isEquation) {
      expr = F.Subtract(((IAST) expr).arg1(), ((IAST) expr).arg2());
    }

    ISymbol[] vars = new ISymbol[3];
    double[] min = new double[3];
    double[] max = new double[3];
    for (int i = 0; i < 3; i++) {
      IAST range = (IAST) ast.get(i + 2);
      vars[i] = (ISymbol) range.arg1();
      min[i] = range.arg2().evalfNaN();
      max[i] = range.arg3().evalfNaN();
      if (!Double.isFinite(min[i]) || !Double.isFinite(max[i]) || max[i] <= min[i]) {
        return Errors.printMessage(S.ContourPlot3D, "pllim", F.list(range), engine);
      }
    }

    int points = plotPoints(options[Plot3DTools.X_PLOT_POINTS]);
    double[][][] grid =
        sample(expr, vars, min, max, options[Plot3DTools.X_EVALUATION_MONITOR], points, engine,
            RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine));
    if (grid == null) {
      return F.NIL;
    }

    double[] levels = contourLevels(options[Plot3DTools.X_CONTOURS], grid, isEquation);
    if (levels.length == 0) {
      return F.NIL;
    }

    // ContourStyle is what styles the contour surfaces; PlotStyle only gets a say when no
    // ContourStyle was given, so that a plot written either way looks the same
    IExpr contourStyle = options[Plot3DTools.X_CONTOUR_STYLE];
    IExpr styleSource =
        contourStyle == S.Automatic ? options[Plot3DTools.X_PLOT_STYLE] : contourStyle;

    IASTAppendable surfaces = F.ListAlloc(levels.length);
    for (int level = 0; level < levels.length; level++) {
      GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, false);
      IExpr style = Plot3DTools.surfaceStyle(level, styleSource);
      Plot3DTools.applyStyle(builder, style, options[Plot3DTools.X_MESH]);
      marchingCubes(builder, grid, levels[level], min, max, points);
      IExpr complex = builder.build();
      if (complex.isPresent()) {
        surfaces.append(complex);
      }
    }
    if (surfaces.argSize() == 0) {
      return F.NIL;
    }

    return Plot3DTools.graphics3D(surfaces, originalAST, argSize,
        new IExpr[] {
            F.Rule(S.PlotRange,
                F.List(F.List(F.num(min[0]), F.num(max[0])), F.List(F.num(min[1]), F.num(max[1])),
                    F.List(F.num(min[2]), F.num(max[2])))),
            F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.C1)), F.Rule(S.Axes, S.True),
            F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  private static int plotPoints(IExpr option) {
    int points = Plot3DTools.plotPoints(option, DEFAULT_PLOT_POINTS)[0];
    return Math.max(2, Math.min(points, MAX_PLOT_POINTS));
  }

  /** Sample the function on a regular grid, through the compiled numeric path. */
  private static double[][][] sample(IExpr expr, ISymbol[] vars, double[] min, double[] max,
      IExpr monitor, int points, EvalEngine engine, RegionFunctionFilter region) {
    final MultiVariateNumerical function;
    try {
      function = new MultiVariateNumerical(expr, F.List(vars[0], vars[1], vars[2]));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
    double[] step = new double[3];
    for (int i = 0; i < 3; i++) {
      step[i] = (max[i] - min[i]) / (points - 1);
    }
    double[][][] grid = new double[points][points][points];
    double[] at = new double[3];
    for (int i = 0; i < points; i++) {
      at[0] = min[0] + i * step[0];
      for (int j = 0; j < points; j++) {
        at[1] = min[1] + j * step[1];
        for (int k = 0; k < points; k++) {
          at[2] = min[2] + k * step[2];
          Plot3DTools.monitor(monitor, engine);
          double value = function.value(at);
          if (!Double.isFinite(value)
              || (region != null && !region.accepts(at[0], at[1], at[2], value))) {
            // a sample the region rejects is treated as one the function has no value at, and
            // marchingCubes leaves out every cube that touches it
            value = Double.NaN;
          }
          grid[i][j][k] = value;
        }
      }
    }
    return grid;
  }

  /**
   * The values the surfaces are drawn at.
   *
   * <p>
   * An equation fixes the level at zero. For a bare function {@code Contours} chooses, defaulting
   * to a few levels spread through the range the function actually takes.
   */
  private static double[] contourLevels(IExpr option, double[][][] grid, boolean isEquation) {
    if (option != null && option.isList()) {
      IAST list = (IAST) option;
      double[] levels = new double[list.argSize()];
      int count = 0;
      for (int i = 1; i <= list.argSize(); i++) {
        double value = list.get(i).evalfNaN();
        if (Double.isFinite(value)) {
          levels[count++] = value;
        }
      }
      if (count > 0) {
        double[] trimmed = new double[count];
        System.arraycopy(levels, 0, trimmed, 0, count);
        return trimmed;
      }
    }
    int requested = option != null && option.isInteger() ? option.toIntDefault(0) : 0;
    if (requested <= 0) {
      if (isEquation) {
        return new double[] {0.0};
      }
      requested = 3;
    }
    double lo = Double.MAX_VALUE;
    double hi = -Double.MAX_VALUE;
    for (double[][] plane : grid) {
      for (double[] row : plane) {
        for (double value : row) {
          if (Double.isFinite(value)) {
            lo = Math.min(lo, value);
            hi = Math.max(hi, value);
          }
        }
      }
    }
    if (lo > hi) {
      return new double[0];
    }
    if (hi - lo < 1e-12) {
      return new double[] {lo};
    }
    double[] levels = new double[requested];
    for (int i = 0; i < requested; i++) {
      levels[i] = lo + (hi - lo) * (i + 1.0) / (requested + 1.0);
    }
    return levels;
  }

  private static void marchingCubes(GraphicsComplexBuilder builder, double[][][] grid,
      double contour, double[] min, double[] max, int points) {
    double[] step = new double[3];
    double[][] axis = new double[3][points];
    for (int c = 0; c < 3; c++) {
      step[c] = (max[c] - min[c]) / (points - 1);
      for (int i = 0; i < points; i++) {
        axis[c][i] = min[c] + i * step[c];
      }
    }

    for (int i = 0; i < points - 1; i++) {
      for (int j = 0; j < points - 1; j++) {
        for (int k = 0; k < points - 1; k++) {
          double[] corner = {grid[i][j][k], grid[i + 1][j][k], grid[i + 1][j + 1][k],
              grid[i][j + 1][k], grid[i][j][k + 1], grid[i + 1][j][k + 1],
              grid[i + 1][j + 1][k + 1], grid[i][j + 1][k + 1]};
          boolean defined = true;
          for (double value : corner) {
            if (Double.isNaN(value)) {
              defined = false;
              break;
            }
          }
          if (!defined) {
            continue;
          }

          int cubeIndex = 0;
          for (int c = 0; c < 8; c++) {
            if (corner[c] < contour) {
              cubeIndex |= 1 << c;
            }
          }
          if (cubeIndex == 0 || cubeIndex == 255) {
            continue;
          }

          int[] edges = MarchingCubesTables.getTriangles(cubeIndex);
          for (int e = 0; e + 2 < edges.length; e += 3) {
            if (edges[e] == -1) {
              break;
            }
            int[] face = new int[3];
            for (int v = 0; v < 3; v++) {
              double[] pointAndNormal = interpolateEdge(contour, corner, edges[e + v], i, j, k,
                  axis[0], axis[1], axis[2], grid, step[0], step[1], step[2]);
              face[v] = builder.addVertex(pointAndNormal[0], pointAndNormal[1], pointAndNormal[2],
                  new double[] {pointAndNormal[3], pointAndNormal[4], pointAndNormal[5]}, null);
            }
            // A crossing that lands exactly on a grid corner is shared by the edges meeting
            // there, so two corners of the triangle weld to one vertex and it covers no area.
            // Keeping it would only give the renderer a degenerate normal to work with.
            if (face[0] == face[1] || face[1] == face[2] || face[0] == face[2]) {
              continue;
            }
            // The table already winds each triangle so that its front face points away from the
            // region below the contour, which is the direction the normals point in too. They
            // have to agree: where they do not, the renderer treats every visible fragment as a
            // back face, flips the normal it was given, and the surface comes out unlit.
            builder.addPolygon(face[0], face[1], face[2]);
          }
        }
      }
    }
  }

  /**
   * Where the surface crosses one edge of a cell, and the surface normal there.
   *
   * <p>
   * The normal comes from the gradient of the sampled field rather than from the triangle, which is
   * what lets a coarse grid still shade as a smooth surface.
   */
  private static double[] interpolateEdge(double target, double[] val, int edge, int i, int j,
      int k, double[] xVals, double[] yVals, double[] zVals, double[][][] grid, double dx,
      double dy, double dz) {
    // the same numbering the triangle table is written against, taken from it rather than
    // repeated here, because a private copy that drifts from the table shreds the surface
    int[][] edgeToVertices = MarchingCubesTables.EDGE_VERTICES;
    int[][] vertexOffsets = MarchingCubesTables.VERTEX_OFFSETS;

    int v1 = edgeToVertices[edge][0];
    int v2 = edgeToVertices[edge][1];
    double value1 = val[v1];
    double value2 = val[v2];
    double t = Math.abs(value2 - value1) < 1e-12 ? 0.5 : (target - value1) / (value2 - value1);
    t = Math.max(0.0, Math.min(1.0, t));

    int[] o1 = vertexOffsets[v1];
    int[] o2 = vertexOffsets[v2];
    double x1 = xVals[i + o1[0]];
    double y1 = yVals[j + o1[1]];
    double z1 = zVals[k + o1[2]];
    double x2 = xVals[i + o2[0]];
    double y2 = yVals[j + o2[1]];
    double z2 = zVals[k + o2[2]];

    double[] n1 = gradient(grid, i + o1[0], j + o1[1], k + o1[2], dx, dy, dz);
    double[] n2 = gradient(grid, i + o2[0], j + o2[1], k + o2[2], dx, dy, dz);
    double nx = n1[0] + t * (n2[0] - n1[0]);
    double ny = n1[1] + t * (n2[1] - n1[1]);
    double nz = n1[2] + t * (n2[2] - n1[2]);
    double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
    if (length > 1e-12) {
      // the field grows inwards, so the outward normal is the negated gradient
      nx = -nx / length;
      ny = -ny / length;
      nz = -nz / length;
    } else {
      nx = 0;
      ny = 0;
      nz = 1;
    }
    return new double[] {x1 + t * (x2 - x1), y1 + t * (y2 - y1), z1 + t * (z2 - z1), nx, ny, nz};
  }

  /** A central difference gradient of the sampled field, one sided at the edges of the grid. */
  private static double[] gradient(double[][][] grid, int i, int j, int k, double dx, double dy,
      double dz) {
    int n = grid.length;
    double gx = difference(value(grid, i + 1, j, k, n), value(grid, i - 1, j, k, n),
        value(grid, i, j, k, n), dx);
    double gy = difference(value(grid, i, j + 1, k, n), value(grid, i, j - 1, k, n),
        value(grid, i, j, k, n), dy);
    double gz = difference(value(grid, i, j, k + 1, n), value(grid, i, j, k - 1, n),
        value(grid, i, j, k, n), dz);
    return new double[] {gx, gy, gz};
  }

  private static double value(double[][][] grid, int i, int j, int k, int n) {
    if (i < 0 || j < 0 || k < 0 || i >= n || j >= n || k >= n) {
      return Double.NaN;
    }
    return grid[i][j][k];
  }

  private static double difference(double forward, double backward, double center, double step) {
    if (Double.isFinite(forward) && Double.isFinite(backward)) {
      return (forward - backward) / (2 * step);
    }
    if (Double.isFinite(forward) && Double.isFinite(center)) {
      return (forward - center) / step;
    }
    if (Double.isFinite(backward) && Double.isFinite(center)) {
      return (center - backward) / step;
    }
    return 0.0;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return new int[] {4, Integer.MAX_VALUE};
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options =
        Plot3DTools.frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()).add(S.Automatic,
            S.Contours, S.ContourStyle, S.RegionBoundaryStyle));
    setOptions(newSymbol, options.keys(), options.values());
  }
}
