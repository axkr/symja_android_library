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
 * TriangleMeasurement(tri)
 * TriangleMeasurement(tri, type)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the value of the specified measurement <code>type</code> for the triangle <code>tri</code>.
 * <code>TriangleMeasurement(tri)</code> gives the area of the triangle.
 * </p>
 * </blockquote>
 */
public class TriangleMeasurement extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST points = TriangleGeometry.vertices(ast.arg1(), engine);
    if (points.isNIL()) {
      return F.NIL;
    }

    if (ast.isAST1()) {
      // TriangleMeasurement(tri) is equivalent to Area(Triangle(tri))
      return TriangleGeometry.area(points, engine);
    }

    IExpr arg2 = ast.arg2();
    TriangleGeometry.Spec spec = TriangleGeometry.spec(arg2, points);
    if (spec == null || !isMeasurementType(spec.type)) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleMeasurement, "bspec",
          F.List(arg2, F.stringx("measurement")), engine);
    }
    if (spec.vertex == TriangleGeometry.INVALID_VERTEX) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.TriangleMeasurement, "bspec",
          F.List(spec.vertexSpec, F.stringx("vertex")), engine);
    }

    if (spec.vertex == TriangleGeometry.ALL_VERTICES) {
      IASTAppendable result = F.ListAlloc(3);
      for (int i = 1; i <= 3; i++) {
        IExpr measurement = measure(spec.type, points, i, engine);
        if (measurement.isNIL()) {
          return F.NIL;
        }
        result.append(measurement);
      }
      return result;
    }
    return measure(spec.type, points, spec.vertex, engine);
  }

  /** <code>true</code> if <code>type</code> names one of the supported measurements. */
  private static boolean isMeasurementType(String type) {
    return "Area".equals(type) //
        || "Perimeter".equals(type) //
        || "Semiperimeter".equals(type) //
        || "Inradius".equals(type) //
        || "Circumradius".equals(type) //
        || "NinePointRadius".equals(type) //
        || "Exradius".equals(type) //
        || "InteriorAngle".equals(type) //
        || "ExteriorAngle".equals(type) //
        || "FullExteriorAngle".equals(type) //
        || "Height".equals(type);
  }

  private static IExpr measure(String type, IAST points, int vertexIndex, EvalEngine engine) {
    if ("Area".equals(type)) {
      return TriangleGeometry.area(points, engine);
    }

    IExpr[] sideSquares = TriangleGeometry.sideSquares(points, engine);
    IExpr[] sides = TriangleGeometry.sides(sideSquares, engine);
    if ("Perimeter".equals(type)) {
      return engine.evaluate(F.Plus(sides[0], sides[1], sides[2]));
    }
    if ("Semiperimeter".equals(type)) {
      return TriangleGeometry.semiperimeter(sides, engine);
    }

    if ("InteriorAngle".equals(type) || "ExteriorAngle".equals(type)
        || "FullExteriorAngle".equals(type)) {
      IExpr interiorAngle = interiorAngle(sideSquares, sides, vertexIndex, engine);
      if (interiorAngle.isNIL()) {
        return F.NIL;
      }
      if ("ExteriorAngle".equals(type)) {
        return engine.evaluate(F.Subtract(S.Pi, interiorAngle));
      }
      if ("FullExteriorAngle".equals(type)) {
        return engine.evaluate(F.Subtract(F.C2Pi, interiorAngle));
      }
      return interiorAngle;
    }

    IExpr area = TriangleGeometry.area(points, engine);
    if (area.isNIL()) {
      return F.NIL;
    }
    if ("Inradius".equals(type)) {
      return engine.evaluate(F.Divide(area, TriangleGeometry.semiperimeter(sides, engine)));
    }
    if ("Circumradius".equals(type)) {
      return engine.evaluate(F.Divide(F.Times(sides[0], sides[1], sides[2]), F.Times(F.C4, area)));
    }
    if ("NinePointRadius".equals(type)) {
      // half of the circumradius
      return engine.evaluate(F.Divide(F.Times(sides[0], sides[1], sides[2]), F.Times(F.C8, area)));
    }
    if ("Exradius".equals(type)) {
      return engine.evaluate(F.Divide(area,
          F.Subtract(TriangleGeometry.semiperimeter(sides, engine), sides[vertexIndex - 1])));
    }
    if ("Height".equals(type)) {
      return engine.evaluate(F.Divide(F.Times(F.C2, area), sides[vertexIndex - 1]));
    }
    return F.NIL;
  }

  /**
   * The interior angle at the vertex <code>vertexIndex</code>:
   * <code>ArcCos((b^2+c^2-a^2)/(2*b*c))</code> for the vertex <code>1</code>.
   */
  private static IExpr interiorAngle(IExpr[] sideSquares, IExpr[] sides, int vertexIndex,
      EvalEngine engine) {
    IExpr[] conway = TriangleGeometry.conway(sideSquares, engine);
    IExpr denominator = F.C1;
    for (int i = 1; i <= 3; i++) {
      if (i != vertexIndex) {
        // the two sides adjacent to the vertex
        denominator = F.Times(denominator, sides[i - 1]);
      }
    }
    denominator = engine.evaluate(F.Times(F.C2, denominator));
    if (denominator.isZero()) {
      return F.NIL;
    }
    return engine.evaluate(F.ArcCos(F.Divide(conway[vertexIndex - 1], denominator)));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
