package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of ListPlot3D. Supports: 1. Regular Data: {{z11, z12...}, ...} (Height Map) 2.
 * Irregular Data: {{x1,y1,z1}, {x2,y2,z2}, ...} (Triangulated Point Cloud)
 */
public class ListPlot3D extends AbstractFunctionOptionEvaluator {

  public ListPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    IExpr data = ast.arg1();
    if (!data.isList()) {
      return F.NIL;
    }

    IExpr plotRangeOpt = options[Plot3DTools.X_PLOT_RANGE];
    IExpr dataRangeOpt = options[Plot3DTools.X_DATA_RANGE];
    IExpr boxRatiosOpt = options[Plot3DTools.X_BOX_RATIOS];
    IExpr meshOpt = options[Plot3DTools.X_MESH];
    IExpr plotStyleOpt = options[Plot3DTools.X_PLOT_STYLE];

    IAST listData = (IAST) data;
    if (listData.isEmpty()) {
      return F.Graphics3D(F.CEmptyList);
    }

    // --- Data Detection ---
    boolean treatAsCoordinates = false;

    // Check dimensions
    IExpr firstRow = listData.arg1();
    if (firstRow.isList()) {
      int cols = ((IAST) firstRow).argSize();
      // three columns of numbers are {x, y, z} triples. DataRange used to be part of this test,
      // so giving it silently reinterpreted a coordinate list as a rectangular height array.
      if (cols == 3 && ((IAST) firstRow).forAll(x -> x.isNumber())) {
        treatAsCoordinates = true;
      }
    }

