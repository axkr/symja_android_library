package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Shared geometry kernel for {@link PolygonAngle} and {@link PolygonCoordinates}.
 *
 * <p>
 * A polygon is represented by the list of its vertices in the order in which they are connected.
 * Two derived orders are used by the polygon functions:
 *
 * <ul>
 * <li>the <i>canonical order</i> of {@link #canonicalOrder(IAST, EvalEngine)} keeps the cyclic
 * sequence of the vertices, but always starts at the smallest vertex and always runs counter
 * clockwise. This is the order in which {@link PolygonAngle} reports the angles.
 * <li>the <i>canonical coordinates</i> of {@link #sortedCoordinates(IAST)} drop the connection
 * information and simply sort the vertices. This is the result of {@link PolygonCoordinates}.
 * </ul>
 *
 * <p>
 * The counter clockwise orientation is what makes the reflex vertices of a concave polygon
 * detectable: at a reflex vertex the polygon turns clockwise, so the cross product of the two
 * adjacent edges is negative and the interior angle is the larger one of the two angles enclosed by
 * the edges.
 */
public final class PolygonGeometry {

  /** Returned by {@link #vertexIndex(IExpr, IAST)} for the {@link S#All} specification. */
  public static final int ALL_VERTICES = 0;

  /** Returned by {@link #vertexIndex(IExpr, IAST)} if the vertex cannot be identified. */
  public static final int INVALID_VERTEX = -1;

  private PolygonGeometry() {}

  /**
   * Extract the vertices of a polygon.
   *
   * <p>
   * Accepted forms are <code>{p1, ..., pn}</code>, <code>Polygon({p1, ..., pn})</code> and
   * <code>Triangle({p1, p2, p3})</code>. The argument-less <code>Polygon()</code> and
   * <code>Triangle()</code> default to the unit triangle <code>{{0,0}, {1,0}, {0,1}}</code>.
   * Coordinates which repeat the previous vertex are dropped, because they don't add a corner to
   * the polygon.
   *
   * @return the list of at least three vertices or {@link F#NIL} if <code>arg</code> doesn't define
   *         a polygon
   */
  public static IAST vertices(IExpr arg) {
    IExpr list = arg;
    if (arg.isAST(S.Polygon) || arg.isAST(S.Triangle)) {
      IAST region = (IAST) arg;
      if (region.argSize() == 0) {
        return F.List(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
      }
      if (region.argSize() != 1) {
        // a polygon with holes is not supported
        return F.NIL;
      }
      list = region.arg1();
    }
    if (!list.isList()) {
      return F.NIL;
    }
    IAST points = (IAST) list;
    if (points.argSize() < 3 || !points.arg1().isList()) {
      return F.NIL;
    }
    int dimension = points.arg1().argSize();
    if (dimension < 2) {
      return F.NIL;
    }
    for (int i = 2; i < points.size(); i++) {
      if (!points.get(i).isList() || points.get(i).argSize() != dimension) {
        return F.NIL;
      }
    }
    return removeRepeatedVertices(points);
  }

  /**
   * Extract the vertices of a non degenerate polygon.
   *
   * <p>
   * Behaves like {@link #vertices(IExpr)}, but additionally rejects polygons which enclose no area.
   */
  public static IAST vertices(IExpr arg, EvalEngine engine) {
    IAST points = vertices(arg);
    if (points.isPresent() && isDegenerate(points, engine)) {
      return F.NIL;
    }
    return points;
  }

  /**
   * Drop every vertex which repeats its cyclic predecessor.
   *
   * @return the reduced list of vertices or {@link F#NIL} if less than three vertices remain
   */
  private static IAST removeRepeatedVertices(IAST points) {
    int n = points.argSize();
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 1; i <= n; i++) {
      if (!points.get(i).equals(points.get(i % n + 1))) {
        result.append(points.get(i));
      }
    }
    return result.argSize() < 3 ? F.NIL : result;
  }

  /**
   * <code>true</code> if the polygon provably encloses no area, which is the case for collinear
   * vertices. Symbolic vertices, for which the area cannot be decided, are not degenerate.
   */
  public static boolean isDegenerate(IAST points, EvalEngine engine) {
    IExpr doubledArea = signedDoubleArea(points, engine);
    return doubledArea.isPresent() && doubledArea.isZero();
  }

  /** The number of coordinates of each vertex; <code>2</code> for a polygon in the plane. */
  public static int dimension(IAST points) {
    return points.arg1().argSize();
  }

  /**
   * Twice the signed area of a planar polygon, computed with the shoelace formula
   * <code>Sum(x_i*y_(i+1) - x_(i+1)*y_i)</code>. The sign is positive for counter clockwise and
   * negative for clockwise oriented vertices.
   *
   * @return {@link F#NIL} if the polygon isn't planar
   */
  public static IExpr signedDoubleArea(IAST points, EvalEngine engine) {
    if (dimension(points) != 2) {
      return F.NIL;
    }
    int n = points.argSize();
    IASTAppendable sum = F.PlusAlloc(n);
    for (int i = 1; i <= n; i++) {
      IAST current = (IAST) points.get(i);
      IAST next = (IAST) points.get(i % n + 1);
      sum.append(F.Subtract(//
          F.Times(current.arg1(), next.arg2()), //
          F.Times(next.arg1(), current.arg2())));
    }
    return engine.evaluate(sum);
  }

  /**
   * Rewrite the vertices in canonical order: the cyclic sequence of the vertices is preserved, but
   * it is oriented counter clockwise and starts at the smallest vertex.
   *
   * @return {@link F#NIL} if the orientation cannot be decided, which is the case for a non planar
   *         polygon and for symbolic coordinates
   */
  public static IAST canonicalOrder(IAST points, EvalEngine engine) {
    IExpr doubledArea = signedDoubleArea(points, engine);
    if (doubledArea.isNIL()) {
      return F.NIL;
    }
    boolean clockwise = doubledArea.isNegativeResult();
    if (!clockwise && !doubledArea.isPositiveResult()) {
      return F.NIL;
    }
    int n = points.argSize();
    int start = 1;
    for (int i = 2; i <= n; i++) {
      if (points.get(i).compareTo(points.get(start)) < 0) {
        start = i;
      }
    }
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      // walk the cycle backwards if the vertices are oriented clockwise
      int index = clockwise ? (start - 1 - i + n) % n : (start - 1 + i) % n;
      result.append(points.get(index + 1));
    }
    return result;
  }

  /** The vertices of the polygon in canonical, that is sorted, order. */
  public static IAST sortedCoordinates(IAST points) {
    return EvalAttributes.copySort(points);
  }

  /**
   * Resolve a vertex specification to the index <code>1..n</code> of the meant vertex.
   *
   * <p>
   * A vertex can be given as the integer index <code>1..n</code>, as the coordinates
   * <code>{x, y}</code> of one of the vertices, as <code>Point({x, y})</code> or as {@link S#All}.
   * An omitted specification is equivalent to {@link S#All}.
   *
   * @return <code>1..n</code>, {@link #ALL_VERTICES} or {@link #INVALID_VERTEX}
   */
  public static int vertexIndex(IExpr vertexSpec, IAST points) {
    if (vertexSpec.isNIL() || vertexSpec.equals(S.All)) {
      return ALL_VERTICES;
    }
    if (vertexSpec.isInteger()) {
      int index = vertexSpec.toIntDefault();
      return (index >= 1 && index <= points.argSize()) ? index : INVALID_VERTEX;
    }
    IExpr point = vertexSpec.isAST(S.Point, 2) ? vertexSpec.first() : vertexSpec;
    for (int i = 1; i < points.size(); i++) {
      if (point.equals(points.get(i))) {
        return i;
      }
    }
    return INVALID_VERTEX;
  }

  /**
   * The interior angle at the vertex <code>vertexIndex</code>:
   * <code>ArcCos(u.w/(Sqrt(u.u)*Sqrt(w.w)))</code> for the vectors <code>u</code> and
   * <code>w</code> pointing from the vertex to its two neighbours. At a reflex vertex the
   * complement <code>2*Pi-angle</code> is returned.
   *
   * <p>
   * The vertices must be in the canonical order of {@link #canonicalOrder(IAST, EvalEngine)},
   * otherwise the reflex vertices cannot be identified.
   */
  public static IExpr interiorAngle(IAST points, int vertexIndex, EvalEngine engine) {
    int n = points.argSize();
    IExpr vertex = points.get(vertexIndex);
    IExpr previous = points.get((vertexIndex + n - 2) % n + 1);
    IExpr next = points.get(vertexIndex % n + 1);
    IExpr u = engine.evaluate(F.Subtract(previous, vertex));
    IExpr w = engine.evaluate(F.Subtract(next, vertex));
    IExpr denominator = engine.evaluate(F.Times(F.Sqrt(F.Dot(u, u)), F.Sqrt(F.Dot(w, w))));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr angle = engine.evaluate(F.ArcCos(F.Divide(F.Dot(u, w), denominator)));
    if (isReflex(vertex, previous, next, engine)) {
      return engine.evaluate(F.Subtract(F.C2Pi, angle));
    }
    return angle;
  }

  /**
   * <code>true</code> if the counter clockwise oriented polygon turns clockwise at the vertex,
   * which means that its interior angle is greater than <code>Pi</code>.
   */
  private static boolean isReflex(IExpr vertex, IExpr previous, IExpr next, EvalEngine engine) {
    if (!vertex.isList2() || !previous.isList2() || !next.isList2()) {
      return false;
    }
    IAST v = (IAST) vertex;
    IAST p = (IAST) previous;
    IAST q = (IAST) next;
    // z component of the cross product of the incoming and the outgoing edge
    IExpr cross = F.Subtract(//
        F.Times(F.Subtract(v.arg1(), p.arg1()), F.Subtract(q.arg2(), v.arg2())), //
        F.Times(F.Subtract(v.arg2(), p.arg2()), F.Subtract(q.arg1(), v.arg1())));
    return engine.evaluate(cross).isNegativeResult();
  }

  /** <code>true</code> if <code>type</code> names one of the supported angle types. */
  public static boolean isAngleType(String type) {
    return "Interior".equals(type) //
        || "Exterior".equals(type) //
        || "FullExterior".equals(type);
  }

  /**
   * Convert the interior angle into the angle of the given <code>type</code>: the exterior angle is
   * <code>Pi-angle</code>, the full exterior angle is <code>2*Pi-angle</code>.
   */
  public static IExpr angleOfType(String type, IExpr interiorAngle, EvalEngine engine) {
    if ("Exterior".equals(type)) {
      return engine.evaluate(F.Subtract(S.Pi, interiorAngle));
    }
    if ("FullExterior".equals(type)) {
      return engine.evaluate(F.Subtract(F.C2Pi, interiorAngle));
    }
    return interiorAngle;
  }
}
