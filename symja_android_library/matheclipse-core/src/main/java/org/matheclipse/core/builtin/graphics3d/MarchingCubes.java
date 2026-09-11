package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.MarchingCubesTables;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Surfaces of a function of three variables sampled on a grid: the samples, and the triangles of
 * a level surface through them, extracted with marching cubes. Used by <code>ContourPlot3D</code>
 * for its contour surfaces and by <code>RegionPlot3D</code> for the surface of a solid.
 */
public final class MarchingCubes {

  private MarchingCubes() {}

  /**
   * Sample the function on a regular grid of <code>points</code> samples per axis, through the
   * compiled numeric path.
   *
   * @return the samples, <code>NaN</code> where the function has no value or the region function
   *         rejects the point, or <code>null</code> if the function cannot be evaluated numerically
   */
  public static double[][][] sample(IExpr expr, ISymbol[] vars, double[] min, double[] max,
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
   * The surface where the samples of a regular grid from <code>min</code> to <code>max</code>
   * take the value <code>contour</code>, added to <code>builder</code> as triangles.
   */
  public static void marchingCubes(GraphicsComplexBuilder builder, double[][][] grid,
      double contour, double[] min, double[] max, int points) {
    double[][] axis = new double[3][points];
    for (int c = 0; c < 3; c++) {
      double step = (max[c] - min[c]) / (points - 1);
      for (int i = 0; i < points; i++) {
        axis[c][i] = min[c] + i * step;
      }
    }
    marchingCubes(builder, grid, contour, axis);
  }

  /**
   * Marching cubes over a cubic grid whose sample coordinates are given per axis,
   * <code>axis[c].length</code> samples along every axis. The distance between the second and the
   * third sample is the step of the gradients, so a first and a last layer of zero width - which
   * <code>RegionPlot3D</code> lays on the faces of the box to close its surfaces - is allowed.
   */
  public static void marchingCubes(GraphicsComplexBuilder builder, double[][][] grid,
      double contour, double[][] axis) {
    int points = axis[0].length;
    double[] step = new double[3];
    for (int c = 0; c < 3; c++) {
      step[c] = points > 2 ? axis[c][2] - axis[c][1] : axis[c][1] - axis[c][0];
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
}