    if (treatAsCoordinates) {
      return processCoordinateList(listData, boxRatiosOpt, plotRangeOpt, meshOpt, plotStyleOpt,
          originalAST, argSize,
          RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine));
    } else {
      if (isRectangularArray(listData)) {
        return processHeightMap(listData, dataRangeOpt, boxRatiosOpt, plotRangeOpt, meshOpt,
            plotStyleOpt, options[Plot3DTools.X_MESH_STYLE], originalAST, argSize,
            RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine),
            options[Plot3DTools.X_BOUNDARY_STYLE],
            Plot3DTools.plotColors(PlotColorFunction.Family.SURFACE_3D, options, S.ListPlot3D,
                engine));
      }
    }

    return F.NIL;
  }

  /**
   * Processes a list of {x,y,z} coordinates using Delaunay Triangulation.
   */
  private IExpr processCoordinateList(IAST data, IExpr boxRatiosOpt, IExpr plotRangeOpt,
      IExpr meshOpt, IExpr plotStyleOpt, IAST originalAST, int argSize,
      RegionFunctionFilter region) {
    int n = data.argSize();
    if (n < 3)
      return F.NIL; // Need at least 3 points for a surface

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
    Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(0, plotStyleOpt), meshOpt);

    // 1. Extract Points and Register with Builder
    List<PointXYZ> points = new ArrayList<>(n);
    for (int i = 1; i <= n; i++) {
      if (!data.get(i).isList() || ((IAST) data.get(i)).argSize() != 3) {
        return F.NIL;
      }
      IAST row = (IAST) data.get(i);
      double x = row.arg1().evalfNaN();
      double y = row.arg2().evalfNaN();
      double z = row.arg3().evalfNaN();

      if (!Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) && !Double.isInfinite(x)
          && !Double.isInfinite(y) && !Double.isInfinite(z)
          && (region == null || region.accepts(x, y, z))) {
        int idx = builder.addVertex(x, y, z, null, null);
        points.add(new PointXYZ(x, y, z, idx));
      }
    }

    // 2. Triangulate (Projected to XY plane)
    List<Triangle> triangles = Triangulator.delaunay(points);

    // 3. Construct Polygons
    for (Triangle t : triangles) {
      builder.addPolygon(t.p1, t.p2, t.p3); // Triangulator retains builder indices
    }

    IExpr graphicsComplex = builder.build();
    if (graphicsComplex.equals(F.NIL)) {
      return F.NIL;
    }

    return Plot3DTools.graphics3D(graphicsComplex, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, plotRangeOpt),
            F.Rule(S.BoxRatios, boxRatiosOpt.isList() ? boxRatiosOpt : Plot3DTools.FLAT_BOX_RATIOS),
            F.Rule(S.Axes, S.True), F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  /**
   * Processes a rectangular array of z-values {{z11, z12...}, ...}
   */
  private IExpr processHeightMap(IAST heightData, IExpr dataRangeOpt, IExpr boxRatiosOpt,
      IExpr plotRangeOpt, IExpr meshOpt, IExpr plotStyleOpt, IExpr meshStyleOpt, IAST originalAST,
      int argSize, RegionFunctionFilter region, IExpr boundaryStyle,
      PlotColorFunction.Builder colorBuilder) {
    int rows = heightData.argSize();
    IExpr firstRow = heightData.arg1();
    int cols = ((IAST) firstRow).argSize();

    double xMin = 1.0;
    double xMax = cols;
    double yMin = 1.0;
    double yMax = rows;

    if (dataRangeOpt.isList() && ((IAST) dataRangeOpt).argSize() == 2) {
      IAST range = (IAST) dataRangeOpt;
      if (range.arg1().isList() && range.arg2().isList()) {
        IAST xRange = (IAST) range.arg1();
        IAST yRange = (IAST) range.arg2();
        double xMinValue = xRange.arg1().evalfNaN();
        double xMaxValue = xRange.arg2().evalfNaN();
        double yMinValue = yRange.arg1().evalfNaN();
        double yMaxValue = yRange.arg2().evalfNaN();
        if (!Double.isNaN(xMinValue) && !Double.isNaN(xMaxValue) && !Double.isNaN(yMinValue)
            && !Double.isNaN(yMaxValue)) {
          // otherwise ignore the parsing error and stick to the defaults
          xMin = xMinValue;
          xMax = xMaxValue;
          yMin = yMinValue;
          yMax = yMaxValue;
        }
      }
    }

    // the grid is handed to the shared surface builder, so this plot gets the same winding,
    // vertex normals and mesh lines as the ones that sample a function
    double[][][] grid = new double[rows][cols][];
    for (int i = 1; i <= rows; i++) {
      IExpr arg = heightData.get(i);
      if (!arg.isAST() || arg.argSize() != cols) {
        return F.NIL;
      }
      IAST row = (IAST) arg;
      double y = (rows > 1) ? yMin + (i - 1) * (yMax - yMin) / (rows - 1.0) : yMin;

      for (int j = 1; j <= cols; j++) {
        double x = (cols > 1) ? xMin + (j - 1) * (xMax - xMin) / (cols - 1.0) : xMin;
        double z = row.get(j).evalfNaN();
        if (Double.isFinite(z) && (region == null || region.accepts(x, y, z))) {
          grid[i - 1][j - 1] = new double[] {x, y, z};
        }
      }
    }
    double[] box = Plot3DTools.extentOf(grid);
    PlotColorFunction colorMap =
        colorBuilder.ranges(box[0], box[1], box[2], box[3], box[4], box[5]).build();
    IExpr[][] colors = null;
    if (colorMap != null) {
      colors = new IExpr[rows][cols];
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          double[] p = grid[i][j];
          if (p != null) {
            colors[i][j] = colorMap.color(p[0], p[1], p[2]);
          }
        }
      }
    }
    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, colors != null);
    Plot3DTools.applyStyle(builder, Plot3DTools.surfaceStyle(0, plotStyleOpt), meshOpt);
    Plot3DTools.addSurface(builder, grid, false, false, colors, true, meshOpt, meshStyleOpt);

    IExpr graphicsComplex = builder.build();
    if (graphicsComplex.equals(F.NIL)) {
      return F.NIL;
    }
    // the rim of the surface, and the rim of every hole a RegionFunction or a datum without a
    // value left in it
    graphicsComplex = Plot3DTools.withBoundary(graphicsComplex, grid, boundaryStyle);

    return Plot3DTools.graphics3D(graphicsComplex, originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, plotRangeOpt),
            F.Rule(S.BoxRatios, boxRatiosOpt.isList() ? boxRatiosOpt : Plot3DTools.FLAT_BOX_RATIOS),
            F.Rule(S.Axes, S.True), F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)});
  }

  private boolean isRectangularArray(IAST list) {
    if (list.isEmpty())
      return false;
    IExpr first = list.arg1();
    if (!first.isList())
      return false;

    IAST firstRow = (IAST) first;
    if (firstRow.isEmpty())
      return false;

    int expectedCols = firstRow.argSize();
    for (int i = 2; i <= list.argSize(); i++) {
      if (list.get(i).isList() && ((IAST) list.get(i)).argSize() != expectedCols) {
        return false;
      }
    }
    return true;
  }

  // --- Simple Delaunay Triangulation (Bowyer-Watson) Helpers ---

  private static class PointXYZ {
    double x, y, z;
    int originalIndex; // Mapped to GraphicsComplexBuilder index

    PointXYZ(double x, double y, double z, int id) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.originalIndex = id;
    }
  }

  private static class Triangle {
    int p1, p2, p3;
    PointXYZ a, b, c;

    Triangle(PointXYZ a, PointXYZ b, PointXYZ c) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.p1 = a.originalIndex;
      this.p2 = b.originalIndex;
      this.p3 = c.originalIndex;
    }

    boolean hasVertex(PointXYZ p) {
      return p == a || p == b || p == c;
    }
  }

  private static class Triangulator {
    static List<Triangle> delaunay(List<PointXYZ> points) {
      List<Triangle> triangulation = new ArrayList<>();

      // 1. Super Triangle (must encompass all points)
      double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
      double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

      for (PointXYZ p : points) {
        if (p.x < minX)
          minX = p.x;
        if (p.x > maxX)
          maxX = p.x;
        if (p.y < minY)
          minY = p.y;
        if (p.y > maxY)
          maxY = p.y;
      }

      double dx = maxX - minX;
      double dy = maxY - minY;
      double dMax = Math.max(dx, dy);
      if (dMax == 0.0)
        dMax = 1.0;

      double midX = (minX + maxX) / 2.0;
      double midY = (minY + maxY) / 2.0;

      PointXYZ s1 = new PointXYZ(midX - 20 * dMax, midY - dMax, 0, -1);
      PointXYZ s2 = new PointXYZ(midX, midY + 20 * dMax, 0, -2);
      PointXYZ s3 = new PointXYZ(midX + 20 * dMax, midY - dMax, 0, -3);

      triangulation.add(new Triangle(s1, s2, s3));

      // 2. Incremental Add
      for (PointXYZ p : points) {
        List<Triangle> badTriangles = new ArrayList<>();
        for (Triangle t : triangulation) {
          if (isPointInCircumcircle(p, t)) {
            badTriangles.add(t);
          }
        }

        List<Edge> polygon = new ArrayList<>();
        for (Triangle t : badTriangles) {
          addEdge(polygon, t.a, t.b);
          addEdge(polygon, t.b, t.c);
          addEdge(polygon, t.c, t.a);
        }

        triangulation.removeAll(badTriangles);
        for (Edge edge : polygon) {
          triangulation.add(new Triangle(edge.p1, edge.p2, p));
        }
      }

      // 3. Cleanup Super Triangle
      triangulation.removeIf(t -> t.hasVertex(s1) || t.hasVertex(s2) || t.hasVertex(s3));

      return triangulation;
    }

    private static boolean isPointInCircumcircle(PointXYZ p, Triangle t) {
      double ax = t.a.x, ay = t.a.y;
      double bx = t.b.x, by = t.b.y;
      double cx = t.c.x, cy = t.c.y;

      double D = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
      double Ux = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay)
          + (cx * cx + cy * cy) * (ay - by)) / D;
      double Uy = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx)
          + (cx * cx + cy * cy) * (bx - ax)) / D;
      double rSq = (ax - Ux) * (ax - Ux) + (ay - Uy) * (ay - Uy);
      double dSq = (p.x - Ux) * (p.x - Ux) + (p.y - Uy) * (p.y - Uy);

      return dSq <= rSq; // Inside or on edge
    }

    private static void addEdge(List<Edge> polygon, PointXYZ a, PointXYZ b) {
      for (int i = 0; i < polygon.size(); i++) {
        Edge e = polygon.get(i);
        if ((e.p1 == a && e.p2 == b) || (e.p1 == b && e.p2 == a)) {
          polygon.remove(i); // Shared edge removed (internal)
          return;
        }
      }
      polygon.add(new Edge(a, b));
    }

    private static class Edge {
      PointXYZ p1, p2;

      Edge(PointXYZ a, PointXYZ b) {
        p1 = a;
        p2 = b;
      }
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.listPlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
