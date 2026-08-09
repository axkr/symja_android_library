package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * TriangleConstruct(tri, type)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the specified construct <code>type</code> for the triangle <code>tri</code>.
 * </p>
 * </blockquote>
 *
 * <p>
 * Depending on the construct the result is a <code>Point</code>, a <code>Line</code>, an
 * <code>InfiniteLine</code>, a <code>Circle</code> or a <code>Triangle</code>. The circle
 * constructs are only defined for a triangle in the plane; for a triangle embedded in 3D space
 * they return unevaluated.
 */
public class TriangleConstruct extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST points = TriangleGeometry.vertices(ast.arg1(), engine);
    if (points.isNIL()) {
      return F.NIL;
    }

    IExpr arg2 = ast.arg2();
    TriangleGeometry.Spec spec = TriangleGeometry.spec(arg2, points);
    if (spec == null || !isConstructType(spec.type)) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleConstruct, "bspec",
          F.List(arg2, F.stringx("construction")), engine);
    }
    if (spec.vertex == TriangleGeometry.INVALID_VERTEX) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleConstruct, "bspec",
          F.List(spec.vertexSpec, F.stringx("vertex")), engine);
    }

    if (spec.vertex == TriangleGeometry.ALL_VERTICES) {
      IASTAppendable result = F.ListAlloc(3);
      for (int i = 1; i <= 3; i++) {
        IExpr construct = construct(spec.type, spec.center, points, i, engine);
        if (construct.isNIL()) {
          return F.NIL;
        }
        result.append(construct);
      }
      return result;
    }
    return construct(spec.type, spec.center, points, spec.vertex, engine);
  }

  /** <code>true</code> if <code>type</code> names one of the supported constructs. */
  private static boolean isConstructType(String type) {
    if (TriangleGeometry.isCenterType(type)) {
      return true;
    }
    switch (type) {
      case "Altitude":
      case "AngleBisectingCevian":
      case "AngleBisector":
      case "AntimedialTriangle":
      case "Boundary":
      case "Cevian":
      case "Circumcircle":
      case "EulerLine":
      case "Excircle":
      case "ExteriorAngleBisector":
      case "Incircle":
      case "MedialTriangle":
      case "Median":
      case "NinePointCircle":
      case "OppositeSide":
      case "PerpendicularBisector":
      case "Symmedian":
      case "Triangle":
        return true;
      default:
        return false;
    }
  }

  private static IExpr construct(String type, IExpr centerSpec, IAST points, int vertexIndex,
      EvalEngine engine) {
    if (TriangleGeometry.isCenterType(type)) {
      return point(TriangleGeometry.center(type, centerSpec, points, vertexIndex, engine));
    }

    IExpr vertex = points.get(vertexIndex);
    switch (type) {
      case "Altitude":
        return segment(vertex,
            TriangleGeometry.center("Foot", F.NIL, points, vertexIndex, engine));
      case "Median":
        return segment(vertex,
            TriangleGeometry.center("Midpoint", F.NIL, points, vertexIndex, engine));
      case "AngleBisectingCevian":
        return segment(vertex, TriangleGeometry.center("AngleBisectingCevianEndpoint", F.NIL,
            points, vertexIndex, engine));
      case "Symmedian":
        return segment(vertex,
            TriangleGeometry.center("SymmedianEndpoint", F.NIL, points, vertexIndex, engine));
      case "Cevian":
        return segment(vertex,
            TriangleGeometry.center("CevianEndpoint", centerSpec, points, vertexIndex, engine));
      case "OppositeSide": {
        int[] opposite = otherVertices(vertexIndex);
        return segment(points.get(opposite[0]), points.get(opposite[1]));
      }
      case "Boundary":
        return F.Line(
            F.List(points.arg1(), points.arg2(), points.arg3(), points.arg1()));

      case "AngleBisector":
        return infiniteLine(vertex, TriangleGeometry.center("AngleBisectingCevianEndpoint", F.NIL,
            points, vertexIndex, engine));
      case "ExteriorAngleBisector":
        return exteriorAngleBisector(points, vertexIndex, engine);
      case "PerpendicularBisector":
        return perpendicularBisector(points, vertexIndex, engine);
      case "EulerLine":
        return eulerLine(points, engine);

      case "Incircle":
        return circle(TriangleGeometry.center("Incenter", F.NIL, points, vertexIndex, engine),
            TriangleGeometry.inradius(points, engine), points);
      case "Circumcircle":
        return circle(TriangleGeometry.center("Circumcenter", F.NIL, points, vertexIndex, engine),
            TriangleGeometry.circumradius(points, engine), points);
      case "NinePointCircle": {
        IExpr circumradius = TriangleGeometry.circumradius(points, engine);
        if (circumradius.isNIL()) {
          return F.NIL;
        }
        return circle(
            TriangleGeometry.center("NinePointCenter", F.NIL, points, vertexIndex, engine),
            engine.evaluate(F.Times(F.C1D2, circumradius)), points);
      }
      case "Excircle":
        return circle(TriangleGeometry.center("Excenter", F.NIL, points, vertexIndex, engine),
            TriangleGeometry.exradius(points, vertexIndex, engine), points);

      case "Triangle":
        return F.Triangle(points);
      case "MedialTriangle":
        return medialTriangle(points, engine);
      case "AntimedialTriangle":
        return antimedialTriangle(points, engine);
      default:
        return F.NIL;
    }
  }

  /** The indices of the two vertices other than <code>vertexIndex</code>, in ascending order. */
  private static int[] otherVertices(int vertexIndex) {
    if (vertexIndex == 1) {
      return new int[] {2, 3};
    }
    if (vertexIndex == 2) {
      return new int[] {1, 3};
    }
    return new int[] {1, 2};
  }

  /**
   * The bisector of the exterior angle at the vertex. Its direction is the difference of the two
   * normalized edge directions, which is perpendicular to the interior angle bisector.
   */
  private static IExpr exteriorAngleBisector(IAST points, int vertexIndex, EvalEngine engine) {
    int[] opposite = otherVertices(vertexIndex);
    IExpr vertex = points.get(vertexIndex);
    IExpr edge1 = TriangleGeometry
        .unit(engine.evaluate(F.Subtract(points.get(opposite[0]), vertex)), engine);
    IExpr edge2 = TriangleGeometry
        .unit(engine.evaluate(F.Subtract(points.get(opposite[1]), vertex)), engine);
    if (edge1.isNIL() || edge2.isNIL()) {
      return F.NIL;
    }
    IExpr direction = engine.evaluate(F.Subtract(edge1, edge2));
    if (isZeroVector(direction, engine)) {
      return F.NIL;
    }
    return infiniteLine(vertex, engine.evaluate(F.Plus(vertex, direction)));
  }

  /**
   * The perpendicular bisector of the side opposite of the vertex. In the plane the direction is
   * the side rotated by 90 degrees, in higher dimensions the component of the median perpendicular
   * to that side.
   */
  private static IExpr perpendicularBisector(IAST points, int vertexIndex, EvalEngine engine) {
    int[] opposite = otherVertices(vertexIndex);
    IExpr point1 = points.get(opposite[0]);
    IExpr point2 = points.get(opposite[1]);
    IExpr midpoint = engine.evaluate(F.Times(F.C1D2, F.Plus(point1, point2)));
    IExpr side = engine.evaluate(F.Subtract(point1, point2));

    IExpr direction;
    if (side.isList2()) {
      direction = engine.evaluate(F.List(F.Negate(side.second()), side.first()));
    } else {
      direction = TriangleGeometry.perpendicular(side,
          engine.evaluate(F.Subtract(points.get(vertexIndex), midpoint)), engine);
    }
    if (direction.isNIL() || isZeroVector(direction, engine)) {
      return F.NIL;
    }
    return infiniteLine(midpoint, engine.evaluate(F.Plus(midpoint, direction)));
  }

  /** The line through circumcenter, centroid, orthocenter and nine-point center. */
  private static IExpr eulerLine(IAST points, EvalEngine engine) {
    IExpr circumcenter = TriangleGeometry.center("Circumcenter", F.NIL, points, 2, engine);
    IExpr orthocenter = TriangleGeometry.center("Orthocenter", F.NIL, points, 2, engine);
    if (circumcenter.isNIL() || orthocenter.isNIL() || circumcenter.equals(orthocenter)) {
      // all centers coincide in an equilateral triangle - the Euler line is not defined
      return F.NIL;
    }
    return infiniteLine(circumcenter, orthocenter);
  }

  /** The triangle formed by the midpoints of the three sides. */
  private static IExpr medialTriangle(IAST points, EvalEngine engine) {
    IASTAppendable vertices = F.ListAlloc(3);
    for (int i = 1; i <= 3; i++) {
      int[] opposite = otherVertices(i);
      vertices.append(engine.evaluate(
          F.Times(F.C1D2, F.Plus(points.get(opposite[0]), points.get(opposite[1])))));
    }
    return F.Triangle(vertices);
  }

  /** The triangle whose medial triangle is the original one. */
  private static IExpr antimedialTriangle(IAST points, EvalEngine engine) {
    IASTAppendable vertices = F.ListAlloc(3);
    for (int i = 1; i <= 3; i++) {
      int[] opposite = otherVertices(i);
      vertices.append(engine.evaluate(F.Subtract(
          F.Plus(points.get(opposite[0]), points.get(opposite[1])), points.get(i))));
    }
    return F.Triangle(vertices);
  }

  private static boolean isZeroVector(IExpr vector, EvalEngine engine) {
    return engine.evaluate(F.Dot(vector, vector)).isZero();
  }

  private static IExpr point(IExpr coordinates) {
    return coordinates.isList() ? F.Point((IAST) coordinates) : F.NIL;
  }

  private static IExpr segment(IExpr from, IExpr to) {
    if (from.isNIL() || to.isNIL()) {
      return F.NIL;
    }
    return F.Line(F.List(from, to));
  }

  private static IExpr infiniteLine(IExpr from, IExpr to) {
    if (from.isNIL() || to.isNIL()) {
      return F.NIL;
    }
    return F.unaryAST1(S.InfiniteLine, F.List(from, to));
  }

  /**
   * A circle is only representable for a triangle in the plane; for higher dimensions
   * {@link F#NIL} is returned without a message.
   */
  private static IExpr circle(IExpr center, IExpr radius, IAST points) {
    if (TriangleGeometry.dimension(points) != 2 || !center.isList() || radius.isNIL()) {
      return F.NIL;
    }
    return F.Circle((IAST) center, radius);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
