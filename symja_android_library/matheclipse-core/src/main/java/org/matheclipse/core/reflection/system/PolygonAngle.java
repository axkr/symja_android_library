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
 * PolygonAngle(poly)
 * PolygonAngle(poly, p)
 * PolygonAngle(poly, p, type)
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the angles at the vertices of the polygon <code>poly</code>, or the angle at the vertex
 * <code>p</code>.
 * </p>
 * </blockquote>
 */
public class PolygonAngle extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST points = PolygonGeometry.vertices(ast.arg1(), engine);
    if (points.isNIL()) {
      return F.NIL;
    }
    points = PolygonGeometry.canonicalOrder(points, engine);
    if (points.isNIL()) {
      return F.NIL;
    }

    IExpr vertexSpec = F.NIL;
    String type = "Interior";
    if (ast.isAST3()) {
      vertexSpec = ast.arg2();
      if (!ast.arg3().isString() || !PolygonGeometry.isAngleType(ast.arg3().toString())) {
        // `1` is not a valid `2` specification.
        return Errors.printMessage(S.PolygonAngle, "bspec",
            F.List(ast.arg3(), F.stringx("angle")), engine);
      }
      type = ast.arg3().toString();
    } else if (ast.isAST2()) {
      if (ast.arg2().isString()) {
        if (!PolygonGeometry.isAngleType(ast.arg2().toString())) {
          // `1` is not a valid `2` specification.
          return Errors.printMessage(S.PolygonAngle, "bspec",
              F.List(ast.arg2(), F.stringx("angle")), engine);
        }
        type = ast.arg2().toString();
      } else {
        vertexSpec = ast.arg2();
      }
    }

    int vertex = PolygonGeometry.vertexIndex(vertexSpec, points);
    if (vertex == PolygonGeometry.INVALID_VERTEX) {
      // `1` is not a valid `2` specification.
      return Errors.printMessage(S.PolygonAngle, "bspec", F.List(vertexSpec, F.stringx("vertex")),
          engine);
    }

    if (vertex == PolygonGeometry.ALL_VERTICES) {
      int n = points.argSize();
      IASTAppendable result = F.ListAlloc(n);
      for (int i = 1; i <= n; i++) {
        IExpr angle = angle(type, points, i, engine);
        if (angle.isNIL()) {
          return F.NIL;
        }
        result.append(angle);
      }
      return result;
    }
    return angle(type, points, vertex, engine);
  }

  private static IExpr angle(String type, IAST points, int vertexIndex, EvalEngine engine) {
    IExpr interiorAngle = PolygonGeometry.interiorAngle(points, vertexIndex, engine);
    if (interiorAngle.isNIL()) {
      return F.NIL;
    }
    return PolygonGeometry.angleOfType(type, interiorAngle, engine);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }
}
