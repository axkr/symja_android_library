package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DiscretizeGraphics(g)</code> - the polygons of a two or three dimensional graphic, or of
 * a list of graphics primitives, as one <code>MeshRegion</code>.
 *
 * <p>
 * The polygons may be given by their corners or, inside a <code>GraphicsComplex</code>, by the
 * indices of its points. Corners with the same coordinates are merged into one mesh point, and the
 * points are numbered in the order the polygons first use them. Directives are skipped. A graphic
 * of a single other region primitive is discretized as that region.
 */
public class DiscretizeGraphics extends AbstractFunctionEvaluator {

  /** The polygons of a graphic and the points they share. */
  private static final class Mesh {
    /** The numbered points by their coordinates, in the order they are first used. */
    final Map<List<Double>, Integer> points = new LinkedHashMap<List<Double>, Integer>();

    /** The corner indices of each polygon. */
    final IASTAppendable faces = F.ListAlloc();

    /** The dimension of the points, <code>0</code> before the first one. */
    int dimension = 0;

    /**
     * Add a polygon by its corners.
     *
     * @return <code>false</code> if a corner isn't a numeric point of the dimension of the others
     */
    boolean addFace(IAST corners) {
      IASTAppendable face = F.ListAlloc(corners.argSize());
      for (IExpr corner : corners) {
        if (!corner.isList() || corner.argSize() < 2 || corner.argSize() > 3
            || (dimension != 0 && corner.argSize() != dimension)) {
          return false;
        }
        dimension = corner.argSize();
        Double[] coordinates = new Double[dimension];
        for (int i = 0; i < dimension; i++) {
          double c = corner.getAt(i + 1).evalfNaN();
          if (!Double.isFinite(c)) {
            return false;
          }
          // -0.0 and 0.0 are the same point
          coordinates[i] = Double.valueOf(c + 0.0);
        }
        List<Double> key = Arrays.asList(coordinates);
        Integer index = points.get(key);
        if (index == null) {
          index = Integer.valueOf(points.size() + 1);
          points.put(key, index);
        }
        face.append(F.ZZ(index.intValue()));
      }
      faces.append(face);
      return true;
    }

    IAST coordinates() {
      IASTAppendable result = F.ListAlloc(points.size());
      for (List<Double> point : points.keySet()) {
        IASTAppendable p = F.ListAlloc(point.size());
        for (Double c : point) {
          p.append(F.num(c.doubleValue()));
        }
        result.append(p);
      }
      return result;
    }
  }

  /** Primitives which aren't polygons or plane regions. */
  private static final java.util.Set<IExpr> UNSUPPORTED_PRIMITIVES =
      new java.util.HashSet<IExpr>(Arrays.asList(S.Arrow, S.Ball, S.BSplineCurve,
          S.BSplineSurface, S.BezierCurve, S.Cone, S.Cuboid, S.Cylinder, S.Ellipsoid,
          S.Hexahedron, S.Inset, S.Parallelepiped, S.Point, S.Polyhedron, S.Prism, S.Pyramid,
          S.Simplex, S.Sphere, S.Tetrahedron, S.Text, S.Tube));

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr primitives = ast.arg1();
    if (primitives.isAST(S.Graphics) || primitives.isAST(S.Graphics3D)) {
      if (primitives.argSize() < 1) {
        return F.NIL;
      }
      primitives = primitives.first();
    }
    List<IExpr> regions = new ArrayList<IExpr>();
    Mesh mesh = new Mesh();
    if (!collect(primitives, null, mesh, regions)) {
      return F.NIL;
    }
    if (mesh.faces.argSize() == 0) {
      if (regions.size() == 1) {
        // a single region primitive is discretized as that region
        IExpr result = engine.evaluate(F.unaryAST1(S.DiscretizeRegion, regions.get(0)));
        return result.isAST(S.MeshRegion) ? result : F.NIL;
      }
      return F.NIL;
    }
    if (!regions.isEmpty()) {
      return F.NIL;
    }
    return F.binaryAST2(S.MeshRegion, mesh.coordinates(), F.List(F.Polygon(mesh.faces)));
  }

  /**
   * Gather the polygons of <code>primitives</code> into <code>mesh</code> and the other region
   * primitives into <code>regions</code>.
   *
   * @param points the points of the enclosing <code>GraphicsComplex</code>, or <code>null</code>
   * @return <code>false</code> if a polygon can't be read
   */
  private static boolean collect(IExpr primitives, IAST points, Mesh mesh, List<IExpr> regions) {
    if (primitives.isList()) {
      for (IExpr primitive : (IAST) primitives) {
        if (!collect(primitive, points, mesh, regions)) {
          return false;
        }
      }
      return true;
    }
    if (!primitives.isAST() || primitives.argSize() < 1) {
      return true;
    }
    IAST primitive = (IAST) primitives;
    switch (primitive.headID()) {
      case ID.GraphicsComplex:
        if (primitive.argSize() < 2 || !primitive.arg1().isListOfLists()) {
          return false;
        }
        return collect(primitive.arg2(), (IAST) primitive.arg1(), mesh, regions);
      case ID.Style:
        return collect(primitive.arg1(), points, mesh, regions);
      case ID.Polygon:
      case ID.Triangle:
        if (primitive.argSize() != 1) {
          return false;
        }
        return addPolygons(primitive.arg1(), points, mesh);
      case ID.Annulus:
      case ID.Circle:
      case ID.Disk:
      case ID.DiskSegment:
      case ID.Line:
      case ID.Parallelogram:
      case ID.Rectangle:
      case ID.RegularPolygon:
        if (points != null) {
          return false;
        }
        regions.add(primitive);
        return true;
      default:
        // a directive is skipped, a primitive this function can't discretize stops it
        return !UNSUPPORTED_PRIMITIVES.contains(primitive.head());
    }
  }

  /**
   * Add one polygon or a list of polygons, given by their corners or by indices of
   * <code>points</code>.
   */
  private static boolean addPolygons(IExpr spec, IAST points, Mesh mesh) {
    if (!spec.isList() || spec.argSize() == 0) {
      return false;
    }
    IAST list = (IAST) spec;
    IExpr first = list.first();
    boolean several = points == null ? first.isList() && first.argSize() > 0 && first.first().isList()
        : first.isList();
    if (several) {
      for (IExpr polygon : list) {
        if (!addPolygons(polygon, points, mesh)) {
          return false;
        }
      }
      return true;
    }
    if (points == null) {
      return mesh.addFace(list);
    }
    IASTAppendable corners = F.ListAlloc(list.argSize());
    for (IExpr index : list) {
      int i = index.toIntDefault();
      if (i < 1 || i > points.argSize()) {
        return false;
      }
      corners.append(points.get(i));
    }
    return mesh.addFace(corners);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
