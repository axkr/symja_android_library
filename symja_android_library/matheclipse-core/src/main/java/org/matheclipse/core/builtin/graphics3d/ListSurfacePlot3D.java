package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.quickhull.ConvexHull3D;

/**
 * <code>ListSurfacePlot3D(points)</code>: a surface through points in space.
 *
 * <ul>
 * <li>An array <code>{{p11, p12, ...}, {p21, p22, ...}, ...}</code> of points is joined cell by
 * cell: <code>p(i,j)</code>, <code>p(i+1,j)</code>, <code>p(i+1,j+1)</code> and <code>p(i,j+1)</code>
 * make a quadrilateral.</li>
 * <li>A list <code>{p1, p2, ...}</code> of scattered points is wrapped in the surface of its convex
 * hull, which is the surface of a set of points sampled from a closed convex shape such as a sphere
 * or an ellipsoid.</li>
 * </ul>
 *
 * <code>PlotStyle</code> gives the colour, <code>ColorFunction</code> colours by <code>x, y, z</code>.
 */
public class ListSurfacePlot3D extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (argSize != 1 || !ast.arg1().isList()) {
      return F.NIL;
    }
    IAST data = (IAST) ast.arg1();
    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, false);
    if (isPointArray(data)) {
      int rows = data.argSize();
      int cols = data.arg1().argSize();
      double[][][] grid = new double[rows][cols][];
      for (int i = 0; i < rows; i++) {
        IAST row = (IAST) data.get(i + 1);
        if (row.argSize() != cols) {
          return Errors.printMessage(S.ListSurfacePlot3D, "arrayerr", F.list(data), engine);
        }
        for (int j = 0; j < cols; j++) {
          grid[i][j] = Plot3DTools.finitePoint3D((IAST) row.get(j + 1));
        }
      }
      Plot3DTools.addSurface(builder, grid, false, false, null, true);
    } else if (data.forAll(p -> p.isList() && p.argSize() == 3)) {
      if (data.argSize() < 3) {
        // Data value `1` must be a list of at least 3 points in 3D.
        return Errors.printMessage(S.ListSurfacePlot3D, "gpts1", F.list(engine.evalN(data)),
            engine);
      }
      if (!addConvexHull(builder, data)) {
        // points on a line or in a plane enclose nothing: an empty picture
        return Plot3DTools.surfaceGraphics3D(F.CEmptyList, ast, argSize, options);
      }
    } else {
      // `1` is not a valid array of points or list of points in three dimensions
      return Errors.printMessage(S.ListSurfacePlot3D, "arrayerr", F.list(data), engine);
    }
    if (builder.polygonCount() == 0) {
      return F.NIL;
    }
    IExpr plotStyle = options[Plot3DTools.X_PLOT_STYLE];
    IExpr style = Plot3DTools.surfaceStyle(0, plotStyle);
    double[] extent = extent(builder);
    PlotColorFunction colors = Plot3DTools
        .plotColors(PlotColorFunction.Family.SURFACE_3D, options, S.ListSurfacePlot3D, engine)
        .ranges(extent[0], extent[1], extent[2], extent[3], extent[4], extent[5]).build();
    if (colors != null) {
      IExpr uniform = Plot3DTools.colorVertices(builder, colors, p -> p, engine);
      style = uniform.isPresent() ? uniform : F.NIL;
    }
    if (style.isPresent()) {
      Plot3DTools.applyStyle(builder, style, options[Plot3DTools.X_MESH]);
    } else {
      builder.setStyle(F.EdgeForm(S.None));
    }
    return Plot3DTools.surfaceGraphics3D(F.List(builder.build()), ast, argSize, options);
  }

  /** <code>{{p11, p12, ...}, ...}</code>: rows of at least two points each, at least two rows. */
  private static boolean isPointArray(IAST data) {
    return data.argSize() >= 2 && data.forAll(row -> row.isList() && row.argSize() >= 2
        && ((IAST) row).forAll(p -> p.isList() && p.argSize() == 3));
  }

  /** The faces of the convex hull of the points, each listed counter-clockwise from outside. */
  private static boolean addConvexHull(GraphicsComplexBuilder builder, IAST points) {
    // the hull is found in machine numbers; exact coordinates make its predicates symbolic
    // points which are no finite numbers are left out
    org.matheclipse.core.interfaces.IASTAppendable numeric = F.ListAlloc(points.argSize());
    for (IExpr p : points) {
      double[] xyz = Plot3DTools.finitePoint3D((IAST) p);
      if (xyz != null) {
        numeric.append(F.List(F.num(xyz[0]), F.num(xyz[1]), F.num(xyz[2])));
      }
    }
    if (numeric.argSize() < 4) {
      return false;
    }
    ConvexHull3D hull = new ConvexHull3D();
    try {
      hull.build(numeric);
      hull.triangulate();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // fewer than four points off one plane have no hull
      return false;
    }
    int count = hull.getNumVertices();
    IExpr[] coords = new IExpr[3 * count];
    hull.getVertices(coords);
    int[][] faces = hull.getFaces();
    int[] index = new int[count];
    for (int v = 0; v < count; v++) {
      index[v] = builder.addVertex(coords[3 * v].evalf(), coords[3 * v + 1].evalf(),
          coords[3 * v + 2].evalf(), null, null);
    }
    for (int[] face : faces) {
      int[] polygon = new int[face.length];
      for (int k = 0; k < face.length; k++) {
        polygon[k] = index[face[k]];
      }
      builder.addPolygon(polygon);
    }
    return true;
  }

  /** <code>{xmin, xmax, ymin, ymax, zmin, zmax}</code> of the vertices. */
  private static double[] extent(GraphicsComplexBuilder builder) {
    double[] e = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    for (int v = 1; v <= builder.vertexCount(); v++) {
      double[] p = builder.coordinatesOf(v);
      for (int a = 0; a < 3; a++) {
        e[2 * a] = Math.min(e[2 * a], p[a]);
        e[2 * a + 1] = Math.max(e[2 * a + 1], p[a]);
      }
    }
    return e;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.listPlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
