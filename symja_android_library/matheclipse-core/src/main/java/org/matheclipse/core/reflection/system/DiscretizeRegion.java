package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.MeshFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>DiscretizeRegion(region)</code> - a <code>MeshRegion</code> approximating a region in the
 * plane: a polygon is cut into triangles, curved boundaries are replaced by polygons of
 * {@link #SEGMENTS} sides, and lines and circles become mesh lines. A mesh region is returned as it
 * is.
 */
public class DiscretizeRegion extends AbstractFunctionEvaluator {

  /** The number of chords which replace a full turn of a curved boundary. */
  private static final int SEGMENTS = 64;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr region = ast.arg1();
    if (MeshFunctions.isMeshRegion(region)) {
      return region;
    }
    if (!region.isAST() || region.headID() < 0) {
      return F.NIL;
    }
    IAST reg = (IAST) region;
    switch (reg.headID()) {
      case ID.Annulus:
        return annulus(reg);
      case ID.Disk:
        return disk(reg);
      case ID.DiskSegment: {
        RegionPrimitives.DiskSegmentSpec spec = RegionPrimitives.parseDiskSegment(reg, engine);
        return spec == null ? F.NIL : diskSegment(spec);
      }
      case ID.Circle:
        return circle(reg);
      case ID.Line:
        return line(reg);
      case ID.Rectangle:
        return rectangle(reg);
      case ID.Polygon:
      case ID.Triangle:
        return outline(reg.argSize() == 0 ? F.NIL : reg.arg1());
      case ID.Parallelogram:
        return parallelogram(reg);
      case ID.RegularPolygon:
        return outline(RegionMember.regularPolygonVertices(reg, engine));
      default:
        return F.NIL;
    }
  }

  /** The numeric point <code>expr</code>, or <code>null</code>. */
  private static double[] point(IExpr expr) {
    if (!expr.isList2()) {
      return null;
    }
    double[] p = {expr.first().evalfNaN(), expr.second().evalfNaN()};
    return Double.isFinite(p[0]) && Double.isFinite(p[1]) ? p : null;
  }

  private static IExpr coordinates(List<double[]> points) {
    IASTAppendable result = F.ListAlloc(points.size());
    for (double[] p : points) {
      result.append(F.List(F.num(p[0]), F.num(p[1])));
    }
    return result;
  }

  private static IAST triangle(int a, int b, int c) {
    return F.Polygon(F.List(F.ZZ(a + 1), F.ZZ(b + 1), F.ZZ(c + 1)));
  }

  /** The mesh of the triangles with the 0-based corners in <code>triangles</code>. */
  private static IExpr triangles(List<double[]> points, List<int[]> triangles) {
    IASTAppendable cells = F.ListAlloc(triangles.size());
    for (int[] t : triangles) {
      cells.append(triangle(t[0], t[1], t[2]));
    }
    return F.binaryAST2(S.MeshRegion, coordinates(points), cells);
  }

  /** The polygon of the given corners cut into triangles. */
  private static IExpr outline(IExpr corners) {
    if (!corners.isListOfLists() || corners.argSize() < 3) {
      return F.NIL;
    }
    List<double[]> points = new ArrayList<>(corners.argSize());
    for (IExpr corner : (IAST) corners) {
      double[] p = point(corner);
      if (p == null) {
        return F.NIL;
      }
      points.add(p);
    }
    List<int[]> triangles = earClipping(points);
    return triangles == null ? F.NIL : triangles(points, triangles);
  }

  private static double cross(double[] a, double[] b, double[] c) {
    return (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0]);
  }

  /** Whether <code>p</code> lies in the triangle <code>a, b, c</code> of positive orientation. */
  private static boolean inTriangle(double[] p, double[] a, double[] b, double[] c) {
    return cross(a, b, p) >= 0.0 && cross(b, c, p) >= 0.0 && cross(c, a, p) >= 0.0;
  }

  /**
   * The triangles of a simple polygon: its ears are cut off one after the other.
   *
   * @return <code>null</code> if the outline isn't a simple polygon
   */
  private static List<int[]> earClipping(List<double[]> points) {
    final int n = points.size();
    double area = 0.0;
    for (int i = 0; i < n; i++) {
      double[] p = points.get(i);
      double[] q = points.get((i + 1) % n);
      area += p[0] * q[1] - q[0] * p[1];
    }
    List<Integer> rest = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      rest.add(area >= 0.0 ? i : n - 1 - i);
    }
    List<int[]> triangles = new ArrayList<>(n);
    while (rest.size() > 3) {
      int ear = findEar(points, rest);
      if (ear < 0) {
        return null;
      }
      int size = rest.size();
      triangles.add(new int[] {rest.get((ear + size - 1) % size), rest.get(ear),
          rest.get((ear + 1) % size)});
      rest.remove(ear);
    }
    triangles.add(new int[] {rest.get(0), rest.get(1), rest.get(2)});
    return triangles;
  }

  /** The position in <code>rest</code> of a corner which can be cut off, or <code>-1</code>. */
  private static int findEar(List<double[]> points, List<Integer> rest) {
    final int size = rest.size();
    for (int i = 0; i < size; i++) {
      int previous = rest.get((i + size - 1) % size);
      int next = rest.get((i + 1) % size);
      double[] a = points.get(previous);
      double[] b = points.get(rest.get(i));
      double[] c = points.get(next);
      if (cross(a, b, c) <= 0.0) {
        continue;
      }
      boolean empty = true;
      for (int index : rest) {
        if (index != previous && index != next && index != rest.get(i)
            && inTriangle(points.get(index), a, b, c)) {
          empty = false;
          break;
        }
      }
      if (empty) {
        return i;
      }
    }
    return -1;
  }

  /** The numeric semi axes of a radius <code>r</code> or <code>{rx, ry}</code>. */
  private static double[] radii(IExpr radius) {
    double[] r = radius.isList2() ? point(radius)
        : new double[] {radius.evalfNaN(), radius.evalfNaN()};
    return r != null && r[0] > 0.0 && r[1] > 0.0 ? r : null;
  }

  private static double[] angles(IAST reg, int position) {
    return reg.argSize() >= position ? point(reg.get(position)) : new double[] {0.0, 2.0 * Math.PI};
  }

  /** The points of the arc from <code>angles[0]</code> to <code>angles[1]</code>. */
  private static List<double[]> arc(double[] center, double[] radii, double[] angles,
      boolean closed) {
    double sweep = angles[1] - angles[0];
    int count = Math.max(2, (int) Math.ceil(SEGMENTS * Math.abs(sweep) / (2.0 * Math.PI)));
    List<double[]> points = new ArrayList<>(count + 1);
    for (int i = 0; i < (closed ? count : count + 1); i++) {
      double t = angles[0] + sweep * i / count;
      points.add(new double[] {center[0] + radii[0] * Math.cos(t),
          center[1] + radii[1] * Math.sin(t)});
    }
    return points;
  }

  private static boolean isFullTurn(double[] angles) {
    return Math.abs(angles[1] - angles[0]) >= 2.0 * Math.PI - 1.0e-12;
  }

  private static double[] centerOf(IAST reg) {
    return reg.argSize() == 0 ? new double[] {0.0, 0.0} : point(reg.arg1());
  }

  private static double[] radiiOf(IAST reg) {
    return reg.argSize() < 2 ? new double[] {1.0, 1.0} : radii(reg.arg2());
  }

  /** The fan of triangles around the center of a disk or a sector. */
  private static IExpr disk(IAST reg) {
    double[] center = centerOf(reg);
    double[] radii = radiiOf(reg);
    double[] angles = angles(reg, 3);
    if (center == null || radii == null || angles == null) {
      return F.NIL;
    }
    boolean full = isFullTurn(angles);
    List<double[]> points = new ArrayList<>(SEGMENTS + 2);
    points.add(center);
    points.addAll(arc(center, radii, angles, full));
    int rim = points.size() - 1;
    List<int[]> triangles = new ArrayList<>(rim);
    for (int i = 1; i < (full ? rim + 1 : rim); i++) {
      triangles.add(new int[] {0, i, i == rim ? 1 : i + 1});
    }
    return triangles(points, triangles);
  }

  /** The strip of triangles between the two rims of an annulus. */
  private static IExpr annulus(IAST reg) {
    IExpr radii = reg.argSize() >= 2 ? reg.arg2() : F.List(F.C1, F.C2);
    double[] r = point(radii);
    double[] center = centerOf(reg);
    double[] angles = angles(reg, 3);
    if (r == null || center == null || angles == null || !(r[0] >= 0.0) || !(r[1] > r[0])) {
      return F.NIL;
    }
    boolean full = isFullTurn(angles);
    List<double[]> inner = arc(center, new double[] {r[0], r[0]}, angles, full);
    List<double[]> outer = arc(center, new double[] {r[1], r[1]}, angles, full);
    final int count = inner.size();
    List<double[]> points = new ArrayList<>(2 * count);
    points.addAll(inner);
    points.addAll(outer);
    List<int[]> triangles = new ArrayList<>(2 * count);
    for (int i = 0; i < (full ? count : count - 1); i++) {
      int next = (i + 1) % count;
      triangles.add(new int[] {i, count + i, count + next});
      triangles.add(new int[] {i, count + next, next});
    }
    return triangles(points, triangles);
  }

  /** The polygon between the arc of a segment and its chord. */
  private static IExpr diskSegment(RegionPrimitives.DiskSegmentSpec spec) {
    double[] center = point(spec.center);
    double[] radii = {spec.rx.evalfNaN(), spec.ry.evalfNaN()};
    double[] angles = {spec.theta1.evalfNaN(), spec.theta2.evalfNaN()};
    if (center == null || !(radii[0] > 0.0) || !(radii[1] > 0.0) || !Double.isFinite(angles[0])
        || !Double.isFinite(angles[1])) {
      return F.NIL;
    }
    List<double[]> points = arc(center, radii, angles, false);
    List<int[]> triangles = earClipping(points);
    return triangles == null ? F.NIL : triangles(points, triangles);
  }

  /** The mesh of the line through the points in their order. */
  private static IExpr lineMesh(List<double[]> points, boolean closed) {
    IASTAppendable indices = F.ListAlloc(points.size() + 1);
    for (int i = 1; i <= points.size(); i++) {
      indices.append(F.ZZ(i));
    }
    if (closed) {
      indices.append(F.C1);
    }
    return F.binaryAST2(S.MeshRegion, coordinates(points), F.List(F.Line(indices)));
  }

  private static IExpr circle(IAST reg) {
    double[] center = centerOf(reg);
    double[] radii = radiiOf(reg);
    double[] angles = angles(reg, 3);
    if (center == null || radii == null || angles == null) {
      return F.NIL;
    }
    boolean full = isFullTurn(angles);
    return lineMesh(arc(center, radii, angles, full), full);
  }

  private static IExpr line(IAST reg) {
    if (reg.argSize() != 1 || !reg.arg1().isListOfLists() || reg.arg1().argSize() < 2) {
      return F.NIL;
    }
    List<double[]> points = new ArrayList<>(reg.arg1().argSize());
    for (IExpr corner : (IAST) reg.arg1()) {
      double[] p = point(corner);
      if (p == null) {
        return F.NIL;
      }
      points.add(p);
    }
    return lineMesh(points, false);
  }

  private static IExpr rectangle(IAST reg) {
    double[] low = reg.argSize() == 0 ? new double[] {0.0, 0.0} : point(reg.arg1());
    if (low == null) {
      return F.NIL;
    }
    double[] high = reg.argSize() >= 2 ? point(reg.arg2()) : new double[] {low[0] + 1, low[1] + 1};
    if (high == null || !(high[0] > low[0]) || !(high[1] > low[1])) {
      return F.NIL;
    }
    return outline(F.List(F.List(F.num(low[0]), F.num(low[1])), F.List(F.num(high[0]), F.num(low[1])),
        F.List(F.num(high[0]), F.num(high[1])), F.List(F.num(low[0]), F.num(high[1]))));
  }

  private static IExpr parallelogram(IAST reg) {
    if (reg.argSize() != 2 || !reg.arg2().isListOfLists() || reg.arg2().argSize() != 2) {
      return F.NIL;
    }
    double[] p = point(reg.arg1());
    double[] u = point(reg.arg2().first());
    double[] v = point(reg.arg2().second());
    if (p == null || u == null || v == null) {
      return F.NIL;
    }
    return outline(F.List(F.List(F.num(p[0]), F.num(p[1])),
        F.List(F.num(p[0] + u[0]), F.num(p[1] + u[1])),
        F.List(F.num(p[0] + u[0] + v[0]), F.num(p[1] + u[1] + v[1])),
        F.List(F.num(p[0] + v[0]), F.num(p[1] + v[1]))));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
