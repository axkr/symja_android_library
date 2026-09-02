package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Shared geometry kernel for {@link TriangleCenter}, {@link TriangleConstruct} and
 * {@link TriangleMeasurement}.
 *
 * <p>
 * The triangle centers are computed from homogeneous barycentric coordinates
 * <code>(u : v : w)</code> with respect to the vertices <code>p1, p2, p3</code>. Because
 * barycentric coordinates are homogeneous, all formulas use the <i>doubled</i> Conway values
 * <code>2*SA = b^2+c^2-a^2</code> and so on - the common factor cancels when the coordinates are
 * normalized in {@link #fromBarycentric(IExpr[], IAST, EvalEngine)}.
 *
 * <p>
 * Every method is dimension agnostic: the side lengths are derived from {@link S#Dot} products of
 * the difference vectors, so triangles embedded in 3D (or higher) space work as well as planar
 * ones.
 */
public final class TriangleGeometry {

  /** Returned by {@link #vertexIndex(IExpr, IAST)} for the {@link S#All} specification. */
  public static final int ALL_VERTICES = 0;

  /** Returned by {@link #vertexIndex(IExpr, IAST)} if the vertex cannot be identified. */
  public static final int INVALID_VERTEX = -1;

  /**
   * A parsed <code>type</code> specification of the form <code>"Type"</code>,
   * <code>{"Type", p}</code> or <code>{"Type", center, p}</code>.
   */
  public static final class Spec {
    /** The type name, for example <code>"Circumcenter"</code>. */
    public final String type;

    /** The center argument of a <code>{"Cevian", center, p}</code> style spec, or NIL. */
    public final IExpr center;

    /** The unparsed vertex argument, or NIL if it was omitted. Used for error messages. */
    public final IExpr vertexSpec;

    /** The vertex index <code>1..3</code>, {@link #ALL_VERTICES} or {@link #INVALID_VERTEX}. */
    public final int vertex;

    private Spec(String type, IExpr center, IExpr vertexSpec, int vertex) {
      this.type = type;
      this.center = center;
      this.vertexSpec = vertexSpec;
      this.vertex = vertex;
    }
  }

  private TriangleGeometry() {}

  /**
   * Extract the three vertices of a triangle.
   *
   * <p>
   * Accepted forms are <code>{p1, p2, p3}</code>, <code>Triangle({p1, p2, p3})</code> and
   * <code>Polygon({p1, p2, p3})</code>. The argument-less <code>Triangle()</code> and
   * <code>Polygon()</code> default to the unit triangle <code>{{0,0}, {1,0}, {0,1}}</code>.
   *
   * @return the list of the three vertices or {@link F#NIL} if <code>arg</code> doesn't define a
   *         triangle
   */
  public static IAST vertices(IExpr arg) {
    IExpr list = arg;
    if (arg.isAST(S.Triangle) || arg.isAST(S.Polygon)) {
      IAST region = (IAST) arg;
      if (region.argSize() == 0) {
        return F.List(F.CListC0C0, F.List(F.C1, F.C0), F.List(F.C0, F.C1));
      }
      list = region.arg1();
    }
    if (list.isList3()) {
      IAST points = (IAST) list;
      if (!points.arg1().isList()) {
        return F.NIL;
      }
      int dimension = points.arg1().argSize();
      if (dimension < 2) {
        return F.NIL;
      }
      for (int i = 2; i <= 3; i++) {
        if (!points.get(i).isList() || points.get(i).argSize() != dimension) {
          return F.NIL;
        }
      }
      return points;
    }
    return F.NIL;
  }

  /**
   * Extract the three vertices of a non degenerate triangle.
   *
   * <p>
   * Behaves like {@link #vertices(IExpr)}, but additionally rejects triangles with collinear
   * vertices, which enclose no area and have neither an incircle nor a circumcircle.
   */
  public static IAST vertices(IExpr arg, EvalEngine engine) {
    IAST points = vertices(arg);
    if (points.isPresent() && isDegenerate(points, engine)) {
      return F.NIL;
    }
    return points;
  }

  /**
   * <code>true</code> if the three vertices are provably collinear.
   *
   * <p>
   * Uses <code>16*area^2 == 4*a^2*b^2-(a^2+b^2-c^2)^2</code>, which vanishes exactly for collinear
   * vertices and works in any dimension. Symbolic vertices, for which the expression cannot be
   * decided, are not degenerate.
   */
  public static boolean isDegenerate(IAST points, EvalEngine engine) {
    IExpr[] sideSquares = sideSquares(points, engine);
    IExpr sc = conway(sideSquares, engine)[2];
    return engine.evaluate(F.Subtract(F.Times(F.C4, sideSquares[0], sideSquares[1]), F.Sqr(sc)))
        .isZero();
  }

  /** The number of coordinates of each vertex; <code>2</code> for a triangle in the plane. */
  public static int dimension(IAST points) {
    return points.arg1().argSize();
  }

  /**
   * Resolve a vertex specification to the index <code>1..3</code> of the meant vertex.
   *
   * <p>
   * A vertex can be given as the integer index <code>1</code>, <code>2</code> or <code>3</code>, as
   * the coordinates <code>{x, y}</code> of one of the vertices, as <code>Point({x, y})</code> or as
   * {@link S#All}. An omitted specification defaults to the second vertex.
   *
   * @return <code>1..3</code>, {@link #ALL_VERTICES} or {@link #INVALID_VERTEX}
   */
  public static int vertexIndex(IExpr vertexSpec, IAST points) {
    if (vertexSpec.isNIL()) {
      // "p defaults to p2"
      return 2;
    }
    if (vertexSpec == S.All) {
      return ALL_VERTICES;
    }
    if (vertexSpec.isInteger()) {
      int index = vertexSpec.toIntDefault();
      return (index >= 1 && index <= 3) ? index : INVALID_VERTEX;
    }
    IExpr point = vertexSpec.isAST(S.Point, 2) ? vertexSpec.first() : vertexSpec;
    for (int i = 1; i <= 3; i++) {
      if (point.equals(points.get(i))) {
        return i;
      }
    }
    return INVALID_VERTEX;
  }

  /**
   * Parse a type specification of the form <code>"Type"</code>, <code>{"Type", p}</code> or
   * <code>{"Type", center, p}</code>.
   *
   * @return the parsed specification or <code>null</code> if <code>arg</code> is structurally not a
   *         type specification. A structurally valid spec with an unresolvable vertex is returned
   *         with {@link Spec#vertex} set to {@link #INVALID_VERTEX}.
   */
  public static Spec spec(IExpr arg, IAST points) {
    String type;
    IExpr center = F.NIL;
    IExpr vertexSpec = F.NIL;
    if (arg.isString()) {
      type = arg.toString();
    } else if (arg.isList() && arg.argSize() >= 1 && arg.first().isString()) {
      IAST list = (IAST) arg;
      type = list.arg1().toString();
      if (list.argSize() == 2) {
        vertexSpec = list.arg2();
      } else if (list.argSize() == 3) {
        center = list.arg2();
        vertexSpec = list.arg3();
      } else if (list.argSize() != 1) {
        return null;
      }
    } else {
      return null;
    }
    return new Spec(type, center, vertexSpec, vertexIndex(vertexSpec, points));
  }

  /**
   * The squared side lengths <code>{a^2, b^2, c^2}</code> with <code>a = |p2-p3|</code>,
   * <code>b = |p1-p3|</code> and <code>c = |p1-p2|</code>.
   */
  public static IExpr[] sideSquares(IAST points, EvalEngine engine) {
    IExpr diff1 = engine.evaluate(F.Subtract(points.arg2(), points.arg3()));
    IExpr diff2 = engine.evaluate(F.Subtract(points.arg1(), points.arg3()));
    IExpr diff3 = engine.evaluate(F.Subtract(points.arg1(), points.arg2()));
    return new IExpr[] {//
        engine.evaluate(F.Dot(diff1, diff1)), //
        engine.evaluate(F.Dot(diff2, diff2)), //
        engine.evaluate(F.Dot(diff3, diff3))};
  }

  /**
   * The doubled Conway values <code>{2*SA, 2*SB, 2*SC}</code> derived from the squared side lengths
   * returned by {@link #sideSquares(IAST, EvalEngine)}.
   */
  public static IExpr[] conway(IExpr[] sideSquares, EvalEngine engine) {
    IExpr a2 = sideSquares[0];
    IExpr b2 = sideSquares[1];
    IExpr c2 = sideSquares[2];
    return new IExpr[] {//
        engine.evaluate(F.Plus(b2, c2, F.Negate(a2))), //
        engine.evaluate(F.Plus(a2, c2, F.Negate(b2))), //
        engine.evaluate(F.Plus(a2, b2, F.Negate(c2)))};
  }

  /**
   * The side lengths <code>{a, b, c}</code>, where <code>sides[i-1]</code> is the length of the
   * side opposite of the vertex <code>i</code>.
   */
  public static IExpr[] sides(IExpr[] sideSquares, EvalEngine engine) {
    return new IExpr[] {//
        engine.evaluate(F.Sqrt(sideSquares[0])), //
        engine.evaluate(F.Sqrt(sideSquares[1])), //
        engine.evaluate(F.Sqrt(sideSquares[2]))};
  }

  /** The area of the triangle; delegates to <code>Area(Triangle(points))</code>. */
  public static IExpr area(IAST points, EvalEngine engine) {
    return S.Area.funEval(engine, F.Triangle(points));
  }

  /**
   * Half of the perimeter of the triangle. The halves are summed instead of halving the sum, so
   * that rational sides stay combined with the irrational ones.
   */
  public static IExpr semiperimeter(IExpr[] sides, EvalEngine engine) {
    return engine.evaluate(F.Plus(//
        F.Times(F.C1D2, sides[0]), //
        F.Times(F.C1D2, sides[1]), //
        F.Times(F.C1D2, sides[2])));
  }

  /** The radius of the inscribed circle: <code>area/semiperimeter</code>. */
  public static IExpr inradius(IAST points, EvalEngine engine) {
    IExpr area = area(points, engine);
    if (area.isNIL()) {
      return F.NIL;
    }
    IExpr[] sides = sides(sideSquares(points, engine), engine);
    return engine.evaluate(F.Divide(area, semiperimeter(sides, engine)));
  }

  /** The radius of the circumscribed circle: <code>a*b*c/(4*area)</code>. */
  public static IExpr circumradius(IAST points, EvalEngine engine) {
    IExpr area = area(points, engine);
    if (area.isNIL()) {
      return F.NIL;
    }
    IExpr[] sides = sides(sideSquares(points, engine), engine);
    return engine.evaluate(F.Divide(F.Times(sides[0], sides[1], sides[2]), F.Times(F.C4, area)));
  }

  /**
   * The radius of the excircle opposite of the vertex <code>vertexIndex</code>:
   * <code>area/(semiperimeter-sideOpposite)</code>.
   */
  public static IExpr exradius(IAST points, int vertexIndex, EvalEngine engine) {
    IExpr area = area(points, engine);
    if (area.isNIL()) {
      return F.NIL;
    }
    IExpr[] sides = sides(sideSquares(points, engine), engine);
    return engine
        .evaluate(F.Divide(area, F.Subtract(semiperimeter(sides, engine), sides[vertexIndex - 1])));
  }

  /**
   * The homogeneous barycentric coordinates of the center <code>type</code>.
   *
   * @param vertexIndex the reference vertex <code>1..3</code> of the vertex dependent types
   * @return an array of three coordinates; <code>{NIL, NIL, NIL}</code> if <code>type</code> is
   *         unknown
   */
  public static IExpr[] barycentrics(String type, IExpr[] sideSquares, IExpr[] conway,
      int vertexIndex) {
    IExpr a2 = sideSquares[0];
    IExpr b2 = sideSquares[1];
    IExpr c2 = sideSquares[2];
    IExpr sa = conway[0];
    IExpr sb = conway[1];
    IExpr sc = conway[2];
    IExpr u = F.NIL;
    IExpr v = F.NIL;
    IExpr w = F.NIL;

    if ("Centroid".equals(type)) {
      u = F.C1;
      v = F.C1;
      w = F.C1;

    } else if ("Circumcenter".equals(type)) {
      u = F.Times(a2, sa);
      v = F.Times(b2, sb);
      w = F.Times(c2, sc);

    } else if ("Incenter".equals(type)) {
      u = F.Sqrt(a2);
      v = F.Sqrt(b2);
      w = F.Sqrt(c2);

    } else if ("Orthocenter".equals(type)) {
      u = F.Times(sb, sc);
      v = F.Times(sa, sc);
      w = F.Times(sa, sb);

    } else if ("NinePointCenter".equals(type)) {
      // a^2*(b^2+c^2)-(b^2-c^2)^2 : ...
      u = F.Subtract(F.Times(a2, F.Plus(b2, c2)), F.Sqr(F.Subtract(b2, c2)));
      v = F.Subtract(F.Times(b2, F.Plus(a2, c2)), F.Sqr(F.Subtract(a2, c2)));
      w = F.Subtract(F.Times(c2, F.Plus(a2, b2)), F.Sqr(F.Subtract(a2, b2)));

    } else if ("SymmedianPoint".equals(type)) {
      u = a2;
      v = b2;
      w = c2;

    } else if ("Excenter".equals(type)) {
      IExpr a = F.Sqrt(a2);
      IExpr b = F.Sqrt(b2);
      IExpr c = F.Sqrt(c2);
      if (vertexIndex == 1) {
        u = F.Negate(a);
        v = b;
        w = c;
      } else if (vertexIndex == 2) {
        u = a;
        v = F.Negate(b);
        w = c;
      } else {
        u = a;
        v = b;
        w = F.Negate(c);
      }

    } else if ("Midpoint".equals(type)) {
      if (vertexIndex == 1) {
        u = F.C0;
        v = F.C1;
        w = F.C1;
      } else if (vertexIndex == 2) {
        u = F.C1;
        v = F.C0;
        w = F.C1;
      } else {
        u = F.C1;
        v = F.C1;
        w = F.C0;
      }

    } else if ("Foot".equals(type)) {
      if (vertexIndex == 1) {
        u = F.C0;
        v = sc;
        w = sb;
      } else if (vertexIndex == 2) {
        u = sc;
        v = F.C0;
        w = sa;
      } else {
        u = sb;
        v = sa;
        w = F.C0;
      }

    } else if ("AngleBisectingCevianEndpoint".equals(type)) {
      IExpr a = F.Sqrt(a2);
      IExpr b = F.Sqrt(b2);
      IExpr c = F.Sqrt(c2);
      if (vertexIndex == 1) {
        u = F.C0;
        v = b;
        w = c;
      } else if (vertexIndex == 2) {
        u = a;
        v = F.C0;
        w = c;
      } else {
        u = a;
        v = b;
        w = F.C0;
      }

    } else if ("SymmedianEndpoint".equals(type)) {
      if (vertexIndex == 1) {
        u = F.C0;
        v = b2;
        w = c2;
      } else if (vertexIndex == 2) {
        u = a2;
        v = F.C0;
        w = c2;
      } else {
        u = a2;
        v = b2;
        w = F.C0;
      }
    }

    return new IExpr[] {u, v, w};
  }

  /** Convert homogeneous barycentric coordinates into cartesian coordinates. */
  public static IExpr fromBarycentric(IExpr[] barycentrics, IAST points, EvalEngine engine) {
    IExpr u = barycentrics[0];
    IExpr v = barycentrics[1];
    IExpr w = barycentrics[2];
    if (u.isNIL() || v.isNIL() || w.isNIL()) {
      return F.NIL;
    }
    IExpr sum = engine.evaluate(F.Plus(u, v, w));
    if (sum.isZero()) {
      // the "center" lies on the line at infinity
      return F.NIL;
    }
    return engine.evaluate(F.Divide(F.Plus(//
        F.Times(u, points.arg1()), //
        F.Times(v, points.arg2()), //
        F.Times(w, points.arg3())), sum));
  }

  /**
   * <code>true</code> if <code>type</code> names one of the supported triangle centers.
   */
  public static boolean isCenterType(String type) {
    return "Centroid".equals(type) //
        || "Circumcenter".equals(type) //
        || "Incenter".equals(type) //
        || "Orthocenter".equals(type) //
        || "NinePointCenter".equals(type) //
        || "SymmedianPoint".equals(type) //
        || "Excenter".equals(type) //
        || "Midpoint".equals(type) //
        || "Foot".equals(type) //
        || "AngleBisectingCevianEndpoint".equals(type) //
        || "SymmedianEndpoint".equals(type) //
        || "CevianEndpoint".equals(type);
  }

  /**
   * The cartesian coordinates of the triangle center <code>type</code>.
   *
   * @param center the center of a <code>{"CevianEndpoint", center, p}</code> specification;
   *        {@link F#NIL} for all other types
   * @param vertexIndex the reference vertex <code>1..3</code> of the vertex dependent types
   * @return {@link F#NIL} if <code>type</code> is unknown or the center is not defined for this
   *         triangle
   */
  public static IExpr center(String type, IExpr center, IAST points, int vertexIndex,
      EvalEngine engine) {
    if ("Centroid".equals(type)) {
      // no side lengths needed
      return engine.evaluate(F.Divide(F.Plus(points.arg1(), points.arg2(), points.arg3()), F.C3));
    }
    if ("NinePointCenter".equals(type)) {
      // the midpoint between circumcenter and orthocenter
      IExpr circumcenter = center("Circumcenter", F.NIL, points, vertexIndex, engine);
      IExpr orthocenter = center("Orthocenter", F.NIL, points, vertexIndex, engine);
      if (circumcenter.isPresent() && orthocenter.isPresent()) {
        return engine.evaluate(F.Times(F.C1D2, F.Plus(circumcenter, orthocenter)));
      }
      return F.NIL;
    }

    IExpr[] sideSquares = sideSquares(points, engine);
    IExpr[] conway = conway(sideSquares, engine);
    if ("CevianEndpoint".equals(type)) {
      return cevianEndpoint(center, points, vertexIndex, sideSquares, conway, engine);
    }
    return fromBarycentric(barycentrics(type, sideSquares, conway, vertexIndex), points, engine);
  }

  /**
   * The point where the line through the vertex <code>vertexIndex</code> and <code>center</code>
   * meets the opposite side.
   */
  private static IExpr cevianEndpoint(IExpr center, IAST points, int vertexIndex,
      IExpr[] sideSquares, IExpr[] conway, EvalEngine engine) {
    Spec centerSpec = spec(center, points);
    if (centerSpec == null || centerSpec.vertex <= 0 || !isCenterType(centerSpec.type)
        || "CevianEndpoint".equals(centerSpec.type)) {
      return F.NIL;
    }
    IExpr[] barycentrics = barycentrics(centerSpec.type, sideSquares, conway, centerSpec.vertex);
    if (barycentrics[0].isNIL()) {
      return F.NIL;
    }
    // dropping the coordinate of the vertex projects the center onto the opposite side
    barycentrics[vertexIndex - 1] = F.C0;
    return fromBarycentric(barycentrics, points, engine);
  }

  /** The normalized direction of the vector <code>v</code>. */
  public static IExpr unit(IExpr v, EvalEngine engine) {
    IExpr normSquared = engine.evaluate(F.Dot(v, v));
    if (normSquared.isZero()) {
      return F.NIL;
    }
    return engine.evaluate(F.Divide(v, F.Sqrt(normSquared)));
  }

  /**
   * The component of <code>v</code> perpendicular to <code>d</code>:
   * <code>v - ((v.d)/(d.d))*d</code>. For a non degenerate triangle this never vanishes, which
   * makes it a safe direction for the perpendicular bisector in any dimension.
   */
  public static IExpr perpendicular(IExpr d, IExpr v, EvalEngine engine) {
    IExpr normSquared = engine.evaluate(F.Dot(d, d));
    if (normSquared.isZero()) {
      return F.NIL;
    }
    return engine.evaluate(F.Subtract(v, F.Times(F.Divide(F.Dot(v, d), normSquared), d)));
  }
}
