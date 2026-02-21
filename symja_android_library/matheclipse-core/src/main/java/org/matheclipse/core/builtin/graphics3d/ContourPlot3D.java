package org.matheclipse.core.builtin.graphics3d;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.MarchingCubesTables;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of ContourPlot3D with solid double-sided mesh generation.
 */
public class ContourPlot3D extends AbstractFunctionOptionEvaluator {

  public ContourPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 4) {
      return F.NIL;
    }

    IExpr functionOrEquation = ast.arg1();
    IExpr xRange = ast.arg2();
    IExpr yRange = ast.arg3();
    IExpr zRange = ast.arg4();

    if (!xRange.isList3() || !xRange.first().isSymbol()) {
      return Errors.printMessage(S.ContourPlot3D, "pllim", F.list(xRange), engine);
    }
    if (!yRange.isList3() || !yRange.first().isSymbol()) {
      return Errors.printMessage(S.ContourPlot3D, "pllim", F.list(yRange), engine);
    }
    if (!zRange.isList3() || !zRange.first().isSymbol()) {
      return Errors.printMessage(S.ContourPlot3D, "pllim", F.list(zRange), engine);
    }

    // Convert equations (LHS == RHS) to expressions (LHS - RHS = 0)
    IExpr exprToEvaluate = functionOrEquation;
    double targetContour = 0.0;
    if (functionOrEquation.isAST(S.Equal, 3)) {
      exprToEvaluate =
          F.Subtract(((IAST) functionOrEquation).arg1(), ((IAST) functionOrEquation).arg2());
    }

    int plotPoints = 35; // Default for smoother surfaces
    if (options[0].isInteger()) {
      plotPoints = options[0].toIntDefault();
    }
    if (plotPoints < 2)
      plotPoints = 2;

    ISymbol xVar;
    double xMin, xMax;
    ISymbol yVar;
    double yMin, yMax;
    ISymbol zVar;
    double zMin, zMax;

    try {
      xVar = (ISymbol) ((IAST) xRange).arg1();
      xMin = ((IAST) xRange).arg2().evalf();
      xMax = ((IAST) xRange).arg3().evalf();

      yVar = (ISymbol) ((IAST) yRange).arg1();
      yMin = ((IAST) yRange).arg2().evalf();
      yMax = ((IAST) yRange).arg3().evalf();

      zVar = (ISymbol) ((IAST) zRange).arg1();
      zMin = ((IAST) zRange).arg2().evalf();
      zMax = ((IAST) zRange).arg3().evalf();
    } catch (ArgumentTypeException ate) {
      return F.NIL;
    }

    return createIsosurface(exprToEvaluate, xVar, xMin, xMax, yVar, yMin, yMax, zVar, zMin, zMax,
        targetContour, plotPoints, options, engine);
  }

  private IExpr createIsosurface(IExpr expr, ISymbol xVar, double xMin, double xMax, ISymbol yVar,
      double yMin, double yMax, ISymbol zVar, double zMin, double zMax, double contour, int points,
      final IExpr[] options, EvalEngine engine) {

    double[][][] gridValues = new double[points][points][points];
    double[] xVals = new double[points];
    double[] yVals = new double[points];
    double[] zVals = new double[points];

    double dx = (xMax - xMin) / (points - 1);
    double dy = (yMax - yMin) / (points - 1);
    double dz = (zMax - zMin) / (points - 1);

    for (int i = 0; i < points; i++) {
      xVals[i] = xMin + i * dx;
      for (int j = 0; j < points; j++) {
        yVals[j] = yMin + j * dy;
        for (int k = 0; k < points; k++) {
          zVals[k] = zMin + k * dz;
          try {
            IExpr subst = F.subst(expr, F.List(F.Rule(xVar, F.num(xVals[i])),
                F.Rule(yVar, F.num(yVals[j])), F.Rule(zVar, F.num(zVals[k]))));
            IExpr evaluated = engine.evaluate(subst);

            if (evaluated.isNumber()) {
              double val = evaluated.evalf();
              if (Double.isNaN(val) || Double.isInfinite(val)) {
                gridValues[i][j][k] = Double.NaN;
              } else {
                gridValues[i][j][k] = val;
              }
            } else {
              gridValues[i][j][k] = Double.NaN;
            }
          } catch (Exception e) {
            gridValues[i][j][k] = Double.NaN;
          }
        }
      }
    }

    IASTAppendable vertexAST = F.ListAlloc();
    IASTAppendable normalsAST = F.ListAlloc();
    IASTAppendable polygonAST = F.ListAlloc();
    Map<String, Integer> vertexMap = new HashMap<>();
    int vertexCounter = 1;

    for (int i = 0; i < points - 1; i++) {
      for (int j = 0; j < points - 1; j++) {
        for (int k = 0; k < points - 1; k++) {

          double[] val = {gridValues[i][j][k], gridValues[i + 1][j][k], gridValues[i + 1][j + 1][k],
              gridValues[i][j + 1][k], gridValues[i][j][k + 1], gridValues[i + 1][j][k + 1],
              gridValues[i + 1][j + 1][k + 1], gridValues[i][j + 1][k + 1]};

          boolean hasNaN = false;
          for (int c = 0; c < 8; c++) {
            if (Double.isNaN(val[c])) {
              hasNaN = true;
              break;
            }
          }
          if (hasNaN)
            continue;

          int cubeIndex = 0;
          if (val[0] < contour)
            cubeIndex |= 1;
          if (val[1] < contour)
            cubeIndex |= 2;
          if (val[2] < contour)
            cubeIndex |= 4;
          if (val[3] < contour)
            cubeIndex |= 8;
          if (val[4] < contour)
            cubeIndex |= 16;
          if (val[5] < contour)
            cubeIndex |= 32;
          if (val[6] < contour)
            cubeIndex |= 64;
          if (val[7] < contour)
            cubeIndex |= 128;

          if (cubeIndex == 0 || cubeIndex == 255) {
            continue;
          }

          int[] edges = MarchingCubesTables.getTriangles(cubeIndex);

          for (int e = 0; e < edges.length; e += 3) {
            if (edges[e] == -1) {
              break;
            }

            int[] faceIdx = new int[3];
            for (int v = 0; v < 3; v++) {
              int edgeIdx = edges[e + v];

              double[] ptAndNorm = interpolateEdge(contour, val, edgeIdx, i, j, k, xVals, yVals,
                  zVals, gridValues, dx, dy, dz);

              // Use scaled rounding to weld vertices and eliminate microscopic seam gaps
              long kx = Math.round(ptAndNorm[0] * 1e5);
              long ky = Math.round(ptAndNorm[1] * 1e5);
              long kz = Math.round(ptAndNorm[2] * 1e5);
              String key = kx + "_" + ky + "_" + kz;

              if (!vertexMap.containsKey(key)) {
                vertexMap.put(key, vertexCounter++);
                vertexAST
                    .append(F.List(F.num(ptAndNorm[0]), F.num(ptAndNorm[1]), F.num(ptAndNorm[2])));
                normalsAST
                    .append(F.List(F.num(ptAndNorm[3]), F.num(ptAndNorm[4]), F.num(ptAndNorm[5])));
              }
              faceIdx[v] = vertexMap.get(key);
            }

            // Output Counter-Clockwise (CCW) triangles to prevent WebGL backface culling.
            // The default Marching Cubes table produces Clockwise winding.
            polygonAST.append(F.List(F.ZZ(faceIdx[0]), F.ZZ(faceIdx[2]), F.ZZ(faceIdx[1])));
          }
        }
      }
    }

    IASTAppendable surfaceGroup = F.ListAlloc(3);

    surfaceGroup.append(S.Orange);

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
    IExpr meshOption = options[5];
    if (meshOption.isFalse() || meshOption.equals(S.None)) {
      IASTAppendable edgeForm = F.ast(S.EdgeForm);
      edgeForm.append(S.None);
      builder.setStyle(S.Orange, edgeForm);
    } else {
      builder.setStyle(S.Orange);
    }

    for (int i = 0; i < points - 1; i++) {
      for (int j = 0; j < points - 1; j++) {
        for (int k = 0; k < points - 1; k++) {

          double[] val = {gridValues[i][j][k], gridValues[i + 1][j][k], gridValues[i + 1][j + 1][k],
              gridValues[i][j + 1][k], gridValues[i][j][k + 1], gridValues[i + 1][j][k + 1],
              gridValues[i + 1][j + 1][k + 1], gridValues[i][j + 1][k + 1]};

          boolean hasNaN = false;
          for (int c = 0; c < 8; c++) {
            if (Double.isNaN(val[c])) {
              hasNaN = true;
              break;
            }
          }
          if (hasNaN)
            continue;

          int cubeIndex = 0;
          if (val[0] < contour)
            cubeIndex |= 1;
          if (val[1] < contour)
            cubeIndex |= 2;
          if (val[2] < contour)
            cubeIndex |= 4;
          if (val[3] < contour)
            cubeIndex |= 8;
          if (val[4] < contour)
            cubeIndex |= 16;
          if (val[5] < contour)
            cubeIndex |= 32;
          if (val[6] < contour)
            cubeIndex |= 64;
          if (val[7] < contour)
            cubeIndex |= 128;

          if (cubeIndex == 0 || cubeIndex == 255) {
            continue;
          }

          int[] edges = MarchingCubesTables.getTriangles(cubeIndex);

          for (int e = 0; e < edges.length; e += 3) {
            if (edges[e] == -1) {
              break;
            }

            int[] faceIdx = new int[3];
            for (int v = 0; v < 3; v++) {
              int edgeIdx = edges[e + v];

              double[] ptAndNorm = interpolateEdge(contour, val, edgeIdx, i, j, k, xVals, yVals,
                  zVals, gridValues, dx, dy, dz);

              double[] normal = new double[] {ptAndNorm[3], ptAndNorm[4], ptAndNorm[5]};

              // The builder automatically handles scaled rounding and map lookups
              faceIdx[v] =
                  builder.addVertex(ptAndNorm[0], ptAndNorm[1], ptAndNorm[2], normal, null);
            }

            // Output single-sided CCW triangles.
            // symja_webgl.js uses THREE.DoubleSide, which handles backfaces automatically.
            builder.addPolygon(faceIdx[0], faceIdx[2], faceIdx[1]);
          }
        }
      }
    }

    IExpr graphicsComplex = builder.build();
    if (graphicsComplex.equals(F.NIL)) {
      return F.NIL;
    }

    IASTAppendable result = F.ast(S.Graphics3D);
    result.append(graphicsComplex);

    result.append(F.Rule(S.PlotRange, F.List(F.List(F.num(xMin), F.num(xMax)),
        F.List(F.num(yMin), F.num(yMax)), F.List(F.num(zMin), F.num(zMax)))));
    result.append(F.Rule(S.BoxRatios, F.List(F.num(1), F.num(1), F.num(1))));

    return result;
  }

  private double[] interpolateEdge(double target, double[] val, int edge, int i, int j, int k,
      double[] xVals, double[] yVals, double[] zVals, double[][][] grid, double dx, double dy,
      double dz) {

    int[][] edgeToVertices = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}};
    int[][] vertexOffsets =
        {{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}};

    int v1 = edgeToVertices[edge][0];
    int v2 = edgeToVertices[edge][1];

    double diff = val[v2] - val[v1];
    double mu;

    if (Double.isNaN(diff) || diff == 0.0 || Math.abs(diff) < 1e-12) {
      mu = 0.5;
    } else {
      mu = (target - val[v1]) / diff;
    }

    mu = Math.max(0.0, Math.min(1.0, mu));

    double x1 = xVals[i + vertexOffsets[v1][0]];
    double y1 = yVals[j + vertexOffsets[v1][1]];
    double z1 = zVals[k + vertexOffsets[v1][2]];

    double x2 = xVals[i + vertexOffsets[v2][0]];
    double y2 = yVals[j + vertexOffsets[v2][1]];
    double z2 = zVals[k + vertexOffsets[v2][2]];

    double x = x1 + mu * (x2 - x1);
    double y = y1 + mu * (y2 - y1);
    double z = z1 + mu * (z2 - z1);

    x = Math.max(xVals[0], Math.min(xVals[xVals.length - 1], x));
    y = Math.max(yVals[0], Math.min(yVals[yVals.length - 1], y));
    z = Math.max(zVals[0], Math.min(zVals[zVals.length - 1], z));

    double[] n1 = getGridNormal(grid, i + vertexOffsets[v1][0], j + vertexOffsets[v1][1],
        k + vertexOffsets[v1][2], dx, dy, dz);
    double[] n2 = getGridNormal(grid, i + vertexOffsets[v2][0], j + vertexOffsets[v2][1],
        k + vertexOffsets[v2][2], dx, dy, dz);

    double nx = n1[0] + mu * (n2[0] - n1[0]);
    double ny = n1[1] + mu * (n2[1] - n1[1]);
    double nz = n1[2] + mu * (n2[2] - n1[2]);

    if (Double.isNaN(nx) || Double.isInfinite(nx))
      nx = 0.0;
    if (Double.isNaN(ny) || Double.isInfinite(ny))
      ny = 0.0;
    if (Double.isNaN(nz) || Double.isInfinite(nz))
      nz = 1.0;

    double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
    if (len > 1e-7 && !Double.isNaN(len)) {
      nx /= len;
      ny /= len;
      nz /= len;
    } else {
      nx = 0.0;
      ny = 0.0;
      nz = 1.0;
    }

    return new double[] {x, y, z, nx, ny, nz};
  }

  private double[] getGridNormal(double[][][] grid, int i, int j, int k, double dx, double dy,
      double dz) {
    int points = grid.length;
    double nx = 0.0, ny = 0.0, nz = 0.0;

    if (i == 0) {
      double v1 = grid[1][j][k], v0 = grid[0][j][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nx = (v1 - v0) / dx;
    } else if (i == points - 1) {
      double v1 = grid[points - 1][j][k], v0 = grid[points - 2][j][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nx = (v1 - v0) / dx;
    } else {
      double v1 = grid[i + 1][j][k], v0 = grid[i - 1][j][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nx = (v1 - v0) / (2.0 * dx);
    }

    if (j == 0) {
      double v1 = grid[i][1][k], v0 = grid[i][0][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        ny = (v1 - v0) / dy;
    } else if (j == points - 1) {
      double v1 = grid[i][points - 1][k], v0 = grid[i][points - 2][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        ny = (v1 - v0) / dy;
    } else {
      double v1 = grid[i][j + 1][k], v0 = grid[i][j - 1][k];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        ny = (v1 - v0) / (2.0 * dy);
    }

    if (k == 0) {
      double v1 = grid[i][j][1], v0 = grid[i][j][0];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nz = (v1 - v0) / dz;
    } else if (k == points - 1) {
      double v1 = grid[i][j][points - 1], v0 = grid[i][j][points - 2];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nz = (v1 - v0) / dz;
    } else {
      double v1 = grid[i][j][k + 1], v0 = grid[i][j][k - 1];
      if (!Double.isNaN(v1) && !Double.isNaN(v0))
        nz = (v1 - v0) / (2.0 * dz);
    }

    if (Double.isNaN(nx) || Double.isInfinite(nx))
      nx = 0.0;
    if (Double.isNaN(ny) || Double.isInfinite(ny))
      ny = 0.0;
    if (Double.isNaN(nz) || Double.isInfinite(nz))
      nz = 0.0;

    double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
    if (len > 1e-7 && !Double.isNaN(len)) {
      return new double[] {nx / len, ny / len, nz / len};
    }
    return new double[] {0.0, 0.0, 1.0};
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_4_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol,
        new IBuiltInSymbol[] {S.PlotPoints, S.PlotRange, S.ColorFunction, S.PlotStyle, S.BoxRatios,
            S.Mesh},
        new IExpr[] {F.ZZ(45), S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.True});
  }
}
